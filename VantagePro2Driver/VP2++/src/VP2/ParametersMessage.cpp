/* 
 * Copyright (C) 2016 Bruce Beisel
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
#include <sstream>
#include "UnitConverter.h"
#include "ParametersMessage.h"

using namespace std;

namespace vp2 {
const std::string ParametersMessage::MANUFACTURER = "Davis Instruments";
const std::string ParametersMessage::MODEL = "Vantage Pro 2";

ParametersMessage::ParametersMessage() {
}

ParametersMessage::~ParametersMessage() {
}

string
ParametersMessage::getFirmwareDate() const {
    return firmwareDate;
}

void
ParametersMessage::setFirmwareDate(const string & value) {
    firmwareDate = value;
}

string
ParametersMessage::getFirmwareVersion() const {
    return firmwareVersion;
}

void
ParametersMessage::setFirmwareVersion(const string & value) {
    firmwareVersion = value;
}

double
ParametersMessage::getLatitude() const {
    return latitude;
}

void
ParametersMessage::setLatitude(double value) {
    latitude = value;
}

double
ParametersMessage::getLongitude() const {
    return longitude;
}

void
ParametersMessage::setLongitude(double value) {
    longitude = value;
}

int
ParametersMessage::getElevation() const {
    return elevation;
}

void
ParametersMessage::setElevation(int value) {
    elevation = value;
}

int
ParametersMessage::getRainSeasonStart() const {
    return rainSeasonStart;
}

void
ParametersMessage::setRainSeasonStart(int value) {
    rainSeasonStart = value;
}

int
ParametersMessage::getArchivePeriod() const {
    return archivePeriod;
}

void
ParametersMessage::setArchivePeriod(int value) {
    archivePeriod = value;
}

int
ParametersMessage::getWindCupSize() const {
    return windCupSize;
}

void
ParametersMessage::setWindCupSize(int value) {
    windCupSize = value;
}

Rainfall
ParametersMessage::getRainCollectorSize() const {
    return rainCollectorSize;
}

void
ParametersMessage::setRainCollectorSize(Rainfall value) {
    rainCollectorSize = value;
}

/// <summary>
/// Format the parameters message
/// </summary>
/// <returns>The message as a string</returns>
string
ParametersMessage::formatMessage() const {
    ostringstream ss;
    ss << "wsParametersMessage '{";
    ss << "\"manufacturer\":\"" << MANUFACTURER << "\",";
    ss << "\"model\":\"" << MODEL << "</model>";
    ss << "\"firmwareDate\":\"" << firmwareDate << "\","
    ss << "\"firmwareVersion\":\"" << firmwareVersion << "\",";
    ss << "\"location\":{<latitude\":" << latitude << ",";
    ss << "\"longitude\":" << longitude << ",";
    ss << "\"altitude\":" << UnitConverter::feetToMillimeters(elevation) << "},";
    ss << "\"weatherYearStartMonth\":" << rainSeasonStart << ",";
    ss << "\"parameters\":{";
    ss << "{\"key\":" << "Archive Period" << ",\"value\":\"" << archivePeriod << "\"}";
    ss << "{\"key\":" << "Wind Cup Size" << ",\"value\":\"" << (windCupSize == 0 ? "Small" : "Large") << "\"}";
    ss << "{\"key\":" << "Rain Collector Size" << ",\"value\":\"" << rainCollectorSize << " inches" << "\"}";
    ss << "}";
    ss << "}'";

    return ss.str();
}
}