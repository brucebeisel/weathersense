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
#ifdef _WIN32
#include <windows.h>
#else
#include <unistd.h>
#endif
#include <time.h>
#include <sstream>
#include "VP2Constants.h"
#include "CurrentWeather.h"
#include "VantagePro2CRC.h"
#include "BitConverter.h"
#include "ProtocolException.h"
#include "VantagePro2Station.h"

using namespace std;

namespace vp2 {

VantagePro2Station::VantagePro2Station(const string & portName, int baudRate) :
                                            serialPort(portName, baudRate),
                                            rainCollectorSize(0.0F),
                                            callback(NULL),
                                            log(VP2Logger::getLogger("VantagePro2Station")),
                                            firstLoopPacket(true) {
}

VantagePro2Station::~VantagePro2Station() {
}

void
VantagePro2Station::setCallback(Callback & callback) {
    this->callback = &callback;
}

Rainfall
VantagePro2Station::getRainCollectorSize() const {
    return rainCollectorSize;
}
int
VantagePro2Station::getArchivePeriod() const {
    return archivePeriod;
}

const vector<SensorStation> &
VantagePro2Station::getSensorStations() const {
    return sensorStations;
}

const vector<Sensor> &
VantagePro2Station::getSensors() const {
    return sensors;
}

bool
VantagePro2Station::openStation() {
    return serialPort.open();
}

void
VantagePro2Station::closeStation() {
    serialPort.close();
}

DateTime
VantagePro2Station::getTime() {
    DateTime stationTime = 0;

    if (!sendAckedCommand(VP2Constants::GET_TIME_CMD))
        return stationTime;

    if (serialPort.read(buffer, TIME_RESPONSE_LENGTH + CRC_BYTES) && VantagePro2CRC::checkCRC(buffer, TIME_RESPONSE_LENGTH)) {
        time_t now = time(0);
        struct tm tm;
        Weather::localtime(now, tm);
        tm.tm_year = buffer[5];
        tm.tm_mon = buffer[4] - 1;
        tm.tm_mday = buffer[3];
        tm.tm_hour = buffer[2];
        tm.tm_min = buffer[1];
        tm.tm_sec = buffer[0];
        stationTime = mktime(&tm);
    }

    return stationTime;
}

bool
VantagePro2Station::wakeupStation() {
    bool awake = false;

    for (int i = 0; i < WAKEUP_TRIES && !awake; i++) {
        log.log(VP2Logger::VP2_DEBUG1) << "Attempting to wakeup console" << endl;
        serialPort.write(VP2Constants::WAKEUP_COMMAND);
        Weather::sleep(1000);
      
        //
        // After sending the wakeup command the console will respond with <LF><CR>
        //
        if (serialPort.read(buffer, 2) && (char)buffer[0] == VP2Constants::LINE_FEED && (char)buffer[1] == VP2Constants::CARRIAGE_RETURN) {
            awake = true;
            log.log(VP2Logger::VP2_INFO) << "Console is awake" << endl;
        }
        else
            serialPort.discardInBuffer();
    }

    return awake;
}

bool
VantagePro2Station::readEEPROM(const string & address, int count) {
    ostringstream command;
    command << VP2Constants::READ_EEPROM_AS_BINARY_CMD << " " << address << " " << hex << count;
    if (!sendAckedCommand(command.str()))
        return false;

    if (!serialPort.read(buffer, count + CRC_BYTES) || !VantagePro2CRC::checkCRC(buffer, count))
        return false;

    return true;
}

bool
VantagePro2Station::retrieveRainCollectorSize() {
    log.log(VP2Logger::VP2_INFO) << "Determining rain collector size" << endl;
    if (!readEEPROM(VP2Constants::EE_SETUP_BITS, 1))
        return false;

    int rainType = buffer[0] & 0x30;

    if (rainType == 0)
        rainCollectorSize = .01F;      // .01 inch
    else if (rainType == 0x10)
        rainCollectorSize = .007874F; // .2 mm
    else if (rainType == 0x20)
        rainCollectorSize = .003937F; // .1 mm
    else
        return false;

    log.log(VP2Logger::VP2_DEBUG1) << "Rain collector increment: " << rainCollectorSize << endl;

    LoopPacket::setRainfallIncrement(rainCollectorSize);
    Loop2Packet::setRainfallIncrement(rainCollectorSize);

    return true;
}

bool
VantagePro2Station::retrieveArchivePeriod() {
    log.log(VP2Logger::VP2_INFO) << "Determining archive interval" << endl;
    if (!readEEPROM(VP2Constants::EE_ARCHIVE_PERIOD, 1))
        return false;

    archivePeriod = BitConverter::toInt8(buffer, 0);

    log.log(VP2Logger::VP2_DEBUG1) << "Archive interval = " << archivePeriod << endl;
    return true;
}

bool
VantagePro2Station::retrieveSensorStationInfo() {
    log.log(VP2Logger::VP2_INFO) << "Getting Sensor information" << endl;
    if (!readEEPROM(VP2Constants::EE_STATION_LIST, 16))
        return false;

    for (int i = 0; i < 16; i += 2) {
        SensorStation::SensorStationType sensorType = (SensorStation::SensorStationType)(buffer[i] & 0xF);
        if (sensorType != SensorStation::NO_STATION)
            sensorStations.push_back(SensorStation(sensorType, (i / 2) + 1));
    }

    for (vector<SensorStation>::iterator it = sensorStations.begin(); it != sensorStations.end(); ++it)
        log.log(VP2Logger::VP2_DEBUG1) << *it << endl;

    return true;
}

string
VantagePro2Station::getStringValue(const string & command) {
    string s;
    bool success = false;
    if (!sendOKedCommand(command))
        return s;

    while (serialPort.read(buffer, 1)) {
        if (buffer[0] != VP2Constants::LINE_FEED && buffer[0] != VP2Constants::CARRIAGE_RETURN)
            s.append(1, buffer[0]);

        if (buffer[0] == VP2Constants::CARRIAGE_RETURN) {
            success = true;
            break;
        }
    }

    // TODO used to return NULL
    return s;
}

bool
VantagePro2Station::getParameters(ParametersMessage & parameters) {
    log.log(VP2Logger::VP2_INFO) << "Getting parameters" << endl;
    parameters.setFirmwareDate(getStringValue(VP2Constants::FIRMWARE_DATE_CMD));

    log.log(VP2Logger::VP2_INFO) << "Getting firmware date" << endl;
    parameters.setFirmwareVersion(getStringValue(VP2Constants::FIRMWARE_VERSION_CMD));

    log.log(VP2Logger::VP2_INFO) << "Getting latitude" << endl;
    if (!readEEPROM(VP2Constants::EE_LATITUDE, 2))
        return false;

    short lat = BitConverter::toInt16(buffer, 0) & 0xFFFF;
    parameters.setLatitude((double)lat / 10.0);

    log.log(VP2Logger::VP2_INFO) << "Getting longitude" << endl;
    if (!readEEPROM(VP2Constants::EE_LONGITUDE, 2))
        return false;

    short lon = BitConverter::toInt16(buffer, 0) & 0xFFFF;
    parameters.setLongitude((double)lon / 10.0);

    log.log(VP2Logger::VP2_INFO) << "Getting elevation" << endl;
    if (!readEEPROM(VP2Constants::EE_ELEVATION, 2))
        return false;

    int ialt = BitConverter::toInt16(buffer, 0) & 0xFF;
    cout << "buffer[0]: " << (int)buffer[0] << " buffer[1]: " << (int)buffer[1] << " ialt: " << ialt << endl;
    parameters.setElevation(ialt);

    parameters.setArchivePeriod(archivePeriod);

    log.log(VP2Logger::VP2_INFO) << "Getting rain season start" << endl;
    if (!readEEPROM(VP2Constants::EE_RAIN_SEASON_START, 1))
        return false;

    parameters.setRainSeasonStart(buffer[0]);

    log.log(VP2Logger::VP2_INFO) << "Getting wind cup size" << endl;
    if (!readEEPROM(VP2Constants::EE_SETUP_BITS, 1))
        return false;

    parameters.setWindCupSize(buffer[0] & 0x8);
    parameters.setRainCollectorSize(rainCollectorSize);

    log.log(VP2Logger::VP2_INFO) << "Parameters message : " << parameters.formatMessage() << endl;
    return true;
}

bool
VantagePro2Station::setConsoleTime() {
    if (!sendAckedCommand(VP2Constants::SET_TIME_CMD))
        return false;

    time_t now = time(0);
    struct tm tm;
    Weather::localtime(now, tm);
    log.log(VP2Logger::VP2_INFO) << "Setting console time to " << Weather::formatDateTime(now) << endl;
    buffer[0] = (byte)tm.tm_sec;
    buffer[1] = (byte)tm.tm_min;
    buffer[2] = (byte)tm.tm_hour;
    buffer[3] = (byte)tm.tm_mday;
    buffer[4] = (byte)tm.tm_mon + 1;
    buffer[5] = (byte)tm.tm_year;

    int crc = VantagePro2CRC::calculateCRC(buffer, SET_TIME_LENGTH);
    BitConverter::getBytes(crc, buffer, SET_TIME_LENGTH, CRC_BYTES, false);

    serialPort.write(buffer, SET_TIME_LENGTH + CRC_BYTES);

    return consumeAck();
}

bool
VantagePro2Station::readLoopPacket(LoopPacket & loopPacket) {
    log.log(VP2Logger::VP2_DEBUG1) << "Reading LOOP Packet" << endl;
    if (!serialPort.read(buffer, LOOP_PACKET_SIZE))
        return false;

    if (!loopPacket.parseLoopPacket(buffer))
        return false;

    //
    // First time through determine what sensors are attached to the weather station based on the valid data in
    // the LOOP packet.
    //
    if (firstLoopPacket) {
        firstLoopPacket = false;
        Sensor::detectSensors(loopPacket, sensors);
    }

    //
    // Pull out the battery status for the sensor stations
    //
    consoleBatteryVoltage = loopPacket.getConsoleBatteryVoltage();
    for (vector<SensorStation>::iterator it = sensorStations.begin(); it != sensorStations.end(); ++it) {
        it->setBatteryStatus(loopPacket.isTransmitterBatteryGood(it->getSensorIndex()));
    }

    log.log(VP2Logger::VP2_DEBUG1) << "LOOP packet read successfully" << endl;
    return true;
}

bool
VantagePro2Station::readLoop2Packet(Loop2Packet & loop2Packet) {
    log.log(VP2Logger::VP2_DEBUG1) << "Reading LOOP2 Packet" << endl;
    if (!serialPort.read(buffer, LOOP_PACKET_SIZE))
        return false;

    if (!loop2Packet.parseLoop2Packet(buffer))
        return false;

    log.log(VP2Logger::VP2_DEBUG1) << "LOOP2 packet read successfully" << endl;
    return true;
}

void
VantagePro2Station::currentValuesLoop(int records) {
    LoopPacket loopPacket;
    Loop2Packet loop2Packet;
    bool terminateLoop = false;
    bool resetNeeded = false;

    ostringstream command;
    command << VP2Constants::LPS_CMD << " " << (records * 2);

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
        CurrentWeather cw(loopPacket, loop2Packet, pastWindDirs);

        //
        // Build a list of past wind directions. This is to mimic what is shown on the
        // console
        //
        pastWindDirs.addHeading(loopPacket.getWindDirection());

        //
        // Keep the wind gust data in order to populate the next archive packet. This may
        // not be necessary, but since the Vantage Pro 2 does not report wind gust in the
        // archive packet, this may be the only way to get a wind gust value.
        //
        windGust10Minute = loop2Packet.getWindGust10Minute();
        windGustDirection10Minute = loop2Packet.getWindGustHeading10Minute();

        //
        // Send the message for processing
        //
        terminateLoop = callback->processCurrentWeather(cw);

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

bool
VantagePro2Station::archivePacketContainsData(const byte * buffer, int offset) {
    bool containsData = false;

    //
    // Any bytes that is not equal to 0xFF means that there is data
    //
    for (int i = 0; i < BYTES_PER_ARCHIVE_RECORD; i++) {
        if (BitConverter::toInt8(buffer, offset + i) != VP2Constants::NO_VALUE) {
            containsData = true;
            break;
        }
    }

    return containsData;
}

ArchivePacket
VantagePro2Station::convertBufferToArchivePacket(const byte * buffer, int index) const {
    ArchivePacket packet(buffer, index, rainCollectorSize, archivePeriod, windGust10Minute, windGustDirection10Minute);
    return packet;
}

void
VantagePro2Station::parseArchivePage(vector<ArchivePacket> & list, const byte * buffer, int firstRecord, DateTime newestPacketTime) {
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
            ArchivePacket packet = convertBufferToArchivePacket(buffer, 1 + (BYTES_PER_ARCHIVE_RECORD * i));
           
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

bool
VantagePro2Station::processArchivePage(vector<ArchivePacket> & list, int firstRecord, DateTime newestPacketTime) {
    bool rv = true;
    log.log(VP2Logger::VP2_DEBUG1) << "Processing archive page. Newest packet time = " << Weather::formatDateTime(newestPacketTime) << endl;

    //
    // Try to read the page. Will attempt 3 tries to correct CRC errors.
    //
    for (int i = 0; i < 3; i++) {
        if (serialPort.read(buffer, ARCHIVE_PAGE_SIZE + CRC_BYTES)) {
            if (VantagePro2CRC::checkCRC(buffer, ARCHIVE_PAGE_SIZE)) {
                parseArchivePage(list, buffer, firstRecord, newestPacketTime);
                rv = true;
                break;
            }
            else {
                log.log(VP2Logger::VP2_WARNING) << "CRC check failed on archive page. Try # " << (i + 1) << endl;
                serialPort.write(VP2Constants::DMP_RESEND_PAGE);
                rv = false;
            }
        }
        else {
            serialPort.write(VP2Constants::DMP_CANCEL_DOWNLOAD);
            rv = false;
            break;
        }
    }

    return rv;
}

void
VantagePro2Station::dump(vector<ArchivePacket> & list) {
    log.log(VP2Logger::VP2_INFO) << "Dumping archive..." << endl;
    list.clear();

    if (sendAckedCommand(VP2Constants::DUMP_ARCHIVE_CMD)) {
        for (int i = 0; i < NUM_ARCHIVE_PAGES; i++) {
            processArchivePage(list, 0, time(0));
        }
    }
}

bool
VantagePro2Station::dumpAfter(DateTime time, vector<ArchivePacket> & list) {
    log.log(VP2Logger::VP2_DEBUG1) << "Dumping after " << Weather::formatDateTime(time) << endl;
    list.clear();
    DateTime newestPacketTime = time;

    //
    // First send the dump after command and get an ACK back
    //
    if (!sendAckedCommand(VP2Constants::DUMP_AFTER_CMD))
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
    BitConverter::getBytes(crc, dateTimeBytes, 4, CRC_BYTES, false);

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

    bool rv = true;
    for (int i = 0; i < numPages; i++) {
        //
        // Process a single page. This will return 1 - 5 packets
        //
        if (!processArchivePage(list, firstRecord, newestPacketTime)) {
            serialPort.write(VP2Constants::DMP_CANCEL_DOWNLOAD);
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

        serialPort.write(VP2Constants::DMP_SEND_NEXT_PAGE);

        firstRecord = 0;
    }

    log.log(VP2Logger::VP2_INFO) << "Received " << list.size() << " records from DMPAFT " << Weather::formatDateTime(time) << endl;

    return rv;
}

bool
VantagePro2Station::consumeAck() {
    if (serialPort.read(buffer, 1)) {
        char response = buffer[0];
        char expected = VP2Constants::ACK;
        bool rv = response == expected;
        if (!rv)
            log.log(VP2Logger::VP2_WARNING) << "consumeACK() read " << hex << (int)response << dec << " not an ACK" << endl;

        return rv;
    }
    else {
        log.log(VP2Logger::VP2_INFO) << "consumeACK() failed to read ACK" << endl;
        return false;
    }
}

bool
VantagePro2Station::sendOKedCommand(const string & command) {
    log.log(VP2Logger::VP2_DEBUG1) << "Sending command '" << command << "' that expects and OK response" << endl;
    bool success = false;

    for (int i = 0; i < 5 && !success; i++) {
        serialPort.write(command);
        serialPort.write(VP2Constants::COMMAND_TERMINATOR);
        if (!serialPort.read(buffer, 6))
            success = false;
        else if ((char)buffer[0] != VP2Constants::LINE_FEED ||
                 (char)buffer[1] != VP2Constants::CARRIAGE_RETURN ||
                 (char)buffer[2] != 'O' ||
                 (char)buffer[3] != 'K' ||
                 (char)buffer[4] != VP2Constants::LINE_FEED ||
                 (char)buffer[5] != VP2Constants::CARRIAGE_RETURN)
            success = false;
        else
            success = true;

        if (!success)
            wakeupStation();
    }

    log.log(VP2Logger::VP2_DEBUG1) << "Command " << command << " status is " << success << endl;
    return success;
}

bool
VantagePro2Station::sendAckedCommand(const string & command) {
    log.log(VP2Logger::VP2_DEBUG1) << "Sending command '" << command << "' that expects and ACK response" << endl;
    bool success = false;

    //
    // Try 5 times for completeness. If an ACK is not received then wakeup the console and
    // try again.
    //
    for (int i = 0; i < 5 && !success; i++) {
        serialPort.write(command);
        serialPort.write(VP2Constants::COMMAND_TERMINATOR);
        success = consumeAck();
        if (!success)
            wakeupStation();
    }
    log.log(VP2Logger::VP2_DEBUG1) << "Command " << command << " status is " << success << endl;
    return success;
}
}
