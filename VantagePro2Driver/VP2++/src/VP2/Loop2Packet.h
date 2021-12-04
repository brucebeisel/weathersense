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
#ifndef LOOP2_PACKET_H
#define LOOP2_PACKET_H

#include "VP2Logger.h"
#include "Weather.h"

namespace vp2 {

/**
 * Class that decodes and holds the data from the VP2 LOOP 2 packet.
 */
class Loop2Packet {
public:
    /**
     * Constructor.
     */
    Loop2Packet();

    /**
     * Destructor.
     */
    virtual ~Loop2Packet();

    /**
     * Parse the LOOP 2 packet.
     * 
     * @param The buffer from which to decode the packet
     * @return True if the buffer was decoded successfully
     */
    bool decodeLoop2Packet(const byte[]);

    const Measurement<Speed> &       getWindGust10Minute() const;
    const Measurement<Heading> &     getWindGustHeading10Minute() const;
    const Measurement<Speed> &       getWindSpeed2MinuteAvg() const;
    const Measurement<Speed> &       getWindSpeed10MinuteAvg() const;
    Rainfall                         getRainHour() const;
    Rainfall                         getRain15Minute() const;
    Rainfall                         getRain24Hour() const;
    const Measurement<Temperature> & getDewPoint() const;
    const Measurement<Temperature> & getHeatIndex() const;
    const Measurement<Temperature> & getWindChill() const;
    const Measurement<Temperature> & getThsw() const;
    const Measurement<Pressure> &    getAtmPressure() const;

private:
    static const int LOOP2_PACKET_TYPE = 1;

    Measurement<Speed>       windGust10Minute;
    Measurement<Heading>     windGustHeading10Minute;
    Measurement<Speed>       windSpeed2MinuteAvg;
    Measurement<Speed>       windSpeed10MinuteAvg;
    Rainfall                 rain15Minute;
    Rainfall                 rainHour;
    Rainfall                 rain24Hour;
    Measurement<Temperature> dewPoint;
    Measurement<Temperature> heatIndex;
    Measurement<Temperature> windChill;
    Measurement<Temperature> thsw;
    Measurement<Pressure>    atmPressure;
    VP2Logger   log;
};
}
#endif
