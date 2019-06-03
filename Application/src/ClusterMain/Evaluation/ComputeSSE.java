package ClusterMain.Evaluation;

import ClusterMain.Document;

import java.util.List;
import java.util.Map;

public class ComputeSSE
{
    public static double  GetECDisctance(Document CentroidDoc, Document CurrentDoc) {
        Map<String, Double> CentroidTf = CentroidDoc.TermTfIDF;
        Map<String, Double> ComparedDoc = CurrentDoc.TermTfIDF;
        double ECDistance = 0;
        for (Map.Entry<String, Double> Doc : ComparedDoc.entrySet()) {
            if (CentroidTf.containsKey(Doc.getKey())) {
                ECDistance += Math.pow(Doc.getValue() - CentroidTf.get(Doc.getKey()), 2);
            }
            else
                ECDistance += Math.pow(Doc.getValue() - 0 , 2);
        }
        return (ECDistance);
    }

    public double GetSSE(Map<Integer, Document> Centroids, Map<Integer, List<Document>> ClusteredDocs)
    {
        double SSE = 0;
        for (Map.Entry<Integer, Document> CentroidDoc : Centroids.entrySet()) {
            List<Document> CurrentClusterDocs = ClusteredDocs.get(CentroidDoc.getKey());
            for (Document CurrentDoc : CurrentClusterDocs) {
                SSE += GetECDisctance(CentroidDoc.getValue(), CurrentDoc);
            }
        }
        //System.out.println("Computed Sum Of Squared Errors:" + SSE + ", for " + Centroids.size() + " Centroid(s)");
        int Size = Centroids.size()-1;
        if(Size == 0)
            Size =1;
        System.out.println(SSE + "  " + Size);

        return SSE;
    }
}
