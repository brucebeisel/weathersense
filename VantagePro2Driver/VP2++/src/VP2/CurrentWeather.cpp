/* 
 * Copyright (C) 2015 Bruce Beisel
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
    ss << "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
       << "<currentWeather>"
       << "<time>" << Weather::formatDateTime(time(0)) << "</time>"
       << "<indoorTemperature>" << loopPacket.getInsideTemperature() << "</indoorTemperature>"
       << "<indoorHumidity>" << loopPacket.getInsideHumidity() << "</indoorHumidity>"
       << "<outdoorTemperature>" << loopPacket.getOutsideTemperature() << "</outdoorTemperature>"
       << "<outdoorHumidity>" << loopPacket.getOutsideHumidity() << "</outdoorHumidity>"
       << "<dewPoint>" << loop2Packet.getDewPoint() << "</dewPoint>"
       << "<windChill>" << loop2Packet.getWindChill() << "</windChill>"
       << "<heatIndex>" << loop2Packet.getHeatIndex() << "</heatIndex>";

    if (loop2Packet.isThswValid())
       ss << "<thsw>" << loop2Packet.getThsw() << "</thsw>";

    ss << "<wind><speed>" << loopPacket.getWindSpeed() << "</speed><direction>" << loopPacket.getWindDirection() << "</direction></wind>"
       << "<windGust><speed>" << loop2Packet.getWindGust10Minute() << "</speed><direction>" << loop2Packet.getWindGustHeading10Minute() << "</direction></windGust>"
       << "<windSpeed10MinAvg>" << loopPacket.getAvgWindSpeed10Min() << "</windSpeed10MinAvg>" 
       << "<windSpeed2MinAvg>" << loop2Packet.getWindSpeed2MinuteAvg() << "</windSpeed2MinAvg>";

    vector<int> pastWindDirsList;
    pastWindDirs.pastHeadings(pastWindDirsList);
    for (unsigned int i = 0; i < pastWindDirsList.size(); i++)
       ss << "<windDir" << i + 2 << ">" << pastWindDirsList.at(i) << "</windDir" << i + 2 << ">";
   
    ss << "<baroPressure>" << loopPacket.getBarometricPressure() << "</baroPressure>"
       << "<baroTrend>" << loopPacket.getBaroTrendString() << "</baroTrend>"
       << "<rainRate>" << loopPacket.getRainRate() << "</rainRate>"
       << "<rainToday>" << loopPacket.getDayRain() << "</rainToday>"
       << "<rain15Minute>" << loop2Packet.getRain15Minute() << "</rain15Minute>"
       << "<rainHour>" << loop2Packet.getRainHour() << "</rainHour>"
       <<  "<rain24Hour>" << loop2Packet.getRain24Hour() << "</rain24Hour>"
       << "<rainMonth>" << loopPacket.getMonthRain() << "</rainMonth>" 
       << "<rainWeatherYear>" << loopPacket.getYearRain() << "</rainWeatherYear>";
    
    if (loopPacket.isSolarRadiationValid())
        ss << "<solarRadiation>" << loopPacket.getSolarRadiation() << "</solarRadiation>";

    if (loopPacket.getDayET() > 0.0)
        ss << "<dayET>" << loopPacket.getDayET() << "</dayET>";

    if (loopPacket.getMonthET() > 0.0)
        ss << "<monthET>" << loopPacket.getMonthET() << "</monthET>";

    if (loopPacket.getYearET() > 0.0)
        ss << "<yearET>" << loopPacket.getYearET() << "</yearET>";

    if (loopPacket.isUvIndexValid())
        ss << "<uvIndex><index>" << loopPacket.getUvIndex() << "</index></uvIndex>";

    if (loopPacket.isStormOngoing())
        ss << "<stormStart>" << Weather::formatDate(loopPacket.getStormStart()) << "</stormStart><stormRain>" << loopPacket.getStormRain() << "</stormRain>";


     ss << "<forecastRule>" << ForecastRule::forecastString(loopPacket.getForecastRule()) << "</forecastRule>"
        << "<forecast>" << loopPacket.getForecastIconString() << "</forecast>" 
        << "<temperatureSensorEntries>";

    for (int i = 0; i < LoopPacket::NUM_EXTRA_TEMPERATURES; i++) {
        if (loopPacket.isExtraTemperatureValid(i)) {
            ss << "<entry><key>" << 100 + i << "</key><value><sensorId>" << 100 + i << "</sensorId><sensorType>THERMOMETER</sensorType>"
               << "<measurement xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"temperature\">"
               << loopPacket.getExtraTemperature(i) << "</measurement></value></entry>";
        }
    }
    ss << "</temperatureSensorEntries>"
       << "<humiditySensorEntries>";

    for (int i = 0; i < LoopPacket::NUM_EXTRA_HUMIDITIES; i++) {
        if (loopPacket.isExtraHumidityValid(i)) {
            ss << "<entry><key>" << 200 + i << "</key><value><sensorId>" << 200 + i << "</sensorId><sensorType>HYGROMETER</sensorType>"
               << "<measurement xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"humidity\">"
               << loopPacket.getExtraHumidity(i) << "</measurement></value></entry>";
        }
    }

    ss << "</humiditySensorEntries>"
       << "<soilMoistureSensorEntries>";

    for (int i = 0; i < LoopPacket::NUM_SOIL_MOISTURES; i++) {
        if (loopPacket.isSoilMoistureValid(i)) {
            ss << "<entry><key>" << 600 + i << "</key><value><sensorId>" << 600 + i << "</sensorId><sensorType>SOIL_MOISTURE</sensorType>"
               << "<measurement xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"soilMoisture\">"
               << loopPacket.getSoilMoisture(i) << "</measurement></value></entry>";
        }
    }
    ss << "</soilMoistureSensorEntries>"
       << "<leafWetnessSensorEntries>";

    for (int i = 0; i < LoopPacket::NUM_LEAF_WETNESSES; i++) {
        if (loopPacket.isLeafWetnessValid(i)) {
            ss << "<entry><key>" << 500 + i << "</key><value><sensorId>" << 500 + i << "</sensorId><sensorType>LEAF_WETNESS</sensorType>"
               << "<measurement xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"leafWetness\">"
               << loopPacket.getLeafWetness(i) << "</measurement></value></entry>";
        }
    }

    ss << "</leafWetnessSensorEntries>"
       << "</currentWeather>";

    return ss.str();
}
}