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
#include "VP2Constants.h"

namespace vp2 {
/**
 * Class that decodes and stores the data from the LOOP packet.
 */
class LoopPacket {
public:
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
    unsigned int RAIN_BIT = 0x1;
    unsigned int MOSTLY_CLOUDY_BIT = 0x2;
    unsigned int PARTLY_CLOUDY_BIT = 0x6;
    unsigned int SUNNY_BIT = 0x8;
    unsigned int SNOW_BIT = 0x10;

    enum Forecast {
        SUNNY =                                 0x08,
        PARTLY_CLOUDY =                         0x06,
        MOSTLY_CLOUDY =                         0x02,
        MOSTLY_CLOUDY_WITH_RAIN =               0x03,
        MOSTLY_CLOUDY_WITH_SNOW =               0x12,
        MOSTLY_CLOUDY_WITH_RAIN_OR_SNOW =       0x13,
        PARTLY_CLOUDY_WITH_RAIN_LATER =         0x07,
        PARTLY_CLOUDY_WITH_SNOW_LATER =         0x16,
        PARTLY_CLOUDY_WITH_RAIN_OR_SNOW_LATER = 0x17
    };

    /**
     * Constructor.
     */
    LoopPacket();

    /**
     * Destructor.
     */
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

private:
    static const int LOOP_PACKET_TYPE = 0;

    VP2Logger          log;

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
    UvIndex            uvIndex;
    bool               uvIndexValid;
    SolarRadiation     solarRadiation;
    Evapotranspiration dayET;
    Evapotranspiration monthET;
    Evapotranspiration yearET;
    Forecast           forecastIcon;
    int                forecastRule;
    DateTime           sunriseTime;
    DateTime           sunsetTime;
    DateTime           stormStart;
    Speed              avgWindSpeed10Min;
    Speed              avgWindSpeed2Min;
    int                transmitterBatteryStatus;
    float              consoleBatteryVoltage;

    SoilMoisture       soilMoisture[VP2Constants::MAX_SOIL_MOISTURES];
    bool               soilMoistureValid[VP2Constants::MAX_SOIL_MOISTURES];

    LeafWetness        leafWetness[VP2Constants::MAX_LEAF_WETNESSES];
    bool               leafWetnessValid[VP2Constants::MAX_LEAF_WETNESSES];

    Temperature        leafTemperature[VP2Constants::MAX_LEAF_TEMPERATURES];
    bool               leafTemperatureValid[VP2Constants::MAX_LEAF_TEMPERATURES];

    Temperature        soilTemperature[VP2Constants::MAX_SOIL_TEMPERATURES];
    bool               soilTemperatureValid[VP2Constants::MAX_SOIL_TEMPERATURES];

    Temperature        temperatureExtra[VP2Constants::MAX_EXTRA_TEMPERATURES];
    bool               temperatureExtraValid[VP2Constants::MAX_EXTRA_TEMPERATURES];

    Humidity           humidityExtra[VP2Constants::MAX_EXTRA_HUMIDITIES];
    bool               humidityExtraValid[VP2Constants::MAX_EXTRA_HUMIDITIES];
};
}
#endif
