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
    static constexpr int BYTES_PER_PACKET = 52;

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
    DateTime extractArchiveDate() const;

    static const DateTime EMPTY_ARCHIVE_PACKET_TIME = 0;
    static const int UNKNOWN_ET = 0;
    static const int UNKNOWN_SOLAR_RADIATION = 0;

    //static const int EXTRA_TEMPERATURE_OFFSET = 90;
    //static const Temperature TEMPERATURE_SCALE;
    //static const Pressure BAROMETER_SCALE;
    //static const UvIndex UV_INDEX_SCALE;
    //static const Evapotranspiration ET_SCALE;
    //static const Heading DEGREES_PER_SLICE;
    //
    // Archive packet (Rev B)
    //
    static constexpr int REV_A_RECORD_TYPE = 0xFF;
    static constexpr int REV_B_RECORD_TYPE = 0;
    static constexpr int DATE_STAMP_OFFSET = 0;
    static constexpr int TIME_STAMP_OFFSET = 2;
    static constexpr int OUTSIDE_TEMPERATURE_OFFSET = 4;
    static constexpr int HIGH_OUTSIDE_TEMPERATURE_OFFSET = 6;
    static constexpr int LOW_OUTSIDE_TEMPERATURE_OFFSET = 8;
    static constexpr int RAINFALL_OFFSET = 10;
    static constexpr int HIGH_RAIN_RATE_OFFSET = 12;
    static constexpr int BAROMETER_OFFSET = 14;
    static constexpr int SOLAR_RADIATION_OFFSET = 16;
    static constexpr int NUM_WIND_SAMPLES_OFFSET = 18;
    static constexpr int INSIDE_TEMPERATURE_OFFSET = 20;
    static constexpr int INSIDE_HUMIDITY_OFFSET = 22;
    static constexpr int OUTSIDE_HUMIDITY_OFFSET = 23;
    static constexpr int AVG_WIND_SPEED_OFFSET = 24;
    static constexpr int HIGH_WIND_SPEED_OFFSET = 25;
    static constexpr int DIR_OF_HIGH_WIND_SPEED_OFFSET = 26;
    static constexpr int PREVAILING_WIND_DIRECTION_OFFSET = 27;
    static constexpr int AVG_UV_INDEX_OFFSET = 28;
    static constexpr int ET_OFFSET = 29;
    static constexpr int HIGH_SOLAR_RADIATION_OFFSET = 30;
    static constexpr int HIGH_UV_INDEX_OFFSET = 32;
    static constexpr int FORECAST_RULE_OFFSET = 33;
    static constexpr int LEAF_TEMPERATURE_BASE_OFFSET = 34;
    static constexpr int LEAF_WETNESS_BASE_OFFSET = 36;
    static constexpr int SOIL_TEMPERATURE_BASE_OFFSET = 38;
    static constexpr int RECORD_TYPE_OFFSET = 42;
    static constexpr int EXTRA_HUMIDITIES_BASE_OFFSET = 43;
    static constexpr int EXTRA_TEMPERATURES_BASE_OFFSET = 45;
    static constexpr int SOIL_MOISTURES_BASE_OFFSET = 48;

    //static const int EXTRA_TEMPERATURE_OFFSET = 90;

/*
    static const int INVALID_TEMPERATURE = 32767;
    static const int INVALID_HIGH_TEMPERATURE = -32768;
    static const int INVALID_EXTRA_TEMPERATURE = 255;
    static const int INVALID_HUMIDITY = 255;
    static const int INVALID_WIND_DIRECTION = 255;
    static const int INVALID_WIND_SPEED = 255;
    static const int INVALID_UV_INDEX = 255;
    static const int INVALID_LEAF_WETNESS = 255;
    static const int INVALID_LEAF_TEMPERATURE = 255;
    static const int INVALID_SOIL_TEMPERATURE = 255;
    static const int INVALID_SOIL_MOISTURE = 255;
    static const int INVALID_BAROMETER = 0;
    static const int INVALID_SOLAR_RADIATION = 32767;
*/

    static const int MAX_EXTRA_TEMPERATURES = 3;
    static const int MAX_EXTRA_HUMIDITIES = 2;

    // The serial protocol document says this is 4, but the 4th value is not set to the Dash value when there
    // are not soil temperature sensors.
/* All things dealing with leaf wetness/temperature and soil wetness/temperature are being disabled due to lack
 * of clarity in the documentation.
    static const int APB_MAX_SOIL_TEMPERATURES = 3;
    static const int APB_MAX_SOIL_MOISTURES = 4;
    static const int APB_MAX_LEAF_WETNESSES = 2;
    static const int APB_MAX_LEAF_TEMPERATURES = 2;

    static const int MIN_LEAF_WETNESS = 0;
    static const int MAX_LEAF_WETNESS = 15;
*/

    //static const int ET_SCALE = 1000;


    DateTime packetTime;
    int windSampleCount;
    int archivePeriod;
    Rainfall rainInterval;
    Speed windGust;
    Heading windGustDirection;
    byte buffer[BYTES_PER_PACKET];
    VP2Logger * log;
};

}

#endif	/* ARCHIVEPACKET_H */
