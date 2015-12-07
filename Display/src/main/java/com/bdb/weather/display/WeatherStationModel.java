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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.bdb.weather.common.SpeedBin;
import com.bdb.weather.common.WeatherStation;

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
    private final DoubleProperty  altitude = new SimpleDoubleProperty();
    private final IntegerProperty weatherYearStartMonth = new SimpleIntegerProperty();
    private final DoubleProperty  windSpeedBinInterval = new SimpleDoubleProperty();
    private final IntegerProperty numWindSpeedBins = new SimpleIntegerProperty();
    private final BooleanProperty windDirSlices8 = new SimpleBooleanProperty();
    private final BooleanProperty windDirSlices16 = new SimpleBooleanProperty();
    private final BooleanProperty windDirSlices360 = new SimpleBooleanProperty();
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
    private final BooleanProperty windSpeedBins3 = new SimpleBooleanProperty();
    private final BooleanProperty windSpeedBins4 = new SimpleBooleanProperty();
    private final BooleanProperty windSpeedBins5 = new SimpleBooleanProperty();
    private final BooleanProperty windSpeedBins6 = new SimpleBooleanProperty();

    public void importWeatherStation(WeatherStation ws) {
        manufacturer.setValue(ws.getManufacturer());
        model.setValue(ws.getModel());
        firmwareDate.setValue(ws.getFirmwareDate());
        firmwareVersion.setValue(ws.getFirmwareVersion());
        locationCode.setValue(ws.getLocationCode());
        locationDescription.setValue(ws.getLocationDescription());
        latitude.setValue(ws.getGeographicLocation().getLatitude().get());
        longitude.setValue(ws.getGeographicLocation().getLongitude().get());
        altitude.setValue(ws.getGeographicLocation().getAltitude().get());
        switch (ws.getWindParameters().getNumWindDirectionSlices()) {
            case 8:
                windDirSlices8.setValue(true);
                windDirSlices16.setValue(false);
                windDirSlices360.setValue(false);
                break;

            case 16:
                windDirSlices8.setValue(false);
                windDirSlices16.setValue(true);
                windDirSlices360.setValue(false);
                break;

            case 360:
                windDirSlices8.setValue(false);
                windDirSlices16.setValue(false);
                windDirSlices360.setValue(true);
                break;
        }

        switch (ws.getWindParameters().getNumWindSpeedBins()) {
            case 3:
                windSpeedBins3.setValue(true);
                windSpeedBins4.setValue(false);
                windSpeedBins5.setValue(false);
                windSpeedBins6.setValue(false);
                break;
            case 4:
                windSpeedBins3.setValue(false);
                windSpeedBins4.setValue(true);
                windSpeedBins5.setValue(false);
                windSpeedBins6.setValue(false);
                break;
            case 5:
                windSpeedBins3.setValue(false);
                windSpeedBins4.setValue(false);
                windSpeedBins5.setValue(true);
                windSpeedBins6.setValue(false);
                break;
            case 6:
                windSpeedBins3.setValue(false);
                windSpeedBins4.setValue(false);
                windSpeedBins5.setValue(false);
                windSpeedBins6.setValue(true);
                break;
        }

        thermometerMin.setValue(ws.getThermometerMin().get());
        thermometerMax.setValue(ws.getThermometerMax().get());
        barometerMin.setValue(ws.getBarometerMin().get());
        barometerMax.setValue(ws.getBarometerMax().get());
        dopplerRadarUrl.setValue(ws.getDopplerRadarUrl());
        weatherUndergroundStationId.setValue(ws.getWeatherUndergroundStationId());
        weatherUndergroundPassword.setValue(ws.getWeatherUndergroundPassword());
        dailyRainMax.setValue(ws.getDailyRainMax().get());
        monthlyRainMax.setValue(ws.getMonthlyRainMax().get());
        yearlyRainMax.setValue(ws.getYearlyRainMax().get());
        windSpeedBinInterval.setValue(ws.getWindParameters().getWindSpeedBinInterval().get());
    }

    public void exportWeatherStation(WeatherStation ws) {
        ws.setManufacturer(manufacturer.getValue());
        ws.setModel(model.getValue());
        ws.setFirmwareDate(firmwareDate.getValue());
        ws.setFirmwareVersion(firmwareVersion.getValue());
    }

    public StringProperty manufacturerProperty() {
        return manufacturer;
    }

    public StringProperty modelProperty() {
        return model;
    }

    public StringProperty locationCodeProperty() {
        return locationCode;
    }

    public StringProperty locationDescriptionProperty() {
        return locationDescription;
    }

    public StringProperty firmwareDateProperty() {
        return firmwareDate;
    }

    public StringProperty firmwareVersionProperty() {
        return firmwareVersion;
    }

    public DoubleProperty latitudeProperty() {
        return latitude;
    }

    public DoubleProperty longitudeProperty() {
        return longitude;
    }

    public DoubleProperty altitudeProperty() {
        return altitude;
    }

    public DoubleProperty windSpeedBinIntervalProperty() {
        return windSpeedBinInterval;
    }

    public IntegerProperty numWindSpeedBinsProperty() {
        return numWindSpeedBins;
    }

    public BooleanProperty windDirectionSlices8Property() {
        return windDirSlices8;
    }

    public BooleanProperty windDirectionSlices16Property() {
        return windDirSlices16;
    }

    public BooleanProperty windDirectionSlices360Property() {
        return windDirSlices360;
    }

    public DoubleProperty thermometerMinProperty() {
        return thermometerMin;
    }

    public DoubleProperty thermometerMaxProperty() {
        return thermometerMax;
    }

    public DoubleProperty barometerMinProperty() {
        return barometerMin;
    }

    public DoubleProperty barometerMaxProperty() {
        return barometerMax;
    }

    public DoubleProperty dailyRainMaxProperty() {
        return dailyRainMax;
    }

    public DoubleProperty monthlyRainMaxProperty() {
        return monthlyRainMax;
    }

    public DoubleProperty yearlyRainMaxProperty() {
        return yearlyRainMax;
    }

    public StringProperty weatherUndergroundStationIdProperty() {
        return weatherUndergroundStationId;
    }

    public StringProperty weatherUndergroundPasswordProperty() {
        return weatherUndergroundPassword;
    }

    public StringProperty dopplerRadarUrlProperty() {
        return dopplerRadarUrl;
    }

    public BooleanProperty windSpeedBins3() {
        return windSpeedBins3;
    }

    public BooleanProperty windSpeedBins4() {
        return windSpeedBins4;
    }

    public BooleanProperty windSpeedBins5() {
        return windSpeedBins5;
    }

    public BooleanProperty windSpeedBins6() {
        return windSpeedBins6;
    }

}
