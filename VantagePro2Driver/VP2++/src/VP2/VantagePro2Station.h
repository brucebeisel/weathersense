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
#include "LoopPacket.h"
#include "Loop2Packet.h"
#include "WindDirectionSlices.h"
#include "ParametersMessage.h"
#include "SensorStation.h"
#include "Sensor.h"
#include "SerialPort.h"

namespace vp2 {
class CurrentWeather;
class HiLowPacket;

class StationConfiguration {
public:
private:
    double                     issLatitude;
    double                     issLongitude;
    int                        issElevation;
    int                        timeZoneIndex;
    bool                       automaticDaylightSavingsTime;
    bool                       manualDaylightSavingsTimeOn;
    float                      gmtOffset;
    bool                       useTimezone;
    std::vector<SensorStation> sensorStations;
    int                        retransmitId;                 // 0 = no retransmission

    Rainfall                   rainCollectorSize;
    int                        archivePeriod;
    bool                       north;
    bool                       east;
    std::string                firmwareDate;
    std::string                firmwareVersion;
    VP2Logger                  log;
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
        virtual bool processCurrentWeather(const CurrentWeather & cw) = 0;
        //virtual bool processArchivePage(const std::vector<ArchivePacket> & page) = 0;
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
     * Retrieve the configuration parameters.
     */
    bool retrieveConfigurationParameters();


    //
    // The following methods correspond to the commands in section VIII of the Vantage Serial Protocol Document, version 2.6.1
    //

    /////////////////////////////////////////////////////////////////////////////////
    // Testing Commands
    /////////////////////////////////////////////////////////////////////////////////
    bool sendTestCommand();

    bool retrieveConsoleDiagnosticsReport();

    bool performReceiveTest();

    bool retrieveReceiverList();

    bool retrieveFirmwareVersion();

    bool retrieveFirmwareDate();

    /////////////////////////////////////////////////////////////////////////////////
    // Current Data Commands
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Retrieve the current weather by reading the LOOP and LOOP2 packet in a loop.
     * 
     * @param records The number of times to execute the loop before returning
     */
    void currentValuesLoop(int records);

    bool retrieveHiLowValues(HiLowPacket &packet);

    bool putYearlyRain(Rainfall rain);

    bool putYearlyET(Evapotranspiration et);


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
     * @return True if successful
     */
    bool dumpAfter(DateTime time, std::vector<ArchivePacket> & archive);


    /////////////////////////////////////////////////////////////////////////////////
    // EEPROM Commands
    /////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////
    // Calibration Commands
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
    // Configuration Commands
    /////////////////////////////////////////////////////////////////////////////////
    bool updateBaudRate(int rate);
    

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

    //
    // End of console commands
    //

    //
    // EEPROM commands
    //

    bool eepromReadDataBlock();

    /**
     * Read part of the EEPROM memory.
     *
     * @param The EEPROM address at which the reading will begin
     * @return True if the read is successful
     */
    bool eepromRead(unsigned address, unsigned count);

    /**
     * Read part of the EEPROM memory.
     *
     * @param The EEPROM address at which the reading will begin
     * @return True if the read is successful
     */
    bool eepromBinaryRead(unsigned address, unsigned count);

    bool eepromWriteByte(unsigned address, int value);

    bool eepromBinaryWrite(unsigned address, const byte buffer[], unsigned count);


    //
    // End of EEPROM commands
    //

    /**
     * Retrieve the state of the manual setting for daylight savings time.
     * If the DST mode is automatic, this entry is not used.
     *
     * @return True if successful
     */
    //bool retrieveDaylightSavingsState();

    //bool retrieveGMTOffset();

    /**
     * Retrieve whether the time zone offset is set by GMT offset or specifying a time zone.
     *
     * @return True if successful
     */
    //bool retrieveTimeZoneMode();

    //bool retrieveTransmitterListenBits();

    //bool retrieveRetransmitID();

    //bool retrieveStationList();

    //bool retrieveUnits();

    //bool retrieveSetupBits();

    //bool retrieveRainSeasonStart();
    //bool retrieveArchivePeriod();
    //bool retrieveAlarmThresholds();


    /**
     * Get the archive period (in minutes).
     * 
     * @return The archive period
     */
    int getArchivePeriod() const;

