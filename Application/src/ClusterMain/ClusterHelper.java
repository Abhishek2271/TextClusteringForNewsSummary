package ClusterMain;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import ClusterMain.Tokenizer.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ClusterMain.Tokenizer.POSTagging;
import Utils.DBAction;
import com.opencsv.CSVWriter;
import org.apache.xpath.operations.Bool;


public class ClusterHelper {

    public static final String SPLIT_TOKENS = "[\\s]";

    static String reg_pattern_WEBLINK = "^(http\\:\\/\\/|https\\:\\/\\/)?([a-z0-9][a-z0-9\\-]*\\.)+[a-z0-9][a-z0-9\\-]*";
    static String reg_pattern_HTMLTAG = "</?[a-z][a-z0-9]*[^<>]*>";
    static String reg_pattern_NUM = "^[+-]?[0-9]{1,3}(?:[0-9]*(?:[.,][0-9]{2})?|(?:,[0-9]{3})*(?:\\.[0-9]{2})?|(?:\\.[0-9]{3})*(?:,[0-9]{2})?)$"; //currency (EU and US and all num only)

    /// <summary>
    /// Tokenize the given string.
    /// </summary>
    /// <param name="InputString">String to tokenize</param>
    /// <param name="FilterStopWords">Boolean: if true then filter stop words, If not true then keep the stop words</param>
    /// <Returns>Final list of tokens</Returns>
    public static List<String> ExtractTokens(String InputString, boolean FilterStopWords)
    {
        StopWordChecker stopWordChecker = new StopWordChecker();
        List<String> result = new ArrayList<>();
        String tokens[] = InputString.split(SPLIT_TOKENS);
        for (String token : tokens)
        {
            if (token.length() == 0 ||
                    (FilterStopWords && stopWordChecker.isStopWord(token.toLowerCase()))
                    //|| token.startsWith("href=")
                    //|| token.startsWith("src=")
                    //|| token.startsWith("color=")
                    //|| token.startsWith("target=")
                    //|| MatchRegex(reg_pattern_WEBLINK, token)
                    //|| MatchRegex(reg_pattern_HTMLTAG, token)
                    //|| MatchRegex(reg_pattern_NUM, token)
            )
                continue;
            //token = standardiseToken(token.toLowerCase());
            result.add(token);
        }
        return result;
    }

    public static boolean MatchRegex(String reg_pattern, String Token)
    {
        Pattern r = Pattern.compile(reg_pattern);
        Matcher m = r.matcher(Token);
        if(m.find())
            return true;
        else
            return false;
    }

    public static List<String> ExtractTokensFromTags(String InputString, boolean FilterStopWords)
    {
        List<String> tokens = new ArrayList<>();
        List<String> result = new ArrayList<>();

        POSTagging tag = new POSTagging();
        tokens = tag.tag(InputString, FilterStopWords);
        //TOKEN STANDARIZATION
        /*
        for (String token : tokens)
        {
            token = standardiseToken(token.toLowerCase());
            result.add(token);
        }
*/
        return tokens;    //NO STEMMING
        //return result;      //WITH STEMMING
    }

    /// <summary>
    /// Standarize the tokens by applying a stemmer.
    /// </summary>
    /// <param name="token">Input token to be stemmed</param>
    /// <Returns>List of standarized (stemmed) tokens</Returns>
    public static String standardiseToken (String token)
    {
        // The SnowBall stemmer is not that good since it stems weirdly "issuing" -> "issu"
        SnowballStemmer snowballStemmer = new SnowballStemmer();
        String newToken = token.trim().toLowerCase();
        newToken = snowballStemmer.stem(newToken);
        return newToken;
    }

    /// <summary>
    /// Represent the documents in terms of TF-IDF Vectors
    /// </summary>
    /// <param name="DocumetTermFreqMap">Map with Key as "DocumentName" and Value as another Map representing "Term"(Key) and "corresponding frequency"(value)</param>
    /// <param name="OverallTermFreq">Contains all terms in the given set of data (all documents) and their frequency (OVERALL TERM FREQUENCY)</param>
    /// <Returns>List of Document type object. Each "Document" object contains complete information about a document</Returns>
    public static ArrayList<Document> ComputeDocumentVector(Map<String, Map> DocumetTermFreqMap, Map<String, Integer> OverallTermFreq)
    {
        ArrayList<Document> DocumentVector = new ArrayList<>();
        for (Map.Entry<String, Map> TermFreq : DocumetTermFreqMap.entrySet())
        {
            Map<String, Integer> TermToFreqMap = TermFreq.getValue();
            Map<String, Double> TermToTfIdfMap = GetTf_Idf(TermToFreqMap, DocumetTermFreqMap.size(), OverallTermFreq);
            String DocumentName = TermFreq.getKey();
            int indexOfChar = DocumentName.indexOf('_');
            String Documentid = DocumentName.substring(0, indexOfChar);
            DocumentVector.add(new Document(Integer.parseInt(Documentid), TermFreq.getKey(), TermToTfIdfMap));
            //System.out.println(TermFreq.getKey() + " : " + TermToTfIdfMap + "%n");
        }
        return DocumentVector;
    }

