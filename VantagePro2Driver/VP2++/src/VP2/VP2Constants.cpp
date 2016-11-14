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
#include "VP2Constants.h"

namespace vp2 {
    //
    // Generic strings for various command protocols
    //
    const std::string VP2Constants::COMMAND_TERMINATOR = std::string(1, VP2Constants::LINE_FEED);
    const std::string VP2Constants::CRC_FAILURE = std::string(1, VP2Constants::CANCEL);
    const std::string VP2Constants::RESPONSE_FRAME = std::string(1, VP2Constants::LINE_FEED) + std::string(1, VP2Constants::CARRIAGE_RETURN);;
    const std::string VP2Constants::COMMAND_RECOGNIZED_RESPONSE = VP2Constants::RESPONSE_FRAME + "OK" + VP2Constants::RESPONSE_FRAME;

    //
    // Wakeup command/response
    //
    const std::string VP2Constants::WAKEUP_COMMAND = std::string(1, VP2Constants::LINE_FEED);
    const std::string VP2Constants::WAKEUP_RESPONSE = std::string(1, VP2Constants::LINE_FEED) + std::string(1, VP2Constants::CARRIAGE_RETURN);

    //
    // Testing Commands
    //
    const std::string VP2Constants::TEST_CMD = "TEST";
    const std::string VP2Constants::STATION_TYPE_CMD = "WRD\0x12\0x4D";
    // Console diagnostics report
    const std::string VP2Constants::RECEIVE_CHECK_CMD = "RXCHECK";
    // Move console to main current conditions screen
    const std::string VP2Constants::FORCE_CONSOLE_TO_MAIN_SCREEN_CMD = "RXTEST";
    // Firmware date
    const std::string VP2Constants::FIRMWARE_DATE_CMD = "VER";
    // Get the list of receivers as a bitmap, bit 0 represents station ID 1
    const std::string VP2Constants::RECEIVER_LIST_CMD = "RECEIVERS";
    // Get the firmware version
    const std::string VP2Constants::FIRMWARE_VERSION_CMD = "NVER";

    //
    // Current Data Commands
    //
    // Get the current data values, alarms, battery status, etc. through the LOOP packet
    const std::string VP2Constants::LOOP_CMD = "LOOP";
    // Get the current values through both the LOOP and LOOP2 packets
    const std::string VP2Constants::LPS_CMD = "LPS 3";
    // Get the high and low that includes daily, monthly and yearly
    const std::string VP2Constants::HIGH_LOW_CMD = "HILOWS";
    // Set the yearly rainfall
    const std::string VP2Constants::SET_YEARLY_RAIN_CMD = "PUTRAIN";
    // Set the yearly ET
    const std::string VP2Constants::SET_YEARLY_ET_CMD = "PUTRAIN";

    //
    // Download Commands
    //
    // Dump the entire archive
    const std::string VP2Constants::DUMP_ARCHIVE_CMD = "DMP";
    // Dump the archive after a given date/time
    const std::string VP2Constants::DUMP_AFTER_CMD = "DMPAFT";

    //
    // EEPROM Commands
    //
    // Read the entire EEPROM data block
    const std::string VP2Constants::DUMP_EEPROM_CMD = "GETEE";
    // Read EEPROM address as hex strings
    const std::string VP2Constants::READ_EEPROM_AS_HEX_CMD = "EERD";
    // Write a single byte to EEPROM as hex strings
    const std::string VP2Constants::WRITE_EEPROM_AS_HEX_CMD = "EEWR";
    // Read EEPROM address as binary
    const std::string VP2Constants::READ_EEPROM_AS_BINARY_CMD = "EEBRD";
    // Write to EEPROM as binary
    const std::string VP2Constants::WRITE_EEPROM_AS_BINARY_CMD = "EEWR";

