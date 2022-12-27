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
#ifdef _WIN32
#include <windows.h>
#else
#include <unistd.h>
#endif
#include <time.h>
#include <math.h>
#include <sstream>
#include <cstring>
#include "VP2Constants.h"
#include "CurrentWeather.h"
#include "HiLowPacket.h"
#include "VantagePro2CRC.h"
#include "BitConverter.h"
#include "ProtocolException.h"
#include "VantagePro2Station.h"

using namespace std;

namespace vp2 {
//
// Wakeup command/response
//
static const std::string WAKEUP_COMMAND = std::string(1, VP2Constants::LINE_FEED);
static const std::string WAKEUP_RESPONSE = std::string(1, VP2Constants::LINE_FEED) + std::string(1, VP2Constants::CARRIAGE_RETURN);

//
// Testing Commands
//
static const std::string TEST_CMD = "TEST";                          // Sends the string "TEST\n" back
//static const std::string STATION_TYPE_CMD = "WRD\0x12\0x4D";       // Responds with a weather station type that is backward compatible with earlier Davis weather products
static const std::string RECEIVE_CHECK_CMD = "RXCHECK";              // Console diagnostics report
static const std::string RXTEST_CMD = "RXTEST";                      // Move console to main current conditions screen
static const std::string FIRMWARE_DATE_CMD = "VER";                  // Firmware date
static const std::string RECEIVER_LIST_CMD = "RECEIVERS";            // Get the list of receivers as a bitmap, bit 0 represents station ID 1
static const std::string FIRMWARE_VERSION_CMD = "NVER";              // Get the firmware version

//
// Current Data Commands
//
//static const std::string LOOP_CMD = "LOOP";                        // Get the current data values, alarms, battery status, etc. through the LOOP packet. Note that the LPS command renders this superfluous.
static const std::string LPS_CMD = "LPS 3";                          // Get the current values through both the LOOP and LOOP2 packets
static const std::string HIGH_LOW_CMD = "HILOWS";                    // Get the high and low that includes daily, monthly and yearly
static const std::string PUT_YEARLY_RAIN_CMD = "PUTRAIN";            // Set the yearly rainfall
static const std::string PUT_YEARLY_ET_CMD = "PUTET";                // Set the yearly ET

//
// Download Commands
//
static const std::string DUMP_ARCHIVE_CMD = "DMP";                   // Dump the entire archive
static const std::string DUMP_AFTER_CMD = "DMPAFT";                  // Dump the archive after a given date/time


//
// EEPROM Commands
//
static const std::string DUMP_EEPROM_CMD = "GETEE";                  // Read the entire EEPROM data block
static const std::string WRITE_EEPROM_CMD = "EEWR";                  // Write a single byte to EEPROM as hex strings
static const std::string READ_EEPROM_CMD = "EERD";                   // Read EEPROM address as hex strings
static const std::string WRITE_EEPROM_AS_BINARY_CMD = "EEBWR";       // Write to EEPROM as binary
static const std::string READ_EEPROM_AS_BINARY_CMD = "EEBRD";        // Read EEPROM address as binary

//
// Calibration Commands
//
//static const std::string CALIBRATE_TEMPERATURE_HUMIDITY = "CALED";   // Send temperature and humidity calibration values
//static const std::string CALIBRATE_TEMPERATURE_HUMIDITY2 = "CALFIX"; // Updates the display when calibration numbers have changed
//static const std::string SET_BAROMETRIC_DATA_CMD = "BAR";            // Sets barometric offset using local reading and/or elevation
//static const std::string SET_BAROMETRIC_CAL_DATA_CMD = "BARDATA";    // Get the current barometer calibration parameters

//
// Clearing Commands
//
static const std::string CLEAR_ARCHIVE_CMD = "CLRLOG";               // Clear the archived data
static const std::string CLEAR_ALARM_THRESHOLDS_CMD = "CLRALM";      // Clear the alarm thresholds
static const std::string CLEAR_TEMP_HUMID_CAL_CMD = "CLRCAL";        // Set temperature and humidity calibration offsets to zero
static const std::string CLEAR_GRAPH_POINTS_CMD = "CLRGRA";          // Clear the graph points
static const std::string CLEAR_CUMULATIVE_VALUE_CMD = "CLRVAR";      // Clear the specified cumulative value
static const std::string CLEAR_HIGH_VALUES_CMD = "CLRHIGHS";         // Clear the daily, monthly or yearly high values
static const std::string CLEAR_LOW_VALUES_CMD = "CLRLOWS";           // Clear the daily, monthly or yearly low values
static const std::string CLEAR_ACTIVE_ALARMS_CMD = "CLRBITS";        // Clear active alarms
static const std::string CLEAR_CURRENT_DATA_VALUES_CMD = "CLRDATA";  // Clear all current data values

//
// Configuration Commands
//
static const std::string SET_BAUD_RATE_CMD = "BAUD";                 // Sets the console to a new baud rate. Valid values are 1200, 2400, 4800, 9600, 14400 and 19200
static const std::string SET_TIME_CMD = "SETTIME";                   // Sets the time and date on the console
static const std::string GET_TIME_CMD = "GETTIME";                   // Retrieves the current time and date on the Vantage console. Data is sent in binary format
static const std::string SET_ARCHIVE_PERIOD_CMD = "SETPER";          // Set how often the console saves an archive record
//static const std::string STOP_ARCHIVING_CMD = "STOP";              // Disables the creation of archive records
//static const std::string START_ARCHIVING_CMD = "START";            // Enables the create of archive records
static const std::string REINITIALIZE_CMD = "NEWSETUP";              // Reinitialize the console after making any significant changes to the console's configuration
static const std::string CONTROL_LAMP_CMD = "LAMPS";                 // Turn on/off the console's light

//
// Dump/Dump After responses
//
static const std::string DMP_SEND_NEXT_PAGE = std::string(1, VP2Constants::ACK);
static const std::string DMP_CANCEL_DOWNLOAD = std::string(1, VP2Constants::ESCAPE);
static const std::string DMP_RESEND_PAGE = std::string(1, VP2Constants::NACK);

//
// Generic strings for various command protocols
//
static const std::string COMMAND_TERMINATOR = std::string(1, VP2Constants::LINE_FEED);
//static const std::string CRC_FAILURE = std::string(1, VP2Constants::CANCEL);
static const std::string RESPONSE_FRAME = std::string(1, VP2Constants::LINE_FEED) + std::string(1, VP2Constants::CARRIAGE_RETURN);;
static const std::string COMMAND_RECOGNIZED_RESPONSE = RESPONSE_FRAME + "OK" + RESPONSE_FRAME;
static const std::string DONE_RESPONSE = "DONE" + std::string(1, VP2Constants::LINE_FEED) + std::string(1, VP2Constants::CARRIAGE_RETURN);;
static const std::string TEST_RESPONSE = "TEST" + std::string(1, VP2Constants::LINE_FEED) + std::string(1, VP2Constants::CARRIAGE_RETURN);;

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
VantagePro2Station::VantagePro2Station(const string & portName, int baudRate) :
                                            serialPort(portName, baudRate),
                                            callback(nullptr),
                                            consoleBatteryVoltage(0.0),
                                            baudRate(baudRate),
                                            rainCollectorSize(0.0),
                                            archivePeriod(0),
                                            windSensorStationId(0),
                                            log(VP2Logger::getLogger("VantagePro2Station")),
                                            firstLoopPacketReceived(false) {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
VantagePro2Station::~VantagePro2Station() {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
VantagePro2Station::setCallback(Callback & callback) {
    this->callback = &callback;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::openStation() {
    if (!serialPort.open())
        return false;
    else
        return initialize();
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
VantagePro2Station::closeStation() {
    serialPort.close();
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::wakeupStation() {
    bool awake = false;

    for (int i = 0; i < WAKEUP_TRIES && !awake; i++) {
        log.log(VP2Logger::VP2_DEBUG1) << "Attempting to wakeup console" << endl;
        serialPort.write(WAKEUP_COMMAND);
        Weather::sleep(WAKEUP_WAIT);
      
        //
        // After sending the wakeup command the console will respond with <LF><CR>
        //
        //if (serialPort.read(buffer, 2) && (char)buffer[0] == VP2Constants::LINE_FEED && (char)buffer[1] == VP2Constants::CARRIAGE_RETURN) {
        if (serialPort.read(buffer, 2) && buffer[0] == WAKEUP_RESPONSE[0] && buffer[1] == WAKEUP_RESPONSE[1]) {
            awake = true;
            log.log(VP2Logger::VP2_INFO) << "Console is awake" << endl;
        }
        else
            serialPort.discardInBuffer();
    }

    return awake;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::initialize() {
    if (!wakeupStation())
        return false;

    if (!retrieveSensorStationInfo())
        return false;

    if (!eepromBinaryRead(VP2Constants::EE_SETUP_BITS, 1))
        return false;

    int setupBits = BitConverter::toInt8(buffer, 0);

    VP2Constants::RainCupSizeType type = static_cast<VP2Constants::RainCupSizeType>((setupBits & 0x30) >> 4);

    switch (type) {
        case VP2Constants::RainCupSizeType::POINT_01_INCH:
            rainCollectorSize = VP2Constants::POINT_01_INCH_SIZE;
            break;
        case VP2Constants::RainCupSizeType::POINT_02_MM:
            rainCollectorSize = VP2Constants::POINT_02_MM_SIZE;
            break;
        case VP2Constants::RainCupSizeType::POINT_01_MM:
            rainCollectorSize = VP2Constants::POINT_01_MM_SIZE;
            break;
    }

    if (!eepromBinaryRead(VP2Constants::EE_ARCHIVE_PERIOD, 1))
        return false;

    archivePeriod = BitConverter::toInt8(buffer, 0);

    log.log(VP2Logger::VP2_INFO) << "Initialize results: Rain Collector Size: " << rainCollectorSize << " Archive Period: " << archivePeriod << endl;

    return true;
}

//
// Testing Commands
//

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::sendTestCommand() {
    serialPort.write(TEST_CMD);
    serialPort.write(COMMAND_TERMINATOR);

    if (serialPort.read(buffer, TEST_RESPONSE.length())) {
        log.log(VP2Logger::VP2_WARNING) << "sendTestCommand() read failed while waiting for test response" << endl;
        return false;
    }

    if (TEST_RESPONSE != buffer) {
        log.log(VP2Logger::VP2_WARNING) << "sendTestCommand() received unexpected test response: '" << buffer << "'" << endl;
        return false;
    }

    return true;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::retrieveConsoleDiagnosticsReport(ConsoleDiagnosticReport & report) {
    string response;

    if (!sendStringValueCommand(RECEIVE_CHECK_CMD, response))
        return false;

    sscanf(response.c_str(), "%d %d %d %d %d", &report.packetCount,
                                               &report.missedPacketCount,
                                               &report.syncCount,
                                               &report.maxPacketSequence,
                                               &report.crcErrorCount);

    return true;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::performReceiveTest() {
    //
    // There is no documentation in the serial protocol document regarding this
    // command. It does not state if a command terminator must be added or if
    // there is any response.
    //
    serialPort.write(RXTEST_CMD);
    serialPort.write(COMMAND_TERMINATOR);

    return true;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::retrieveFirmwareVersion(std::string * fwVersion) {
    log.log(VP2Logger::VP2_INFO) << "Retrieving firmware version" << endl;
    if (!sendStringValueCommand(FIRMWARE_VERSION_CMD, firmwareVersion))
        return false;

    if (fwVersion != nullptr)
        *fwVersion = firmwareVersion;

    return true;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::retrieveReceiverList(std::vector<StationId> * sensorStations) {

    if (!sendOKedCommand(RECEIVER_LIST_CMD))
       return false;

    byte stations;
    if (!serialPort.read(&stations, 1))
        return false;

    if (sensorStations != nullptr)
        sensorStations->clear();

    stationIds.clear();

    for (int i = 0; i < MAX_STATION_ID; i++) {
        if (stations & (1 << i) != 0) {
            if (sensorStations != nullptr)
                sensorStations->push_back(i + 1);

            stationIds.push_back(i + 1);
        }
    }

    return true;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::retrieveFirmwareDate(std::string * fwDate) {
    log.log(VP2Logger::VP2_INFO) << "Retrieving firmware date" << endl;

    if (!sendStringValueCommand(FIRMWARE_DATE_CMD, firmwareDate))
        return false;

    if (fwDate != nullptr)
        *fwDate = firmwareDate;

    return true;
}

//
// End of Testing Commands
//

//
// Current Data Commands
//

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
VantagePro2Station::currentValuesLoop(int records) {
    LoopPacket loopPacket;
    Loop2Packet loop2Packet;
    bool terminateLoop = false;
    bool resetNeeded = false;

    ostringstream command;
    command << LPS_CMD << " " << (records * 2);

    if (!sendAckedCommand(command.str()))
        return;

    for (int i = 0; i < records && !terminateLoop; i++) {
        log.log(VP2Logger::VP2_DEBUG1) << "Getting Current Weather ---------------------------------" << endl;
        //
        // Loop packet comes first
        //
        if (!readLoopPacket(loopPacket)) {
            resetNeeded = true;
            break;
        }

        //
        // Per the Vantage Pro serial communication document, sleep 2 seconds between
        // the loop the loop 2 packets
        //
        Weather::sleep(LOOP_PACKET_WAIT);
        if (!readLoop2Packet(loop2Packet)) {
            resetNeeded = true;
            break;
        }

        //
        // Build a current weather message from the loop packets
        //
        currentWeather.setData(loopPacket, loop2Packet, pastWindDirs);

        //
        // Build a list of past wind directions. This is to mimic what is shown on the
        // console
        //
        pastWindDirs.addHeading(loopPacket.getWindDirection());

        //
        // Send the message for processing
        //
        terminateLoop = callback->processCurrentWeather(currentWeather);

        //
        // Per the Vantage Pro serial communication document, sleep 2 seconds between
        // the loop 2 the loop packets
        //
        if (!terminateLoop)
            Weather::sleep(LOOP_PACKET_WAIT);
       
        log.log(VP2Logger::VP2_INFO) << "Retrieved Current Weather" << endl;
    }
    //
    // If the callback wants to terminated the loop early or there was a problem use the Wakeup sequence to terminate the loop
    // See the LPS command in the Vantage Pro 2 Serial Protocol document
    //
    if (terminateLoop || resetNeeded)
        wakeupStation();
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::retrieveHiLowValues(HiLowPacket & packet) {
    log.log(VP2Logger::VP2_DEBUG1) << "Retrieving Hi/Low packet" << endl;

    if (!sendAckedCommand(HIGH_LOW_CMD))
        return false;

    if (!serialPort.read(buffer, HILOW_PACKET_SIZE + CRC_BYTES)) {
        log.log(VP2Logger::VP2_ERROR) << "Failed to read response to HILOWS command" << endl;
        return false;
    }

    packet.decodeHiLowPacket(buffer);
    cout << "Hi/Low packet:" << endl << packet.formatXML() << endl;
    return true;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::putYearlyRain(Rainfall rain) {
    ostringstream ss;

    int argument = round(rain * 100.0);

    ss << PUT_YEARLY_RAIN_CMD << " " << argument;

    return sendAckedCommand(ss.str());
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::putYearlyET(Evapotranspiration et) {
    ostringstream ss;

    int argument = round(et * VP2Constants::MONTH_YEAR_ET_SCALE);

    ss << PUT_YEARLY_ET_CMD << " " << argument;

    return sendAckedCommand(ss.str());
}
//
// End Current Data Commands
//

//
// Download Commands
//

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
VantagePro2Station::dump(vector<ArchivePacket> & list) {
    log.log(VP2Logger::VP2_INFO) << "Dumping archive..." << endl;
    list.clear();

    if (sendAckedCommand(DUMP_ARCHIVE_CMD)) {
        for (int i = 0; i < NUM_ARCHIVE_PAGES; i++) {
            readNextArchivePage(list, 0, time(0));
        }
    }
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::dumpAfter(DateTime time, vector<ArchivePacket> & list) {
    log.log(VP2Logger::VP2_DEBUG1) << "Dumping after " << Weather::formatDateTime(time) << endl;
    list.clear();

    //
    // First send the dump after command and get an ACK back
    //
    if (!sendAckedCommand(DUMP_AFTER_CMD))
        return false;

    //
    // Next send the date with a checksum
    //
    struct tm tm;
    Weather::localtime(time, tm);
    int datestamp = tm.tm_mday + ((tm.tm_mon + 1) * 32) + ((tm.tm_year + 1900 - VP2_YEAR_OFFSET) * 512);
    int timestamp = (tm.tm_hour * 100) + tm.tm_min;

    byte dateTimeBytes[TIME_LENGTH + CRC_BYTES];
    BitConverter::getBytes(datestamp, dateTimeBytes, 0, 2);
    BitConverter::getBytes(timestamp, dateTimeBytes, 2, 2);

    int crc = VantagePro2CRC::calculateCRC(dateTimeBytes, TIME_LENGTH);
    BitConverter::getBytes(crc, dateTimeBytes, TIME_LENGTH, CRC_BYTES, false);

    serialPort.write(dateTimeBytes, TIME_LENGTH + CRC_BYTES);

    //
    // Another ACK
    //
    if (!consumeAck()) {
        log.log(VP2Logger::VP2_WARNING) << "Canceling DUMPAFT due to CRC failure" << endl
                                        << Weather::dumpBuffer(dateTimeBytes, sizeof(dateTimeBytes));
        return false;
    }

    //
    // Now the console sends 4 bytes indicating the number of pages to be
    // dumped and which record in the first page is valid for the date specified
    //
    if (!serialPort.read(buffer, DUMP_AFTER_RESPONSE_LENGTH + CRC_BYTES)) {
        log.log(VP2Logger::VP2_ERROR) << "Failed to read response to DMPAFT command" << endl;
        return false;
    }

    if (!VantagePro2CRC::checkCRC(buffer, DUMP_AFTER_RESPONSE_LENGTH)) {
        log.log(VP2Logger::VP2_ERROR) << "Response to DMPAFT command failed CRC check" << endl;
        return false;
    }

    serialPort.write(string(1, VP2Constants::ACK));

    int numPages = BitConverter::toInt16(buffer, 0);
    int firstRecord = BitConverter::toInt16(buffer, 2);
    log.log(VP2Logger::VP2_DEBUG1) << "Dumping " << numPages << " pages. First Record = " << firstRecord << endl;

    if (numPages == 0)
        return true;

    return readAfterArchivePages(time, list, numPages, firstRecord);
}

//
// EEPROM Commands
//

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::eepromReadDataBlock(byte buffer[]) {
    if (!sendAckedCommand(DUMP_EEPROM_CMD))
        return false;

    if (!serialPort.read(buffer, EEPROM_DATA_BLOCK_SIZE + CRC_BYTES) || !VantagePro2CRC::checkCRC(buffer, EEPROM_DATA_BLOCK_SIZE))
        return false;

    return true;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::eepromRead(unsigned address, unsigned count) {
    ostringstream command;
    command << READ_EEPROM_CMD << " " << uppercase << hex << address << " " << count << nouppercase;
    if (!sendOKedCommand(command.str()))
        return false;

    //
    // Read four bytes at a time, each read will contain a 2 digit hex code and a <LF><CR> sequence.
    //
    for (int i = 0; i < count; i++) {
        if (serialPort.read(this->buffer, EEPROM_READ_LINE_LENGTH) &&
            this->buffer[EEPROM_READ_LINE_LENGTH - 2] == VP2Constants::LINE_FEED &&
            this->buffer[EEPROM_READ_LINE_LENGTH - 1] == VP2Constants::CARRIAGE_RETURN) {

            int value = strtol(this->buffer, nullptr, 16);
            buffer[i] = value & BitConverter::ONE_BYTE_MASK;
        }
        else
            return false;
    }

    return true;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::eepromBinaryRead(unsigned address, unsigned count, char * output) {
    ostringstream command;
    command << READ_EEPROM_AS_BINARY_CMD << " " << uppercase << hex << address << " " << count << nouppercase;

    if (!sendAckedCommand(command.str()))
        return false;

    if (!serialPort.read(buffer, count + CRC_BYTES) || !VantagePro2CRC::checkCRC(buffer, count))
        return false;

    if (output != nullptr)
        memcpy(output, buffer, count);

    return true;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::eepromWriteByte(unsigned address, byte value) {
    ostringstream command;
    command << WRITE_EEPROM_CMD << " " << hex << address << " " << value;
    return sendOKedCommand(command.str());
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool 
VantagePro2Station::eepromBinaryWrite(unsigned address, const byte data[], unsigned count) {
    ostringstream command;
    command << WRITE_EEPROM_AS_BINARY_CMD << " " << uppercase << hex << address << " " << count << nouppercase;

    if (!sendAckedCommand(command.str()))
        return false;

    byte writeBuffer[EEPROM_DATA_BLOCK_SIZE + CRC_BYTES];
    memcpy(writeBuffer, data, count);

    int crc = VantagePro2CRC::calculateCRC(data, count);

    BitConverter::getBytes(crc, writeBuffer, count, CRC_BYTES, false);

    serialPort.write(writeBuffer, count + CRC_BYTES);

    return true;
}

//
// Calibration Commands
//

//
// Clearing commands
//

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::clearArchive() {
    return sendAckedCommand(CLEAR_ARCHIVE_CMD);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::clearAlarmThresholds() {
    return sendOKedWithDoneCommand(CLEAR_ALARM_THRESHOLDS_CMD);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::clearTemperatureHumidityCalibrationOffsets() {
    // The Vantage Pro2 protocol document indicates that the leading <LF><CR>
    // is not sent in response to the CLRCAL command. This needs to be tested
    // to determine if this is an error in the document or an inconsistency
    // with the protocol
    return sendOKedWithDoneCommand(CLEAR_TEMP_HUMID_CAL_CMD);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::clearGraphPoints() {
    // See the comment in clearTemperatureHumidityCalibrationOffsets()
    return sendOKedWithDoneCommand(CLEAR_GRAPH_POINTS_CMD);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::clearCumulativeValue(VP2Constants::CumulativeValue cumValue) {
    ostringstream ss;

    ss << CLEAR_CUMULATIVE_VALUE_CMD << " " << static_cast<int>(cumValue);

    return sendAckedCommand(ss.str());
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::clearHighValues(VP2Constants::ExtremePeriod period) {
    ostringstream command;
    command << CLEAR_HIGH_VALUES_CMD << " " << static_cast<int>(period);

    return sendAckedCommand(command.str());
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::clearLowValues(VP2Constants::ExtremePeriod period) {
    ostringstream command;
    command << CLEAR_LOW_VALUES_CMD << " " << static_cast<int>(period);

    return sendAckedCommand(command.str());
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::clearActiveAlarms() {
    return sendAckedCommand(CLEAR_ACTIVE_ALARMS_CMD);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::clearCurrentData() {
    return sendAckedCommand(CLEAR_CURRENT_DATA_VALUES_CMD);
}

//
// End of Clearing Commands
//

//
// Configuration Commands
//

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::updateBaudRate(VP2Constants::BaudRate baudRate) {
    ostringstream command;
    command << SET_BAUD_RATE_CMD << " " << static_cast<int>(baudRate);

    //
    // First set the console's baud rate, then reopen the serial port with the
    // new baud rate
    //
    if (sendOKedCommand(CLEAR_CURRENT_DATA_VALUES_CMD)) {
        serialPort.close();
        serialPort.setBaudRate(static_cast<int>(baudRate));
        serialPort.open();
    }

    return true;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::updateConsoleTime() {
    if (!sendAckedCommand(SET_TIME_CMD))
        return false;

    time_t now = time(0);
    struct tm tm;
    Weather::localtime(now, tm);
    log.log(VP2Logger::VP2_INFO) << "Setting console time to " << Weather::formatDateTime(now) << endl;
    int n = 0;
    buffer[n++] = static_cast<byte>(tm.tm_sec);
    buffer[n++] = static_cast<byte>(tm.tm_min);
    buffer[n++] = static_cast<byte>(tm.tm_hour);
    buffer[n++] = static_cast<byte>(tm.tm_mday);
    buffer[n++] = static_cast<byte>(tm.tm_mon + 1);
    buffer[n] = static_cast<byte>(tm.tm_year);

    int crc = VantagePro2CRC::calculateCRC(buffer, SET_TIME_LENGTH);
    BitConverter::getBytes(crc, buffer, SET_TIME_LENGTH, CRC_BYTES, false);

    serialPort.write(buffer, SET_TIME_LENGTH + CRC_BYTES);

    return consumeAck();
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::retrieveConsoleTime(DateTime & stationTime) {
    stationTime = 0;

    if (!sendAckedCommand(GET_TIME_CMD))
        return false;

    if (serialPort.read(buffer, TIME_RESPONSE_LENGTH + CRC_BYTES) && VantagePro2CRC::checkCRC(buffer, TIME_RESPONSE_LENGTH)) {
        time_t now = time(0);
        struct tm tm;
        Weather::localtime(now, tm);
        int n = 0;
        tm.tm_sec = buffer[n++];
        tm.tm_min = buffer[n++];
        tm.tm_hour = buffer[n++];
        tm.tm_mday = buffer[n++];
        tm.tm_mon = buffer[n++] - 1;
        tm.tm_year = buffer[n];
        stationTime = mktime(&tm);
    }

    return true;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::updateArchivePeriod(VP2Constants::ArchivePeriod period) {
    ostringstream command;
    command << SET_ARCHIVE_PERIOD_CMD << " " << static_cast<int>(period);
    log.log(VP2Logger::VP2_INFO) << "Updating archive period to: " << period << endl;

    return sendAckedCommand(command.str());
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::initializeSetup() {
    log.log(VP2Logger::VP2_INFO) << "Reinitializing console" << endl;
    return sendAckedCommand(REINITIALIZE_CMD);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::controlConsoleLamp(bool on) {
    ostringstream command;
    command << CONTROL_LAMP_CMD << " " << (on ? "1" : "0");

    log.log(VP2Logger::VP2_INFO) << "Sending lamp command: " << (on ? "On" : "Off") << endl;
    return sendOKedCommand(command.str());
}

//
// EEPROM retrieval commands
//

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const vector<SensorStation> &
VantagePro2Station::getSensorStations() const {
    return sensorStations;
}

/*
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const vector<Sensor> &
VantagePro2Station::getSensors() const {
    return sensors;
}
 */

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
struct StationData {
    SensorStation::RepeaterId        repeaterId;
    SensorStation::SensorStationType stationType;
    int                              humiditySensorNumber;
    int                              temperatureSensorNumber;
};

bool
VantagePro2Station::retrieveSensorStationInfo() {
    log.log(VP2Logger::VP2_INFO) << "Retrieving sensor station information" << endl;

    if (!eepromBinaryRead(VP2Constants::EE_STATION_LIST, STATION_DATA_SIZE))
        return false;

    StationData data[MAX_STATION_ID];
    for (int i = 0; i < MAX_STATION_ID; i++) {
        data[i].repeaterId = static_cast<SensorStation::RepeaterId>(BitConverter::getUpperNibble(buffer[i * 2]));
        data[i].stationType = static_cast<SensorStation::SensorStationType>(BitConverter::getLowerNibble(buffer[i * 2]));
        data[i].humiditySensorNumber = BitConverter::getUpperNibble(buffer[(i * 2) + 1]);
        data[i].temperatureSensorNumber = BitConverter::getLowerNibble(buffer[(i * 2) + 1]);
        if (data[i].stationType == SensorStation::ANEMOMETER)
            windSensorStationId = i + 1;
        else if (data[i].stationType == SensorStation::INTEGRATED_SENSOR_STATION && windSensorStationId == 0)
            windSensorStationId = i + 1;

    }
    for (int i = 0; i < MAX_STATION_ID; i++) {
        if (data[i].stationType != SensorStation::NO_STATION) {
            bool hasAnemometer = (i + 1) == windSensorStationId;
            sensorStations.push_back(SensorStation(data[i].stationType, i + 1, data[i].repeaterId, hasAnemometer));
        }
    }

    cout << "@@@@@@@@@@ Station Data:" << endl;
    cout << "@@@@@@@@@@ Wind Sensor Station ID: " << windSensorStationId << endl;
    for (int i = 0; i < MAX_STATION_ID; i++) {
        cout << "@@@@@@@@@@ [" << i << "] Repeater ID: " << data[i].repeaterId
             << " Station Type: " << data[i].stationType
             << " Humidity Sensor: " << data[i].humiditySensorNumber
             << " Temperature Sensor: " << data[i].temperatureSensorNumber << endl;

    }
    /*
    for (vector<SensorStation>::iterator it = sensorStations.begin(); it != sensorStations.end(); ++it)
        log.log(VP2Logger::VP2_DEBUG1) << *it << endl;
    */

    return true;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
int
VantagePro2Station::calculateStationReceptionPercentage(int archivePacketWindSamples) const {
    static const int stationId = 1;

    float archivePeriodSeconds = archivePeriod * 60.0F;
    float stationIndex = stationId - 1.0F;
    int maxPackets = static_cast<int>(archivePeriodSeconds / ((41.0F + stationIndex) / 16.0F));

    int stationReception = (archivePacketWindSamples * 100) / maxPackets;
    if (stationReception > MAX_STATION_RECEPTION)
        stationReception = MAX_STATION_RECEPTION;

    return stationReception;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const CurrentWeather &
VantagePro2Station::getCurrentWeather() const {
    return currentWeather;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Rainfall
VantagePro2Station::getRainCollectorSize() const {
    return rainCollectorSize;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::readLoopPacket(LoopPacket & loopPacket) {
    log.log(VP2Logger::VP2_DEBUG1) << "Reading LOOP Packet" << endl;

    //
    // Read and decode the LOOP packet
    //
    if (!serialPort.read(buffer, LOOP_PACKET_SIZE))
        return false;

    if (!loopPacket.decodeLoopPacket(buffer))
        return false;

    //
    // First time through determine what sensors are attached to the weather station based on the valid data in
    // the LOOP packet.
    //
    if (!firstLoopPacketReceived) {
        firstLoopPacketReceived = true;
        //Sensor::detectSensors(loopPacket, sensors);
    }

    //
    // Save the battery voltage of the console
    //
    consoleBatteryVoltage = loopPacket.getConsoleBatteryVoltage();

    //
    // Pull out the battery status for the sensor stations
    //
    //for (vector<SensorStation>::iterator it = sensorStations.begin(); it != sensorStations.end(); ++it) {
    //    it->setBatteryStatus(loopPacket.isTransmitterBatteryGood(it->getSensorIndex()));
    //}

    log.log(VP2Logger::VP2_DEBUG1) << "LOOP packet read successfully" << endl;
    return true;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::readLoop2Packet(Loop2Packet & loop2Packet) {
    log.log(VP2Logger::VP2_DEBUG1) << "Reading LOOP2 Packet" << endl;
    if (!serialPort.read(buffer, LOOP_PACKET_SIZE))
        return false;

    if (!loop2Packet.decodeLoop2Packet(buffer))
        return false;

    log.log(VP2Logger::VP2_DEBUG1) << "LOOP2 packet read successfully" << endl;
    return true;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::readAfterArchivePages(DateTime afterTime, vector<ArchivePacket> & list, int firstRecord, int numPages) {
    DateTime newestPacketTime = afterTime;

    bool rv = true;
    for (int i = 0; i < numPages; i++) {
        //
        // Process a single page. This will return 1 - 5 packets
        //
        if (!readNextArchivePage(list, firstRecord, newestPacketTime)) {
            serialPort.write(DMP_CANCEL_DOWNLOAD);
            rv = false;
            break;
        }

        //
        // Sometimes the last page of the dump contains zero records. We don't
        // need to save the newest time because we are at the end of the dump
        // anyway.
        //
        if (list.size() > 0)
            newestPacketTime = list.at(list.size() - 1).getDateTime();

        serialPort.write(DMP_SEND_NEXT_PAGE);

        firstRecord = 0;
    }

    if (rv)
        log.log(VP2Logger::VP2_INFO) << "Received " << list.size() << " records from DMPAFT " << Weather::formatDateTime(afterTime) << endl;
    else
        log.log(VP2Logger::VP2_WARNING) << "Read of archive after " << Weather::formatDateTime(afterTime) << " failed" << endl;

    return rv;

}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::readNextArchivePage(vector<ArchivePacket> & list, int firstRecord, DateTime newestPacketTime) {
    bool rv = true;
    log.log(VP2Logger::VP2_DEBUG1) << "Processing archive page. Newest packet time = " << Weather::formatDateTime(newestPacketTime) << endl;

    //
    // Try to read the page. Will attempt 3 tries to correct CRC errors.
    //
    for (int i = 0; i < ARCHIVE_PAGE_READ_RETRIES; i++) {
        if (serialPort.read(buffer, ARCHIVE_PAGE_SIZE + CRC_BYTES)) {
            if (VantagePro2CRC::checkCRC(buffer, ARCHIVE_PAGE_SIZE)) {
                decodeArchivePage(list, buffer, firstRecord, newestPacketTime);
                rv = true;
                break;
            }
            else {
                log.log(VP2Logger::VP2_WARNING) << "CRC check failed on archive page. Try # " << (i + 1) << endl;
                serialPort.write(DMP_RESEND_PAGE);
                rv = false;
            }
        }
        else {
            serialPort.write(DMP_CANCEL_DOWNLOAD);
            rv = false;
            break;
        }
    }

    return rv;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
VantagePro2Station::decodeArchivePage(vector<ArchivePacket> & list, const byte * buffer, int firstRecord, DateTime newestPacketTime) {
    int recordCount = 0;
    log.log(VP2Logger::VP2_DEBUG1) << "Parsing archive page. Newest packet time = " << Weather::formatDateTime(newestPacketTime) << endl;

    //
    // Which page this is in a DMP or DMPAFT command
    //
    int pageSequence = BitConverter::toInt8(buffer, 0);

    //
    // The first record value may not be zero in the case of a dump after command. The first record after the specified time may not be at the
    // beginning of a page so the others must be skipped.
    //
    for (int i = firstRecord; i < RECORDS_PER_ARCHIVE_PAGE; i++) {
        if (archivePacketContainsData(buffer, 1 + (BYTES_PER_ARCHIVE_RECORD * i))) {
            ArchivePacket packet(buffer, 1 + (BYTES_PER_ARCHIVE_RECORD * i));
           
            //
            // In the case of a dump after command the last page may contain packets from the beginning of the circular
            // archive buffer. In this case the packets will have earlier dates than the last packet of the previous page
            // or the time of the DMPAFT command.
            //
            if (packet.getDateTime() > newestPacketTime) {
                list.push_back(packet);
                recordCount++;
            }
            else
                log.log(VP2Logger::VP2_DEBUG1) << "Skipping page with date " << Weather::formatDateTime(packet.getDateTime()) << endl;
        }
    }

    log.log(VP2Logger::VP2_DEBUG1) << "Page " << pageSequence << " contained " << recordCount << " records" << endl;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::archivePacketContainsData(const byte * buffer, int offset) {
    bool containsData = false;

    //
    // Any bytes that is not equal to 0xFF means that there is data
    //
    for (int i = 0; i < BYTES_PER_ARCHIVE_RECORD; i++) {
        if (BitConverter::toInt8(buffer, offset + i) != NO_VALUE) {
            containsData = true;
            break;
        }
    }

    return containsData;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::sendOKedCommand(const string & command) {
    log.log(VP2Logger::VP2_DEBUG1) << "Sending command '" << command << "' that expects and OK response" << endl;
    bool success = false;

    for (int i = 0; i < COMMAND_RETRIES && !success; i++) {
        serialPort.write(command);
        serialPort.write(COMMAND_TERMINATOR);
        if (!serialPort.read(buffer, COMMAND_RECOGNIZED_RESPONSE.length()))
            success = false;
        else if (COMMAND_RECOGNIZED_RESPONSE != buffer)
            success = false;
        else
            success = true;

        if (!success)
            wakeupStation();
    }

    log.log(VP2Logger::VP2_DEBUG1) << "Command " << command << " status is " << success << endl;
    return success;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::sendOKedWithDoneCommand(const string & command) {
    bool success = false;

    if (!sendOKedCommand(command))
        return false;

    //
    // This may require a loop due to the console's delay between the "OK" and the "DONE".
    // The serial port class only waits for 2 seconds for data before returning an error.
    //
    log.log(VP2Logger::VP2_DEBUG1) << "Waiting for 'DONE' to complete the command" << endl;
    if (!serialPort.read(buffer, DONE_RESPONSE.length()))
        success = false;
    else if (DONE_RESPONSE != buffer)
        success = false;
    else
        success = true;

    if (!success)
        wakeupStation();

    log.log(VP2Logger::VP2_DEBUG1) << "Command " << command << " final status is " << success << endl;
    return success;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::sendAckedCommand(const string & command) {
    log.log(VP2Logger::VP2_DEBUG1) << "Sending command '" << command << "' that expects and ACK response" << endl;
    bool success = false;

    //
    // Try multiple times for completeness. If an ACK is not received then wakeup the console and
    // try again.
    //
    for (int i = 0; i < COMMAND_RETRIES && !success; i++) {
        serialPort.write(command);
        serialPort.write(COMMAND_TERMINATOR);
        success = consumeAck();
        if (!success)
            wakeupStation();
    }

    log.log(VP2Logger::VP2_DEBUG1) << "Command " << command << " status is " << success << endl;
    return success;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::sendStringValueCommand(const string & command, string & results) {
    bool rv = false;

    results.clear();

    if (!sendOKedCommand(command))
        return false;

    //
    // Read 1 byte at a time, appending to the string value until a CR or LF is detected.
    // The CR is the indicator that the command is complete.
    //
    byte b;
    while (serialPort.read(&b, 1)) {
        if (b != VP2Constants::LINE_FEED && b != VP2Constants::CARRIAGE_RETURN)
            results.append(1, b);

        if (b == VP2Constants::CARRIAGE_RETURN) {
            rv = true;
            break;
        }
    }

    return rv;
}


////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Station::consumeAck() {
    byte b;
    bool rv = false;
    if (!serialPort.read(&b, 1)) {
        log.log(VP2Logger::VP2_INFO) << "consumeACK() read failed while consuming ACK" << endl;
        return false;
    }

    if (b != VP2Constants::ACK) {
        log.log(VP2Logger::VP2_WARNING) << "consumeACK() read " << hex << static_cast<int>(b) << dec << " not an ACK" << endl;
        return false;
    }

    return true;
}

}
