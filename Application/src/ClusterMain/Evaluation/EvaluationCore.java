package ClusterMain.Evaluation;
import ClusterMain.Document;
import Utils.DBAction;
import Utils.IniParser;
import Utils.ReadCsv.*;

import java.io.*;
import java.util.*;

import static Utils.ReadCsv.ReadCentroidsFromCsv;
import static Utils.ReadCsv.ReadFromCsv;
import static Utils.ReadCsv.ReadFromCsvToMem;

public class EvaluationCore
{
    static String FilePath_GroundTruth = "D:\\GroundTruth.csv";
    static String FilePath_ComputedCluster = "D:\\ClusterSummary.csv";

    public static void main(String[] args)
    {
        try
        {
            //String FilePath = "D:\\ClusterSummary.csv";
            //ReadFromCsv(FilePath);
            //GeClusterFromCSV(FilePath, false);
            IniParser parser = new IniParser();
            parser.ReadClusterEvalSettings();
            ComputePurity.GetPurity(FilePath_GroundTruth, FilePath_ComputedCluster);
        }
        catch (IOException exp)
        {
            exp.printStackTrace();
        }
    }

    public static void GeClusterFromCSV(String FilePath, boolean IsGroundTruth)
    {
        Map<Integer, List<Document>> Cluster = new HashMap<>();

        Cluster = ReadFromCsvToMem(FilePath, IsGroundTruth);
    }

    public static Map<Integer, Document> GetSpecificCentroid(List<Document> DocList)
    {
        String FileLocation = "D:\\TU\\_SEM 4\\DataApplication_devloper\\DataScienceApplication\\_PURITY_Data\\TOKENS WITHOUT SNOWBALL FILTER\\ClusterSummary_CENTROIDS.csv";
        Map<Integer, Document> Centroid = new HashMap<>();
        Map<Integer, Integer> CentroidsFromCSV = new HashMap<>();
        CentroidsFromCSV = ReadCentroidsFromCsv(FileLocation);
        for (Map.Entry<Integer, Integer> Centroids : CentroidsFromCSV.entrySet()) {
            for (Document doc : DocList) {
                if (doc.Documentid == Centroids.getKey()) {
                    Document CentroidDoc = doc;
                    Centroid.put(Centroids.getValue(), CentroidDoc);
                    CentroidDoc.IsCentroid = true;
                    CentroidDoc.SetClusterID(Centroids.getValue());
                }
            }
        }
        return Centroid;
    }
}