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
#include <time.h>
#include <string>
#include <ctime>
#include <sstream>
#include "VP2Logger.h"
#include "VP2Constants.h"
#include "BitConverter.h"
#include "UnitConverter.h"
#include "ArchivePacket.h"
#include "JsonUtils.h"
#include "CurrentWeather.h"

using namespace std;

namespace vp2 {
const Temperature ArchivePacket::TEMPERATURE_SCALE = static_cast<Temperature>(10.0);
const Pressure ArchivePacket::BAROMETER_SCALE = static_cast<Pressure>(1000.0);
const UvIndex ArchivePacket::UV_INDEX_SCALE = static_cast<UvIndex>(10.0);
const Evapotranspiration ArchivePacket::ET_SCALE = static_cast<Evapotranspiration>(1000.0);
const Heading ArchivePacket::DEGREES_PER_SLICE = static_cast<Heading>(22.5);

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

ArchivePacket::~ArchivePacket() {
}

const byte *
ArchivePacket::getBuffer() const {
    return buffer;
}

int
ArchivePacket::getWindSampleCount() const {
    return windSampleCount;
}

DateTime
ArchivePacket::getDateTime() const {
    return packetTime;
}

bool ArchivePacket::isEmptyPacket() const {
    return packetTime == EMPTY_ARCHIVE_PACKET_TIME;
}

DateTime
ArchivePacket::extractDate() const {
    int date = BitConverter::toInt16(buffer, 0);
    int time = BitConverter::toInt16(buffer, 2);
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

/// <summary>
/// Format the WeatherSense historical record XML message.
/// <para>The message will only include those values that are valid (not dashed)</para>
/// </summary>
/// <returns>The message</returns>
std::string
ArchivePacket::formatMessage() const {
    ostringstream ss;
    ss << "archiveMessage '{";
    DateTime archiveTime = extractDate();
    ss << "\"time\":" << Weather::formatDateTime(archiveTime) << ",";
    ss << "\"duration\":" << (archivePeriod * 60) << ",";

    int value16 = BitConverter::toInt16(buffer, 4);
    if (value16 != VP2Constants::APB_INVALID_TEMPERATURE)
        ss << "\"avgOutdoorTemperature\":" << UnitConverter::toCelcius((Temperature)value16 / TEMPERATURE_SCALE) << ",";

    value16 = BitConverter::toInt16(buffer, 6);
    if (value16 != VP2Constants::APB_INVALID_HIGH_TEMPERATURE)
        ss << "\"highOutdoorTemperature\":" << UnitConverter::toCelcius((Temperature)value16 / TEMPERATURE_SCALE) << ",";

    value16 = BitConverter::toInt16(buffer, 8);
    if (value16 != VP2Constants::APB_INVALID_TEMPERATURE)
        ss << "\"lowOutdoorTemperature\":" << UnitConverter::toCelcius((Temperature)value16 / TEMPERATURE_SCALE) << ",";

    value16 = BitConverter::toInt16(buffer, 10);
    ss << "\"rainfall\":" << UnitConverter::toMillimeter((Rainfall)value16 * rainInterval) << ",";

    value16 = BitConverter::toInt16(buffer, 12);
    ss << "\"highRainfallRate\":" << UnitConverter::toMillimeter((Rainfall)value16 * rainInterval) << ",";

    value16 = BitConverter::toInt16(buffer, 14);
    if (value16 != VP2Constants::APB_INVALID_BAROMETER)
        ss << "\"baroPressure\":" << UnitConverter::toMillibars((Pressure)value16 / BAROMETER_SCALE) << ",";

    value16 = BitConverter::toInt16(buffer, 16);
    if (value16 != VP2Constants::APB_INVALID_SOLAR_RADIATION)
        ss << "\"avgSolarRadiation\":" << value16 << ",";
  
    value16 = BitConverter::toInt16(buffer, 20);
    if (value16 != VP2Constants::APB_INVALID_TEMPERATURE)
        ss << "\"indoorTemperature\":" << UnitConverter::toCelcius((Temperature)value16 / TEMPERATURE_SCALE) << ",";

    int value8 = BitConverter::toInt8(buffer, 22);
    if (value8 != VP2Constants::APB_INVALID_HUMIDITY)
        ss << "\"indoorHumidity\":" << value8 << "<,";

    value8 = BitConverter::toInt8(buffer, 23);
    if (value8 != VP2Constants::APB_INVALID_HUMIDITY)
        ss << "\"outdoorHumidity\":" << value8 << ",";

    int windSpeed = BitConverter::toInt8(buffer, 24);
    if (windSpeed == VP2Constants::APB_INVALID_WIND_SPEED)
        windSpeed = 0;
    
    ss << "\"avgWind\":\"speed\":" << UnitConverter::toMetersPerSecond(windSpeed);

    int windDirection = BitConverter::toInt8(buffer, 27);
    if (windDirection != VP2Constants::APB_INVALID_WIND_DIRECTION)
        ss << ",\"direction\":" << (windDirection * DEGREES_PER_SLICE);

    ss << "},";

    windSpeed = BitConverter::toInt8(buffer, 25);
    if (windSpeed == VP2Constants::APB_INVALID_WIND_SPEED)
        windSpeed = 0;
    
    ss << "\"highWind\":{\"speed\":" << UnitConverter::toMetersPerSecond(windSpeed);

    windDirection = BitConverter::toInt8(buffer, 26);
    if (windDirection != VP2Constants::APB_INVALID_WIND_DIRECTION)
        ss << ",\"direction\"" << (windDirection * DEGREES_PER_SLICE);

    ss << "},";

    //
    // Use the wind gust data from the LOOP 2 packet if this archive packet is less than 10 minutes old,
    // otherwise just use the high wind speed as the gust
    //
    DateTime now = time(0);
    DateTime span = now - archiveTime;
    if (span < 10 * 60) {
        log->log(VP2Logger::VP2_DEBUG1) << "Using gust from LOOP2 packet" << endl;
        if (windGust != 0.0)
            ss << "\"windGust\":{\"speed\":" << windGust << ",\"direction\":" << windGustDirection << "},";
    }
    else {
        log->log(VP2Logger::VP2_DEBUG1) << "Setting gust to max wind speed/direction" << endl;
        windSpeed = BitConverter::toInt8(buffer, 25);
        if (windSpeed == VP2Constants::APB_INVALID_WIND_SPEED)
            windSpeed = 0;

        windDirection = BitConverter::toInt8(buffer, 26);
        ss << "\"windGust\":{\"speed\":" << UnitConverter::toMetersPerSecond(windSpeed);

        if (windDirection != VP2Constants::APB_INVALID_WIND_DIRECTION)
            ss << ",\"direction\":" << (windDirection * DEGREES_PER_SLICE);

        ss << "},";
    }

    value8 = BitConverter::toInt8(buffer, 28);
    if (value8 != VP2Constants::APB_INVALID_UV_INDEX)
        ss << "\"avgUvIndex\":" << ((UvIndex)value8 / UV_INDEX_SCALE) << ",";

    value8 = BitConverter::toInt8(buffer, 29);
    if (value8 != UNKNOWN_ET)
        ss << "\"evapotranspiration\":" << UnitConverter::toMillimeter((Evapotranspiration)value8 / ET_SCALE) << ",";

    value16 = BitConverter::toInt16(buffer, 30);
    if (value16 != UNKNOWN_SOLAR_RADIATION)
        ss << "\"highSolarRadiation\":" << value16 << ",";

    value8 = BitConverter::toInt8(buffer, 32);
    if (value8 != VP2Constants::APB_INVALID_UV_INDEX)
        ss << "\"highUvIndex\":" << ((UvIndex)value8 / UV_INDEX_SCALE) << ",";

    ss << "\"leafWetnessSensorEntries\":{";
    bool addComma = false;
    for (int i = 0; i < VP2Constants::APB_MAX_LEAF_WETNESSES; i++) {
        int leafWetness = BitConverter::toInt8(buffer, 36 + i);
        if (leafWetness != VP2Constants::APB_INVALID_LEAF_WETNESS) {
            JsonUtils::formatSensorMeasurement(ss, addComma, CurrentWeather::LEAF_WETNESS_BASE_SENSOR_ID +i, "LEAF_WETNESS", static_cast<double>(leafWetness));
            addComma = true;
        }
    }
    ss << "},";

    ss << "\"humiditySensorEntries\":{";
    addComma = false;
    for (int i = 0; i < VP2Constants::APB_MAX_EXTRA_HUMIDITIES; i++) {
        int humidity = BitConverter::toInt8(buffer, 43 + i);
        if (humidity != VP2Constants::APB_INVALID_HUMIDITY) {
            JsonUtils::formatSensorMeasurement(ss, addComma, CurrentWeather::HYGROMETER_BASE_SENSOR_ID + i, "HYGROMETER", static_cast<double>(humidity));
            addComma = true;
        }
    }
    ss << "},";

    ss << "\"temperatureSensorEntries\":{";
    addComma = false;
    for (int i = 0; i < VP2Constants::APB_MAX_EXTRA_TEMPERATURES; i++) {
        int temperature = BitConverter::toInt8(buffer, 45 + i);
        if (temperature != VP2Constants::APB_INVALID_EXTRA_TEMPERATURE) {
            temperature = UnitConverter::toCelcius(temperature - EXTRA_TEMPERATURE_OFFSET);
            JsonUtils::formatSensorMeasurement(ss, addComma, CurrentWeather::THERMOMETER_BASE_SENSOR_ID + i, "THERMOMETER", static_cast<double>(temperature));
            addComma = true;
        }
    }

    for (int i = 0; i < VP2Constants::APB_MAX_SOIL_TEMPERATURES; i++) {
        int temperature = UnitConverter::toCelcius(buffer[38 + i] - EXTRA_TEMPERATURE_OFFSET);
        if (temperature != VP2Constants::APB_INVALID_SOIL_TEMPERATURE) {
            temperature = UnitConverter::toCelcius(temperature - EXTRA_TEMPERATURE_OFFSET);
            JsonUtils::formatSensorMeasurement(ss, addComma, CurrentWeather::SOIL_TEMPERATURE_BASE_SENSOR_ID + i, "SOIL_TEMPERATURE", static_cast<double>(temperature));
            addComma = true;
        }
    }

    for (int i = 0; i < VP2Constants::APB_MAX_LEAF_TEMPERATURES; i++) {
        int temperature = UnitConverter::toCelcius(buffer[34 + i] - EXTRA_TEMPERATURE_OFFSET);
        if (value8 != VP2Constants::APB_INVALID_LEAF_TEMPERATURE) {
            temperature = UnitConverter::toCelcius(temperature - EXTRA_TEMPERATURE_OFFSET);
            JsonUtils::formatSensorMeasurement(ss, addComma, CurrentWeather::LEAF_TEMPERATURE_BASE_SENSOR_ID + i, "LEAF_TEMPERATURE", static_cast<double>(temperature));
            addComma = true;
        }
    }

    ss << "},";

    ss << "\"soilMoistureSensorEntries\":{";
    addComma = false;
    for (int i = 0; i < VP2Constants::APB_MAX_SOIL_MOISTURES; i++) {
        int soilMoisture = BitConverter::toInt8(buffer, 48 + i);
        if (soilMoisture != VP2Constants::APB_INVALID_SOIL_MOISTURE) {
            JsonUtils::formatSensorMeasurement(ss, addComma, CurrentWeather::SOIL_MOISTURE_BASE_SENSOR_ID + i, "SOIL_MOISTURE", static_cast<double>(soilMoisture));
            addComma = true;
        }
    }
    ss << "}" << "}'";
    return ss.str();
}
}
