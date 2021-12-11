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
#include <iostream>
#include "BitConverter.h"
#include "UnitConverter.h"
#include "VP2Constants.h"
#include "VantagePro2CRC.h"
#include "VP2Decoder.h"
#include "Loop2Packet.h"

using namespace std;

namespace vp2 {

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Loop2Packet::Loop2Packet() : log(VP2Logger::getLogger("Loop2Packet")) {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Loop2Packet::~Loop2Packet() {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Speed> &
Loop2Packet::getWindGust10Minute() const {
    return windGust10Minute;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Heading> &
Loop2Packet::getWindGustHeading10Minute() const {
    return windGustHeading10Minute;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Speed> &
Loop2Packet::getWindSpeed2MinuteAvg() const {
    return windSpeed2MinuteAvg;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Speed> &
Loop2Packet::getWindSpeed10MinuteAvg() const {
    return windSpeed10MinuteAvg;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Rainfall
Loop2Packet::getRain15Minute() const {
    return rain15Minute;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Rainfall
Loop2Packet::getRainHour() const {
    return rainHour;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Rainfall
Loop2Packet::getRain24Hour() const {
    return rain24Hour;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Temperature> &
Loop2Packet::getDewPoint() const {
    return dewPoint;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Temperature> &
Loop2Packet::getHeatIndex() const {
    return heatIndex;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Temperature> &
Loop2Packet::getWindChill() const {
    return windChill;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Temperature> &
Loop2Packet::getThsw() const {
    return thsw;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
const Measurement<Pressure> &
Loop2Packet::getAtmPressure() const {
    return atmPressure;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
Loop2Packet::decodeLoop2Packet(const byte buffer[]) {

    //
    // Perform packet validation before decoding the actual data
    //
    if (!VantagePro2CRC::checkCRC(buffer, 97)) {
        log.log(VP2Logger::VP2_ERROR) << "LOOP2 packet failed CRC check" << endl;
        return false;
    }

    if (buffer[0] != 'L' || buffer[1] != 'O' || buffer[2] != 'O') {
        log.log(VP2Logger::VP2_ERROR) << "LOOP2 buffer does not begin with LOO: "
                                      << "[0] = " << buffer[0] << " [1] = " << buffer[1] << " [2] = " << buffer[2] << endl;
        return false;
    }

    int packetType = BitConverter::toInt8(buffer, 4);
    if (packetType != LOOP2_PACKET_TYPE) {
        log.log(VP2Logger::VP2_ERROR) << "Invalid packet type for LOOP2 packet. "
                                      << "Expected " << LOOP2_PACKET_TYPE << " Received: " << packetType << endl;
        return false;
    }

    if (buffer[95] != VP2Constants::LINE_FEED || buffer[96] != VP2Constants::CARRIAGE_RETURN) {
        log.log(VP2Logger::VP2_ERROR) << "<LF><CR> not found" << endl;
        return false;
    }

    windSpeed10MinuteAvg = VP2Decoder::decodeAvgWindSpeed(buffer, 18);
    windSpeed2MinuteAvg = VP2Decoder::decodeAvgWindSpeed(buffer, 20);
    windGust10Minute = VP2Decoder::decode16BitWindSpeed(buffer, 22);
    windGustHeading10Minute = VP2Decoder::decodeWindDirection(buffer, 24);

    rain15Minute = VP2Decoder::decodeRain(buffer, 52);
    rainHour = VP2Decoder::decodeRain(buffer, 54);
    rain24Hour = VP2Decoder::decodeRain(buffer, 58);

    VP2Decoder::decodeNonScaled16BitTemperature(buffer, 30, dewPoint);
    VP2Decoder::decodeNonScaled16BitTemperature(buffer, 35, heatIndex);
    VP2Decoder::decodeNonScaled16BitTemperature(buffer, 37, windChill);
    VP2Decoder::decodeNonScaled16BitTemperature(buffer, 39, thsw);

    VP2Decoder::decodeBarometricPressure(buffer, 65, atmPressure);

    return true;
}
} // End namespace vp2
