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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.jdbc.DBConnection;

/**
 * Database utilities for testing.
 *
 * @author Bruce
 */
public class DatabaseUtilities {
    private final DBConnection connection;
    private static final String DERBY_URL = "jdbc:derby:memory:" + DatabaseConstants.DATABASE_NAME + ";";
    private static final String CREATE_DERBY_DB_URL = DERBY_URL + "create=true";
    
    private static final String CREATE_SENSORS_TABLE_SQL =
            "CREATE  TABLE " + SensorTable.TABLE_NAME + " (\n" +
            "sensor_id INT NOT NULL, \n" +
            "type VARCHAR(10) NOT NULL ,\n" +
            "name VARCHAR(20), \n" +
            "PRIMARY KEY (sensor_id) )";

    static private final String CREATE_HISTORY_TABLE_SQL =
            "CREATE TABLE " + HistoryTable.TABLE_NAME
            + " (date TIMESTAMP NOT NULL ,"
            + "duration INT DEFAULT 0 ,"
            + "low_outdoor_temperature DECIMAL(6,3) DEFAULT NULL ,"
            + "avg_outdoor_temperature DECIMAL(6,3) DEFAULT NULL ,"
            + "high_outdoor_temperature DECIMAL(6,3) DEFAULT NULL ,"
            + "outdoor_humidity INT DEFAULT NULL ,"
            + "indoor_temperature DECIMAL(6,3) DEFAULT NULL ,"
            + "indoor_humidity INT DEFAULT NULL ,"
            + "avg_wind_speed DECIMAL(5,2) DEFAULT NULL ,"
            + "prevailing_wind_direction DECIMAL(4,1) DEFAULT NULL ,"
            + "high_wind_speed DECIMAL(5,2) DEFAULT NULL ,"
            + "high_wind_direction DECIMAL(4,1) DEFAULT NULL ,"
            + "wind_gust DECIMAL(5,2) DEFAULT NULL ,"
            + "wind_gust_direction DECIMAL(4,1) DEFAULT NULL ,"
            + "baro_pressure DECIMAL(6,2) DEFAULT NULL ,"
            + "rainfall DECIMAL(7,3) DEFAULT NULL ,"
            + "high_rain_rate DECIMAL(7,3) DEFAULT NULL ,"
            + "avg_uv_index DECIMAL(3,1) DEFAULT NULL ,"
            + "high_uv_index DECIMAL(3,1) DEFAULT NULL ,"
            + "avg_solar_radiation DOUBLE DEFAULT NULL ,"
            + "high_solar_radiation DOUBLE DEFAULT NULL ,"
            + "evapotranspiration DOUBLE DEFAULT NULL ,"
            + "PRIMARY KEY (date) )";

    private static final String CREATE_SENSOR_VALUE_HISTORY_TABLE_SQL =
              "CREATE  TABLE " + HistoryTable.MEASUREMENT_HISTORY_TABLE_NAME
            + " (date TIMESTAMP NOT NULL ,\n"
            + "sensor_id INT NOT NULL ,\n"
            + "sensor_type VARCHAR(2) NOT NULL ,\n"
            + "measurement DOUBLE NOT NULL ,\n"
            + "PRIMARY KEY (date, sensor_id),\n"
            + "CONSTRAINT fk_sensor_value_history\n"
            + "FOREIGN KEY (date)\n"
            + "REFERENCES weathersense24.history (date)\n"
            + "ON DELETE CASCADE\n"
            + "ON UPDATE NO ACTION)";

