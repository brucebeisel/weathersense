#include <sstream>
#include "BitConverter.h"
#include "Weather.h"
#include "VP2Decoder.h"
#include "HiLowPacket.h"

using namespace std;

namespace vp2 {

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
HiLowPacket::HiLowPacket() : log(VP2Logger::getLogger("HiLowPacket")) {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
HiLowPacket::~HiLowPacket() {
}


////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
template<typename T>
string
HiLowPacket::Values<T>::formatXML(bool low) const {
    ostringstream ss;

    string which = low ? "low" : "high";

    ss << "    <" << which << ">" << endl
       << "        <day>" << endl
       << "            <value>" << dayExtremeValue << "</value>" << endl
       << "            <time>" << Weather::formatDateTime(dayExtremeValueTime) << "</time>" << endl
       << "        </day>" << endl
       << "        <month>" << monthExtremeValue << "</month>" << endl
       << "        <year>" << yearExtremeValue <<"</year>" << endl
       << "    </" << which << ">" << endl;

    return ss.str();
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
template<typename T>
string
HiLowPacket::HighLowValues<T>::formatXML() const {
    string s = lows.formatXML(true).append(highs.formatXML(false));
    return s;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
std::string
HiLowPacket::formatMessage() const {
    ostringstream ss;
    ss << "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" << endl;
    ss << "<hiLowPacket>" << endl;
    ss << "<barometer>" << endl;
    ss << barometer.formatXML();
    ss << "</barometer>" << endl;
    ss << "<wind>" << endl;
    ss << wind.formatXML(false) << endl;
    ss << "</wind>" << endl;
    ss << "<insideTemperature>" << endl;
    ss << insideTemperature.formatXML();
    ss << "</insideTemperature>" << endl;
    ss << "<outsideTemperature>" << endl;
    ss << outsideTemperature.formatXML();
    ss << "</outsideTemperature>" << endl;
    ss << "<insideHumidity>" << endl;
    ss << insideHumidity.formatXML();
    ss << "</insideHumidity>" << endl;
    ss << "<outsideHumidity>" << endl;
    ss << outsideHumidity.formatXML();
    ss << "</outsideHumidity>" << endl;
    ss << "<dewPoint>" << endl;
    ss << dewPoint.formatXML();
    ss << "</dewPoint>" << endl;
    ss << "<windChill>" << endl;
    ss << windChill.formatXML(true);
    ss << "</windChill>" << endl;
    ss << "<heatIndex>" << endl;
    ss << heatIndex.formatXML(false);
    ss << "</heatIndex>" << endl;
    ss << "<thsw>" << endl;
    ss << thsw.formatXML(false);
    ss << "</thsw>" << endl;
    ss << "<solarRadiation>" << endl;
    ss << solarRadiation.formatXML(false);
    ss << "</solarRadiation>" << endl;
    ss << "<uvIndex>" << endl;
    ss << uvIndex.formatXML(false);
    ss << "</uvIndex>" << endl;
    ss << "<rainfallRate>" << endl;
    ss << "    <hour>" << highHourRainRate <<"</hour>" << endl;
    ss << rainRate.formatXML(false);
    ss << "</rainfallRate>" << endl;
    ss << "</hiLowPacket>";

   return ss.str();
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
HiLowPacket::decodeHiLowPacket(byte buffer[]) {

    //
    // Barometer section
    //
    bool valid;
    barometer.lows.dayExtremeValue      = VP2Decoder::decodeBarometricPressure(buffer, 0, valid);
    barometer.highs.dayExtremeValue     = VP2Decoder::decodeBarometricPressure(buffer, 2, valid);
    barometer.lows.monthExtremeValue    = VP2Decoder::decodeBarometricPressure(buffer, 4, valid);
    barometer.highs.monthExtremeValue   = VP2Decoder::decodeBarometricPressure(buffer, 6, valid);
    barometer.lows.yearExtremeValue     = VP2Decoder::decodeBarometricPressure(buffer, 8, valid);
    barometer.highs.yearExtremeValue    = VP2Decoder::decodeBarometricPressure(buffer, 10, valid);
    barometer.lows.dayExtremeValueTime  = VP2Decoder::decodeTime(buffer, 12);
    barometer.highs.dayExtremeValueTime = VP2Decoder::decodeTime(buffer, 14);

    //
    // Wind section
    //
    wind.dayExtremeValue     = VP2Decoder::decodeWindSpeed(buffer, 16, valid);
    wind.dayExtremeValueTime = VP2Decoder::decodeTime(buffer, 17);
    wind.monthExtremeValue   = VP2Decoder::decodeWindSpeed(buffer, 19, valid);
    wind.yearExtremeValue    = VP2Decoder::decodeWindSpeed(buffer, 20, valid);

    //
    // Inside temperature section
    //
    insideTemperature.highs.dayExtremeValue     = VP2Decoder::decode16BitTemperature(buffer, 21, valid);
    insideTemperature.lows.dayExtremeValue      = VP2Decoder::decode16BitTemperature(buffer, 23, valid);
    insideTemperature.highs.dayExtremeValueTime = VP2Decoder::decodeTime(buffer, 25);
    insideTemperature.lows.dayExtremeValueTime  = VP2Decoder::decodeTime(buffer, 27);
    insideTemperature.lows.monthExtremeValue    = VP2Decoder::decode16BitTemperature(buffer, 29, valid);
    insideTemperature.highs.monthExtremeValue   = VP2Decoder::decode16BitTemperature(buffer, 31, valid);
    insideTemperature.lows.yearExtremeValue     = VP2Decoder::decode16BitTemperature(buffer, 33, valid);
    insideTemperature.highs.yearExtremeValue    = VP2Decoder::decode16BitTemperature(buffer, 35, valid);

    //
    // Indoor humidity section
    //
    insideHumidity.highs.dayExtremeValue     = VP2Decoder::decodeHumidity(buffer, 37, valid);
    insideHumidity.lows.dayExtremeValue      = VP2Decoder::decodeHumidity(buffer, 38, valid);
    insideHumidity.highs.dayExtremeValueTime = VP2Decoder::decodeTime(buffer, 39);
    insideHumidity.lows.dayExtremeValueTime  = VP2Decoder::decodeTime(buffer, 41);
    insideHumidity.highs.monthExtremeValue   = VP2Decoder::decodeHumidity(buffer, 43, valid);
    insideHumidity.lows.monthExtremeValue    = VP2Decoder::decodeHumidity(buffer, 44, valid);
    insideHumidity.highs.yearExtremeValue    = VP2Decoder::decodeHumidity(buffer, 45, valid);
    insideHumidity.lows.yearExtremeValue     = VP2Decoder::decodeHumidity(buffer, 46, valid);

    //
    // Outside temperature section
    //
    outsideTemperature.lows.dayExtremeValue      = VP2Decoder::decode16BitTemperature(buffer, 47, valid);
    outsideTemperature.highs.dayExtremeValue     = VP2Decoder::decode16BitTemperature(buffer, 49, valid);
    outsideTemperature.lows.dayExtremeValueTime  = VP2Decoder::decodeTime(buffer, 51);
    outsideTemperature.highs.dayExtremeValueTime = VP2Decoder::decodeTime(buffer, 53);
    outsideTemperature.highs.monthExtremeValue   = VP2Decoder::decode16BitTemperature(buffer, 55, valid);
    outsideTemperature.lows.monthExtremeValue    = VP2Decoder::decode16BitTemperature(buffer, 57, valid);
    outsideTemperature.highs.yearExtremeValue    = VP2Decoder::decode16BitTemperature(buffer, 59, valid);
    outsideTemperature.lows.yearExtremeValue     = VP2Decoder::decode16BitTemperature(buffer, 61, valid);

    //
    // Dew point section
    //
    dewPoint.lows.dayExtremeValue      = VP2Decoder::decode16BitTemperature(buffer, 63, valid);
    dewPoint.highs.dayExtremeValue     = VP2Decoder::decode16BitTemperature(buffer, 65, valid);
    dewPoint.lows.dayExtremeValueTime  = VP2Decoder::decodeTime(buffer, 67);
    dewPoint.highs.dayExtremeValueTime = VP2Decoder::decodeTime(buffer, 69);
    dewPoint.highs.monthExtremeValue   = VP2Decoder::decode16BitTemperature(buffer, 71, valid);
    dewPoint.lows.monthExtremeValue    = VP2Decoder::decode16BitTemperature(buffer, 73, valid);
    dewPoint.highs.yearExtremeValue    = VP2Decoder::decode16BitTemperature(buffer, 75, valid);
    dewPoint.lows.yearExtremeValue     = VP2Decoder::decode16BitTemperature(buffer, 77, valid);

    //
    // Wind chill section
    //
    windChill.dayExtremeValue      = VP2Decoder::decode16BitTemperature(buffer, 79, valid);
    windChill.dayExtremeValueTime  = VP2Decoder::decodeTime(buffer, 81);
    windChill.monthExtremeValue    = VP2Decoder::decode16BitTemperature(buffer, 83, valid);
    windChill.yearExtremeValue     = VP2Decoder::decode16BitTemperature(buffer, 85, valid);

    //
    // Heat index section
    //
    heatIndex.dayExtremeValue      = VP2Decoder::decode16BitTemperature(buffer, 87, valid);
    heatIndex.dayExtremeValueTime  = VP2Decoder::decodeTime(buffer, 89);
    heatIndex.monthExtremeValue    = VP2Decoder::decode16BitTemperature(buffer, 91, valid);
    heatIndex.yearExtremeValue     = VP2Decoder::decode16BitTemperature(buffer, 93, valid);

    //
    // THSW index section
    //
    thsw.dayExtremeValue      = VP2Decoder::decode16BitTemperature(buffer, 95, valid);
    thsw.dayExtremeValueTime  = VP2Decoder::decodeTime(buffer, 97);
    thsw.monthExtremeValue    = VP2Decoder::decode16BitTemperature(buffer, 99, valid);
    thsw.yearExtremeValue     = VP2Decoder::decode16BitTemperature(buffer, 101, valid);

    //
    // Solar radiation section
    //
    solarRadiation.dayExtremeValue      = VP2Decoder::decodeSolarRadiation(buffer, 103, valid);
    solarRadiation.dayExtremeValueTime  = VP2Decoder::decodeTime(buffer, 105);
    solarRadiation.monthExtremeValue    = VP2Decoder::decodeSolarRadiation(buffer, 107, valid);
    solarRadiation.yearExtremeValue     = VP2Decoder::decodeSolarRadiation(buffer, 109, valid);

    //
    // UV section
    //
    uvIndex.dayExtremeValue      = VP2Decoder::decodeUvIndex(buffer, 111, valid);
    uvIndex.dayExtremeValueTime  = VP2Decoder::decodeTime(buffer, 112);
    uvIndex.monthExtremeValue    = VP2Decoder::decodeUvIndex(buffer, 114, valid);
    uvIndex.yearExtremeValue     = VP2Decoder::decodeUvIndex(buffer, 115, valid);

    //
    // Rain rate section
    //
    rainRate.dayExtremeValue      = VP2Decoder::decodeRain(buffer, 116);
    rainRate.dayExtremeValueTime  = VP2Decoder::decodeTime(buffer, 118);
    highHourRainRate              = VP2Decoder::decodeRain(buffer, 120);
    rainRate.monthExtremeValue    = VP2Decoder::decodeRain(buffer, 122);
    rainRate.yearExtremeValue     = VP2Decoder::decodeRain(buffer, 124);

    //
    // Extra temperatures section
    //
    for (int i = 0; i < VP2Constants::MAX_EXTRA_TEMPERATURES; i++) {
        extraTemperature[i].lows.dayExtremeValue      = VP2Decoder::decode8BitTemperature(buffer, 126 + i, valid);
        extraTemperature[i].highs.dayExtremeValue     = VP2Decoder::decode8BitTemperature(buffer, 141 + i, valid);
        extraTemperature[i].lows.dayExtremeValueTime  = VP2Decoder::decodeTime(buffer, 156 + (i * 2));
        extraTemperature[i].highs.dayExtremeValueTime = VP2Decoder::decodeTime(buffer, 186 + (i * 2));
        extraTemperature[i].highs.monthExtremeValue   = VP2Decoder::decode8BitTemperature(buffer, 216 + i, valid);
        extraTemperature[i].lows.monthExtremeValue    = VP2Decoder::decode8BitTemperature(buffer, 231 + i, valid);
        extraTemperature[i].highs.yearExtremeValue    = VP2Decoder::decode8BitTemperature(buffer, 246 + i, valid);
        extraTemperature[i].lows.yearExtremeValue     = VP2Decoder::decode8BitTemperature(buffer, 261 + i, valid);
    }

    //
    // Soil temperatures section
    //
    int offset = VP2Constants::MAX_EXTRA_TEMPERATURES;
    for (int i = 0; i < VP2Constants::MAX_SOIL_TEMPERATURES; i++) {
        soilTemperature[i].lows.dayExtremeValue      = VP2Decoder::decode8BitTemperature(buffer, 126 + offset + i, valid);
        soilTemperature[i].highs.dayExtremeValue     = VP2Decoder::decode8BitTemperature(buffer, 141 + offset + i, valid);
        soilTemperature[i].lows.dayExtremeValueTime  = VP2Decoder::decodeTime(buffer, 156 + (offset * 2) + (i * 2));
        soilTemperature[i].highs.dayExtremeValueTime = VP2Decoder::decodeTime(buffer, 186 + (offset * 2) + (i * 2));
        soilTemperature[i].highs.monthExtremeValue   = VP2Decoder::decode8BitTemperature(buffer, 216 + offset + i, valid);
        soilTemperature[i].lows.monthExtremeValue    = VP2Decoder::decode8BitTemperature(buffer, 231 + offset + i, valid);
        soilTemperature[i].highs.yearExtremeValue    = VP2Decoder::decode8BitTemperature(buffer, 246 + offset + i, valid);
        soilTemperature[i].lows.yearExtremeValue     = VP2Decoder::decode8BitTemperature(buffer, 261 + offset + i, valid);
    }

    //
    // Leaf temperature
    offset = VP2Constants::MAX_EXTRA_TEMPERATURES + VP2Constants::MAX_SOIL_TEMPERATURES;
    for (int i = 0; i < VP2Constants::MAX_LEAF_TEMPERATURES; i++) {
        leafTemperature[i].lows.dayExtremeValue      = VP2Decoder::decode8BitTemperature(buffer, 126 + offset + i, valid);
        leafTemperature[i].highs.dayExtremeValue     = VP2Decoder::decode8BitTemperature(buffer, 141 + offset + i, valid);
        leafTemperature[i].lows.dayExtremeValueTime  = VP2Decoder::decodeTime(buffer, 156 + (offset * 2) + (i * 2));
        leafTemperature[i].highs.dayExtremeValueTime = VP2Decoder::decodeTime(buffer, 186 + (offset * 2) + (i * 2));
        leafTemperature[i].highs.monthExtremeValue   = VP2Decoder::decode8BitTemperature(buffer, 216 + offset + i, valid);
        leafTemperature[i].lows.monthExtremeValue    = VP2Decoder::decode8BitTemperature(buffer, 231 + offset + i, valid);
        leafTemperature[i].highs.yearExtremeValue    = VP2Decoder::decode8BitTemperature(buffer, 246 + offset + i, valid);
        leafTemperature[i].lows.yearExtremeValue     = VP2Decoder::decode8BitTemperature(buffer, 261 + offset + i, valid);
    }

    //
    // Outdoor humidity section
    //
    outsideHumidity.lows.dayExtremeValue      = VP2Decoder::decodeHumidity(buffer, 276, valid);
    outsideHumidity.highs.dayExtremeValue     = VP2Decoder::decodeHumidity(buffer, 284, valid);
    outsideHumidity.lows.dayExtremeValueTime  = VP2Decoder::decodeTime(buffer, 308);
    outsideHumidity.highs.dayExtremeValueTime = VP2Decoder::decodeTime(buffer, 292);
    outsideHumidity.highs.monthExtremeValue   = VP2Decoder::decodeHumidity(buffer, 324, valid);
    outsideHumidity.lows.monthExtremeValue    = VP2Decoder::decodeHumidity(buffer, 332, valid);
    outsideHumidity.highs.yearExtremeValue    = VP2Decoder::decodeHumidity(buffer, 340, valid);
    outsideHumidity.lows.yearExtremeValue     = VP2Decoder::decodeHumidity(buffer, 348, valid);

    //
    // Extra humidity section
    //
    for (int i = 0; i < VP2Constants::MAX_EXTRA_HUMIDITIES; i++) {
        extraHumidity[i].lows.dayExtremeValue      = VP2Decoder::decodeHumidity(buffer, 277 + i, valid);
        extraHumidity[i].highs.dayExtremeValue     = VP2Decoder::decodeHumidity(buffer, 285 + i, valid);
        extraHumidity[i].lows.dayExtremeValueTime  = VP2Decoder::decodeTime(buffer, 309 + i);
        extraHumidity[i].highs.dayExtremeValueTime = VP2Decoder::decodeTime(buffer, 293 + i);
        extraHumidity[i].highs.monthExtremeValue   = VP2Decoder::decodeHumidity(buffer, 325 + i, valid);
        extraHumidity[i].lows.monthExtremeValue    = VP2Decoder::decodeHumidity(buffer, 333 + i, valid);
        extraHumidity[i].highs.yearExtremeValue    = VP2Decoder::decodeHumidity(buffer, 341 + 1, valid);
        extraHumidity[i].lows.yearExtremeValue     = VP2Decoder::decodeHumidity(buffer, 349 + i, valid);
    }

    //
    // Soil moisture section
    //
    for (int i = 0; i < VP2Constants::MAX_SOIL_MOISTURES; i++) {
        soilMoisture[i].highs.dayExtremeValue     = VP2Decoder::decodeSoilMoisture(buffer, 356 +i, valid);
        soilMoisture[i].highs.dayExtremeValueTime = VP2Decoder::decodeTime(buffer, 360 + i);
        soilMoisture[i].lows.dayExtremeValue      = VP2Decoder::decodeSoilMoisture(buffer, 368 +i, valid);
        soilMoisture[i].lows.dayExtremeValueTime  = VP2Decoder::decodeTime(buffer, 372 + i);
        soilMoisture[i].lows.monthExtremeValue    = VP2Decoder::decodeSoilMoisture(buffer, 380 + i, valid);
        soilMoisture[i].highs.monthExtremeValue   = VP2Decoder::decodeSoilMoisture(buffer, 384 + i, valid);
        soilMoisture[i].lows.yearExtremeValue     = VP2Decoder::decodeSoilMoisture(buffer, 388 + i, valid);
        soilMoisture[i].highs.yearExtremeValue    = VP2Decoder::decodeSoilMoisture(buffer, 392 + 1, valid);
    }

    //
    // Leaf wetness section
    //
    for (int i = 0; i < VP2Constants::MAX_SOIL_MOISTURES; i++) {
        leafWetness[i].highs.dayExtremeValue     = VP2Decoder::decodeLeafWetness(buffer, 396 +i, valid);
        leafWetness[i].highs.dayExtremeValueTime = VP2Decoder::decodeTime(buffer, 400 + i);
        leafWetness[i].lows.dayExtremeValue      = VP2Decoder::decodeLeafWetness(buffer, 408 +i, valid);
        leafWetness[i].lows.dayExtremeValueTime  = VP2Decoder::decodeTime(buffer, 412 + i);
        leafWetness[i].lows.monthExtremeValue    = VP2Decoder::decodeLeafWetness(buffer, 420 + i, valid);
        leafWetness[i].highs.monthExtremeValue   = VP2Decoder::decodeLeafWetness(buffer, 424 + i, valid);
        leafWetness[i].lows.yearExtremeValue     = VP2Decoder::decodeLeafWetness(buffer, 428 + i, valid);
        leafWetness[i].highs.yearExtremeValue    = VP2Decoder::decodeLeafWetness(buffer, 432 + 1, valid);
    }

    return true;
}

}
