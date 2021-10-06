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
#ifndef HILOW_PACKET_H
#define HILOW_PACKET_H

#include <string>
#include "VP2Logger.h"
#include "Weather.h"

namespace vp2 {
/**
 * Class that parses and stores the data from the LOOP packet.
 */
class LoopPacket {
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
     * @return True if the LOOP packet was parsed successfully
     */
    bool parseHiLowPacket(byte buffer[]);

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
    static DateTime extractDate(int time);

    Pressure       dayLowBarometer;
    DateTime       dayLowBarometerTime;
    Pressure       dayHighBarometer;
    DateTime       dayHighBarometerTime;
    Pressure       monthHighBarometer;
    Pressure       yearHighBarometer;

    Temperature        outsideTemperature;
    Temperature        insideTemperature;
    Humidity           outsideHumidity;
    Humidity           insideHumidity;
    Speed              windSpeed;
    Heading            windDirection;
    Pressure           barometricPressure;
    BaroTrend          baroTrend;
    Rainfall           rainRate;
    Rainfall           stormRain;
    Rainfall           dayRain;
    Rainfall           monthRain;
    Rainfall           yearRain;
    int                uvIndex;
    SolarRadiation     solarRadiation;
    Evapotranspiration dayET;
    Evapotranspiration monthET;
    Evapotranspiration yearET;
    Forecast           forecastIcon;
    int                forecastRule;
    DateTime           sunRiseTime;
    DateTime           sunSetTime;
    DateTime           stormStart;
    Speed              avgWindSpeed10Min;
    Speed              avgWindSpeed2Min;

    SoilMoisture       soilMoisture[NUM_SOIL_MOISTURES];
    LeafWetness        leafWetness[NUM_LEAF_WETNESSES];
    Temperature        leafTemperature[NUM_LEAF_TEMPERATURES];
    bool               leafTemperatureValid[NUM_LEAF_TEMPERATURES];
    Temperature        soilTemperature[NUM_SOIL_TEMPERATURES];
    bool               soilTemperatureValid[NUM_SOIL_TEMPERATURES];
    Temperature        temperatureExtra[NUM_EXTRA_TEMPERATURES];
    bool               temperatureExtraValid[NUM_EXTRA_TEMPERATURES];
    Humidity           humidityExtra[NUM_EXTRA_HUMIDITIES];
    int                transmitterBatteryStatus;
    float              consoleBatteryVoltage;
    VP2Logger          log;

    static Rainfall    rainfallIncrement;
};
}
#endif
