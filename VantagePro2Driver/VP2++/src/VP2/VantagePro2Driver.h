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
#ifndef VANTAGE_PRO2_DRIVER_H
#define VANTAGE_PRO2_DRIVER_H
#ifdef _WIN32
#pragma warning(disable : 4512)
#endif

#include <string>
#include "Weather.h"
#include "ArchiveManager.h"
#include "ParametersMessage.h"
#include "SensorStation.h"
#include "VantagePro2Station.h"

namespace vp2 {
class VP2Logger;
class CurrentWeather;
class CurrentWeatherPublisher;

/**
 * Class that coordinates the communications with the Vantage Pro 2 console.
 */
class VantagePro2Driver : public VantagePro2Station::Callback {
public:
    /**
     * Constructor.
     * 
     * @param archiveManager The archive manager that will maintain the file containing the raw archive packets
     * @param cwp            The publisher that will be called each time a current weather record has been received
     * @param station        The object that handles the command protocols with the VP2 console
     */
    VantagePro2Driver(ArchiveManager & archiveManager, CurrentWeatherPublisher & cwp, VantagePro2Station & station);

    /**
     * Destructor.
     */
    virtual ~VantagePro2Driver();

    /**
     * Initialize the driver.
     * 
     * @return True on success
     */
    bool initialize();

    /**
     * Close and open the station.
     */
    bool reopenStation();

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

    static const int INITIAL_LOOP_PACKET_RETRIES = 5;

    /**
     * How often to set the time on the console.
     */
    static const int TIME_SET_INTERVAL = 3600;

    /**
     * Called when a valid LOOP/LOOP2 packet pair is received.
     * 
     * @param cw The current weather
     * @return True if the LOOP/LOOP2 processing should continue
     */
    bool processCurrentWeather(const CurrentWeather & cw);

    VantagePro2Station &      station;
    CurrentWeatherPublisher & currentWeatherPublisher;
    ArchiveManager &          archiveManager;
    bool                      exitLoop;
    bool                      receivedFirstLoopPacket;
    int                       nextRecord;
    int                       previousNextRecord;
    DateTime                  lastArchivePacketTime;
    DateTime                  consoleTimeSetTime;
    DateTime                  sensorStationSendTime;
    ParametersMessage         parameters;
    VP2Logger                 log;
};

}
#endif /* VANTAGE_PRO2_DRIVER_H */
