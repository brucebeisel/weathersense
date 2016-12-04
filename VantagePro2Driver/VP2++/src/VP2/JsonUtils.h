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

/* 
 * File:   JsonUtils.h
 * Author: bruce
 *
 * Created on December 3, 2016, 7:57 PM
 */

#ifndef JSONUTILS_H
#define JSONUTILS_H

namespace vp2 {

}
class JsonUtils {
public:
    /**
     * Format the entry for a single sensor value
     * 
     * @param ss The stream in which to write the string
     * @param key The key of the sensor
     * @param sensorType The type of sensor
     * @param measurement The measurement from this sensor
     * @return The ostream to allow the calls to be chained
     */
    static std::ostream & formatSensorMeasurement(std::ostream & os, bool addComma, int key, const std::string & sensorType, double measurement) const;
private:
    JsonUtils();

};
}
#endif /* JSONUTILS_H */

