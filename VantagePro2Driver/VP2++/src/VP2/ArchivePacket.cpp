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
#include "VP2Decoder.h"
#include "BitConverter.h"
#include "UnitConverter.h"
#include "ArchivePacket.h"

using namespace std;

namespace vp2 {

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
ArchivePacket::ArchivePacket(const byte buffer[], int offset, Rainfall rainInterval, int archivePeriod, Speed windGust, Heading windGustDirection) :
                    log(&VP2Logger::getLogger("ArchivePacket")) {
    this->archivePeriod = archivePeriod;
    this->rainInterval = rainInterval;
    this->windGust = windGust;
    this->windGustDirection = windGustDirection;

    //
    // Copy the packet from the passed in buffer to the buffer member
    //
    for (int i = 0; i < BYTES_PER_PACKET; i++) {
        this->buffer[i] = buffer[offset + i];
    }

    windSampleCount = BitConverter::toInt16(this->buffer, 18);
    packetTime = extractArchiveDate();
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
ArchivePacket::extractArchiveDate() const {
    int date = BitConverter::toInt16(buffer, DATE_STAMP_OFFSET);
    int time = BitConverter::toInt16(buffer, TIME_STAMP_OFFSET);
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
    DateTime archiveTime = extractArchiveDate();
    ss << "<time>" << Weather::formatDateTime(archiveTime) << "</time>";
    ss << "<duration>" << (archivePeriod * 60) << "</duration>";

    bool valid;

    Temperature t = VP2Decoder::decode16BitTemperature(buffer, OUTSIDE_TEMPERATURE_OFFSET, valid);
    if (valid)
        ss << "<avgOutdoorTemperature>" << t << "</avgOutdoorTemperature>";

    t = VP2Decoder::decode16BitTemperature(buffer, HIGH_OUTSIDE_TEMPERATURE_OFFSET, valid);
    if (valid)
        ss << "<highOutdoorTemperature>" << t << "</highOutdoorTemperature>";

    t = VP2Decoder::decode16BitTemperature(buffer, LOW_OUTSIDE_TEMPERATURE_OFFSET, valid);
    if (valid)
        ss << "<lowOutdoorTemperature>" << t << "</lowOutdoorTemperature>";

    int value16 = BitConverter::toInt16(buffer, RAINFALL_OFFSET);
    ss << "<rainfall>" << UnitConverter::toMillimeter((Rainfall)value16 * rainInterval) << "</rainfall>";

    value16 = BitConverter::toInt16(buffer, HIGH_RAIN_RATE_OFFSET);
    ss << "<highRainfallRate>" << UnitConverter::toMillimeter((Rainfall)value16 * rainInterval) << "</highRainfallRate>";

    Pressure baroPressure = VP2Decoder::decodeBarometricPressure(buffer, BAROMETER_OFFSET, valid);
    if (valid)
        ss << "<baroPressure>" << baroPressure << "</baroPressure>";

    SolarRadiation sr = VP2Decoder::decodeSolarRadiation(buffer, SOLAR_RADIATION_OFFSET, valid);
    if (valid)
        ss << "<avgSolarRadiation>" << sr << "</avgSolarRadiation>";
  
    t = VP2Decoder::decode16BitTemperature(buffer, INSIDE_TEMPERATURE_OFFSET, valid);
    if (valid)
        ss << "<indoorTemperature>" << t << "</indoorTemperature>";

    Humidity h = VP2Decoder::decodeHumidity(buffer, INSIDE_HUMIDITY_OFFSET, valid);
    if (valid)
        ss << "<indoorHumidity>" << h << "</indoorHumidity>";

    h = VP2Decoder::decodeHumidity(buffer, OUTSIDE_HUMIDITY_OFFSET, valid);
    if (valid)
        ss << "<outdoorHumidity>" << h << "</outdoorHumidity>";

    //
    // Both wind speed and direction must be valid to generate the XML
    //
    bool valid2;
    Speed windSpeed = VP2Decoder::decodeWindSpeed(buffer, AVG_WIND_SPEED_OFFSET, valid);
    Heading windDir = VP2Decoder::decodeWindDirectionSlice(buffer, PREVAILING_WIND_DIRECTION_OFFSET, valid2);

    if (valid && valid2) {
        ss << "<avgWind><speed>" << windSpeed << "</speed>"
           << "<direction>" << windDir << "</direction>"
           << "</avgWind>";
    }

    windSpeed = VP2Decoder::decodeWindSpeed(buffer, HIGH_WIND_SPEED_OFFSET, valid);
    windDir = VP2Decoder::decodeWindDirectionSlice(buffer, DIR_OF_HIGH_WIND_SPEED_OFFSET, valid2);

    if (valid && valid2) {
        ss << "<highWind><speed>" << windSpeed << "</speed>"
           << "<direction>" << windDir << "</direction>"
           << "</highWind>";
    }

/* Decided to remove the wind gust element as the high wind speed is essentially the wind gust
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
        windSpeed = BitConverter::toInt8(buffer, HIGH_WIND_SPEED_OFFSET);
        if (windSpeed == VP2Constants::APB_INVALID_WIND_SPEED)
            windSpeed = 0;

        windDirection = BitConverter::toInt8(buffer, DIR_OF_HIGH_WIND_SPEED_OFFSET);
        ss << "<windGust><speed>" << UnitConverter::toMetersPerSecond(windSpeed) << "</speed>";

        if (windDirection != VP2Constants::APB_INVALID_WIND_DIRECTION)
            ss << "<direction>" << (windDirection * DEGREES_PER_SLICE) << "</direction>";

        ss << "</windGust>";
    }
*/

    UvIndex uvIndex = VP2Decoder::decodeUvIndex(buffer, AVG_UV_INDEX_OFFSET, valid);
    if (valid)
        ss << "<avgUvIndex>" << uvIndex << "</avgUvIndex>";

    Evapotranspiration et = VP2Decoder::decodeDayET(buffer, ET_OFFSET, valid);
    if (valid)
        ss << "<evapotranspiration>" << et << "</evapotranspiration>";

    sr = VP2Decoder::decodeSolarRadiation(buffer, HIGH_SOLAR_RADIATION_OFFSET, valid);
    if (valid)
        ss << "<highSolarRadiation>" << value16 << "</highSolarRadiation>";

    uvIndex = VP2Decoder::decodeUvIndex(buffer, HIGH_UV_INDEX_OFFSET, valid);
    if (valid)
        ss << "<highUvIndex>" << uvIndex << "</highUvIndex>";

/*
    for (int i = 0; i < VP2Constants::APB_MAX_LEAF_TEMPERATURES; i++) {
        value8 = buffer[LEAF_TEMPERATURE_BASE_OFFSET + i];
        if (value8 != VP2Constants::APB_INVALID_LEAF_TEMPERATURE)
            ss << ""; //leaf_temperature[{0}]={1};", i, value8 - EXTRA_TEMPERATURE_OFFSET);
    }
*/

/*
    ss << "<leafWetnessSensorEntries>";
    for (int i = 0; i < VP2Constants::APB_MAX_LEAF_WETNESSES; i++) {
        int leafWetness = BitConverter::toInt8(buffer, LEAF_WETNESS_BASE_OFFSET + i);
        if (leafWetness != VP2Constants::APB_INVALID_LEAF_WETNESS) {
            ss << "<entry><key>" << (500 + i) << "</key><value><sensorId>" << (500 + i) << "</sensorId><sensorType>LEAF_WETNESS</sensorType>";
            ss << "<measurement xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"leafWetness\">";
            ss << leafWetness << "</measurement></value></entry>";
        }
    }
    ss << "</leafWetnessSensorEntries>";
*/

/*
    for (int i = 0; i < VP2Constants::APB_MAX_SOIL_TEMPERATURES; i++) {
        value8 = buffer[SOIL_TEMPERATURE_BASE_OFFSET + i];
        if (value8 != VP2Constants::APB_INVALID_SOIL_TEMPERATURE)
            ss << "";//soil_temperature[{0}]={1};", i, value8 - EXTRA_TEMPERATURE_OFFSET);
    }
*/

    ss << "<humiditySensorEntries>";
    for (int i = 0; i < MAX_EXTRA_HUMIDITIES; i++) {
        h = VP2Decoder::decodeHumidity(buffer, EXTRA_HUMIDITIES_BASE_OFFSET + i, valid);
        if (valid) {
            ss << "<entry><key>" << (200 + i) << "</key><value><sensorId>" << (200 + i) << "</sensorId><sensorType>HYGROMETER</sensorType>";
            ss << "<measurement xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"humidity\">";
            ss << h << "</measurement></value></entry>";
        }
    }
    ss << "</humiditySensorEntries>";

    ss << "<temperatureSensorEntries>";
    for (int i = 0; i < MAX_EXTRA_TEMPERATURES; i++) {
        t = VP2Decoder::decode8BitTemperature(buffer, EXTRA_TEMPERATURES_BASE_OFFSET + i, valid);
        if (valid) {
            ss << "<entry><key>" << (100 + i) << "</key><value><sensorId>" << (100 + i) << "</sensorId><sensorType>THERMOMETER</sensorType>";
            ss << "<measurement xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"temperature\">";
            ss << t << "</measurement></value></entry>";
        }

    }
    ss << "</temperatureSensorEntries>";

/*
    ss << "<soilMoistureSensorEntries>";
    for (int i = 0; i < VP2Constants::APB_MAX_SOIL_MOISTURES; i++) {
        int soilMoisture = BitConverter::toInt8(buffer, SOIL_MOISTURES_BASE_OFFSET + i);
        if (soilMoisture != VP2Constants::APB_INVALID_SOIL_MOISTURE) {
            ss << "<entry><key>" << (600 + i) << "</key><value><sensorId>" << (600 + i) << "</sensorId><sensorType>SOIL_MOISTURE</sensorType>";
            ss << "<measurement xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"soilMoisture\">";
            ss << soilMoisture << "</measurement></value></entry>";
        }
    }
    ss << "</soilMoistureSensorEntries>";
*/
    ss << "</historicalRecord>";
    return ss.str();
}
}
