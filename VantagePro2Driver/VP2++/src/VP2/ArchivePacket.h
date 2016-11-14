/* 
 * Copyright (C) 2016 Bruce Beisel
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
#ifndef ARCHIVEPACKET_H
#define	ARCHIVEPACKET_H
#include <string>
#include "Weather.h"
#include "VP2Constants.h"

namespace vp2 {

class VP2Logger;

/**
 * A class that represents a Vantage Pro 2 archive packet. Very little data is needed outside the construction of the archive
 * message that is sent to the collector.
 */
class ArchivePacket {
public:
    /**
     * Constructor.
     * 
     * @param buffer The buffer containing the raw archive packet
     * @param offset The offset into the buffer that the archive packet begins
     * @param rainInterval The amount of rainfall each tip of the rain bucket is equivalent to
     * @param archivePeriod The duration over which this archive packet was collected
     * @param windGust The speed of the wind gust as reported by the weather station
     * @param windGustDirection The direction of the wind gust
     */
    ArchivePacket(const byte buffer[], int offset, Rainfall rainInterval, int archivePeriod, Speed windGust, Heading windGustDirection);

    /**
     * Destructor.
     */
    virtual ~ArchivePacket();

    /**
     * Get the raw packet data used to extract the archive data.
     * 
     * @return The raw packet
     */
    const byte * getBuffer() const;
    
    /**
     * The number of wind samples that were collector by the weather station over the archive period.
     * 
     * @return The wind sample count from the archive packet
     */
    int getWindSampleCount() const;
    
    /**
     * Get the date/time that was extracted from the packet.
     * 
     * @return The date.time
     */
    DateTime getDateTime() const;

    /**
     * Check whether this is an empty packet, the empty packet concept is used to avoid the use of NULL.
     * 
     * @return True if it is an empty packet
     */
    bool isEmptyPacket() const;
    
    /**
     * Format the WeatherSense message for an archive packet.
     * 
     * @return The formatted messages
     */
    std::string formatMessage() const;

private:
    static const DateTime EMPTY_ARCHIVE_PACKET_TIME = 0;
    static const int UNKNOWN_ET = 0;
    static const int UNKNOWN_SOLAR_RADIATION = 0;
    static const int EXTRA_TEMPERATURE_OFFSET = 90;
    static const Temperature TEMPERATURE_SCALE;
    static const Pressure BAROMETER_SCALE;
    static const UvIndex UV_INDEX_SCALE;
    static const Evapotranspiration ET_SCALE;
    static const Heading DEGREES_PER_SLICE;

    DateTime extractDate() const;

    DateTime packetTime;
    int windSampleCount;
    int archivePeriod;
    Rainfall rainInterval;
    Speed windGust;
    Heading windGustDirection;
    byte buffer[VP2Constants::APB_BYTES_PER_RECORD];
    VP2Logger * log;
};

}

#endif	/* ARCHIVEPACKET_H */