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

import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.Extreme;
import com.bdb.weather.common.RainfallRecordType;
import com.bdb.weather.common.measurement.Depth;

/**
 * Class to access the table of rainfall records.
 * 
 * @author Bruce
 *
 */
public class RainfallRecordTable extends MeasurementRecordTable<Depth,RainfallRecordType,Depth.Unit> {
    private static final String TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".rainfall_records";
    
    /**
     * Constructor.
     * 
     * @param connection The connection to the database
     */
    public RainfallRecordTable(DBConnection connection) {
        super(connection, TABLE_NAME, DatabaseConstants.DEPTH_TYPE);
    }
    
    /**
     * Save a rainfall record (i.e. an extreme measurement).
     * 
     * @param extreme The rainfall record
     * @return Whether the save was successful
     */
    public boolean saveExtreme(Extreme<Depth,RainfallRecordType> extreme) {
        return super.saveExtreme(extreme, DatabaseUnits.DEPTH);
    }
}
