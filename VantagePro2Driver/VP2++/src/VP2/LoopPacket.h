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
#ifndef LOOP_PACKET_H
#define LOOP_PACKET_H

#include <string>
#include "VP2Logger.h"
#include "Weather.h"

namespace vp2 {
/**
 * Class that decodes and stores the data from the LOOP packet.
 */
class LoopPacket {
public:
    // TODO Use the constants from VP2 constants
    static const int NUM_EXTRA_TEMPERATURES = 7;
    static const int NUM_EXTRA_HUMIDITIES =   7;
    static const int NUM_SOIL_TEMPERATURES =  4;
    static const int NUM_LEAF_TEMPERATURES =  3;
    static const int NUM_SOIL_MOISTURES =     4;
    static const int NUM_LEAF_WETNESSES =     3;

    /**
     * The trend of the barometer as reported in the LOOP packet
     */
    enum BaroTrend {
        STEADY =            0,
        RISING_SLOWLY =    20,
        RISING_RAPIDLY =   60,
        FALLING_RAPIDLY = 196,
        FALLING_SLOWLY =  236,
        UNKNOWN =         255
    };

    /**
     * The forecast reported by the LOOP packet.
     */
    enum Forecast {
        SUNNY =                                  8,
        PARTLY_CLOUDY =                          6,
        MOSTLY_CLOUDY =                          2,
        MOSTLY_CLOUDY_WITH_RAIN =                3,
        MOSTLY_CLOUDY_WITH_SNOW =               18,
        MOSTLY_CLOUDY_WITH_RAIN_OR_SNOW =       19,
        PARTLY_CLOUDY_WITH_RAIN_LATER =          7,
        PARTLY_CLOUDY_WITH_SNOW_LATER =         22,
        PARTLY_CLOUDY_WITH_RAIN_OR_SNOW_LATER = 23
    };

    LoopPacket();
    ~LoopPacket();

    /**
     * Parse the LOOP packet buffer.
     * 
     * @param buffer The buffer to decode
     * @return True if the LOOP packet was decoded successfully
     */
    bool               decodeLoopPacket(byte buffer[]);

    int                getNextRecord() const;
    Temperature        getOutsideTemperature() const;
    Temperature        getInsideTemperature() const;
    Temperature        getExtraTemperature(int index) const;
    bool               isExtraTemperatureValid(int index) const;
    Humidity           getOutsideHumidity() const;
    Humidity           getInsideHumidity() const;
    bool               isExtraHumidityValid(int index) const;
    Humidity           getExtraHumidity(int index) const;
    Rainfall           getRainRate() const;
    Rainfall           getDayRain() const;
    Rainfall           getMonthRain() const;
    Rainfall           getYearRain() const;
    Rainfall           getStormRain() const;
    DateTime           getStormStart() const;
    bool               isStormOngoing() const;
    Speed              getWindSpeed() const;
    Speed              getAvgWindSpeed10Min() const;
    Heading            getWindDirection() const;
    Pressure           getBarometricPressure() const;
    UvIndex            getUvIndex() const;
    bool               isUvIndexValid() const;
    Forecast           getForecastIcon() const;
    std::string        getForecastIconString() const;
    int                getForecastRule() const;
    BaroTrend          getBaroTrend() const;
    std::string        getBaroTrendString() const;
    Temperature        getLeafTemperature(int index) const;
    bool               isLeafTemperatureValid(int index) const;
    bool               isSoilTemperatureValid(int index) const;
    SoilMoisture       getSoilMoisture(int index) const;
    bool               isSoilMoistureValid(int index) const;
    LeafWetness        getLeafWetness(int index) const;
    bool               isLeafWetnessValid(int index) const;
    Temperature        getSoilTemperature(int index) const;
    bool               isTransmitterBatteryGood(int index) const;
    float              getConsoleBatteryVoltage() const;
    SolarRadiation     getSolarRadiation() const;
    bool               isSolarRadiationValid() const;
    Evapotranspiration getDayET() const;
    Evapotranspiration getMonthET() const;
    Evapotranspiration getYearET() const;

    static void        setRainfallIncrement(Rainfall increment);

private:
    VP2Logger          log;

    static DateTime extractStormStartDate(int time);

    static const int                TEMPERATURE_OFFSET = 90;
    static const Temperature        TEMPERATURE_SCALE;
    static const int                INVALID_EXTRA_TEMPERATURE = 255;
    static const int                INVALID_EXTRA_HUMIDITY = 255;
    static const int                INVALID_UV_INDEX = 255;
    static const int                INVALID_LEAF_WETNESS = 255;
    static const int                INVALID_SOIL_MOISTURE = 255;
    static const int                INVALID_SOLAR_RADIATION = 32767;
    static const int                STORM_START_YEAR_OFFSET = 2000;
    static const Rainfall           STORM_RAINFALL_SCALE;
    static const Pressure           BAROMETER_SCALE;
    static const Evapotranspiration DAY_ET_SCALE;
    static const Evapotranspiration MONTH_ET_SCALE;
    static const Evapotranspiration YEAR_ET_SCALE;

    int                nextRecord;
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
    int                transmitterBatteryStatus;
    float              consoleBatteryVoltage;

    SoilMoisture       soilMoisture[NUM_SOIL_MOISTURES];
    LeafWetness        leafWetness[NUM_LEAF_WETNESSES];
    Temperature        leafTemperature[NUM_LEAF_TEMPERATURES];
    bool               leafTemperatureValid[NUM_LEAF_TEMPERATURES];
    Temperature        soilTemperature[NUM_SOIL_TEMPERATURES];
    bool               soilTemperatureValid[NUM_SOIL_TEMPERATURES];
    Temperature        temperatureExtra[NUM_EXTRA_TEMPERATURES];
    bool               temperatureExtraValid[NUM_EXTRA_TEMPERATURES];
    Humidity           humidityExtra[NUM_EXTRA_HUMIDITIES];

    static Rainfall    rainfallIncrement;
};
}
#endif
