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

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.Extreme;
import com.bdb.weather.common.TemperatureRecordType;
import com.bdb.weather.common.measurement.Temperature;

/**
 * Class for accessing the temperature records.
 * 
 * @author Bruce
 *
 */
public class TemperatureRecordTable extends MeasurementRecordTable<Temperature,TemperatureRecordType,Temperature.Unit> {
    private static final String TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".temperature_records";
    private static final Logger logger = Logger.getLogger(TemperatureRecordTable.class.getName());
    
    /**
     * Constructor.
     * 
     * @param connection The connection to the database
     */
    public TemperatureRecordTable(DBConnection connection) {
        super(connection, TABLE_NAME, DatabaseConstants.TEMPERATURE_TYPE);
    }
    
    /**
     * Save the new temperature record.
     * 
     * @param extreme The temperature record
     */
    public void saveExtreme(Extreme<Temperature,TemperatureRecordType> extreme) {
        super.saveExtreme(extreme, DatabaseUnits.TEMPERATURE);
    }
    
    /**
     * Retrieve the temperature records that occurred over a time range.
     * 
     * @param start The start of the time range
     * @param end The end of the time range
     * @return The list of temperature records
     */
    public List<Extreme<Temperature,TemperatureRecordType>> retrieveRecordsForDateRange(LocalDate start, LocalDate end) {
        try {
            return retrieveRecordsForDateRange(new Temperature(0.0), DatabaseUnits.TEMPERATURE, TemperatureRecordType.MAX_HIGH, start, end);
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Caught SQLException", e);
            return new ArrayList<>();
        }
    }
}
