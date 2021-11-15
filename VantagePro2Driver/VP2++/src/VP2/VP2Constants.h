/* 
 * Copyright (C) 2022 Bruce Beisel
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
#include "Weather.h"

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
    static constexpr char NULLCHAR = '\0';
    static constexpr int  NO_VALUE = 0xFF;
    static constexpr char LINE_FEED = '\n';
    static constexpr char CARRIAGE_RETURN = '\r';
    static constexpr char ACK = 0x6;
    static constexpr char NACK = 0x21; // Not an ASCII NACK, but it is what is used
    static constexpr char CANCEL = 0x18;
    static constexpr char ESCAPE = 0x15;
    static constexpr char COMMAND_TERMINATOR[] = {LINE_FEED, NULLCHAR};
    static constexpr char CRC_FAILURE[] =  {CANCEL, NULLCHAR};
    static constexpr char RESPONSE_FRAME[] = {LINE_FEED, CARRIAGE_RETURN, NULLCHAR};
    static constexpr char COMMAND_RECOGNIZED_RESPONSE[] = {LINE_FEED, CARRIAGE_RETURN, 'O', 'K', LINE_FEED, CARRIAGE_RETURN, NULLCHAR};

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
    // Common constants
    //
    static constexpr Temperature        TEMPERATURE_16BIT_SCALE = 10.0;
    static constexpr int                TEMPERATURE_16BIT_INVALID_VALUE = 32767;
    static constexpr Temperature        TEMPERATURE_8BIT_OFFSET = 90.0;
    static constexpr int                TEMPERATURE_8BIT_INVALID_VALUE = 255;
    static constexpr Pressure           BAROMETER_SCALE = 1000.0;
    static constexpr int                YEAR_OFFSET = 2000;
    static constexpr UvIndex            UV_INDEX_SCALE= 10.0;
    static constexpr Evapotranspiration ET_SCALE= 1000.0;

    static constexpr int INVALID_16BIT_TEMPERATURE = 32767;
    static constexpr int INVALID_16BIT_HIGH_TEMPERATURE = -32768;
    static constexpr int INVALID_8BIT_TEMPERATURE = 255;
    static constexpr int INVALID_HUMIDITY = 255;
    static constexpr int INVALID_WIND_DIRECTION = 255;
    static constexpr int INVALID_WIND_SPEED = 255;
    static constexpr int INVALID_UV_INDEX = 255;
    static constexpr int INVALID_LEAF_WETNESS = 255;
    static constexpr int INVALID_LEAF_TEMPERATURE = 255;
    static constexpr int INVALID_SOIL_TEMPERATURE = 255;
    static constexpr int INVALID_SOIL_MOISTURE = 255;
    static constexpr int INVALID_BAROMETER = 0;
    static constexpr int INVALID_SOLAR_RADIATION = 32767;
    static constexpr int INVALID_ET = 0;

    //
    // A wind slice is a segment of wind direction that is centered on a compass direction such
    // as N, NNE, NE, etc.
    //
    static constexpr int NUM_WIND_DIR_SLICES = 16;
    static constexpr double DEGREES_PER_SLICE = 360.0 / NUM_WIND_DIR_SLICES;
};
}

#endif	/* VP2_CONSTANTS_H */
