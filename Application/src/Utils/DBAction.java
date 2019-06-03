package Utils;

import java.sql.*;

/*
    <Created Date> Nov 1, 2018 </CreatedDate>
    <LastUpdated> Nov 2, 2018 </LastUpdated>
    <About>
        Establishes connection to the database based on the current values set in the ini file and returns a connection instance.
        Has methods that execute query based on static connection.
        NOTE: Connection is global and static. Is need be then for multiple instances this needs to be considered.
    </About>
*/

public class DBAction
{
    public static String ServerType = "sqlserver";  // This is sql in our case.

    public static Connection con = null;       //Set a static connection parameter. When connection is initialized is initialized then this connection is set.

    /// <summary>
    /// Initiate default sql connection.
    /// Hostname, username and password are as specified in the ini file.
    ///A jdbc jar should be included before we initiate the connection since this api establishes the connection to sql.
    ///</summary>
    public static Connection InitiateSqlConnection() throws SQLException
    {
        String FullHostName = GetCompleteHostName(ServerType, IniParser.Host, IniParser.Port, IniParser.DBName, IniParser.UserName, IniParser.Password);
        con = DriverManager.getConnection(FullHostName, IniParser.UserName, IniParser.Password);
        return con;
    }

    /// <summary>
    /// Initiate custom sql connection.
    ///A jdbc jar should be included before we initiate the connection since this api establishes the connection to sql.
    ///</summary>
    /// <param name="_ServerType">In our case this is sqlserver</param>
    /// <param name="_HostName">Host name</param>
    /// <param name="_Port">Sql server port. By default: 1433</param>
    /// <param name="_DBName">Control db name</param>
    /// <param name="_UserName">SQL connection UserName</param>
    /// <param name="_Password">SQL connection Password</param>
    public static Connection InitiateSqlConnection(String _serverType, String _HostName, int _Port, String _DBName, String _UserName, String _Password) throws SQLException
    {
        String FullHostName = GetCompleteHostName(_serverType, _HostName, _Port, _DBName, _UserName, _Password);
        con = DriverManager.getConnection(FullHostName, _UserName, _Password);
        return con;
    }

    /// <summary>
    /// Get complete hostname for sql connection. This is like a connection string (but worse).
    /// </summary>
    /// <param name="ServerType">In our case this is sqlserver</param>
    /// <param name="Host">Host name</param>
    /// <param name="DBPort">Sql server port. By default: 1433</param>
    /// <param name="DBName">Control db name</param>
    /// <param name="UserName">SQL connection UserName</param>
    /// <param name="Password">SQL connection Password</param>
    public static String GetCompleteHostName(String ServerType, String Host, int DBPort, String DBName, String UserName, String Password)
    {
        String HostName = "jdbc:" + ServerType + "://" + Host + ":" + DBPort + ";databaseName=" + DBName + ";user=" + UserName + ";password=" + Password;
        return HostName;
    }

    /// <summary>
    /// Execute sql query with no result set. Connection is established already.
    /// </summary>
    /// <param name="Query">SQL query to execute</param>
    public static void Execute(String Query)
    {
        try
        {
            Statement stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            stmt.execute(Query);
        }
        catch (SQLException exp)
        {
            String message = exp.getMessage();
            System.out.println(message);
        }
    }

    /// <summary>
    /// Execute sql query with result set. Connection is established already.
    /// </summary>
    /// <param name="Query">SQL query to execute</param>
    public static ResultSet ExecuteQuery(String Query)
    {
        ResultSet rs = null;
        try
        {
            Statement stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery(Query);
        }
        catch (SQLException exp)
        {
            String message = exp.getMessage();
            System.out.println(message);
        }
        return rs;
    }
}
