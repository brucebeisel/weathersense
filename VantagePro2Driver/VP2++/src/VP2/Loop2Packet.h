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
#ifndef LOOP2_PACKET_H
#define LOOP2_PACKET_H

#include "VP2Logger.h"
#include "Weather.h"

namespace vp2 {

/**
 * Class that parses and holds the data from the VP2 LOOP 2 packet.
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
     * @param The buffer from which to parse the packet
     * @return True if the buffer was parsed successfully
     */
    bool parseLoop2Packet(const byte[]);

    Speed getWindGust10Minute() const;
    Heading getWindGustHeading10Minute() const;
    Speed getWindSpeed2MinuteAvg() const;
    Rainfall getRainHour() const;
    Rainfall getRain15Minute() const;
    Rainfall getRain24Hour() const;
    Temperature getDewPoint() const;
    Temperature getHeatIndex() const;
    Temperature getWindChill() const;
    bool isThswValid() const;
    Temperature getThsw() const;
    Pressure getAtmPressure() const;

    /**
     * Set the rainfall increment that will be used to translate the rain bucket tip count to an actual amount of rain.
     * 
     * @param increment The value that will be used to convert to actual rainfall amounts
     */
    static void setRainfallIncrement(Rainfall increment);

private:
    Speed windGust10Minute;
    Heading windGustHeading10Minute;
    Speed windSpeed2MinuteAvg;
    Rainfall rain15Minute;
    Rainfall rainHour;
    Rainfall rain24Hour;
    Temperature dewPoint;
    Temperature heatIndex;
    Temperature windChill;
    Temperature thsw;
    bool thswValid;
    Pressure atmPressure;
    VP2Logger log;
    static Rainfall rainfallIncrement;
};
}
#endif