    private static final String CREATE_SUMMARY_TABLE_SQL =
          "CREATE  TABLE " + DailySummaryTable.TABLE_NAME +
	  " (date DATE NOT NULL ,\n" +
	  "total_duration INT DEFAULT 0 ,\n" +
	  "outdoor_temp_duration INT DEFAULT 0 ,\n" +
	  "high_outdoor_temp DECIMAL(6,3) DEFAULT NULL ,\n" +
	  "high_outdoor_temp_time TIMESTAMP DEFAULT NULL ,\n" +
	  "low_outdoor_temp DECIMAL(6,3) DEFAULT NULL ,\n" +
	  "low_outdoor_temp_time TIMESTAMP DEFAULT NULL ,\n" +
	  "avg_outdoor_temp DECIMAL(6,3) DEFAULT NULL ,\n" +
	  "high_heat_index DECIMAL(6,3) DEFAULT NULL ,\n" +
	  "high_heat_index_time TIMESTAMP DEFAULT NULL ,\n" +
	  "low_wind_chill DECIMAL(6,3) DEFAULT NULL ,\n" +
	  "low_wind_chill_time TIMESTAMP DEFAULT NULL ,\n" +
	  "low_dew_point DECIMAL(6,3) DEFAULT NULL ,\n" +
	  "low_dew_point_time TIMESTAMP DEFAULT NULL ,\n" +
	  "high_dew_point DECIMAL(6,3) DEFAULT NULL ,\n" +
	  "high_dew_point_time TIMESTAMP DEFAULT NULL ,\n" +
	  "indoor_temp_duration INT DEFAULT 0 ,\n" +
	  "high_indoor_temp DECIMAL(6,3) DEFAULT NULL ,\n" +
	  "high_indoor_temp_time TIMESTAMP DEFAULT NULL ,\n" +
	  "low_indoor_temp DECIMAL(6,3) DEFAULT NULL ,\n" +
	  "low_indoor_temp_time TIMESTAMP DEFAULT NULL ,\n" +
	  "avg_indoor_temp DECIMAL(6,3) DEFAULT NULL ,\n" +
	  "outdoor_humid_duration INT DEFAULT 0 ,\n" +
	  "high_outdoor_humid DECIMAL(5,2) DEFAULT NULL ,\n" +
	  "high_outdoor_humid_time TIMESTAMP DEFAULT NULL ,\n" +
	  "low_outdoor_humid DECIMAL(5,2) DEFAULT NULL ,\n" +
	  "low_outdoor_humid_time TIMESTAMP DEFAULT NULL ,\n" +
	  "avg_outdoor_humid DECIMAL(5,2) DEFAULT NULL ,\n" +
	  "indoor_humid_duration INT DEFAULT 0 ,\n" +
	  "high_indoor_humid DECIMAL(5,2) DEFAULT NULL ,\n" +
	  "high_indoor_humid_time TIMESTAMP DEFAULT NULL ,\n" +
	  "low_indoor_humid DECIMAL(5,2) DEFAULT NULL ,\n" +
	  "low_indoor_humid_time TIMESTAMP DEFAULT NULL ,\n" +
	  "avg_indoor_humid DECIMAL(5,2) DEFAULT NULL ,\n" +
	  "baro_pressure_duration INT DEFAULT 0 ,\n" +
	  "high_baro_pressure DECIMAL(6,2) DEFAULT NULL ,\n" +
	  "high_baro_pressure_time TIMESTAMP DEFAULT NULL ,\n" +
	  "low_baro_pressure DECIMAL(6,2) DEFAULT NULL ,\n" +
	  "low_baro_pressure_time TIMESTAMP DEFAULT NULL ,\n" +
	  "avg_baro_pressure DECIMAL(6,2) DEFAULT NULL ,\n" +
	  "wind_duration INT DEFAULT 0 ,\n" +
	  "max_wind_speed DECIMAL(5,3) DEFAULT NULL ,\n" +
	  "max_wind_speed_time TIMESTAMP DEFAULT NULL ,\n" +
	  "max_wind_gust DECIMAL(5,3) DEFAULT NULL ,\n" +
	  "max_wind_gust_time TIMESTAMP DEFAULT NULL ,\n" +
	  "avg_wind_speed DECIMAL(5,3) DEFAULT NULL ,\n" +
	  "rainfall DECIMAL(7,3) DEFAULT NULL ,\n" +
	  "max_rainfall_rate DECIMAL(7,3) DEFAULT NULL ,\n" +
	  "max_rainfall_rate_time TIMESTAMP DEFAULT NULL ,\n" +
	  "avg_solar_radiation DECIMAL(4,0) DEFAULT NULL,\n" +
          "solar_radiation_duration INT DEFAULT 0,\n" +
	  "max_solar_radiation DECIMAL(4,0) DEFAULT NULL,\n" +
	  "max_solar_radiation_time TIMESTAMP DEFAULT NULL ,\n" +
	  "total_evapotranspiration DECIMAL(7,3) DEFAULT NULL ,\n" +
          "avg_uv_index DECIMAL(4,1) DEFAULT NULL,\n" +
          "uv_duration INT DEFAULT 0,\n" +
          "max_uv_index DECIMAL(4,1) DEFAULT NULL,\n" +
          "max_uv_index_time TIMESTAMP DEFAULT NULL,\n" +
	  "PRIMARY KEY (date) )";

