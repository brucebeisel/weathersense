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

#ifndef VANTAGE_PRO2_STATION_H
#define VANTAGE_PRO2_STATION_H

#include <string>
#include <vector>
#include "VP2Logger.h"
#include "ArchivePacket.h"
#include "CurrentWeather.h"
#include "LoopPacket.h"
#include "Loop2Packet.h"
#include "WindDirectionSlices.h"
#include "SensorStation.h"
#include "Sensor.h"
#include "BitConverter.h"
#include "SerialPort.h"

namespace vp2 {
class HiLowPacket;

struct TimeZoneData {
    int index;
    int offsetMinutes;
    const char * name;
};

static const TimeZoneData TIME_ZONES[] = {
    0, -1200, "(GMT-12:00) Eniwetok, Kwajalein",
    1, -1100, "(GMT-11:00) Midway Island, Samoa",
    2, -1000, "(GMT-10:00) Hawaii",
    3,  -900, "(GMT-09:00) Alaska",
    4,  -800, "(GMT-08:00) Pacific Time, Tijuana",
    5,  -700, "(GMT-07:00) Mountain Time",
    6,  -600, "(GMT-06:00) Central Time",
    7,  -600, "(GMT-06:00) Mexico City",
    8,  -600, "(GMT-06:00) Central America",
    9,  -500, "(GMT-05.00) Bogota, Lima, Quito",
    10, -500, "(GMT-05:00) Eastern Time",
    11, -400, "(GMT-04:00) Atlantic Time",
    12, -400, "(GMT-04.00) Caracas, La Paz, Santiago",
    13, -330, "(GMT-03.30) Newfoundland",
    14, -300, "(GMT-03.00) Brasilia",
    15, -300, "(GMT-03.00) Buenos Aires, Georgetown, Greenland",
    16, -200, "(GMT-02.00) Mid-Atlantic",
    17, -100, "(GMT-01:00) Azores, Cape Verde Island",
    18,    0, "(GMT) Greenwich Mean Time, Dublin, Edinburgh, Lisbon, London",
    19,    0, "(GMT) Monrovia, Casablanca",
    20,  100, "(GMT+01.00) Berlin, Rome, Amsterdam, Bern, Stockholm, Vienna",
    21,  100, "(GMT+01.00) Paris, Madrid, Brussels, Copenhagen, W Central Africa",
    22,  100, "(GMT+01.00) Prague, Belgrade, Bratislava, Budapest, Ljubljana",
    23,  200, "(GMT+02.00) Athens, Helsinki, Istanbul, Minsk, Riga, Tallinn",
    24,  200, "(GMT+02:00) Cairo",
    25,  200, "(GMT+02.00) Eastern Europe, Bucharest",
    26,  200, "(GMT+02:00) Harare, Pretoria",
    27,  200, "(GMT+02.00) Israel, Jerusalem",
    28,  300, "(GMT+03:00) Baghdad, Kuwait, Nairobi, Riyadh",
    29,  300, "(GMT+03.00) Moscow, St. Petersburg, Volgograd",
    30,  330, "(GMT+03:30) Tehran",
    31,  400, "(GMT+04:00) Abu Dhabi, Muscat, Baku, Tblisi, Yerevan, Kazan",
    32,  430, "(GMT+04:30) Kabul",
    33,  500, "(GMT+05:00) Islamabad, Karachi, Ekaterinburg, Tashkent",
    34,  530, "(GMT+05:30) Bombay, Calcutta, Madras, New Delhi, Chennai",
    35,  600, "(GMT+06:00) Almaty, Dhaka, Colombo, Novosibirsk, Astana",
    36,  700, "(GMT+07:00) Bangkok, Jakarta, Hanoi, Krasnoyarsk"
};

/**
 * Class that handles the command protocols with the VP2 console.
 */
class VantagePro2Station {
public:
    /**
     * Interface class used to callback the current weather and archive pages.
     */
    class Callback {
    public:
        virtual ~Callback() {}
        virtual bool processCurrentWeather(const CurrentWeather & cw) = 0;
    };
    static const int MAX_STATION_RECEPTION = 100;