    /**
     * Get the size of the rain collector.
     *
     * @return The size of the rain collector in inches
     */
    Rainfall getRainCollectorSize() const;

    bool getISSLocation(double & issLatitude, double & issLongitude, int & issElevation);

    bool areWindCupsLarge() const;
    bool getDaylightSavingsTimeMode();

    /**
     * Get the list of sensor stations.
     * 
     * @return the list of sensor stations
     */
    const std::vector<SensorStation> & getSensorStations() const;

    /**
     * Get the list of sensors attached to the sensor stations.
     * 
     * @return The list of sensors
     */
    const std::vector<Sensor> & getSensors() const;

    /**
     * Retrieve the rain collector size from the console.
     * 
     * @return True if the rain collector size was stored successfully
     */

    int calculateISSReception(int loopPacketWindSamples) const;


private:
    static constexpr int WAKEUP_TRIES = 5;               // The number of times to try to wake up the console before performing a disconnect/reconnect cycle
    static constexpr int CRC_BYTES = 2;                  // The number of bytes in the CRC

    static constexpr int NUM_ARCHIVE_PAGES = 512;        // The total number of pages in the console's memory
    static constexpr int ARCHIVE_PAGE_SIZE = 265;        // 1 sequence byte, 5 52 byte records (260 bytes) and 4 spare bytes. 1 + 260 + 4 = 265 bytes
    static constexpr int BYTES_PER_ARCHIVE_RECORD = 52;  // The size of each archive record
    static constexpr int RECORDS_PER_ARCHIVE_PAGE = 5;   // The number of archive records per archive page
    static constexpr int DUMP_AFTER_RESPONSE_LENGTH = 4; // The length of the response to the DUMP AFTER command

    static constexpr int LOOP_PACKET_SIZE = 99;
    static constexpr int TIME_RESPONSE_LENGTH = 6;
    static constexpr int TIME_LENGTH = 4;
    static constexpr int SET_TIME_LENGTH = 6;
    static constexpr int LOOP_PACKET_WAIT = 2000;
    static constexpr int VP2_YEAR_OFFSET = 2000;
    static constexpr int HILOW_PACKET_SIZE = 436;
    static constexpr int NO_VALUE = 0xFF;
    static constexpr int EEPROM_DATA_BLOCK_SIZE = 4096;
    static constexpr int EEPROM_NON_GRAPH_DATA_SIZE = 176;

    static constexpr int ALARM_THRESHOLDS_SIZE = 94; 


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
     * Read exactly one byte, checking for an ACK.
     *
     * @return True if one character was read and it was an ACK
     */
    bool consumeAck();

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

    void        decodeArchivePage(std::vector<ArchivePacket> &, const byte * buffer, int firstRecord, DateTime newestPacketTime);

    bool        processArchivePage(std::vector<ArchivePacket> &, int firstRecord, DateTime newestPacketTime);

    bool        archivePacketContainsData(const byte * buffer, int offset);


public:
    bool        retrieveSensorStationInfo();
private:


    /**
     * Send the command to retrieve a string value.
     *
     * @param command The command to send
     * @param results The string value returned for the given command
     * @return True if successful
     */
    bool sendStringValueCommand(const std::string & command, std::string & results);

    bool readNonGraphEepromData();


    SerialPort                 serialPort;
    std::string                portName;
    int                        baudRate;
    byte                       buffer[512];
    byte                       eepromBuffer[EEPROM_DATA_BLOCK_SIZE + CRC_BYTES];
    byte                       eepromNonGraphData[EEPROM_NON_GRAPH_DATA_SIZE + CRC_BYTES];
    WindDirectionSlices        pastWindDirs;
    Speed                      windGust10Minute;
    Heading                    windGustDirection10Minute;
    Callback *                 callback;
    std::vector<SensorStation> sensorStations;
    std::vector<Sensor>        sensors;
    float                      consoleBatteryVoltage;
    bool                       firstLoopPacket;
    std::string                firmwareDate;
    std::string                firmwareVersion;
    byte                       alarmThresholds[ALARM_THRESHOLDS_SIZE];
    VP2Logger                  log;
};
}

#endif /* VANTAGE_PRO2_STATION_H */
