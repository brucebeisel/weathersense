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

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.converter.NumberStringConverter;

import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.db.WeatherStationTable;
import com.bdb.weather.common.measurement.AngularMeasurement;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;

/**
 * FXML Controller class
 *
 * @author bruce
 */
public class WeatherStationMgrController implements Initializable {
    @FXML private TextField manufacturer;
    @FXML private TextField model;
    @FXML private TextField firmwareDate;
    @FXML private TextField firmwareVersion;
    @FXML private TextField locationCode;
    @FXML private TextField locationDescription;
    @FXML private TextField latitude;
    @FXML private TextField longitude;
    @FXML private TextField altitude;
    @FXML private RadioButton windDirSlices8;
    @FXML private RadioButton windDirSlices16;
    @FXML private RadioButton windDirSlices360;
    @FXML private TextField thermometerMin;
    @FXML private TextField thermometerMax;
    @FXML private TextField barometerMin;
    @FXML private TextField barometerMax;
    @FXML private TextField weatherUndergroundStationId;
    @FXML private TextField weatherUndergroundPassword;
    @FXML private TextField dopplerRadarURL;
    @FXML private TextField dailyRain;
    @FXML private TextField monthlyRain;
    @FXML private TextField yearlyRain;
    @FXML private TextField windSpeedBinInterval;
    @FXML private RadioButton windSpeedBins3;
    @FXML private RadioButton windSpeedBins4;
    @FXML private RadioButton windSpeedBins5;
    @FXML private RadioButton windSpeedBins6;
    private final WeatherStationModel wsm = new WeatherStationModel();
    private WeatherStationTable wst;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        wsm.manufacturerProperty().bindBidirectional(manufacturer.textProperty());
        wsm.modelProperty().bindBidirectional(model.textProperty());
        wsm.firmwareDateProperty().bindBidirectional(firmwareDate.textProperty());
        wsm.firmwareVersionProperty().bindBidirectional(firmwareVersion.textProperty());
        locationCode.textProperty().bindBidirectional(wsm.locationCodeProperty());
        locationDescription.textProperty().bindBidirectional(wsm.locationDescriptionProperty());
        latitude.textProperty().bindBidirectional(wsm.latitudeProperty(), new NumberStringConverter(AngularMeasurement.getDefaultUnit().getFormatter()));
        longitude.textProperty().bindBidirectional(wsm.longitudeProperty(), new NumberStringConverter(AngularMeasurement.getDefaultUnit().getFormatter()));
        altitude.textProperty().bindBidirectional(wsm.altitudeProperty(), new NumberStringConverter());
        windDirSlices8.selectedProperty().bindBidirectional(wsm.windDirectionSlices8Property());
        windDirSlices16.selectedProperty().bindBidirectional(wsm.windDirectionSlices16Property());
        windDirSlices360.selectedProperty().bindBidirectional(wsm.windDirectionSlices360Property());
        thermometerMin.textProperty().bindBidirectional(wsm.thermometerMinProperty(), new NumberStringConverter(Temperature.getDefaultUnit().getFormatter()));
        thermometerMax.textProperty().bindBidirectional(wsm.thermometerMaxProperty(), new NumberStringConverter(Temperature.getDefaultUnit().getFormatter()));
        barometerMin.textProperty().bindBidirectional(wsm.barometerMinProperty(), new NumberStringConverter(Pressure.getDefaultUnit().getFormatter()));
        barometerMax.textProperty().bindBidirectional(wsm.barometerMaxProperty(), new NumberStringConverter(Pressure.getDefaultUnit().getFormatter()));
        weatherUndergroundStationId.textProperty().bindBidirectional(wsm.weatherUndergroundStationIdProperty());
        weatherUndergroundPassword.textProperty().bindBidirectional(wsm.weatherUndergroundPasswordProperty());
        dopplerRadarURL.textProperty().bindBidirectional(wsm.dopplerRadarUrlProperty());
        dailyRain.textProperty().bindBidirectional(wsm.dailyRainMaxProperty(), new NumberStringConverter(Depth.getDefaultUnit().getFormatter()));
        monthlyRain.textProperty().bindBidirectional(wsm.monthlyRainMaxProperty(), new NumberStringConverter(Depth.getDefaultUnit().getFormatter()));
        yearlyRain.textProperty().bindBidirectional(wsm.yearlyRainMaxProperty(), new NumberStringConverter(Depth.getDefaultUnit().getFormatter()));
        windSpeedBinInterval.textProperty().bindBidirectional(wsm.windSpeedBinIntervalProperty(), new NumberStringConverter(Speed.getDefaultUnit().getFormatter()));
        windSpeedBins3.selectedProperty().bindBidirectional(wsm.windSpeedBins3());
        windSpeedBins4.selectedProperty().bindBidirectional(wsm.windSpeedBins4());
        windSpeedBins5.selectedProperty().bindBidirectional(wsm.windSpeedBins5());
        windSpeedBins6.selectedProperty().bindBidirectional(wsm.windSpeedBins6());
    }    

    public void loadWsData(WeatherStationTable table) {
        wst = table;
        WeatherStation ws = wst.getWeatherStation();
        if (ws == null)
            ws = new WeatherStation();

        wsm.importWeatherStation(ws);
    }
    
    @FXML
    public void saveWsData() {
        WeatherStation ws = wst.getWeatherStation();
        wsm.exportWeatherStation(ws);
        //wst.updateRow(ws);
        System.out.println("Manufacturer: " + ws.getManufacturer());
        System.out.println("Model: " + ws.getManufacturer());
        System.out.println("Firmware Date: " + ws.getFirmwareDate());
        System.out.println("Firmware Version: " + ws.getFirmwareVersion());
    }

    @FXML
    public void testURL() {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("URL Test");
        alert.getButtonTypes().add(ButtonType.OK);
        Image image = new Image(dopplerRadarURL.getText());
        if (image.isError()) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setHeaderText("URL is not valid");
            alert.showAndWait();
        }
        else {
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.setHeaderText("URL is Valid");
            alert.getDialogPane().setContent(new ImageView(image));
            alert.showAndWait();
        }
    }
}
