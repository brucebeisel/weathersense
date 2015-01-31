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
import java.time.Month;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.Pair;
import com.bdb.util.jdbc.DBConnection;
import com.bdb.util.jdbc.DBTable;

import com.bdb.weather.common.GeographicLocation;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.WindParameters;
import com.bdb.weather.common.measurement.AngularMeasurement;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Distance;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;

/**
 * The weather station table contains the attributes of a weather station.
 * 
 * @author Bruce
 *
 */
public class WeatherStationTable extends DBTable<WeatherStation> {
    protected static final String TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".weather_station";
    private static final int SINGLETON_VALUE = 1;
    private static final int INSERT_COLUMN_COUNT = 25;
    private static final int UPDATE_COLUMN_COUNT = 24;
    private static final String SINGLETON_ID_COLUMN = "singleton_id";
    private static final String WHERE_CLAUSE = " where " + SINGLETON_ID_COLUMN + "=" + SINGLETON_VALUE;
    private static final String MANUFACTURER_COLUMN = "manufacturer";
    private static final String MODEL_COLUMN = "model";
    private static final String FIRMWARE_DATE_COLUMN = "firmware_date";
    private static final String FIRMWARE_VERSION_COLUMN = "firmware_version";
    private static final String LOCATION_CODE_COLUMN = "location_code";
    private static final String STATION_LOCATION_DESCRIPTION_COLUMN = "location_description";
    private static final String STATION_LATITUDE_COLUMN = "latitude";
    private static final String STATION_LONGITUDE_COLUMN = "longitude";
    private static final String STATION_ALTITUDE_COLUMN = "altitude";
    private static final String WEATHER_YEAR_START_MONTH_COLUMN = "weather_year_start_month";
    private static final String WIND_SLICE_COUNT_COLUMN = "wind_slice_count";
    private static final String WIND_SPEED_BIN_INTERVAL_COLUMN = "wind_speed_bin_interval";
    private static final String NUM_WIND_SPEED_BINS_COLUMN = "num_wind_speed_bins";
    private static final String THERMOMETER_MIN_COLUMN = "thermometer_min";
    private static final String THERMOMETER_MAX_COLUMN = "thermometer_max";
    private static final String BARO_PRESSURE_MIN_COLUMN = "atm_pressure_min";
    private static final String BARO_PRESSURE_MAX_COLUMN = "atm_pressure_max";
    private static final String DAILY_RAIN_MAX_COLUMN = "daily_rain_max";
    private static final String MONTHLY_RAIN_MAX_COLUMN = "monthly_rain_max";
    private static final String YEARLY_RAIN_MAX_COLUMN = "yearly_rain_max";
    private static final String DOPPLER_RADAR_URL_COLUMN = "doppler_radar_url";
    private static final String WEATHER_UNDERGROUND_STATION_ID_COLUMN = "weather_underground_station_id";
    private static final String WEATHER_UNDERGROUND_PASSWORD_COLUMN = "weather_underground_password";
    private static final String INSERT_SQL = "insert into " + TABLE_NAME + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String UPDATE_SQL = "update " + TABLE_NAME + " set " +
            MANUFACTURER_COLUMN + "=?," +
            MODEL_COLUMN + "=?," +
            FIRMWARE_DATE_COLUMN + "=?," +
            FIRMWARE_VERSION_COLUMN + "=?," +
            LOCATION_CODE_COLUMN + "=?," +
            STATION_LOCATION_DESCRIPTION_COLUMN + "=?," +
            STATION_LATITUDE_COLUMN + "=?," +
            STATION_LONGITUDE_COLUMN + "=?," +
            STATION_ALTITUDE_COLUMN + "=?," +
            WEATHER_YEAR_START_MONTH_COLUMN + "=?," +
            WIND_SLICE_COUNT_COLUMN + "=?," +
            WIND_SPEED_BIN_INTERVAL_COLUMN + "=?," +
            NUM_WIND_SPEED_BINS_COLUMN + "=?," +
            THERMOMETER_MIN_COLUMN + "=?," +
            THERMOMETER_MAX_COLUMN + "=?," +
            BARO_PRESSURE_MIN_COLUMN + "=?," +
            BARO_PRESSURE_MAX_COLUMN + "=?," +
            DAILY_RAIN_MAX_COLUMN + "=?," +
            MONTHLY_RAIN_MAX_COLUMN + "=?," +
            YEARLY_RAIN_MAX_COLUMN + "=?," +
            DOPPLER_RADAR_URL_COLUMN + "=?," +
            WEATHER_UNDERGROUND_STATION_ID_COLUMN + "=?," +
            WEATHER_UNDERGROUND_PASSWORD_COLUMN + "=?" +
            WHERE_CLAUSE;
    private static final Logger logger = Logger.getLogger(WeatherStationTable.class.getName());
    private final SensorTable   sensorTable;

