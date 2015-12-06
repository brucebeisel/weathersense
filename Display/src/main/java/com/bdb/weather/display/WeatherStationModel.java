/*
 * Copyright (C) 2015 bruce
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
package com.bdb.weather.display;

import java.time.Month;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.bdb.weather.common.SpeedBin;

/**
 *
 * @author bruce
 */
public class WeatherStationModel {
    private final StringProperty  manufacturer = new SimpleStringProperty();
    private final StringProperty  model = new SimpleStringProperty();
    private final StringProperty  locationCode = new SimpleStringProperty();
    private final StringProperty  locationDescription = new SimpleStringProperty();
    private final StringProperty  firmwareDate = new SimpleStringProperty();
    private final StringProperty  firmwareVersion = new SimpleStringProperty();
    private final DoubleProperty  latitude = new SimpleDoubleProperty();
    private final DoubleProperty  longitude = new SimpleDoubleProperty();
    private Month           weatherYearStartMonth;
    private final DoubleProperty  windSpeedBinInterval = new SimpleDoubleProperty();
    private final IntegerProperty numWindSpeedBins = new SimpleIntegerProperty();
    private final IntegerProperty numWindDirectionSlices = new SimpleIntegerProperty();
    private final ListProperty<SpeedBin> speedBins = new SimpleListProperty<>();
    private final DoubleProperty  thermometerMin = new SimpleDoubleProperty();
    private final DoubleProperty  thermometerMax = new SimpleDoubleProperty();
    private final DoubleProperty  barometerMin = new SimpleDoubleProperty();
    private final DoubleProperty  barometerMax = new SimpleDoubleProperty();
    private final DoubleProperty  dailyRainMax = new SimpleDoubleProperty();
    private final DoubleProperty  monthlyRainMax = new SimpleDoubleProperty();
    private final DoubleProperty  yearlyRainMax = new SimpleDoubleProperty();
    private final StringProperty  weatherUndergroundStationId = new SimpleStringProperty();
    private final StringProperty  weatherUndergroundPassword = new SimpleStringProperty();
    private final StringProperty  dopplerRadarUrl = new SimpleStringProperty();

    public void setManufacturer(String value) {
        manufacturer.set(value);
    }

    public String getManufacturer() {
        return manufacturer.get();
    }

    public StringProperty manufacturerProperty() {
        return manufacturer;
    }

    public void setModel(String value) {
        model.set(value);
    }

    public String getModel() {
        return model.get();
    }

    public StringProperty modelProperty() {
        return model;
    }

    public void setLocationCode(String value) {
        locationCode.set(value);
    }

    public String getLocationCode() {
        return locationCode.get();
    }

    public StringProperty locationCodeProperty() {
        return locationCode;
    }

    public void setLocationDescription(String value) {
        locationDescription.set(value);
    }

    public String getLocationDescription() {
        return locationDescription.get();
    }

    public StringProperty locationDescriptionProperty() {
        return locationDescription;
    }

    public void setFirmwareDate(String value) {
        firmwareDate.set(value);
    }

    public String getFirmwareDate() {
        return firmwareDate.get();
    }

    public StringProperty firmwareDateProperty() {
        return firmwareDate;
    }

    public void setFirmwareVersion(String value) {
        firmwareVersion.set(value);
    }

    public String getFirmwareVersion() {
        return firmwareVersion.get();
    }

    public StringProperty firmwareVersionProperty() {
        return firmwareVersion;
    }

    public void setLatitude(Double value) {
        latitude.set(value);
    }

    public Double getLatitude() {
        return latitude.get();
    }

    public DoubleProperty latitudeProperty() {
        return latitude;
    }

    public void setLongitude(Double value) {
        longitude.set(value);
    }

    public Double getLongitude() {
        return longitude.get();
    }

    public DoubleProperty longitudeProperty() {
        return longitude;
    }

    public void setWindSpeedBinInterval(Double value) {
        windSpeedBinInterval.set(value);
    }

    public Double getWindSpeedBinInterval() {
        return windSpeedBinInterval.get();
    }

    public DoubleProperty windSpeedBinIntervalProperty() {
        return windSpeedBinInterval;
    }

    public void setNumWindSpeedBins(Integer value) {
        numWindSpeedBins.set(value);
    }

    public Integer getNumWindSpeedBins() {
        return numWindSpeedBins.get();
    }

    public IntegerProperty numWindSpeedBinsProperty() {
        return numWindSpeedBins;
    }

    public void setNumWindDirectionSlices(Integer value) {
        numWindDirectionSlices.set(value);
    }

    public Integer getNumWindDirectionSlices() {
        return numWindDirectionSlices.get();
    }

    public IntegerProperty numWindDirectionSlicesProperty() {
        return numWindDirectionSlices;
    }

    public void setThermometerMin(Double value) {
        thermometerMin.set(value);
    }

    public Double getThermometerMin() {
        return thermometerMin.get();
    }

    public DoubleProperty thermometerMinProperty() {
        return thermometerMin;
    }

    public void setThermometerMax(Double value) {
        thermometerMax.set(value);
    }

    public Double getThermometerMax() {
        return thermometerMax.get();
    }

    public DoubleProperty thermometerMaxProperty() {
        return thermometerMax;
    }

    public void setBarometerMin(Double value) {
        barometerMin.set(value);
    }

    public Double getBarometerMin() {
        return barometerMin.get();
    }

    public DoubleProperty barometerMinProperty() {
        return barometerMin;
    }

    public void setBarometerMax(Double value) {
        barometerMax.set(value);
    }

    public Double getBarometerMax() {
        return barometerMax.get();
    }

    public DoubleProperty barometerMaxProperty() {
        return barometerMax;
    }

    public void setDailyRainMax(Double value) {
        dailyRainMax.set(value);
    }

    public Double getDailyRainMax() {
        return dailyRainMax.get();
    }

    public DoubleProperty dailyRainMaxProperty() {
        return dailyRainMax;
    }

    public void setMonthlyRainMax(Double value) {
        monthlyRainMax.set(value);
    }

    public Double getMonthlyRainMax() {
        return monthlyRainMax.get();
    }

    public DoubleProperty monthlyRainMaxProperty() {
        return monthlyRainMax;
    }

    public void setYearlyRainMax(Double value) {
        yearlyRainMax.set(value);
    }

    public Double getYearlyRainMax() {
        return yearlyRainMax.get();
    }

    public DoubleProperty yearlyRainMaxProperty() {
        return yearlyRainMax;
    }

    public void setWeatherUndergroundStationId(String value) {
        weatherUndergroundStationId.set(value);
    }

    public String getWeatherUndergroundStationId() {
        return weatherUndergroundStationId.get();
    }

    public StringProperty weatherUndergroundStationIdProperty() {
        return weatherUndergroundStationId;
    }

    public void setWeatherUndergroundPassword(String value) {
        weatherUndergroundPassword.set(value);
    }

    public String getWeatherUndergroundPassword() {
        return weatherUndergroundPassword.get();
    }

    public StringProperty weatherUndergroundPasswordProperty() {
        return weatherUndergroundPassword;
    }

    public void setDopplerRadarUrl(String value) {
        dopplerRadarUrl.set(value);
    }

    public String getDopplerRadarUrl() {
        return dopplerRadarUrl.get();
    }

    public StringProperty dopplerRadarUrlProperty() {
        return dopplerRadarUrl;
    }
  
   
}