    struct ConsoleDiagnosticReport {
        int packetCount;
        int missedPacketCount;
        int syncCount;
        int maxPacketSequence;
        int crcErrorCount;
    };

    /**
     * Constructor.
     * 
     * @param portName The name of he serial port to open
     * @param baudRate The baud rate to use on the serial port
     */
    VantagePro2Station(const std::string & portName, int baudRate);

    /**
     * Destructor.
     */
    virtual ~VantagePro2Station();

    /**
     * Set the callback object that will be called when current weather, archive packets are received.
     * 
     * @param callback The callback object
     */
    void setCallback(Callback & callback);

    /**
     * Open the VP2 console.
     * 
     * @return True if the console was opened
     */
    bool openStation();

    /**
     * Close the VP2 console.
     */
    void closeStation();

    /**
     * Wake up the console.
     * 
     * @return True of the console is awake
     */
    bool wakeupStation();

    /**
     * Read any data from the station that is needed for other commands, such as the archive period or rain collector.
     */
    bool initialize();

    //
    // The following methods correspond to the commands in section VIII of the Vantage Serial Protocol Document, version 2.6.1
    //

    /////////////////////////////////////////////////////////////////////////////////
    // Testing Commands
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Send a test command to the console that expects the response "TEST<LF><CR>". This command
     * is used to test connectivity with the console.
     *
     * @return True if the correct response was received
     */
    bool sendTestCommand();

    /**
     * Retrieve the console diagnostic report.
     *
     * @param report The structure into which the report parameters are written
     *
     * @return True if the report was successfully retrieved
     */
    bool retrieveConsoleDiagnosticsReport(ConsoleDiagnosticReport & report);

    /**
     * Move the console from the "Receiving from..." screen to the current condition screen and reset
     * counters in the console diagnostic report.
     */
    bool performReceiveTest();

    /**
     * Retrieve the date of the console firmware.
     *
     * @param firmwareDate An optional string to return the firmware date. nullptr is allowed.
     *
     * @return True if the date was retrieved successfully
     */
    bool retrieveFirmwareDate(std::string * firmwareDate);

    /**
     * Retrieve the list of receivers the console can hear. Note that this is not the set of stations that the console is
     * reading from. There can be other sensor stations in the area that do not belong to this Vantage station.
     *
     * @param sensorStations An optional vector into which the sensor station list will be written. nullptr is allowed.
     *
     * @return True if the list of sensor stations was retrieved
     */
    bool retrieveReceiverList(std::vector<StationId> * sensorStations);

    /**
     * Retrieve the version of the console firmware.
     *
     * @param firmwareVersion An optional string to return the firmware version. nullptr is allowed.
     *
     * @return True if the version was retrieved successfully
     */
    bool retrieveFirmwareVersion(std::string * firmwareVersion);

    /////////////////////////////////////////////////////////////////////////////////
    // End Testing Commands
    /////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////
    // Current Data Commands
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Retrieve the current weather by reading the LOOP and LOOP2 packet in a loop.
     * 
     * @param records The number of times to execute the loop before returning
     */
    void currentValuesLoop(int records);

    /**
     * Retrieve the current high/low packet from the console.
     *
     * @param packet The packet into which the high/low packet will be written
     *
     * @return True if successful
     */
    bool retrieveHiLowValues(HiLowPacket &packet);

    /**
     * Write the specified rainfall amount as the current yearly accumulated rainfall.
     * This can be used when a weather station is installed or reset mid-year.
     *
     * @param rain The rainfall amount to write to the console
     *
     * @return True if successful
     */
    bool putYearlyRain(Rainfall rain);

    /**
     * Write the specified ET as the current yearly accumulated ET.
     * This can be used when a weather station is installed or reset mid-year.
     *
     * @param et The ET to write to the console
     *
     * @return True if successful
     */
    bool putYearlyET(Evapotranspiration et);

