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
package com.bdb.weather.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Bruce
 */
public class SensorManager {
    private final Map<Integer,Sensor> sensors;
    private static final SensorManager instance;

    static {
        instance = new SensorManager();
    }

    /**
     *
     * @return
     */
    public static SensorManager getInstance() {
        return instance;
    }

    private SensorManager() {
        sensors = new TreeMap<>();
    }

    /**
     *
     * @param sensor
     */
    public void addSensor(Sensor sensor) {
        sensors.put(sensor.getSensorId(), sensor);
    }

    /**
     *
     * @return
     */
    public Collection<Sensor> getAllSensors() {
        return Collections.unmodifiableCollection(sensors.values());
    }

    /**
     *
     * @param type
     * @param includeStandard
     * @param includeExtra
     * @return
     */
    public List<Sensor> getSensors(SensorType type, boolean includeStandard, boolean includeExtra) {
        List<Sensor> list = new ArrayList<>();
        for (Sensor sensor : sensors.values()) {
            if (sensor.getType() != type)
                continue;

            if (includeStandard && sensor.getSensorId() > Sensor.LAST_STANDARD_SENSOR_ID)
                continue;

            if (includeExtra && sensor.getSensorId() <= Sensor.LAST_STANDARD_SENSOR_ID)
                continue;
            
            list.add(sensor);
        }

        return list;
    }

    /**
     *
     * @param type
     * @return
     */
    public List<Sensor> getExtraSensors(SensorType type) {
        return getSensors(type, false, true);
    }

    /**
     *
     * @param type
     * @return
     */
    public List<Sensor> getStandardSensors(SensorType type) {
        return getSensors(type, true, false);
    }

    /**
     *
     * @param sensorId
     * @return
     */
    public boolean sensorInstalled(int sensorId) {
        return sensors.get(sensorId) != null;
    }

    /**
     *
     * @param sensorId
     * @return
     */
    public Sensor getSensor(int sensorId) {
        return sensors.get(sensorId);
    }
}