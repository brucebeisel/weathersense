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
#include "VP2Decoder.h"
#include "VantagePro2CRC.h"
#include "LoopPacket.h"

using namespace std;

namespace vp2 {

static const char *ALARM_STRINGS[][8] = {
    {
        "Falling Barometer Trend",
        "Rising Barometer Trend",
        "Low Indoor Temperature",
        "High Indoor Temperature",
        "Low Indoor Humidity",
        "High Indoor Humidity",
        "Time",
        nullptr
    },
    {
        "High Rain Rate",
        "15 Minute Rain",
        "24 Hour Rain",
        "Storm Total Rain",
        "Daily ET",
        nullptr,
        nullptr,
        nullptr
    },
    {
        "Low Outdoor Temperature",
        "High Outdoor Temperature",
        "Wind Speed",
        "10 Minute Average Wind Speed",
        "Low Dew Point",
        "High Dew Point",
        "High Heat Index",
        "Low Wind Chill"
    },
    {
        "High THSW",
        "High Solar Radiation",
        "High UV",
        "UV Dose",
        "UV Dose Manually Cleared",
        nullptr,
        nullptr,
        nullptr
    },
    {
        "Low Outdoor Humidity",
        "High Outdoor Humidity",
        nullptr,
        nullptr,
        nullptr,
        nullptr,
        nullptr,
        nullptr
    },
    {
        "Extra Temperature/Humidity 1 - Low Temperature",
        "Extra Temperature/Humidity 1 - High Temperature",
        "Extra Temperature/Humidity 1 - Low Humidity",
        "Extra Temperature/Humidity 1 - High Humidity",
        nullptr,
        nullptr,
        nullptr,
        nullptr
    },
    {
        "Extra Temperature/Humidity 2 - Low Temperature",
        "Extra Temperature/Humidity 2 - High Temperature",
        "Extra Temperature/Humidity 2 - Low Humidity",
        "Extra Temperature/Humidity 2 - High Humidity",
        nullptr,
        nullptr,
        nullptr,
        nullptr
    },
    {
        "Extra Temperature/Humidity 3 - Low Temperature",
        "Extra Temperature/Humidity 3 - High Temperature",
        "Extra Temperature/Humidity 3 - Low Humidity",
        "Extra Temperature/Humidity 3 - High Humidity",
        nullptr,
        nullptr,
        nullptr,
        nullptr
    },
    {
        "Extra Temperature/Humidity 4 - Low Temperature",
        "Extra Temperature/Humidity 4 - High Temperature",
        "Extra Temperature/Humidity 4 - Low Humidity",
        "Extra Temperature/Humidity 4 - High Humidity",
        nullptr,
        nullptr,
        nullptr,
        nullptr
    },
    {
        "Extra Temperature/Humidity 5 - Low Temperature",
        "Extra Temperature/Humidity 5 - High Temperature",
        "Extra Temperature/Humidity 5 - Low Humidity",
        "Extra Temperature/Humidity 5 - High Humidity",
        nullptr,
        nullptr,
        nullptr,
        nullptr
    },
    {
        "Extra Temperature/Humidity 6 - Low Temperature",
        "Extra Temperature/Humidity 6 - High Temperature",
        "Extra Temperature/Humidity 6 - Low Humidity",
        "Extra Temperature/Humidity 6 - High Humidity",
        nullptr,
        nullptr,
        nullptr,
        nullptr
    },
    {
        "Extra Temperature/Humidity 7 - Low Temperature",
        "Extra Temperature/Humidity 7 - High Temperature",
        "Extra Temperature/Humidity 7 - Low Humidity",
        "Extra Temperature/Humidity 7 - High Humidity",
        nullptr,
        nullptr,
        nullptr,
        nullptr
    },
    {
        "Soil/Leaf 1 - Low Leaf Wetness",
        "Soil/Leaf 1 - High Leaf Wetness",
        "Soil/Leaf 1 - Low Soil Moisture",
        "Soil/Leaf 1 - High Soil Moisture",
        "Soil/Leaf 1 - Low Leaf Temperature",
        "Soil/Leaf 1 - High Leaf Temperature",
        "Soil/Leaf 1 - Low Soil Temperature",
        "Soil/Leaf 1 - High Soil Temperature",
    },
    {
        "Soil/Leaf 2 - Low Leaf Wetness",
        "Soil/Leaf 2 - High Leaf Wetness",
        "Soil/Leaf 2 - Low Soil Moisture",
        "Soil/Leaf 2 - High Soil Moisture",
        "Soil/Leaf 2 - Low Leaf Temperature",
        "Soil/Leaf 2 - High Leaf Temperature",
        "Soil/Leaf 2 - Low Soil Temperature",
        "Soil/Leaf 2 - High Soil Temperature",
    },
    {
        "Soil/Leaf 3 - Low Leaf Wetness",
        "Soil/Leaf 3 - High Leaf Wetness",
        "Soil/Leaf 3 - Low Soil Moisture",
        "Soil/Leaf 3 - High Soil Moisture",
        "Soil/Leaf 3 - Low Leaf Temperature",
        "Soil/Leaf 3 - High Leaf Temperature",
        "Soil/Leaf 3 - Low Soil Temperature",
        "Soil/Leaf 3 - High Soil Temperature",
    },
    {
        "Soil/Leaf 4 - Low Leaf Wetness",
        "Soil/Leaf 4 - High Leaf Wetness",
        "Soil/Leaf 4 - Low Soil Moisture",
        "Soil/Leaf 4 - High Soil Moisture",
        "Soil/Leaf 4 - Low Leaf Temperature",
        "Soil/Leaf 4 - High Leaf Temperature",
        "Soil/Leaf 4 - Low Soil Temperature",
        "Soil/Leaf 4 - High Soil Temperature",
    }
};


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
const Measurement<Temperature> &
LoopPacket::getOutsideTemperature() const {
    return outsideTemperature;
}
        
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Temperature> &
LoopPacket::getInsideTemperature() const {
    return insideTemperature;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Humidity> &
LoopPacket::getOutsideHumidity() const {
    return outsideHumidity;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Humidity> &
LoopPacket::getInsideHumidity() const {
    return insideHumidity;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Speed> &
LoopPacket::getWindSpeed() const {
    return windSpeed;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Speed> &
LoopPacket::getAvgWindSpeed10Min() const {
    return avgWindSpeed10Min;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Heading> &
LoopPacket::getWindDirection() const {
    return windDirection;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Pressure> &
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
const Measurement<UvIndex> &
LoopPacket::getUvIndex() const {
    return uvIndex;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Evapotranspiration> &
LoopPacket::getYearET() const {
    return yearET;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Evapotranspiration> &
LoopPacket::getMonthET() const {
    return monthET;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Evapotranspiration> &
LoopPacket::getDayET() const {
    return dayET;
}


////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<SolarRadiation> &
LoopPacket::getSolarRadiation() const {
    return solarRadiation;
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
const Measurement<Temperature> &
LoopPacket::getExtraTemperature(int index) const {
    return extraTemperature[index];
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Humidity> &
LoopPacket::getExtraHumidity(int index) const {
    return extraHumidity[index];
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<LeafWetness> &
LoopPacket::getLeafWetness(int index) const {
    return leafWetness[index];
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<SoilMoisture> &
LoopPacket::getSoilMoisture(int index) const {
    return soilMoisture[index];
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Temperature> &
LoopPacket::getSoilTemperature(int index) const {
    return soilTemperature[index];
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Temperature> &
LoopPacket::getLeafTemperature(int index) const {
    return leafTemperature[index];
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
LoopPacket::isStormOngoing() const {
    //
    // The last couple of LOOP packets that have a valid storm start will
    // report a storm rain total of 0.0 inches. This may be an indicator that the storm has stopped,
    // but we are not using that at this point in time. By definition the storm rain has to be > 0, so
    // we will stop reporting an ongoing storm if the storm rain is 0.0
    //
    return stormStart != 0 && stormRain > 0.0;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
LoopPacket::decodeLoopPacket(byte buffer[]) {
    //
    // Perform a number of validation on the Loop packet before decoding all of the values
    //
    if (buffer[0] != 'L' || buffer[1] != 'O' || buffer[2] != 'O') {
        log.log(VP2Logger::VP2_ERROR) << "LOOP buffer does not begin with LOO: "
                                      << "[0] = " << buffer[0] << " [1] = " << buffer[1] << " [2] = " << buffer[2] << endl;
        return false;
    }

    if (!VantagePro2CRC::checkCRC(buffer, 97)) {
        log.log(VP2Logger::VP2_ERROR) << "LOOP packet failed CRC check" << endl;
        return false;
    }


    int packetType = BitConverter::toInt8(buffer, 4);
    if (packetType != LOOP_PACKET_TYPE) {
        log.log(VP2Logger::VP2_ERROR)<< "Invalid packet type for LOOP packet. Expected: "
                                     << LOOP_PACKET_TYPE << " Received: " << packetType << endl;
        return false;
    }

    if (buffer[95] != VP2Constants::LINE_FEED || buffer[96] != VP2Constants::CARRIAGE_RETURN) {
        log.log(VP2Logger::VP2_ERROR) << "<LF><CR> not found" << endl;
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

    nextRecord = BitConverter::toInt16(buffer,5);

    bool valid;
    VP2Decoder::decodeBarometricPressure(buffer, 7, barometricPressure);
    VP2Decoder::decode16BitTemperature(buffer, 9, insideTemperature);
    VP2Decoder::decodeHumidity(buffer, 11, insideHumidity);
    VP2Decoder::decode16BitTemperature(buffer, 12, outsideTemperature);

    windSpeed = VP2Decoder::decodeWindSpeed(buffer, 14);
    avgWindSpeed10Min = VP2Decoder::decodeWindSpeed(buffer, 15);
    windDirection = VP2Decoder::decodeWindDirection(buffer, 16);

    for (int i = 0; i < VP2Constants::MAX_EXTRA_TEMPERATURES; i++)
        VP2Decoder::decode8BitTemperature(buffer, 18 + i, extraTemperature[i]);

    for (int i = 0; i < VP2Constants::MAX_SOIL_TEMPERATURES; i++)
        VP2Decoder::decode8BitTemperature(buffer, 25 + i, soilTemperature[i]);

    for (int i = 0; i < VP2Constants::MAX_LEAF_TEMPERATURES; i++)
        VP2Decoder::decode8BitTemperature(buffer, 29 + i, leafTemperature[i]);

    VP2Decoder::decodeHumidity(buffer, 33, outsideHumidity);

    for (int i = 0; i < VP2Constants::MAX_EXTRA_HUMIDITIES; i++)
        VP2Decoder::decodeHumidity(buffer, 34 + i, extraHumidity[i]);

    rainRate = VP2Decoder::decodeRain(buffer, 41);

    VP2Decoder::decodeUvIndex(buffer, 43, uvIndex);
    VP2Decoder::decodeSolarRadiation(buffer, 44, solarRadiation);

    stormRain = VP2Decoder::decodeStormRain(buffer, 46);
    stormStart = VP2Decoder::decodeStormStartDate(buffer, 48);

    dayRain = VP2Decoder::decodeRain(buffer, 50);
    monthRain = VP2Decoder::decodeRain(buffer, 52);
    yearRain = VP2Decoder::decodeRain(buffer, 54);

    dayET = VP2Decoder::decodeDayET(buffer, 56);
    monthET = VP2Decoder::decodeMonthYearET(buffer, 58);
    yearET = VP2Decoder::decodeMonthYearET(buffer, 60);

    for (int i = 0; i < VP2Constants::MAX_SOIL_MOISTURES; i++)
        VP2Decoder::decodeSoilMoisture(buffer, 62 + i, soilMoisture[i]);

    for (int i = 0; i < VP2Constants::MAX_LEAF_WETNESSES; i++)
        VP2Decoder::decodeLeafWetness(buffer, 66 + i, leafWetness[i]);

    for (int i = 0; i < 16; i++) {
        int alarms = BitConverter::toInt8(buffer, 70 + i);
        for (int j = 0; j < 8; j++) {
            if (alarms & (1 << j)) {
                std::cout << lookupAlarm(i, j) << std::endl;
            }
        }
    }

    transmitterBatteryStatus = BitConverter::toInt8(buffer, 86);
    log.log(VP2Logger::VP2_DEBUG2) << "Transmitter Battery Status: " << transmitterBatteryStatus << endl;

    consoleBatteryVoltage = VP2Decoder::decodeConsoleBatteryVoltage(buffer, 87);
    log.log(VP2Logger::VP2_DEBUG2) << "Console Battery Voltage: " << consoleBatteryVoltage << endl;

    forecastIcon = static_cast<Forecast>(BitConverter::toInt8(buffer, 89));
    forecastRule = BitConverter::toInt8(buffer, 90);

    sunriseTime = VP2Decoder::decodeTime(buffer, 91);
    sunsetTime = VP2Decoder::decodeTime(buffer, 93);

    return true;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
string
LoopPacket::lookupAlarm(int byte, int bit) const {
    if (byte < 0 || byte >= 16 || bit < 0 || bit >= 8)
        return "Invalid Alarm Index";

    const char * alarmString = ALARM_STRINGS[byte][bit];

    if (alarmString == nullptr)
        return "Invalid Alarm Index";
    else
        return alarmString;
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
