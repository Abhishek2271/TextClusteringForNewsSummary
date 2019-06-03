package Utils;

import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;


/*
    <Created Date> Nov 1, 2018 </CreatedDate>
    <LastUpdated> Nov 2, 2018 </LastUpdated>
    <About>
        Parses the ini file.
        The default values are specified for fail safe only but can be misleading if ini is not read.
        The default location of ini file is assumed to be within project location.

        NOTE: Uses ini4j.wini api for ini parsing. Please make sure it is included in the project.
    </About>
*/

public class IniParser {

    //Get ini file location
    public static String IniName = System.getProperty("user.dir") + "\\src\\UserSettings.ini";

    //Set default values.
    //TODO: These values should not be specified. Should always be null and later checked. Assigning values here is very misleading.
    //DB SETTINGS
    public static String Host = "Localhost";    // Current hostname connecting to SQL
    public static String DBName = "DSApp_PCD";  // Current SQL Control DBName
    public static String UserName = "sa";       // SQL Username
    public static String Password = "sandman";  // SQL Password
    public static int Port = 1433;              // Port running SQL. This is 1433 by default
    //CLUSTER SETTINGS
    public static int NumberOfClusters = 3;
    public static boolean FilterStopWords = true;

    public static String GroundTruth_File_Location = "D:\\GroundTruth.csv";
    public static String ClusterResults_File_Location = "D:\\ClusterSummary.csv";

    public static int TopStoriesCount = 10;

    public static int News_Collection_delay = 24; // Collect news every [Hour]

    /// <summary>
    /// Read db related Settings from ini file.
    ///</summary>
    public void ReadDBSettings() throws IOException
    {
        System.out.println(IniName);
        Wini ini = new Wini(new File(IniName));

        Host = ini.get("DBSettings","Host");
        DBName = ini.get("DBSettings","ControlDB");
        UserName = ini.get("DBSettings", "Username");
        Password = ini.get("DBSettings", "Password");
        Port = ini.get("DBSettings", "Port", int.class);
    }


    /// <summary>
    /// Read Clustering related Settings from ini file.
    ///</summary>
    public void ReadClusterSettings() throws IOException
    {
        //Read cluster related settings.
        Wini ini = new Wini(new File(IniName));
        NumberOfClusters = ini.get("Clustering_Settings", "Number_of_Clusters", int.class);
        FilterStopWords = ini.get("Clustering_Settings", "Ignore_Stop_Words", boolean.class);
    }

    public void ReadClusterEvalSettings() throws IOException
    {
        Wini ini = new Wini(new File(IniName));
        GroundTruth_File_Location = ini.get("Clustering_Settings", "GroundTruth_File_Location");
        ClusterResults_File_Location = ini.get("Clustering_Settings", "ClusterResults_File_Location");

    }

    public void ReadStoriesSettings()throws IOException
    {
        Wini ini = new Wini(new File(IniName));
        TopStoriesCount = ini.get("StoriesSettings", "TopNewsCount", int.class);
    }

    public void CollectionDelay() throws IOException
    {
        Wini ini = new Wini(new File(IniName));
        News_Collection_delay = ini.get("StoriesSettings", "TopNewsCount", int.class);
    }
}
