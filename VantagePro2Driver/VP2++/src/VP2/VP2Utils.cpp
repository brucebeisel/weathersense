#include "BitConverter.h"
#include "UnitConverter.h"
#include "VP2Constants.h"
#include "VP2Utils.h"

namespace vp2 {

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Temperature
VP2Utils::decode16BitTemperature(const byte buffer[], int offset, bool & valid) {
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
VP2Utils::decode8BitTemperature(const byte buffer[], int offset, bool &valid) {
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
VP2Utils::decodeBarometricPressure(const byte buffer[], int offset, bool &valid) {
    Pressure barometricPressure = 0.0;
    valid = true;

    int value16 = BitConverter::toInt16(buffer, offset);

    barometricPressure = UnitConverter::toMillibars(static_cast<Pressure>(value16) / VP2Constants::BAROMETER_SCALE);

    return barometricPressure;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Humidity
VP2Utils::decodeHumidity(const byte buffer[], int offset, bool & valid) {
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
VP2Utils::decodeUvIndex(const byte buffer[], int offset, bool &valid) {
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
VP2Utils::decodeET(const byte buffer[], int offset, bool &valid) {
    Evapotranspiration et = 0.0;
    valid = false;

    int value8 = BitConverter::toInt8(buffer, offset);

    if (value8 != VP2Constants::INVALID_ET) {
        et = UnitConverter::toMillimeter(static_cast<Evapotranspiration>(value8) / VP2Constants::ET_SCALE);
        valid = true;
    }

    return et;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
SolarRadiation
VP2Utils::decodeSolarRadiation(const byte buffer[], int offset, bool &valid) {
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
VP2Utils::decodeWindSpeed(const byte buffer[], int offset, bool &valid) {
    Speed windSpeed = 0.0;
    valid = false;

    int value8 = BitConverter::toInt8(buffer, offset);

    if (value8 != VP2Constants::INVALID_WIND_SPEED) {
        windSpeed = UnitConverter::toMetersPerSecond(static_cast<Speed>(windSpeed));
        valid = true;
    }

    return windSpeed;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Heading
VP2Utils::decodeWindDirectionSlice(const byte buffer[], int offset, bool &valid) {
    Heading heading = 0.0F;
    valid = false;

    int value8 = BitConverter::toInt8(buffer, offset);

    if (value8 != VP2Constants::INVALID_WIND_DIRECTION) {
        heading = static_cast<Heading>(value8) * VP2Constants::DEGREES_PER_SLICE;
        valid = true;
    }

    return heading;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
DateTime
VP2Utils::decodeTime(const byte buffer[], int offset) {
    int value16 = BitConverter::toInt16(buffer, offset);
    int minute = value16 % 100;
    int hour = value16 / 100;

    return 0;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
/*
DateTime
VP2Utils::decodeStormDate(const byte buffer[], int offset) {
    int value16 = BitConverter::toInt16(buffer, offset);

    int year = (value16 & 0x3F) + VP2Constants::YEAR_OFFSET;
    int day = (value16 >> 7) & 0x1F;
    int month = (value16 >> 12) & 0xF;

    return 0;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
DateTime
VP2Utils::decodeArchiveDate(const byte buffer[], int offset) {
    int value16 = BitConverter::toInt16(buffer, offset);

    int year = ((date >> 9) & 0x3F) + VP2Constants::YEAR_OFFSET;
    int month = (date >> 5) & 0xF;
    int day = date & 0x1F;

    return 0;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
int
VP2Utils::encodeArchiveDateTime(byte buffer[], int offset, DateTime dateTime) {
    int value16 = 0;

    int year = ((date >> 9) & 0x3F) + VP2Constants::YEAR_OFFSET;
    int month = (date >> 5) & 0xF;
    int day = date & 0x1F;

    return 0;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
DateTime
VP2Utils::decodeDateTime(const byte buffer[], int dateOffset, int timeOffset) {
    return 0;
}
*/
}