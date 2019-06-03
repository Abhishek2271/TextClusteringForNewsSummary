package RSS;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
/*
    <Created Date> Nov 2, 2018 </CreatedDate>
    <LastUpdated> Nov 2, 2018 </LastUpdated>
    <About>
        Class containing all currently supported RSS fields.
    </About>
*/
//TODO: Use these fields to build a XML instead of a seprate table.
 class RSSFields
{
    String Title;
    String Link;
    String Description;
    Date PublishedDate;
    Date UpdatedDate;
    String ComputedHash;

    //Unused Fields.
    String Author;
    String Category;
    String Comments;
    String Contributers;
    String Source;
    String Enclosure;
}

class RSSChannelElements
{
    String Language;        //Same for all contents of one rss link.
    //String Copyright;
    String managingEditor;
    String webMaster;
    Date PublishedDate;
    Date LastBuildDate;
    String Generator;
    //String Docs;
    //String ImageURI;
    String Description;
    String Author;
}
