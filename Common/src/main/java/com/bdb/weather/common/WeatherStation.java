/* 
 * Copyright (C) 2015 Bruce Beisel
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

import com.bdb.weather.common.astronomical.SolarEventCalculator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import com.bdb.weather.common.measurement.AngularMeasurement;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Distance;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.Temperature;

/**
 * Class the represents a weather station. A weather station is a collection of sensors that are
 * connected to sensor stations.
 *
 * @author bruce
 */
public final class WeatherStation {
    private String               manufacturer;
    private String               model;
    private String               locationCode;
    private String               locationDescription;
    private String               firmwareDate;
    private String               firmwareVersion;
    private GeographicLocation   geographicLocation;
    private Month                weatherYearStartMonth;
    private WindParameters       windParameters;
    private Temperature          thermometerMin;
    private Temperature          thermometerMax;
    private Pressure             barometerMin;
    private Pressure             barometerMax;
    private Depth                dailyRainMax;
    private Depth                monthlyRainMax;
    private Depth                yearlyRainMax;
    private String               weatherUndergroundStationId;
    private String               weatherUndergroundPassword;
    private String               dopplerRadarUrl;
    private SolarEventCalculator solar;
    private final Map<String,SensorStation>   sensorStations;
    private final SensorManager  sensorManager;

    /**
     * Constructor.
     */
    public WeatherStation() {
        geographicLocation = new GeographicLocation(new AngularMeasurement(0.0, AngularMeasurement.Unit.DEGREES),
                               new AngularMeasurement(0.0, AngularMeasurement.Unit.DEGREES),
                               new Distance(0.0, Depth.Unit.METERS));
        thermometerMin = new Temperature(-20, Temperature.Unit.FAHRENHEIT);
        thermometerMax = new Temperature(120, Temperature.Unit.FAHRENHEIT);
        barometerMin = new Pressure(28.0, Pressure.Unit.IN_HG);
        barometerMax = new Pressure(32.0, Pressure.Unit.IN_HG);
        dailyRainMax = new Depth(10.0, Depth.Unit.INCHES);
        monthlyRainMax = new Depth(20.0, Depth.Unit.INCHES);
        yearlyRainMax = new Depth(100.0, Depth.Unit.INCHES);
        solar = new SolarEventCalculator(geographicLocation);
        sensorStations = new TreeMap<>();
        windParameters = new WindParameters();
        weatherYearStartMonth = Month.JANUARY;
        sensorManager = SensorManager.getInstance();
        weatherUndergroundStationId = "";
        weatherUndergroundPassword = "";
        dopplerRadarUrl = "";
        manufacturer = "";
        model = "";
        locationCode = "";
        locationDescription = "";
        firmwareDate = "";
        firmwareVersion = "";
    }
    
