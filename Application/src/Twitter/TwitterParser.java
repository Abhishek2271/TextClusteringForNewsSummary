package Twitter;

import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sun.security.krb5.internal.crypto.Des;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;
import Utils.*;

public class TwitterParser {

    private String SourceName;
    private int SourceID;
    private Date CreatedAt;
    private String URL;
    private String Description;
    private List<Status> statuses;
    private String HashValue;

    private String OAuthConsumerKey;
    private String OAuthConsumerSecret;
    private String setOAuthAccessToken;
    private String setOAuthAccessTokenSecret;


    public TwitterParser()
    {
        SetUserAccessDetails();
       // DateFormat CurrentDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    }

    private void SetUserAccessDetails()
    {
        OAuthConsumerKey = "eDPGVzVBIdc8Ys4nuM8stMlRe";
        OAuthConsumerSecret = "yHhLn3qv2u2tEhXAJsXrBwUD7wDPzenK0Qq7CnsH7pWXfPerPk";
        setOAuthAccessToken = "778065307739054080-HQzSGXefZ7a1BdAfwqOLAZ9hrxw80xe";
        setOAuthAccessTokenSecret = "5eIg6bV7SEl4p4cylqjOzdHbQpp1TwpzqrCOminskSZQB";
    }

    public static List<String> extractUrls(String text)
    {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }

    private void removeUrl()
    {
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(Description);
        int i = 0;
        while (m.find()) {
            Description = Description.replaceAll(m.group(i),"").trim();
            i++;
        }

    }

    public void parseProfile(String SourceName, int S_ID, int ReadInterval)
    {
        try
        {
            List<String> HashFields = new ArrayList<>();
            Date CurrentDate = new Date();
            SourceID = S_ID;

            Paging p = new Paging();
            p.setCount(100);

            ConfigurationBuilder cb = new ConfigurationBuilder();

            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(OAuthConsumerKey)
                    .setOAuthConsumerSecret(OAuthConsumerSecret)
                    .setOAuthAccessToken(setOAuthAccessToken)
                    .setOAuthAccessTokenSecret(setOAuthAccessTokenSecret);

            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();

            User user = twitter.showUser(SourceName);
            statuses = twitter.getUserTimeline(SourceName, p);

            for (Status status : statuses)
            {

                System.err.println(status.getText());
                // System.err.println(status.getSource());
                Description = status.getText();
                //HashFields.add(Description);
                CreatedAt = status.getCreatedAt();

                TimeValidations ValidateTimeInterval = new TimeValidations();
                //Get news from only last 24 hours
                if (ValidateTimeInterval.IsWithinValidWindow(CreatedAt, CurrentDate, ReadInterval))
                {
                    List<String> extractedUrls = extractUrls(Description);
                    for (String url : extractedUrls)
                    {
                        URL = url;
                    }
                    System.err.println(URL);
                    //Do not remove URL for now.
                    //removeUrl();
                    HashFields.add(URL);
                    System.err.println(Description);
                    //GET HASH VALUE OF THE FIELD
                    HashGenerator Gen = new HashGenerator();
                    HashValue = Gen.HashCalculator(HashFields);
                    try
                    {
                        //INSERT VALUES TO DB
                        insertToDB();
                    }
                    catch (java.sql.SQLException ex) {
                        String message = ex.getMessage();
                        System.out.println(message);
                    }

                }
            }
        }
        catch (TwitterException te)
        {
            System.out.println("Failed to get status: " + te.getMessage());
        }
    }


    public void insertToDB() throws java.sql.SQLException
    {
        Date CurrentDate = new Date();
        String SQLQuery = "INSERT INTO tbl_ex_TwitterData " +
                "                       (SourceID, Link, Description,PublishedDate,UpdatedDate,ParsedDate, Hash) " +
                "           VALUES  " +
                "                       (?, ?, ?,?, ?, ?, ?)";
        PreparedStatement InsertQuery = DBAction.con.prepareStatement(SQLQuery);
        InsertQuery.setInt(1, SourceID);
        InsertQuery.setString(2, URL);
        InsertQuery.setString(3, Description);
        InsertQuery.setTimestamp(4, new java.sql.Timestamp(CreatedAt.getTime()));
        InsertQuery.setTimestamp(5,new java.sql.Timestamp(CurrentDate.getTime()));
        InsertQuery.setTimestamp(6,new java.sql.Timestamp(CurrentDate.getTime()));
        InsertQuery.setString(7, HashValue);
        InsertQuery.execute();
    }
}