    /**
     * Constructor.
     * 
     * @param connection The connection with the database
     */
    public WeatherStationTable(DBConnection connection) {
        super(TABLE_NAME, connection);
        sensorTable = new SensorTable(connection);
    }

    /**
     * Get the weather station.
     * 
     * @return The weather station
     */
    public WeatherStation getWeatherStation() {
        List<WeatherStation> list = query(null);
        if (list.isEmpty())
            return null;
        else
            return list.get(0);
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#query(java.lang.String)
     */
    @Override
    public List<WeatherStation> query(String clause) {
        String sql = "select * from " + TABLE_NAME + WHERE_CLAUSE;

        List<WeatherStation> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            WeatherStation ws = new WeatherStation();
            ws.setManufacturer(rs.getString(MANUFACTURER_COLUMN));
            ws.setModel(rs.getString(MODEL_COLUMN));
            ws.setFirmwareDate(rs.getString(FIRMWARE_DATE_COLUMN));
            ws.setFirmwareVersion(rs.getString(FIRMWARE_VERSION_COLUMN));
            ws.setLocationCode(rs.getString(LOCATION_CODE_COLUMN));
            ws.setLocationDescription(rs.getString(STATION_LOCATION_DESCRIPTION_COLUMN));
            
            GeographicLocation geoLoc = new GeographicLocation(new AngularMeasurement(rs.getDouble(STATION_LATITUDE_COLUMN), AngularMeasurement.Unit.DEGREES),
                    new AngularMeasurement(rs.getDouble(STATION_LONGITUDE_COLUMN), AngularMeasurement.Unit.DEGREES),
                    new Distance(rs.getDouble(STATION_ALTITUDE_COLUMN), Depth.Unit.METERS));
            ws.setGeographicLocation(geoLoc);
            ws.setWeatherYearStartMonth(Month.of(rs.getInt(WEATHER_YEAR_START_MONTH_COLUMN)));
            int windSlices = rs.getInt(WIND_SLICE_COUNT_COLUMN);
            Speed windSpeedInterval = new Speed(rs.getDouble(WIND_SPEED_BIN_INTERVAL_COLUMN), DatabaseUnits.SPEED);
            int numBins = rs.getInt(NUM_WIND_SPEED_BINS_COLUMN);
            ws.setWindParameters(new WindParameters(windSpeedInterval, numBins, windSlices));
            ws.setWeatherUndergroundStationId(rs.getString(WEATHER_UNDERGROUND_STATION_ID_COLUMN));
            ws.setWeatherUndergroundPassword(rs.getString(WEATHER_UNDERGROUND_PASSWORD_COLUMN));
            ws.setDailyRainMax(new Depth(rs.getDouble(DAILY_RAIN_MAX_COLUMN), DatabaseUnits.DEPTH));
            ws.setMonthlyRainMax(new Depth(rs.getDouble(MONTHLY_RAIN_MAX_COLUMN), DatabaseUnits.DEPTH));
            ws.setYearlyRainMax(new Depth(rs.getDouble(YEARLY_RAIN_MAX_COLUMN), DatabaseUnits.DEPTH));
            ws.setThermometerMin(new Temperature(rs.getDouble(THERMOMETER_MIN_COLUMN), DatabaseUnits.TEMPERATURE));
            ws.setThermometerMax(new Temperature(rs.getDouble(THERMOMETER_MAX_COLUMN), DatabaseUnits.TEMPERATURE));
            ws.setBarometerMin(new Pressure(rs.getDouble(BARO_PRESSURE_MIN_COLUMN), DatabaseUnits.PRESSURE));
            ws.setBarometerMax(new Pressure(rs.getDouble(BARO_PRESSURE_MAX_COLUMN), DatabaseUnits.PRESSURE));
            ws.setDopplerRadarUrl(rs.getString(DOPPLER_RADAR_URL_COLUMN));
            return ws;
        });
        
        if (!list.isEmpty()) {
            WeatherStation ws = list.get(0);
            sensorTable.getSensorList().stream().forEach((sensor) -> {
                ws.addSensor(sensor);
            });
        }

