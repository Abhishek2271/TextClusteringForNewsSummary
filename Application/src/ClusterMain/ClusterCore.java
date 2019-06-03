package ClusterMain;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.*;
import java.util.logging.Filter;
import java.util.stream.Collectors;

import ClusterMain.Evaluation.ComputeSSE;
import ClusterMain.Evaluation.EvaluationCore;
import ClusterMain.SimilarityMeasure.CosineSimilarity;
import ClusterMain.Tokenizer.NamedEntityTokenizer;
import ClusterMain.Tokenizer.POSTagging;
import ClusterMain.Tokenizer.WhiteSpaceTokenizer;
import Utils.*;
import java.util.ArrayList;
import static java.util.stream.Collectors.toConcurrentMap;

public class ClusterCore
{
    public static Map<String, Integer> OverallTermFreq;                  //Map containing term freq of each term over all sources
    public static int TopNewsCount = 10;                                 //Show this number of news items in the UI. Each item belongs(must) to distinct cluster
    public static String ParsedDate = "2019-02-14";                     //Get data from this date (this is for test only, for actual purpose take current date)

    public static void main(String[] args)
    {
        BeginClusterProc();
    }


    /// <summary>
    /// 1. Read cluster setting from DB.
    //  2. Initialize everything required for clustering.
    //  3. Get dataset from DB for clustering
    /// </summary>
    public static void BeginClusterProc()
    {
        HashMap<String, Map> DocumetTermFreqMap = new HashMap<>();      //Map contaning source (DocumentName) as key and each term frequency in that source
        OverallTermFreq = new HashMap<>();

        try {
            //TODO: DO NOT INITIALIZE DB AND INI HERE.
            IniParser parser = new IniParser();
            parser.ReadDBSettings();


            System.out.println(parser.DBName);
            System.out.println(parser.UserName);
            System.out.println(parser.Password);

            //INITIATE DATABASE CONNECTION
            DBAction.InitiateSqlConnection();

            parser.ReadClusterSettings();
            int NumberOfClusters = 0;                           //Number of clusters to create
            boolean FilterStopWords = true;                      //Whether or not to filter stop words
            NumberOfClusters = parser.NumberOfClusters;
            FilterStopWords = parser.FilterStopWords;
            parser.ReadStoriesSettings();
            TopNewsCount = parser.TopStoriesCount;
            //Get all description and Link for clustering. Currently, Link is used as a key and description is used as a clustering text
            //DATE 1 = 2018-11-29
            //DATE 2 = 2019-01-09
            /*String Query = "SELECT T1.Title AS [Source], Description AS [Text] FROM [tbl_ex_RSSData] T1 WITH(NOLOCK)" +
                    "       UNION ALL " +
                    "       SELECT T2.Description AS [Source], Description AS [Text] FROM [tbl_ex_TwitterData] T2 WITH(NOLOCK)" +
                    "       UNION ALL " +
                    "       SELECT T1.Title AS [Source], Description AS [Text] FROM [tbl_ex_APIData] T2 WITH(NOLOCK)";
            */
            //GET UNIQUE IDs as well while getting all data

            String Query = " SELECT  ROW_NUMBER() OVER(PARTITION BY m.progression order by Source) AS  DocumentID, m.Source, m.Text FROM(" +
                    " SELECT T1.Title AS [Source], T1.Description AS [Text], 1 as Progression FROM [tbl_ex_RSSData] T1 WITH(NOLOCK) WHERE  CAST(ParsedDate AS date) = '2019-02-14'" +
                    " UNION ALL " +
                    " SELECT T2.Description AS [Source], Description AS [Text], 1 as Progression FROM [tbl_ex_TwitterData] T2 WITH(NOLOCK) WHERE  CAST(ParsedDate AS date) = '2019-02-14'" +
                    " UNION ALL " +
                    " SELECT T3.Title AS [Source], T3.Description AS [Text], 1 as Progression FROM [tbl_ex_APIData] T3 WITH(NOLOCK) WHERE  CAST(ParsedDate AS date) = '2019-02-14')M";

            //  WHERE  CAST(ParsedDate AS date) = '2019-01-19'
            //A simple test query that does not represent original data set in any way but only to test if clustering is sensible or not.
            //String t_Query = "Select DocID as Source, Description as Text FROM [SampleData] WITH(NOLOCK)";
            String e_Query = "Select DocumentID, Source, Text  FROM [CLUSTER_EVAL] WITH(NOLOCK)";
            ResultSet rs = DBAction.ExecuteQuery(Query);
            //List<String> tokenList = ClusterHelper.ExtractTokensFromTags("#UPDATE At least 20 killed, 54 injured in a ruptured fuel pipeline explosion in central #Mexico, Hidalgo state governor says https://t.co/LfTiwT1FXP", FilterStopWords);

            while (rs.next()) {
                //TOKENIZE THE TEXT FROM THE DESCRIPTION.
                //WhiteSpaceTokenizer st = new WhiteSpaceTokenizer();
                //List<String> tokenList = st.extractTokens(rs.getString("Text"), true);


                List<String> tokenList = ClusterHelper.ExtractTokens(rs.getString("Text"), true);
                //List<String> tokenList = ClusterHelper.ExtractTokensFromTags(rs.getString("Text"), FilterStopWords);


                //CREATE A MAP OF TERM TO FREQUENCY.
                //System.out.println(tokenList);
                Map<String, Integer> termFreqMap;
                termFreqMap = tokenList.parallelStream().flatMap(s -> Arrays.stream(s.split(" "))).
                        collect(toConcurrentMap(String::toLowerCase, (String w) -> 1, Integer::sum));

                DocumetTermFreqMap.put(rs.getInt("DocumentID") + "_" + rs.getString("Source"), termFreqMap);
                //System.out.println(DocumetTermFreqMap);

                //OVERALL TERM FREQ. Frequency of all the terms in the overall data set. NEEDED FOR IDF COMPUTATION.
                //TODO: MAKE THIS BETTER: THIS IS REDUNDANT.
                Set<String> tokenSet = termFreqMap.keySet();
                for (String token : tokenSet) {
                    if (!OverallTermFreq.containsKey(token))
                        OverallTermFreq.put(token, 1);
                    else {
                        int OldFreq = OverallTermFreq.get(token);
                        int NewFreq = OldFreq + 1;
                        OverallTermFreq.put(token, NewFreq);
                    }
                    //OverallTermFreq.get(token).frequency += 1;
                }

                /*for (Map.Entry<String, Integer> TermFreq : OverallTermFreq.entrySet())
                {
                    System.out.println(TermFreq.getKey() + ":" + TermFreq.getValue());
                }
                */
            }

            /*

            for (Map.Entry<String, Integer> TermFreq : OverallTermFreq.entrySet()) {

                System.out.println(TermFreq.getKey() + ":" + TermFreq.getValue());
            }*/

            //GET TF-IDF OF EACH TERM IN ALL THE DOCUMENTS
            ArrayList<Document> DocumentVector = ClusterHelper.ComputeDocumentVector(DocumetTermFreqMap, OverallTermFreq);
            //Initialize clustering
            InitializeClustering(NumberOfClusters, DocumentVector);
            //InitializeSingleLoopClustering(NumberOfClusters, DocumentVector);


        } catch (java.sql.SQLException exp) {
            String message = exp.getMessage();
            System.out.println(message);
        } catch (IOException e) {
            String message = e.getMessage();
            System.out.println(message);
        }
    }


