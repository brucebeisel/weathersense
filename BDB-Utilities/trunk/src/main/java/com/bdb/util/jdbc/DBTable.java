/* 
 * Copyright (C) 2015 Bruce Beisel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.bdb.util.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class that provides operations for manipulating a database table. It is assumed the database table
 * has a class that represents the structure of the table. Packages, such as Hibernate, now do this type
 * of mapping, but this was written before that.
 * 
 * @author Bruce
 *
 * @param <T> The class that represents the data in the table
 */
public abstract class DBTable<T> {
    private final String         tableName;
    protected final DBConnection connection;
    private static final Logger     log = Logger.getLogger(DBTable.class.getName());
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Constructor.
     * 
     * @param tableName The name of the table
     * @param connection A connection to the database
     */
    public DBTable(String tableName, DBConnection connection) {
        this.tableName = tableName;
        this.connection = connection;
    }

    /**
     * A date formatter that can be used to build a date/time string in the proper format for SQL.
     * 
     * @return The date formatter
     */
    public static DateTimeFormatter dateTimeFormatter() {
        return dateTimeFormatter;
    }

    /**
     * A date formatter that can be used to build a date string in the proper format for SQL.
     * 
     * @return The date formatter
     */
    public static DateTimeFormatter dateFormatter() {
        return dateFormatter;
    }
    
    /**
     * Convenience method to close a statement and handle any exceptions.
     * 
     * @param stmt The statement to close
     */
    protected void closeStatement(Statement stmt) {
        try {
            if (stmt != null)
                stmt.close();
        }
        catch (SQLException e) {
            log.log(Level.SEVERE, "Caught SQL Exception", e);
        }
    }
    
