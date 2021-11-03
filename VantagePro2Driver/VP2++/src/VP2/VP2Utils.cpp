#include "VP2Constants.h"
#include "VP2Utils.h"

namespace vp2 {

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Temperature
VP2Utils::parse16BitTemperature(const byte buffer[], int offset, bool & valid) {
    Temperature t = 0.0;
    valid = false;

    int value16 = BitConverter::toInt16(buffer, offset);

    if (value16 != VP2Constants::TEMPERATURE_16BIT_INVALID_VALUE) {
        t = UnitConverter::toCelcius(static_cast<Temperature>(value16) / TEMPERATURE_16BIT_SCALE);
        valid = true;
    }

    return t;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Temperature
VP2Utils::parse8BitTemperature(const byte buffer[], int offset, bool &valid) {
    Temperature t = 0.0;
    valid = false;

    int value8 = BitConverter::toInt8(buffer, offset);

    if (value8 != VP2Constants::TEMPERATURE_8BIT_INVALID_VALUE) {
        t = UnitConverter::toCelcius(static_cast<Temperature>(value8 - TEMPERATURE_8BIT_OFFSET));
        valid = true;
    }

    return t;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Pressure
VP2Utils::parseBarometericPressure(const byte buffer[], int offset, bool &valid) {
    Pressure barometricPressure = 0.0;
    valid = true;

    int value16 = BitConverter::toInt16(buffer, offset);

    barometricPressure = UnitConverter::toMillibars(static_cast<Pressure>(value16) / VP2Constants::BAROMETER_SCALE);

    return barometricPressure;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
DateTime
VP2Utils::parseTime(const byte buffer[], int offset) {
    int value16 = BitConverter::toInt16(buffer, offset);
    int minute = value16 % 100;
    int hour = value16 / 100;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
DateTime
VP2Utils::parseStormDate(const byte buffer[], int offset) {
    int value16 = BitConverter::toInt16(buffer, offset);

    int year = (value16 & 0x3F) + VP2Constants::YEAR_OFFSET;
    int day = (value16 >> 7) & 0x1F;
    int month = (value16 >> 12) & 0xF;

}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
DateTime
VP2Utils::parseArchiveDate(const byte buffer[], int offset) {
    int value16 = BitConverter::toInt16(buffer, offset);

    int year = ((date >> 9) & 0x3F) + VP2Constants::YEAR_OFFSET;
    int month = (date >> 5) & 0xF;
    int day = date & 0x1F;

}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
int
VP2Utils::encodeArchiveDateTime(byte buffer[], int offset, DateTime dateTime) {
    int value16 = 0;

    int year = ((date >> 9) & 0x3F) + VP2Constants::YEAR_OFFSET;
    int month = (date >> 5) & 0xF;
    int day = date & 0x1F;

}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
DateTime
VP2Utils::parseDateTime(const byte buffer[], int dateOffset, int timeOffset) {
}