    /////////////////////////////////////////////////////////////////////////////////
    // End Current Data Commands
    /////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////
    // Download Commands
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Dump the entire archive.
     *
     * @param list The list that will contain the entire archive
     */
    void dump(std::vector<ArchivePacket> & list);

    /**
     * Perform a dump of the archive after the specified time.
     * 
     * @param time    The time after which to dump the archive
     * @param archive The vector into which the dumped archive packets will be returned
     *
     * @return True if successful
     */
    bool dumpAfter(DateTime time, std::vector<ArchivePacket> & archive);

    /////////////////////////////////////////////////////////////////////////////////
    // End Download Commands
    /////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////
    // EEPROM Commands
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Read the entire EEPROM data block.
     *
     * @return True if the read was successful
     */
    bool eepromReadDataBlock(byte buffer[]);

    /**
     * Read part of the EEPROM memory.
     *
     * @param address The EEPROM address at which the reading will begin
     * @param count   The number of bytes to read
     *
     * @return True if the read is successful
     */
    bool eepromRead(unsigned address, unsigned count);

    /**
     * Read part of the EEPROM memory.
     *
     * @param address The EEPROM address at which the reading will begin
     * @param count   The number of bytes to read
     * @param output  Option point to which the results of the read will be copied
     *
     * @return True if the read is successful
     */
    bool eepromBinaryRead(unsigned address, unsigned count, char * output = nullptr);

    /**
     * Write a single byte to the specified EEPROM address.
     *
     * @param address The address within the EEPROM memory
     * @param value   The value to write to the specified address
     *
     * @return True if successful
     */
    bool eepromWriteByte(unsigned address, byte value);

    /**
     * Write a series of bytes to the EEPROM.
     *
     * @param address The address within the EEPROM at which the write will start
     * @param buffer  The buffer to write to the EEPROM
     * @param count   The number of bytes to write to the EEPROM
     *
     * @return bool True if successful
     */
    bool eepromBinaryWrite(unsigned address, const byte buffer[], unsigned count);

    /////////////////////////////////////////////////////////////////////////////////
    // End EEPROM Commands
    /////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////
    // Calibration Commands
    /////////////////////////////////////////////////////////////////////////////////

    // Calibration commands are not supported at this time

    /////////////////////////////////////////////////////////////////////////////////
    // End Calibration Commands
    /////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////
    // Clearing Commands
    /////////////////////////////////////////////////////////////////////////////////
    bool clearArchive();
    bool clearAlarmThresholds();
    bool clearTemperatureHumidityCalibrationOffsets();
    bool clearGraphPoints();
    bool clearCumulativeValue(VP2Constants::CumulativeValue cumValue);
    bool clearHighValues(VP2Constants::ExtremePeriod period);
    bool clearLowValues(VP2Constants::ExtremePeriod period);
    bool clearActiveAlarms();
    bool clearCurrentData();

    /////////////////////////////////////////////////////////////////////////////////
    // End Clearing Commands
    /////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////
    // Configuration Commands
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Change the baud rate for communicating with the console.
     *
     * @param rate The new baud rate
     *
     * @return True if successful
     */
    bool updateBaudRate(VP2Constants::BaudRate rate);
    
    /**
     * Update the console's time.
     * 
     * @return True if successful
     */
    bool updateConsoleTime();

    /**
     * Get the clock time on the console.
     * 
     * @param stationTime The time that was retrieved or 0 on failure
     * @return True if the time was retrieved successfully
     */
    bool retrieveConsoleTime(DateTime &stationTime);

