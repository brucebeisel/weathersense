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
#ifndef VP2_CONSTANTS_H
#define VP2_CONSTANTS_H
#include <string>
#include "Weather.h"

namespace vp2 {
/**
 * This class contains all of the constants from the
 * Vantage Pro/Pro 2/Vue Serial Communications Reference Manual
 * Version 2.6.1
 * Date: 3/29/2013
 */
namespace VP2Constants {
    //
    // Various values used generically in various command protocols
    //
    static constexpr char NULLCHAR = '\0';
    static constexpr char LINE_FEED = '\n';
    static constexpr char CARRIAGE_RETURN = '\r';
    static constexpr char ACK = 0x6;
    static constexpr char NACK = 0x21; // Not an ASCII NACK, but it is what is used
    static constexpr char CANCEL = 0x18;
    static constexpr char ESCAPE = 0x15;

    //
    // Cumulative Values that can be cleared using CLRVAR
    //
    enum CumulativeValue {
        DAILY_RAIN_CUM = 13,
        STORM_RAIN_CUM = 14,
        MONTH_RAIN_CUM = 16,
        YEAR_RAIN_CUM = 17,
        DAY_ET_CUM = 26,
        MONTH_ET_CUM = 25,
        YEAR_ET_CUM = 27
    };

    //
    // High/Low Clear Types
    //
    enum ExtremePeriod {
        DAILY_EXTREMES = 0,
        MONTHLY_EXTREMES = 1,
        YEARLY_EXTREMES = 2
    };

    enum ArchivePeriod {
        ONE_MINUTE = 1,
        FIVE_MINUTES = 5,
        TEN_MINUTES = 10,
        FIFTEEN_MINUTES = 15,
        THIRTY_MINUTES = 30,
        ONE_HOUR = 60,
        TWO_HOURS = 120,
    };

    enum class BaudRate : int {
        BAUD_1200 = 1200,
        BAUD_2400 = 2400,
        BAUD_4800 = 4800,
        BAUD_9600 = 9600,
        BAUD_14400 = 14400,
        BAUD_19200 = 19200
    };

    enum class RainCupSizeType : int {
        POINT_01_INCH = 0,
        POINT_02_MM = 1,
        POINT_01_MM = 2
    };

    static constexpr Rainfall POINT_01_INCH_SIZE = 0.01;       // Inches
    static constexpr Rainfall POINT_02_MM_SIZE   = 0.2 / 25.4; // Inches
    static constexpr Rainfall POINT_01_MM_SIZE   = 0.1 / 25.4; // Inches

    //
    // LOOP packet forecast icons
    //
    static const short RAIN_BIT = 0x1;
    static const short CLOUDY_BIT = 0x2;
    static const short PARTLY_CLOUDY_BIT = 0x4;
    static const short SUNNY_BIT = 0x8;
    static const short SNOW_BIT = 0x10;

    static const int MOSTLY_CLEAR_FORECAST = SUNNY_BIT;
    static const int PARTLY_CLOUDY_FORECAST = PARTLY_CLOUDY_BIT | CLOUDY_BIT;
    static const int MOSTLY_CLOUDY_FORECAST = CLOUDY_BIT;
    static const int MOSTLY_CLOUDY_CHANCE_OF_RAIN_FORECAST = CLOUDY_BIT | RAIN_BIT;
    static const int MOSTLY_CLOUDY_CHANCE_OF_SNOW_FORECAST = CLOUDY_BIT | SNOW_BIT;
    static const int PARTLY_CLOUDY_CHANCE_OF_RAIN_FORECAST = PARTLY_CLOUDY_BIT | RAIN_BIT;
    static const int PARTLY_CLOUDY_CHANCE_OF_SNOW_FORECAST = PARTLY_CLOUDY_BIT | SNOW_BIT;
    static const int PARTLY_CLOUDY_CHANCE_OF_RAIN_OR_SNOW_FORECAST = PARTLY_CLOUDY_BIT | RAIN_BIT | SNOW_BIT;

