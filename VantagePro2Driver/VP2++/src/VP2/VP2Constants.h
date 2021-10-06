/* 
 * Copyright (C) 2021 Bruce Beisel
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
#ifndef VP2_CONSTANTS_H
#define	VP2_CONSTANTS_H
#include <string>

namespace vp2 {
    /**
     * This class contains all of the constants from the
     * Vantage Pro/Pro 2/Vue Serial Communications Reference Manual
     * Version 2.6.1
     * Date: 3/29/2013
     */
    class VP2Constants {
    public:
        //
        // Various values used generically in various command protocols
        //
        static const int  NO_VALUE = 0xFF;
        static const char LINE_FEED = '\n';
        static const char CARRIAGE_RETURN = '\r';
        static const char ACK = 0x6;
        static const char NACK = 0x21; // Not an ASCII NACK, but it is what is used
        static const char CANCEL = 0x18;
        static const char ESCAPE = 0x15;
        static const std::string COMMAND_TERMINATOR;
        static const std::string CRC_FAILURE;
        static const std::string RESPONSE_FRAME;
        static const std::string COMMAND_RECOGNIZED_RESPONSE;

        //
        // Wakeup command/response
        //
        static const std::string WAKEUP_COMMAND;
        static const std::string WAKEUP_RESPONSE;

        //
        // Testing Commands
        //
        static const std::string TEST_CMD;
        static const std::string STATION_TYPE_CMD;
        // Console diagnostics report
        static const std::string RECEIVE_CHECK_CMD;
        // Move console to main current conditions screen
        static const std::string FORCE_CONSOLE_TO_MAIN_SCREEN_CMD;
        // Firmware date
        static const std::string FIRMWARE_DATE_CMD;
        // Get the list of receivers as a bitmap, bit 0 represents station ID 1
        static const std::string RECEIVER_LIST_CMD;
        // Get the firmware version
        static const std::string FIRMWARE_VERSION_CMD;

        //
        // Current Data Commands
        //
        // Get the current data values, alarms, battery status, etc. through the LOOP packet
        static const std::string LOOP_CMD;
        // Get the current values through both the LOOP and LOOP2 packets
        static const std::string LPS_CMD;
        // Get the high and low that includes daily, monthly and yearly
        static const std::string HIGH_LOW_CMD;
        // Set the yearly rainfall
        static const std::string SET_YEARLY_RAIN_CMD;
        // Set the yearly ET
        static const std::string SET_YEARLY_ET_CMD;

        //
        // Download Commands
        //
        // Dump the entire archive
        static const std::string DUMP_ARCHIVE_CMD;
        // Dump the archive after a given date/time
        static const std::string DUMP_AFTER_CMD;

        //
        // EEPROM Commands
        //
        // Read the entire EEPROM data block
        static const std::string DUMP_EEPROM_CMD;
        // Read EEPROM address as hex strings
        static const std::string READ_EEPROM_AS_HEX_CMD;
        // Write a single byte to EEPROM as hex strings
        static const std::string WRITE_EEPROM_AS_HEX_CMD;
        // Read EEPROM address as binary
        static const std::string READ_EEPROM_AS_BINARY_CMD;
        // Write to EEPROM as binary
        static const std::string WRITE_EEPROM_AS_BINARY_CMD;

        //
        // Calibration Commands
        //
        // Send temperature and humidity calibration values
        static const std::string CALIBRATE_TEMPERATURE_HUMIDITY;
        static const std::string CALIBRATE_TEMPERATURE_HUMIDITY2;
        // Sets barometric offset using local reading and/or elevation
        static const std::string SET_BAROMETRIC_DATA_CMD;
        // Get the current barometer calibration parameters
        static const std::string SET_BAROMETRIC_CAL_DATA_CMD;

        //
        // Clearing Commands
        //
        // Clear the archived data
        static const std::string CLEAR_ARCHIVE_CMD;
        // Clear the alarm thresholds
        static const std::string CLEAR_ALARM_THRESHOLDS_CMD;
        // Set temperature and humidity calibration offsets to zero
        static const std::string CLEAR_TEMPERATURE_HUMIDTY_CALIBRATIONS_CMD;
        // Clear the graph points
        static const std::string CLEAR_GRAPHS_CMD;
        // Clear cumulative value
        static const std::string CLEAR_CUMULATIVE_VALUE_CMD;
        // Clear the daily, monthly or yearly high values
        static const std::string CLEAR_HIGH_VALUES_CMD;
        // Clear the daily, monthly or yearly low values
        static const std::string CLEAR_LOW_VALUES_CMD;
        // Clear active alarms
        static const std::string CLEAR_ACTIVE_ALARMS_CMD;
        // Clear all current data values
        static const std::string CLEAR_CURRENT_DATA_VALUES_CMD;

        //
        // Configuration Commands
        //
        static const std::string SET_BAUD_RATE_CMD;
        static const std::string SET_TIME_CMD;
        static const std::string GET_TIME_CMD;
        // Set the gain of the radio receiver. Only Vantage Pro (not VP2 or Vue)
        static const std::string SET_GAIN_CMD;
        // Set how often the console saves an archive record
        static const std::string SET_ARCHIVE_PERIOD_CMD;
        static const std::string STOP_ARCHIVING_CMD;
        static const std::string START_ARCHIVING_CMD;
        // Reinitialize the console after making any significant changes to the console's configuration
        static const std::string REINITIALIZE_CMD;
        // Turn on/off the console's light
        static const std::string CONTROL_LAMP_CMD;

        //
        // Station Types
        //
        static const int WIZARD_III = 0;
        static const int WIZARD_II = 1;
        static const int MONITOR = 2;
        static const int PERCEPTION = 3;
        static const int GRO_WEATHER = 4;
        static const int ENERGY_ENVIRONMONITOR = 5;
        static const int HEALTH_ENVIRONMONITOR = 6;
        static const int VANTAGE_PRO = 16;
        static const int VANTAGE_PRO2 = 16;
        static const int VANTAGE_VUE = 17;

        //
        // Cumulative Values that can be cleared using CLRVAR
        //
        static const int DAILY_RAIN_CUM = 13;
        static const int STORM_RAIN_CUM = 14;
        static const int MONTH_RAIN_CUM = 16;
        static const int YEAR_RAIN_CUM = 17;
        static const int DAY_ET_CUM = 26;
        static const int MONTH_ET_CUM = 25;
        static const int YEAR_ET_CUM = 27;

        //
        // High/Low Clear Types
        //
        static const int CLEAR_DAILY_VALUES = 0;
        static const int CLEAR_MONTHLY_VALUES = 1;
        static const int CLEAR_YEARILY_VALUES = 2;

        enum ArchivePeriod {
            ONE_MINUTE = 1,
            FIVE_MINUTES = 5,
            TEN_MINUTES = 10,
            FIFTEEN_MINUTES = 15,
            THIRTY_MINUTES = 30,
            ONE_HOUR = 60,
            TWO_HOURS = 120,
        };

        //
        // LOOP packet forecast icons
        //
        static const short RAIN_BIT = 0x1;
        static const short CLOUDY_BIT = 0x2;
        static const short PARTLY_CLOUDY_BIT = 0x4;
        static const short SUNNY_BIT = 0x8;
        static const short SNOW_BIT = 0x10;

        static const int MOSTLY_CLEAR_FORECAST = SUNNY_BIT;
        static const int PARTLY_CLOUDY_FORECAST = PARTLY_CLOUDY_BIT | CLOUDY_BIT;
        static const int MOSTLY_CLOUDY_FORECAST = CLOUDY_BIT;
        static const int MOSTLY_CLOUDY_CHANCE_OF_RAIN_FORECAST = CLOUDY_BIT | RAIN_BIT;
        static const int MOSTLY_CLOUDY_CHANCE_OF_SNOW_FORECAST = CLOUDY_BIT | SNOW_BIT;
        static const int PARTLY_CLOUDY_CHANCE_OF_RAIN_FORECAST = PARTLY_CLOUDY_BIT | RAIN_BIT;
        static const int PARTLY_CLOUDY_CHANCE_OF_SNOW_FORECAST = PARTLY_CLOUDY_BIT | SNOW_BIT;
        static const int PARTLY_CLOUDY_CHANCE_OF_RAIN_OR_SNOW_FORECAST = PARTLY_CLOUDY_BIT | RAIN_BIT | SNOW_BIT;

        //
        // EEPROM Addresses
        //
        static const std::string EE_LATITUDE;
        static const std::string EE_LONGITUDE;
        static const std::string EE_ELEVATION;
        static const std::string EE_TIMEZONE;
        static const std::string EE_STATION_LIST;
        static const std::string EE_SETUP_BITS;
        static const std::string EE_RAIN_SEASON_START;
        static const std::string EE_ARCHIVE_PERIOD;

        //
        // Maximum counts
        //
        static const int MAX_SENSOR_STATIONS = 16;
        static const int LOOP_MAX_EXTRA_TEMPERATURES = 7;
        static const int LOOP_MAX_EXTRA_HUMIDITIES = 7;
        static const int LOOP_MAX_SOIL_TEMPERATURES = 4;
        static const int LOOP_MAX_SOIL_MOISTURES = 4;
        static const int LOOP_MAX_LEAF_TEMPERATURES = 4;
        static const int LOOP_MAX_LEAF_WETNESSES = 4;

        static const int APB_MAX_EXTRA_TEMPERATURES = 3;
        static const int APB_MAX_EXTRA_HUMIDITIES = 2;
        // The serial protocol document says this is 4, but the 4th value is not set to the Dash value when there
        // are not soil temperature sensors.
        static const int APB_MAX_SOIL_TEMPERATURES = 3;
        static const int APB_MAX_SOIL_MOISTURES = 4;
        static const int APB_MAX_LEAF_WETNESSES = 2;
        static const int APB_MAX_LEAF_TEMPERATURES = 2;

        //
        // Dump/Dump After responses
        //
        static const std::string DMP_SEND_NEXT_PAGE;
        static const std::string DMP_CANCEL_DOWNLOAD;
        static const std::string DMP_RESEND_PAGE;

        static const int NUM_ARCHIVE_RECORDS = 2560;
        //
        // Archive packet (Rev B)
        //
        static const int APB_BYTES_PER_RECORD = 52;
        static const int APB_REV_A_RECORD_TYPE = 0xFF;
        static const int APB_REV_B_RECORD_TYPE = 0;
        static const int APB_DATE_STAMP_OFFSET = 0;
        static const int APB_TIME_STAMP_OFFSET = 2;
        static const int APB_OUTSIDE_TEMPERATURE_OFFSET = 4;
        static const int APB_HIGH_OUTSIDE_TEMPERATURE_OFFSET = 6;
        static const int APB_LOW_OUTSIDE_TEMPERATURE_OFFSET = 8;
        static const int APB_RAINFALL_OFFSET = 10;
        static const int APB_HIGH_RAIN_RATE_OFFSET = 12;
        static const int APB_BAROMETER_OFFSET = 14;
        static const int APB_SOLAR_RADIATION_OFFSET = 16;
        static const int APB_NUM_WIND_SAMPLES_OFFSET = 18;
        static const int APB_INSIDE_TEMPERATURE_OFFSET = 20;
        static const int APB_INSIDE_HUMIDITY_OFFSET = 22;
        static const int APB_OUTSIDE_HUMIDITY_OFFSET = 23;
        static const int APB_AVG_WIND_SPEED_OFFSET = 24;
        static const int APB_HIGH_WIND_SPEED_OFFSET = 25;
        static const int APB_DIR_OF_HIGH_WIND_SPEED_OFFSET = 26;
        static const int APB_PREVAILING_WIND_DIRECTION_OFFSET = 27;
        static const int APB_AVG_UV_INDEX_OFFSET = 28;
        static const int APB_ET_OFFSET = 29;
        static const int APB_HIGH_SOLAR_RADIATION_OFFSET = 30;
        static const int APB_HIGH_UV_INDEX_OFFSET = 32;
        static const int APB_FORECAST_RULE_OFFSET = 33;
        static const int APB_LEAF_TEMPERATURE_BASE_OFFSET = 34;
        static const int APB_LEAF_WETNESS_BASE_OFFSET = 36;
        static const int APB_SOIL_TEMPERATURE_BASE_OFFSET = 38;
        static const int APB_RECORD_TYPE_OFFSET = 42;
        static const int APB_EXTRA_HUMIDITIES_BASE_OFFSET = 43;
        static const int APB_EXTRA_TEMPERATURES_BASE_OFFSET = 45;
        static const int APB_SOIL_MOISTURES_BASE_OFFSET = 48;

        static const int APB_EXTRA_TEMPERATURE_OFFSET = 90;

        static const int APB_INVALID_TEMPERATURE = 32767;
        static const int APB_INVALID_HIGH_TEMPERATURE = -32768;
        static const int APB_INVALID_EXTRA_TEMPERATURE = 255;
        static const int APB_INVALID_HUMIDITY = 255;
        static const int APB_INVALID_WIND_DIRECTION = 255;
        static const int APB_INVALID_WIND_SPEED = 255;
        static const int APB_INVALID_UV_INDEX = 255;
        static const int APB_INVALID_LEAF_WETNESS = 255;
        static const int APB_INVALID_LEAF_TEMPERATURE = 255;
        static const int APB_INVALID_SOIL_TEMPERATURE = 255;
        static const int APB_INVALID_SOIL_MOISTURE = 255;
        static const int APB_INVALID_BAROMETER = 0;
        static const int APB_INVALID_SOLAR_RADIATION = 32767;

        static const int APB_MIN_LEAF_WETNESS = 0;
        static const int APB_MAX_LEAF_WETNESS = 15;

        static const int APB_TEMPERATURE_SCALE = 10;
        static const int APB_AVG_UV_INDEX_SCALE = 10;
        static const int APB_ET_SCALE = 1000;
        static const int APB_BAROMETER_SCALE = 1000;
    };
}

#endif	/* VP2_CONSTANTS_H */
