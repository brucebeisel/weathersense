#include "BitConverter.h"
#include "VantagePro2Configuration.h"

namespace vp2 {

VantagePro2Configuration::VantagePro2Configuration() {
    // TODO Auto-generated constructor stub

}

VantagePro2Configuration::~VantagePro2Configuration() {
    // TODO Auto-generated destructor stub
}

void
VantagePro2Configuration::decodeData(const byte buffer[]) {
    issLatitude = static_cast<double>(BitConverter::toInt16(buffer, 11)) / 10.0;
    issLongitude = static_cast<double>(BitConverter::toInt16(buffer, 13)) / 10.0;
    issElevation = BitConverter::toInt16(buffer, 15);
    timezoneIndex = static_cast<char>(buffer[17]) - '0';
    manualDaylightSavingsTime = BitConverter::toInt8(buffer, 18) == 1;
    manualDaylightSavingsTimeOn = BitConverter::toInt8(buffer, 19) == 1;
    int value16 = BitConverter::toInt16(buffer, 20);
    gmtOffsetMinutes = (value16 / 100) + ((value16 % 100) * 60 / 100);
    useTimezone = BitConverter::toInt8(buffer, 22) == 0;
    int setupBits = BitConverter::toInt8(buffer, 43);
    amPmMode = (setupBits & 0x1) == 0;
    isAM = (setupBits & 0x2) == 1;
    monthDayFormat = (setupBits & 0x4) == 1;
    windCupLarge = (setupBits & 0x8) == 1;
    latitudeNorth = (setupBits & 0x40) == 1;
    longitudeEast = (setupBits & 0x80) == 1;
    RainCupSizeType type = static_cast<RainCupSizeType>((setupBits & 0x30) >> 4);
    switch (type) {
        case POINT_01_INCH:
            rainCollectorSize = POINT_01_INCH_SIZE;
            break;
        case POINT_02_MM:
            rainCollectorSize = POINT_02_MM_SIZE;
            break;
        case POINT_01_MM:
            rainCollectorSize = POINT_01_MM_SIZE;
            break;
    }
}

bool
VantagePro2Configuration::retrieveConfigurationParameters() {
    byte buffer[EEPROMR::EEPROM_NON_]
    if (station.eepromBinaryRead(0, EEPROM_NON_GRAPH_DATA_SIZE)) {
        memcpy(eepromNonGraphData, buffer, EEPROM_NON_GRAPH_DATA_SIZE);
        stationConfiguration.decodeData(eepromNonGraphData);
        return true;
    }
    else
        return false;
}

}
