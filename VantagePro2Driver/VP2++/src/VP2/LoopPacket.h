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
#include "Measurement.h"
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

    int                              getNextRecord() const;
    const Measurement<Temperature> & getOutsideTemperature() const;
    const Measurement<Temperature> & getInsideTemperature() const;
    const Measurement<Temperature> & getExtraTemperature(int index) const;
    const Measurement<Humidity> &    getOutsideHumidity() const;
    const Measurement<Humidity> &    getInsideHumidity() const;
    const Measurement<Humidity> &    getExtraHumidity(int index) const;
    Rainfall                         getRainRate() const;
    Rainfall                         getDayRain() const;
    Rainfall                         getMonthRain() const;
    Rainfall                         getYearRain() const;
    Rainfall                         getStormRain() const;
    const Measurement<Speed> &       getWindSpeed() const;
    const Measurement<Speed> &       getAvgWindSpeed10Min() const;
    const Measurement<Heading> &     getWindDirection() const;
    const Measurement<Pressure> &    getBarometricPressure() const;
    const Measurement<UvIndex> &     getUvIndex() const;
    const Measurement<Temperature> &        getLeafTemperature(int index) const;
    const Measurement<SoilMoisture> &       getSoilMoisture(int index) const;
    const Measurement<LeafWetness> &        getLeafWetness(int index) const;
    const Measurement<Temperature> &        getSoilTemperature(int index) const;
    const Measurement<SolarRadiation> &     getSolarRadiation() const;
    const Measurement<Evapotranspiration> & getDayET() const;
    const Measurement<Evapotranspiration> & getMonthET() const;
    const Measurement<Evapotranspiration> & getYearET() const;
    bool               isTransmitterBatteryGood(int index) const;
    float              getConsoleBatteryVoltage() const;
    DateTime           getStormStart() const;
    bool               isStormOngoing() const;
    Forecast           getForecastIcon() const;
    std::string        getForecastIconString() const;
    int                getForecastRule() const;
    BaroTrend          getBaroTrend() const;
    std::string        getBaroTrendString() const;

private:
    std::string lookupAlarm(int byte, int bit) const;

    static const int LOOP_PACKET_TYPE = 0;

    VP2Logger          log;

    int                             nextRecord;
    Measurement<Temperature>        outsideTemperature;
    Measurement<Temperature>        insideTemperature;
    Measurement<Humidity>           outsideHumidity;
    Measurement<Humidity>           insideHumidity;
    Measurement<Speed>              windSpeed;
    Measurement<Heading>            windDirection;
    Measurement<Pressure>           barometricPressure;
    BaroTrend                       baroTrend;
    Rainfall                        rainRate;
    Rainfall                        stormRain;
    Rainfall                        dayRain;
    Rainfall                        monthRain;
    Rainfall                        yearRain;
    Measurement<UvIndex>            uvIndex;
    Measurement<SolarRadiation>     solarRadiation;
    Measurement<Evapotranspiration> dayET;
    Measurement<Evapotranspiration> monthET;
    Measurement<Evapotranspiration> yearET;
    Measurement<Speed>              avgWindSpeed10Min;
    Measurement<Speed>              avgWindSpeed2Min;
    Measurement<SoilMoisture>       soilMoisture[VP2Constants::MAX_SOIL_MOISTURES];
    Measurement<LeafWetness>        leafWetness[VP2Constants::MAX_LEAF_WETNESSES];
    Measurement<Temperature>        leafTemperature[VP2Constants::MAX_LEAF_TEMPERATURES];
    Measurement<Temperature>        soilTemperature[VP2Constants::MAX_SOIL_TEMPERATURES];
    Measurement<Temperature>        extraTemperature[VP2Constants::MAX_EXTRA_TEMPERATURES];
    Measurement<Humidity>           extraHumidity[VP2Constants::MAX_EXTRA_HUMIDITIES];

    Forecast           forecastIcon;
    int                forecastRule;
    DateTime           sunriseTime;
    DateTime           sunsetTime;
    DateTime           stormStart;
    int                transmitterBatteryStatus;
    float              consoleBatteryVoltage;

};
}
#endif
