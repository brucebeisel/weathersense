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
#ifndef HILOW_PACKET_H
#define HILOW_PACKET_H

#include <string>
#include "VP2Logger.h"
#include "Weather.h"
#include "VP2Constants.h"

namespace vp2 {
/**
 * Class that decodes and stores the data from the High/Low packet.
 */
class HiLowPacket {
public:
    HiLowPacket();
    ~HiLowPacket();

    /**
     * Parse the High/Lows packet buffer.
     * 
     * @param buffer The buffer to decodes
     * @return True if the Hi/Low packet was decodes successfully
     */
    bool decodeHiLowPacket(byte buffer[]);

    std::string formatXML() const;
    std::string formatJSON() const;

    //
    // Barometer High/Lows
    //
    Pressure getDayLowBarometer() const;
    DateTime getDayLowBarometerTime() const;
    Pressure getDayHighBarometer() const;
    DateTime getDayHighBarometerTime() const;
    Pressure getMonthLowBarometer() const;
    Pressure getMonthHighBarometer() const;
    Pressure getYearLowBarometer() const;
    Pressure getYearHighBarometer() const;

    //
    // Wind Highs
    //
    Speed    getDayHighWind() const;
    DateTime getDayHighWindTime() const;
    Speed    getMonthHighWind() const;
    Speed    getYearHighWind() const;

    //
    // Inside Temperature High/Lows
    //
    Temperature getDayLowInsideTemperature() const;
    DateTime    getDayLowInsideTemperatureTime() const;
    Temperature getDayHighInsideTemperature() const;
    DateTime    getDayHighInsideTemperatureTime() const;
    Temperature getMonthLowInsideTemperature() const;
    Temperature getMonthHighInsideTemperature() const;
    Temperature getYearLowInsideTemperature() const;
    Temperature getYearHighInsideTemperature() const;

    //
    // Outside Temperature High/Lows
    //
    Temperature getDayLowOutsideTemperature() const;
    DateTime    getDayLowOutsideTemperatureTime() const;
    Temperature getDayHighOutsideTemperature() const;
    DateTime    getDayHighOutsideTemperatureTime() const;
    Temperature getMonthLowOutsideTemperature() const;
    Temperature getMonthHighOutsideTemperature() const;
    Temperature getYearLowOutsideTemperature() const;
    Temperature getYearHighOutsideTemperature() const;

    //
    // Inside Humidity High/Lows
    //
    Humidity getDayLowInsideHumidity() const;
    DateTime getDayLowInsideHumidityTime() const;
    Humidity getDayHighInsideHumidity() const;
    DateTime getDayHighInsideHumidityTime() const;
    Humidity getMonthLowInsideHumidity() const;
    Humidity getMonthHighInsideHumidity() const;
    Humidity getYearLowInsideHumidity() const;
    Humidity getYearHighInsideHumidity() const;

    //
    // Outside Humidity High/Lows
    //
    Humidity getDayLowOutsideHumidity() const;
    DateTime getDayLowOutsideHumidityTime() const;
    Humidity getDayHighOutsideHumidity() const;
    DateTime getDayHighOutsideHumidityTime() const;
    Humidity getMonthLowOutsideHumidity() const;
    Humidity getMonthHighOutsideHumidity() const;
    Humidity getYearLowOutsideHumidity() const;
    Humidity getYearHighOutsideHumidity() const;

    //
    // Dew Point High/Lows
    //
    Temperature getDayLowDewPoint() const;
    DateTime    getDayLowDewPointTime() const;
    Temperature getDayHighDewPoint() const;
    DateTime    getDayHighDewPointTime() const;
    Temperature getMonthLowDewPoint() const;
    Temperature getMonthHighDewPoint() const;
    Temperature getYearLowDewPoint() const;
    Temperature getYearHighDewPoint() const;

    //
    // Wind Chill Lows
    //
    Temperature getDayLowWindChill() const;
    DateTime    getDayLowWindChillTime() const;
    Temperature getMonthLowWindChill() const;
    Temperature getYearLowWindChill() const;

    //
    // Heat Index Highs
    //
    Temperature getDayHighHeatIndex() const;
    DateTime    getDayHighHeatIndexTime() const;
    Temperature getMonthHighHeatIndex() const;
    Temperature getYearHighHeatIndex() const;

    //
    // THSW Highs
    //
    Temperature getDayHighTHSW() const;
    DateTime    getDayHighTHSWTime() const;
    Temperature getMonthHighTHSW() const;
    Temperature getYearHighTHSW() const;

    //
    // Solar Radiation Highs
    //
    SolarRadiation getDayHighSolarRadiation() const;
    DateTime       getDayHighSolarRadiationTime() const;
    SolarRadiation getMonthHighSolarRadiation() const;
    SolarRadiation getYearHighSolarRadiation() const;

    //
    // UV Index Highs
    //
    UvIndex   getDayHighUvIndex() const;
    DateTime  getDayHighUvIndexTime() const;
    UvIndex   getMonthHighUvIndex() const;
    UvIndex   getYearHighUvIndex() const;

    //
    // Rain Rate Highs
    //
    Rainfall  getDayHighRainRate() const;
    DateTime  getDayHighRainRateTime() const;
    Rainfall  getMonthHighRainRate() const;
    Rainfall  getYearHighRainRate() const;

    //
    // Extra temperatures and soil and leaf wetness goes here
    //

private:

    template<typename T>
    struct Values {
        Measurement<T> dayExtremeValue;
        DateTime       dayExtremeValueTime;
        Measurement<T> monthExtremeValue;
        Measurement<T> yearExtremeValue;
        bool isValid() const;
        std::string    formatXML(bool low) const;
        std::string    formatJSON(bool low) const;
    };

    template<typename T> using LowValues = Values<T>;
    template<typename T> using HighValues = Values<T>;

    template<typename T>
    struct HighLowValues {
        Values<T>   lows;
        Values<T>   highs;
        bool isValid() const;
        std::string formatXML() const;
        std::string formatJSON() const;
    };

    bool decodeHiLowTemperature(const byte buffer[], HighLowValues<Temperature> & values, int baseOffset);

    HighLowValues<Pressure>     barometer;
    HighValues<Speed>           wind;
    HighLowValues<Temperature>  insideTemperature;
    HighLowValues<Humidity>     insideHumidity;
    HighLowValues<Temperature>  outsideTemperature;
    HighLowValues<Humidity>     outsideHumidity;
    HighLowValues<Temperature>  dewPoint;
    HighValues<Temperature>     heatIndex;
    LowValues<Temperature>      windChill;
    HighValues<Temperature>     thsw;
    HighValues<SolarRadiation>  solarRadiation;
    HighValues<UvIndex>         uvIndex;
    HighValues<Rainfall>        rainRate;
    Rainfall                    highHourRainRate;
    HighLowValues<Temperature>  extraTemperature[VP2Constants::MAX_EXTRA_TEMPERATURES];
    HighLowValues<Temperature>  soilTemperature[VP2Constants::MAX_SOIL_TEMPERATURES];
    HighLowValues<Temperature>  leafTemperature[VP2Constants::MAX_LEAF_TEMPERATURES];
    HighLowValues<Humidity>     extraHumidity[VP2Constants::MAX_EXTRA_HUMIDITIES];
    HighLowValues<SoilMoisture> soilMoisture[VP2Constants::MAX_SOIL_MOISTURES];
    HighLowValues<LeafWetness>  leafWetness[VP2Constants::MAX_LEAF_WETNESSES];

    VP2Logger          log;
};
}
#endif
