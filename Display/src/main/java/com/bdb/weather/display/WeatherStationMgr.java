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
package com.bdb.weather.display;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.weather.common.db.CollectorCommandsTable;
import com.bdb.weather.common.db.TemperatureBinTable;
import com.bdb.weather.common.db.WeatherStationTable;

/**
 *
 * @author Bruce
 */
public class WeatherStationMgr {
    private static DBConnection connection;
    
    private WeatherStationTable wsTable;
    private TemperatureBinTable binTable;
    private CollectorCommandsTable commandTable;
    /*
    private String origWindSpeedInterval;
    private int origSpeedBins;
    private int origWindSlices;
    private final DBConnection connection;
    */

    private WeatherStationMgr() {
        wsTable = new WeatherStationTable(connection);
        commandTable = new CollectorCommandsTable(connection);
        binTable = new TemperatureBinTable(connection);
    }
    
//    public void setManufacturer(String value) {
//        manufacturer.setValue(value);
//    }
//    
//    public String getManufacturer() {
//        return manufacturer.getValue();
//    }
//    
//    public StringProperty manufacturerProperty() {
//        return manufacturer;
//    }

    private void createElements() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WeatherStationMgr.fxml"));
        loader.load();
        WeatherStationMgrController controller = loader.getController();
        controller.loadWsData(wsTable);
        BorderPane root = loader.getRoot();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/weathersense.css");
        stage.setTitle("Weather Station Editor");
        //stage.setResizable(false);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    /*
    private void loadWsData() {
        if (ws == null) {
            ws = new WeatherStation();
        }

        manufacturerTF.setText(ws.getManufacturer());
        modelTF.setText(ws.getModel());
        firmwareDateTF.setText(ws.getFirmwareDate());
        firmwareVersionTF.setText(ws.getFirmwareVersion());

        if (ws.getLocationCode() != null)
            locationCodeTF.setText(ws.getLocationCode());

        if (ws.getLocationDescription() != null)
            locationDescriptionTF.setText(ws.getLocationDescription());

        latitudeTF.setText(String.format("%.6f", ws.getGeographicLocation().getLatitude().get(AngularMeasurement.Unit.DEGREES)));
        longitudeTF.setText(String.format("%.6f", ws.getGeographicLocation().getLongitude().get(AngularMeasurement.Unit.DEGREES)));
        altitudeTF.setText(ws.getGeographicLocation().getAltitude().toString());
        origWindSlices = ws.getWindParameters().getNumWindDirectionSlices();
        windSliceCountRB.setValue(Integer.toString(origWindSlices));
        origSpeedBins = ws.getWindParameters().getNumWindSpeedBins();
        windSpeedBinCountRB.setValue(Integer.toString(origSpeedBins));
        origWindSpeedInterval = Speed.getDefaultFormatter().format(ws.getWindParameters().getWindSpeedBinInterval().get());
        windSpeedBinIntervalTF.setText(origWindSpeedInterval);
        minThermometerValueTF.setText(ws.getThermometerMin().toString());
        maxThermometerValueTF.setText(ws.getThermometerMax().toString());
        minBarometerValueTF.setText(ws.getBarometerMin().toString());
        maxBarometerValueTF.setText(ws.getBarometerMax().toString());
        maxDailyRainTF.setText(ws.getDailyRainMax().toString());
        maxMonthlyRainTF.setText(ws.getMonthlyRainMax().toString());
        maxYearlyRainTF.setText(ws.getYearlyRainMax().toString());

        if (ws.getWeatherUndergroundStationId() != null) {
            weatherUndergroundIdTF.setText(ws.getWeatherUndergroundStationId());
            weatherUndergroundPasswordTF.setText(ws.getWeatherUndergroundPassword());
        }
        else {
            weatherUndergroundIdTF.setText("");
            weatherUndergroundPasswordTF.setText("");
        }
        
        if (ws.getDopplerRadarUrl() != null)
            dopplerRadarUrlTF.setText(ws.getDopplerRadarUrl());


        TemperatureBinMgr mgr = new TemperatureBinMgr(connection);
        mgr.refresh();
        temperatureBinEditor.loadValues(mgr.getAllBins());

    }
        if (ws == null)
            ws = new WeatherStation();

        String newWindSpeedInterval = windSpeedBinIntervalTF.getText();
        int newWindSliceCount = Integer.parseInt(windSliceCountRB.getValue());
        int newSpeedBinCount = Integer.parseInt(windSpeedBinCountRB.getValue());
        boolean windParamsChanged = !(newWindSpeedInterval.equals(origWindSpeedInterval) && newWindSliceCount == origWindSlices && newSpeedBinCount == origSpeedBins);
        boolean temperatureBinsChanged = temperatureBinEditor.hasChangeOccurred();

        String message = null;
        String title = null;

        if (windParamsChanged && temperatureBinsChanged) {
            message = "Temperature bins and Wind Parameters have changed. All Daily Summary records will be recalculated. This may take some time. Save anyway?";
            title = "Temperature Bins/Wind Parameters Changed";
        }
        else if (temperatureBinsChanged) {
            message = "Temperature bins have changed. All Daily Summary records will be recalculated. This may take some time. Save anyway?";
            title = "Temperature Bins Changed";
        }
        else if (windParamsChanged) {
            message = "Wind parameters have changed. All Daily Summary records will be recalculated. This may take some time. Save anyway?";
            title = "Wind Parameters Changed";
        }

