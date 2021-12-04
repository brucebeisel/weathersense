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
#include <sstream>
#include "Sensor.h"

using namespace std;

namespace vp2 {

static const char *SENSOR_NAMES[] = {
    "THERMOMETER",
    "ULTRAVIOLET",
    "ANEMOMETER",
    "LEAF_WETNESS",
    "LEAF_TEMPERATURE",
    "SOLAR_RADIATION",
    "SOIL_MOISTURE",
    "SOIL_TEMPERATURE",
    "HYGROMETER",
    "RAIN_COLLECTOR",
    "BAROMETER"
};

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
Sensor::Sensor(SensorType type, int id) {
    sensorType = type;
    sensorId = id;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
Sensor::detectSensors(LoopPacket loop, vector<Sensor> & sensors) {
    sensors.push_back(Sensor(THERMOMETER, OUTDOOR_THERMOMETER_SENSOR_ID));
    sensors.push_back(Sensor(THERMOMETER, INDOOR_THERMOMETER_SENSOR_ID));
    sensors.push_back(Sensor(HYGROMETER, OUTDOOR_HYGROMETER_SENSOR_ID));
    sensors.push_back(Sensor(HYGROMETER, INDOOR_HYGROMETER_SENSOR_ID));
    sensors.push_back(Sensor(BAROMETER, BAROMETER_SENSOR_ID));
    sensors.push_back(Sensor(ANEMOMETER, WIND_SENSOR_ID));
    sensors.push_back(Sensor(RAIN_COLLECTOR, RAIN_COLLECTOR_SENSOR_ID));

    if (loop.getUvIndex().isValid())
        sensors.push_back(Sensor(ULTRAVIOLET, UV_SENSOR_ID));

    if (loop.getSolarRadiation().isValid())
        sensors.push_back(Sensor(SOLAR_RADIATION, SOLAR_RADIATION_SENSOR_ID));

    for (int i = 0; i < VP2Constants::MAX_EXTRA_HUMIDITIES; i++) {
        if (loop.getExtraHumidity(i).isValid())
            sensors.push_back(Sensor(HYGROMETER, HYGROMETER_BASE_SENSOR_ID + i));
    }

    for (int i = 0; i < VP2Constants::MAX_EXTRA_TEMPERATURES; i++) {
        if (loop.getExtraTemperature(i).isValid())
            sensors.push_back(Sensor(THERMOMETER, THERMOMETER_BASE_SENSOR_ID + i));
    }

    for (int i = 0; i < VP2Constants::MAX_LEAF_WETNESSES; i++) {
        if (loop.getLeafWetness(i).isValid())
            sensors.push_back(Sensor(LEAF_WETNESS, LEAF_WETNESS_BASE_SENSOR_ID + i));
    }

    for (int i = 0; i < VP2Constants::MAX_SOIL_MOISTURES; i++) {
        if (loop.getSoilMoisture(i).isValid())
            sensors.push_back(Sensor(SOIL_MOISTURE, SOIL_MOISTURE_BASE_SENSOR_ID + i));
    }

    for (int i = 0; i < VP2Constants::MAX_SOIL_TEMPERATURES; i++) {
        if (loop.getSoilTemperature(i).isValid())
            sensors.push_back(Sensor(SOIL_TEMPERATURE, SOIL_TEMPERATURE_BASE_SENSOR_ID + i));
    }

    for (int i = 0; i < VP2Constants::MAX_LEAF_TEMPERATURES; i++) {
        if (loop.getLeafTemperature(i).isValid())
            sensors.push_back(Sensor(LEAF_TEMPERATURE, LEAF_TEMPERATURE_BASE_SENSOR_ID + i));
    }
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
string
Sensor::formatMessage(const vector<Sensor> & list) {
    ostringstream sb;
    sb << "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
    sb << "<sensorMessage>";
    for (vector<Sensor>::const_iterator it = list.begin() ; it != list.end(); ++it) {
        sb << "<sensor><type>" << SENSOR_NAMES[it->sensorType] << "</type><sensorId>" << it->sensorId << "</sensorId></sensor>";
    }
    sb << "</sensorMessage>";

    return sb.str();
}
}
