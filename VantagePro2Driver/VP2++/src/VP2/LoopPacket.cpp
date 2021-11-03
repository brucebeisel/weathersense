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
#include <iostream>
#include "BitConverter.h"
#include "UnitConverter.h"
#include "VP2Constants.h"
#include "VantagePro2CRC.h"
#include "LoopPacket.h"

using namespace std;

namespace vp2 {
const Temperature LoopPacket::TEMPERATURE_SCALE = 10.0f;
Rainfall LoopPacket::rainfallIncrement = 0.0;

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
LoopPacket::LoopPacket(void) : log(VP2Logger::getLogger("LoopPacket")) {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
LoopPacket::~LoopPacket(void) {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
int
LoopPacket::getNextRecord() const {
    return nextRecord;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
LoopPacket::BaroTrend
LoopPacket::getBaroTrend() const {
    return baroTrend;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Temperature
LoopPacket::getOutsideTemperature() const {
    return outsideTemperature;
}
        
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Temperature
LoopPacket::getInsideTemperature() const {
    return insideTemperature;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Humidity
LoopPacket::getOutsideHumidity() const {
    return outsideHumidity;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Humidity
LoopPacket::getInsideHumidity() const {
    return insideHumidity;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Speed
LoopPacket::getWindSpeed() const {
    return windSpeed;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Speed
LoopPacket::getAvgWindSpeed10Min() const {
    return avgWindSpeed10Min;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Heading
LoopPacket::getWindDirection() const {
    return windDirection;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Pressure
LoopPacket::getBarometricPressure() const {
    return barometricPressure;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Rainfall
LoopPacket::getRainRate() const {
    return rainRate;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Rainfall
LoopPacket::getStormRain() const {
    return stormRain;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
DateTime
LoopPacket::getStormStart() const {
    return stormStart;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Rainfall
LoopPacket::getDayRain() const {
    return dayRain;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Rainfall
LoopPacket::getMonthRain() const {
    return monthRain;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Rainfall
LoopPacket::getYearRain() const {
    return yearRain;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
UvIndex
LoopPacket::getUvIndex() const {
    return static_cast<UvIndex>(uvIndex) / 10.0F;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Evapotranspiration
LoopPacket::getYearET() const {
    return yearET;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Evapotranspiration
LoopPacket::getMonthET() const {
    return monthET;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Evapotranspiration
LoopPacket::getDayET() const {
    return dayET;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
LoopPacket::isUvIndexValid() const {
    return uvIndex != INVALID_UV_INDEX;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
SolarRadiation
LoopPacket::getSolarRadiation() const {
    return solarRadiation;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
LoopPacket::isSolarRadiationValid() const {
    return solarRadiation != INVALID_SOLAR_RADIATION;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
float
LoopPacket::getConsoleBatteryVoltage() const {
    return consoleBatteryVoltage;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
LoopPacket::Forecast
LoopPacket::getForecastIcon() const {
    return forecastIcon;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
int
LoopPacket::getForecastRule() const {
    return forecastRule;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
LoopPacket::isTransmitterBatteryGood(int index) const {
    return (transmitterBatteryStatus & (1 << (index - 1))) == 0;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
LoopPacket::isExtraTemperatureValid(int index) const {
    return temperatureExtraValid[index];
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Temperature
LoopPacket::getExtraTemperature(int index) const {
    return temperatureExtra[index];
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
LoopPacket::isExtraHumidityValid(int index) const {
    return humidityExtra[index] != INVALID_EXTRA_HUMIDITY;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Humidity
LoopPacket::getExtraHumidity(int index) const {
    return humidityExtra[index];
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
LeafWetness
LoopPacket::getLeafWetness(int index) const {
    return leafWetness[index];
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
LoopPacket::isLeafWetnessValid(int index) const {
    return leafWetness[index] != INVALID_LEAF_WETNESS;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
int
LoopPacket::getSoilMoisture(int index) const {
    return soilMoisture[index];
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
LoopPacket::isSoilMoistureValid(int index) const {
    return soilMoisture[index] != INVALID_SOIL_MOISTURE;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Temperature
LoopPacket::getSoilTemperature(int index) const {
    return soilTemperature[index];
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
LoopPacket::isSoilTemperatureValid(int index) const {
    return soilTemperatureValid[index];
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Temperature
LoopPacket::getLeafTemperature(int index) const {
    return leafTemperature[index];
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
LoopPacket::isLeafTemperatureValid(int index) const {
    return leafTemperatureValid[index];
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
//
// The last couple of LOOP packets that have a valid storm start will
// report a storm rain total of 0.0 inches. This may be an indicator that the storm has stopped,
// but we are not using that at this point in time. By definition the storm rain has to be > 0, so
// we will stop reporting an ongoing storm if the storm rain is 0.0
//
bool
LoopPacket::isStormOngoing() const {
    return stormStart != 0 && stormRain > 0.0;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
LoopPacket::parseLoopPacket(byte buffer[]) {
    if (rainfallIncrement == 0) {
        log.log(VP2Logger::VP2_ERROR) << "Rain increment not set, cannot parse LOOP packet" << endl;
        return false;
    }

    if (buffer[0] != 'L' || buffer[1] != 'O' || buffer[2] != 'O') {
        log.log(VP2Logger::VP2_ERROR) << "LOOP buffer does not begin with LOO: [0] = " << buffer[0] << " [1] = " << buffer[1] << " [2] = " << buffer[2] << endl;
        return false;
    }

    if (buffer[3] != 'P') {
        switch (BitConverter::toInt8(buffer, 3)) {
	    case 255:
		baroTrend = UNKNOWN;
		break;
            case 196:
                baroTrend = FALLING_RAPIDLY;
                break;
            case 236:
                baroTrend = FALLING_SLOWLY;
                break;
            case 0:
                baroTrend = STEADY;
                break;
            case 20:
                baroTrend = RISING_SLOWLY;
                break;
            case 60:
                baroTrend = RISING_RAPIDLY;
                break;
            default:
                log.log(VP2Logger::VP2_ERROR) << "Invalid barometer trend 0x" << hex << (int)buffer[3] << dec << endl;
                return false;
        }
    }

    int packetType = BitConverter::toInt8(buffer, 4);

    if (packetType != 0) {
        log.log(VP2Logger::VP2_ERROR)<< "Invalid packet type for LOOP packet: " << packetType << endl;
        return false;
    }

    nextRecord = BitConverter::toInt16(buffer,5);
    barometricPressure = UnitConverter::toMillibars((Pressure)BitConverter::toInt16(buffer, 7) / 1000.0F);
    insideTemperature = UnitConverter::toCelsius((Pressure)BitConverter::toInt16(buffer, 9) / TEMPERATURE_SCALE);
    insideHumidity = BitConverter::toInt8(buffer, 11);
    outsideTemperature = UnitConverter::toCelsius(BitConverter::toInt16(buffer, 12) / TEMPERATURE_SCALE);
    windSpeed = UnitConverter::toMetersPerSecond((Speed)BitConverter::toInt8(buffer, 14));
    avgWindSpeed10Min = UnitConverter::toMetersPerSecond((Speed)BitConverter::toInt8(buffer, 15));
    windDirection = static_cast<Heading>(BitConverter::toInt16(buffer, 16));
    //
    // Zero degrees from the weather station means no wind, translate 360 to 0 for north
    //
    if (windDirection == 360)
        windDirection = 0;

    for (int i = 0; i < NUM_EXTRA_TEMPERATURES; i++) {
        int temperature = BitConverter::toInt8(buffer, 18 + i);
        temperatureExtraValid[i] = temperature != INVALID_EXTRA_TEMPERATURE;
        temperatureExtra[i] = UnitConverter::toCelsius(temperature - TEMPERATURE_OFFSET);
    }

    for (int i = 0; i < NUM_SOIL_TEMPERATURES; i++) {
        int temperature = BitConverter::toInt8(buffer, 25 + i);
        soilTemperatureValid[i] = temperature != INVALID_EXTRA_TEMPERATURE;
        soilTemperature[i] = UnitConverter::toCelsius((float)temperature - TEMPERATURE_OFFSET);
    }

    for (int i = 0; i < NUM_LEAF_TEMPERATURES; i++) {
        //int temperature = BitConverter::toInt8(buffer, 29 + i);
        //leafTemperatureValid[i] = temperature != INVALID_EXTRA_TEMPERATURE;
        //leafTemperature[i] = UnitConverter::toCelsius(temperature - TEMPERATURE_OFFSET);
        leafTemperature[i] = VP2Utils::parse8BitTemperature(buffer, 29 + i, leafTemperatureValid[i]);
    }

    outsideHumidity = BitConverter::toInt8(buffer, 33);

    for (int i = 0; i < NUM_EXTRA_HUMIDITIES; i++) {
        humidityExtra[i] = BitConverter::toInt8(buffer, 34 + i);
    }

    rainRate = UnitConverter::toMillimeter((float)BitConverter::toInt16(buffer, 41) / 100.0F);

    uvIndex = BitConverter::toInt8(buffer, 43);
    solarRadiation = BitConverter::toInt16(buffer, 44);

    stormRain = UnitConverter::toMillimeter((float)BitConverter::toInt16(buffer, 46) / 100.0F);

    int stormStart = BitConverter::toInt16(buffer, 48);

    if (stormStart != -1)
        this->stormStart = extractStormStartDate(stormStart);

    dayRain = UnitConverter::toMillimeter((float)BitConverter::toInt16(buffer, 50) * rainfallIncrement);
    monthRain = UnitConverter::toMillimeter((float)BitConverter::toInt16(buffer, 52) * rainfallIncrement);
    yearRain = UnitConverter::toMillimeter((float)BitConverter::toInt16(buffer, 54) * rainfallIncrement);

    int idayET = BitConverter::toInt16(buffer, 56);
    int imonthET = BitConverter::toInt16(buffer, 58);
    int iyearET = BitConverter::toInt16(buffer, 60);
    log.log(VP2Logger::VP2_DEBUG3) << "ET: Day=" << idayET << ", Month=" << imonthET << ", Year=" << iyearET << endl;
    dayET = UnitConverter::toMillimeter((float)BitConverter::toInt16(buffer, 56) / 1000.0F);
    monthET = UnitConverter::toMillimeter((float)BitConverter::toInt16(buffer, 58) / 100.0F);
    yearET = UnitConverter::toMillimeter((float)BitConverter::toInt16(buffer, 60) / 100.0F);

    for (int i = 0; i < NUM_SOIL_MOISTURES; i++)
        soilMoisture[i] = BitConverter::toInt8(buffer, 62 + i);

    for (int i = 0; i < NUM_LEAF_WETNESSES; i++)
        leafWetness[i] = BitConverter::toInt8(buffer, 66 + i);

    int indoorAlarms = (int)BitConverter::toInt8(buffer, 70);
    int rainAlarms = (int)BitConverter::toInt8(buffer, 71);
    int outsideAlarms1 = (int)BitConverter::toInt8(buffer, 72);
    int outsideAlarms2 = (int)BitConverter::toInt8(buffer, 73);
    int extraTemperatureHumidityAlarms[8];
    int alarmIndex = 74;
    for (int i = 0; i <= 8; i++)
	extraTemperatureHumidityAlarms[i] = (int)BitConverter::toInt8(buffer, alarmIndex + i);

    int soilLeafAlarms[4];
    alarmIndex = 82;
    for (int i = 0; i <= 4; i++)
	soilLeafAlarms[i] = (int)BitConverter::toInt8(buffer, alarmIndex + i);

    transmitterBatteryStatus = (int)BitConverter::toInt8(buffer, 86);
    log.log(VP2Logger::VP2_DEBUG2) << "Transmitter Battery Status: " << transmitterBatteryStatus << endl;
    consoleBatteryVoltage = (((int)BitConverter::toInt16(buffer, 87) * 300) / 512) / 100.0F;
    forecastIcon = (Forecast)BitConverter::toInt8(buffer, 89);
    forecastRule = BitConverter::toInt8(buffer, 90);

    int sunrise = BitConverter::toInt16(buffer, 91);
    int sunset = BitConverter::toInt16(buffer, 93);
    int sunriseHour = sunrise / 100;
    int sunriseMinute = sunrise % 100;
    int sunsetHour = sunset / 100;
    int sunsetMinute = sunset % 100;

    time_t now = time(0);
    struct tm tm;
    Weather::localtime(now, tm);
    tm.tm_hour = sunriseHour;
    tm.tm_min = sunriseMinute;
    sunRiseTime = mktime(&tm);
    tm.tm_hour = sunsetHour;
    tm.tm_min = sunsetMinute;
    sunSetTime = mktime(&tm);

    if (buffer[95] != VP2Constants::LINE_FEED || buffer[96] != VP2Constants::CARRIAGE_RETURN) {
        log.log(VP2Logger::VP2_ERROR) << "<LF><CR> not found" << endl;
        return false;
    }

    bool rv =  VantagePro2CRC::checkCRC(buffer, 97);

    if (!rv)
        log.log(VP2Logger::VP2_ERROR) << "LOOP packet failed CRC check" << endl;

    return rv;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
DateTime
LoopPacket::extractStormStartDate(int time) {
    int year = (time & 0x3F) + STORM_START_YEAR_OFFSET;
    int day = (time >> 7) & 0x1F;
    int month = (time >> 12) & 0xF;

    time_t now = ::time(0);
    struct tm tm;
    Weather::localtime(now, tm);
    tm.tm_year = year - 1900;
    tm.tm_mon = month - 1;
    tm.tm_mday = day;
    tm.tm_hour = 0;
    tm.tm_min = 0;
    tm.tm_sec = 0;
    return mktime(&tm);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
LoopPacket::setRainfallIncrement(Rainfall increment) {
    rainfallIncrement = increment;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
string
LoopPacket::getBaroTrendString() const {
    switch(baroTrend) {
        case FALLING_RAPIDLY:
            return "FALLING_RAPIDLY";

        case FALLING_SLOWLY:
            return "FALLING_SLOWLY";

        case STEADY:
            return "STEADY";

        case RISING_SLOWLY:
            return "RISING_SLOWLY";

        case RISING_RAPIDLY:
            return "RISING_RAPIDLY";

        default:
            return "STEADY";
    }
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
string
LoopPacket::getForecastIconString() const {
    switch (forecastIcon) {
        case SUNNY:
            return "SUNNY";

        case PARTLY_CLOUDY:
            return "PARTLY_CLOUDY";

        case MOSTLY_CLOUDY:
            return "MOSTLY_CLOUDY";

        case MOSTLY_CLOUDY_WITH_RAIN:
            return "MOSTLY_CLOUDY_WITH_RAIN";

        case MOSTLY_CLOUDY_WITH_SNOW:
            return "MOSTLY_CLOUDY_WITH_SNOW";

        case MOSTLY_CLOUDY_WITH_RAIN_OR_SNOW:
            return "MOSTLY_CLOUDY_WITH_RAIN_OR_SNOW";

        case PARTLY_CLOUDY_WITH_RAIN_LATER:
            return "PARTLY_CLOUDY_WITH_RAIN_LATER";

        case PARTLY_CLOUDY_WITH_SNOW_LATER:
            return "PARTLY_CLOUDY_WITH_SNOW_LATER";

        case PARTLY_CLOUDY_WITH_RAIN_OR_SNOW_LATER:
            return "PARTLY_CLOUDY_WITH_SNOW_LATER";

        default:
            return "SUNNY";
    }
}

}