    /// <summary>
    /// Start the clustering process. For this the documents needed for clustering should be well defined (Represented in terms of tfidf vectors )
    //  This method will initiate a clustering for a single loop. Cluster number should be specified
    /// </summary>
    /// <param name="DocumetTermFreqMap">Map with Key as "DocumentName" and Value as another Map representing "Term"(Key) and "corresponding frequency"(value)</param>
    /// <param name="OverallTermFreq">Contains all terms in the given set of data (all documents) and their frequency (OVERALL TERM FREQUENCY)</param>
    private static void InitializeSingleLoopClustering(int NumberOfClusters, ArrayList<Document> documentVector)
    {
        //A dictionary to hold all the Squared Error List along with the corresponding number of clusters.
        Map<Integer, Double> SSE_List = new HashMap<>();
        //A dictionary to hold all the centroids computed for the given Number of clusters. For instance when there are 3 centroids then the key will be 3 and the value will be all the three centoids,
        //This is necessary because we need to use the same centroids later to get the clustered documents.
        Map<Integer, Map<Integer, Document>> Centroids_logs = new HashMap<>();
        double prev_SSE = 0;
        double curr_SSE = 0;
        int Increments = 50;
        int ClusterNumber = NumberOfClusters;
        int prev_ClusterNumber = ClusterNumber;
        //A Map containing Cluster Number as key and the diff between the current cluster SSE(Key) and previous cluster SSE(value)
        Map<Integer, Long> SSE_Diff = new HashMap<>();


        //GET CENTROID

        Map<Integer, Document> Centroid = ClusterHelper.GetCentroid(ClusterNumber, documentVector);


        //FOR EVALUATION GET SPECIFIC CENTROID
       // Map<Integer, Document> Centroid = EvaluationCore.GetSpecificCentroid(documentVector);

        //START CLUSTERING BASED ON THE CENTROID
        Map<Integer, List<Document>> ClusterMap = new HashMap<>();  // A list that holds a map of clusterid to corresponding document the cluster belongs to.
        ClusterMap = GetCluster(Centroid, documentVector);
        //DISPLAY THE RESULTS
        ClusterHelper.DisplayResults(ClusterMap);
        ClusterHelper.WriteToCsv(ClusterMap, false, 0);
        //COMPUTE THE SUM OF SQUARED ERRORS
        /*
        ComputeSSE SSE = new ComputeSSE();
            SSE_List.put(ClusterNumber, SSE.GetSSE(Centroid, ClusterMap));
            //log the centroid and the cluster number
            //Centroids_logs.put(ClusterNumber, Centroid);

            if (SSE_List.size() > 1) {
                prev_SSE = Math.round(SSE_List.get(prev_ClusterNumber));
                curr_SSE = SSE_List.get(ClusterNumber);
                long diff = Math.abs(Math.round(prev_SSE) - Math.round(curr_SSE));
                SSE_Diff.put(ClusterNumber, diff);
                //ITERATE OVER TILL BETTER RESULTS ARE OBTAINED. INSTEAD OF DOING THIS GET THE DISTANCE BETWEEN EACH POINTS
                //if (diff > 200)
                //  break;
            } else if (SSE_List.size() == 1) {
                // THE FIRST ELEMENT HAS NO DIFFERENCE SO PUT 0
                SSE_Diff.put(ClusterNumber, Long.valueOf(0));
            }
            prev_ClusterNumber = ClusterNumber;
            ClusterNumber = ClusterNumber + Increments;
            ResetArray(documentVector);
            if (ClusterNumber >= documentVector.size()) {
                //By this time the clusternumber would be greater than document vector size and we are not using this number anymore so for
                //display purposes we need to get the original clusternummer

                ClusterNumber = ClusterNumber - Increments;
                break;
            }

        Long max = SSE_Diff.entrySet()
                .stream()
                .max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1)
                .get()
                .getValue();

        List listOfMax = SSE_Diff.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == max)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        System.out.println(listOfMax);
        System.out.println("Optimal Number of Clusters = " + listOfMax.get(0) + String.format("%n") + "Final SSE = " + SSE_Diff.get(listOfMax.get(0)));

        //GET THE CLUSTER WITH OPTIMAL NUMBER OF CENTROIDS
        Map<Integer, Document> Centroid_opt_Cluster = new HashMap<>();
        Centroid_opt_Cluster = Centroids_logs.get(listOfMax.get(0));
        Map<Integer, List<Document>> ClusterMap = new HashMap<>();  // A list that holds a map of clusterid to corresponding document the cluster belongs to.
        ClusterMap = GetCluster(Centroid_opt_Cluster, documentVector);
        //DISPLAY THE RESULTS
        ClusterHelper.DisplayResults(ClusterMap);
        ClusterHelper.WriteToCsv(ClusterMap);
        ClusterHelper helper = new ClusterHelper();
        helper.GetTop10Clusters(ClusterMap, TopNewsCount);
        */
    }