    /**
     * Calculate the sunrise for the given date.
     *
     * @param date The date for which to calculate sunrise
     * @return The time of the sunrise
     */
    public LocalDateTime sunriseFor(LocalDate date) {
        return solar.computeSunrise(date);

        //return LocalDateTime.ofInstant(sunrise.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Calculate the sunset for the given date.
     *
     * @param date The date for which to calculate sunset
     * @return The time of the sunset
     */
    public LocalDateTime sunsetFor(LocalDate date) {
        return solar.computeSunset(date);
    }
    
    /**
     * Get the weather station manufacturer.
     *
     * @return The manufacturer
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Set the weather station manufacturer.
     *
     * @param manufacturer The manufacturer
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * Get the model name of the weather station.
     *
     * @return The model name
     */
    public String getModel() {
        return model;
    }

    /**
     * Set the model name of the weather station.
     *
     * @param model The model name
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Get the date of the weather station firmware.
     *
     * @return The firmware date
     */
    public String getFirmwareDate() {
        return firmwareDate;
    }

    /**
     * Set the date of the weather station firmware.
     *
     * @param firmwareDate The firmware date
     */
    public void setFirmwareDate(String firmwareDate) {
        this.firmwareDate = firmwareDate;
    }

    /**
     * Get the firmware version.
     *
     * @return The firmware version
     */
    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    /**
     * Set the firmware version.
     *
     * @param firmwareVersion The firmware version
     */
    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    /**
     * Get the code that identifies the location of the weather station.
     * For the United States the location code is a zip (or postal) code.
     *
     * @return The location code
     */
    public String getLocationCode() {
        return locationCode;
    }

    /**
     * Set the code that identifies the location of the weather station.
     *
     * @param locationCode The location code
     */
    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    /**
     * Get the description of the location.
     *
     * @return The location description
     */
    public String getLocationDescription() {
        return locationDescription;
    }

    /**
     * Set the location description.
     *
     * @param desc The location description
     */
    public void setLocationDescription(String desc) {
        locationDescription = desc;
    }

    /**
     * Get the geographic location (latitude, longitude, altitude) of the weather station barometer.
     *
     * @return The geographic location
     */
    public GeographicLocation getGeographicLocation() {
        return geographicLocation;
    }

    /**
     * Set the geographic location (latitude, longitude, altitude) of the weather station barometer.
     *
     * @param geoLocation The geographic location
     */
    public void setGeographicLocation(GeographicLocation geoLocation) {
        geographicLocation = geoLocation;
        solar = new SolarEventCalculator(geoLocation);
    }

    /**
     * Get the month in which the weather year starts.
     *
     * @return The month in which the weather year starts
     */
    public Month getWeatherYearStartMonth() {
        return weatherYearStartMonth;
    }
    
    /**
     * Set the month in which the weather year starts.
     *
     * @param month The month in which the weather year starts
     */
    public void setWeatherYearStartMonth(Month month) {
        weatherYearStartMonth = month;
    }

    /**
     * Get the wind parameters for the weather station. The wind parameters determine
     * how many slices into which the wind direction is divided. As well as how many bins
     * the wind speed is divided into.
     *
     * @return The wind parameters
     */
    public WindParameters getWindParameters() {
        return windParameters;
    }

    /**
     * Set the wind parameters for the weather station.
     *
     * @param windParameters The wind parameters
     */
    public void setWindParameters(WindParameters windParameters) {
        this.windParameters = windParameters;
    }

    /**
     * Check if this weather station is associated with a weather underground weather station.
     *
     * @return True if the weather station is associated with a weather underground weather station
     */
    public boolean hasWeatherUndergroundStation() {
        return !weatherUndergroundStationId.isEmpty() && !weatherUndergroundPassword.isEmpty();
    }

    /**
     * Get the Weather Underground station ID
     *
     * @return The Weather Underground station ID
     */
    public String getWeatherUndergroundStationId() {
        return weatherUndergroundStationId;
    }

    /**
     * Set the Weather Underground station ID
     *
     * @param id The Weather Underground station ID
     */
    public void setWeatherUndergroundStationId(String id) {
        weatherUndergroundStationId = id;
    }

    /**
     * Get the Weather Underground password for this station.
     *
     * @return The password
     */
    public String getWeatherUndergroundPassword() {
        return weatherUndergroundPassword;
    }

    /**
     * Set the Weather Underground password for this station.
     *
     * @param password The password
     */
    public void setWeatherUndergroundPassword(String password) {
        weatherUndergroundPassword = password;
    }

    /**
     * Get the maximum value that the barometer gauge will display.
     *
     * @return The maximum value display by the barometer gauge
     */
    public Pressure getBarometerMax() {
        return barometerMax;
    }

    /**
     * Set the maximum value that the barometer gauge will display.
     *
     * @param max The maximum value display by the barometer gauge
     */
    public void setBarometerMax(Pressure max) {
        barometerMax = max;
    }

    /**
     * Get the minimum value that the barometer gauge will display.
     *
     * @return The maximum value display by the barometer gauge
     */
    public Pressure getBarometerMin() {
        return barometerMin;
    }

    /**
     * Set the minimum value that the barometer gauge will display.
     *
     * @param min The minimum value display by the barometer gauge
     */
    public void setBarometerMin(Pressure min) {
        barometerMin = min;
    }

    /**
     * Get the maximum amount of rain typically received by this weather station in a day.
     *
     * @return The maximum daily rain
     */
    public Depth getDailyRainMax() {
        return dailyRainMax;
    }

    /**
     * Set the maximum amount of rain typically received by this weather station.
     *
     * @param rainMax The maximum daily rain
     */
    public void setDailyRainMax(Depth rainMax) {
        dailyRainMax = rainMax;
    }

    /**
     * Get the maximum amount of rain typically received by this weather station in a year.
     *
     * @return The maximum yearly rain
     */
    public Depth getYearlyRainMax() {
        return yearlyRainMax;
    }

    /**
     * Set the maximum amount of rain typically received by this weather station in a year.
     *
     * @param rainMax The maximum yearly rain
     */
    public void setYearlyRainMax(Depth rainMax) {
        yearlyRainMax = rainMax;
    }

    /**
     * Get the maximum amount of rain typically received by this weather station in a month.
     *
     * @return The maximum monthly rain
     */
    public Depth getMonthlyRainMax() {
        return monthlyRainMax;
    }

    /**
     * Set the maximum amount of rain typically received by this weather station in a month.
     *
     * @param rainMax The maximum monthly rain
     */
    public void setMonthlyRainMax(Depth rainMax) {
        monthlyRainMax = rainMax;
    }

    /**
     * Get the highest temperature typically experienced by this weather station.
     *
     * @return The typical highest temperature
     */
    public Temperature getThermometerMax() {
        return thermometerMax;
    }

    /**
     * Set the highest temperature typically experienced by this weather station.
     *
     * @param max The typical highest temperature
     */
    public void setThermometerMax(Temperature max) {
        thermometerMax = max;
    }

    /**
     * Get the lowest temperature typically experienced by this weather station.
     *
     * @return The typical lowest temperature
     */
    public Temperature getThermometerMin() {
        return thermometerMin;
    }

    /**
     * Set the lowest temperature typically experienced by this weather station.
     *
     * @param min The typical lowest temperature
     */
    public void setThermometerMin(Temperature min) {
        thermometerMin = min;
    }
    
    /**
     * Get the URL where a Doppler radar image can be obtained.
     *
     * @return The URL
     */
    public String getDopplerRadarUrl() {
        return dopplerRadarUrl;
    }
    
    /**
     * Set the URL where a Doppler radar image can be obtained.
     *
     * @param url The URL
     */
    public void setDopplerRadarUrl(String url) {
        dopplerRadarUrl = url;
    }
    
    /**
     * Add a sensor that is connected to this weather station.
     *
     * @param sensor The sensor
     */
    public void addSensor(Sensor sensor) {
        sensorManager.addSensor(sensor);
    }
    
    /**
     * Get the sensor manager that can be used to retrieve the sensors attached to this weather station.
     *
     * @return The sensor manager
     */
    public SensorManager getSensorManager() {
        return sensorManager;
    }
    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("Manufacturer = ").append(manufacturer).append("\n");
        sb.append("Model = ").append(model).append("\n");
        sb.append("Firmware Date = ").append(firmwareDate).append("\n");
        sb.append("Firmware Version = ").append(firmwareVersion).append("\n");
        sb.append("Sensors:\n");
        sensorStations.values().stream().forEach((sensor) -> {
            sb.append(sensor.getType()).append(" ").append(sensor.getName()).append("\n");
        });
        
        return sb.toString();
    }
}