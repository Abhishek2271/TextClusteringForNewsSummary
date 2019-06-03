package Utils;

import ClusterMain.Document;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadCsv
{

    public static void main(String args[])
    {
        String FileLocation = "D:\\ClusterSummary.csv";
        ReadFromCsv(FileLocation);
    }

    public static void ReadFromCsv(String FileLocation)
    {
        String csvFile = FileLocation;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] Data = line.split(cvsSplitBy);

                System.out.println("DocumentID= " + Integer.parseInt(Data[0].replaceAll("^\"|\"$", "")) + " , ClusterID=" + Data[1].replaceAll("^\"|\"$", "") + " , IsCentroid=" + Data[2].replaceAll("^\"|\"$", ""));

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Map<Integer, List<Document>> ReadFromCsvToMem(String FileLocation, boolean IsGroundTruth)
    {
        String csvFile = FileLocation;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        int Current_ClusterID = 0;
        int Previous_ClusterID = 0;
        Map<Integer, List<Document>> Cluster = new HashMap<>();
        List<Document> DocList = new ArrayList<>();
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] Data = line.split(cvsSplitBy);
                boolean IsCentroid = false;
                int DocumentID = Integer.parseInt(Data[0].replaceAll("^\"|\"$", ""));
                Document Doc = new Document(DocumentID, "", null);
                int ClusterID = Integer.parseInt(Data[1].replaceAll("^\"|\"$", ""));
                Current_ClusterID = ClusterID;
                if(!IsGroundTruth)
                {
                    IsCentroid = Boolean.parseBoolean(Data[2].replaceAll("^\"|\"$", ""));
                    Doc.IsCentroid = IsCentroid;
                }

                Doc.SetClusterID(ClusterID);
                Doc.Documentid = DocumentID;

                if(Current_ClusterID == Previous_ClusterID || Previous_ClusterID == 0)
                {
                    DocList.add(Doc);
                }
                else
                {
                    Cluster.put(Previous_ClusterID, DocList);
                    //DocList.clear(); //IF YOU JUST CLEAR THE DOCLIST THEN SOMEHOW THE WHOLE MAP BECOMES RESET TO THE NEW LIST AND WILL UPDATE ALL PREVIOUS ENTRIES IN THE LIST TO CURRENT ENTRY.
                    DocList = new ArrayList<>();
                    DocList.add(Doc);
                }
                Previous_ClusterID = Current_ClusterID;
                System.out.println("DocumentID= " + DocumentID + " , ClusterID=" + ClusterID + " , IsCentroid=" + IsCentroid);
            }
            //ADD REMAINING OF LAST BULK OF CLUSTERID TO DOCUMENT LIST MAP TO THE HASHMAP.
            Cluster.put(Current_ClusterID, DocList);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Cluster;
    }

    public static Map<Integer, Integer> ReadCentroidsFromCsv(String FileLocation)
    {
        String csvFile = FileLocation;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        Map<Integer, Integer> Centroids = new HashMap<>();

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] Data = line.split(cvsSplitBy);
                int DocumentID = Integer.parseInt(Data[0].replaceAll("^\"|\"$", ""));
                int ClusterID = Integer.parseInt(Data[1].replaceAll("^\"|\"$", ""));
                Centroids.put(DocumentID, ClusterID);
                System.out.println("DocumentID= " + DocumentID + " , ClusterID=" + ClusterID + " , IsCentroid=" + Data[2].replaceAll("^\"|\"$", ""));

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return  Centroids;
    }
}
