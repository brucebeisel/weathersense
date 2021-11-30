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
#ifndef SENSOR_H
#define SENSOR_H

#include <vector>
#include "LoopPacket.h"

namespace vp2 {

class Sensor {
public:
    /**
     * The types of sensors supported by the VP2
     */
    enum SensorType {
        THERMOMETER,
        ULTRAVIOLET,
        ANEMOMETER,
        LEAF_WETNESS,
        LEAF_TEMPERATURE,
        SOLAR_RADIATION, 
        SOIL_MOISTURE,
        SOIL_TEMPERATURE,
        HYGROMETER,
        RAIN_COLLECTOR,
        BAROMETER
    };

    /**
     * Constructor.
     * 
     * @param type The type of this sensor
     * @param id The unique ID of this sensor
     */
    Sensor(SensorType type, int id);

    /**
     * Destructor.
     */
    virtual ~Sensor() {};

    /**
     * Detect the sensors that are installed on the VP2 by inspecting the values in the LOOP packet.
     * 
     * @param loop The LOOP packet to inspect
     * @param sensors The list of detected sensors
     */
    static void detectSensors(LoopPacket loop, std::vector<Sensor> & sensors);

    /**
     * Format the sensor message to be sent to the WeatherSense collector.
     * 
     * @param list The list of sensors to be sent in the message
     * @return The formatted message
     */
    static std::string formatMessage(const std::vector<Sensor> & list);

private:
    static const int LAST_DEFAULT_SENSOR_ID = 99;
    //
    // The VP2 can have a number of additional sensors that are reported by additional sensor stations.
    // These are the base IDs for each sensor type.
    // The actual sensor ID is the base value + the location in the LOOP packet array for the sensor value type.
    //
    static const int THERMOMETER_BASE_SENSOR_ID = 100;
    static const int HYGROMETER_BASE_SENSOR_ID = 200;
    static const int LEAF_WETNESS_BASE_SENSOR_ID = 400;
    static const int LEAF_TEMPERATURE_BASE_SENSOR_ID = 500;
    static const int SOIL_MOISTURE_BASE_SENSOR_ID = 600;
    static const int SOIL_TEMPERATURE_BASE_SENSOR_ID = 700;
    //
    // Hard-coded sensor IDs. These are coupled with the implementation of the collector.
    //
    static const int BAROMETER_SENSOR_ID = 1;
    static const int WIND_SENSOR_ID = 2;
    static const int UV_SENSOR_ID = 3;
    static const int SOLAR_RADIATION_SENSOR_ID = 4;
    static const int RAIN_COLLECTOR_SENSOR_ID = 5;
    static const int INDOOR_THERMOMETER_SENSOR_ID = 6;
    static const int OUTDOOR_THERMOMETER_SENSOR_ID = 7;
    static const int INDOOR_HYGROMETER_SENSOR_ID = 8;
    static const int OUTDOOR_HYGROMETER_SENSOR_ID = 9;
    SensorType sensorType;
    int sensorId;
};
}
#endif /* SENSOR_H */
