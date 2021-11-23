#include <iostream>
#include "BitConverter.h"
#include "UnitConverter.h"
#include "VP2Constants.h"
#include "VP2Decoder.h"

namespace vp2 {

Rainfall VP2Decoder::rainCollectorSize = static_cast<Rainfall>(0.0);

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Temperature
VP2Decoder::decode16BitTemperature(const byte buffer[], int offset, bool & valid) {
    Temperature t = 0.0;
    valid = false;

    int value16 = BitConverter::toInt16(buffer, offset);

    if (value16 != VP2Constants::INVALID_16BIT_TEMPERATURE) {
        t = UnitConverter::toCelsius(static_cast<Temperature>(value16) / VP2Constants::TEMPERATURE_16BIT_SCALE);
        valid = true;
    }

    return t;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Temperature
VP2Decoder::decodeNonScaled16BitTemperature(const byte buffer[], int offset, bool & valid) {
    Temperature t = 0.0;
    valid = false;

    int value16 = BitConverter::toInt16(buffer, offset);

    if (value16 != VP2Constants::INVALID_16BIT_TEMPERATURE) {
        t = UnitConverter::toCelsius(static_cast<Temperature>(value16));
        valid = true;
    }

    return t;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Temperature
VP2Decoder::decode8BitTemperature(const byte buffer[], int offset, bool &valid) {
    Temperature t = 0.0;
    valid = false;

    int value8 = BitConverter::toInt8(buffer, offset);

    if (value8 != VP2Constants::INVALID_8BIT_TEMPERATURE) {
        t = UnitConverter::toCelsius(static_cast<Temperature>(value8 - VP2Constants::TEMPERATURE_8BIT_OFFSET));
        valid = true;
    }

    return t;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Pressure
VP2Decoder::decodeBarometricPressure(const byte buffer[], int offset, bool &valid) {
    Pressure barometricPressure = 0.0;
    valid = true;

    int value16 = BitConverter::toInt16(buffer, offset);

    barometricPressure = UnitConverter::toMillibars(static_cast<Pressure>(value16) / VP2Constants::BAROMETER_SCALE);

    return barometricPressure;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Humidity
VP2Decoder::decodeHumidity(const byte buffer[], int offset, bool & valid) {
    Humidity humidity = 0.0;
    valid = false;

    int value8 = BitConverter::toInt8(buffer, offset);

    if (value8 != VP2Constants::INVALID_HUMIDITY) {
        humidity = static_cast<Humidity>(value8);
        valid = true;
    }

    return humidity;
}


////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
UvIndex
VP2Decoder::decodeUvIndex(const byte buffer[], int offset, bool &valid) {
    UvIndex uvIndex = 0.0;
    valid = false;

    int value8 = BitConverter::toInt8(buffer, offset);

    if (value8 != VP2Constants::INVALID_UV_INDEX) {
        uvIndex = static_cast<UvIndex>(value8) / VP2Constants::UV_INDEX_SCALE;
        valid = true;
    }

    return uvIndex;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Evapotranspiration
VP2Decoder::decodeDayET(const byte buffer[], int offset, bool &valid) {
    Evapotranspiration et = 0.0;
    valid = false;

    int value8 = BitConverter::toInt8(buffer, offset);

    if (value8 != VP2Constants::INVALID_ET) {
        et = UnitConverter::toMillimeter(static_cast<Evapotranspiration>(value8) / VP2Constants::DAY_ET_SCALE);
        valid = true;
    }

    return et;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Evapotranspiration
VP2Decoder::decodeMonthYearET(const byte buffer[], int offset, bool &valid) {
    Evapotranspiration et = 0.0;
    valid = false;

    int value8 = BitConverter::toInt8(buffer, offset);

    if (value8 != VP2Constants::INVALID_ET) {
        et = UnitConverter::toMillimeter(static_cast<Evapotranspiration>(value8) / VP2Constants::MONTH_YEAR_ET_SCALE);
        valid = true;
    }

    return et;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
SolarRadiation
VP2Decoder::decodeSolarRadiation(const byte buffer[], int offset, bool &valid) {
    SolarRadiation sr = 0.0;
    valid = false;

    int value16 = BitConverter::toInt16(buffer, offset);
    if (value16 != VP2Constants::INVALID_SOLAR_RADIATION) {
        sr = static_cast<SolarRadiation>(value16);
        valid = true;
    }

    return sr;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Speed
VP2Decoder::decodeWindSpeed(const byte buffer[], int offset, bool &valid) {
    Speed windSpeed = 0.0;
    valid = false;

    int value8 = BitConverter::toInt8(buffer, offset);

    if (value8 != VP2Constants::INVALID_WIND_SPEED) {
        windSpeed = UnitConverter::toMetersPerSecond(static_cast<Speed>(value8));
        valid = true;
    }

    std::cout << "Wind speed decode. Value: " << value8 << " Speed: " << windSpeed << std::endl;
    return windSpeed;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Speed
VP2Decoder::decodeAvgWindSpeed(const byte buffer[], int offset, bool &valid) {
    Speed windSpeed = 0.0;
    valid = false;

    int value16 = BitConverter::toInt16(buffer, offset);

    if (value16 != VP2Constants::INVALID_WIND_SPEED) {
        windSpeed = UnitConverter::toMetersPerSecond(static_cast<Speed>(value16) / VP2Constants::AVG_WIND_SPEED_SCALE);
        valid = true;
    }

    return windSpeed;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Heading
VP2Decoder::decodeWindDirectionSlice(const byte buffer[], int offset, bool &valid) {
    Heading heading = 0.0F;
    valid = false;

    int value8 = BitConverter::toInt8(buffer, offset);

    if (value8 != VP2Constants::INVALID_WIND_DIRECTION_SLICE) {
        heading = static_cast<Heading>(value8) * VP2Constants::DEGREES_PER_SLICE;
        valid = true;
    }

    return heading;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Heading
VP2Decoder::decodeWindDirection(const byte buffer[], int offset, bool &valid) {
    Heading heading = 0.0F;
    valid = false;

    int value16 = BitConverter::toInt16(buffer, offset);

    if (value16 != VP2Constants::INVALID_WIND_DIRECTION) {
        if (value16 == 360)
            heading = 0.0;
        else
            heading = static_cast<Heading>(value16);

        valid = true;
    }

    return heading;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Rainfall
VP2Decoder::decodeStormRain(const byte buffer[], int offset) {
    int value16 = BitConverter::toInt16(buffer, offset);
    Rainfall rain = UnitConverter::toMillimeter(static_cast<Rainfall>(value16) / VP2Constants::STORM_RAIN_SCALE);

    return rain;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
VP2Decoder::setRainCollectorSize(Rainfall collectorSize) {
    rainCollectorSize = collectorSize;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Rainfall
VP2Decoder::decodeRain(const byte buffer[], int offset) {
    int value16 = BitConverter::toInt16(buffer, offset);
    Rainfall rain = UnitConverter::toMillimeter(static_cast<Rainfall>(value16) * rainCollectorSize);

    return rain;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
DateTime
VP2Decoder::decodeStormStartDate(const byte buffer[], int offset) {
    DateTime stormStart = 0;
    int value16 = BitConverter::toInt16(buffer, offset);

    if (value16 != VP2Constants::NO_STORM_ACTIVE_DATE) {
        int year = (value16 & 0x3F) + VP2Constants::YEAR_OFFSET;
        int day = (value16 >> 7) & 0x1F;
        int month = (value16 >> 12) & 0xF;

        struct tm tm;
        tm.tm_year = year - 1900;
        tm.tm_mon = month - 1;
        tm.tm_mday = day;
        tm.tm_hour = 0;
        tm.tm_min = 0;
        tm.tm_sec = 0;

        stormStart = mktime(&tm);
    }

    return stormStart;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
float
VP2Decoder::decodeConsoleBatteryVoltage(const byte buffer[], int offset) {
    int value16 = BitConverter::toInt16(buffer, offset);

    float consoleBatteryVoltage = static_cast<float>(value16 * 300) / 512.0F / 100.0F;

    return consoleBatteryVoltage;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
LeafWetness
VP2Decoder::decodeLeafWetness(const byte buffer[], int offset, bool &valid) {
    valid = false;
    LeafWetness lw = VP2Constants::MIN_LEAF_WETNESS;

    int value8 = BitConverter::toInt8(buffer, offset);

    if (value8 != VP2Constants::INVALID_LEAF_WETNESS && value8 >= VP2Constants::MIN_LEAF_WETNESS && value8 <= VP2Constants::MAX_LEAF_WETNESS) {
        valid = true;
        lw = static_cast<LeafWetness>(value8);
    }

    return lw;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
SoilMoisture
VP2Decoder::decodeSoilMoisture(const byte buffer[], int offset, bool &valid) {
    valid = false;
    SoilMoisture sm = 0;

    int value8 = BitConverter::toInt8(buffer, offset);

    if (value8 != VP2Constants::INVALID_SOIL_MOISTURE) {
        valid = true;
        sm = static_cast<SoilMoisture>(value8);
    }

    return sm;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
DateTime
VP2Decoder::decodeTime(const byte buffer[], int offset) {
    int value16 = BitConverter::toInt16(buffer, offset);
    int minute = value16 % 100;
    int hour = value16 / 100;

    time_t now = time(0);
    struct tm tm;
    Weather::localtime(now, tm);
    tm.tm_hour = hour;
    tm.tm_min = minute;
    tm.tm_sec = 0;
    DateTime t = mktime(&tm);

    return t;
}

/*
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
DateTime
VP2Decoder::decodeArchiveDate(const byte buffer[], int offset) {
    int value16 = BitConverter::toInt16(buffer, offset);

    int year = ((date >> 9) & 0x3F) + VP2Constants::YEAR_OFFSET;
    int month = (date >> 5) & 0xF;
    int day = date & 0x1F;

    return 0;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
int
VP2Decoder::encodeArchiveDateTime(byte buffer[], int offset, DateTime dateTime) {
    int value16 = 0;

    int year = ((date >> 9) & 0x3F) + VP2Constants::YEAR_OFFSET;
    int month = (date >> 5) & 0xF;
    int day = date & 0x1F;

    return 0;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
DateTime
VP2Decoder::decodeDateTime(const byte buffer[], int dateOffset, int timeOffset) {
    return 0;
}
*/
}
