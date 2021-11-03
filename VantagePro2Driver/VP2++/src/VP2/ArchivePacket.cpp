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
#include <time.h>
#include <string>
#include <ctime>
#include <sstream>
#include "VP2Logger.h"
#include "VP2Constants.h"
#include "BitConverter.h"
#include "UnitConverter.h"
#include "ArchivePacket.h"

using namespace std;

namespace vp2 {
const Temperature ArchivePacket::TEMPERATURE_SCALE = static_cast<Temperature>(10.0);
const Pressure ArchivePacket::BAROMETER_SCALE = static_cast<Pressure>(1000.0);
const UvIndex ArchivePacket::UV_INDEX_SCALE = static_cast<UvIndex>(10.0);
const Evapotranspiration ArchivePacket::ET_SCALE = static_cast<Evapotranspiration>(1000.0);
const Heading ArchivePacket::DEGREES_PER_SLICE = static_cast<Heading>(22.5);

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
ArchivePacket::ArchivePacket(const byte buffer[], int offset, Rainfall rainInterval, int archivePeriod, Speed windGust, Heading windGustDirection) :
                    log(&VP2Logger::getLogger("ArchivePacket")) {
    this->archivePeriod = archivePeriod;
    this->rainInterval = rainInterval;
    this->windGust = windGust;
    this->windGustDirection = windGustDirection;
    for (int i = 0; i < VP2Constants::APB_BYTES_PER_RECORD; i++) {
        this->buffer[i] = buffer[offset + i];
    }

    windSampleCount = BitConverter::toInt16(this->buffer, 18);
    packetTime = extractDate();
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
ArchivePacket::~ArchivePacket() {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const byte *
ArchivePacket::getBuffer() const {
    return buffer;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
int
ArchivePacket::getWindSampleCount() const {
    return windSampleCount;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
DateTime
ArchivePacket::getDateTime() const {
    return packetTime;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool ArchivePacket::isEmptyPacket() const {
    return packetTime == EMPTY_ARCHIVE_PACKET_TIME;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
DateTime
ArchivePacket::extractDate() const {
    int date = BitConverter::toInt16(buffer, APB_DATE_STAMP_OFFSET);
    int time = BitConverter::toInt16(buffer, APB_TIME_STAMP_OFFSET);
    int year = ((date >> 9) & 0x3F) + 2000;
    int month = (date >> 5) & 0xF;
    int day = date & 0x1F;
    int hour = time / 100;
    int minute = time % 100;

    time_t now = ::time(0);
    struct tm tm;
    Weather::localtime(now, tm);
    tm.tm_year = year - 1900;
    tm.tm_mon = month - 1;
    tm.tm_mday = day;
    tm.tm_hour = hour;
    tm.tm_min = minute;
    tm.tm_sec = 0;
    return mktime(&tm);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
std::string
ArchivePacket::formatMessage() const {
    ostringstream ss;
    ss << "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
    ss << "<historicalRecord>";
    DateTime archiveTime = extractDate();
    ss << "<time>" << Weather::formatDateTime(archiveTime) << "</time>";
    ss << "<duration>" << (archivePeriod * 60) << "</duration>";

    int value16 = BitConverter::toInt16(buffer, APB_OUTSIDE_TEMPERATURE_OFFSET);
    if (value16 != VP2Constants::APB_INVALID_TEMPERATURE)
        ss << "<avgOutdoorTemperature>" << UnitConverter::toCelcius((Temperature)value16 / TEMPERATURE_SCALE) << "</avgOutdoorTemperature>";

    value16 = BitConverter::toInt16(buffer, APB_HIGH_OUTSIDE_TEMPERATURE_OFFSET);
    if (value16 != VP2Constants::APB_INVALID_HIGH_TEMPERATURE)
        ss << "<highOutdoorTemperature>" << UnitConverter::toCelcius((Temperature)value16 / TEMPERATURE_SCALE) << "</highOutdoorTemperature>";

    value16 = BitConverter::toInt16(buffer, APB_LOW_OUTSIDE_TEMPERATURE_OFFSET);
    if (value16 != VP2Constants::APB_INVALID_TEMPERATURE)
        ss << "<lowOutdoorTemperature>" << UnitConverter::toCelcius((Temperature)value16 / TEMPERATURE_SCALE) << "</lowOutdoorTemperature>";

    value16 = BitConverter::toInt16(buffer, APB_RAINFALL_OFFSET);
    ss << "<rainfall>" << UnitConverter::toMillimeter((Rainfall)value16 * rainInterval) << "</rainfall>";

    value16 = BitConverter::toInt16(buffer, APB_HIGH_RAIN_RATE_OFFSET);
    ss << "<highRainfallRate>" << UnitConverter::toMillimeter((Rainfall)value16 * rainInterval) << "</highRainfallRate>";

    value16 = BitConverter::toInt16(buffer, APB_BAROMETER_OFFSET);
    if (value16 != VP2Constants::APB_INVALID_BAROMETER)
        ss << "<baroPressure>" << UnitConverter::toMillibars((Pressure)value16 / BAROMETER_SCALE) << "</baroPressure>";

    value16 = BitConverter::toInt16(buffer, APB_SOLAR_RADIATION_OFFSET);
    if (value16 != VP2Constants::APB_INVALID_SOLAR_RADIATION)
        ss << "<avgSolarRadiation>" << value16 << "</avgSolarRadiation>";
  
    value16 = BitConverter::toInt16(buffer, APB_INSIDE_TEMPERATURE_OFFSET);
    if (value16 != VP2Constants::APB_INVALID_TEMPERATURE)
        ss << "<indoorTemperature>" << UnitConverter::toCelcius((Temperature)value16 / TEMPERATURE_SCALE) << "</indoorTemperature>";

    int value8 = BitConverter::toInt8(buffer, APB_INSIDE_HUMIDITY_OFFSET);
    if (value8 != VP2Constants::APB_INVALID_HUMIDITY)
        ss << "<indoorHumidity>" << value8 << "</indoorHumidity>";

    value8 = BitConverter::toInt8(buffer, APB_OUTSIDE_HUMIDITY_OFFSET);
    if (value8 != VP2Constants::APB_INVALID_HUMIDITY)
        ss << "<outdoorHumidity>" << value8 << "</outdoorHumidity>";

    int windSpeed = BitConverter::toInt8(buffer, APB_AVG_WIND_SPEED_OFFSET);
    if (windSpeed == VP2Constants::APB_INVALID_WIND_SPEED)
        windSpeed = 0;
    
    ss << "<avgWind><speed>" << UnitConverter::toMetersPerSecond(windSpeed) << "</speed>";

    int windDirection = BitConverter::toInt8(buffer, APB_PREVAILING_WIND_DIRECTION_OFFSET);
    if (windDirection != VP2Constants::APB_INVALID_WIND_DIRECTION)
        ss << "<direction>" << (windDirection * DEGREES_PER_SLICE) << "</direction>"; 

    ss << "</avgWind>";

    windSpeed = BitConverter::toInt8(buffer, APB_HIGH_WIND_SPEED_OFFSET);
    if (windSpeed == VP2Constants::APB_INVALID_WIND_SPEED)
        windSpeed = 0;
    
    ss << "<highWind><speed>" << UnitConverter::toMetersPerSecond(windSpeed) << "</speed>"; 

    windDirection = BitConverter::toInt8(buffer, APB_DIR_OF_HIGH_WIND_SPEED_OFFSET);
    if (windDirection != VP2Constants::APB_INVALID_WIND_DIRECTION)
        ss << "<direction>" << (windDirection * DEGREES_PER_SLICE) << "</direction>";

    ss << "</highWind>";

    //
    // Use the wind gust data from the LOOP 2 packet if this archive packet is less than 10 minutes old,
    // otherwise just use the high wind speed as the gust
    //
    DateTime now = time(0);
    DateTime span = now - archiveTime;
    if (span < 10 * 60) {
        log->log(VP2Logger::VP2_DEBUG1) << "Using gust from LOOP2 packet" << endl;
        if (windGust != 0.0)
            ss << "<windGust><speed>" << windGust << "</speed><direction>" << windGustDirection << "</direction></windGust>";
    }
    else {
        log->log(VP2Logger::VP2_DEBUG1) << "Setting gust to max wind speed/direction" << endl;
        windSpeed = BitConverter::toInt8(buffer, APB_HIGH_WIND_SPEED_OFFSET);
        if (windSpeed == VP2Constants::APB_INVALID_WIND_SPEED)
            windSpeed = 0;

        windDirection = BitConverter::toInt8(buffer, APB_DIR_OF_HIGH_WIND_SPEED_OFFSET);
        ss << "<windGust><speed>" << UnitConverter::toMetersPerSecond(windSpeed) << "</speed>";

        if (windDirection != VP2Constants::APB_INVALID_WIND_DIRECTION)
            ss << "<direction>" << (windDirection * DEGREES_PER_SLICE) << "</direction>";

        ss << "</windGust>";
    }

    value8 = BitConverter::toInt8(buffer, APB_AVG_UV_INDEX_OFFSET);
    if (value8 != VP2Constants::APB_INVALID_UV_INDEX) {
        ss << "<avgUvIndex>" << ((UvIndex)value8 / UV_INDEX_SCALE) << "</avgUvIndex>";
    }

    value8 = BitConverter::toInt8(buffer, APB_ET_OFFSET);
    if (value8 != UNKNOWN_ET)
        ss << "<evapotranspiration>" << UnitConverter::toMillimeter((Evapotranspiration)value8 / ET_SCALE) << "</evapotranspiration>";

    value16 = BitConverter::toInt16(buffer, APB_HIGH_SOLAR_RADIATION_OFFSET);
    if (value16 != UNKNOWN_SOLAR_RADIATION)
        ss << "<highSolarRadiation>" << value16 << "</highSolarRadiation>";

    value8 = BitConverter::toInt8(buffer, APB_HIGH_UV_INDEX_OFFSET);
    if (value8 != VP2Constants::APB_INVALID_UV_INDEX) {
        ss << "<highUvIndex>" << ((UvIndex)value8 / UV_INDEX_SCALE) << "</highUvIndex>";
    }

    for (int i = 0; i < VP2Constants::APB_MAX_LEAF_TEMPERATURES; i++) {
        value8 = buffer[APB_LEAF_TEMPERATURE_BASE_OFFSET + i];
        if (value8 != VP2Constants::APB_INVALID_LEAF_TEMPERATURE)
            ss << ""; //leaf_temperature[{0}]={1};", i, value8 - EXTRA_TEMPERATURE_OFFSET);
    }

    ss << "<leafWetnessSensorEntries>";
    for (int i = 0; i < VP2Constants::APB_MAX_LEAF_WETNESSES; i++) {
        int leafWetness = BitConverter::toInt8(buffer, APB_LEAF_WETNESS_BASE_OFFSET + i);
        if (leafWetness != VP2Constants::APB_INVALID_LEAF_WETNESS) {
            ss << "<entry><key>" << (500 + i) << "</key><value><sensorId>" << (500 + i) << "</sensorId><sensorType>LEAF_WETNESS</sensorType>";
            ss << "<measurement xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"leafWetness\">";
            ss << leafWetness << "</measurement></value></entry>";
        }
    }
    ss << "</leafWetnessSensorEntries>";

    for (int i = 0; i < VP2Constants::APB_MAX_SOIL_TEMPERATURES; i++) {
        value8 = buffer[APB_SOIL_TEMPERATURE_BASE_OFFSET + i];
        if (value8 != VP2Constants::APB_INVALID_SOIL_TEMPERATURE)
            ss << "";//soil_temperature[{0}]={1};", i, value8 - EXTRA_TEMPERATURE_OFFSET);
    }

    ss << "<humiditySensorEntries>";
    for (int i = 0; i < VP2Constants::APB_MAX_EXTRA_HUMIDITIES; i++) {
        int humidity = BitConverter::toInt8(buffer, APB_EXTRA_HUMIDITIES_BASE_OFFSET + i);
        if (humidity != VP2Constants::APB_INVALID_HUMIDITY) {
            ss << "<entry><key>" << (200 + i) << "</key><value><sensorId>" << (200 + i) << "</sensorId><sensorType>HYGROMETER</sensorType>";
            ss << "<measurement xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"humidity\">";
            ss << humidity << "</measurement></value></entry>";
        }
    }
    ss << "</humiditySensorEntries>";

    ss << "<temperatureSensorEntries>";
    for (int i = 0; i < VP2Constants::APB_MAX_EXTRA_TEMPERATURES; i++) {
        int temperature = BitConverter::toInt8(buffer, APB_EXTRA_TEMPERATURES_BASE_OFFSET + i);
        if (temperature != VP2Constants::APB_INVALID_EXTRA_TEMPERATURE) {
            ss << "<entry><key>" << (100 + i) << "</key><value><sensorId>" << (100 + i) << "</sensorId><sensorType>THERMOMETER</sensorType>";
            ss << "<measurement xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"temperature\">";
            ss << UnitConverter::toCelcius(temperature - EXTRA_TEMPERATURE_OFFSET) << "</measurement></value></entry>";
        }
    }
    ss << "</temperatureSensorEntries>";

    ss << "<soilMoistureSensorEntries>";
    for (int i = 0; i < VP2Constants::APB_MAX_SOIL_MOISTURES; i++) {
        int soilMoisture = BitConverter::toInt8(buffer, APB_SOIL_MOISTURES_BASE_OFFSET + i);
        if (soilMoisture != VP2Constants::APB_INVALID_SOIL_MOISTURE) {
            ss << "<entry><key>" << (600 + i) << "</key><value><sensorId>" << (600 + i) << "</sensorId><sensorType>SOIL_MOISTURE</sensorType>";
            ss << "<measurement xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"soilMoisture\">";
            ss << soilMoisture << "</measurement></value></entry>";
        }
    }
    ss << "</soilMoistureSensorEntries>";
    ss << "</historicalRecord>";
    return ss.str();
}
}
