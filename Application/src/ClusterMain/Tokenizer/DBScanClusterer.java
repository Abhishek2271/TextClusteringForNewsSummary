package ClusterMain.Tokenizer;

import org.apache.commons.math3.ml.clustering.Clusterable.*;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.stat.clustering.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DBScanClusterer
{
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        /*
        File[] files = getFiles("D:\\a");

        DBSCANClusterer dbscan = new DBSCANClusterer(.05, 50);
        List<Cluster<T>> = dbscan.cluster(getGPS(files));

        for(DoublePoint c: cluster){
            System.out.println(c);
        }
        */
    }

    private static File[] getFiles(String args) {
        return new File(args).listFiles();
    }

    private static List<String> GetStr()
    {
        List<String> str = new ArrayList<>();
        str.add("This");
        str.add("These");
        str.add("mail");
        str.add("male");
        return str;
    }


    private static List<DoublePoint> getGPS(File[] files) throws FileNotFoundException, IOException {

        List<DoublePoint> points = new ArrayList<DoublePoint>();
        for (File f : files) {
            BufferedReader in = new BufferedReader(new FileReader(f));
            String line;

            while ((line = in.readLine()) != null) {
                try {
                    double[] d = new double[2];
                    d[0] = Double.parseDouble(line.split(",")[1]);
                    d[1] = Double.parseDouble(line.split(",")[2]);
                    points.add(new DoublePoint(d));
                } catch (ArrayIndexOutOfBoundsException e) {
                } catch(NumberFormatException e){
                }
            }
        }
        return points;
    }


}
