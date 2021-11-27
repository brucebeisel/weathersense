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
#pragma warning(disable : 4100)
#else
#include <signal.h>
#include <stdio.h>
#endif
#include <iostream>
#include <fstream>
#include "VP2Logger.h"
#include "ArchiveManager.h"
#include "VantagePro2Driver.h"
#include "CurrentWeatherPublisher.h"

using namespace std;
using namespace vp2;

extern "C" {
bool signalCaught = false;
//#ifndef WIN32
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
sigHandler(int sig) {
    if (sig == SIGINT || sig == SIGTERM)
        signalCaught = true;
}
//#endif
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
int
main(int argc, char *argv[]) {
#ifdef _WIN32
    string archiveFile("C:\\WeatherSense\\2.4\\archive\\archive.vp2");
    string serialPortName("COM3");
    string logFile("C:\\WeatherSense\\2.4\\logs\\vp2_driver.log");
#else
    string archiveFile("/weathersense/3.0/archive/vp2.archive");
    string logFile("/weathersense/3.0/log/vp2_driver.log");
    string serialPortName("/dev/ttyUSB0");
    signal(SIGPIPE, sigHandler);
    signal(SIGINT, sigHandler);
    signal(SIGTERM, sigHandler);
#endif

    ofstream logStream(logFile.c_str(), ios::app | ios::ate | ios::out);
    //VP2Logger::setLogStream(logStream);
    VP2Logger::setLogLevel(VP2Logger::VP2_DEBUG3);
    WeatherSenseSocket socket("127.0.0.1", 11461);
    CurrentWeatherPublisher cwPublisher;
    VantagePro2Station station(serialPortName, 19200);
    ArchiveManager archiveManager(archiveFile, station);
    VantagePro2Driver driver(archiveManager, socket, cwPublisher, station);

    VP2Logger & log = VP2Logger::getLogger("VP2 Main");

    /*
    vector<ArchivePacket> list;

    archiveManager.setNewestRecordTime(time(0) - (86400 * 12));
    int total = 0;
    do {
        list.clear();
        archiveManager.getArchiveRecords(list);
        cout << "Received " << list.size() << " records" << endl;
        if (list.size() > 0)
            cout << "First record: " << Weather::formatDateTime(list.at(0).getDateTime()) << " Last Record: " << Weather::formatDateTime(list.at(list.size() - 1).getDateTime()) << endl;
        total += list.size();
    } while (list.size() > 0);
    cout << "Received " << total << " total records" << endl;
    Sleep(100000);
*/
    try {
        int rv;
        if ((rv = driver.initialize()) != 0)
            return rv;

        driver.mainLoop();
        log.log(VP2Logger::VP2_INFO) << "Main loop returned" << endl;
    }
    catch (std::exception & e) {
        log.log(VP2Logger::VP2_ERROR) << "Caught exception from main loop " << e.what() << endl;
    }
    catch (...) {
    }
}
