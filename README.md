# Summarizing daily news

**A short introduction**


The project will gather data from the specified data sources and then will perform feature extraction process on the token obtained from collected documents. 
The application will then carry out k-means clustering for the given data.

**The ini file:**
- Before starting please configure the ini file. The file contains details on SQL connection and clustering and other features

**The db scripts:**
Please run the scripts to create database and relevant tables before running the application

**Running the application**
-  Please run the scheduler.java. The application will collect data in the defined intervals. The default read interval is 24 hours but can be configured from ini file.
-  Conversely you can also run the "ClusterForSummary.jar" file inside the /TextClusteringForNewsSummary/Application/out/artifacts/ClusterForSummary_jar/ directory
