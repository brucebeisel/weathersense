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
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.util.jdbc.DBTable;
import com.bdb.util.measurement.Measurement;
import com.bdb.util.measurement.MeasurementStatistics;
import com.bdb.util.measurement.Unit;

import com.bdb.weather.common.DayHourRain;
import com.bdb.weather.common.MeasurementStatisticsEntry;
import com.bdb.weather.common.SensorType;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.TemperatureBinDuration;
import com.bdb.weather.common.TemperatureBinMgr;
import com.bdb.weather.common.WindParameters;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.common.measurement.LeafWetness;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.SoilMoisture;
import com.bdb.weather.common.measurement.SolarRadiation;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;

/**
 * Class to access the daily summary table.
 * 
 * @author Bruce
 *
 */
public class DailySummaryTable extends DBTable<SummaryRecord> {
    private final WindSliceTable              windSliceTable;
    private final RainHourTable               rainHourTable;
    private final TemperatureBinDurationTable temperatureBinDurationTable;
    private static final Logger         logger = Logger.getLogger(DailySummaryTable.class.getName());;

    /**
     * Name of the daily summary table
     */
    protected static final String TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".daily_summary";
    static final String         DATE_COLUMN = "date";
    private static final String TOTAL_DURATION_COLUMN = "total_duration";
    private static final String OUTDOOR_TEMP_DURATION_COLUMN = "outdoor_temp_duration";
    private static final String HIGH_OUTDOOR_TEMP_COLUMN = "high_outdoor_temp";
    private static final String HIGH_OUTDOOR_TEMP_TIME_COLUMN = "high_outdoor_temp_time";
    private static final String LOW_OUTDOOR_TEMP_COLUMN = "low_outdoor_temp";
    private static final String LOW_OUTDOOR_TEMP_TIME_COLUMN = "low_outdoor_temp_time";
    private static final String AVG_OUTDOOR_TEMP_COLUMN = "avg_outdoor_temp";
    private static final String HIGH_HEAT_INDEX_COLUMN = "high_heat_index";
    private static final String HIGH_HEAT_INDEX_TIME_COLUMN = "high_heat_index_time";
    private static final String LOW_WIND_CHILL_COLUMN = "low_wind_chill";
    private static final String LOW_WIND_CHILL_TIME_COLUMN = "low_wind_chill_time";
    private static final String LOW_DEW_POINT_COLUMN = "low_dew_point";
    private static final String LOW_DEW_POINT_TIME_COLUMN = "low_dew_point_time";
    private static final String HIGH_DEW_POINT_COLUMN = "high_dew_point";
    private static final String HIGH_DEW_POINT_TIME_COLUMN = "high_dew_point_time";
    private static final String INDOOR_TEMP_DURATION_COLUMN = "indoor_temp_duration";
    private static final String HIGH_INDOOR_TEMP_COLUMN = "high_indoor_temp";
    private static final String HIGH_INDOOR_TEMP_TIME_COLUMN = "high_indoor_temp_time";
    private static final String LOW_INDOOR_TEMP_COLUMN = "low_indoor_temp";
    private static final String LOW_INDOOR_TEMP_TIME_COLUMN = "low_indoor_temp_time";
    private static final String AVG_INDOOR_TEMP_COLUMN = "avg_indoor_temp";
    private static final String OUTDOOR_HUMID_DURATION_COLUMN = "outdoor_humid_duration";
    private static final String HIGH_OUTDOOR_HUMID_COLUMN = "high_outdoor_humid";
    private static final String HIGH_OUTDOOR_HUMID_TIME_COLUMN = "high_outdoor_humid_time";
    private static final String LOW_OUTDOOR_HUMID_COLUMN = "low_outdoor_humid";
    private static final String LOW_OUTDOOR_HUMID_TIME_COLUMN = "low_outdoor_humid_time";
    private static final String AVG_OUTDOOR_HUMID_COLUMN = "avg_outdoor_humid";
    private static final String INDOOR_HUMID_DURATION_COLUMN = "indoor_humid_duration";
    private static final String HIGH_INDOOR_HUMID_COLUMN = "high_indoor_humid";
    private static final String HIGH_INDOOR_HUMID_TIME_COLUMN = "high_indoor_humid_time";
    private static final String LOW_INDOOR_HUMID_COLUMN = "low_indoor_humid";
    private static final String LOW_INDOOR_HUMID_TIME_COLUMN = "low_indoor_humid_time";
    private static final String AVG_INDOOR_HUMID_COLUMN = "avg_indoor_humid";
    private static final String PRESSURE_DURATION_COLUMN = "baro_pressure_duration";
    private static final String HIGH_BARO_PRESSURE_COLUMN = "high_baro_pressure";
    private static final String HIGH_PRESSURE_TIME_COLUMN = "high_baro_pressure_time";
    private static final String LOW_BARO_PRESSURE_COLUMN = "low_baro_pressure";
    private static final String LOW_PRESSURE_TIME_COLUMN = "low_baro_pressure_time";
    private static final String AVG_BARO_PRESSURE_COLUMN = "avg_baro_pressure";
    private static final String WIND_DURATION_COLUMN = "wind_duration";
    private static final String MAX_WIND_SPEED_COLUMN = "max_wind_speed";
    private static final String MAX_WIND_SPEED_TIME_COLUMN = "max_wind_speed_time";
    private static final String MAX_WIND_GUST_COLUMN = "max_wind_gust";
    private static final String MAX_WIND_GUST_TIME_COLUMN = "max_wind_gust_time";
    private static final String AVG_WIND_SPEED_COLUMN = "avg_wind_speed";
    private static final String RAINFALL_COLUMN = "rainfall";
    private static final String MAX_RAINFALL_RATE_COLUMN = "max_rainfall_rate";
    private static final String MAX_RAINFALL_RATE_TIME_COLUMN = "max_rainfall_rate_time";
    private static final String AVG_SOLAR_RADIATION_COLUMN = "avg_solar_radiation";
    private static final String SOLAR_RADIATION_DURATION_COLUMN = "solar_radiation_duration";
    private static final String MAX_SOLAR_RADIATION_COLUMN = "max_solar_radiation";
    private static final String MAX_SOLAR_RADIATION_TIME_COLUMN = "max_solar_radiation_time";
    private static final String TOTAL_EVAPOTRANSPIRATION_COLUMN = "total_evapotranspiration";
    private static final String AVG_UV_INDEX_COLUMN = "avg_uv_index";
    private static final String UV_DURATION_COLUMN = "uv_duration";
    private static final String MAX_UV_INDEX_COLUMN = "max_uv_index";
    private static final String MAX_UV_INDEX_TIME_COLUMN = "max_uv_index_time";

