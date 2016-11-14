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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.util.jdbc.DBTable;

import com.bdb.weather.common.SpeedBinDuration;
import com.bdb.weather.common.WindRoseData;
import com.bdb.weather.common.WindSlice;
import com.bdb.weather.common.measurement.Speed;

/**
 * The wind slice table is really a join between it and the wind speed duration table. The two table together
 * are used to populate the wind rose data object.
 * 
 * @author Bruce
 *
 */
public class WindSliceTable extends DBTable<WindSlice> {
    /**
     * The name of the wind slice table
     */
    protected static final String TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".wind_slices";
    /**
     * The name of the date column
     */
    protected static final String DATE_COLUMN = "date";
    /**
     * The name of the wind heading index column
     */
    protected static final String HEADING_COLUMN = "wind_heading_index";
    private static final String   SLICE_DURATION_COLUMN = "slice_duration";
    private static final String   TOTAL_WINDY_DURATION_COLUMN = "windy_duration";
    private static final String   TOTAL_DURATION_COLUMN = "total_duration";
    private static final String   AVG_SPEED_COLUMN = "avg_speed";
    private static final String   MAX_SPEED_COLUMN = "max_speed";
    private static final String   WINDY_PERCENTAGE_COLUMN = "windy_percentage";
    private static final String   TOTAL_PERCENTAGE_COLUMN = "total_percentage";
    private static final String   INSERT_WIND_SLICE_SQL = "insert into " + TABLE_NAME + " values(?,?,?,?,?,?,?,?,?)";

    /**
     * The name of the wind speed bin duration table
     */
    protected static final String BIN_TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".wind_speed_bin_durations";
    private static final String   BIN_DATE_COLUMN = "date";
    private static final String   BIN_WIND_HEADING_INDEX_COLUMN = "wind_heading_index";
    private static final String   BIN_WIND_SPEED_BIN_INDEX_COLUMN = "wind_speed_bin_index";
    private static final String   BIN_DURATION_COLUMN = "duration";
    private static final String   INSERT_BIN_SQL = "insert into " + BIN_TABLE_NAME + " values(?,?,?,?)";
    private static final Logger   logger = Logger.getLogger(WindSliceTable.class.getName());

    /**
     * Constructor.
     * 
     * @param connection The connection with the database
     */
    public WindSliceTable(DBConnection connection) {
        super(TABLE_NAME, connection);
    }

    /**
     * Add the wind slice data from the wind rose data.
     * 
     * @param record The wind rose data
     * TODO This is also strange, need to investigate how the wind rose data could be better organized
     * from a coding point of view.
     * @return True if all of the data was added successfully 
     */
    public boolean addDailyValues(WindRoseData record) {
        int numSlices = record.getNumSlices();

        List<WindSlice> list = new ArrayList<>(numSlices);

        for (int i = 0; i < numSlices; i++) {
            WindSlice slice = record.getSlice(i);
            list.add(slice);
        }

	return addWindSlices(list);
    }

