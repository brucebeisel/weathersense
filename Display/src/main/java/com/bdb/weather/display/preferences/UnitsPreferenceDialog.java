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
    @FXML private ToggleGroup temperatureToggleGroup;
    @FXML private ToggleGroup rainfallToggleGroup;
    @FXML private ToggleGroup elevationToggleGroup;
    private final UserPreferences  prefs = UserPreferences.getInstance();
    
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
    }
    
    public void handleOK() {
        Toggle selected = temperatureToggleGroup.getSelectedToggle();
        Temperature.Unit temperatureUnit = (Temperature.Unit)selected.getUserData();
        prefs.setTemperatureUnit(temperatureUnit);

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
    }

    public void handleEnglish() {
        fahrenheit.setSelected(true);
        rainfallInches.setSelected(true);
        elevationFeet.setSelected(true);
    }
}