    /**
     * Name of the sensor value summary table
     */
    protected static final String SENSOR_SUMMARY_TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".sensor_value_summary";
    private static final String SENSOR_DATE_COLUMN = "date";
    private static final String SENSOR_ID_COLUMN = "sensor_id";
    private static final String SENSOR_TYPE_COLUMN = "sensor_type";
    private static final String SENSOR_HIGH_MEASUREMENT_COLUMN = "high_measurement";
    private static final String SENSOR_HIGH_MEASUREMENT_TIME_COLUMN = "high_measurement_time";
    private static final String SENSOR_LOW_MEASUREMENT_COLUMN = "low_measurement";
    private static final String SENSOR_LOW_MEASUREMENT_TIME_COLUMN = "low_measurement_time";
    private static final String SENSOR_DURATION_COLUMN = "duration";
    private static final String SENSOR_AVG_MEASUREMENT_COLUMN = "avg_measurement";

    private static final String INSERT_SQL =
		"insert into " + TABLE_NAME + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String UPDATE_SQL = "update " + TABLE_NAME + " set " +
        			    TOTAL_DURATION_COLUMN + "=?," +
                                    OUTDOOR_TEMP_DURATION_COLUMN + "=?," +
				    HIGH_OUTDOOR_TEMP_COLUMN + "=?, " +
                                    HIGH_OUTDOOR_TEMP_TIME_COLUMN + "=?, " +
                                    LOW_OUTDOOR_TEMP_COLUMN + "=?, " +
                                    LOW_OUTDOOR_TEMP_TIME_COLUMN + "=?, " +
                                    AVG_OUTDOOR_TEMP_COLUMN + "=?, " +
				    HIGH_HEAT_INDEX_COLUMN + "?, " +
				    HIGH_HEAT_INDEX_TIME_COLUMN + "?, " +
				    LOW_WIND_CHILL_COLUMN + "?, " +
				    LOW_WIND_CHILL_TIME_COLUMN + "?, " +
				    LOW_DEW_POINT_COLUMN + "?, " +
				    LOW_DEW_POINT_TIME_COLUMN + "?, " +
				    HIGH_DEW_POINT_COLUMN + "?, " +
				    HIGH_DEW_POINT_TIME_COLUMN + "?, " +
                                    INDOOR_TEMP_DURATION_COLUMN + "=?, " +
                                    HIGH_INDOOR_TEMP_COLUMN + "=?, " +
                                    HIGH_INDOOR_TEMP_TIME_COLUMN + "=?, " +
                                    LOW_INDOOR_TEMP_COLUMN + "=?, " +
                                    LOW_INDOOR_TEMP_TIME_COLUMN + "=?, " +
                                    AVG_INDOOR_TEMP_COLUMN + "=?," +
                                    OUTDOOR_HUMID_DURATION_COLUMN + "=?, " +
                                    HIGH_OUTDOOR_HUMID_COLUMN + "=?, " +
                                    HIGH_OUTDOOR_HUMID_TIME_COLUMN + "=?, " +
                                    LOW_OUTDOOR_HUMID_COLUMN + "=?, " +
                                    LOW_OUTDOOR_HUMID_TIME_COLUMN + "=?, " +
                                    AVG_OUTDOOR_HUMID_COLUMN + "=?," +
                                    INDOOR_HUMID_DURATION_COLUMN + "=?, " +
                                    HIGH_INDOOR_HUMID_COLUMN + "=?, " +
                                    HIGH_INDOOR_HUMID_TIME_COLUMN + "=?, " +
                                    LOW_INDOOR_HUMID_COLUMN + "=?, " +
                                    LOW_INDOOR_HUMID_TIME_COLUMN + "=?, " +
                                    AVG_INDOOR_HUMID_COLUMN + "=?," +
                                    PRESSURE_DURATION_COLUMN + "=?, " +
                                    HIGH_BARO_PRESSURE_COLUMN + "=?, " +
                                    HIGH_PRESSURE_TIME_COLUMN + "=?, " +
                                    LOW_BARO_PRESSURE_COLUMN + "=?, " +
                                    LOW_PRESSURE_TIME_COLUMN + "=?, " +
                                    AVG_BARO_PRESSURE_COLUMN + "=?," +
                                    WIND_DURATION_COLUMN + "=?, " +
                                    MAX_WIND_SPEED_COLUMN + "=?," +
                                    MAX_WIND_SPEED_TIME_COLUMN + "=?," +
                                    MAX_WIND_GUST_COLUMN + "=?," +
                                    MAX_WIND_GUST_TIME_COLUMN + "=?," +
                                    AVG_WIND_SPEED_COLUMN + "=?," +
                                    RAINFALL_COLUMN + "=?," +
                                    MAX_RAINFALL_RATE_COLUMN + "=?," +
                                    MAX_RAINFALL_RATE_TIME_COLUMN + "=?," +
                                    AVG_SOLAR_RADIATION_COLUMN + "=?," +
                                    SOLAR_RADIATION_DURATION_COLUMN + "=?," +
                                    MAX_SOLAR_RADIATION_COLUMN + "=?," +
                                    MAX_SOLAR_RADIATION_TIME_COLUMN + "=?," +
                                    TOTAL_EVAPOTRANSPIRATION_COLUMN + "=?" +
                                    " where " + DATE_COLUMN + "='?'";

