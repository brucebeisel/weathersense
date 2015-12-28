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
package com.bdb.weather.display.preferences;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Distance;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;

/**
 *
 * @author Bruce
 */
@SuppressWarnings("serial")
public class UnitsPreferenceDialog extends BorderPane {
    @FXML private RadioButton celsius;
    @FXML private RadioButton fahrenheit;
    @FXML private RadioButton kelvin;
    @FXML private RadioButton rainfallInches;
    @FXML private RadioButton rainfallCentimeters;
    @FXML private RadioButton rainfallMillimeters;
    @FXML private RadioButton elevationFeet;
    @FXML private RadioButton elevationMeters;
    @FXML private RadioButton speedKPH;
    @FXML private RadioButton speedMPS;
    @FXML private RadioButton speedMPH;
    @FXML private RadioButton speedKTS;
    @FXML private RadioButton pressureKPA;
    @FXML private RadioButton pressureHPA;
    @FXML private RadioButton pressureMB;
    @FXML private RadioButton pressureINHG;
    @FXML private ToggleGroup temperatureToggleGroup;
    @FXML private ToggleGroup rainfallToggleGroup;
    @FXML private ToggleGroup elevationToggleGroup;
    @FXML private ToggleGroup windSpeedToggleGroup;
    @FXML private ToggleGroup barometricPressureToggleGroup;
    private final UnitsPreferences  prefs = UnitsPreferences.getInstance();
    
    @SuppressWarnings("LeakingThisInConstructor")
    public UnitsPreferenceDialog() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MeasurementUnits.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        }
	catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        celsius.setUserData(Temperature.Unit.CELSIUS);
        fahrenheit.setUserData(Temperature.Unit.FAHRENHEIT);
        kelvin.setUserData(Temperature.Unit.KELVIN);
        rainfallCentimeters.setUserData(Depth.Unit.CENTIMETERS);
        rainfallMillimeters.setUserData(Depth.Unit.MILLIMETERS);
        rainfallInches.setUserData(Depth.Unit.INCHES);
        elevationFeet.setUserData(Distance.Unit.FEET);
        elevationMeters.setUserData(Distance.Unit.METERS);
        speedKPH.setUserData(Speed.Unit.KM_PER_HOUR);
        speedMPS.setUserData(Speed.Unit.METERS_PER_SEC);
        speedMPH.setUserData(Speed.Unit.MILES_PER_HOUR);
        speedKTS.setUserData(Speed.Unit.KNOTS);
        pressureKPA.setUserData(Pressure.Unit.KILO_PASCAL);
        pressureHPA.setUserData(Pressure.Unit.HECTO_PASCAL);
        pressureMB.setUserData(Pressure.Unit.MILLIBAR);
        pressureINHG.setUserData(Pressure.Unit.IN_HG);

        switch (prefs.getTemperatureUnit()) {
            case CELSIUS:
                celsius.setSelected(true);
                break;

            case FAHRENHEIT:
                fahrenheit.setSelected(true);
                break;

            default:
                kelvin.setSelected(true);
                break;
        }

        switch (prefs.getRainfallUnit()) {
            case CENTIMETERS:
                rainfallCentimeters.setSelected(true);
                break;

            case INCHES:
                rainfallInches.setSelected(true);
                break;

            default:
            case MILLIMETERS:
                rainfallMillimeters.setSelected(true);
                break;
        }

        switch (prefs.getElevationUnit()) {
            case FEET:
                elevationFeet.setSelected(true);
                break;

            default:
            case METERS:
                elevationMeters.setSelected(true);
                break;
        }

        switch (prefs.getSpeedUnit()) {
            case KM_PER_HOUR:
                speedKPH.setSelected(true);
                break;

            default:
            case METERS_PER_SEC:
                speedMPS.setSelected(true);
                break;

            case MILES_PER_HOUR:
                speedMPH.setSelected(true);
                break;

            case KNOTS:
                speedKTS.setSelected(true);
                break;
        }

        switch (prefs.getPressureUnit()) {
            case KILO_PASCAL:
                pressureKPA.setSelected(true);
                break;

            case HECTO_PASCAL:
                pressureHPA.setSelected(true);
                break;

            case MILLIBAR:
                pressureMB.setSelected(true);
                break;

            case IN_HG:
                pressureINHG.setSelected(true);
                break;

        }
    }
    
    public void handleOK() {
        Toggle selected = temperatureToggleGroup.getSelectedToggle();
        prefs.setTemperatureUnit((Temperature.Unit)selected.getUserData());

        selected = rainfallToggleGroup.getSelectedToggle();
        prefs.setRainfallUnit((Depth.Unit)selected.getUserData());

        selected = elevationToggleGroup.getSelectedToggle();
        prefs.setElevationUnit((Distance.Unit)selected.getUserData());

        selected = windSpeedToggleGroup.getSelectedToggle();
        prefs.setSpeedUnit((Speed.Unit)selected.getUserData());

        selected = barometricPressureToggleGroup.getSelectedToggle();
        prefs.setPressureUnit((Pressure.Unit)selected.getUserData());

        prefs.sync();
        handleCancel();
    }

    public void handleCancel() {
        ((Stage)getScene().getWindow()).close();
    }

    public void handleMetric() {
        celsius.setSelected(true);
        rainfallMillimeters.setSelected(true);
        elevationMeters.setSelected(true);
        speedMPS.setSelected(true);
        pressureHPA.setSelected(true);
    }

    public void handleImperial() {
        fahrenheit.setSelected(true);
        rainfallInches.setSelected(true);
        elevationFeet.setSelected(true);
        speedMPH.setSelected(true);
        pressureINHG.setSelected(true);
    }
}
