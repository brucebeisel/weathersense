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
package com.bdb.weather.common.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.util.jdbc.DBTable;
import com.bdb.util.jdbc.DBConnection.NoDbConnectionException;
import com.bdb.util.measurement.Measurement;
import com.bdb.util.measurement.Unit;
import com.bdb.weather.common.Extreme;
import com.bdb.weather.common.RecordType;

//
// TODO: The way the Extreme type is used does not work well. You cannot roll back an extreme in a generic fashion because you
// have to know which type of extreme it is (temperature, rainfall).
//
public class MeasurementRecordTable<MEASUREMENT extends Measurement,RECORD extends RecordType,DBUNIT extends Unit> extends DBTable<Extreme<MEASUREMENT,RECORD>> {
    private static final String DATE_COLUMN = "date";
    private static final String RECORD_TYPE_COLUMN = "record_type";
    private static final String RECORD_VALUE_COLUMN = "value";
    private static final String PREVIOUS_DATE_COLUMN = "previous_date";
    private static final String PREVIOUS_RECORD_VALUE_COLUMN = "previous_value";
    
    private final String dbType;
    private static final Logger logger = Logger.getLogger(MeasurementRecordTable.class.getName());

    /**
     * Constructor.
     * 
     * @param connection The connection to the database
     * @param tableName The name of the table
     * @param dbType 
     */
    public MeasurementRecordTable(DBConnection connection, String tableName, String dbType) {
        super(tableName, connection);
        this.dbType = dbType;
    }
    
    private String buildClause(Extreme<MEASUREMENT, RECORD> extreme) {
        String sql = " where " +
                     DATE_COLUMN + "=\"" + DBTable.dateFormatter().format(extreme.getDate()) + "\" and " +
                     RECORD_TYPE_COLUMN + "=\"" + extreme.getType() + "\"";
        
        return sql;
    }
    
    private boolean recordExistsForDay(Extreme<MEASUREMENT, RECORD> extreme, DBUNIT unit) {
        return query(buildClause(extreme), extreme.getValue(), unit, extreme.getType()).size() == 1;
    }

    public boolean saveExtreme(Extreme<MEASUREMENT, RECORD> extreme, DBUNIT unit) {
        boolean success = false;
        try {
            if (!recordExistsForDay(extreme, unit))
                success = addRow(extreme, unit);
            else {
                String sql = "update " + getTableName() + " set " + RECORD_VALUE_COLUMN + "=? " + buildClause(extreme);
                PreparedStatement stmt = getConnection().getConnection().prepareStatement(sql);
                stmt.setDouble(1, extreme.getValue().get(unit));
                int affectedRecords = getConnection().executeUpdate(stmt);
                if (affectedRecords != 1) {
                    logger.severe("saveExtreme() did not update exactly 1 record. It updated " + affectedRecords + " records");
                    getConnection().rollback();
                }
            }
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Caught SQL Exception", e);
        }
        
        return success;
    }
    
    protected List<Extreme<MEASUREMENT, RECORD>> retrieveRecordsForDateRange(Measurement measurement, DBUNIT unit, RecordType recordType, LocalDate start, LocalDate end) throws NoDbConnectionException, SQLException {
        String clause = " where " +
                        DATE_COLUMN + ">=\"" + DBTable.dateFormatter().format(start) + "\" and " +
                        DATE_COLUMN + "<\"" + DBTable.dateFormatter().format(end) + "\"";
        
        return query(clause, measurement, unit, recordType);
    }
    
