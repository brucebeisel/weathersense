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
#include <vector>
#include <sstream>
#include "ForecastRule.h"
#include "JsonUtils.h"
#include "CurrentWeather.h"

using namespace std;

namespace vp2 {
CurrentWeather::CurrentWeather(const LoopPacket & loopPacket, const Loop2Packet & loop2Packet, WindDirectionSlices  & pastWindDirs) :
        loopPacket(loopPacket), loop2Packet(loop2Packet), pastWindDirs(pastWindDirs){
}

CurrentWeather::~CurrentWeather() {
}

int
CurrentWeather::getNextPacket() const {
    return loopPacket.getNextRecord();
}

std::string
CurrentWeather::formatMessage() const {
    ostringstream ss;
    ss << "current-weather '{"
       << "\"time\":\"" << Weather::formatDateTime(time(0)) << "\","
       << "\"indoorTemp\":" << loopPacket.getInsideTemperature() << ","
       << "\"indoorHumidity\":" << loopPacket.getInsideHumidity() << ","
       << "\"outdoorTemperature\":" << loopPacket.getOutsideTemperature() << ","
       << "\"outdoorHumidity\":" << loopPacket.getOutsideHumidity() << ","
       << "\"dewPoint\":" << loop2Packet.getDewPoint() << ","
       << "\"windChill\":" << loop2Packet.getWindChill() << ","
       << "\"heatIndex\":" << loop2Packet.getHeatIndex() << ","

    if (loop2Packet.isThswValid())
       ss << "\"thsw\":" << loop2Packet.getThsw() << ","

    ss << "\"wind\":{\"speed\":" << loopPacket.getWindSpeed() << ",\"direction\":" << loopPacket.getWindDirection() << "},"
       << "\"gust\":{\"speed\":" << loop2Packet.getWindGust10Minute() << ",\"direction\":" << loop2Packet.getWindGustHeading10Minute() << "},"
       << "\"windSpeed10MinAvg\":" << loopPacket.getAvgWindSpeed10Min() << "," 
       << "\"windSpeed2MinAvg\":" << loop2Packet.getWindSpeed2MinuteAvg() << ",";

    vector<int> pastWindDirsList;
    pastWindDirs.pastHeadings(pastWindDirsList);
    for (unsigned int i = 0; i < pastWindDirsList.size(); i++)
       ss << "\"windDir" << i + 2 << "\":" << pastWindDirsList.at(i) << ",";
   
    ss << "\"baroPressure\":" << loopPacket.getBarometricPressure() << ","
       << "\"baroTrend\":" << loopPacket.getBaroTrendString() << ","
       << "\"rainRate\":" << loopPacket.getRainRate() << ","
       << "\"rainToday\":" << loopPacket.getDayRain() << ","
       << "\"rain15Minute\":" << loop2Packet.getRain15Minute() << ","
       << "\"rainHour\":" << loop2Packet.getRainHour() << "\","
       << "\"<rain24Hour\":" << loop2Packet.getRain24Hour() << ","
       << "\"rainMonth\":" << loopPacket.getMonthRain() << "\"," 
       << "\"rainWeatherYear\":" << loopPacket.getYearRain() << ",";
    
    if (loopPacket.isSolarRadiationValid())
        ss << "\"solarRadiation\":" << loopPacket.getSolarRadiation() << ",";

    if (loopPacket.getDayET() > 0.0)
        ss << "\"dayET\":" << loopPacket.getDayET() << ",";

    if (loopPacket.getMonthET() > 0.0)
        ss << "\"monthET\":" << loopPacket.getMonthET() << ",";

    if (loopPacket.getYearET() > 0.0)
        ss << "\"yearET\":" << loopPacket.getYearET() << ",";

    if (loopPacket.isUvIndexValid())
        ss << "uvIndex><index>" << loopPacket.getUvIndex() << "</index></uvIndex>";

    if (loopPacket.isStormOngoing())
        ss << "<stormStart>" << Weather::formatDate(loopPacket.getStormStart()) << "</stormStart><stormRain>" << loopPacket.getStormRain() << "</stormRain>";


    ss << "\"forecastRule\":\"" << ForecastRule::forecastString(loopPacket.getForecastRule()) << "\","
       << "\"forecast\":\"" << loopPacket.getForecastIconString() << "\",";

    bool addComma = false;
    ss  << "\"temperatureSensorEntries\":{";
    for (int i = 0; i < LoopPacket::NUM_EXTRA_TEMPERATURES; i++) {
        if (loopPacket.isExtraTemperatureValid(i)) {
            JsonUtils::formatSensorMeasurement(ss, addComma, THERMOMETER_BASE_SENSOR_ID + i, "THERMOMETER", loopPacket.getExtraTemperature(i));
            addComma = true;
        }
    }

    for (int i = 0; i < LoopPacket::NUM_LEAF_TEMPERATURES; i++) {
        if (loopPacket.isLeafTemperatureValid(i)) {
            JsonUtils::formatSensorMeasurement(ss, addComma, LEAF_TEMPERATURE_BASE_SENSOR_ID + i, "LEAF_TEMPERATURE", loopPacket.getLeafTemperature(i));
            addComma = true;
        }
    }

    for (int i = 0; i < LoopPacket::NUM_SOIL_TEMPERATURES; i++) {
        if (loopPacket.isSoilTemperatureValid(i)) {
            JsonUtils::formatSensorMeasurement(ss, addComma, SOIL_TEMPERATURE_BASE_SENSOR_ID + i, "SOIL_TEMPERATURE", loopPacket.getSoilTemperature(i));
            addComma = true;
        }
    }

    ss << "}," << "\"humiditySensorEntries\":{";
    addComma = false;
    for (int i = 0; i < LoopPacket::NUM_EXTRA_HUMIDITIES; i++) {
        if (loopPacket.isExtraHumidityValid(i)) {
            JsonUtils::formatSensorMeasurement(ss, addComma, HYGROMETER_BASE_SENSOR_ID + i, "HYGROMETER", loopPacket.getExtraHumidity(i));
            addComma = true;
        }
    }

    ss << "},\"soilMoistureSensorEntries\":{";
    addComma = false;
    for (int i = 0; i < LoopPacket::NUM_SOIL_MOISTURES; i++) {
        if (loopPacket.isSoilMoistureValid(i)) {
            JsonUtils::formatSensorMeasurement(ss, addComma, SOIL_MOISTURE_BASE_SENSOR_ID + i, "SOIL_MOISTURE", loopPacket.getSoilMoisture(i));
            addComma = true;
        }
    }

    ss << "},\"leafWetnessSensorEntries\":{";
    addComma = false;
    for (int i = 0; i < LoopPacket::NUM_LEAF_WETNESSES; i++) {
        if (loopPacket.isLeafWetnessValid(i)) {
            JsonUtils::formatSensorMeasurement(ss, addComma, LEAF_WETNESS_BASE_SENSOR_ID + i, "LEAF_WETNESS", loopPacket.getLeafWetness(i));
            addComma = true;
        }
    }

    ss << "}" << "}";

    return ss.str();
}
}