    //
    // EEPROM Addresses
    //
    static const unsigned EE_LATITUDE = 0x0B;
    static const unsigned EE_LONGITUDE = 0x0D;
    static const unsigned EE_ELEVATION = 0x0F;
    static const unsigned EE_TIMEZONE = 0x11;
    static const unsigned EE_STATION_LIST = 0x19;
    static const unsigned EE_SETUP_BITS = 0x2B;
    static const unsigned EE_RAIN_SEASON_START = 0x2C;
    static const unsigned EE_ARCHIVE_PERIOD = 0x2D;
    static const unsigned EE_ALARM_THRESHOLDS = 0x52;

    static const int NUM_ARCHIVE_RECORDS = 2560;

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
// Everything below has been vetted. Everything above might be moved

    //
    // Maximum counts
    //
    static const int MAX_SENSOR_STATIONS = 16;
    static const int MAX_EXTRA_TEMPERATURES = 7;
    static const int MAX_EXTRA_HUMIDITIES = 7;
    static const int MAX_SOIL_TEMPERATURES = 4;
    static const int MAX_SOIL_MOISTURES = 4;
    static const int MAX_LEAF_TEMPERATURES = 4;
    static const int MAX_LEAF_WETNESSES = 4;

    //
    // Common constants
    //
    static constexpr Temperature        TEMPERATURE_16BIT_SCALE = 10.0;
    static constexpr int                TEMPERATURE_16BIT_INVALID_VALUE = 32767;
    static constexpr Temperature        TEMPERATURE_8BIT_OFFSET = 90.0;
    static constexpr int                TEMPERATURE_8BIT_INVALID_VALUE = 255;
    static constexpr Pressure           BAROMETER_SCALE = 1000.0;
    static constexpr Speed              AVG_WIND_SPEED_SCALE = 10.0;
    static constexpr int                YEAR_OFFSET = 2000;
    static constexpr UvIndex            UV_INDEX_SCALE= 10.0;
    static constexpr Evapotranspiration DAY_ET_SCALE= 1000.0;
    static constexpr Evapotranspiration MONTH_YEAR_ET_SCALE= 100.0;
    static constexpr Rainfall           STORM_RAIN_SCALE= 100.0;

    static const int NORTH_HEADING_VALUE = 360;

    static constexpr int INVALID_16BIT_TEMPERATURE = 32767;
    static constexpr int INVALID_16BIT_HIGH_TEMPERATURE = -32768;
    static constexpr int INVALID_8BIT_TEMPERATURE = 255;
    static constexpr int INVALID_HUMIDITY = 255;
    static constexpr int INVALID_WIND_DIRECTION_SLICE = 255;
    static constexpr int INVALID_WIND_DIRECTION = 0;
    static constexpr int INVALID_WIND_SPEED = 255;
    static constexpr int INVALID_UV_INDEX = 255;
    static constexpr int INVALID_LEAF_WETNESS = 255;
    static constexpr int INVALID_LEAF_TEMPERATURE = 255;
    static constexpr int INVALID_SOIL_TEMPERATURE = 255;
    static constexpr int INVALID_SOIL_MOISTURE = 255;
    static constexpr int INVALID_BAROMETER = 0;
    static constexpr int INVALID_SOLAR_RADIATION = 32767;
    static constexpr int INVALID_THSW = 32767;
    static constexpr int INVALID_ET = 0;
    static constexpr int NO_STORM_ACTIVE_DATE = -1;
    static constexpr int MIN_LEAF_WETNESS = 0;
    static constexpr int MAX_LEAF_WETNESS = 15;

    //
    // A wind slice is a segment of wind direction that is centered on a compass direction such
    // as N, NNE, NE, etc.
    //
    static constexpr int NUM_WIND_DIR_SLICES = 16;
    static constexpr double DEGREES_PER_SLICE = 360.0 / NUM_WIND_DIR_SLICES;
};
}

#endif /* VP2_CONSTANTS_H */