    /**
     * Retrieve the wind slice data and the speed bin durations for the weather station and date contained
     * in the provided wind rose data object.
     * 
     * @param data The wind rose data
     * TODO Using the wind rose data as an argument seems strange here. Why do we not take a station ID and
     * time, then return a WindRoseData?
     */
    public void retrieveDailyValues(WindRoseData data) {
        //
        // All times in the wind slice table must be midnight
        //
        LocalDate date = data.getTime();

        logger.finer("Retrieving daily values");
        String sql = "select weathersense24.wind_slices.date," +
                     "weathersense24.wind_slices.wind_heading_index," +
                     "slice_duration,windy_duration,total_duration," +
                     "avg_speed,max_speed,windy_percentage,total_percentage,duration,wind_speed_bin_index from " + TABLE_NAME +
                     " inner join weathersense24.wind_speed_bin_durations " +
                     " on weathersense24.wind_slices.date=weathersense24.wind_speed_bin_durations.date" +
                     " and weathersense24.wind_slices.wind_heading_index=weathersense24.wind_speed_bin_durations.wind_heading_index" +
                     " where weathersense24.wind_slices.date='" + DBTable.dateFormatter().format(date) + "'" +
                     " order by " + HEADING_COLUMN;

        executeQuery(sql, (ResultSet rs, Object... args) -> {
            WindRoseData data1 = (WindRoseData)args[0];
            for (int i = 0; i < data1.getNumSlices(); i++) {
                WindSlice slice = data1.getSlice(i);
                if (slice.getHeadingIndex() == rs.getInt(HEADING_COLUMN)) {
                    slice.setSliceDuration(Duration.ofSeconds(rs.getInt(SLICE_DURATION_COLUMN)));
                    slice.setWindyDuration(Duration.ofSeconds(rs.getInt(TOTAL_WINDY_DURATION_COLUMN)));
                    slice.setTotalDuration(Duration.ofSeconds(rs.getInt(TOTAL_DURATION_COLUMN)));
                    slice.setAvgSpeed(slice.getSliceDuration(), new Speed(rs.getDouble(AVG_SPEED_COLUMN), DatabaseUnits.SPEED));
                    slice.setMaxSpeed(new Speed(rs.getDouble(MAX_SPEED_COLUMN), DatabaseUnits.SPEED));
                    slice.setPercentageOfWind(rs.getFloat(WINDY_PERCENTAGE_COLUMN));
                    slice.setPercentageOfTotal(rs.getFloat(TOTAL_PERCENTAGE_COLUMN));
                    data1.setCalmDuration(slice.getTotalDuration().minus(slice.getWindyDuration()));
                    data1.setTotalDuration(slice.getTotalDuration());
                    int binDuration = rs.getInt(BIN_DURATION_COLUMN);
                    int windSpeedBinIndex = rs.getInt(BIN_WIND_SPEED_BIN_INDEX_COLUMN);
                    SpeedBinDuration speedBinDuration = new SpeedBinDuration(windSpeedBinIndex, slice.getHeadingIndex(), data1.getTime(), Duration.ofSeconds(binDuration));
                    slice.addBinDuration(speedBinDuration);
                }
            }
            return null;
        }, data);
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#addRow(java.lang.Object)
     */
    @Override
    public boolean addRow(WindSlice slice) {
        List<WindSlice> list = new ArrayList<>();
        list.add(slice);
        return addWindSlices(list);
    }

    /**
     * Add the wind slices to the database.
     * 
     * @param list The list of wind slices to add to the database
     * @return True of all of the wind slices were added to the database
     */
    public boolean addWindSlices(List<WindSlice> list) {
        try {
	    try (PreparedStatement stmt = getConnection().getConnection().prepareStatement(INSERT_WIND_SLICE_SQL);
                 PreparedStatement binStmt = getConnection().getConnection().prepareStatement(INSERT_BIN_SQL)) {
                for (WindSlice slice : list) {
                    LocalDate date = slice.getDate();
                    int n = 1;

                    stmt.setDate(n++,  java.sql.Date.valueOf(date));
                    stmt.setInt(n++, slice.getHeadingIndex());
                    stmt.setLong(n++, slice.getSliceDuration().getSeconds());
                    stmt.setLong(n++, slice.getWindyDuration().getSeconds());
                    stmt.setLong(n++, slice.getTotalDuration().getSeconds());
                    stmt.setDouble(n++, slice.getAvgSpeed().get(DatabaseUnits.SPEED));
                    stmt.setDouble(n++, slice.getMaxSpeed().get(DatabaseUnits.SPEED));
                    stmt.setFloat(n++, slice.getPercentageOfWind());
                    stmt.setFloat(n++, slice.getPercentageOfTotal());
                    stmt.addBatch();

                    for (SpeedBinDuration binDuration : slice.getBinDurations()) {
                        n = 1;

                        binStmt.setDate(n++,  java.sql.Date.valueOf(binDuration.getDate()));
                        binStmt.setInt(n++, binDuration.getHeadingIndex());
                        binStmt.setInt(n++, binDuration.getWindSpeedBinIndex());
                        binStmt.setLong(n++, binDuration.getDuration().getSeconds());
                        binStmt.addBatch();
                    }
                }

		int [] results = stmt.executeBatch();
                boolean success = true;
                for (int result : results)
                    success = success && result == 1;

                if (success) {
                    results = binStmt.executeBatch();
                    for (int result : results)
                        success = success && result == 1;
                }

                return success;
	    }
        }
        catch (SQLException e) {
	    logger.log(Level.SEVERE, "SQL Exception", e);
            return false;
        }
    }
}