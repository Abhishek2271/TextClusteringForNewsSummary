import twitter4j.JSONArray;
import twitter4j.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import Utils.HashGenerator;
import Utils.TimeValidations;
import Utils.DBAction;

public class APIParser {

    public String Title;
    public String Description;
    public String Content;
    public Date PublishedAt;
    public String GET_URL_EVERYTHING;
    public String URL;
    private int SourceID;
    private String HashValue;


    public void GET_API_NEWS(String SourceName,int S_ID, int ReadInterval) throws IOException, ParseException, SQLException {

        SourceID= S_ID;

        List<String> HashFields = new ArrayList<>();
        Date CurrentDate = new Date();

    //    String GET_URL_HEADLINES = "https://newsapi.org/v2/top-headlines?sources=bbc-news&apiKey=9ed9b515e6b349988bdfd7714a07f4e3";

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);


        String dateTo = df.format(new Date());
        System.out.println(dateTo);
        String dateFrom = df.format(new Date(System.currentTimeMillis() - 3600 * 1000 * 24));
        System.out.println(dateFrom);

        GET_URL_EVERYTHING = "https://newsapi.org/v2/everything?" +
                "sources="+SourceName+
                "&pagesize=100" +
                "&sortBy=popularity" +
                "&from=" +
                dateFrom +
                "&to=" +
                dateTo +
                "&apiKey=9ed9b515e6b349988bdfd7714a07f4e3";

        String USER_AGENT = "Mozilla/5.0";
        System.out.println(GET_URL_EVERYTHING);
        URL obj = new URL(GET_URL_EVERYTHING);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {

            //    System.out.println(inputLine);
                response.append(inputLine);

            }
            in.close();

            JSONObject myJSON = new JSONObject(response.toString());
            JSONArray  jArray = myJSON.getJSONArray("articles");
            System.out.println(jArray.length());
            for(int i = 0; i < jArray.length(); i++)
            {
                Title = jArray.getJSONObject(i).getString("title");
                Description = jArray.getJSONObject(i).getString("description");


                //HashFields.add(Description);

                Content = jArray.getJSONObject(i).getString("content");
                URL = jArray.getJSONObject(i).getString("url");
                HashFields.add(URL);
                String date = jArray.getJSONObject(i).getString("publishedAt");

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                PublishedAt = sdf.parse(date);
                TimeValidations ValidateTimeInterval = new TimeValidations();


                if (ValidateTimeInterval.IsWithinValidWindow(PublishedAt, CurrentDate, ReadInterval))
                {
                    HashGenerator Gen = new HashGenerator();
                    HashValue = Gen.HashCalculator(HashFields);
                    insertToDB();
                }


            }



            // print result
           // System.out.println(response.toString());
        } else {
            System.out.println("GET request not worked");
        }



    }

    public void insertToDB() throws java.sql.SQLException
    {
        Date CurrentDate = new Date();
        String SQLQuery = "INSERT INTO  tbl_ex_APIData " +
                "                       (SourceID, Link,Title, Description,Content,PublishedDate,UpdatedDate,ParsedDate, Hash) " +
                "           VALUES  " +
                "                       (?,?, ?, ?,?, ?, ?, ?,?)";
        PreparedStatement InsertQuery = DBAction.con.prepareStatement(SQLQuery);
        InsertQuery.setInt(1, SourceID);
        InsertQuery.setString(2, URL);
        InsertQuery.setString(3, Title);
        InsertQuery.setString(4, Description);
        InsertQuery.setString(5, Content);
        InsertQuery.setTimestamp(6, new java.sql.Timestamp(PublishedAt.getTime()));
        InsertQuery.setTimestamp(7,new java.sql.Timestamp(CurrentDate.getTime()));
        InsertQuery.setTimestamp(8,new java.sql.Timestamp(CurrentDate.getTime()));
        InsertQuery.setString(9, HashValue);
        InsertQuery.execute();
    }


}
