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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.util.jdbc.DBTable;

import com.bdb.weather.common.DateRange;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.MeasurementEntry;
import com.bdb.weather.common.SensorType;
import com.bdb.weather.common.Wind;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Heading;
import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.common.measurement.LeafWetness;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.SoilMoisture;
import com.bdb.weather.common.measurement.SolarRadiation;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;
import com.bdb.weather.common.measurement.UvIndex;

/**
 * The class to access the historical record table.
 * 
 * @author Bruce
 *
 */
public class HistoryTable extends DBTable<HistoricalRecord> {
    protected static final String	TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".history";
    private static final String DATE_COLUMN = "date";
    private static final String DURATION_COLUMN = "duration";
    private static final String LOW_OUTDOOR_TEMP_COLUMN = "low_outdoor_temperature";
    private static final String AVG_OUTDOOR_TEMP_COLUMN = "avg_outdoor_temperature";
    private static final String HIGH_OUTDOOR_TEMP_COLUMN = "high_outdoor_temperature";
    private static final String OUTDOOR_HUMIDITY_COLUMN = "outdoor_humidity";
    private static final String INDOOR_TEMP_COLUMN = "indoor_temperature";
    private static final String INDOOR_HUMIDITY_COLUMN = "indoor_humidity";
    private static final String AVG_WIND_SPEED_COLUMN = "avg_wind_speed";
    private static final String AVG_WIND_DIR_COLUMN = "prevailing_wind_direction";
    private static final String HIGH_WIND_SPEED_COLUMN = "high_wind_speed";
    private static final String HIGH_WIND_DIR_COLUMN = "high_wind_direction";
    private static final String WIND_GUST_COLUMN = "wind_gust";
    private static final String WIND_GUST_DIR_COLUMN = "wind_gust_direction";
    private static final String BARO_PRESSURE_COLUMN = "baro_pressure";
    private static final String RAINFALL_COLUMN = "rainfall";
    private static final String HIGH_RAINFALL_RATE_COLUMN = "high_rain_rate";
    private static final String AVG_UV_INDEX_COLUMN = "avg_uv_index";
    private static final String HIGH_UV_INDEX_COLUMN = "high_uv_index";
    private static final String AVG_SOLAR_RADIATION_COLUMN = "avg_solar_radiation";
    private static final String HIGH_SOLAR_RADIATION_COLUMN = "high_solar_radiation";
    private static final String EVAPOTRANSPIRATION_COLUMN = "evapotranspiration";
    private static final int NUM_COLUMNS = 22;
    
    protected static final String MEASUREMENT_HISTORY_TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".sensor_value_history";
    private static final String SENSOR_ID_COLUMN = "sensor_id";
    private static final String SENSOR_TYPE_COLUMN = "sensor_type";
    private static final String MEASUREMENT_COLUMN = "measurement";
    private static final String INSERT_SQL = "insert into " + TABLE_NAME + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String INSERT_MEASUREMENT_SQL = "insert into " + MEASUREMENT_HISTORY_TABLE_NAME + " values(?,?,?,?)";
    private static final String QUERY_SQL = "select * from " + TABLE_NAME + " left join " + MEASUREMENT_HISTORY_TABLE_NAME +
                                            " on " + TABLE_NAME + "." + DATE_COLUMN + "=" + MEASUREMENT_HISTORY_TABLE_NAME + "." + DATE_COLUMN + " ";
    
    private static final Logger logger = Logger.getLogger(HistoryTable.class.getName());

    /**
     * Constructor.
     * 
     * @param connection The connection with the database
     */
    public HistoryTable(DBConnection connection) {
        super(TABLE_NAME, connection);
    }