    /**
     * A convenience method to close a result set and handle any exceptions.
     * 
     * @param rs The result set to close
     */
    protected void closeResultSet(ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
        }
        catch (SQLException e) {
            log.log(Level.SEVERE, "Caught SQL Exception", e);
        }
    }

    /**
     * Get the name of the table.
     * 
     * @return The table name
     */
    public final String getTableName() {
        return tableName;
    }

    /**
     * Get the connection being used by this table object.
     * 
     * @return The connection object
     */
    protected final DBConnection getConnection() {
        return connection;
    }

    /**
     * Checks if the table exists.
     * 
     * @return Whether the table exists
     */
    public final boolean exists() {
        try {
            //
            // Make sure we are connected
            //
            connection.connect();
            return connection.tableExists(tableName);
        }
        catch (SQLException e) {
            log.log(Level.SEVERE, "Caught SQL Exception", e);
            return false;
        }
    }

    /**
     * Create the table.
     * 
     * @return Whether the table was created
     */
    @Deprecated
    public boolean createTable() {
        throw new UnsupportedOperationException("createTable not supported for table " + tableName);
    }

    /**
     * Actually perform the create table and handle any exceptions.
     * 
     * @param sql The SQL needed to create the table
     * @return Whether the table was created
     */
    @Deprecated
    protected boolean executeCreateTable(String sql) {
        return connection.executeUpdate(sql) == 1;
    }

    /**
     * Drop the table from the database.
     * 
     * @return Whether the table was dropped
     */
    @Deprecated
    public final boolean dropTable() {
        boolean success = true;
        try {
            //
            // Make sure we are connected
            //
            connection.connect();

            String sql = "drop table " + tableName;

            connection.execute(sql);
        }
        catch (SQLException e) {
            log.log(Level.SEVERE, "Caught SQL Exception", e);
            success = false;
        }
        
        return success;
    }

    /**
     * Delete all of the rows in the table.
     * 
     * @return Whether the rows were deleted
     */
    public final boolean deleteAllRows() {
        boolean success = true;
        try {
            //
            // Make sure we are connected
            //
            connection.connect();

            String sql = "delete from " + tableName;

            connection.execute(sql);
        }
        catch (SQLException e) {
            log.log(Level.SEVERE, "Caught SQL Exception", e);
            success = false;
        }
        
        return success;
    }

    /**
     * Lock the table in the given mode.
     * 
     * @param mode The mode of the lock
     * 
     * @return Whether table was locked
     */
    public final boolean lockTable(String mode) {
        boolean success = true;
        
        try {
            String str = "LOCK TABLE" + " " + tableName + " " + "IN" + " " + mode + " " + "MODE";
            connection.connect();
            connection.execute(str);
        }
        catch (SQLException e) {
            log.log(Level.SEVERE, "Caught SQL Exception", e);
            success = false;
        }
        
        return success;
    }

    /**
     * Add a row to the table.
     * 
     * @param row The data to be added
     * @return Whether the row was added
     */
    public boolean addRow(T row) {
        throw new UnsupportedOperationException("addRow not support for table " + tableName);
    }
    
    /**
     * Convenience method for adding a row.
     * 
     * @param sql The add row SQL
     * @return Whether the row was added
     */
    protected boolean executeAddRow(String sql) {
        try (PreparedStatement stmt = connection.getConnection().prepareStatement(sql)) {
            return executeAddRow(stmt);
        }
        catch (SQLException e) {
            log.log(Level.SEVERE, "Caught SQL Exception", e);
            return false;
        }
 
    }

    /**
     * Convenience method for added a row.
     * 
     * @param stmt The prepared statement that will be executed
     * @return Whether the row was added
     */
    protected boolean executeAddRow(PreparedStatement stmt) {
        boolean success = true;
        try {
            if (stmt.executeUpdate() != 1)
                success = false;
        }
        catch (SQLException e) {
            log.log(Level.SEVERE, "Caught SQL Exception", e);
        }

        return success;
    }

    /**
     * Delete a row from the table.
     * 
     * @param row The record used to build the where clause for the delete SQL
     * @return Whether the row was deleted
     */
    public boolean deleteRow(T row) {
        throw new UnsupportedOperationException("deleteRow not supported on table " + tableName);
    }

    /**
     * Convenience method to delete a row.
     * 
     * @param sql The delete row SQL
     * @return Whether the row was deleted
     */
    protected int executeDeleteRows(String sql) {
        int count = 0;
        try (PreparedStatement stmt = connection.getConnection().prepareStatement(sql)) {
            count = stmt.executeUpdate();
        }
        catch (SQLException e) {
            log.log(Level.SEVERE, "Caught SQL Exception", e);
        }
        
        return count;
    }

    /**
     * Query the table for the data, appending the clause to the end of the query statement.
     * 
     * @param clause The clause added to the end of the query SQL, if clause is null all rows will be retrieved
     * @return The list of the queried records
     */
    public List<T> query(String clause) {
        throw new UnsupportedOperationException("query not supported on table " + tableName);
    }

    /**
     * Query the table for all data in the table
     * @return A list containing the records found during the query
     */
    public List<T> query() {
        return query(null);
    }
    
    /**
     * Convenience method for querying a table.
     * 
     * @param <T1> The type of records retrieved during the query. Note, this may be different from the table record (T) type
     * @param sql The SQL used to perform the query
     * @param p The object that will process each record retrieved by the query
     * @param args A set of arguments that are passed to the processor.
     * @return The list of the queried records
     */
    protected <T1> List<T1> executeQuery(String sql, QueryProcessor<T1> p, Object... args) {
        log.log(Level.FINE, "Execute Query: {0}", sql);
        List<T1> list = new ArrayList<>();

        try (Statement stmt = connection.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                T1 item = p.processNextRecord(rs, args);
                list.add(item);
            }
        }
        catch (SQLException e) {
            log.log(Level.INFO, "Caught SQL Exception", e);
        }
        
        log.log(Level.FINE, "{0} records match the query", list.size());

        return list;
    }

    /**
     * Update a row in the table.
     * 
     * @param row The values to be updated. Note that this method does not support changing the key values.
     * @return Whether the row was updated
     */
    public boolean updateRow(T row) {
        throw new UnsupportedOperationException("updateRow not supported by table " + tableName);
    }
}