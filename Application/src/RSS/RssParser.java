package RSS;

import java.sql.PreparedStatement;
import java.net.URL;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.sql.ResultSet;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndContent;
import java.util.Date;

import Utils.*;

/*
    <Created Date> Nov 2, 2018 </CreatedDate>
    <LastUpdated> Nov 3, 2018 </LastUpdated>
    <About>
        Parses the RSS Source data from the provided URL.
        Inserts the parsed data to sql backend
    </About>
*/

public class RssParser {

    private int SourceID;       // SourceID corresponding to Unique source in tbl_pj_sourceinfo
    private String SourceName;  // SourceName from tbl_pj_sourceinfo
    private String Src_URL;     // RSS URL from which the feed has to be fetched
    private int ReadInterval;   // Interval at which the news should be collected. No news within this interval will be collected. This is in Minutes



    /// <summary>
    /// Class constructor for RSS parser.
    /// </summary>
    /// <param name="SourceID">ID of the source that is being parsed</param>
    /// <param name="SourceName">Recognizable Name of the source  that is being parsed</param>
    /// <param name="SrcURL">Accessible URL of the source</param>
    public RssParser(int _SourceID, String _SourceName, String _SrcURL, int _ReadInterval)
    {
        SourceID = _SourceID;
        SourceName = _SourceName;
        Src_URL = _SrcURL;
        ReadInterval = _ReadInterval;
    }


    /// <summary>
    /// Parse RSS feeds but only one URL at a time. Notice the function only has a single source URL in args.
    /// Parsing multiple source URI has to be done via loop in the calling function.
    /// </summary>
    public void PopulateRSSData()
    {
        try
        {
            URL Feed = ParseRSSFromFeed();
            ParseRSS(Feed);
        }
        catch(java.net.MalformedURLException exp)
        {
            String message = exp.getMessage();
            System.out.println(message);
        }
        catch(com.rometools.rome.io.FeedException fexp)
        {
            String message = fexp.getMessage();
            System.out.println(message);
        }
        catch(java.io.IOException ioexp)
        {
            String message = ioexp.getMessage();
            System.out.println(message);
        }
    }

    /// <summary>
    /// Return source URL from the given string.
    ///</summary>
    public URL ParseRSSFromFeed() throws java.net.MalformedURLException
    {
        URL feedUrl = new URL(Src_URL);
        return feedUrl;
    }

