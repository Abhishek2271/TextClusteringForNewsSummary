import java.io.IOException;
import java.sql.SQLException;
import java.sql.ResultSet;

import ClusterMain.ClusterCore;
import RSS.RssParser;
import java.text.ParseException;
import Twitter.TwitterParser;
import Utils.*;

public class Main
{
    public static void main(String args[])
    {
        BeginProc();
    }

    /// <summary>
    /// Get the data from the sources to the database.
    /// </summary>
    public static void BeginProc()
    {
        try {
            /*
            try {
                Date c_date = new Date();
                String Date1 = "2018-11-08 23:32:20.000";
                String Date2 = "2018-11-10 00:05:15.820";
                SimpleDateFormat SDF = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                Date N_date = SDF.parse(Date1);
                Date M_date = SDF.parse(Date2);
                //Date p = SDF.parse(c_date);
                TimeValidations v = new TimeValidations();
                v.IsWithinValidWindow(c_date, N_date);
            }
            catch (java.text.ParseException e)
            {}
            */

            // READ INI FILE
            IniParser parser = new IniParser();
            parser.ReadDBSettings();

            System.out.println(parser.DBName);
            System.out.println(parser.UserName);
            System.out.println(parser.Password);

            //INITIATE DATABASE CONNECTION
            DBAction.InitiateSqlConnection();

            //Start iteriating within sources
            String GetSourceData = "SELECT " +
                    "                    S2.*, S1.SourceID," +
                    "                    S1.SourceName, " +
                    "                    S1.URL, " +
                    "                    S2.ReadInterval " +
                    "               FROM " +
                    "                    tbl_pj_SourceInfo S1, " +
                    "                    tbl_pj_SourceTypes S2 " +
                    "               WHERE " +
                    "                    s1.TypeID = s2.TypeID";
            ResultSet rs = DBAction.ExecuteQuery(GetSourceData);

            //TODO: OPTIMIZATION HERE. FOR LARGE NUMBER OF ROWS THIS IS NOT IDEAL METHOD TO POPULATE.
            while (rs.next()) {
                //HANDLE RSS FEEDS
                if (Integer.parseInt(rs.getString("TypeID")) == 1) {
                    //Start populating data from all RSS FEED TYPES
                    RssParser Parse = new RssParser(Integer.parseInt(rs.getString("SourceID")), rs.getString("SourceName"), rs.getString("URL"), Integer.parseInt(rs.getString("ReadInterval")));
                    Parse.PopulateRSSData();
                }
                //HANDLE TWITTER FEEDS
                else if (Integer.parseInt(rs.getString("TypeID")) == 2) {
                    TwitterParser tp = new TwitterParser();
                    System.out.println(rs.getString("SourceName"));
                    tp.parseProfile(rs.getString("SourceName"), Integer.parseInt(rs.getString("SourceID")), Integer.parseInt(rs.getString("ReadInterval")));

                }
                //Get data from FOX videos and CNN web
                else if (Integer.parseInt(rs.getString("TypeID")) == 3) {
                    APIParser apiParser = new APIParser();
                    apiParser.GET_API_NEWS(rs.getString("SourceName"), Integer.parseInt(rs.getString("SourceID")), Integer.parseInt(rs.getString("ReadInterval")));
                }
            }
            //String[] arges = new String[1];
            //ClusterCore.main(arges);
        } catch (IOException e) {
            String message = e.getMessage();
            System.out.println(message);
        } catch (SQLException exp) {
            String message = exp.getMessage();
            System.out.println(message);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}