    private static final String CREATE_WINDSLICE_TABLE_SQL =
	    "CREATE  TABLE " + WindSliceTable.TABLE_NAME +
	    "  (date DATE NOT NULL ,\n" +
	    "  wind_heading_index INT NOT NULL ,\n" +
	    "  slice_duration INT DEFAULT 0 ,\n" +
	    "  windy_duration INT DEFAULT 0 ,\n" +
	    "  total_duration INT DEFAULT 0 ,\n" +
	    "  avg_speed DECIMAL(5,3) DEFAULT NULL ,\n" +
	    "  max_speed DECIMAL(5,3) DEFAULT NULL ,\n" +
	    "  windy_percentage DECIMAL(4,1) DEFAULT NULL ,\n" +
	    "  total_percentage DECIMAL(4,1) DEFAULT NULL ,\n" +
	    "  PRIMARY KEY (date, wind_heading_index),\n" +
	    "  CONSTRAINT fk_wind_slice_daily_summary\n" +
	    "    FOREIGN KEY (date )\n" +
	    "    REFERENCES weathersense24.daily_summary (date)\n" +
	    "    ON DELETE CASCADE\n" +
	    "    ON UPDATE RESTRICT)";

    private static final String CREATE_WIND_SPEED_BIN_DURATION_TABLE_SQL = 
	    "CREATE  TABLE " + WindSliceTable.BIN_TABLE_NAME +
            " (date DATE NOT NULL ,\n" +
            " wind_heading_index INT NOT NULL ,\n" +
            " wind_speed_bin_index INT NOT NULL ,\n" +
            " duration INT DEFAULT 0 ,\n" +
            " PRIMARY KEY (date, wind_heading_index, wind_speed_bin_index) ,\n" +
            " CONSTRAINT fk_wind_slice_wind_speed_bin_duration\n" +
            "   FOREIGN KEY (date, wind_heading_index)\n" +
            "   REFERENCES weathersense24.wind_slices (date, wind_heading_index)\n" +
            "   ON DELETE CASCADE\n" +
            "   ON UPDATE RESTRICT)";

    private static final String CREATE_TEMPERATURE_BIN_DURATION_TABLE_SQL =
	    "CREATE  TABLE " + TemperatureBinDurationTable.TABLE_NAME +
	    " (date DATE NOT NULL ,\n" +
	    " temperature_bin_id INT NOT NULL ,\n" +
	    " duration INT DEFAULT 0 ,\n" +
	    //" INDEX fk_temperature_bin_duration_daily_summary_idx (date ASC) ,\n" +
	    " PRIMARY KEY (date, temperature_bin_id) ,\n" +
	    " CONSTRAINT fk_temperature_bin_duration_daily_summary\n" +
	    " FOREIGN KEY (date )\n" +
	    " REFERENCES weathersense24.daily_summary (date )\n" +
	    " ON DELETE CASCADE\n" +
	    " ON UPDATE RESTRICT)";

    private static final String CREATE_RAIN_HOUR_TABLE_SQL =
	      "CREATE  TABLE " + RainHourTable.TABLE_NAME +
	      " (date DATE NOT NULL ,\n" +
	      " day_hour INT NOT NULL ,\n" +
	      " rainfall DECIMAL(7,3) DEFAULT 0.0 ,\n" +
	      " PRIMARY KEY (date, day_hour) ,\n" +
	      //" INDEX fk_rain_hour_daily_summary_idx (date ASC) ," +
	      " CONSTRAINT fk_rain_hour_daily_summary\n" +
		" FOREIGN KEY (date )\n" +
		" REFERENCES weathersense24.daily_summary (date )\n" +
		" ON DELETE CASCADE\n" +
		" ON UPDATE NO ACTION)";
    private static final String CREATE_SENSOR_VALUE_SUMMARY_TABLE_SQL = 
              "CREATE  TABLE " + DailySummaryTable.SENSOR_SUMMARY_TABLE_NAME +
              " (date DATE NOT NULL ,\n" +
              " sensor_id INT NOT NULL ,\n" +
              " duration INT NOT NULL, \n" +
              " sensor_type VARCHAR(2) NOT NULL ,\n" +
              " high_measurement DECIMAL(6,3) ,\n" +
              " high_measurement_time TIMESTAMP ,\n" +
              " low_measurement DECIMAL(6,3) ,\n" +
              " low_measurement_time TIMESTAMP ,\n" +
              " avg_measurement DECIMAL(6,3) ,\n" +
              " PRIMARY KEY (date, sensor_id) ,\n" +
              //" INDEX fk_sensor_value_summary_idx (date ASC) ," +
              //" INDEX fk_sensor_value_summary_sensor_idx (sensor_id ASC) ," +
              " CONSTRAINT fk_sensor_value_summary\n" +
                " FOREIGN KEY (date )\n" +
                " REFERENCES weathersense24.daily_summary (date )\n" +
              // " ON DELETE CASCADE" +
              // " ON UPDATE NO ACTION," +
              // " CONSTRAINT fk_sensor_value_summary_sensor" +
              // " FOREIGN KEY (sensor_id )" +
              // " REFERENCES weathersense24.sensors (sensor_id )" +
                " ON DELETE CASCADE\n" +
                " ON UPDATE NO ACTION)";

