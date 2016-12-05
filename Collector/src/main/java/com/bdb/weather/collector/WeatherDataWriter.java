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
package com.bdb.weather.collector;

import java.time.LocalDate;
import java.util.List;

import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.DopplerRadarImage;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.Sensor;
import com.bdb.weather.common.SensorStation;
import com.bdb.weather.common.SensorStationStatus;
import com.bdb.weather.common.messages.WsParametersMessage;

/**
 * Interface for the Database writer.
 * 
 * @author Bruce
 */
interface WeatherDataWriter {
    /**
     * Add a historical record to the database.
     * 
     * @param records A list of historical records to add
     * @throws CollectException An error occurred
     */
    public void addHistoricalRecords(List<HistoricalRecord> records) throws CollectException;

    /**
     * Update the list of sensors in the database, this will replace any existing sensors.
     * 
     * @param sensorList The sensor list
     */
    public void updateSensorList(List<Sensor> sensorList);

    /**
     * Update the list of sensor stations in the database. A sensor station is a station to which stations are attached.
     * 
     * @param sensorStationList The list of sensor stations
     */
    public void updateSensorStationList(List<SensorStation> sensorStationList);

    /**
     * Update the status of the sensor stations.
     * 
     * @param sensorStationStatusList The list of sensor station statuses
     */
    public void updateSensorStationStatus(List<SensorStationStatus> sensorStationStatusList);

    /**
     * Set the current weather.
     * 
     * @param weather The POJO representation of the current weather
     * @param json The JSON representation of the current weather
     */
    public void setCurrentWeather(CurrentWeather weather, String json);

    /**
     * Request that the writer connect to the database.
     */
    public void requestDbConnection();

    /**
     * Add a Doppler radar image to the database.
     * 
     * @param dri The Doppler Radar image
     */
    public void addDopplerRadarImage(DopplerRadarImage dri);

    /**
     * Set the weather station parameters. The parameters are weather station dependent. It is up to the console driver
     * developer to determine which parameters are worthy of storage.
     * 
     * @param parameters The list of parameters
     */
    public void setWeatherStationParameters(WsParametersMessage parameters);

    /**
     * Summarize the specified date and store the results in the database.
     * 
     * @param day The day to summarize
     */
    public void summarizeDay(LocalDate day);
}
