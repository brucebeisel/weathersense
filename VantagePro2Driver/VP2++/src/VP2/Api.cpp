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
#include <iostream>
#include "VantagePro2Station.h"
#include "Api.h"

using namespace std;

namespace vp2 {

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Api::Api(VantagePro2Station & station) : station(station) {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Api::requestSensorStationIds() {
    ostringstream oss;
    std::vector<StationId> list;

    station.retrieveReceiverList(&list);

    oss << "{ \"stationIds\": [";
    for (std::vector<StationId>::iterator it = list.begin(); it != list.end(); ++it) {
        if (it != list.begin())
            oss << ",";
        oss << *it;
    }
    oss << "]}";

    std::string response(oss.str());

}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Api::requestCurrentWeather() {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Api::requestArchiveData(DateTime after) {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Api::requestConfigurationInformation() {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Api::updateConfigurationInformation() {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Api::requestSensorStationStatus() {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Api::updateYearlyET(double yearlyET) {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Api::updateYearlyRainfall(double yearlyRain) {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Api::requestHighLowData() {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Api::clearArchive() {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Api::clearExtremeValues(bool dailyLows, bool monthlyLows, bool yearlyLows, bool dailyHighs, bool monthlyHighs, bool yearlyHighs) {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Api::clearAlarms() {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Api::clearCurrentData() {
    if (station.clearCurrentData())
        sendResponse("SUCCESS");
    else
        sendResponse("FAILURE");
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Api::clearGraphData() {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Api::clearCumulativeValue(int which) {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Api::synchronizeTime() {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Api::updateArchiveInterval(int intervalMinutes) {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Api::controlConsoleLamp(bool on) {
    if (station.controlConsoleLamp(on))
        sendResponse("SUCCESS");
    else
        sendResponse("FAILURE");
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Api::sendResponse(const std::string & response) {
    cout << "Sending RESTful response: " << response << endl;
}

}