     /**
     * Convenience method to find the time of an extreme.
     * 
     * @param type The type of extreme, typically "min" or "max"
     * @return The time of the extreme record
     */
    private LocalDateTime findExtremeRecordTime(String type) {
        String sql = "select " + type + "(" + DATE_COLUMN + ") from " + TABLE_NAME;

        List<LocalDateTime> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            java.sql.Timestamp ts = rs.getTimestamp(1);
            LocalDateTime time = null;
            
            if (ts != null)
                time = ts.toLocalDateTime();
            
            return time;
        });

        if (list.size() > 0)
            return list.get(0);
        else
            return null;
    }
    
    /**
     * Find the time of newest record in the table.
     * 
     * @return The time of the newest record
     */
    public LocalDateTime findNewestRecordTime() {
        return findExtremeRecordTime("max");
    }

    /**
     * Find the time of the oldest record in the table.
     * 
     * @return The time of the oldest record
     */
    public LocalDateTime findOldestRecordTime() {
        return findExtremeRecordTime("min");
    }

    /**
     * Find the range of the data in the table.
     * 
     * @return The range of the data in the historical table
     */
    public DateRange dataRange() {
        String sql = "select min(" + DATE_COLUMN + "), max(" + DATE_COLUMN + ") from " + TABLE_NAME;
        
        List<DateRange> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            LocalDateTime start = null;
            LocalDateTime end = null;
            java.sql.Timestamp ts = rs.getTimestamp(1);
            
            if (ts != null)
                start = ts.toLocalDateTime();
            
            ts = rs.getTimestamp(2);
            
            if (ts != null)
                end = ts.toLocalDateTime();
            
            return new DateRange(start, end);
        });

        return list.get(0);
    }

    /**
     * Retrieve the time of the record in the database before the specified time.
     * 
     * @param time The time
     * @return The time of the record before the record of the specified time
     */
    public LocalDateTime timeOfRecordBefore(LocalDateTime time) {
        String sql = "select max(" + DATE_COLUMN + ") from " + TABLE_NAME +
                     " where " + DATE_COLUMN + "<'" + DBTable.dateTimeFormatter().format(time) + "'";

        List<LocalDateTime> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            LocalDateTime time1 = null;
            java.sql.Timestamp ts = rs.getTimestamp(1);
            if (ts != null)
                time1 = ts.toLocalDateTime();
            return time1;
        });

        if (list.size() > 0)
            return list.get(0);
        else
            return null;
    }

    /**
     * Retrieve the records for a day.
     * 
     * @param day The day for which to query
     * @return The list of records or null on error
     */
    public List<HistoricalRecord> queryRecordsForDay(LocalDate day) {
        LocalDateTime start = day.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        String dayString = DBTable.dateTimeFormatter().format(start);
        String nextDayString = DBTable.dateTimeFormatter().format(end);

        String clause = "where " + TABLE_NAME + "." + DATE_COLUMN + ">='" + dayString + "' and " + TABLE_NAME + "." + DATE_COLUMN + "<'" + nextDayString + "' order by " + TABLE_NAME + "." + DATE_COLUMN;

        return query(clause);
    }

    /**
     * Retrieve the records for a period of time.
     * 
     * @param start The start of the time period
     * @param end The end of the time period
     * @return The historical records for the time period or null on error
     */
    public List<HistoricalRecord> queryRecordsForTimePeriod(LocalDateTime start, LocalDateTime end) {
        String startString = DBTable.dateTimeFormatter().format(start);
        String endString = DBTable.dateTimeFormatter().format(end);
        String clause = "where " + TABLE_NAME + "." + DATE_COLUMN + ">='" + startString + "' and " + TABLE_NAME + "." + DATE_COLUMN + "<='" + endString + "' order by " + TABLE_NAME + "." + DATE_COLUMN;

        return query(clause);
    }

    /**
     * Retrieve the number of records for a day.
     * 
     * @param day The day
     * @return The number of records for the specified day
     */
    public int numRecordsForDay(LocalDate day) {
        LocalDateTime start = day.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        String startString = DBTable.dateTimeFormatter().format(start);

        String endString = DBTable.dateTimeFormatter().format(end);

        String sql = "select count(date) from " + TABLE_NAME +
                     " where " + DATE_COLUMN + ">='" + startString + "' and " + DATE_COLUMN + "<'" + endString + "'";

        List<Integer> list = executeQuery(sql, (ResultSet rs, Object... args) -> rs.getInt(1));

        if (list.size() > 0)
            return list.get(0);
        else
            return 0;
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#query(java.lang.String)
     */
    @Override
    public List<HistoricalRecord> query(String clause) {
        String	sql = QUERY_SQL;

        if (clause != null)
            sql = sql + clause;

        List<HistoricalRecord> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            double d;
            LocalDateTime time = rs.getTimestamp(DATE_COLUMN).toLocalDateTime();
            
            HistoricalRecord rec = new HistoricalRecord(time);
            
            rec.setDuration(Duration.ofSeconds(rs.getInt(DURATION_COLUMN)));
            
            double t = rs.getDouble(AVG_OUTDOOR_TEMP_COLUMN);
            if (!rs.wasNull())
                rec.setAvgOutdoorTemperature(new Temperature(t, DatabaseUnits.TEMPERATURE));
            
            t = rs.getDouble(HIGH_OUTDOOR_TEMP_COLUMN);
            if (!rs.wasNull())
                rec.setHighOutdoorTemperature(new Temperature(t, DatabaseUnits.TEMPERATURE));
            
            t = rs.getDouble(LOW_OUTDOOR_TEMP_COLUMN);
            if (!rs.wasNull())
                rec.setLowOutdoorTemperature(new Temperature(t, DatabaseUnits.TEMPERATURE));
            
            double h = rs.getDouble(OUTDOOR_HUMIDITY_COLUMN);
            if (!rs.wasNull())
                rec.setOutdoorHumidity(new Humidity(h));
            
            h = rs.getDouble(INDOOR_HUMIDITY_COLUMN);
            if (!rs.wasNull())
                rec.setIndoorHumidity(new Humidity(h));
            
            t = rs.getDouble(INDOOR_TEMP_COLUMN);
            if (!rs.wasNull())
                rec.setIndoorTemperature(new Temperature(t, DatabaseUnits.TEMPERATURE));
            
            double ws = rs.getDouble(AVG_WIND_SPEED_COLUMN);
            if (!rs.wasNull()) {
                double wd = rs.getDouble(AVG_WIND_DIR_COLUMN);
                
                if (!rs.wasNull())
                    rec.setAvgWind(new Wind(new Speed(ws, DatabaseUnits.SPEED), new Heading(wd)));
            }
            
            ws = rs.getDouble(HIGH_WIND_SPEED_COLUMN);
            if (!rs.wasNull()) {
                double wd = rs.getDouble(HIGH_WIND_DIR_COLUMN);
                
                if (!rs.wasNull())
                    rec.setHighWind(new Wind(new Speed(ws, DatabaseUnits.SPEED), new Heading(wd)));
            }
            
            ws = rs.getDouble(WIND_GUST_COLUMN);
            if (!rs.wasNull()) {
                double wd = rs.getDouble(WIND_GUST_DIR_COLUMN);
                
                if (!rs.wasNull())
                    rec.setWindGust(new Wind(new Speed(ws, DatabaseUnits.SPEED), new Heading(wd)));
            }
            
            d = rs.getDouble(BARO_PRESSURE_COLUMN);
            if (!rs.wasNull())
                rec.setBaroPressure(new Pressure(d, DatabaseUnits.PRESSURE));
            
            float fval = rs.getFloat(AVG_UV_INDEX_COLUMN);
            if (!rs.wasNull())
                rec.setAvgUvIndex(fval);
            
            int ival = rs.getInt(HIGH_UV_INDEX_COLUMN);
            if (!rs.wasNull())
                rec.setHighUvIndex(UvIndex.getUvIndex(ival));
            
            d = rs.getDouble(AVG_SOLAR_RADIATION_COLUMN);
            if (!rs.wasNull())
                rec.setAvgSolarRadiation(new SolarRadiation(d));
            
            d = rs.getDouble(HIGH_SOLAR_RADIATION_COLUMN);
            if (!rs.wasNull())
                rec.setHighSolarRadiation(new SolarRadiation(d));
            
            d = rs.getDouble(EVAPOTRANSPIRATION_COLUMN);
            if (!rs.wasNull())
                rec.setEvapotranspiration(new Depth(d, DatabaseUnits.DEPTH));
            
            d = rs.getDouble(RAINFALL_COLUMN);
            if (!rs.wasNull())
                rec.setRainfall(new Depth(d, DatabaseUnits.DEPTH));
            
            d = rs.getDouble(HIGH_RAINFALL_RATE_COLUMN);
            if (!rs.wasNull())
                rec.setHighRainfallRate(new Depth(d, DatabaseUnits.DEPTH));
                
            do {
                LocalDateTime nextRecordTime = rs.getTimestamp(DATE_COLUMN).toLocalDateTime();

                if (nextRecordTime.equals(time)) {
                    int sensorId = rs.getInt(SENSOR_ID_COLUMN);
                    if (!rs.wasNull()) {
                        SensorType sensorType = SensorType.fromCode(rs.getString(SENSOR_TYPE_COLUMN));
                        double measurementValue = rs.getDouble(MEASUREMENT_COLUMN);

                        switch (sensorType) {
                            case THERMOMETER:
                            case SOIL_TEMPERATURE:
                            case LEAF_TEMPERATURE:
                                rec.setTemperatureForSensor(sensorId, new Temperature(measurementValue, DatabaseUnits.TEMPERATURE));
                                break;

                            case HYGROMETER:
                                rec.setHumidityForSensor(sensorId, new Humidity(measurementValue));
                                break;

                            case LEAF_WETNESS:
                                rec.setLeafWetnessForSensor(sensorId, new LeafWetness((int)measurementValue));
                                break;

                            case SOIL_MOISTURE:
                                rec.setSoilMoistureForSensor(sensorId, new SoilMoisture(measurementValue));
                                break;
                        }
                    }
                }
                else {
                    rs.previous();
                    break;
                }
            } while (rs.next());

            return rec;
        });
        
        //list.stream().forEach((record) -> { retrieveMeasurements(record); });
        
        return list;
    }
    
    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#addRow(java.lang.Object)
     */
    @Override
    public boolean addRow(HistoricalRecord record) {
        List<HistoricalRecord> list = new ArrayList<>();
        list.add(record);
        return addHistoricalRecords(list);
    }

    public boolean addHistoricalRecords(List<HistoricalRecord> list) {
        boolean success = true;

        try {
            if (!getConnection().startTransaction())
                return false;

            try (PreparedStatement stmt = getConnection().getConnection().prepareStatement(INSERT_SQL);
                 PreparedStatement mstmt = getConnection().getConnection().prepareStatement(INSERT_MEASUREMENT_SQL)) {
                for (HistoricalRecord record : list) {
                    int n = 1;

                    stmt.setTimestamp(n++,  java.sql.Timestamp.valueOf(record.getTime()));
                    stmt.setLong(n++, record.getDuration().getSeconds());
                    
                    Temperature t = record.getLowOutdoorTemperature();
                    if (t != null)
                        stmt.setDouble(n++, t.get(DatabaseUnits.TEMPERATURE));
                    else
                        stmt.setNull(n++, Types.DOUBLE);
                    
                    t = record.getAvgOutdoorTemperature();
                    if (t != null)
                        stmt.setDouble(n++, t.get(DatabaseUnits.TEMPERATURE));
                    else
                        stmt.setNull(n++, Types.DOUBLE);
                    
                    t = record.getHighOutdoorTemperature();
                    if (t != null)
                        stmt.setDouble(n++, t.get(DatabaseUnits.TEMPERATURE));
                    else
                        stmt.setNull(n++, Types.DOUBLE);
                                           
                    Humidity h = record.getOutdoorHumidity();
                    if (h != null)
                        stmt.setDouble(n++, h.get());
                    else
                        stmt.setNull(n++, Types.DOUBLE);
                    
                    t = record.getIndoorTemperature();
                    if (t != null)
                        stmt.setDouble(n++, t.get(DatabaseUnits.TEMPERATURE));
                    else
                        stmt.setNull(n++, Types.DOUBLE);
                    
                    h = record.getIndoorHumidity();
                    if (h != null)
                        stmt.setDouble(n++, h.get());
                    else
                        stmt.setNull(n++, Types.DOUBLE);
                    
                    Wind w = record.getAvgWind();
                    if (w == null) {
                        stmt.setNull(n++, Types.DOUBLE);
                        stmt.setNull(n++, Types.FLOAT);
                    }
                    else {
                        stmt.setDouble(n++, w.getSpeed().get(DatabaseUnits.SPEED));
                        Heading direction = w.getDirection();
                        
                        if (direction != null)
                            stmt.setDouble(n++, direction.get());
                        else
                            stmt.setNull(n++, Types.FLOAT);
                    }
                    
                    w = record.getHighWind();
                    if (w == null) {
                        stmt.setNull(n++, Types.DOUBLE);
                        stmt.setNull(n++, Types.FLOAT);
                    }
                    else {
                        Speed s = w.getSpeed();
                        if (s != null)
                            stmt.setDouble(n++, s.get(DatabaseUnits.SPEED));
                        else
                            stmt.setNull(n++, Types.DOUBLE);
                        
                        Heading direction = w.getDirection();
                        if (direction != null)
                            stmt.setDouble(n++, direction.get());
                        else
                            stmt.setNull(n++, Types.FLOAT);
                    }
                    
                    w = record.getWindGust();
                    if (w == null) {
                        stmt.setNull(n++, Types.DOUBLE);
                        stmt.setNull(n++, Types.FLOAT);
                    }
                    else {
                        Speed s = w.getSpeed();
                        if (s != null)
                            stmt.setDouble(n++, s.get(DatabaseUnits.SPEED));
                        else
                            stmt.setNull(n++, Types.DOUBLE);

                        Heading direction = w.getDirection();
                        if (direction != null)
                            stmt.setDouble(n++, direction.get());
                        else
                            stmt.setNull(n++, Types.FLOAT);
                    }
                    
                    Pressure p = record.getBaroPressure();

                    if (p != null)
                        stmt.setDouble(n++, p.get(DatabaseUnits.PRESSURE));
                    else
                        stmt.setNull(n++, Types.DOUBLE);

                    Depth d = record.getRainfall();
                    if (d != null)
                        stmt.setDouble(n++, d.get(DatabaseUnits.DEPTH));
                    else
                        stmt.setNull(n++, Types.DOUBLE);
                    
                    d = record.getHighRainfallRate();
                    if (d != null)
                        stmt.setDouble(n++, d.get(DatabaseUnits.DEPTH));
                    else
                        stmt.setNull(n++, Types.DOUBLE);

                    Float fval = record.getAvgUvIndex();
                    if (fval != null)
                        stmt.setFloat(n++, fval);
                    else
                        stmt.setNull(n++, Types.FLOAT);
                    
                    UvIndex uvIndex = record.getHighUvIndex();
                    if (uvIndex != null)
                        stmt.setInt(n++, uvIndex.getIndex());
                    else
                        stmt.setNull(n++, Types.INTEGER);
                    
                    SolarRadiation sr = record.getAvgSolarRadiation();
                    if (sr != null)
                        stmt.setDouble(n++, sr.get());
                    else
                        stmt.setNull(n++, Types.DOUBLE);
                    
                    sr = record.getHighSolarRadiation();
                    if (sr != null)
                        stmt.setDouble(n++, sr.get());
                    else
                        stmt.setNull(n++, Types.DOUBLE);
                    
                    Depth dval = record.getEvapotranspiration();
                    if (dval != null)
                        stmt.setDouble(n++, dval.get(DatabaseUnits.DEPTH));
                    else

                        stmt.setNull(n++, Types.DOUBLE);
                    
                    assert n == NUM_COLUMNS + 1 : "Incorrect number of columns being set for current weather update";

                    stmt.addBatch();

                    for (MeasurementEntry entry : record.getMeasurementEntries()) {
                        n = 1;
                        mstmt.setTimestamp(n++,  java.sql.Timestamp.valueOf(record.getTime()));
                        mstmt.setInt(n++, entry.getSensorId());
                        mstmt.setString(n++, entry.getSensorType().getCode());
                        mstmt.setDouble(n++, entry.getMeasurement().get(DatabaseUnits.getDatabaseUnit(entry.getMeasurement())));
                        mstmt.addBatch();
                    }
                }
                int [] results = stmt.executeBatch();
                for (int result : results)
                    success = success && result == 1;

                results = mstmt.executeBatch();
                for (int result : results)
                    success = success && result == 1;
            }
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "HistoryTable.addRow(): ", e);
            success = false;
        }
        finally {
            if (success)
                success = getConnection().endTransaction();
            else
                getConnection().rollback();
        }

        return success;
    }

    /**
     * Retrieve historical records where there is rainfall.
     * 
     * @param start The time to start searching
     * @param end The time to end searching
     * 
     * @return The list of records that have non-zero rainfall
     * 
     * @throws IllegalArgumentException If the start date is the same or after the end date
     */
    public List<HistoricalRecord> retrieveRainRecords(LocalDateTime start, LocalDateTime end) throws IllegalArgumentException {
        String startString = DBTable.dateTimeFormatter().format(start);
        String endString = DBTable.dateTimeFormatter().format(end);

        if (start.compareTo(end) >= 0)
            throw new IllegalArgumentException("start time " + DBTable.dateTimeFormatter().format(start) +
                                               " must be less than end time " + DBTable.dateTimeFormatter().format(end));

        String clause = " where " + TABLE_NAME + "." + DATE_COLUMN + ">='" + startString + "' and " + TABLE_NAME + "." + DATE_COLUMN + "<='" + endString + "' and " +
                        RAINFALL_COLUMN + "> 0.0";

        return query(clause);

    }
    /**
     * Retrieve the total rain for a given period of time.
     * 
     * @param start The start of the time period
     * @param end The end of the time period
     * @return The rain total
     * @throws IllegalArgumentException  Thrown if start time is same or after end time
     */
    public Depth rainTotal(LocalDateTime start, LocalDateTime end) throws IllegalArgumentException {
        String startString = DBTable.dateTimeFormatter().format(start);
        String endString = DBTable.dateTimeFormatter().format(end);

        if (start.compareTo(end) >= 0)
            throw new IllegalArgumentException("start time " + DBTable.dateTimeFormatter().format(start) +
                                               " must be less than end time " + DBTable.dateTimeFormatter().format(end));

        String sql = "select sum(" + RAINFALL_COLUMN + ") from " + TABLE_NAME + " where " +
                     DATE_COLUMN + ">='" + startString + "' and " + DATE_COLUMN + "<='" + endString + "'";

        List<Depth> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            double total = rs.getDouble(1);
            Depth d = new Depth(total, DatabaseUnits.DEPTH);
            return d;
        });

        if (list.size() > 0)
            return list.get(0);
        else
            return new Depth(0.0);
    }
}