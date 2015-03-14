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
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.util.jdbc.DBTable;

import com.bdb.weather.common.DayOfYearCollection;
import com.bdb.weather.common.DayWeatherAverages;
import com.bdb.weather.common.WeatherAverage;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Temperature;

/**
 * Class to access the daily averages table. This table holds the daily averages for temperature and rainfall for a geographic location.
 * It would not make sense to have separate averages for each weather station if they are down the street from each other.
 * 
 * @author Bruce
 *
 */
public class DailyAveragesTable extends DBTable<WeatherAverage> {
    private static final String TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".daily_seasonal_averages";
    private static final String MONTH_COLUMN = "month";
    private static final String DAY_COLUMN = "day";
    private static final String HIGH_TEMP_COLUMN = "high_temperature";
    private static final String LOW_TEMP_COLUMN = "low_temperature";
    private static final String MEAN_TEMP_COLUMN = "mean_temperature";
    private static final String RAINFALL_COLUMN = "rainfall";
    private static final Logger logger = Logger.getLogger(DailyAveragesTable.class.getName());
        //
        // FIXME The custom average spans over the year end, must use more complicated where clause
        //
    private static final String SELECT_SQL = "select avg(" + HIGH_TEMP_COLUMN + "), avg(" + LOW_TEMP_COLUMN + "), " +
    	                                     "avg(" + MEAN_TEMP_COLUMN + "), " +
    	                                     "avg(" + RAINFALL_COLUMN + ") " +
    	                                     "from " + TABLE_NAME;

    /**
     * Constructor.
     * 
     * @param connection The database connection
     */
    public DailyAveragesTable(DBConnection connection) {
        super(TABLE_NAME, connection);
    }

     /**
     * Retrieve the location averages for the specified day of the year.
     * 
     * @param locationCode The location code, this can be a zip code or any other unique identifier
     * @param month The month where January = 1
     * @param day The day of the month (1-31)
     * @return The averages or null if they do not exists or an error occurred
     */
    public WeatherAverage retrieveAveragesForDay(String locationCode, Month month, int day) {
        List<WeatherAverage> record = query(" where " + MONTH_COLUMN + "=" + month.getValue() + " and " + DAY_COLUMN + "=" + day);

        if (record.size() == 1)
            return record.get(0);
        else
            return null;
    }

    /**
     * Retrieve the location averages for the specified day of the year.
     * 
     * @param locationCode The location code, this can be a zip code or any other unique identifier
     * @param date A date object from which the month and day will be extracted
     * @return
     */
    public WeatherAverage retrieveAveragesForDay(String locationCode, LocalDate date) {
        return retrieveAveragesForDay(locationCode, date.getMonth(), date.getDayOfMonth());
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#query(java.lang.String)
     */
    @Override
    public List<WeatherAverage> query(String clause) {
        List<WeatherAverage> list;

        String sql = "select * from " + TABLE_NAME + " ";

        if (clause != null)
            sql = sql + clause;
        
        list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            Temperature high = new Temperature(rs.getDouble(HIGH_TEMP_COLUMN), DatabaseUnits.TEMPERATURE);
            Temperature low = new Temperature(rs.getDouble(LOW_TEMP_COLUMN), DatabaseUnits.TEMPERATURE);
            Temperature mean = new Temperature(rs.getDouble(MEAN_TEMP_COLUMN), DatabaseUnits.TEMPERATURE);
            Depth rainfall = new Depth(rs.getDouble(RAINFALL_COLUMN), DatabaseUnits.DEPTH);
            
            Month month = Month.of(rs.getInt(MONTH_COLUMN));
            WeatherAverage avgs = new WeatherAverage(month, rs.getInt(DAY_COLUMN), high, low, mean, rainfall);
            
            return avgs;
        });

