package ClusterMain.SimilarityMeasure;

import ClusterMain.Document;

import java.util.Map;

public class CosineSimilarity
    {
       public static double  GetCosineSimilarity(Document CentroidDoc, Document CurrentDoc) {
           double Score = 0;
           double DotProduct = 0;
           double Mag1 = 0;
           double Mag2 = 0;

           Map<String, Double> CentroidTf = CentroidDoc.TermTfIDF;
           Map<String, Double> ComparedDoc = CurrentDoc.TermTfIDF;

           for (Map.Entry<String, Double> Doc : ComparedDoc.entrySet()) {
               if (CentroidTf.containsKey(Doc.getKey())) {
                   DotProduct += Doc.getValue() * CentroidTf.get(Doc.getKey());
                   Mag1 += Math.pow(Doc.getValue(), 2);
                   Mag2 += Math.pow(CentroidTf.get(Doc.getKey()), 2);
               }
           }
           if (Mag1 != 0 | Mag2 != 0) {
               Score = DotProduct / (Math.sqrt(Mag1) * Math.sqrt(Mag2));
           } else {
               return 0.0;
           }
           return Score;
       }
    }