        if (message != null) {
            int answer = JOptionPane.showConfirmDialog(frame, message, title, JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.NO_OPTION)
                return;
        }

        ws.setManufacturer(manufacturerTF.getText());
        ws.setModel(modelTF.getText());
        ws.setFirmwareDate(firmwareDateTF.getText());
        ws.setFirmwareVersion(firmwareVersionTF.getText());

        if (!locationCodeTF.getText().isEmpty())
            ws.setLocationCode(locationCodeTF.getText());

        if (!locationDescriptionTF.getText().isEmpty())
            ws.setLocationDescription(locationDescriptionTF.getText());

        double lat;
        if (latitudeTF.getText().equals(""))
            lat = 0.0;
        else
            lat = Double.parseDouble(latitudeTF.getText());

        double lon;
        if (longitudeTF.getText().equals(""))
            lon = 0.0;
        else
            lon = Double.parseDouble(longitudeTF.getText());

        double alt;
        if (altitudeTF.getText().equals(""))
            alt = 0.0;
        else
            alt = Double.parseDouble(altitudeTF.getText());

        ws.setGeographicLocation(new GeographicLocation(new AngularMeasurement(lat, AngularMeasurement.Unit.DEGREES),
                                                        new AngularMeasurement(lon, AngularMeasurement.Unit.DEGREES),
                                                        new Distance(alt))); // TODO Use user preferences


        ws.setWindParameters(new WindParameters(new Speed(Double.parseDouble(newWindSpeedInterval)), newSpeedBinCount, newWindSliceCount));

        double val;
        if (minThermometerValueTF.getText().equals(""))
            val = -40;
        else
            val = Double.parseDouble(minThermometerValueTF.getText());

        ws.setThermometerMin(new Temperature(val));

        if (maxThermometerValueTF.getText().equals(""))
            val = 130;
        else
            val = Double.parseDouble(maxThermometerValueTF.getText());

        ws.setThermometerMax(new Temperature(val));

        if (minBarometerValueTF.getText().equals(""))
            val = 29.00;
        else
            val = Double.parseDouble(minBarometerValueTF.getText());

        ws.setBarometerMin(new Pressure(val));

        if (maxBarometerValueTF.getText().equals(""))
            val = 31.00;
        else
            val = Double.parseDouble(maxBarometerValueTF.getText());

        ws.setBarometerMax(new Pressure(val));

        if (maxDailyRainTF.getText().equals(""))
            val = 10.00;
        else
            val = Double.parseDouble(maxDailyRainTF.getText());

        ws.setDailyRainMax(new Depth(val));

        if (maxMonthlyRainTF.getText().equals(""))
            val = 30.00;
        else
            val = Double.parseDouble(maxMonthlyRainTF.getText());

        ws.setMonthlyRainMax(new Depth(val));

        if (maxYearlyRainTF.getText().equals(""))
            val = 100.00;
        else
            val = Double.parseDouble(maxYearlyRainTF.getText());

        ws.setYearlyRainMax(new Depth(val));

        String id = weatherUndergroundIdTF.getText();

        if (id.length() > 0) {
            ws.setWeatherUndergroundStationId(id);
            ws.setWeatherUndergroundPassword(weatherUndergroundPasswordTF.getText());
        }
        else {
            ws.setWeatherUndergroundStationId(null);
            ws.setWeatherUndergroundPassword(null);
        }
        
        if (!dopplerRadarUrlTF.getText().isEmpty())
            ws.setDopplerRadarUrl(dopplerRadarUrlTF.getText());
        else
            ws.setDopplerRadarUrl(null);

        //
        // If the table was updated successfully and the option dialog was displayed earlier, send a summarize
        // command to the collector.
        //
        connection.startTransaction();
        if (!wsTable.updateRow(ws)) {
            JOptionPane.showMessageDialog(this, "Error saving weather station data", "Database error", JOptionPane.ERROR_MESSAGE);
            connection.rollback();
        }
        else if (temperatureBinsChanged) {
            TemperatureBinMgr mgr = new TemperatureBinMgr(connection);
            mgr.replaceBins(temperatureBinEditor.saveValues());
            if (!mgr.sync()) {
                JOptionPane.showMessageDialog(this, "Error saving temperature bin data", "Database error", JOptionPane.ERROR_MESSAGE);
                connection.rollback();
            }
            else if (connection.endTransaction() && message != null) {
                commandTable.addCommand(CollectorCommand.SUMMARIZE_COMMAND + " " + CollectorCommand.SUMMARIZE_ALL);
            }
        }
    }
*/
    public static void initialize(DBConnection connection) {
        WeatherStationMgr.connection = connection;
    }

    public static void editWeatherStation() {
        try {
            WeatherStationMgr dialog = new WeatherStationMgr();
            dialog.createElements();
        } catch (IOException ex) {
            Logger.getLogger(WeatherStationMgr.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
