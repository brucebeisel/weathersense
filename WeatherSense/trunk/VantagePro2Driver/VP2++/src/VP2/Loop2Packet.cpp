/* 
 * Copyright (C) 2015 Bruce Beisel
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
#include <iostream>
#include "BitConverter.h"
#include "UnitConverter.h"
#include "VP2Constants.h"
#include "VantagePro2CRC.h"
#include "Loop2Packet.h"

using namespace std;

namespace vp2 {

Rainfall Loop2Packet::rainfallIncrement = 0.0;

Loop2Packet::Loop2Packet() : log(VP2Logger::getLogger("Loop2Packet")) {
}

Loop2Packet::~Loop2Packet() {
}

Speed
Loop2Packet::getWindGust10Minute() const {
    return windGust10Minute;
}

Heading
Loop2Packet::getWindGustHeading10Minute() const {
    return windGustHeading10Minute;
}

Speed
Loop2Packet::getWindSpeed2MinuteAvg() const {
    return windSpeed2MinuteAvg;
}

Rainfall
Loop2Packet::getRain15Minute() const {
    return rain15Minute;
}

Rainfall
Loop2Packet::getRainHour() const {
    return rainHour;
}

Rainfall
Loop2Packet::getRain24Hour() const {
    return rain24Hour;
}

Temperature
Loop2Packet::getDewPoint() const {
    return dewPoint;
}

Temperature
Loop2Packet::getHeatIndex() const {
    return heatIndex;
}

Temperature
Loop2Packet::getWindChill() const {
    return windChill;
}

Temperature
Loop2Packet::getThsw() const {
    return thsw;
}

bool
Loop2Packet::isThswValid() const {
    return thswValid;
}

Pressure
Loop2Packet::getAtmPressure() const {
    return atmPressure;
}

/// <summary>
/// Parse the LOOP 2 packet
/// </summary>
/// <param name="buffer">The buffer containing the LOOP 2 packet</param>
/// <param name="rainInterval">The amount of rain 1 bucket tip represents</param>
/// <returns>True if the packet was parsed successfully and passed the CRC check</returns>
bool
Loop2Packet::parseLoop2Packet(const byte *buffer) {
    if (rainfallIncrement == 0.0) {
        log.log(VP2Logger::VP2_ERROR) << "Rain increment not set, cannot parse LOOP2 packet" << endl;
        return false;
    }

    if (buffer[0] != 'L' || buffer[1] != 'O' || buffer[2] != 'O') {
        log.log(VP2Logger::VP2_ERROR) << "LOOP2 buffer does not begin with LOO: [0] = " << buffer[0] << " [1] = " << buffer[1] << " [2] = " << buffer[2] << endl;
        return false;
    }

    //
    // Check for correct packet type
    //
    int packetType = BitConverter::toInt8(buffer, 4);
    if (packetType != 1) {
        log.log(VP2Logger::VP2_ERROR) << "Invalid packet type for LOOP2 packet: " << packetType << endl;
        return false;
    }

    windGust10Minute = UnitConverter::toMetersPerSecond(static_cast<Speed>(BitConverter::toInt16(buffer, 22)));
    windGustHeading10Minute = static_cast<Heading>(BitConverter::toInt16(buffer, 24));
    if (windGustHeading10Minute == 360)
        windGustHeading10Minute = 0;

    windSpeed2MinuteAvg = UnitConverter::toMetersPerSecond((Speed)BitConverter::toInt16(buffer, 20) / 10.0F);
    rain15Minute = UnitConverter::toMillimeter((Rainfall)BitConverter::toInt16(buffer, 52) * rainfallIncrement);
    rainHour = UnitConverter::toMillimeter((Rainfall)BitConverter::toInt16(buffer, 54) * rainfallIncrement);
    rain24Hour = UnitConverter::toMillimeter((Rainfall)BitConverter::toInt16(buffer, 58) * rainfallIncrement);
    dewPoint = UnitConverter::toCelcius(BitConverter::toInt16(buffer, 30));
    heatIndex = UnitConverter::toCelcius(BitConverter::toInt16(buffer, 35));
    windChill = UnitConverter::toCelcius(BitConverter::toInt16(buffer, 37));
    int thswRaw = BitConverter::toInt16(buffer, 37);
    thswValid = thswRaw != 32767;
    thsw = UnitConverter::toCelcius(thswRaw);
    atmPressure = (Pressure)BitConverter::toInt16(buffer, 65) / 1000.0F;


    if ((char)buffer[95] != VP2Constants::LINE_FEED || (char)buffer[96] != VP2Constants::CARRIAGE_RETURN) {
        log.log(VP2Logger::VP2_ERROR) << "<LF><CR> not found" << endl;
        return false;
    }

    bool rv = VantagePro2CRC::checkCRC(buffer, 97);

    if (!rv)
        log.log(VP2Logger::VP2_ERROR) << "LOOP2 packet failed CRC check" << endl;

    return rv;
}

void
Loop2Packet::setRainfallIncrement(Rainfall increment) {
    rainfallIncrement = increment;
}
} // End namespace vp2