        return list;
    }

    /**
     * Retrieve the parameters needed to communicate with the Weather Underground web site.
     * 
     * @return Two strings, one being the weather underground station ID, the other the password
     */
    public Pair<String,String> weatherUndergroundParameters() {
        String sql = "select " + WEATHER_UNDERGROUND_STATION_ID_COLUMN + "," + WEATHER_UNDERGROUND_PASSWORD_COLUMN +
                     " from " + TABLE_NAME;
        
        List<Pair<String,String>> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            Pair<String,String> wuParams = null;
            String wuStationId = rs.getString(1);
            boolean wuStationIdNull = rs.wasNull();
            
            String wuPassword = rs.getString(2);
            boolean wuPasswordNull = rs.wasNull();
            
            if (!wuStationIdNull && !wuPasswordNull)
                wuParams = new Pair<>(wuStationId, wuPassword);
            
            return wuParams;
        });

        if (list.size() > 0)
            return list.get(0);
        else
            return null;
    }

    private boolean addOrUpdate(WeatherStation row) {
        boolean add = false;
        if (getWeatherStation() == null)
            add = true;

        boolean success = false;
        
        if (!getConnection().startTransaction()) {
            logger.severe("Unable to start transaction, aborting update");
            return false;
        }

        String sql;
        int columnCount;
        if (add) {
            sql = INSERT_SQL;
            columnCount = INSERT_COLUMN_COUNT;
        }
        else {
            sql = UPDATE_SQL;
            columnCount = UPDATE_COLUMN_COUNT;
        }


        try (PreparedStatement stmt = getConnection().getConnection().prepareStatement(sql)) {
            int col = 1;
            if (add)
                stmt.setInt(col++, SINGLETON_VALUE);
            stmt.setString(col++, row.getManufacturer());
            stmt.setString(col++, row.getModel());
            stmt.setString(col++, row.getFirmwareDate());
            stmt.setString(col++, row.getFirmwareVersion());
            stmt.setString(col++, row.getLocationCode());
            stmt.setString(col++, row.getLocationDescription());
            stmt.setDouble(col++, row.getGeographicLocation().getLatitude().get(DatabaseUnits.LAT_LON));
            stmt.setDouble(col++, row.getGeographicLocation().getLongitude().get(DatabaseUnits.LAT_LON));
            stmt.setDouble(col++, row.getGeographicLocation().getAltitude().get(DatabaseUnits.ALTITUDE));
            stmt.setInt(col++, row.getWeatherYearStartMonth().getValue());
            stmt.setInt(col++, row.getWindParameters().getNumWindDirectionSlices());
            stmt.setDouble(col++, row.getWindParameters().getWindSpeedBinInterval().get(DatabaseUnits.SPEED));
            stmt.setInt(col++, row.getWindParameters().getNumWindSpeedBins());
            stmt.setDouble(col++, row.getThermometerMin().get(DatabaseUnits.TEMPERATURE));
            stmt.setDouble(col++, row.getThermometerMax().get(DatabaseUnits.TEMPERATURE));
            stmt.setDouble(col++, row.getBarometerMin().get(DatabaseUnits.PRESSURE));
            stmt.setDouble(col++, row.getBarometerMax().get(DatabaseUnits.PRESSURE));
            stmt.setDouble(col++, row.getDailyRainMax().get(DatabaseUnits.DEPTH));
            stmt.setDouble(col++, row.getMonthlyRainMax().get(DatabaseUnits.DEPTH));
            stmt.setDouble(col++, row.getYearlyRainMax().get(DatabaseUnits.DEPTH));
            stmt.setString(col++, row.getDopplerRadarUrl());
            stmt.setString(col++, row.getWeatherUndergroundStationId());
            stmt.setString(col++, row.getWeatherUndergroundPassword());

            assert col == columnCount : "Number of columns is " + col + " not " + columnCount;

            success = stmt.executeUpdate() == 1;
        }
        catch (SQLException e) {
            success = false;
            logger.log(Level.SEVERE, "Failed to insert/update weather station record", e);
        }
	finally {
            if (success) {
                if (!getConnection().endTransaction())
                    logger.severe("End transaction failed");
            }
            else
		getConnection().rollback();
	}

        return success;
    }

    @Override
    public boolean addRow(WeatherStation row) {
        return addOrUpdate(row);
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#updateRow(java.lang.Object)
     */
    @Override
    public boolean updateRow(WeatherStation row) {
        return addOrUpdate(row);
    }
}