    private static final String INSERT_SENSOR_SQL = "insert into " + SENSOR_SUMMARY_TABLE_NAME + " values(?,?,?,?,?,?,?,?,?)";

    /**
     * Constructor.
     * 
     * @param connection Connection to the database
     */
    public DailySummaryTable(DBConnection connection) {
        super(TABLE_NAME, connection);

        windSliceTable = new WindSliceTable(connection);
        rainHourTable = new RainHourTable(connection);
        temperatureBinDurationTable = new TemperatureBinDurationTable(connection);
    }

     /**
     * Find the newest record in the table for a weather station.
     * 
     * @return The newest record in the data or null if there are no records
     */
    public LocalDate findNewestRecord() {
        String sql = "select max(" + DATE_COLUMN + ") from " + TABLE_NAME;

        List<LocalDate> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            LocalDate date = null;
            
            java.sql.Date ts = rs.getDate(1);
            
            if (ts != null)
                date = ts.toLocalDate();
            
            return date;
        });

        return list.get(0);
    }

    /**
     * Retrieve the summary record for today.
     * 
     * @param windParameters The winds parameters used to create the wind rose data
     * @param temperatureBinMgr The temperature bin manager used in the creation of the temperature bin summaries
     * @return Today's summary record or null if it does not exist or an error occurred
     */
    public SummaryRecord retrieveTodaysSummary(WindParameters windParameters, TemperatureBinMgr temperatureBinMgr) {
        return retrieveSummaryForDate(LocalDate.now(), windParameters, temperatureBinMgr);
    }

    /**
     * Retrieve the summary record for specified date.
     * 
     * @param date The date whose summary will be retrieved
     * @param windParameters The winds parameters used to create the wind rose data
     * @param temperatureBinMgr The temperature bin manager used in the creation of the temperature bin summaries
     * @return The summary record
     */
    public SummaryRecord retrieveSummaryForDate(LocalDate date, WindParameters windParameters, TemperatureBinMgr temperatureBinMgr) {
        String sql = " where " + DATE_COLUMN + "='" + DBTable.dateFormatter().format(date) + "'";

        List<SummaryRecord> list = retrieveSummaries(sql, windParameters, temperatureBinMgr);

        if (list.size() != 1)
            return null;
        else
            return list.get(0);
    }

    /**
     * Retrieve the summary records for a weather station for a period of days.
     * 
     * @param start The start of the interval
     * @param end The end of the interval
     * @param windParameters The winds parameters used to create the wind rose data
     * @param temperatureBinMgr The temperature bin manager used in the creation of the temperature bin summaries
     * @return A list of summary records for the period of days specified
     */
    public List<SummaryRecord> retrieveRange(LocalDate start, LocalDate end, WindParameters windParameters, TemperatureBinMgr temperatureBinMgr) {

        String sql = " where " + DATE_COLUMN + ">='" + DBTable.dateFormatter().format(start) + "' and " +
                                 DATE_COLUMN + "<='" + DBTable.dateFormatter().format(end) + "'";

        List<SummaryRecord> list = retrieveSummaries(sql, windParameters, temperatureBinMgr);

        return list;
    }

    /**
     * Convenience method to retrieve the summary records for a weather station using an arbitrary SQL "where" clause
     * 
     * @param clause The SQL clause to restrict the query
     * @param windParameters The winds parameters used to create the wind rose data
     * @param temperatureBinMgr The temperature bin manager used in the creation of the temperature bin summaries
     * @return
     */
    private List<SummaryRecord> retrieveSummaries(String clause, WindParameters windParameters, TemperatureBinMgr temperatureBinMgr) {
        List<SummaryRecord> list;

        String sql = "select * from " + TABLE_NAME + " ";

        if (clause != null)
            sql += clause;

        list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            WindParameters windParameters1 = (WindParameters)args[0];
            TemperatureBinMgr temperatureBinMgr1 = (TemperatureBinMgr)args[1];
            LocalDate date = rs.getDate(DATE_COLUMN).toLocalDate();
            SummaryRecord record = new SummaryRecord(windParameters1, temperatureBinMgr1, date);
            record.setDuration(Duration.ofSeconds(rs.getInt(TOTAL_DURATION_COLUMN)));
            Timestamp ts = rs.getTimestamp(HIGH_OUTDOOR_TEMP_TIME_COLUMN);

            LocalDateTime time = null;
            if (!rs.wasNull())
                time = ts.toLocalDateTime();

            double d = rs.getDouble(HIGH_OUTDOOR_TEMP_COLUMN);
            if (!rs.wasNull())
                record.setMaxOutdoorTemp(new Temperature(d, DatabaseUnits.TEMPERATURE), time);

            ts = rs.getTimestamp(LOW_OUTDOOR_TEMP_TIME_COLUMN);
            if (!rs.wasNull())
                time = ts.toLocalDateTime();
            else
                time = null;

            d = rs.getDouble(LOW_OUTDOOR_TEMP_COLUMN);
            if (!rs.wasNull())
                record.setMinOutdoorTemp(new Temperature(d, DatabaseUnits.TEMPERATURE), time);

            d = rs.getDouble(AVG_OUTDOOR_TEMP_COLUMN);
            if (!rs.wasNull())
                record.setAvgOutdoorTemp(new Temperature(d, DatabaseUnits.TEMPERATURE), Duration.ofSeconds(rs.getInt(OUTDOOR_TEMP_DURATION_COLUMN)));

            ts = rs.getTimestamp(HIGH_INDOOR_TEMP_TIME_COLUMN);
            if (!rs.wasNull())
                time = ts.toLocalDateTime();
            else
                time = null;

            d = rs.getDouble(HIGH_INDOOR_TEMP_COLUMN);
            if (!rs.wasNull())
                record.setMaxIndoorTemp(new Temperature(d, DatabaseUnits.TEMPERATURE), time);

            ts = rs.getTimestamp(LOW_INDOOR_TEMP_TIME_COLUMN);
            if (!rs.wasNull())
                time = ts.toLocalDateTime();
            else
                time = null;

            d = rs.getDouble(LOW_INDOOR_TEMP_COLUMN);
            if (!rs.wasNull())
                record.setMinIndoorTemp(new Temperature(d, DatabaseUnits.TEMPERATURE), time);

            d = rs.getDouble(AVG_INDOOR_TEMP_COLUMN);
            if (!rs.wasNull())
                record.setAvgIndoorTemp(new Temperature(d, DatabaseUnits.TEMPERATURE), Duration.ofSeconds(rs.getInt(INDOOR_TEMP_DURATION_COLUMN)));

            ////////
            ts = rs.getTimestamp(HIGH_DEW_POINT_TIME_COLUMN);
            if (!rs.wasNull())
                time = ts.toLocalDateTime();
            else
                time = null;

            d = rs.getDouble(HIGH_DEW_POINT_COLUMN);
            if (!rs.wasNull())
                record.setMaxDewPoint(new Temperature(d, DatabaseUnits.TEMPERATURE), time);

            ts = rs.getTimestamp(LOW_DEW_POINT_TIME_COLUMN);
            if (!rs.wasNull())
                time = ts.toLocalDateTime();
            else
                time = null;

            d = rs.getDouble(LOW_DEW_POINT_COLUMN);
            if (!rs.wasNull())
                record.setMinDewPoint(new Temperature(d, DatabaseUnits.TEMPERATURE), time);

            /////////////////
            
            ts = rs.getTimestamp(HIGH_HEAT_INDEX_TIME_COLUMN);
            if (!rs.wasNull())
                time = ts.toLocalDateTime();
            else
                time = null;

            d = rs.getDouble(HIGH_HEAT_INDEX_COLUMN);
            if (!rs.wasNull())
                record.setMaxHeatIndex(new Temperature(d, DatabaseUnits.TEMPERATURE), time);

            /////////////////
            
            ts = rs.getTimestamp(LOW_WIND_CHILL_TIME_COLUMN);
            if (!rs.wasNull())
                time = ts.toLocalDateTime();
            else
                time = null;

            d = rs.getDouble(LOW_WIND_CHILL_COLUMN);
            if (!rs.wasNull())
                record.setMinWindChill(new Temperature(d, DatabaseUnits.TEMPERATURE), time);

            /////////////////
            ts = rs.getTimestamp(HIGH_OUTDOOR_HUMID_TIME_COLUMN);
            if (!rs.wasNull())
                time = ts.toLocalDateTime();
            else
                time = null;

            float f = rs.getFloat(HIGH_OUTDOOR_HUMID_COLUMN);
            if (!rs.wasNull())
                record.setMaxOutdoorHumidity(new Humidity(f), time);

            ts = rs.getTimestamp(LOW_OUTDOOR_HUMID_TIME_COLUMN);
            if (!rs.wasNull())
                time = ts.toLocalDateTime();
            else
                time = null;

            f = rs.getFloat(LOW_OUTDOOR_HUMID_COLUMN);
            if (!rs.wasNull())
                record.setMinOutdoorHumidity(new Humidity(f), time);

            f = rs.getFloat(AVG_OUTDOOR_HUMID_COLUMN);
            if (!rs.wasNull())
                record.setAvgOutdoorHumidity(new Humidity(f), Duration.ofSeconds(rs.getInt(OUTDOOR_HUMID_DURATION_COLUMN)));

            ts = rs.getTimestamp(HIGH_INDOOR_HUMID_TIME_COLUMN);
            if (!rs.wasNull())
                time = ts.toLocalDateTime();
            else
                time = null;

            f = rs.getFloat(HIGH_INDOOR_HUMID_COLUMN);
            if (!rs.wasNull())
                record.setMaxIndoorHumidity(new Humidity(f), time);

            ts = rs.getTimestamp(LOW_INDOOR_HUMID_TIME_COLUMN);
            if (!rs.wasNull())
                time = ts.toLocalDateTime();
            else
                time = null;

            f = rs.getFloat(LOW_INDOOR_HUMID_COLUMN);
            if (!rs.wasNull())
                record.setMinIndoorHumidity(new Humidity(f), time);

            f = rs.getFloat(AVG_INDOOR_HUMID_COLUMN);
            if (!rs.wasNull())
                record.setAvgIndoorHumidity(new Humidity(f), Duration.ofSeconds(rs.getInt(INDOOR_HUMID_DURATION_COLUMN)));

            Pressure baro = null;

            d = rs.getDouble(LOW_BARO_PRESSURE_COLUMN);
            if (!rs.wasNull())
                baro = new Pressure(d, DatabaseUnits.PRESSURE);

            ts = rs.getTimestamp(LOW_PRESSURE_TIME_COLUMN);
            if (!rs.wasNull())
                time = ts.toLocalDateTime();
            else
                time = null;

            record.setMinBaroPressure(baro, time);

            d = rs.getDouble(HIGH_BARO_PRESSURE_COLUMN);
            if (!rs.wasNull())
                baro = new Pressure(d, DatabaseUnits.PRESSURE);

            ts = rs.getTimestamp(HIGH_PRESSURE_TIME_COLUMN);
            if (!rs.wasNull())
                time = ts.toLocalDateTime();
            else
                time = null;

            record.setMaxBaroPressure(baro, time);

            d = rs.getDouble(AVG_BARO_PRESSURE_COLUMN);
            if (!rs.wasNull())
                baro = new Pressure(d, DatabaseUnits.PRESSURE);

            record.setAvgBaroPressure(baro, Duration.ofSeconds(rs.getInt(PRESSURE_DURATION_COLUMN)));

            ts = rs.getTimestamp(MAX_WIND_SPEED_TIME_COLUMN);
            if (!rs.wasNull())
                time = ts.toLocalDateTime();
            else
                time = null;

            d = rs.getDouble(MAX_WIND_SPEED_COLUMN);
            if (!rs.wasNull())
                record.setMaxWindSpeed(new Speed(d, DatabaseUnits.SPEED), time);

            ts = rs.getTimestamp(MAX_WIND_GUST_TIME_COLUMN);

            if (!rs.wasNull()) {
                time = ts.toLocalDateTime();
                record.setMaxWindGust(new Speed(rs.getDouble(MAX_WIND_GUST_COLUMN), DatabaseUnits.SPEED), time);
            }

            d = rs.getDouble(AVG_WIND_SPEED_COLUMN);
            if (!rs.wasNull())
                record.setAvgWindSpeed(new Speed(d, DatabaseUnits.SPEED), Duration.ofSeconds(rs.getInt(WIND_DURATION_COLUMN)));

            record.setTotalRainfall(new Depth(rs.getDouble(RAINFALL_COLUMN), DatabaseUnits.DEPTH));

            ts = rs.getTimestamp(MAX_RAINFALL_RATE_TIME_COLUMN);
            if (!rs.wasNull())
                time = ts.toLocalDateTime();
            else
                time = null;

            d = rs.getDouble(MAX_RAINFALL_RATE_COLUMN);
            if (!rs.wasNull())
                record.setMaxRainfallRate(new Depth(d, DatabaseUnits.DEPTH), time);

            d = rs.getDouble(AVG_SOLAR_RADIATION_COLUMN);
            if (!rs.wasNull())
                record.setAvgSolarRadiation(new SolarRadiation(d), Duration.ofSeconds(rs.getInt(SOLAR_RADIATION_DURATION_COLUMN)));

            ts = rs.getTimestamp(MAX_SOLAR_RADIATION_TIME_COLUMN);
            if (!rs.wasNull())
                time = ts.toLocalDateTime();
            else
                time = null;

            d = rs.getDouble(MAX_SOLAR_RADIATION_COLUMN);
            if (!rs.wasNull())
                record.setMaxSolarRadiation(new SolarRadiation(d), time);

            d = rs.getDouble(TOTAL_EVAPOTRANSPIRATION_COLUMN);
            if (!rs.wasNull())
                record.setTotalET(new Depth(d, DatabaseUnits.DEPTH));

            logger.finer("Now retrieving Wind data");

            windSliceTable.retrieveDailyValues(record.getWindRoseData());

            logger.finer("Now retrieving Rain data");

            DayHourRain dayRain = rainHourTable.retrieveDailyValues(date);
            record.setRainfall(dayRain);
            List<TemperatureBinDuration> binDurations = temperatureBinDurationTable.retrieveBinsForDay(date);
            record.setTemperatureBinDurations(binDurations);

            logger.finer("Now retrieving sensor values");
            querySensorValues(record);

            return record;

        }, windParameters, temperatureBinMgr);

        return list;
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#updateRow(java.lang.Object)
     */
    @Override
    public boolean updateRow(SummaryRecord object) {
        return addRow(object);
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#query(java.lang.String)
     */
    @Override
    public List<SummaryRecord> query(String clause) {
        throw new UnsupportedOperationException("Direct call to query not allowed");
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#addRow(java.lang.Object)
     */
    @Override
    public boolean addRow(SummaryRecord record) {
        boolean success = true;
	int col = 1;
        try {
            getConnection().startTransaction();

	    getConnection().executeUpdate("delete from " + TABLE_NAME + " where " +
		                           DATE_COLUMN + "='" + DBTable.dateFormatter().format(record.getDate()) + "'");

	    try (PreparedStatement stmt = getConnection().getConnection().prepareStatement(INSERT_SQL)) {
                stmt.setDate(col++, java.sql.Date.valueOf(record.getDate()));
		stmt.setLong(col++, record.getDuration().getSeconds());
		//
		// Outdoor temperature
		//
		stmt.setLong(col++, record.getOutdoorTempDuration().getSeconds());
		if (record.getMaxOutdoorTemp() != null) {
		    stmt.setDouble(col++, record.getMaxOutdoorTemp().get(DatabaseUnits.TEMPERATURE));
		    stmt.setTimestamp(col++, java.sql.Timestamp.valueOf(record.getMaxOutdoorTempTime()));
		}
		else {
		    stmt.setNull(col++, Types.DOUBLE);
		    stmt.setNull(col++, Types.TIMESTAMP);
		}
		
		if (record.getMinOutdoorTemp() != null) {
		    stmt.setDouble(col++, record.getMinOutdoorTemp().get(DatabaseUnits.TEMPERATURE));
		    stmt.setTimestamp(col++, java.sql.Timestamp.valueOf(record.getMinOutdoorTempTime()));
		}
		else {
		    stmt.setNull(col++, Types.DOUBLE);
		    stmt.setNull(col++, Types.TIMESTAMP);
		}
		
		stmt.setDouble(col++, record.getAvgOutdoorTemp().get(DatabaseUnits.TEMPERATURE));
		
                if (record.getMaxDewPoint() != null) {
		    stmt.setDouble(col++, record.getMaxDewPoint().get(DatabaseUnits.TEMPERATURE));
		    stmt.setTimestamp(col++, java.sql.Timestamp.valueOf(record.getMaxDewPointTime()));
                }
                else {
                    stmt.setNull(col++, Types.DOUBLE);
                    stmt.setNull(col++, Types.TIMESTAMP);
                }

                if (record.getMinDewPoint() != null) {
		    stmt.setDouble(col++, record.getMinDewPoint().get(DatabaseUnits.TEMPERATURE));
		    stmt.setTimestamp(col++, java.sql.Timestamp.valueOf(record.getMinDewPointTime()));
                }
                else {
                    stmt.setNull(col++, Types.DOUBLE);
                    stmt.setNull(col++, Types.TIMESTAMP);
                }

                if (record.getMaxHeatIndex() != null) {
		    stmt.setDouble(col++, record.getMaxHeatIndex().get(DatabaseUnits.TEMPERATURE));
		    stmt.setTimestamp(col++, java.sql.Timestamp.valueOf(record.getMaxHeatIndexTime()));
                }
                else {
                    stmt.setNull(col++, Types.DOUBLE);
                    stmt.setNull(col++, Types.TIMESTAMP);
                }

                if (record.getMinWindChill() != null) {
		    stmt.setDouble(col++, record.getMinWindChill().get(DatabaseUnits.TEMPERATURE));
		    stmt.setTimestamp(col++, java.sql.Timestamp.valueOf(record.getMinWindChillTime()));
                }
                else {
                    stmt.setNull(col++, Types.DOUBLE);
                    stmt.setNull(col++, Types.TIMESTAMP);
                }

		//
		// Indoor temperature
		//
		stmt.setLong(col++, record.getIndoorTempDuration().getSeconds());
		
		if (record.getMaxIndoorTemp() != null) {
		    stmt.setDouble(col++, record.getMaxIndoorTemp().get(DatabaseUnits.TEMPERATURE));
		    stmt.setTimestamp(col++, java.sql.Timestamp.valueOf(record.getMaxIndoorTempTime()));
		}
		else {
		    stmt.setNull(col++, Types.DOUBLE);
		    stmt.setNull(col++, Types.TIMESTAMP);
		}
		
		if (record.getMinIndoorTemp() != null) {
		    stmt.setDouble(col++, record.getMinIndoorTemp().get(DatabaseUnits.TEMPERATURE));
		    stmt.setTimestamp(col++, java.sql.Timestamp.valueOf(record.getMinIndoorTempTime()));
		}
		else {
		    stmt.setNull(col++, Types.DOUBLE);
		    stmt.setNull(col++, Types.TIMESTAMP);
		}
		stmt.setDouble(col++, record.getAvgIndoorTemp().get(DatabaseUnits.TEMPERATURE));
		
		//
		// Outdoor humidity
		//
		stmt.setLong(col++, record.getOutdoorHumidityDuration().getSeconds());
		
		if (record.getMaxOutdoorHumidityTime() != null) {
		    stmt.setDouble(col++, record.getMaxOutdoorHumidity().get());
		    stmt.setTimestamp(col++, java.sql.Timestamp.valueOf(record.getMaxOutdoorHumidityTime()));
		}
		else {
		    stmt.setNull(col++, Types.DOUBLE);
		    stmt.setNull(col++, Types.TIMESTAMP);
		}
		
		if (record.getMinOutdoorHumidity() != null) {
		    stmt.setDouble(col++, record.getMinOutdoorHumidity().get());
		    stmt.setTimestamp(col++, java.sql.Timestamp.valueOf(record.getMinOutdoorHumidityTime()));
		}
		else {
		    stmt.setNull(col++, Types.DOUBLE);
		    stmt.setNull(col++, Types.TIMESTAMP);
		}
		stmt.setDouble(col++, record.getAvgOutdoorHumidity().get());
		
		//
		// Indoor humidity
		//
		stmt.setLong(col++, record.getIndoorHumidityDuration().getSeconds());
		if (record.getMaxIndoorHumidity() != null) {
		    stmt.setDouble(col++, record.getMaxIndoorHumidity().get());
		    stmt.setTimestamp(col++, java.sql.Timestamp.valueOf(record.getMaxIndoorHumidityTime()));
		}
		else {
		    stmt.setNull(col++, Types.DOUBLE);
		    stmt.setNull(col++, Types.TIMESTAMP);
		}
		
		if (record.getMinIndoorHumidity() != null) {
		    stmt.setDouble(col++, record.getMinIndoorHumidity().get());
		    stmt.setTimestamp(col++, java.sql.Timestamp.valueOf(record.getMinIndoorHumidityTime()));
		}
		else {
		    stmt.setNull(col++, Types.DOUBLE);
		    stmt.setNull(col++, Types.TIMESTAMP);
		}
		stmt.setDouble(col++, record.getAvgIndoorHumidity().get());
		
		//
		// Pressure 
		//
		stmt.setLong(col++, record.getBaroPressureDuration().getSeconds());
		if (record.getMaxBaroPressure() != null) {
		    stmt.setDouble(col++, record.getMaxBaroPressure().get(DatabaseUnits.PRESSURE));
		    stmt.setTimestamp(col++, java.sql.Timestamp.valueOf(record.getMaxBaroPressureTime()));
		}
		else {
		    stmt.setNull(col++, Types.DOUBLE);
		    stmt.setNull(col++, Types.TIMESTAMP);
		}
		
		if (record.getMinBaroPressure() != null) {
		    stmt.setDouble(col++, record.getMinBaroPressure().get(DatabaseUnits.PRESSURE));
		    stmt.setTimestamp(col++, java.sql.Timestamp.valueOf(record.getMinBaroPressureTime()));
		}
		else {
		    stmt.setNull(col++, Types.DOUBLE);
		    stmt.setNull(col++, Types.TIMESTAMP);
		}
		
		stmt.setDouble(col++, record.getAvgBaroPressure().get(DatabaseUnits.PRESSURE));
		
		//
		// Wind
		//
		stmt.setLong(col++, record.getWindDuration().getSeconds());
		
		if (record.getMaxWindSpeed() != null) {
		    stmt.setDouble(col++, record.getMaxWindSpeed().get(DatabaseUnits.SPEED));
		    stmt.setTimestamp(col++, java.sql.Timestamp.valueOf(record.getMaxWindSpeedTime()));
		}
		else {
		    stmt.setNull(col++, Types.DOUBLE);
		    stmt.setNull(col++, Types.TIMESTAMP);
		}
		    
		Speed gust = record.getMaxWindGust();

		if (gust != null) {
		    stmt.setDouble(col++, record.getMaxWindGust().get(DatabaseUnits.SPEED));
		    stmt.setTimestamp(col++, java.sql.Timestamp.valueOf(record.getMaxWindGustTime()));
		}
		else {
		    stmt.setNull(col++, Types.DOUBLE);
		    stmt.setNull(col++, Types.TIMESTAMP);
		}

		stmt.setDouble(col++, record.getAvgWindSpeed().get(DatabaseUnits.SPEED));

		//
		// Rainfall
		//
		stmt.setDouble(col++, record.getTotalRainfall().get(DatabaseUnits.DEPTH));
		if (record.getMaxRainfallRate() != null) {
		    stmt.setDouble(col++, record.getMaxRainfallRate().get(DatabaseUnits.DEPTH));
		    stmt.setTimestamp(col++, java.sql.Timestamp.valueOf(record.getMaxRainfallRateTime()));
		}
		else {
		    stmt.setNull(col++, Types.DOUBLE);
		    stmt.setNull(col++, Types.TIMESTAMP);
		}

                //
                // Solar Radiation
                //
                if (record.getAvgSolarRadiation() != null) {
                    stmt.setDouble(col++, record.getAvgSolarRadiation().get());
                    stmt.setLong(col++, record.getSolarRadiationDuration().getSeconds());
                }
                else {
		    stmt.setNull(col++, Types.DOUBLE);
		    stmt.setNull(col++, Types.INTEGER);
                }

                if (record.getMaxSolarRadiation() != null) {
                    stmt.setDouble(col++, record.getMaxSolarRadiation().get());
                    stmt.setTimestamp(col++, java.sql.Timestamp.valueOf(record.getMaxSolarRadiationTime()));
                }
                else {
		    stmt.setNull(col++, Types.DOUBLE);
		    stmt.setNull(col++, Types.TIMESTAMP);
                }

                if (record.getTotalET() != null)
                    stmt.setDouble(col++, record.getTotalET().get(DatabaseUnits.DEPTH));
                else
		    stmt.setNull(col++, Types.DOUBLE);

                // UV Index
                stmt.setNull(col++, Types.DOUBLE);
                stmt.setNull(col++, Types.INTEGER);
                stmt.setNull(col++, Types.DOUBLE);
                stmt.setNull(col++, Types.TIMESTAMP);

		assert col == 59 : "Number of columns is " + col + " not 51";

		success = stmt.executeUpdate() == 1;
	    }

            if (success) {
                if (!windSliceTable.addDailyValues(record.getWindRoseData()) ||
                    !rainHourTable.addHourlyValues(record.getHourlyRainfall()) ||
                    !temperatureBinDurationTable.addRowsFromSummary(record) ||
                    !addSensorValues(record.getSensorValues())) {
                    success = false;
                }
            }
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Caught SQL Exception. Columns = " + col, e);
            success = false;
        }
	finally {
            if (success)
                getConnection().endTransaction();
            else
		getConnection().rollback();
	}

        return success;
    }

    private boolean addSensorValues(Collection<MeasurementStatisticsEntry<? extends Measurement>> values) {
	for (MeasurementStatisticsEntry<? extends Measurement> entry : values) {
	    try {
		try (PreparedStatement stmt = getConnection().getConnection().prepareStatement(INSERT_SENSOR_SQL)) {
		    int n = 1;
                    Unit unit = DatabaseUnits.getDatabaseUnit(entry.getStats().getMaximumValue());
		    stmt.setDate(n++, java.sql.Date.valueOf(entry.getDate()));
                    stmt.setInt(n++, entry.getSensorId());
                    stmt.setInt(n++, (int)entry.getStats().getAverageDuration().getSeconds());
                    stmt.setString(n++, entry.getSensorType().getCode());
                    stmt.setDouble(n++, entry.getStats().getMaximumValue().get(unit));
                    stmt.setTimestamp(n++, java.sql.Timestamp.valueOf(entry.getStats().getMaximumTime()));
                    stmt.setDouble(n++, entry.getStats().getMinimumValue().get(unit));
                    stmt.setTimestamp(n++, java.sql.Timestamp.valueOf(entry.getStats().getMinimumTime()));
                    stmt.setDouble(n++, entry.getStats().getAverage().get(unit));

                    if (stmt.executeUpdate() != 1)
                        return false;
		}
	    }
	    catch (SQLException e) {
		logger.log(Level.SEVERE, "Caught SQL Exception", e);
                return false;
	    }
	}

	return true;
    }

    private boolean querySensorValues(SummaryRecord rec) {
        final String sql = "select * from " + SENSOR_SUMMARY_TABLE_NAME +
                           " where " + SENSOR_DATE_COLUMN + "='" + DBTable.dateFormatter().format(rec.getDate()) + "'";

        List<MeasurementStatisticsEntry<? extends Measurement>> list;
        list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            MeasurementStatisticsEntry<? extends Measurement> entry;
            
            LocalDate date = rs.getDate(SENSOR_DATE_COLUMN).toLocalDate();
            int sensorId = rs.getInt(SENSOR_ID_COLUMN);
            SensorType sensorType = SensorType.fromCode(rs.getString(SENSOR_TYPE_COLUMN));
            double maxValue = rs.getDouble(SENSOR_HIGH_MEASUREMENT_COLUMN);
            LocalDateTime maxTime = rs.getTimestamp(SENSOR_HIGH_MEASUREMENT_TIME_COLUMN).toLocalDateTime();
            double minValue = rs.getDouble(SENSOR_LOW_MEASUREMENT_COLUMN);
            LocalDateTime minTime = rs.getTimestamp(SENSOR_LOW_MEASUREMENT_TIME_COLUMN).toLocalDateTime();
            Duration duration = Duration.ofSeconds(rs.getInt(SENSOR_DURATION_COLUMN));
            double avgValue = rs.getDouble(SENSOR_AVG_MEASUREMENT_COLUMN);
            
            switch (sensorType) {
                case THERMOMETER:
                case LEAF_TEMPERATURE:
                case SOIL_TEMPERATURE:
                    MeasurementStatistics<Temperature> stats = new MeasurementStatistics<>(new Temperature(0.0));
                    stats.setMinimum(new Temperature(minValue, DatabaseUnits.TEMPERATURE), minTime);
                    stats.setMaximum(new Temperature(maxValue, DatabaseUnits.TEMPERATURE), maxTime);
                    stats.setAverage(new Temperature(avgValue, DatabaseUnits.TEMPERATURE), duration);
                    entry = new MeasurementStatisticsEntry<>(date, sensorId, sensorType, stats);
                    break;
                    
                case HYGROMETER:
                    MeasurementStatistics<Humidity> hstats = new MeasurementStatistics<>(new Humidity(0.0));
                    hstats.setMinimum(new Humidity(minValue), minTime);
                    hstats.setMaximum(new Humidity(maxValue), maxTime);
                    hstats.setAverage(new Humidity(avgValue), duration);
                    entry = new MeasurementStatisticsEntry<>(date, sensorId, sensorType, hstats);
                    break;
                    
                case LEAF_WETNESS:
                    MeasurementStatistics<LeafWetness> wstats = new MeasurementStatistics<>(new LeafWetness(0));
                    wstats.setMinimum(new LeafWetness((int)minValue), minTime);
                    wstats.setMaximum(new LeafWetness((int)maxValue), maxTime);
                    wstats.setAverage(new LeafWetness((int)avgValue), duration);
                    entry = new MeasurementStatisticsEntry<>(date, sensorId, sensorType, wstats);
                    break;
                    
                case SOIL_MOISTURE:
                    MeasurementStatistics<SoilMoisture> mstats = new MeasurementStatistics<>(new SoilMoisture(0.0));
                    mstats.setMinimum(new SoilMoisture(minValue), minTime);
                    mstats.setMaximum(new SoilMoisture(maxValue), maxTime);
                    mstats.setAverage(new SoilMoisture(avgValue), duration);
                    entry = new MeasurementStatisticsEntry<>(date, sensorId, sensorType, mstats);
                    break;
                    
                default:
                    logger.log(Level.SEVERE, "Invalid SensorType {0}", sensorType);
                    entry = null;
                    break;
            }
            
            return entry;
        });

        list.stream().forEach((entry) -> {
            rec.addSensorStatistics(entry);
        });

        return true;
    }
}