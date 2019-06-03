package ClusterMain;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.*;


public class Document
{
    public String Name;
    public Map<String, Double> TermTfIDF;
    public int ClusterId;
    public double Score;
    public String ClusterName;
    public boolean IsCentroid;
    public int Documentid;

    public Document(int _Documentid, String DocName, Map<String, Double> DocTfIdf)
    {
        Name = DocName;
        TermTfIDF = DocTfIdf;
        Documentid = _Documentid;
    }

    public void SetDocumentScore(double _Score) { Score = _Score; }

    //Set the ClusterID in which the document currently is in.
    public void SetClusterID(int _ClusterID)
    {
        ClusterId = _ClusterID;
    }

    //ClusterName, Not used not but can be useful later :)
    public void ClusterName(String _ClusterName)
    {
        ClusterName = _ClusterName;
    }
}
