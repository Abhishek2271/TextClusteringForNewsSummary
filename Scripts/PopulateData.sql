--POPULATE SOURCE TYPES
INSERT INTO tbl_pj_SourceTypes values ('RSS', 1440)
INSERT INTO tbl_pj_SourceTypes values ('TWITTER', 1440)
INSERT INTO tbl_pj_SourceTypes values ('NEWSAPI', 1440)

--POPULATE SOURCEDETAILS
INSERT INTO tbl_pj_SourceInfo values ('BBC_WORLD_NEWS', 1,'http://feeds.bbci.co.uk/news/world/rss.xml')
INSERT INTO tbl_pj_SourceInfo values ('CBN_WORLD_NEWS', 1,'http://www1.cbn.com/app_feeds/rss/news/rss.php?section=world')
INSERT INTO tbl_pj_SourceInfo values ('Reuters', 1,'http://feeds.reuters.com/Reuters/worldNews')
INSERT INTO tbl_pj_SourceInfo values ('GOOGLE_WORLD_NEWS', 1,'https://news.google.com/news/rss/headlines/section/topic/WORLD?ned=us&hl=en&gl=US')
INSERT INTO tbl_pj_SourceInfo values ('THE_NEW_YORK_TIMES', 1,'https://www.nytimes.com/svc/collections/v1/publish/https://www.nytimes.com/section/world/rss.xml')
INSERT INTO tbl_pj_SourceInfo values ('THE_GUARDIAN_PRESS', 1,'https://www.theguardian.com/world/rss')
INSERT INTO tbl_pj_SourceInfo values ('Yahoo_NEWS', 1,'https://www.yahoo.com/news/rss/world')
INSERT INTO tbl_pj_SourceInfo values ('THE_WEST_AUSTRALIAN', 1,'https://thewest.com.au/rss-feeds')
INSERT INTO tbl_pj_SourceInfo values ('DAILY TELEGRAPH LIST', 1,'https://www.dailytelegraph.com.au/help-rss')
INSERT INTO tbl_pj_SourceInfo values ('USA Today', 1,'http://rssfeeds.usatoday.com/usatoday-NewsTopStories')


--POPULATE TWITTERDATA
INSERT INTO tbl_pj_SourceInfo values ('BBC_World', 2,'https://twitter.com/BBCWorld')

INSERT INTO tbl_pj_SourceInfo values ('CCNI', 2,'https://twitter.com/cnni')

INSERT INTO tbl_pj_SourceInfo values ('FOX_News', 2,'https://twitter.com/FoxNews')

INSERT INTO tbl_pj_SourceInfo values ('RT_com', 2,'https://twitter.com/RT_com')

INSERT INTO tbl_pj_SourceInfo values ('CGTN_Official', 2,'https://twitter.com/CGTNOfficial')


--POPULATE APIDATA
INSERT INTO tbl_pj_SourceInfo values ('bbc-news', 3,'https://newsapi.org/docs')
INSERT INTO tbl_pj_SourceInfo values ('fox-news', 3,'https://video.foxnews.com/v/5990710115001/')
INSERT INTO tbl_pj_SourceInfo values ('cnn', 3,'http://us.cnn.com')