    /// <summary>
    /// Get TF- IDF of a document in the dataset based on the frequency of terms in each document.
    /// </summary>
    /// <param name="termToFreqMap">Map with Key as Term and Value as its Frequency</param>
    /// <param name="size">Total number of terms in the given document</param>
    /// <param name="OverallTermFreq">Contains all terms in the given set of data (all documents) and their frequency (OVERALL TERM FREQUENCY)</param>
    /// <Returns>A map containing "Terms" in the given document as Key and Corresponding TF-IDF as Value</Returns>
    private static Map<String, Double> GetTf_Idf(Map<String, Integer> termToFreqMap, int size, Map<String, Integer> OverallTermFreq)
    {
        Map<String, Double> tfIdfMap = new HashMap<>();

        for (Map.Entry<String, Integer> obj : termToFreqMap.entrySet())
        {
            String term = obj.getKey();
            int FrqOfTermInAllDocs = OverallTermFreq.get(term);
            Double freq = obj.getValue().doubleValue();
            Double termF = freq / termToFreqMap.size();
            Double idf = Math.log((double) size / FrqOfTermInAllDocs);
            Double tfIdf = termF * idf;
            tfIdfMap.put(term, tfIdf);
        }
        return tfIdfMap;
    }


    /// <summary>
    /// Among the set of documents select centroids randomly.
    /// </summary>
    /// <param name="NumberofClusters">Number of Clusters User wants to create</param>
    /// <param name="DocList">Overall list of documents</param>
    /// <Returns>Dictionary with CentroidID as key and corresponding centroid document as Value</Returns>
    public static Map<Integer, Document> GetCentroid(int NumberOfClusters, List<Document> DocList)
    {
        Map<Integer, Document> Centroid = new HashMap<>();
        int CentroidID = 0;
        while(true)
        {
            //Get any random int.
            int randomInt = new Random().nextInt(DocList.size());
            //Select the random document as centroid.
            Document CentroidDoc = DocList.get(randomInt);
            if(!Centroid.containsValue(CentroidDoc)) {
                CentroidID++;
                Centroid.put(CentroidID, CentroidDoc);
                CentroidDoc.IsCentroid = true;
                CentroidDoc.SetClusterID(CentroidID);
            }
            if(Centroid.size() == NumberOfClusters)
                break;
        }
        return Centroid;
    }



    // Maximise score for each document
    public static void maximiseScore(Map<Integer, Document> centroids, Map<Integer, List<Document>> clusterMap)
    {
        for (Map.Entry<Integer, Document> centroid : centroids.entrySet())
        {
            Map<String, Double> tfIdfMapCentroid = new HashMap<>();
            tfIdfMapCentroid = centroid.getValue().TermTfIDF;
            List<Document> documentsInCluster = new ArrayList<>();
            documentsInCluster = clusterMap.get(centroid.getKey());
            for (Map.Entry<String, Double> term : tfIdfMapCentroid.entrySet())
            {
                String word = term.getKey();
                double score = 0;
                for (Document document : documentsInCluster)
                {
                    if (document.TermTfIDF.containsKey(word))
                    {
                        score += document.TermTfIDF.get(word);
                    }
                }
                // normalise the score
                score /= documentsInCluster.size();
                tfIdfMapCentroid.put(word, score);
            }
        }
    }

