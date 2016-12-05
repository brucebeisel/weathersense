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
package com.bdb.weather.collector.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bdb.weather.common.Sensor;

/**
 * The message that contains a list of sensors.
 * 
 * @author Bruce
 */
public class SensorMessage {
    private final List<Sensor> sensorList;
    
    /**
     * Constructor.
     * 
     * @param sensors The list of sensors
     */
    public SensorMessage(List<Sensor> sensors) {
        sensorList = new ArrayList<>();
        sensorList.addAll(sensors);
    }
    
    /**
     * The no argument constructor required by JAXB.
     */
    private SensorMessage() {
        sensorList = new ArrayList<>();
    }
    
    /**
     * Return the sensor list.
     * 
     * @return The list of sensors
     */
    public List<Sensor> getSensorList() {
        return Collections.unmodifiableList(sensorList);
    }
    
    /**
     * Set the list of sensors.
     * 
     * @param sensors The list of sensors to set
     */
    public void setSensorList(List<Sensor> sensors) {
        sensorList.clear();
        sensorList.addAll(sensors);
    }
}
