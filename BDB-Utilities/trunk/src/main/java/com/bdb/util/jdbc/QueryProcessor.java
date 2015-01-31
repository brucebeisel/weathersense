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
