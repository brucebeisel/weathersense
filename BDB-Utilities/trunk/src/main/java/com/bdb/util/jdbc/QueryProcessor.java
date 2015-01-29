package com.bdb.util.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Interface to process records from a query.
 * 
 * @param <T> The record type of the query
 */
public interface QueryProcessor<T> {
    /**
     * Process the record to which the result set is pointing.
     * 
     * @param rs The results set
     * @param args A variable list of argument that can be passed to the processor
     * @return The object created that corresponds to the queried data
     * 
     * @throws SQLException An error occurred
     */
    public T processNextRecord(ResultSet rs, Object... args) throws SQLException;
}
