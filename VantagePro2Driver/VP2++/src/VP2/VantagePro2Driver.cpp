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
#include <stdlib.h>
#include <time.h>
#include <iostream>
#include <vector>
#include "CurrentWeather.h"
#include "CurrentWeatherPublisher.h"
#include "SensorStation.h"
#include "VP2Logger.h"
#include "VantagePro2Driver.h"
#include "VP2Constants.h"

using namespace std;
extern "C" {
extern bool signalCaught;
}

namespace vp2 {

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
VantagePro2Driver::VantagePro2Driver(ArchiveManager & archiveManager, WeatherSenseSocket & socket, CurrentWeatherPublisher & cwp, VantagePro2Station & station) :
                                                                station(station),
                                                                socket(socket),
                                                                currentWeatherPublisher(cwp),
                                                                archiveManager(archiveManager),
                                                                exitLoop(false),
                                                                receivedFirstLoopPacket(false),
                                                                nextRecord(-1),
                                                                previousNextRecord(-1),
                                                                lastPacketTime(0),
                                                                log(VP2Logger::getLogger("VantagePro2Driver")),
                                                                sensorStationSendTime(0) {
    //
    // Indicate the the console time needs to be set in the near future. 
    // We do not want the console time to be set immediately in case the computer has just started and
    // has not had a chance to synchronize its time with the Internet. This is most important with
    // computers like the Raspberry Pi.
    //
    consoleTimeSetTime = time(0) - TIME_SET_INTERVAL + (1 * 3600);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
VantagePro2Driver::~VantagePro2Driver() {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
VantagePro2Driver::connected(DateTime newestArchiveTimeFromCollector) {
    log.log(VP2Logger::VP2_DEBUG1) << "Connected with collector. Archive time = " << Weather::formatDateTime(newestArchiveTimeFromCollector) << endl;
    archiveManager.setNewestRecordTime(newestArchiveTimeFromCollector);
    lastPacketTime = newestArchiveTimeFromCollector;
    nextRecord = -1;
    string sensorMessage = Sensor::formatMessage(station.getSensors());
    socket.sendData(sensorMessage);

    string parametersMessage = parameters.formatMessage();
    socket.sendData(parametersMessage);

    const vector<SensorStation> & sensorStations = station.getSensorStations();
    string message = SensorStation::formatSensorStationMessage(sensorStations);
    socket.sendData(message);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
int
VantagePro2Driver::initialize() {
    log.log(VP2Logger::VP2_INFO) << "Initializing..." << endl;

    socket.setHistoricalReader(*this);
    station.setCallback(*this);

    if (!station.openStation()) {
        log.log(VP2Logger::VP2_ERROR) << "Failed to open weather station" << endl;
        return 1;
    }
    else {
        log.log(VP2Logger::VP2_INFO) << "Port is open" << endl;
    }

    if (!station.wakeupStation()) {
        log.log(VP2Logger::VP2_ERROR) << "Failed to wake up weather station" << endl;
        return 2;
    }
    else {
        log.log(VP2Logger::VP2_INFO) << "Weather Station is awake" << endl;
    }

    //
    // Get the information from the console that we will need for future calculations
    //
    if (!station.retrieveRainCollectorSize()) {
        log.log(VP2Logger::VP2_ERROR) << "Failed to retrieve rain collector size" << endl;
        return 3;
    }

    if (!station.retrieveArchivePeriod()) {
        log.log(VP2Logger::VP2_ERROR) << "Failed to retrieve archive period" << endl;
        return 4;
    }

    if (!station.retrieveSensorStationInfo()) {
        log.log(VP2Logger::VP2_ERROR) << "Failed to retrieve sensor station information" << endl;
        return 5;
    }

    if (!station.getParameters(parameters)) {
        log.log(VP2Logger::VP2_ERROR) << "Failed to retrieve weather station parameters" << endl;
        return 6;
    }


    //
    // Get one current weather record so that the sensors are detected
    //
    for (int i = 0; i < 5 && !receivedFirstLoopPacket; i++) {
        station.currentValuesLoop(1);
    }

    if (!receivedFirstLoopPacket) {
        log.log(VP2Logger::VP2_ERROR) << "Failed to receive a LOOP packet needed to determine current sensor suite" << endl;
        return 7;
    }

    if (!archiveManager.readArchive()) {
        log.log(VP2Logger::VP2_ERROR) << "Failed to read the archive during initialization" << endl;
        return 8;
    }

    if (!station.wakeupStation()) {
        log.log(VP2Logger::VP2_ERROR) << "Failed to wake up console after initialization" << endl;
        return 9;
    }

    log.log(VP2Logger::VP2_INFO) << "Initialization complete." << endl;

    return 0;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
VantagePro2Driver::stop() {
    exitLoop = true;
    station.closeStation();
    socket.disconnectSocket();
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Driver::processCurrentWeather(const CurrentWeather & cw) {
    //string currentWeatherMessage = cw.formatMessage();
    nextRecord = cw.getNextPacket();

    if (receivedFirstLoopPacket)
        currentWeatherPublisher.sendCurrentWeather(cw);
        //socket.sendData(currentWeatherMessage);

    log.log(VP2Logger::VP2_DEBUG1) << "Previous Next Record: " << previousNextRecord << " Next Record: " << nextRecord << endl;
    //log.log(VP2Logger::VP2_DEBUG3) << currentWeatherMessage << endl;
    receivedFirstLoopPacket = true;
    return signalCaught || previousNextRecord != nextRecord;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Driver::processArchive(const vector<ArchivePacket> & archive) {

    log.log(VP2Logger::VP2_DEBUG1) << "Processing " << archive.size() << " archive packets" << endl;

    for (vector<ArchivePacket>::const_iterator it = archive.begin(); it != archive.end(); ++it) {
        DateTime now = time(0);
        DateTime age = now - it->getDateTime();
        if (age < 3600) {
            int maxPackets = (int)((station.getArchivePeriod() * 60.0F) / ((41.0F + 1.0F - 1.0F) / 16.0F));
            int actualPackets = it->getWindSampleCount();
            int issReception = (actualPackets * 100) / maxPackets;
            if (issReception > 100)
                issReception = 100;

            log.log(VP2Logger::VP2_DEBUG2) << "IIS Reception for archive interval ending at " << it->getDateTime()
                                           << " is " << issReception
                                           << ". Max Packets = " << maxPackets
                                           << ", Actual Packets - " << actualPackets << endl;

            vector<SensorStation> sensorStations = station.getSensorStations();
            for (vector<SensorStation>::iterator it2 = sensorStations.begin(); it2 != sensorStations.end(); ++it2) {
                if (it2->getSensorStationType() == SensorStation::INTEGRATED_SENSOR_STATION)
                    it2->setLinkQuality(issReception);
            }

            string ssMessage = SensorStation::formatSensorStationStatusMessage(sensorStations, it->getDateTime());
            if (!socket.sendData(ssMessage))
                return false;
        }

        string message = it->formatMessage();
        log.log(VP2Logger::VP2_INFO) << "=== Archive === " << Weather::formatDateTime(it->getDateTime()) << " =============" << endl;
        if (!socket.sendData(message))
            return false;
    }

    return true;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
VantagePro2Driver::reopenStation() {
    station.closeStation();
    bool success = station.openStation();

    if (!success)
        log.log(VP2Logger::VP2_ERROR) << "Failed to reopen weather station" << endl;

    return success;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
VantagePro2Driver::mainLoop() {
    DateTime stationTime = station.getTime();
    log.log(VP2Logger::VP2_INFO) << "Station Time: " << Weather::formatDateTime(stationTime) << endl;
    vector<ArchivePacket> list;
    list.reserve(VP2Constants::NUM_ARCHIVE_RECORDS);

    while (!exitLoop) {
        try {
            //
            // If the weather station could not be woken, then close and open
            // the console. It has been observed that on a rare occasion the console
            // never wakes up. Only restarting this driver fixes the issue. Reopening
            // the serial port will hopefully fix this issue. It is hoped that if
            // the this does not work will cause the health monitor to restart the
            // driver, also fixing the issue.
            //
            if (!station.wakeupStation()) {
                reopenStation();
                continue;
            }

            //
            // If it has been more than a day since the time was set, set the time
            //
            DateTime now = time(0);
            if (consoleTimeSetTime + TIME_SET_INTERVAL < now) {
                if (!station.setConsoleTime())
                    log.log(VP2Logger::VP2_ERROR) << "Failed to set station time: " << endl;

                consoleTimeSetTime = now;

            }

            //
            // Get the high/low values
            //
            HiLowPacket packet;
            if (station.retrieveHiLowValues(packet))
                cout << "Got hi/low packet" << endl;

            //
            // Get the current weather values for about a minute
            //
            station.currentValuesLoop(LOOP_PACKET_CYCLES);

            if (signalCaught) {
                exitLoop = true;
                continue;
            }

            //
            // If the LOOP packet data indicates that a new archive packet is available
            // go get it.
            //
            if (previousNextRecord != nextRecord) {
                if (archiveManager.readArchive()) {
                    do {
                        archiveManager.getArchiveRecords(list);
                        if (!processArchive(list))
                            break;
                    } while (list.size() > 0);
                    previousNextRecord = nextRecord;
                }
            }
        }
        catch (std::exception & e) {
            log.log(VP2Logger::VP2_ERROR) << "Caught exception: " << e.what() << endl;     
        } 
    }
}
}
