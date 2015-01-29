package com.bdb.util.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.BBRunnable;
import com.bdb.util.ThreadUtils;

public class DBConnection implements BBRunnable {
    /**
     * Exception that is thrown to indicate that there is no connection with the database server.
     */
    @SuppressWarnings("serial")
    public static class NoDbConnectionException extends SQLException {
        /**
         * Constructor.
         * 
         * @param what The exception text
         */
        public NoDbConnectionException(String what) {
            super(what);
        }
    }
    private static final long CHECK_CONNECTION_INTERVAL_MILLIS = 10000;

    private final String        url;
    private final String        user;
    private final String        passwd;
    private boolean             autoCommit;
    private Connection          connection;
    private boolean             terminate = false;
    private static final Logger logger = Logger.getLogger(DBConnection.class.getName());

    /**
     * Constructor
     * 
     * @param url The URL of the database driver
     * @param user The user to connect with
     * @param passwd The password of the user
     */
    public DBConnection(String url, String user, String passwd) {
        this(url, user, passwd, true);
    }

    /**
     * Constructor
     * 
     * @param url The URL of the database driver
     * @param user The user to connect with
     * @param passwd The password of the user
     * @param autoCommit Whether updates are automatically committed
     */
    public DBConnection(String url, String user, String passwd, boolean autoCommit) {
        logger.fine("Create new DBConnection");

        this.url = url;
        this.user = user;
        this.passwd = passwd;
        this.autoCommit = autoCommit;
    }

    /**
     * Connect to the database
     * 
     * @return Whether the connect succeeded
     */
    public boolean connect() {
        boolean success = false;
        try {
            if (connection != null && !connection.isClosed()) {
                //
                // Make a call to the database to see if the connection is really there.
                // The return value is ignore because we are only trying to reach the server,
                // and exception will be thrown if the server cannot be communicated with.
                //
                connection.isReadOnly();
            }
            else {
                logger.log(Level.FINE, "Connecting to DB. URL = ''{0}'' User = ''{1}''", new Object[]{url, user});
                connection = DriverManager.getConnection(url, user, passwd);
                connection.setAutoCommit(autoCommit);
            }
            
            success = true;
        }
        catch (SQLException e1) {
            logger.log(Level.SEVERE, "DBConnection.connect", e1);
        }
        
        return success;
    }