    /// <summary>
    /// Start the clustering process. For this the documents needed for clustering should be well defined (Represented in terms of tfidf vectors )
    /// This method will initiate clustering and will repeat in loop to find the optimal Cluster number.
    /// </summary>
    /// <param name="DocumetTermFreqMap">Map with Key as "DocumentName" and Value as another Map representing "Term"(Key) and "corresponding frequency"(value)</param>
    /// <param name="OverallTermFreq">Contains all terms in the given set of data (all documents) and their frequency (OVERALL TERM FREQUENCY)</param>
    private static void InitializeClustering(int NumberOfClusters, ArrayList<Document> documentVector)
    {
        try {
            int BatchID = -1;
            //A dictionary to hold all the Squared Error List along with the corresponding number of clusters.
            Map<Integer, Double> SSE_List = new HashMap<>();
            //A dictionary to hold all the centroids computed for the given Number of clusters. For instance when there are 3 centroids then the key will be 3 and the value will be all the three centoids,
            //This is necessary because we need to use the same centroids later to get the clustered documents.
            Map<Integer, Map<Integer, Document>> Centroids_logs = new HashMap<>();
            double prev_SSE = 0;
            double curr_SSE = 0;
            int Increments = 10;
            int ClusterNumber = 1;
            int prev_ClusterNumber = ClusterNumber;
            //A Map containing Cluster Number as key and the diff between the current cluster SSE(Key) and previous cluster SSE(value)
            Map<Integer, Long> SSE_Diff = new HashMap<>();
            while (true) {

                //GET CENTROID

                Map<Integer, Document> Centroid = ClusterHelper.GetCentroid(ClusterNumber, documentVector);


                //FOR EVALUATION GET SPECIFIC CENTROID
                //Map<Integer, Document> Centroid = EvaluationCore.GetSpecificCentroid(documentVector);

                //START CLUSTERING BASED ON THE CENTROID
                Map<Integer, List<Document>> ClusterMap = new HashMap<>();  // A list that holds a map of clusterid to corresponding document the cluster belongs to.
                ClusterMap = GetCluster(Centroid, documentVector);
                //DISPLAY THE RESULTS
                ClusterHelper.DisplayResults(ClusterMap);
                ClusterHelper.WriteToCsv(ClusterMap, false, BatchID);
                //COMPUTE THE SUM OF SQUARED ERRORS
                ComputeSSE SSE = new ComputeSSE();
                SSE_List.put(ClusterNumber, SSE.GetSSE(Centroid, ClusterMap));
                //log the centroid and the cluster number
                Centroids_logs.put(ClusterNumber, Centroid);

                if (SSE_List.size() > 1) {
                    prev_SSE = Math.round(SSE_List.get(prev_ClusterNumber));
                    curr_SSE = SSE_List.get(ClusterNumber);
                    long diff = Math.abs(Math.round(prev_SSE) - Math.round(curr_SSE));
                    SSE_Diff.put(ClusterNumber, diff);
                    //ITERATE OVER TILL BETTER RESULTS ARE OBTAINED. INSTEAD OF DOING THIS GET THE DISTANCE BETWEEN EACH POINTS
                    //if (diff > 200)
                    //  break;
                } else if (SSE_List.size() == 1) {
                    // THE FIRST ELEMENT HAS NO DIFFERENCE SO PUT 0
                    SSE_Diff.put(ClusterNumber, Long.valueOf(0));
                }
                prev_ClusterNumber = ClusterNumber;
                ClusterNumber = ClusterNumber + Increments;
                ResetArray(documentVector, Centroid);
                if (ClusterNumber >= documentVector.size()) {
                    //By this time the clusternumber would be greater than document vector size and we are not using this number anymore so for
                    //display purposes we need to get the original clusternummer

                    ClusterNumber = ClusterNumber - Increments;
                    break;
                }
            }
            Long max = SSE_Diff.entrySet()
                    .stream()
                    .max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1)
                    .get()
                    .getValue();

            List listOfMax = SSE_Diff.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() == max)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            //System.out.println(listOfMax);
            int OptNum = Integer.parseInt(listOfMax.get(0).toString()) - 1;
            if (OptNum == 0)
                OptNum = 1;