    /// <summary>
    /// Display the results and also copy results to file. Using this only till UI is not done
    /// </summary>
    /// <param name="clusterMap">A dictionary with ClusterID as Key and Corresponding Cluster Documents as values</param>
    public static void DisplayResults(Map<Integer, List<Document>> clusterMap)
    {
        try
        {
            System.out.println();
            File file = new File("D:\\ClusterSummary.txt");
            FileWriter Writer = new FileWriter(file);
            int ClusterNumber = 0;
            for (Map.Entry<Integer, List<Document>> Cluster : clusterMap.entrySet()) {
                List<Document> Documents = Cluster.getValue();
                int ClusterSize = Documents.size();

                //System.out.println("\nCluster " + Cluster.getKey() + "\n---------------------------------------");
                //System.out.println("Number of documents in this cluster: " + ClusterSize);

                Writer.write(String.format("%n") + String.format("%n") + "Cluster " + Integer.toString(Cluster.getKey()) + "---------------------------------------");
                Writer.write(String.format("%n") + String.format("%n") + "Number of documents in this cluster: " + ClusterSize + String.format("%n"));
                for (Document Doc : Cluster.getValue()) {
                    //System.out.println(Doc.Name);
                    Writer.write(String.format("%n") + Doc.Name);
                }
            }
            Writer.close();
        }
        catch(Exception exp)
        {
            String message = exp.getMessage();
            System.out.println(message);
        }
    }

    /// <summary>
    /// Get the top stories based on the cluster size. This will be used to display in the UI.
    /// Specified number of centroids are selected along with other members in the cluster
    /// </summary>
    /// <param name="clusterMap">A dictionary with ClusterID as Key and Corresponding Cluster Documents as values</param>
    /// <param name="TopNewsCount">Number of news items to display (number of centroids to choose from the result)</param>
    /// <param name="InsertToDB">Whether or not to insert the result to db. For this it is always true</param>
    /// <param name="BatchID">Current batch id or run count.</param>
    public  void GetTop10Clusters(Map<Integer, List<Document>> clusterMap, int TopNewsCount, Boolean InsertToDB, int BatchID)
    {
        try {

            File file = new File("D:\\TopStories_.csv");
            FileWriter Writer = new FileWriter(file);
            Character Seperator = ',';
            CSVWriter Csv_writer = new CSVWriter(Writer, Seperator);
            //Create a dictionary to hold the clusterid and clustersize;
            Map<Integer, Integer> ClusterSize = new HashMap<>();
            //GET top 10 big clusters, Create a dictionary to hold clusterid with its size
            for (Map.Entry<Integer, List<Document>> Cluster : clusterMap.entrySet())
            {
                ClusterSize.put(Cluster.getKey(), Cluster.getValue().size());
            }
            //GET top 10 big clusters, sort the values in the map according to the size in the created dictionary
            ValueComparator bvc = new ValueComparator(ClusterSize);
            TreeMap<Integer, Integer> sorted_map = new TreeMap<Integer, Integer>(bvc);
            sorted_map.putAll(ClusterSize);
            //write the top 10 to dictionary
            int i = 0;
            for (Map.Entry<Integer, Integer> sorted_result : sorted_map.entrySet())
            {
                int ClusterID = sorted_result.getKey();
                List<Document> Docs = clusterMap.get(ClusterID);
                List<String[]> Data = new ArrayList<String[]>();
                for (Document Doc : Docs)
                {

                    InsertToNewsItems(BatchID, Doc.Name ,Doc.IsCentroid, Doc.ClusterId);
                    Data.add(new String[]{Doc.Name.substring(Doc.Name.indexOf('_') + 1 , Doc.Name.length() -1), Integer.toString(Doc.ClusterId), Boolean.toString(Doc.IsCentroid)});
                    //Writer.write(Doc.Name + Seperator + Doc.ClusterId + Seperator + Doc.IsCentroid + String.format("%n"));
                }
                Csv_writer.writeAll(Data);
                i++;
                if(i>= TopNewsCount)
                    break;
            }
            Writer.close();
        }
        catch (Exception exp) {
            String message = exp.getMessage();
            System.out.println(message);
        }
    }