    private static final String CREATE_TEMPERATURE_BIN_TABLE_SQL =
            "CREATE TABLE " + TemperatureBinTable.TABLE_NAME +
            "  (bin_id INT NOT NULL,\n" +
            "  bin_type VARCHAR(20) NOT NULL,\n" +
            "  threshold DECIMAL(6,3) NOT NULL DEFAULT 0,\n" +
            "  PRIMARY KEY (bin_id))";


    private static final String CREATE_WEATHER_STATION_TABLE_SQL =
            "CREATE TABLE " + WeatherStationTable.TABLE_NAME +
                  " (singleton_id INT NOT NULL DEFAULT 0,\n" +
                  "manufacturer VARCHAR(100),\n" +
                  "model VARCHAR(100) ,\n" +
                  "firmware_date VARCHAR(45) ,\n" +
                  "firmware_version VARCHAR(45) ,\n" +
                  "location_code VARCHAR(20) ,\n" +
                  "location_description VARCHAR(100) ,\n" +
                  "latitude DECIMAL(9,6) NOT NULL DEFAULT 0.0,\n" +
                  "longitude DECIMAL(9,6) NOT NULL DEFAULT 0.0,\n" +
                  "altitude DECIMAL(6,1) NOT NULL DEFAULT 0.0,\n" +
                  "weather_year_start_month INT DEFAULT 1,\n" +
                  "wind_slice_count INT NOT NULL DEFAULT 16,\n" +
                  "wind_speed_bin_interval DECIMAL(4,2) DEFAULT 5.0,\n" +
                  "num_wind_speed_bins INT DEFAULT 4,\n" +
                  "thermometer_min DECIMAL(6,3) DEFAULT -10.0,\n" +
                  "thermometer_max DECIMAL(6,3) DEFAULT 50.0,\n" +
                  "atm_pressure_min DECIMAL(6,2) DEFAULT 900.0,\n" +
                  "atm_pressure_max DECIMAL(6,2) DEFAULT 1200.0,\n" +
                  "daily_rain_max DECIMAL(7,3) DEFAULT .0,\n" +
                  "monthly_rain_max DECIMAL(7,3) DEFAULT 1000.0,\n" +
                  "yearly_rain_max DECIMAL(7,3) DEFAULT 9999.0,\n" +
                  "weather_underground_station_id VARCHAR(100) DEFAULT NULL,\n" +
                  "weather_underground_password VARCHAR(100) DEFAULT NULL,\n" +
                  "doppler_radar_url VARCHAR(100) DEFAULT NULL,\n" +
                  //"UNIQUE INDEX singleton_id_UNIQUE (singleton_id ASC),\n" +
                  "PRIMARY KEY (singleton_id))";

    private static final String CREATE_COLLECTOR_COMMAND_TABLE_SQL = 
        "CREATE TABLE " + CollectorCommandsTable.TABLE_NAME +
        " (id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),\n" +
        "command VARCHAR(100) NOT NULL,\n" +
        "state VARCHAR(20) DEFAULT 'NEW',\n" +
        "PRIMARY KEY (id))";

    private static final String CREATE_STORM_TABLE_SQL = 
        "CREATE TABLE " + StormTable.TABLE_NAME + 
        "(storm_start TIMESTAMP NOT NULL," + 
        "storm_end TIMESTAMP DEFAULT NULL," + 
        "total_rainfall decimal(7,3) NOT NULL," + 
        "PRIMARY KEY (storm_start))";

    /**
     * Constructor.
     * 
     * @param connection Connection to the database.
     */
    public DatabaseUtilities(DBConnection connection) {
        this.connection = connection;
    } 

