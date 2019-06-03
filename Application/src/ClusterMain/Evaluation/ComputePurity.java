package ClusterMain.Evaluation;

import ClusterMain.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Utils.ReadCsv.ReadFromCsvToMem;

public class ComputePurity
{
    public static void GetPurity(String FilePath_GroundTruth, String FilePath_ComputedCluster)
    {
        Map<Integer, List<Document>> GroundTruth = new HashMap<>();
        Map<Integer, List<Document>> ComputedCluster = new HashMap<>();

        GroundTruth = ReadFromCsvToMem(FilePath_GroundTruth, true);
        ComputedCluster = ReadFromCsvToMem(FilePath_ComputedCluster, false);
        int OverallCount = 0;
        int TotalDocumentCount = 0;
        for(Map.Entry<Integer, List<Document>> Computed : ComputedCluster.entrySet())
        {
            int MaxMatchCount = 0;
            TotalDocumentCount += Computed.getValue().size();
            for (Map.Entry<Integer, List<Document>> Truth : GroundTruth.entrySet())
            {
                int MatchCount = 0;
                for(Document C_Document : Computed.getValue())
                {
                    for (Document G_Document: Truth.getValue())
                    {
                        if(C_Document.Documentid == G_Document.Documentid)
                            MatchCount++;
                    }
                }
                if(MatchCount > MaxMatchCount)
                    MaxMatchCount = MatchCount;

            }
            OverallCount = OverallCount + MaxMatchCount;
        }
        double Purity = ( (double)  OverallCount)/ TotalDocumentCount;
        System.out.println("Purity is: " + Purity);
    }
}
