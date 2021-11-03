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

namespace vp2 {
/**
 * Class that parses and stores the data from the High/Low packet.
 */
class HiLowPacket {
public:
    static const int NUM_EXTRA_TEMPERATURES = 7;
    static const int NUM_EXTRA_HUMIDITIES = 7;
    static const int NUM_SOIL_TEMPERATURES = 4;
    static const int NUM_LEAF_TEMPERATURES = 3;
    static const int NUM_SOIL_MOISTURES = 4;
    static const int NUM_LEAF_WETNESSES = 3;

    HiLowPacket();
    ~HiLowPacket();

    /**
     * Parse the High/Lows packet buffer.
     * 
     * @param buffer The buffer to parse
     * @return True if the Hi/Low packet was parsed successfully
     */
    bool parseHiLowPacket(byte buffer[]);

    std::string formatMessage() const;

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
    struct HighValues {
        T        dayHighValue;
        DateTime dayHighValueTime;
        T        monthHighValue;
        T        yearHighValue;
    };

    template<typename T>
    struct LowValues {
        T        dayLowValue;
        DateTime dayLowValueTime;
        T        monthLowValue;
        T        yearLowValue;
    };

    template<typename T>
    struct HiLowValues {
        LowValues<T>  lows;
        HighValues<T> highs;
    };


    HiLowValues<Pressure>     barometer;
    LowValues<Wind>           wind;
    HiLowValues<Temperature>  insideTemperature;
    HiLowValues<Humidity>     insideHumidity;
    HiLowValues<Temperature>  outsideTemperature;
    HiLowValues<Humidity>     outsideHumidity;
    HiLowValues<Temperature>  dewPoint;
    HiValues<Temperature>     heatIndex;
    LowValues<Temperature>    windChill;
    HiValues<Temperature>     thsw;
    HiValues<SolarRadiation>  solarRadiation;
    HiValues<UvIndex>         uvIndex;
    HiValues<Rainfall>        rainRate;
    HiLowValues<Temperature>  extraTemperatures[7];
    HiLowValues<Temperature>  soilTemperatures[4];
    HiLowValues<Temperature>  leafTemperatures[4];
    HiLowValues<Humidity>     extraHumidity[NUM_EXTRA_HUMIDITIES];
    HiLowValues<SoilMoisture> soilMoisture[4];
    HiLowValues<LeafWetness>  leafWetness[4];

    VP2Logger          log;

    static Rainfall    rainfallIncrement;
};
}
#endif