    //
    // Calibration Commands
    //
    // Send temperature and humidity calibration values
    const std::string VP2Constants::CALIBRATE_TEMPERATURE_HUMIDITY = "CALED";
    const std::string VP2Constants::CALIBRATE_TEMPERATURE_HUMIDITY2 = "CALFIX";
    // Sets barometric offset using local reading and/or elevation
    const std::string VP2Constants::SET_BAROMETRIC_DATA_CMD = "BAR";
    // Get the current barometer calibration parameters
    const std::string VP2Constants::SET_BAROMETRIC_CAL_DATA_CMD = "BARDATA";

    //
    // Clearing Commands
    //
    // Clear the archived data
    const std::string VP2Constants::CLEAR_ARCHIVE_CMD = "CLRLOG";
    // Clear the alarm thresholds
    const std::string VP2Constants::CLEAR_ALARM_THRESHOLDS_CMD = "CLRALM";
    // Set temperature and humidity calibration offsets to zero
    const std::string VP2Constants::CLEAR_TEMPERATURE_HUMIDTY_CALIBRATIONS_CMD = "CLRCAL";
    // Clear the graph points
    const std::string VP2Constants::CLEAR_GRAPHS_CMD = "CLRGRA";
    // Clear cumulative value
    const std::string VP2Constants::CLEAR_CUMULATIVE_VALUE_CMD = "CLRVAR";
    // Clear the daily, monthly or yearly high values
    const std::string VP2Constants::CLEAR_HIGH_VALUES_CMD = "CLRHIGHS";
    // Clear the daily, monthly or yearly low values
    const std::string VP2Constants::CLEAR_LOW_VALUES_CMD = "CLRLOWS";
    // Clear active alarms
    const std::string VP2Constants::CLEAR_ACTIVE_ALARMS_CMD = "CLRBITS";
    // Clear all current data values
    const std::string VP2Constants::CLEAR_CURRENT_DATA_VALUES_CMD = "CLRDATA";

    //
    // Configuration Commands
    //
    const std::string VP2Constants::SET_BAUD_RATE_CMD = "BAUD";
    const std::string VP2Constants::SET_TIME_CMD = "SETTIME";
    const std::string VP2Constants::GET_TIME_CMD = "GETTIME";
    // Set the gain of the radio receiver. Only Vantage Pro (not VP2 or Vue)
    const std::string VP2Constants::SET_GAIN_CMD = "GAIN";
    // Set how often the console saves an archive record
    const std::string VP2Constants::SET_ARCHIVE_PERIOD_CMD = "SETPER";
    const std::string VP2Constants::STOP_ARCHIVING_CMD = "STOP";
    const std::string VP2Constants::START_ARCHIVING_CMD = "START";
    // Reinitialize the console after making any significant changes to the console's configuration
    const std::string VP2Constants::REINITIALIZE_CMD = "NEWSETUP";
    // Turn on/off the console's light
    const std::string VP2Constants::CONTROL_LAMP_CMD = "LAMPS";

    //
    // EEPROM addresses
    //
    const std::string VP2Constants::EE_LATITUDE = "0B";
    const std::string VP2Constants::EE_LONGITUDE = "0D";
    const std::string VP2Constants::EE_ELEVATION = "0F";
    const std::string VP2Constants::EE_TIMEZONE = "11";
    const std::string VP2Constants::EE_STATION_LIST = "19";
    const std::string VP2Constants::EE_SETUP_BITS = "2B";
    const std::string VP2Constants::EE_RAIN_SEASON_START = "2C";
    const std::string VP2Constants::EE_ARCHIVE_PERIOD = "2D";

    //
    // Dump/Dump After responses
    //
    const std::string VP2Constants::DMP_SEND_NEXT_PAGE = std::string(1, VP2Constants::ACK);
    const std::string VP2Constants::DMP_CANCEL_DOWNLOAD = std::string(1, VP2Constants::ESCAPE);
    const std::string VP2Constants::DMP_RESEND_PAGE = std::string(1, VP2Constants::NACK);
}