    /**
     * Close the connection to the database.
     */
    public void close() {
        logger.fine("Closing DB connection");

        try {
            connection.close();
            connection = null;
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "DBConnection.close", e);
        }
    }

    /**
     * Get the JDBC connection that this class is a facade for, in general this should not be used.
     * 
     * @return The JDBC connection object
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Get the table names that are in this database.
     * 
     * @return The list of table names
     * 
     * @throws NoDbConnectionException There is no connection with the database
     * @throws SQLException See JDBC documentation
     */
    public List<String> getTableNames() throws NoDbConnectionException, SQLException {
        //
        // Make sure we are connected
        //
        connect();

        //
        // We are only interested in tables
        //
        String ttypes[] = new String[1];
        ttypes[0] = "TABLE";

        DatabaseMetaData md = connection.getMetaData();

        logger.fine("Getting list of tables");

        ResultSet tables = md.getTables(null, null, null, ttypes);

        List<String> tableList = new ArrayList<>();

        while (tables.next())
            tableList.add(tables.getString(3));

        return tableList;

    }

    /**
     * Check of a table exists.
     * 
     * @param name The name of the table
     * @return Whether the table exists
     * 
     * @throws NoDbConnectionException There is no connection with the datbase
     * @throws SQLException An error occurred
     */
    public boolean tableExists(String name) throws NoDbConnectionException, SQLException {
        List<String> tables = getTableNames();

        return tables.contains(name);
    }

    /**
     * Execute an SQL statement.
     * 
     * @param sql The SQL to be executed
     * @throws NoDbConnectionException There is no connection with the database
     * @throws SQLException An error occurred
     */
    public void execute(String sql) throws NoDbConnectionException, SQLException {
        logger.log(Level.FINE, "Excute: {0}", sql);

        if (connection == null)
            throw new NoDbConnectionException("Cannot execute SQL statement " + sql + " because there is no connection to the database");
       
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    /**
     * Execute the SQL statement that created the prepared statement and close the prepared statement.
     * 
     * @param stmt The prepared statement to be executed
     * @return The number of rows affected by the update
     * @throws SQLException An error occurred
     */
    public int executeUpdate(PreparedStatement stmt) throws SQLException {
        logger.fine("Execute Update");

        int count = stmt.executeUpdate();
        stmt.close();

        return count;
    }

    /**
     * Execute an update with an SQL string.
     * 
     * @param sql The SQL to be executed
     * @return The number of rows affected by the update or -1 if the update failed
     */
    public int executeUpdate(String sql) {
        int count = -1;
        
        try {
            logger.log(Level.FINE, "Execute Update: {0}", sql);
            try (Statement stmt = connection.createStatement()) {
                count = stmt.executeUpdate(sql);
            }
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Caught SQL Exception", e);
        }
        
        return count;
    }

    /**
     * Start a transaction.
     * 
     * @return true if transaction is started
     */
    public boolean startTransaction() {
        boolean success = false;
        try {
            logger.fine("Starting transaction");
            connection.setAutoCommit(false);
            success = true;
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Caught SQL Exception", e);
        }
        
        return success;
    }

    /**
     * End a transaction, committed any changes to the database.
     * 
     * @return Whether the transaction ended successfully
     */
    public boolean endTransaction() {
        boolean success = false;
        try {
            success = commit();
            connection.setAutoCommit(true);
        }
        catch (SQLException e) {
            logger.severe("End transation failed. Transaction may still be open.");
        }
        
        return success;
    }

    /**
     * Commit the changes to the database.
     * 
     * @return Whether commit succeeded
     */
    public boolean commit() {
        boolean success = false;
        try {
            logger.fine("Commiting to DB");
            connection.commit();
            logger.fine("Commit successful");
            success = true;
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Caught SQL Exception", e);
        }
        
        return success;
    }

    /**
     * Roll back or cancel a transaction, any changes to the database that occurred without committing will be discarded.
     * 
     * @return Whether the rollback completed successfully
     */
    public boolean rollback() {
        boolean success = false;
        try {
            logger.fine("Rolling back changes to DB");
            connection.rollback();
            connection.setAutoCommit(true);
            success = true;
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Caught SQL Exception", e);
        }
        
        return success;
    }

   /**
    * Lock table(s).
    * 
    * @param tables The list of tables to lock
    * @param mode The locking mode
    * 
    * @throws NoDbConnectionException There is no connection with the database
    * @throws SQLException An error occurred
    */
    public void lockTables(List<String> tables, String mode) throws NoDbConnectionException, SQLException {
        if (tables == null)
            throw new IllegalArgumentException("List of table names may not be null");

        String cmdSql = "LOCK TABLE ";
        String modeSql = " IN " + mode + " MODE";
        String tblName;
        StringBuilder tblList = new StringBuilder();
        String sql;

        logger.log(Level.FINE, "Locking {0} tables", tables.size());

        for (int i = 0; i < tables.size(); i++) {
            tblName = tables.get(i);
            if (i != 0)
                tblList.append(",").append(tblName);
            else
                tblList.append(tblName);
        }

        sql = cmdSql + tblList.toString() + modeSql;

        execute(sql);
    }

    /**
     * Start a thread that will monitor the database connection and attempt to keep it connected.
     */
    @Override
    public void run() {
        logger.fine("Starting database connection thread");
        //
        // This Runnable entry point allows an application to try and keep the
        // connection active
        //
        while (!terminate) {
            ThreadUtils.sleep(CHECK_CONNECTION_INTERVAL_MILLIS);
            logger.fine("Checking database connection");
            connect();
        }
    }

    /**
     * Request that the connection monitor thread terminate.
     */
    @Override
    public void requestTermination() {
        terminate = true;
    }
}