    @SuppressWarnings("unchecked")
    private List<Extreme<MEASUREMENT, RECORD>> query(String clause, Measurement measurement, DBUNIT unit, RecordType recordType) {
        String sql = "select * from " + getTableName();
        
        if (clause != null)
            sql += clause;
        
        List<Extreme<MEASUREMENT, RECORD>> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            MEASUREMENT measurement1 = (MEASUREMENT)args[0];
            DBUNIT unit1 = (DBUNIT)args[1];
            RecordType recordType1 = (RecordType)args[2];
            java.sql.Date date = rs.getDate(DATE_COLUMN);

            LocalDate currentDate = null;
            if (!rs.wasNull()) {
                currentDate = date.toLocalDate();
            }

            String recordTypeString = rs.getString(RECORD_TYPE_COLUMN);

            //
            // The previous date should never be null, but set a default just in case.
            //
            LocalDate previousDate = LocalDate.ofYearDay(1970, 1);
            date = rs.getDate(PREVIOUS_DATE_COLUMN);
            if (!rs.wasNull()) {
                previousDate = date.toLocalDate();
            }

            MEASUREMENT current = null;
            double value = rs.getDouble(RECORD_VALUE_COLUMN);
            if (!rs.wasNull())
                current = (MEASUREMENT)measurement1.newMeasurement(value, unit1);

            MEASUREMENT previous = null;
            
            double previousValue = rs.getDouble(PREVIOUS_RECORD_VALUE_COLUMN);

            if (!rs.wasNull())
                previous = (MEASUREMENT)measurement1.newMeasurement(previousValue, unit1);

            RECORD type = (RECORD)recordType1.fromString(recordTypeString);
            Extreme<MEASUREMENT, RECORD> extreme = new Extreme<>(currentDate, type, current, previousDate.getYear(), previous);
            return extreme;
        }, measurement, unit, recordType);
        
        return list;
    }

    private boolean addRow(Extreme<MEASUREMENT, RECORD> row, DBUNIT unit) {
        boolean success = false;
        try {
            getConnection().startTransaction();
            
            Measurement previousValue = row.getPreviousValue();
  
            String sql = "insert into " + getTableName() + " values (?,?,?,?,?)";
            PreparedStatement stmt = getConnection().getConnection().prepareStatement(sql);
            stmt.setDate(1, java.sql.Date.valueOf(row.getDate()));
            stmt.setString(2, row.getType().toString());
            stmt.setDouble(3, row.getValue().get(unit));
            
            if (previousValue != null) {
                stmt.setDouble(5, row.getPreviousValue().get(unit));
                stmt.setDate(4, java.sql.Date.valueOf(row.getPreviousDate()));
            }
            else {
                stmt.setNull(4, Types.DATE);
                stmt.setNull(5, Types.DOUBLE);
            }
            
            int affectedRecords = getConnection().executeUpdate(stmt);
            
            if (affectedRecords != 1) {
                logger.severe("addRow() did not add exactly 1 record. It added " + affectedRecords + " records");
                getConnection().rollback();
            }
            else {
                success = getConnection().endTransaction();
            }
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Caught SQLException", e);
            getConnection().rollback();
        }
        
        return success;
    }
    
    private void rollbackExtreme(List<Extreme<MEASUREMENT, RECORD>> list) {
//        Extreme<MEASUREMENT, RECORD> correctedExtreme = null;
//        if (list.get(0) == null && list.get(2) == null) {
//            // The record being deleted is the only one for that day so the
//            // record for the day is set to the previous date and value.
//            Extreme<MEASUREMENT, RECORD> rec = list.get(1);
//            correctedExtreme = new Extreme<MEASUREMENT, RECORD>(rec.getPreviousDate(), rec.getType(), rec.getPreviousValue(),
//                                                                rec.getPreviousDate().get(Calendar.YEAR), rec.getPreviousValue());
// 
//            
//        }
        
        // Update DailyRecordsTable
       // if (correctedExtreme != null) {
            //DailyRecordsTable dailyRecordsTable = new DailyRecordsTable(connection);
            //dailyRecordsTable.saveNewRecordMaximumHigh(correctedExtreme);
        //}
    }
    
    public boolean deleteExtreme(Extreme<MEASUREMENT, RECORD> record) throws SQLException{
        String sql = " where " + RECORD_TYPE_COLUMN + "=\"" + record.getType() + "\" and (" +
                                 DATE_COLUMN + "=\"" + DBTable.dateFormatter().format(record.getPreviousDate()) + "\" or " +
                                 DATE_COLUMN + "=\"" + DBTable.dateFormatter().format(record.getDate()) + "\" or " +
                                 PREVIOUS_DATE_COLUMN + "=\"" + DBTable.dateFormatter().format(record.getDate()) + "\")" +
                                 " order by " + DATE_COLUMN;
                                
        List<Extreme<MEASUREMENT, RECORD>> list = query(sql);
        
        //
        // No records that match, just return false
        //
        if (list.isEmpty())
            return false;
        
        //
        // More than 3 records were returned. That should not happen.
        //
        if (list.size() > 3) {
            logger.severe("deleteExtreme() query returned more than 3 records (" + list.size() + ")");
            return false;
        }
        
        //
        // If there is only one record then there is no previous or next record
        //
        if (list.size() == 1) {
            list.add(0, null);
            list.add(null);
        }
        else if (list.size() == 2) {
            //
            // There are only two records, there is either no record before or after the record being deleted.
            // Insert a null record where needed
            //
            if (list.get(0).getDate().equals(record.getDate()))
                list.add(0, null);
            else
                list.add(null);
        }
        
        //
        // Now that we are here the list has exactly 3 items. The first is the record before the given record, the
        // second is the given record and the third is the record after the given record.
        //
        rollbackExtreme(list);
        
        //
        // TODO Actually perform the delete
        //
        return true;
    }
}
