
#include "BitConverter.h"
#include "Weather.h"
#include "VP2Utils.h"
#include "HiLowPacket.h"

namespace vp2 {

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
HiLowPacket::HiLowPacket() : log(VP2Logger::getLogger("HiLowPacket")) {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
HiLowPacket::~HiLowPacket() {
}

std::string
HiLowPacket::formatMessage() const {
   return "";
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
HiLowPacket::decodeHiLowTemperature(const byte buffer[], HighLowValues<Temperature> & values, int baseOffset) {
    bool valid;

    values.lows.dayExtremeValue = VP2Utils::decode16BitTemperature(buffer, baseOffset, valid);
    values.highs.dayExtremeValue = VP2Utils::decode16BitTemperature(buffer, baseOffset + 2, valid);

    int value16 = BitConverter::toInt16(buffer, baseOffset + 4);
    values.lows.dayExtremeValueTime = static_cast<DateTime>(value16);

    value16 = BitConverter::toInt16(buffer, baseOffset + 6);
    values.highs.dayExtremeValueTime = static_cast<DateTime>(value16);

    values.lows.monthExtremeValue = VP2Utils::decode16BitTemperature(buffer, baseOffset + 8, valid);
    values.highs.monthExtremeValue = VP2Utils::decode16BitTemperature(buffer, baseOffset + 10, valid);
    values.lows.yearExtremeValue = VP2Utils::decode16BitTemperature(buffer, baseOffset + 12, valid);
    values.highs.yearExtremeValue = VP2Utils::decode16BitTemperature(buffer, baseOffset + 14, valid);

    return true;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
HiLowPacket::decodeHiLowPacket(byte buffer[]) {

    //
    // Barometer section
    //
    bool valid;
    barometer.lows.dayExtremeValue    = VP2Utils::decodeBarometricPressure(buffer, 0, valid);
    barometer.highs.dayExtremeValue   = VP2Utils::decodeBarometricPressure(buffer, 2, valid);
    barometer.lows.monthExtremeValue  = VP2Utils::decodeBarometricPressure(buffer, 4, valid);
    barometer.highs.monthExtremeValue = VP2Utils::decodeBarometricPressure(buffer, 6, valid);
    barometer.lows.yearExtremeValue   = VP2Utils::decodeBarometricPressure(buffer, 8, valid);
    barometer.highs.yearExtremeValue  = VP2Utils::decodeBarometricPressure(buffer, 10, valid);


    int value16 = BitConverter::toInt16(buffer, 12);
    barometer.lows.dayExtremeValueTime = value16;

    value16 = BitConverter::toInt16(buffer, 14);
    barometer.highs.dayExtremeValueTime = value16;

    //
    // Wind section
    //
    int value8 = BitConverter::toInt8(buffer, 16);
    wind.dayExtremeValue = value8;

    value16 = BitConverter::toInt16(buffer, 17);
    wind.dayExtremeValueTime = value16;

    value8 = BitConverter::toInt8(buffer, 19);
    wind.monthExtremeValue = value8;

    value8 = BitConverter::toInt8(buffer, 20);
    wind.yearExtremeValue = value8;

    //
    // Indoor humidity section
    //
    value8 = BitConverter::toInt8(buffer, 37);
    insideHumidity.lows.dayExtremeValue = static_cast<Humidity>(value16);

    value8 = BitConverter::toInt8(buffer, 38);
    insideHumidity.highs.dayExtremeValue = static_cast<Humidity>(value8);

    value16 = BitConverter::toInt16(buffer, 39);
    insideHumidity.lows.dayExtremeValueTime = value16;

    value16 = BitConverter::toInt16(buffer, 41);
    insideHumidity.highs.dayExtremeValueTime = value16;

    value8 = BitConverter::toInt8(buffer, 43);
    insideHumidity.lows.monthExtremeValue = static_cast<Humidity>(value8);

    value8 = BitConverter::toInt8(buffer, 44);
    insideHumidity.highs.monthExtremeValue = static_cast<Humidity>(value8);

    value8 = BitConverter::toInt8(buffer, 45);
    insideHumidity.lows.yearExtremeValue = static_cast<Humidity>(value8);

    value8 = BitConverter::toInt8(buffer, 46);
    insideHumidity.highs.yearExtremeValue = static_cast<Humidity>(value8);

    decodeHiLowTemperature(buffer, insideTemperature, 21);
    decodeHiLowTemperature(buffer, outsideTemperature, 47);
    decodeHiLowTemperature(buffer, dewPoint, 63);
    decodeHiLowTemperature(buffer, extraTemperatures[0], 126);
    int baseOffset = 126 + (16 * 7);
    decodeHiLowTemperature(buffer, soilTemperatures[0], baseOffset);
    baseOffset += (16 * 4);
    decodeHiLowTemperature(buffer, leafTemperatures[0], baseOffset);

    return true;
}

}
