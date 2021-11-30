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
#ifndef SENSOR_STATION_H
#define SENSOR_STATION_H

#include <iostream>
#include <vector>
#include <string>
#include "Weather.h"

namespace vp2 {
/**
 * Class that represents a sensor station that reports data to the console.
 */
class SensorStation {
public:
    /**
     * Sensor station types supported by the VP2, the enum integer values are from the VP2 serial protocol.
     */
    enum SensorStationType {
        INTEGRATED_SENSOR_STATION = 0,
        TEMPERATURE_ONLY = 1,
        HUMIDITY_ONLY = 2,
        TEMPERATURE_HUMIDITY = 3,
        ANEMOMETER = 4,
        RAIN = 5,
        LEAF = 6,
        SOIL = 7,
        SOIL_LEAF = 8,
        NO_STATION = 10
    };

    /**
     * Constructor.
     * 
     * @param type The type of sensor station
     * @param sensorIndex The index of the station as reported by the console, these are usually determined by
     *                    DIP switches within the hardware.
     */
    SensorStation(SensorStationType type, int sensorIndex);

    /**
     * Destructor.
     */
    virtual ~SensorStation() {}

    /**
     * Get the sensor station type.
     * 
     * @return The sensor station type
     */
    SensorStationType getSensorStationType() const;

    /**
     * Get the sensor station index.
     * 
     * @return The index of the sensor station
     */
    int getSensorIndex() const;

    /**
     * Get the battery status of the sensor station. These stations are typically wireless and the battery will
     * need to be replaced at varying intervals. It has been my observation that by the time the station reports a
     * bad battery, it will need to be replaced in less than a day.
     * 
     * @return True if the battery is good
     */
    bool getBatteryStatus() const;

    /**
     * Set the battery status.
     * 
     * @param value True if the battery is good
     */
    void setBatteryStatus(bool value);

    /**
     * Get the link quality of this station. Not all sensor stations are monitored for link quality. The VP2 only
     * monitors the link quality of the Integrated Sensor Suite station (ISS).
     * 
     * @return The quality of the link between 0 and 100
     */
    int getLinkQuality() const;

    /**
     * Set the link quality for this sensor station.
     * 
     * @param value The link quality between 0 and 100
     */
    void setLinkQuality(int value);

    /**
     * Build a message to send to the collector that reports which sensor stations are connected (wired or wireless) to the console.
     * 
     * @param list The list of sensor stations
     * 
     * @return The message
     */
    static std::string formatSensorStationMessage(const std::vector<SensorStation> & list);

    /**
     * Build a message to send to the collector that reports the status of the sensor stations.
     * 
     * @param list The list of sensor stations
     * @param time The time that the data was collected
     * @return The message
     */
    static std::string formatSensorStationStatusMessage(const std::vector<SensorStation> & list, DateTime time);

    /**
     * ostream operator.
     *
     * @param os The output stream
     * @param ss The sensor station for which the text will be generated
     * @return The output stream passed in
     */
    friend std::ostream & operator<<(std::ostream & os, const SensorStation & ss);

private:
    SensorStationType type;
    int               sensorIndex;
    bool              batteryStatus;
    int               linkQuality;

};
}

#endif /* SENSOR_STATION_H */
