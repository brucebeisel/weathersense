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
#include <thread>
#include <atomic>
#include <fstream>
#include "VP2Logger.h"
#include "ArchiveManager.h"
#include "VantagePro2Driver.h"
#include "CurrentWeatherPublisher.h"

using namespace std;
using namespace vp2;

atomic_bool signalCaught = false;

//#ifndef WIN32
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
sigHandler(int sig) {
    if (sig == SIGINT || sig == SIGTERM)
        signalCaught.store(true);
}
//#endif

void
consoleThreadEntry(const string & archiveFile, const string & serialPortName, const string & logFile, int baudRate) {
    VP2Logger & log = VP2Logger::getLogger("VP2 Main");
    log.log(VP2Logger::VP2_INFO) << "Starting console thread" << endl;

    CurrentWeatherPublisher cwPublisher;
    VantagePro2Station station(serialPortName, baudRate);
    ArchiveManager archiveManager(archiveFile, station);
    EventManager eventManager;
    VantagePro2Driver driver(archiveManager, cwPublisher, station, eventManager);


    try {
        if (!driver.initialize())
            return;

        driver.mainLoop();
        log.log(VP2Logger::VP2_INFO) << "Main loop returned" << endl;
    }
    catch (std::exception & e) {
        log.log(VP2Logger::VP2_ERROR) << "Caught exception from main loop " << e.what() << endl;
    }
    catch (...) {
        log.log(VP2Logger::VP2_ERROR) << "Caught unknown exception from main loop" << endl;
    }

    log.log(VP2Logger::VP2_INFO) << "Ending console thread" << endl;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
int
main(int argc, char *argv[]) {

#ifdef _WIN32
    const string archiveFile("C:\\WeatherSense\\2.4\\archive\\archive.vp2");
    const string serialPortName("COM3");
    const string logFile("C:\\WeatherSense\\2.4\\logs\\vp2_driver.log");
#else
    const string archiveFile("/weathersense/3.0/archive/vp2.archive");
    const string logFile("/weathersense/3.0/log/vp2_driver.log");
    const string serialPortName("/dev/ttyUSB0");
    signal(SIGPIPE, sigHandler);
    signal(SIGINT, sigHandler);
    signal(SIGTERM, sigHandler);
#endif
    ofstream logStream(logFile.c_str(), ios::app | ios::ate | ios::out);
    //VP2Logger::setLogStream(logStream);
    VP2Logger::setLogLevel(VP2Logger::VP2_DEBUG3);

    thread consoleThread(consoleThreadEntry, archiveFile, serialPortName, logFile, 19200);

    consoleThread.join();
}
