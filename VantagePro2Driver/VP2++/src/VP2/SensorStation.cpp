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
#include <iostream>
#include <sstream>
#include "SensorStation.h"

using namespace std;

namespace vp2 {
static const char *STATION_TYPES[] = {
    "INTEGRATED_SENSOR_STATION",
    "TEMPERATURE_ONLY",
    "HUMIDITY_ONLY",
    "TEMPERATURE_HUMIDITY",
    "ANEMOMETER",
    "RAIN",
    "LEAF",
    "SOIL",
    "SOIL_LEAF"
};

SensorStation::SensorStation(SensorStation::SensorStationType type, int sensorIndex) : type(type), sensorIndex(sensorIndex) {
}

SensorStation::SensorStationType
SensorStation::getSensorStationType() const {
    return type;
}

int
SensorStation::getSensorIndex() const {
    return sensorIndex;
}

bool
SensorStation::getBatteryStatus() const {
    return batteryStatus;
}
void
SensorStation::setBatteryStatus(bool value) {
    batteryStatus = value;
}

int
SensorStation::getLinkQuality() const {
    return linkQuality;
}

void
SensorStation::setLinkQuality(int value) {
    linkQuality = value;
}


string
SensorStation::formatSensorStationMessage(const vector<SensorStation> & list) {
    ostringstream ss;
    ss << "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
    ss << "<sensorStationMessage>";
    for (vector<SensorStation>::const_iterator it = list.begin(); it != list.end(); ++it) {
        ss << "<sensorStation>";
        ss << "<name>Sensor Station - "<< it->sensorIndex << "</name><type>" << STATION_TYPES[it->type] << "</type><sensorStationId>" << it->sensorIndex << "</sensorStationId>";
        ss <<"</sensorStation>";
    }

    ss <<"</sensorStationMessage>";

    return ss.str();
}

string
SensorStation::formatSensorStationStatusMessage(const vector<SensorStation> & list, DateTime time) {
    ostringstream ss;
    
    ss << "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
       << "<sensorStationStatusMessage>";

    for (vector<SensorStation>::const_iterator it = list.begin(); it != list.end(); ++it) {
        ss << "<sensorStationStatus>";
        ss << "<time>" << Weather::formatDateTime(time) << "</time><sensorStationId>" << it->getSensorIndex() << "</sensorStationId><batteryOk>" << (it->getBatteryStatus() ? "true" : "false") << "</batteryOk>";
        if (it->getSensorStationType() == INTEGRATED_SENSOR_STATION)
            ss << "<linkQuality>" << it->getLinkQuality() << "</linkQuality>";
        ss << "</sensorStationStatus>";
    }
    ss << "</sensorStationStatusMessage>";

    return ss.str();
}

ostream &
operator<<(ostream & os, const SensorStation & ss) {
    os << "Station Type: " << ss.type << ", Station Index: " << ss.sensorIndex;
    return os;
}
}
