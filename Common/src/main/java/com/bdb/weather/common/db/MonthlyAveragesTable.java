/* 
 * Copyright (C) 2016 Bruce Beisel
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

import java.sql.ResultSet;
import java.time.Month;
import java.util.List;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.util.jdbc.DBTable;

import com.bdb.weather.common.MonthWeatherAverages;
import com.bdb.weather.common.WeatherAverage;
import com.bdb.weather.common.WeatherAverages;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Temperature;

/**
 * The class to access the table of month averages.
 * 
 * @author Bruce
 *
 */
public class MonthlyAveragesTable extends DBTable<WeatherAverage> {
    private static final String TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".monthly_seasonal_averages";
    private static final String MONTH_COLUMN = "month";
    private static final String HIGH_TEMP_COLUMN = "high_temperature";
    private static final String LOW_TEMP_COLUMN = "low_temperature";
    private static final String MEAN_TEMP_COLUMN = "mean_temperature";
    private static final String RAINFALL_COLUMN = "rainfall";

    /**
     * Constructor.
     * 
     * @param connection The connection to the database
     */
    public MonthlyAveragesTable(DBConnection connection) {
        super(TABLE_NAME, connection);
    }

     /**
     * Retrieve the averages for a single month.
     * 
     * @param locationCode The code for the location
     * @param month The month (January = 1)
     * @return The averages for the month or null on error
     */
    public WeatherAverage retrieveAveragesForMonth(String locationCode, int month) {
        List<WeatherAverage> record = query(" where " + MONTH_COLUMN + "=" + month);

        if (record.size() == 1)
            return record.get(0);
        else
            return null;
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#query(java.lang.String)
     */
    @Override
    public List<WeatherAverage> query(String clause) {
        String  sql = "select * from " + TABLE_NAME + " ";

        if (clause != null)
            sql = sql + clause;
        
        List<WeatherAverage> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            Temperature high = new Temperature(rs.getDouble(HIGH_TEMP_COLUMN), DatabaseUnits.TEMPERATURE);
            Temperature low = new Temperature(rs.getDouble(LOW_TEMP_COLUMN), DatabaseUnits.TEMPERATURE);
            Temperature mean = new Temperature(rs.getDouble(MEAN_TEMP_COLUMN), DatabaseUnits.TEMPERATURE);
            Depth rainfall = new Depth(rs.getDouble(RAINFALL_COLUMN), DatabaseUnits.DEPTH);
            
            WeatherAverage avgs = new WeatherAverage(Month.of(rs.getInt(MONTH_COLUMN)), high, low, mean, rainfall);
            
            return avgs;
        });
            
        return list;
    }

    /**
     * Retrieve the monthly averages for a location.
     * 
     * @param location The unique identifier of the location
     * @return The monthly averages or null on error
     */
    public WeatherAverages retrieveMonthlyAveragesForLocation(String location) {
        String clause = " order by " + MONTH_COLUMN;
        List<WeatherAverage> list = query(clause);
        if (list != null && !list.isEmpty()) {
            WeatherAverages avgs = new MonthWeatherAverages(list);
            return avgs;
        }
        else
            return null;
    }
}