    /// <summary>
    /// Parse RSS from the input URL and insert to DB.
    ///</summary>
    /// <param name="feedUrl">RSS feed URL</param>
    public void ParseRSS(URL feedUrl) throws com.rometools.rome.io.FeedException, java.io.IOException {
        //TODO: EVERY THING HERE SHOULD BE IN A TRANSACTION
        List<String> HashFields = new ArrayList<>();
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedUrl));
        RSSChannelElements ChannelElements = new RSSChannelElements();
        Date CurrentDate = new Date();
        SimpleDateFormat SDF = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        //GET ADDITIONAL PROPERTIES OF THE CHANNEL
        //GET CHANNEL AUTHOR
        String ch_author = feed.getAuthor();
        ChannelElements.Author = ch_author;
        //GET PUBLISHED DATE
        Date ch_PubDate = feed.getPublishedDate();
        ChannelElements.PublishedDate = ch_PubDate;
        //GET LANGUAGE
        String ch_Language = feed.getLanguage();
        ChannelElements.Language = ch_Language;
        //GET WEBMASTER
        String ch_WebMaster = feed.getWebMaster();
        ChannelElements.webMaster = ch_WebMaster;
        //GET MANAGING EDITOR
        String ch_ME = feed.getManagingEditor();
        ChannelElements.managingEditor = ch_ME;
        //GET GENERATOR
        String ch_generator = feed.getGenerator();
        ChannelElements.Generator = ch_generator;
        //GET DESCRIPTION
        String ch_description = feed.getDescription();
        ChannelElements.Description = ch_description;
        int CurrentChannelID = 0;
        //TODO: DO THIS IN A STORED PROCEDURE
        try
        {
            TimeValidations ValidateTime = new TimeValidations();
            if (ValidateTime.IsWithinValidWindow(CurrentDate, ch_PubDate, ReadInterval))
            {
                //INSERT TO DB THE CHANNEL DETAILS
                CurrentChannelID = InsertChannelDetails(ChannelElements);
            }
        }
        catch(java.sql.SQLException exp)
        {
            String message = exp.getMessage();
            System.out.println(message);
        }


        //GET ELEMENTS FROM THE "ITEMS"
        List entries = feed.getEntries();
        Iterator it = entries.iterator();
        while (it.hasNext()) {
            RSSFields RssFieldValues = new RSSFields();
            SyndEntry entry = (SyndEntry) it.next();
            //GET TITLE
            System.out.println(entry.getTitle());
            String Title = entry.getTitle();
            RssFieldValues.Title = Title;
            //HashFields.add(Title);
            //GET LINK
            System.out.println(entry.getLink());
            RssFieldValues.Link = entry.getLink();
            HashFields.add(RssFieldValues.Link);
            //GET DESCRIPTION
            SyndContent description = entry.getDescription();
            System.out.println(description.getValue());
            RssFieldValues.Description = description.getValue();
            //HashFields.add(description.getValue());
            //GET PUBLISHED DATE
            Date Pub_Date = entry.getPublishedDate();
            System.out.println(SDF.format(Pub_Date));
            RssFieldValues.PublishedDate = entry.getPublishedDate();
            //GET UPDATED DATE
            Date U_date = entry.getUpdatedDate();
            System.out.println(U_date);
            RssFieldValues.UpdatedDate = entry.getPublishedDate();
            //GET AUTHOR
            String Author = entry.getAuthor();
            RssFieldValues.Author = Author;
            System.out.println(Author);
            //GET COMMENTS;
            String Comments = entry.getComments();
            RssFieldValues.Comments = Comments;
            System.out.println();
            try {
                //org.joda.time.Period P = new org.joda.time.Period(Pub_Date, CurrentDate);
                //if(SDF.parse(Pub_Date.toString()) == SDF.parse(CurrentDate.toString()))
                //org.joda.time.Period p = new org.joda.time.Period(CurrentDate, Pub_Date);
                //ONLY INSERT IF THE FEED IS WITHIN THE SPECIFIED PERIOD
                TimeValidations ValidateTime = new TimeValidations();
                if (ValidateTime.IsWithinValidWindow(CurrentDate, U_date, ReadInterval))
                {
                    //INSERT NEWS SPECIFIC VALUES TO DB
                    HashGenerator Gen = new HashGenerator();
                    RssFieldValues.ComputedHash = Gen.HashCalculator(HashFields);
                    InsertToDB(RssFieldValues, CurrentChannelID);
                }
            } catch (java.sql.SQLException ex) {
                String message = ex.getMessage();
                System.out.println(message);
            }
        }
        // System.out.println(feed);
    }

    /// <summary>
    /// Insert the parsed RSS fields (Individual Fields Specific) to DB.
    ///</summary>
    /// <param name="RssFieldValues">Class containing all parsed values</param>
    public void InsertToDB(RSSFields RssFieldValues, int ChannelID) throws java.sql.SQLException
    {
        String SQLQuery = "INSERT INTO  tbl_ex_RSSData " +
                "                       (SourceID, ChannelInstanceID, Title, Link, Description,PublishedDate,UpdatedDate, ParsedDate, Hash) " +
                "           VALUES  " +
                "                       (?, ?, ?, ?, ?, ?, ?,GetDate(), ?)";
        PreparedStatement InsertQuery = DBAction.con.prepareStatement(SQLQuery);
        InsertQuery.setInt(1, SourceID);
        InsertQuery.setInt(2, ChannelID);
        InsertQuery.setString(3, RssFieldValues.Title);
        InsertQuery.setString(4, RssFieldValues.Link);
        InsertQuery.setString(5,RssFieldValues.Description);
        InsertQuery.setTimestamp(6,new java.sql.Timestamp(RssFieldValues.PublishedDate.getTime()));
        InsertQuery.setTimestamp(7,new java.sql.Timestamp(RssFieldValues.UpdatedDate.getTime()));
        InsertQuery.setString(8, RssFieldValues.ComputedHash);
        InsertQuery.execute();
    }

    /// <summary>
    /// Insert the parsed RSS fields (Channel Specifics) to DB.
    ///</summary>
    /// <param name="ChannelElements">Class containing all parsed values for channel specific data</param>
    public int InsertChannelDetails(RSSChannelElements ChannelElements) throws java.sql.SQLException
    {
        String SQLQuery = "INSERT INTO  tbl_ex_channeldetails " +
            "                       (SourceID, AuthorName, Language, PublishedDate, WebMaster, LastBuildDate, ManagingEditor, Generator, Description) " +
            "               SELECT  " +
            "                       ?, ?, ?, ?, ?, ?, ?, ?, ? " +
            "               WHERE  NOT EXISTS " +
                "           (SELECT SourceID, Language,  Description FROM tbl_ex_channeldetails " +
                "           WHERE" +
                "                   SourceID = ? " +
                "                   AND Language = ? " +
                "                   AND Description  = ?)" +
                "           SELECT MAX(ChannelID) as channelID FROM tbl_ex_channeldetails WHERE SourceID = ?";

        PreparedStatement InsertQuery = DBAction.con.prepareStatement(SQLQuery);
        InsertQuery.setInt(1, SourceID);
        InsertQuery.setString(2, ChannelElements.Author);
        InsertQuery.setString(3, ChannelElements.Language);
        if(ChannelElements.PublishedDate != null)
            InsertQuery.setTimestamp(4, new java.sql.Timestamp(ChannelElements.PublishedDate.getTime()));
        else
            InsertQuery.setNull(4, Types.DATE);
        InsertQuery.setString(5, ChannelElements.webMaster);
        if(ChannelElements.LastBuildDate != null)
            InsertQuery.setTimestamp(6, new java.sql.Timestamp(ChannelElements.LastBuildDate.getTime()));
        else
            InsertQuery.setNull(6, Types.DATE);
        InsertQuery.setString(7, ChannelElements.managingEditor);
        InsertQuery.setString(8, ChannelElements.Generator);
        InsertQuery.setString(9, ChannelElements.Description);

        InsertQuery.setInt(10, SourceID);
        InsertQuery.setString(11, ChannelElements.Language);

        InsertQuery.setString(12, ChannelElements.Description);
        InsertQuery.setInt(13, SourceID);
        ResultSet rs = InsertQuery.executeQuery();
        int ChannelID = 0;
        while(rs.next()){
            ChannelID = rs.getInt("channelID"); //IDTable

        }
        System.out.println("ChannelID IST: " + ChannelID);
        return ChannelID;
    }
}
