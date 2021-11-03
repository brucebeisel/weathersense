
namespace vp2 {

    template<typename T>
    struct Values {
        T        dayExtremeValue;
        DateTime dayExtremeValueTime;
        T        monthExtremeValue;
        T        yearExtremeValue;
    };

    typedef LowValues Values;
    typedef HighValues Values;

    template<typename T>
    struct HiLowValues {
        Values<T>  lows;
        Values<T> highs;
    };


bool
HiLowPacket::parseHiLowTemperature(byte buffer[], HiLowValues<Temperature> & values, int baseOffset) {
    bool valid;

    values.lows.dayExtemeValue = VP2Utils::parse16BitTemperature(buffer, baseOffset, valid);
    values.highs.dayExtemeValue = VP2Utils::parse16BitTemperature(buffer, baseOffset + 2, valid);

    value16 = BitConverter::toInt16(buffer, baseOffset + 4);
    values.low.dayExtremeValueTime = static_case<Temperature>(value16);

    value16 = BitConverter::toInt16(buffer, baseOffset + 6);
    values.high.dayExtremeValueTime = static_case<Temperature>(value16);

    values.lows.monthExtemeValue = VP2Utils::parse16BitTemperature(buffer, baseOffset + 8, valid);
    values.highs.monthExtemeValue = VP2Utils::parse16BitTemperature(buffer, baseOffset + 10, valid);
    values.lows.yearExtemeValue = VP2Utils::parse16BitTemperature(buffer, baseOffset + 12, valid);
    values.highs.yearExtemeValue = VP2Utils::parse16BitTemperature(buffer, baseOffset + 14, valid);
}

bool
HiLowPacket::parseHiLowPacket(byte buffer[]) {

    //
    // Barometer section
    //
    bool valid;
    barometer.lows.dayExtemeValue    = VP2Utils::parseBaromtricPressure(buffer, 0, valid);
    barometer.highs.dayExtemeValue   = VP2Utils::parseBaromtricPressure(buffer, 2, valid);
    barometer.lows.monthExtemeValue  = VP2Utils::parseBaromtricPressure(buffer, 4, valid);
    barometer.highs.monthExtemeValue = VP2Utils::parseBaromtricPressure(buffer, 6, valid);
    barometer.lows.yearExtemeValue   = VP2Utils::parseBaromtricPressure(buffer, 8, valid);
    barometer.highs.yearExtemeValue  = VP2Utils::parseBaromtricPressure(buffer, 10, valid);


    value16 = BitConverter::toInt16(buffer, 12);
    barometer.low.dayExtremeValueTime = value16;

    value16 = BitConverter::toInt16(buffer, 14);
    barometer.high.dayExtremeValueTime = value16;

    //
    // Wind section
    //
    int value8 = BitConverter::toInt8(buffer, 16);
    wind.dayExtemeValue = value8;

    value16 = BitConverter::toInt16(buffer, 17);
    wind.dayExtremeValueTime = value16;

    value8 = BitConverter::toInt8(buffer, 19);
    wind.monthExtemeValue = value8;

    value8 = BitConverter::toInt8(buffer, 20);
    wind.yearhExtemeValue = value8;

    //
    // Indoor humidity section
    //
    value8 = BitConverter::toInt8(buffer, 37);
    values.lows.dayExtemeValue = static_cast<Humidity>(value16);

    value8 = BitConverter::toInt8(buffer, 38);
    values.highs.dayExtemeValue = static_case<Humidity>(value8);

    value16 = BitConverter::toInt16(buffer, 39);
    values.low.dayExtremeValueTime = value16;

    value16 = BitConverter::toInt16(buffer, 41);
    values.high.dayExtremeValueTime = value16;

    value8 = BitConverter::toInt8(buffer, 43);
    values.lows.monthExtemeValue = static_case<Humidity>(value8);

    value8 = BitConverter::toInt8(buffer, 44);
    values.highs.monthExtemeValue = static_case<Humidity>(value8);

    value8 = BitConverter::toInt8(buffer, 45);
    values.low.yearhExtemeValue = static_case<Humidity>(value8);

    value8 = BitConverter::toInt8(buffer, 46);
    values.high.yearhExtemeValue = static_case<Humidity>(value8);

    parseHiLowTemperature(buffer, insideTemperature, 21);
    parseHiLowTemperature(buffer, outsideTemperature, 47);
    parseHiLowTemperature(buffer, dewPoint, 63);
    parseHiLowTemperature(buffer, extraTemperatures, 126);
    int baseOffset = 126 + (16 * 7);
    parseHiLowTemperature(buffer, soilTemperatures, baseOffset);
    int baseOffset += (16 * 4);
    parseHiLowTemperature(buffer, leafTemperatures, baseOffset);



}

}
