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
#ifndef WEATHER_H
#define WEATHER_H

#include <time.h>
#include <string>

namespace vp2 {
//
// Typedefs for the various weather types
//
typedef double Speed;
typedef double Rainfall;
typedef double Temperature;
typedef double Pressure;
typedef double Humidity;
typedef double SolarRadiation;
typedef int    UvIndex;
typedef int    LeafWetness;
typedef int    SoilMoisture;
typedef float  Heading;
typedef char   byte;
typedef time_t DateTime;
typedef double Evapotranspiration;

/**
 * Utility container class for various functions.
 */
class Weather {
public:
    /**
     * Format the given date/time into the format used by the WeatherSense collector.
     * 
     * @param time The date/time to format
     * @return The formatted date/time
     */
    static std::string formatDateTime(DateTime time);

    /**
     * Format the given date into the format used by the WeatherSense collector.
     * 
     * @param time The date to format
     * @return The formatted date
     */
    static std::string formatDate(DateTime time);

    /**
     * Dump a buffer as hex strings to be used in debug and logs.
     * 
     * @param buffer The buffer to dump
     * @param nbytes The number of bytes to dump
     * @return The formatted string
     */
    static std::string dumpBuffer(const byte buffer[], int nbytes);

    /**
     * Utility function to encapsulate the sleep function between Windows and LINUX
     * 
     * @param millis The number of milliseconds to sleep
     */
    static void sleep(long millis);

    /**
     * Wrapper function for the localtime_r (LINUX), localtime_s (Windows)
     * 
     * @param t The time in UNIX time
     * @param The time structure that will be filled in
     */
    static void localtime(time_t t, struct tm & tm);
};
}

#endif