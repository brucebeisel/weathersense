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
#ifndef WEATHER_H
#define WEATHER_H

#include <time.h>
#include <string>

namespace vp2 {
//
// Typedefs for the various weather types
//
using Speed = double;
using Rainfall = double;
using Temperature = double;
using Pressure = double;
using Humidity = double;
using SolarRadiation = double;
using UvIndex = float;
using LeafWetness = int;
using SoilMoisture = int;
using Heading = float;
using byte = char;
using DateTime = time_t;
using Evapotranspiration = double;
using StationId = unsigned;

const int SECONDS_PER_HOUR = 3600;
const int TIME_STRUCT_YEAR_OFFSET = 1900;

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

    /**
     * Extract a DateTime values from a console time value.
     *
     * @param time The time as reported by the console in HHMM integer format
     * @return The converted time
     */
    static DateTime extractDate(int time);
};
}

#endif