    /**
     * Update the archive period to one of the allowed intervals.
     *
     * @param period The interval at which the archive data will saved
     *
     * @return True if successful
     */
    bool updateArchivePeriod(VP2Constants::ArchivePeriod period);

private:
    /**
     * Reinitialize the console after making any of the following changes to the configuration:
     *      1. Lat/Lon
     *      2. Elevation
     *      3. Any value in the EEPROM byte 0x2B (Decimal 43)
     * This command is private as it can only be triggered by a command that changes the configuration.
     *
     * @return True if successful
     */
    bool initializeSetup();

public:
    /**
     * Turn the console lamp on or off.
     *
     * @param on Turn the lamp on if true
     *
     * @return True if successful
     */
    bool controlConsoleLamp(bool on);

    /////////////////////////////////////////////////////////////////////////////////
    // End of Configuration Commands
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Calculate the percentage of packets that have been received over the past "archive period" minutes.
     * Note that this percentage is only calculated for the station that contains the wind anemometer.
     *
     * @param archivePacketWindSamples The number of samples as reported by the Archive packet
     */
    int calculateStationReceptionPercentage(int archivePacketWindSamples) const;

    /**
     * Get the current weather data.
     *
     * @return the current weather data
     */
    const CurrentWeather & getCurrentWeather() const;

    Rainfall getRainCollectorSize() const;
    const std::vector<SensorStation> & getSensorStations() const;

private:
    static constexpr int WAKEUP_TRIES = 5;               // The number of times to try to wake up the console before performing a disconnect/reconnect cycle
    static constexpr int CRC_BYTES = 2;                  // The number of bytes in the CRC

    static constexpr int NUM_ARCHIVE_PAGES = 512;        // The total number of pages in the console's memory
    static constexpr int ARCHIVE_PAGE_SIZE = 265;        // 1 sequence byte, 5 52 byte records (260 bytes) and 4 spare bytes. 1 + 260 + 4 = 265 bytes
    static constexpr int BYTES_PER_ARCHIVE_RECORD = 52;  // The size of each archive record
    static constexpr int RECORDS_PER_ARCHIVE_PAGE = 5;   // The number of archive records per archive page
    static constexpr int DUMP_AFTER_RESPONSE_LENGTH = 4; // The length of the response to the DUMP AFTER command
    static constexpr int EEPROM_READ_LINE_LENGTH = 4; // The length of the response to the DUMP AFTER command

    static constexpr int LOOP_PACKET_SIZE = 99;
    static constexpr int TIME_RESPONSE_LENGTH = 6;
    static constexpr int TIME_LENGTH = 4;
    static constexpr int SET_TIME_LENGTH = 6;
    static constexpr int LOOP_PACKET_WAIT = 2000;
    static constexpr int WAKEUP_WAIT = 1000;
    static constexpr int VP2_YEAR_OFFSET = 2000;
    static constexpr int HILOW_PACKET_SIZE = 436;
    static constexpr int NO_VALUE = 0xFF;
    static constexpr int EEPROM_DATA_BLOCK_SIZE = 4096;
    static constexpr int EEPROM_NON_GRAPH_DATA_SIZE = 176;

    static constexpr int ALARM_THRESHOLDS_SIZE = 94; 
    static constexpr int MAX_STATION_ID = 8;
    static constexpr int STATION_DATA_SIZE = 16;

    static constexpr int COMMAND_RETRIES = 5;
    static constexpr int ARCHIVE_PAGE_READ_RETRIES = 3;
    static constexpr int BUFFER_SIZE = EEPROM_DATA_BLOCK_SIZE + CRC_BYTES;

    static constexpr double LAT_LON_SCALE = 10.0;


    /**
     * Read the LOOP packet and save off a few values for later use.
     *
     * @param loopPacket The LoopPacket into which the data will be copied
     *
     * @return True if the packet was read succesfully
     */
    bool readLoopPacket(LoopPacket & loopPacket);

    /**
     * Read the LOOP2 packet.
     *
     * @param loop2Packet The Loop2Packet into which the data will be copied
     *
     * @return True if the packet was read succesfully
     */
    bool readLoop2Packet(Loop2Packet & loop2Packet);