    /**
     * Connect to the derby database.
     * 
     * @return The database connection
     */
    public static DBConnection connectToDerby() {
        DBConnection dbcon = new DBConnection(CREATE_DERBY_DB_URL, "", "", true);
        dbcon.connect();
        return dbcon;
    }

    /**
     * Create the database schema.
     * 
     * @return True if successful
     */
    public boolean createDatabaseSchema() {
        try {
            createHistoryTables();
            createSensorTable();
            createDailySummaryTables();
            createCollectorCommandsTable();
            createWeatherStationTable();
            return true;
        }
        catch (SQLException e) {
            return false;
        }
    }

    /**
     * Drop the database schema.
     */
    public void dropSchema() {
        dropHistoryTables();
        dropSensorTable();
        dropDailySummaryTables();
        dropCollectorCommandsTable();
        dropWeatherStationTable();
    }

    private void dropTable(String tableName) {
        try {
            connection.execute("drop table " + tableName);
        }
        catch (SQLException ex) {
            Logger.getLogger(DatabaseUtilities.class.getName()).log(Level.SEVERE, "Failed to drop table " + tableName, ex);
        }
    }

    /**
     * Create the tables that are history related.
     * 
     * @throws SQLException An SQL error occurred
     */
    public void createHistoryTables() throws SQLException {
        connection.execute(CREATE_HISTORY_TABLE_SQL);
        connection.execute(CREATE_SENSOR_VALUE_HISTORY_TABLE_SQL);
        connection.execute(CREATE_STORM_TABLE_SQL);
    }

    /**
     * Drop the history related tables.
     */
    public void dropHistoryTables() {
        dropTable(HistoryTable.MEASUREMENT_HISTORY_TABLE_NAME);
        dropTable(HistoryTable.TABLE_NAME);
        dropTable(StormTable.TABLE_NAME);

    }

    /**
     * Create database tables related to summaries.
     * 
     * @throws SQLException An SQL error occurred
     */
    public void createDailySummaryTables() throws SQLException {
        connection.execute(CREATE_SUMMARY_TABLE_SQL);
        connection.execute(CREATE_SENSOR_VALUE_SUMMARY_TABLE_SQL);
        connection.execute(CREATE_TEMPERATURE_BIN_TABLE_SQL);
        connection.execute(CREATE_TEMPERATURE_BIN_DURATION_TABLE_SQL);
        connection.execute(CREATE_RAIN_HOUR_TABLE_SQL);
        connection.execute(CREATE_WINDSLICE_TABLE_SQL);
        connection.execute(CREATE_WIND_SPEED_BIN_DURATION_TABLE_SQL);
    }

    /**
     * Drop the summary tables from the database.
     */
    public void dropDailySummaryTables() {
        dropTable(WindSliceTable.BIN_TABLE_NAME);
        dropTable(WindSliceTable.TABLE_NAME);
        dropTable(RainHourTable.TABLE_NAME);
        dropTable(TemperatureBinDurationTable.TABLE_NAME);
        dropTable(DailySummaryTable.SENSOR_SUMMARY_TABLE_NAME);
        dropTable(DailySummaryTable.TABLE_NAME);
        dropTable(TemperatureBinTable.TABLE_NAME);
    }

    /**
     * Create the sensor table.
     * 
     * @throws SQLException An SQL error occurred
     */
    public void createSensorTable() throws SQLException {
        connection.execute(CREATE_SENSORS_TABLE_SQL);
    }

    /**
     * Drop the sensor table.
     */
    public void dropSensorTable() {
        dropTable(SensorTable.TABLE_NAME);
    }

    /**
     * Create the weather station table.
     * 
     * @throws SQLException An SQL error occurred
     */
    public void createWeatherStationTable() throws SQLException {
        connection.execute(CREATE_WEATHER_STATION_TABLE_SQL);
    }

    /**
     * Drop the weather station table.
     */
    public void dropWeatherStationTable() {
        dropTable(WeatherStationTable.TABLE_NAME);
    }

    /**
     * Create collector commands table.
     * 
     * @throws SQLException An SQL error occurred
     */
    public void createCollectorCommandsTable() throws SQLException {
        connection.execute(CREATE_COLLECTOR_COMMAND_TABLE_SQL);
    }

    /**
     * Drop the collector commands table.
     */
    public void dropCollectorCommandsTable() {
        dropTable(CollectorCommandsTable.TABLE_NAME);
    }
}