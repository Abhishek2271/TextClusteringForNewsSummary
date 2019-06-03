package ClusterMain;
import weka.core.Instance;
import weka.core.Instances;
import weka.clusterers.*;
import weka.gui.beans.DataSource;
import weka.core.converters.ConverterUtils;
import java.io.FileNotFoundException;
import java.io.IOException;


public class DBScanWekaClusterer
{
    public static void main(String[] args) throws FileNotFoundException, IOException, java.lang.Exception
    {
       /* ConverterUtils.DataSource source = new ConverterUtils.DataSource("D:\\a.csv");
        //ConverterUtils.DataSource source = new DataSource("/some/where/data.csv");
        //convert the data to "Instances" instances
        Instances data = source.getDataSet();
        // The option string is generated using the WEKA GUI
        String Options="-init 0 -max-candidates 100 -periodic-pruning 10000 -min-density 2.0 -t1 -1.25 -t2 -1.0 -N 10 -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -num-slots 1 -S 10";

        SimpleKMeans kmean= new SimpleKMeans();
        kmean.setOptions(weka.core.Utils.splitOptions(Options));
        kmean.buildClusterer(data); System.out.println(kmean.toString());
        //we get the 6th instance (line)
        Instance test = data.get(1); //we classify the instance
        System.out.println(kmean.clusterInstance(test));*/
    }
}