    /**
     * Read the archive pages that occur after the specified time.
     *
     * @param afterTime   The time after which the archive records will be saved
     * @param list        The list into which the archive records will be saved
     * @param firstRecord The first record in the first page that is part of the dump
     * @param numPages    The number of pages in the archive that are after the specified time
     *
     * @return True if successful
     */
    bool readAfterArchivePages(DateTime afterTime, std::vector<ArchivePacket> & list, int firstRecord, int numPages);

    /**
     * Read the next archive page that is part of the one of the dump commands.
     *
     * @param packets          The vector into which the processed archive packets will be returned
     * @param firstRecord      The first record to process, which may not be zero if this is the first page that is being dumped
     * @param newestPacketTime The newest packet used to detect if the page contains the end of the ring buffer
     *
     * @return True if the page was read successfully
     */
    bool readNextArchivePage(std::vector<ArchivePacket> & packets, int firstRecord, DateTime newestPacketTime);

    /**
     * Decode an archive page that contains up to 5 packets.
     *
     * @param packets          The vector into which the processed archive packets will be returned
     * @param firstRecord      The first record to process, which may not be zero if this is the first page that is being dumped
     * @param newestPacketTime The newest packet used to detect if the page contains the end of the ring buffer
     */
    void decodeArchivePage(std::vector<ArchivePacket> &, const byte * buffer, int firstRecord, DateTime newestPacketTime);

    /**
     * Checks if an archive packet contains data.
     *
     * @param buffer The buffer containing the packet
     * @param offset The offset within the buffer where the packet starts
     *
     * @return True if the packet contains data
     */
    bool archivePacketContainsData(const byte * buffer, int offset);

    /**
     * Send a command that expects on "OK" response.

     * @param command The command to be sent to the VP2 console
     * @return True if the command was sent successfully
     */
    bool sendOKedCommand(const std::string & command);

    /**
     * Send a command that expects on "OK" response followed by a "DONE" after a period of time.

     * @param command The command to be sent to the VP2 console
     * @return True if the command was sent successfully
     */
    bool sendOKedWithDoneCommand(const std::string & command);

    /**
     * Send a command that expects an ACK response.
     *
     * @param command The command to be sent to the VP2 console
     * @return True if the command was sent successfully
     */
    bool sendAckedCommand(const std::string & command);

    /**
     * Send the command to retrieve a string value.
     *
     * @param command The command to send
     * @param results The string value returned for the given command
     * @return True if successful
     */
    bool sendStringValueCommand(const std::string & command, std::string & results);

    /**
     * Read exactly one byte, checking for an ACK.
     *
     * @return True if one character was read and it was an ACK
     */
    bool consumeAck();


public:
    bool retrieveSensorStationInfo();
private:
    Callback *                 callback;
    SerialPort                 serialPort;               // The serial port object that communicates with the console
    bool                       firstLoopPacketReceived;  // Whether a LOOP packet has been received
    int                        baudRate;                 // The baud rate for communicating with the console
    WindDirectionSlices        pastWindDirs;             // The past wind direction measurements used to determine the arrows on the wind display
    CurrentWeather             currentWeather;           // The most recent current weather data
    byte                       buffer[BUFFER_SIZE];      // The buffer used for all reads
    float                      consoleBatteryVoltage;    // The console battery voltage received in the LOOP packet

    std::string                firmwareDate;             // TBD - Is this really needed?
    std::string                firmwareVersion;          // TBD - Is this really needed?
    std::vector<SensorStation> sensorStations;           // The sensor stations as reported by the console
    std::vector<StationId>     stationIds;               // The ID of the stations that the console can hear
    int                        windSensorStationId;      // The ID of the sensor station containing the anemometer
    Rainfall                   rainCollectorSize;        // The size of the rain collector that is needed for calculating rain amounts
    int                        archivePeriod;            // The archive period used to calculate reception percentage
    VP2Logger                  log;
    //StationConfiguration       stationConfiguration;
    //std::vector<Sensor>        sensors;
};
}

#endif /* VANTAGE_PRO2_STATION_H */
