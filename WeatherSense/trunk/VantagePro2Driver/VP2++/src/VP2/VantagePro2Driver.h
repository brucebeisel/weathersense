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
#ifndef VANTAGE_PRO2_DRIVER_H
#define	VANTAGE_PRO2_DRIVER_H
#ifdef _WIN32
#pragma warning(disable : 4512)
#endif

#include <string>
#include "WeatherSenseSocket.h"
#include "Weather.h"
#include "ArchiveManager.h"
#include "ParametersMessage.h"
#include "SensorStation.h"
#include "VantagePro2Station.h"

namespace vp2 {
class VP2Logger;
class CurrentWeather;

/**
 * Class that coordinates the communications with the Vantage Pro 2 console.
 */
class VantagePro2Driver : public WeatherSenseSocket::HistoricalReader, public VantagePro2Station::Callback {
public:
    /**
     * Constructor.
     * 
     * @param archiveManager The archive manager that will maintain the file containing the raw archive packets
     * @param socket The object that communicates with the WeatherSense collector
     * @param station The object that handles the command protocols with the VP2 console
     */
    VantagePro2Driver(ArchiveManager & archiveManager, WeatherSenseSocket & socket, VantagePro2Station & station);

    /**
     * Destructor.
     */
    virtual ~VantagePro2Driver();

    /**
     * Intialize the driver.
     * 
     * @return 0 on success or non-zero on error
     */
    int initialize();

    /**
     * Request that the main loop exits.
     */
    void stop();

    /**
     * The main loop that is the main "thread" of the driver.
     */
    void mainLoop();

private:
    /**
     * The number of LOOP/LOOP2 packet pairs that are received in succession. Note that if a new archive record is available
     * the loop packet cycle will be interrupted early.
     */
    static const int LOOP_PACKET_CYCLES = 12;
    /**
     * How often to set the time on the console.
     */
    static const int TIME_SET_INTERVAL = 86400;

    /**
     * Called when a valid LOOP/LOOP2 packet pair is received.
     * 
     * @param cw The current weather
     * @return True if the LOOP/LOOP2 processing should continue
     */
    bool processCurrentWeather(const CurrentWeather & cw);

    /**
     * Process a page of archive packets, the VP2 archive page is up to 5 packets.
     * 
     * @param archive The collection of archive packets
     * @return True if the archive packets were processed successfully
     */
    bool processArchive(const std::vector<ArchivePacket> & archive);

    /**
     * Called when a connection is established with the WeatherSense collector.
     * 
     * @param newestArchiveTimeFromCollector The time of the collector reported as the latest in the database
     */
    void connected(DateTime newestArchiveTimeFromCollector);

    VantagePro2Station & station;
    WeatherSenseSocket & socket;
    ArchiveManager & archiveManager;
    bool exitLoop;
    bool receivedFirstLoopPacket;
    int thread;
    int nextRecord;
    int previousNextRecord;
    DateTime lastPacketTime;
    DateTime consoleTimeSetTime;
    DateTime sensorStationSendTime;
    ParametersMessage parameters;
    VP2Logger log;
};

}
#endif	/* VANTAGE_PRO2_DRIVER_H */