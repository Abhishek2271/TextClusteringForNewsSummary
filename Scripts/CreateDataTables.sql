USE [DSApp_PCD]
	/*Created Date: 02 Nov 2018*/
	
GO
	/*
		Created Date: 03 Nov 2018
		Description: This table contains all sources that are being used by the application
	*/
	CREATE TABLE tbl_pj_SourceTypes
	(
		TypeID INT IDENTITY(1, 1) Primary Key,
		[Type] Nvarchar(1000),
		ReadInterval INT DEFAULT 1440
				 
	)
GO


	/*
		Created Date: 03 Nov 2018
		Description: Detailed info about each source
	*/
	CREATE TABLE tbl_pj_SourceInfo
	(
		SourceID INT IDENTITY(1, 1) Primary Key,
		SourceName Nvarchar(1000),
		TypeID INT,
		URL Nvarchar(max),
		FOREIGN KEY (TypeID) REFERENCES tbl_pj_SourceTypes(TypeID) 
	)
	
	/*
		Created Date: 03 Nov 2018
		Description: Field Names of all the source tables. The purpose of this table is to create tables automatically from SP
		--NOT USABLE NOW
	*/
	CREATE TABLE tbl_pj_SourceDetails
	(
		SourceID INT,
		Fields Nvarchar(max)
		FOREIGN KEY (SourceID) REFERENCES tbl_pj_SourceInfo(SourceID)
	)
	
	
	/*
		Created Date: 03 Nov 2018
		Description: All channel specific data extracted from RSS link in the source
	*/
	CREATE TABLE tbl_ex_ChannelDetails
	(
		ChannelID INT IDENTITY(1,1) PRIMARY KEY,
		SourceID INT,
		AuthorName Nvarchar(80),
		[Language] Nvarchar(40),
		WebMaster Nvarchar(80),
		ManagingEditor Nvarchar(80),
		Generator Nvarchar(2000),
		[Description] Nvarchar(max),	
		[PublishedDate] Date,
		[LastBuildDate] Date,
		FOREIGN KEY (SourceID) REFERENCES tbl_pj_SourceInfo(SourceID)
	)

	/*
		Created Date: 02 Nov 2018
		Description: All info extracted from RSS is inserted to this table
	*/
	
	CREATE TABLE tbl_ex_RSSData
	(
		ID	BIGINT IDENTITY(1, 1),
		SourceID INT,
		ChannelInstanceID INT,
		Title Nvarchar(2000),
		Link Nvarchar(2000),
		[Description] Nvarchar(max),	
		[PublishedDate] DateTime,
		[UpdatedDate] DateTime,	
		[ParsedDate] DateTime,
		[Hash] nvarchar(80) PRIMARY KEY WITH (IGNORE_DUP_KEY = ON),		
		Comments Nvarchar(200) DEFAULT NULL,
		Author Nvarchar(80) DEFAULT NULL,
		Category Nvarchar(20) DEFAULT NULL,
		Contributers Nvarchar(2000) DEFAULT NULL,
		FOREIGN KEY (SourceID) REFERENCES tbl_pj_SourceInfo(SourceID),
		FOREIGN KEY (ChannelInstanceID) REFERENCES tbl_ex_ChannelDetails(ChannelID)
	)
	
	/*
		Created Date: 03 Nov 2018
		Description: Details from data extracted from twitter feed		
	*/
	CREATE TABLE tbl_ex_TwitterData
	(
		ID	BIGINT IDENTITY(1, 1),
		SourceID INT,
		Link Nvarchar(2000),
		[Description] Nvarchar(max),	
		[PublishedDate] DateTime,
		[UpdatedDate] DateTime,	
        [ParsedDate] DateTime,
        [Hash] nvarchar(80) PRIMARY KEY WITH (IGNORE_DUP_KEY = ON),
		FOREIGN KEY (SourceID) REFERENCES tbl_pj_SourceInfo(SourceID)
	)
	/*
		Created Date: 27 Nov 2018
		Description: Details from News API extracted from BBC News		
	*/
	
	CREATE TABLE tbl_ex_NewsAPI
	(
		ID	BIGINT IDENTITY(1, 1),
		SourceID INT,
		Link Nvarchar(2000),
		[Title] Nvarchar(max),
		[Description] Nvarchar(max),
		[Content] Nvarchar(max),	
		[PublishedDate] DateTime,
		[UpdatedDate] DateTime,	
        [ParsedDate] DateTime,
        [Hash] nvarchar(80) PRIMARY KEY WITH (IGNORE_DUP_KEY = ON),
		FOREIGN KEY (SourceID) REFERENCES tbl_pj_SourceInfo(SourceID)
	)
	
	/*
		Created Date: 01 Jan 2019
		Description: News API is a HTTP REST API for searching and retrieving live News articles from all over the web. 
        URL: https://newsapi.org/docs
		
	*/
	
	CREATE TABLE tbl_ex_APIData
	(
		ID	BIGINT IDENTITY(1, 1),
		SourceID INT,
		Link Nvarchar(2000),
		[Title] Nvarchar(max),
		[Description] Nvarchar(max),
		[Content] Nvarchar(max),	
		[PublishedDate] DateTime,
		[UpdatedDate] DateTime,	
        [ParsedDate] DateTime,
        [Hash] nvarchar(80) PRIMARY KEY WITH (IGNORE_DUP_KEY = ON),
		FOREIGN KEY (SourceID) REFERENCES tbl_pj_SourceInfo(SourceID)
	)
	
	/*
		Created Date: 15 Feb 2019
		Description: Log every final cluster created by application		
	*/
	CREATE TABLE tbl_pj_ClusterLog
	(
		ID BIGINT IDENTITY (1, 1),
		ClusterID INT,
		BatchID INT,
		DocTitle NVARCHAR(max),
		IsCentroid BIT,		
		[Date] Date		
	)
	
	/*
		Created Date: 15 Feb 2019
		Description: Will hold only recent news item that is to be shown in the UI. 
					 This table should be truncated each time the application runs clustering and creates news items to display in the frontend
	*/
	CREATE TABLE tbl_ex_NewsItems
	(
		BatchID INT,
		DocumentTitle NVARCHAR(max),
		ClusterID INT,
		IsCentroid BIT	
	)
	
	
	
