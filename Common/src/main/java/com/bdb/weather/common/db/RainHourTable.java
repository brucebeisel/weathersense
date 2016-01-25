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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.util.jdbc.DBTable;

import com.bdb.weather.common.DayHourRain;
import com.bdb.weather.common.RainRecord;
import com.bdb.weather.common.measurement.Depth;

public class RainHourTable extends DBTable<RainRecord> {
    private static final Logger logger = Logger.getLogger(RainHourTable.class.getName());
    protected static final String TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".rain_hour";
    private static final String DATE_COLUMN = "date";
    private static final String HOUR_COLUMN = "day_hour";
    private static final String RAINFALL_COLUMN = "rainfall";

    /**
     * Constructor.
     * 
     * @param connection The connection with the database
     */
    public RainHourTable(DBConnection connection) {
        super(TABLE_NAME, connection);
    }

    /**
     * Retrieve the hourly rain amounts.
     * 
     * @param date The day for which to retrieve the hourly rain amounts
     * @return The hourly rain for the specified day
     */
    public DayHourRain retrieveDailyValues(LocalDate date) {
        DayHourRain rain = new DayHourRain(date);

        LocalDate nextDay = date.plusDays(1);

        String dayString = DBTable.dateFormatter().format(date);

        String nextDayString = DBTable.dateFormatter().format(nextDay);

        String clause;
        clause = " where " + DATE_COLUMN + ">='" + dayString + "' and " + DATE_COLUMN + "<'" + nextDayString + "'"
               + " order by " + DATE_COLUMN;

        List<RainRecord> list = query(clause);

        list.stream().forEach((rec) -> { rain.putRain(rec.getHour(), rec.getRainfall()); });

        return rain;
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#query(java.lang.String)
     */
    @Override
    public List<RainRecord> query(String clause) {
        String sql = "select * from " + TABLE_NAME + " ";

        if (clause != null)
            sql = sql + clause;
        
        List<RainRecord> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            LocalDate date = rs.getDate(DATE_COLUMN).toLocalDate();
            int hour = rs.getInt(HOUR_COLUMN);
            double depth = rs.getDouble(RAINFALL_COLUMN);
            
            RainRecord rec = new RainRecord(date, hour, new Depth(depth, DatabaseUnits.DEPTH));
            
            return rec;
        });
        
        return list;
    }

    /**
     * Add the hourly rain amounts for a day.
     * 
     * @param rain The hourly rainfall amounts for a day
     * @return True if all values were added 
     */
    public boolean addHourlyValues(DayHourRain rain) {
        List<RainRecord> list = new ArrayList<>(24);
        rain.getHourValues().stream().forEach((hour) -> {
            Depth depth = rain.getRain(hour);
            if (depth != null) {
                RainRecord rr = new RainRecord(rain.getDate(), hour, depth);
                list.add(rr);
            }
        });

        return addRainRecords(list);
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#addRow(java.lang.Object)
     */
    @Override
    public boolean addRow(RainRecord record) {
        List<RainRecord> list = new ArrayList<>();
        list.add(record);
        return addRainRecords(list);
    }

    public boolean addRainRecords(List<RainRecord> list) {
        boolean success = true;
        try {
            String stmtString = "insert into " + TABLE_NAME + " values(?,?,?)";
            try (PreparedStatement stmt = getConnection().getConnection().prepareStatement(stmtString)) {
                for (RainRecord record : list) {
                    LocalDate date = record.getDate();
                    int hour = record.getHour();
                    stmt.setDate(1, java.sql.Date.valueOf(date));
                    stmt.setInt(2, hour);
                    stmt.setDouble(3, record.getRainfall().get(DatabaseUnits.DEPTH));
                    stmt.addBatch();
                }
                int [] results = stmt.executeBatch();
                for (int result : results)
                    success = success && result == 1;
            }

            return success;
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Caught SQL Exception", e);
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#updateRow(java.lang.Object)
     */
    @Override
    public boolean updateRow(RainRecord row) {
        return addRow(row);
    }
}