#include <iostream>
#include "BitConverter.h"
#include "UnitConverter.h"
#include "VP2Constants.h"
#include "VP2Decoder.h"

namespace vp2 {

Rainfall VP2Decoder::rainCollectorSize = static_cast<Rainfall>(0.0);
bool VP2Decoder::rainCollectorSizeSet = false;
VP2Logger * log = nullptr;

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Temperature> &
VP2Decoder::decode16BitTemperature(const byte buffer[], int offset, Measurement<Temperature> & measurement) {
    int value16 = BitConverter::toInt16(buffer, offset);

    if (value16 != VP2Constants::INVALID_16BIT_TEMPERATURE)
        measurement.setValue(UnitConverter::toCelsius(static_cast<Temperature>(value16) / VP2Constants::TEMPERATURE_16BIT_SCALE));
    else
        measurement.invalidate();

    return measurement;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Measurement<Temperature>
VP2Decoder::decode16BitTemperature(const byte buffer[], int offset) {
    Measurement<Temperature> measurement;
    return decode16BitTemperature(buffer, offset, measurement);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Temperature> &
VP2Decoder::decodeNonScaled16BitTemperature(const byte buffer[], int offset, Measurement<Temperature> & measurement) {
    int value16 = BitConverter::toInt16(buffer, offset);

    if (value16 != VP2Constants::INVALID_16BIT_TEMPERATURE)
        measurement.setValue(UnitConverter::toCelsius(static_cast<Temperature>(value16)));
    else
        measurement.invalidate();

    return measurement;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Measurement<Temperature>
VP2Decoder::decodeNonScaled16BitTemperature(const byte buffer[], int offset) {
    Measurement<Temperature> measurement;
    return decodeNonScaled16BitTemperature(buffer, offset, measurement);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Temperature> &
VP2Decoder::decode8BitTemperature(const byte buffer[], int offset, Measurement<Temperature> & measurement) {
    int value8 = BitConverter::toInt8(buffer, offset);

    if (value8 != VP2Constants::INVALID_8BIT_TEMPERATURE)
        measurement.setValue(UnitConverter::toCelsius(static_cast<Temperature>(value8 - VP2Constants::TEMPERATURE_8BIT_OFFSET)));
    else
        measurement.invalidate();

    return measurement;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Measurement<Temperature>
VP2Decoder::decode8BitTemperature(const byte buffer[], int offset) {
    Measurement<Temperature> measurement;
    return decode8BitTemperature(buffer, offset, measurement);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Pressure> &
VP2Decoder::decodeBarometricPressure(const byte buffer[], int offset, Measurement<Pressure> & measurement) {
    int value16 = BitConverter::toInt16(buffer, offset);

    measurement = UnitConverter::toMillibars(static_cast<Pressure>(value16) / VP2Constants::BAROMETER_SCALE);

    return measurement;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Measurement<Pressure>
VP2Decoder::decodeBarometricPressure(const byte buffer[], int offset) {
    Measurement<Pressure> measurement;
    return decodeBarometricPressure(buffer, offset, measurement);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Humidity> &
VP2Decoder::decodeHumidity(const byte buffer[], int offset, Measurement<Humidity> & measurement) {
    int value8 = BitConverter::toInt8(buffer, offset);

    if (value8 != VP2Constants::INVALID_HUMIDITY)
        measurement.setValue(static_cast<Humidity>(value8));
    else
        measurement.invalidate();

    return measurement;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Measurement<Humidity>
VP2Decoder::decodeHumidity(const byte buffer[], int offset) {
    Measurement<Humidity> measurement;
    return decodeHumidity(buffer, offset, measurement);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<UvIndex> &
VP2Decoder::decodeUvIndex(const byte buffer[], int offset, Measurement<UvIndex> & measurement) {
    int value8 = BitConverter::toInt8(buffer, offset);

    if (value8 != VP2Constants::INVALID_UV_INDEX)
        measurement.setValue(static_cast<UvIndex>(value8) / VP2Constants::UV_INDEX_SCALE);
    else
        measurement.invalidate();

    return measurement;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Measurement<UvIndex>
VP2Decoder::decodeUvIndex(const byte buffer[], int offset) {
    Measurement<UvIndex> measurement;
    return decodeUvIndex(buffer, offset, measurement);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Evapotranspiration> &
VP2Decoder::decodeDayET(const byte buffer[], int offset, Measurement<Evapotranspiration> & measurement) {
    int value16 = BitConverter::toInt16(buffer, offset);

    if (value16 != VP2Constants::INVALID_ET)
        measurement.setValue(UnitConverter::toMillimeter(static_cast<Evapotranspiration>(value16) / VP2Constants::DAY_ET_SCALE));
    else
        measurement.invalidate();

    return measurement;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Measurement<Evapotranspiration>
VP2Decoder::decodeDayET(const byte buffer[], int offset) {
    Measurement<Evapotranspiration> measurement;
    return decodeDayET(buffer, offset, measurement);

}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Evapotranspiration> &
VP2Decoder::decodeMonthYearET(const byte buffer[], int offset, Measurement<Evapotranspiration> & measurement) {
    int value16 = BitConverter::toInt16(buffer, offset);

    if (value16 != VP2Constants::INVALID_ET)
        measurement.setValue(UnitConverter::toMillimeter(static_cast<Evapotranspiration>(value16) / VP2Constants::MONTH_YEAR_ET_SCALE));
    else
        measurement.invalidate();

    return measurement;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Measurement<Evapotranspiration>
VP2Decoder::decodeMonthYearET(const byte buffer[], int offset) {
    Measurement<Evapotranspiration> measurement;
    return decodeMonthYearET(buffer, offset, measurement);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<SolarRadiation> &
VP2Decoder::decodeSolarRadiation(const byte buffer[], int offset, Measurement<SolarRadiation> & measurement) {
    int value16 = BitConverter::toInt16(buffer, offset);

    if (value16 != VP2Constants::INVALID_SOLAR_RADIATION)
        measurement.setValue(static_cast<SolarRadiation>(value16));
    else
        measurement.invalidate();

    return measurement;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Measurement<SolarRadiation>
VP2Decoder::decodeSolarRadiation(const byte buffer[], int offset) {
    Measurement<SolarRadiation> measurement;
    return decodeSolarRadiation(buffer, offset, measurement);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Speed> &
VP2Decoder::decodeWindSpeed(const byte buffer[], int offset, Measurement<Speed> & measurement) {
    int value8 = BitConverter::toInt8(buffer, offset);

    if (value8 != VP2Constants::INVALID_WIND_SPEED)
        measurement.setValue(UnitConverter::toMetersPerSecond(static_cast<Speed>(value8)));
    else
        measurement.invalidate();

    return measurement;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Measurement<Speed>
VP2Decoder::decodeWindSpeed(const byte buffer[], int offset) {
    Measurement<Speed> measurement;
    return decodeWindSpeed(buffer, offset, measurement);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Speed> &
VP2Decoder::decode16BitWindSpeed(const byte buffer[], int offset, Measurement<Speed> & measurement) {
    int value16 = BitConverter::toInt16(buffer, offset);

    measurement.setValue(UnitConverter::toMetersPerSecond(static_cast<Speed>(value16)));

    return measurement;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Measurement<Speed>
VP2Decoder::decode16BitWindSpeed(const byte buffer[], int offset) {
    Measurement<Speed> measurement;
    return decode16BitWindSpeed(buffer, offset, measurement);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Speed> &
VP2Decoder::decodeAvgWindSpeed(const byte buffer[], int offset, Measurement<Speed> & measurement) {
    int value16 = BitConverter::toInt16(buffer, offset);

    if (value16 != VP2Constants::INVALID_WIND_SPEED)
        measurement.setValue(UnitConverter::toMetersPerSecond(static_cast<Speed>(value16) / VP2Constants::AVG_WIND_SPEED_SCALE));
    else
        measurement.invalidate();

    return measurement;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Measurement<Speed>
VP2Decoder::decodeAvgWindSpeed(const byte buffer[], int offset) {
    Measurement<Speed> measurement;
    return decodeAvgWindSpeed(buffer, offset, measurement);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Heading> &
VP2Decoder::decodeWindDirectionSlice(const byte buffer[], int offset, Measurement<Heading> & measurement) {
    int value8 = BitConverter::toInt8(buffer, offset);

    if (value8 != VP2Constants::INVALID_WIND_DIRECTION_SLICE)
        measurement.setValue(static_cast<Heading>(value8) * VP2Constants::DEGREES_PER_SLICE);
    else
        measurement.invalidate();

    return measurement;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Measurement<Heading>
VP2Decoder::decodeWindDirectionSlice(const byte buffer[], int offset) {
    Measurement<Heading> measurement;
    return decodeWindDirectionSlice(buffer, offset, measurement);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Heading> &
VP2Decoder::decodeWindDirection(const byte buffer[], int offset, Measurement<Heading> & measurement) {
    int value16 = BitConverter::toInt16(buffer, offset);

    if (value16 != VP2Constants::INVALID_WIND_DIRECTION) {
        Heading heading;
        if (value16 == VP2Constants::NORTH_HEADING_VALUE)
            heading = 0.0;
        else
            heading = static_cast<Heading>(value16);

        measurement.setValue(heading);
    }
    else
        measurement.invalidate();

    return measurement;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Measurement<Heading>
VP2Decoder::decodeWindDirection(const byte buffer[], int offset) {
    Measurement<Heading> measurement;
    return decodeWindDirection(buffer, offset, measurement);
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
    rainCollectorSizeSet = true;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Rainfall
VP2Decoder::decodeRain(const byte buffer[], int offset) {

    if (log == nullptr)
        log = &VP2Logger::getLogger("VP2Decoder");

    if (!rainCollectorSizeSet)
        log->log(VP2Logger::VP2_WARNING) << "Decoding rain value before rain collector size has been set" << std::endl;
    
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
        tm.tm_year = year - TIME_STRUCT_YEAR_OFFSET;
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
const Measurement<LeafWetness> &
VP2Decoder::decodeLeafWetness(const byte buffer[], int offset, Measurement<LeafWetness> & measurement) {
    int value8 = BitConverter::toInt8(buffer, offset);

    if (value8 != VP2Constants::INVALID_LEAF_WETNESS && value8 >= VP2Constants::MIN_LEAF_WETNESS && value8 <= VP2Constants::MAX_LEAF_WETNESS)
        measurement.setValue(static_cast<LeafWetness>(value8));
    else
        measurement.invalidate();

    return measurement;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Measurement<LeafWetness>
VP2Decoder::decodeLeafWetness(const byte buffer[], int offset) {
    Measurement<LeafWetness> measurement;
    return decodeLeafWetness(buffer, offset, measurement);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<SoilMoisture> &
VP2Decoder::decodeSoilMoisture(const byte buffer[], int offset, Measurement<SoilMoisture> & measurement) {
    int value8 = BitConverter::toInt8(buffer, offset);

    if (value8 != VP2Constants::INVALID_SOIL_MOISTURE)
        measurement.setValue(static_cast<SoilMoisture>(value8));
    else
        measurement.invalidate();

    return measurement;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Measurement<SoilMoisture>
VP2Decoder::decodeSoilMoisture(const byte buffer[], int offset) {
    Measurement<SoilMoisture> measurement;
    return decodeSoilMoisture(buffer, offset, measurement);
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

}