            System.out.println("Optimal Number of Clusters = " + OptNum + String.format("%n") + "Final SSE = " + SSE_Diff.get(listOfMax.get(0)));

            //GET THE CLUSTER WITH OPTIMAL NUMBER OF CENTROIDS
            Map<Integer, Document> Centroid_opt_Cluster = new HashMap<>();
            Centroid_opt_Cluster = Centroids_logs.get(listOfMax.get(0));
            Map<Integer, List<Document>> ClusterMap = new HashMap<>();  // A list that holds a map of clusterid to corresponding document the cluster belongs to.
            ClusterMap = GetCluster(Centroid_opt_Cluster, documentVector);
            //DISPLAY THE RESULTS
            ClusterHelper.DisplayResults(ClusterMap);
            //Write to CSV and also to DB since this is the final result
            BatchID = ClusterHelper.GetBatchID();
            ClusterHelper.WriteToCsv(ClusterMap, true, BatchID);
            ClusterHelper helper = new ClusterHelper();
            helper.GetTop10Clusters(ClusterMap, TopNewsCount, true, BatchID);
        }
        catch (java.sql.SQLException exp)
        {
            String message = exp.getMessage();
            System.out.println(message);
        }
    }

    /// <summary>
    /// Reset all the parameters of the document that is dependent in a particular cluster
    /// </summary>
    private static void ResetArray(ArrayList<Document> documentVector, Map<Integer, Document> Centroid)
    {

        for (Map.Entry<Integer, Document> Cent : Centroid.entrySet())
        {
            //ADD THE CENTROIDS AGAIN
            documentVector.add(Cent.getValue());
        }
        for(Document doc : documentVector)
        {
            doc.IsCentroid = false;
            doc.ClusterId = 0;
            doc.SetDocumentScore(0.0);
        }
    }

    /// <summary>
    ///  Create clusters based on the similarity of all listed documents with centroids.
    /// </summary>
    /// <param name="centroids">Dictionary with ClusterID as Key and Corresponding document as Value</param>
    /// <param name="documentVector">List of all documents which should be clustered</param>
    /// <Returns>Dictionary with ClusterID and the list of documents that belong to that cluster id</Returns>
    private static Map<Integer, List<Document>> GetCluster(Map<Integer, Document> centroids, List<Document> documentVector)
    {
        double MaxScore = 0;    //The similarity score of the document.
        int DocClusterID = 0;   //Clusterid in which the document belongs
        Map<Integer, List<Document>> ClusterMap = new HashMap<>();  // A list that holds a map of clusterid to corresponding document the cluster belongs to.

        //List<Document> documentVector_copy = new ArrayList<>(documentVector);

        //INSERT CENTROIDS TO THE CLUSTER MAP SINCE ALL THE CENTROIDS SHOULD UNIQUE KEY AND DO NOT LIE IN ANOTHER CLUSTER.
        for (Map.Entry<Integer, Document> Centroid : centroids.entrySet()) {
            //CENTROIDS ARE ALWAYS PART OF THE CLUSTER THEY ARE TRYING TO SEPARATE
            Document CentroidDoc = Centroid.getValue();
            CentroidDoc.IsCentroid = true;
            CentroidDoc.ClusterId = Centroid.getKey();

            ClusterMap.put(Centroid.getKey(), new ArrayList<>());
            //ClusterMap.get(Centroid.getKey()).add(Centroid.getValue());
            ClusterMap.get(Centroid.getKey()).add(CentroidDoc);
            //NO NEED TO COMPARE THE CLUSTERS AGAIN

            documentVector.remove(Centroid.getValue());

        }

        //COMPUTE COSINE SIMILARITY AMONG THE DOCUMENTS.
        for (Document Doc : documentVector) {
            //DocClusterID = 0;
            Map<Integer, Double> ScoresMap = new HashMap<>();   //A mapping of document with centroid and its corresponding score
            Map.Entry<Integer, Double> maxScore = null;         //This mapping will hold the max score and the clusterid
            for (Map.Entry<Integer, Document> Centroid : centroids.entrySet()) {
                //GET SIMILARITY SCORES FROM COSINE SIMILARITY
                double score = CosineSimilarity.GetCosineSimilarity(Centroid.getValue(), Doc);
                ScoresMap.put(Centroid.getKey(), score);
                //This does not work. NOT SURE WHY!!!!!!!
                /*
                if(MaxScore < score)
                {
                    MaxScore = score;
                    DocClusterID = Centroid.getKey();
                }
                */
            }

            for (Map.Entry<Integer, Double> score : ScoresMap.entrySet())
            {
                if (maxScore == null || score.getValue() > maxScore.getValue())
                    maxScore = score;
            }
            DocClusterID = maxScore.getKey();
            MaxScore = ScoresMap.get(DocClusterID);
            Doc.SetClusterID(DocClusterID);
            Doc.SetDocumentScore(MaxScore);


            //Doc.SetDocumentScore(MaxScore);
            //Doc.SetClusterID(DocClusterID);


            if (!ClusterMap.containsKey(Doc.ClusterId))
                ClusterMap.put(Doc.ClusterId, new ArrayList<>());
            ClusterMap.get(Doc.ClusterId).add(Doc);

        }
        return ClusterMap;
    }
}