        return list;
    }
    
    @Override
    public boolean addRow(WeatherAverage row) {
        boolean success;
        String sql = "insert into " + TABLE_NAME + " values (?,?,?,?,?,?)";

        try {
            PreparedStatement stmt = getConnection().getConnection().prepareStatement(sql);
            stmt.setInt(1, row.getMonth().getValue());
            stmt.setInt(2, row.getDay());
            stmt.setDouble(3, row.getLowTemperature().get(DatabaseUnits.TEMPERATURE));
            stmt.setDouble(4, row.getMeanTemperature().get(DatabaseUnits.TEMPERATURE));
            stmt.setDouble(5, row.getHighTemperature().get(DatabaseUnits.TEMPERATURE));
            stmt.setDouble(6, row.getRainfall().get(DatabaseUnits.DEPTH));
            success = getConnection().executeUpdate(stmt) == 1;
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Caught SQL Exception", e);
            success = false;
        }
        
        return success;
    }

    /**
     * Retrieve the averages for every day of the year for the given location.
     * 
     * @return The averages for every day of the year
     */
    public DayWeatherAverages retrieveDailyAverages() {
        DayOfYearCollection<WeatherAverage> collection = new DayOfYearCollection<>();

        List<WeatherAverage> list = query(null);
        
        if (list.isEmpty())
            return null;

        for (WeatherAverage avgs : list)
            collection.addItem(avgs.getMonth(), avgs.getDay(), avgs);

        return new DayWeatherAverages(collection);
    }

    /**
     * Update the averages for every day of the year for the given location.
     *  
     * @param dayAverages The new values
     * @return True if table was updated successfully 
     */
    public boolean updateDailyAverages(DayWeatherAverages dayAverages) {
        boolean success = true;
        try {
            if (getConnection().startTransaction()) {
                deleteAllRows();
                for (WeatherAverage avgs : dayAverages.getAllAverages())
                    success = success && addRow(avgs);
            }
        }
        finally {
            if (success)
                getConnection().endTransaction();
            else
                getConnection().rollback();
        }
        
        return success;
    }

    /**
     * Retrieve an average for a custom time range, this allows you to get the average rainfall for February 10 through March 21.
     * 
     * @param startMonth The start month of the custom average (January = 1)
     * @param startDay The start day of the custom average (1-31)
     * @param endMonth The start month of the custom average (January = 1)
     * @param endDay The start day of the custom average (1-31)
     * @return The custom average or null if averages for the location do not exist or an error occurred
     */
    public WeatherAverage retrieveCustomAverages(Month startMonth, int startDay, Month endMonth, int endDay) {
        if (startMonth == endMonth && endDay < startDay)
            throw new IllegalArgumentException("start month/day is after end start month/day");
        
        String sql = SELECT_SQL + " where (" + MONTH_COLUMN + "=" + startMonth.getValue() + " and " +
                     DAY_COLUMN + ">=" + startDay + ") or " +
                     "(" + MONTH_COLUMN + ">" + startMonth.getValue() + " and " +
                     MONTH_COLUMN + "<" + endMonth.getValue() + ") or " +
                     "(" + MONTH_COLUMN + "=" + endMonth.getValue() + " and " +
                     DAY_COLUMN + "<=" + endDay + ")";


        List<WeatherAverage> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            Month startMonth1 = (Month)args[0];
            int startDay1 = (Integer)args[1];
            Month endMonth1 = (Month)args[2];
            int endDay1 = (Integer)args[3];
            Temperature high = new Temperature(rs.getDouble(1), DatabaseUnits.TEMPERATURE);
            Temperature low = new Temperature(rs.getDouble(2), DatabaseUnits.TEMPERATURE);
            Temperature mean = new Temperature(rs.getDouble(3), DatabaseUnits.TEMPERATURE);
            Depth rainfall = new Depth(rs.getDouble(4), DatabaseUnits.DEPTH);
            WeatherAverage avgs = new WeatherAverage(startMonth1, startDay1, endMonth1, endDay1, high, low, mean, rainfall);
            return avgs;
        }, startMonth, startDay, endMonth, endDay);
        
        if (list.size() > 0)
            return list.get(0);
        else
            return null;
    }
}
