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
    
    public LocalDateTime sunriseFor(LocalDate date) {
        Calendar c = Calendar.getInstance();
        c.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        Calendar sunrise = solar.computeSunriseCalendar(c);

        return LocalDateTime.ofInstant(sunrise.toInstant(), ZoneId.systemDefault());

    }

    public LocalDateTime sunsetFor(LocalDate date) {
        Calendar c = Calendar.getInstance();
        c.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        Calendar sunset = solar.computeSunsetCalendar(c);

        return LocalDateTime.ofInstant(sunset.toInstant(), ZoneId.systemDefault());
    }
    
    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFirmwareDate() {
        return firmwareDate;
    }

    public void setFirmwareDate(String firmwareDate) {
        this.firmwareDate = firmwareDate;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String desc) {
        locationDescription = desc;
    }

    public GeographicLocation getGeographicLocation() {
        return geographicLocation;
    }

    public void setGeographicLocation(GeographicLocation geoLocation) {
        geographicLocation = geoLocation;
        solar = new SolarEventCalculator(geoLocation);
    }

    public Month getWeatherYearStartMonth() {
        return weatherYearStartMonth;
    }
    
    public void setWeatherYearStartMonth(Month month) {
        weatherYearStartMonth = month;
    }

    public WindParameters getWindParameters() {
        return windParameters;
    }

    public void setWindParameters(WindParameters windParameters) {
        this.windParameters = windParameters;
    }

    public boolean hasWeatherUndergroundStation() {
        return !weatherUndergroundStationId.isEmpty() && !weatherUndergroundPassword.isEmpty();
    }

    public String getWeatherUndergroundStationId() {
        return weatherUndergroundStationId;
    }

    public void setWeatherUndergroundStationId(String id) {
        weatherUndergroundStationId = id;
    }

    public String getWeatherUndergroundPassword() {
        return weatherUndergroundPassword;
    }

    public void setWeatherUndergroundPassword(String password) {
        weatherUndergroundPassword = password;
    }

    public Pressure getBarometerMax() {
        return barometerMax;
    }

    public void setBarometerMax(Pressure max) {
        barometerMax = max;
    }

    public Pressure getBarometerMin() {
        return barometerMin;
    }

    public void setBarometerMin(Pressure min) {
        barometerMin = min;
    }

    public Depth getDailyRainMax() {
        return dailyRainMax;
    }

    public void setDailyRainMax(Depth rainMax) {
        dailyRainMax = rainMax;
    }

    public Depth getYearlyRainMax() {
        return yearlyRainMax;
    }

    public void setYearlyRainMax(Depth rainMax) {
        yearlyRainMax = rainMax;
    }

    public Depth getMonthlyRainMax() {
        return monthlyRainMax;
    }

    public void setMonthlyRainMax(Depth rainMax) {
        monthlyRainMax = rainMax;
    }

    public Temperature getThermometerMax() {
        return thermometerMax;
    }

    public void setThermometerMax(Temperature max) {
        thermometerMax = max;
    }

    public Temperature getThermometerMin() {
        return thermometerMin;
    }

    public void setThermometerMin(Temperature min) {
        thermometerMin = min;
    }
    
    public String getDopplerRadarUrl() {
        return dopplerRadarUrl;
    }
    
    public void setDopplerRadarUrl(String url) {
        dopplerRadarUrl = url;
    }
    
    public void addSensor(Sensor sensor) {
        sensorManager.addSensor(sensor);
    }
    
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
