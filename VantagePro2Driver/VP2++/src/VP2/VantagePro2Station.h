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
#define	VANTAGE_PRO2_STATION_H

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
     * Get the archive period (in minutes)
     * 
     * @return The archive period
     */
    int getArchivePeriod() const;

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
     * Get the clock time on the console.
     * 
     * @return The time
     */
    DateTime getTime();


    /**
     * Decode a buffer into a single archive packet.
     *
     * @param buffer The buffer from which to decode the archive packet
     * @param index  The index within the buffer to start decoding the archive packet
     *
     * @return The decoded archive packet
     */
    ArchivePacket convertBufferToArchivePacket(const byte * buffer, int index) const;

    /**
     * Perform a dump of the archive after the specified time.
     * 
     * @param time    The time after which to dump the archive
     * @param archive The vector into which the dumped archive packets will be returned
     * @return True if successful
     */
    bool dumpAfter(DateTime time, std::vector<ArchivePacket> & archive);

    /**
     * Retrieve various parameters from the console.
     * 
     * @param message The message in which the parameters are to be stored
     * @return True if successful
     */
    bool getParameters(ParametersMessage & message);

    /**
     * Set the console's time.
     * 
     * @return True if successful
     */
    bool setConsoleTime();

    /**
     * Retrieve the current weather by reading the LOOP and LOOP2 packet in a loop.
     * 
     * @param records The number of times to execute the loop before returning
     */
    void currentValuesLoop(int records);

    /**
     * Set the callback object that will be called when current weather, archive packets are received.
     * 
     * @param callback The callback object
     */
    void setCallback(Callback & callback);

    /**
     * Retrieve the rain collector size from the console.
     * 
     * @return True if the rain collector size was stored successfully
     */
    bool retrieveRainCollectorSize();

    /**
     * Get the size of the rain collector that was previously retrieved
     *
     * @return The size of the rain collector in inches
     */
    Rainfall    getRainCollectorSize() const;

    /**
     * Retrieve the archive period from the console.
     * 
     * @return True if the archive period was stored successfully
     */
    bool retrieveArchivePeriod();

    /**
     * Retrieve information about the sensor stations that are communicating with the console.
     * 
     * @return True if the sensor station information was stored successfully
     */
    bool retrieveSensorStationInfo();

    bool retrieveAlarmSettings();

    bool retrieveHiLowValues(HiLowPacket &packet);


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


    /**
     * Send a command that expects on "OK" response.

     * @param command The command to be sent to the VP2 console
     * @return True if the command was sent successfully
     */
    bool        sendOKedCommand(const std::string & command);

    /**
     * Send a command that expects an ACK response.
     *
     * @param command The command to be sent to the VP2 console
     * @return True if the command was sent successfully
     */
    bool        sendAckedCommand(const std::string & command);
    bool        consumeAck();
    bool        readLoopPacket(LoopPacket & loopPacket);
    bool        readLoop2Packet(Loop2Packet & loop2Packet);
    void        decodeArchivePage(std::vector<ArchivePacket> &, const byte * buffer, int firstRecord, DateTime newestPacketTime);
    bool        processArchivePage(std::vector<ArchivePacket> &, int firstRecord, DateTime newestPacketTime);
    std::string getStringValue(const std::string & command);
    bool        readEEPROM(const std::string & address, int count);
    void        dump(std::vector<ArchivePacket> & list);
    bool        archivePacketContainsData(const byte * buffer, int offset);

    SerialPort                 serialPort;
    std::string                portName;
    int                        baudRate;
    byte                       buffer[512];
    Rainfall                   rainCollectorSize;
    WindDirectionSlices        pastWindDirs;
    Speed                      windGust10Minute;
    Heading                    windGustDirection10Minute;
    Callback *                 callback;
    std::vector<SensorStation> sensorStations;
    int                        archivePeriod;
    std::vector<Sensor>        sensors;
    float                      consoleBatteryVoltage;
    bool                       firstLoopPacket;
    VP2Logger                  log;
};
}

#endif	/* VANTAGE_PRO2_STATION_H */