    /// <summary>
    /// Display the results and also copy results to a csv file. The csv only contains the clusterid and the documentid
    /// </summary>
    /// <param name="clusterMap">A dictionary with ClusterID as Key and Corresponding Cluster Documents as values</param>
    /// <param name="InsertToDb">Whether or not to insert the result to DB. True only when inserting the final cluster with optimal number of centroids</param>
    /// <param name="BatchID">Current BatchID</param>
    public static void WriteToCsv(Map<Integer, List<Document>> clusterMap, Boolean InsertToDb, int BatchID)
    {
        try
        {
            System.out.println();
            File file = new File("D:\\ClusterSummary.csv");
            FileWriter Writer = new FileWriter(file);
            int ClusterNumber = 0;
            Character Seperator = ',';
            CSVWriter Csv_writer = new CSVWriter(Writer, Seperator);

            //Add headers
            List<String[]> Header = new ArrayList<String[]>();
            Header.add(new String[] { "Title", "ClusterID", "IsCentroid" });
            //writer.write("Title" + Seperator + "ClusterID" + Seperator + "IsCentroid" + String.format("%n"));
            //Csv_writer.writeAll(Header);
            for (Map.Entry<Integer, List<Document>> Cluster : clusterMap.entrySet())
            {
                List<Document> Documents = Cluster.getValue();
                List<String[]> Data = new ArrayList<String[]>();
                for (Document Doc : Cluster.getValue())
                {
                    if(InsertToDb)
                    {
                        InsertToClusterLog(Doc.ClusterId, Doc.Name, Doc.IsCentroid, BatchID);
                    }

                    Data.add(new String[] {Integer.toString(Doc.Documentid), Integer.toString(Doc.ClusterId), Boolean.toString(Doc.IsCentroid)});
                    //Writer.write(Doc.Name + Seperator + Doc.ClusterId + Seperator + Doc.IsCentroid + String.format("%n"));
                }
                Csv_writer.writeAll(Data);
            }
            Writer.close();
        }
        catch(Exception exp)
        {
            String message = exp.getMessage();
            System.out.println(message);
        }
    }

    class ValueComparator implements Comparator<Integer>
    {
        Map<Integer, Integer> base;

        public  ValueComparator(Map<Integer, Integer> base) {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with
        // equals.
        public int compare(Integer a, Integer b) {
            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }
    }

    /// <summary>
    /// Get current BatchID or the run count (from beginning when the app was run). A batchnumber is constant for a particular complete run.
    /// </summary>
    public static int GetBatchID() throws java.sql.SQLException
    {
        int BatchID = 0;
        //FIRST GET THE BATCHID
        String Query = "SELECT ISNULL(MAX(BatchID),0) + 1 as BatchID FROM tbl_pj_ClusterLog WITH(NOLOCK)";
        ResultSet rs = DBAction.ExecuteQuery(Query);
        while (rs.next()) {
            BatchID = (int) rs.getInt(1);
        }
        return BatchID;
    }

    /// <summary>
    /// Insert the final clustered documents to database.
    /// </summary>
    /// <param name="ClusterID">Corresponding clusterid of each document</param>
    /// <param name="DocTitle">Title of the document</param>
    /// <param name="IsCentroid">"true" if the document is centroid, "False" if NOT</param>
    /// <param name="BatchID">Current batch id or run count.</param>
    private static void InsertToClusterLog(int ClusterID, String DocTitle, Boolean IsCentroid, int BatchID) throws java.sql.SQLException
    {
        //INSERT TO LOG

        String SQLQuery = "INSERT INTO  tbl_pj_ClusterLog " +
                "                       (ClusterID, BatchID, DocTitle, IsCentroid, Date) " +
                "           VALUES  " +
                "                       (?, ?, ?, ?, GetDate())";
        PreparedStatement InsertQuery = DBAction.con.prepareStatement(SQLQuery);
        InsertQuery.setInt(1, ClusterID);
        InsertQuery.setInt(2, BatchID);
        InsertQuery.setString(3, DocTitle);
        InsertQuery.setBoolean(4, IsCentroid);
        InsertQuery.execute();
    }

    /// <summary>
    /// Insert the most important (top stories or final result to be shown in UI) from clustered documents to database.
    /// </summary>
    /// <param name="BatchID">Current batch id or run count.</param>
    /// <param name="DocTitle">Title of the document</param>
    /// <param name="IsCentroid">"true" if the document is centroid, "False" if NOT</param>
    /// <param name="ClusterID">Corresponding clusterid of each document</param>
    private static void InsertToNewsItems(int BatchID, String DocumentTitle, Boolean IsCentroid, int ClusterID) throws java.sql.SQLException
    {
        String SQLQuery = "INSERT INTO  tbl_ex_NewsItems " +
                "                       (BatchID, DocumentTitle, ClusterID, IsCentroid) " +
                "           VALUES  " +
                "                       (?, ?, ?, ?)";
        PreparedStatement InsertQuery = DBAction.con.prepareStatement(SQLQuery);
        InsertQuery.setInt(1, BatchID);
        InsertQuery.setString(2, DocumentTitle);
        InsertQuery.setInt(3, ClusterID);
        InsertQuery.setBoolean(4, IsCentroid);
        InsertQuery.execute();

    }
}