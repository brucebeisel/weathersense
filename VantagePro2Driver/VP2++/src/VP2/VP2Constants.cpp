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
#include "VP2Constants.h"

namespace vp2 {

    //
    // EEPROM addresses
    //
    const std::string VP2Constants::EE_LATITUDE = "0B";
    const std::string VP2Constants::EE_LONGITUDE = "0D";
    const std::string VP2Constants::EE_ELEVATION = "0F";
    const std::string VP2Constants::EE_TIMEZONE = "11";
    const std::string VP2Constants::EE_STATION_LIST = "19";
    const std::string VP2Constants::EE_SETUP_BITS = "2B";
    const std::string VP2Constants::EE_RAIN_SEASON_START = "2C";
    const std::string VP2Constants::EE_ARCHIVE_PERIOD = "2D";

}
