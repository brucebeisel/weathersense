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
package com.bdb.weather.display;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.db.CollectorCommandsTable;
import com.bdb.weather.common.db.TemperatureBinTable;
import com.bdb.weather.common.db.WeatherStationTable;

/**
 *
 * @author Bruce
 */
@SuppressWarnings("serial")
public class WeatherStationMgr implements Initializable {
    private static DBConnection connection;
    
    private WeatherStationTable wsTable;
    private TemperatureBinTable binTable;
    private CollectorCommandsTable commandTable;
    private WeatherStation ws;
    //@FXML
    private TextField manufacturer;
    /*
    private final TextField manufacturerTF = new TextField();
    private final TextField modelTF = new TextField(50);
    private final TextField firmwareDateTF = new TextField(50);
    private final TextField firmwareVersionTF = new TextField(50);
    private final TextField locationCodeTF = new TextField(20);
    private final TextField locationDescriptionTF = new TextField(50);
    private final TextField latitudeTF = new FormattedTextField(new DecimalFormat("##.######"));
    private final TextField longitudeTF = new FormattedTextField(new DecimalFormat("###.######"));
    private final TextField altitudeTF = new FormattedTextField(Distance.getDefaultFormatter());
    private final TextField minThermometerValueTF = new FormattedTextField(Temperature.getDefaultFormatter());
    private final TextField maxThermometerValueTF = new FormattedTextField(Temperature.getDefaultFormatter());
    private final TextField minBarometerValueTF = new FormattedTextField(Pressure.getDefaultFormatter());
    private final TextField maxBarometerValueTF = new FormattedTextField(Pressure.getDefaultFormatter());
    private final TextField maxDailyRainTF = new FormattedTextField(Depth.getDefaultFormatter());
    private final TextField maxMonthlyRainTF = new FormattedTextField(Depth.getDefaultFormatter());
    private final TextField maxYearlyRainTF = new FormattedTextField(Depth.getDefaultFormatter());
    private final RadioButtonPanel windSliceCountRB = new RadioButtonPanel(WIND_SLICE_OPTIONS);
    private final RadioButtonPanel windSpeedBinCountRB = new RadioButtonPanel(WIND_SPEED_BIN_COUNT_OPTIONS);
    private final TextField windSpeedBinIntervalTF = new FormattedTextField(Speed.getDefaultFormatter());
    private final TextField dopplerRadarUrlTF = new TextField(50);
    private final TextField weatherUndergroundIdTF = new TextField(20);
    private final TextField weatherUndergroundPasswordTF = new TextField(20);
    private final TemperatureBinEditor temperatureBinEditor = new TemperatureBinEditor();
    private String origWindSpeedInterval;
    private int origSpeedBins;
    private int origWindSlices;
    private final DBConnection connection;
    */

    private WeatherStationMgr() {
        wsTable = new WeatherStationTable(connection);
        ws = wsTable.getWeatherStation();
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
        BorderPane root = loader.getRoot();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/weathersense.css");
        stage.setTitle("Weather Station Editor");
        //stage.setResizable(false);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();

        /*
        JPanel paramPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(paramPanel, BoxLayout.Y_AXIS);
        paramPanel.setLayout(boxLayout);

        JPanel wsInfoPanel = new JPanel();
        setDefaultBorder(wsInfoPanel, "Weather Station Information");
        GridBagLayout layout = new GridBagLayout();
        wsInfoPanel.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel l = new JLabel("Manufacturer:");
        wsInfoPanel.add(l, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 2;
        wsInfoPanel.add(manufacturerTF, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        wsInfoPanel.add(new JLabel("Model:"), gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 2;
        wsInfoPanel.add(modelTF, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        wsInfoPanel.add(new JLabel("Firmware Date:"), gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 2;
        wsInfoPanel.add(firmwareDateTF, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        wsInfoPanel.add(new JLabel("Firmware Version:"), gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 2;
        wsInfoPanel.add(firmwareVersionTF, gbc);

        JPanel locationPanel = new JPanel();
        setDefaultBorder(locationPanel, "Location Information");
        layout = new GridBagLayout();
        locationPanel.setLayout(layout);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        l = new JLabel("Location Code:");
        locationPanel.add(l, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 2;
        locationPanel.add(locationCodeTF, gbc);
        locationCodeTF.setToolTipText("The location code (zip code) is used to load the seasonal averages and extremes.");

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        locationPanel.add(new JLabel("Location Description:"), gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 3;
        locationPanel.add(locationDescriptionTF, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        locationPanel.add(new JLabel("Latitude:"), gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        locationPanel.add(latitudeTF, gbc);
        latitudeTF.setToolTipText("The latitude and longitude are used to calculate the sunrise and sunset");

        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        locationPanel.add(new JLabel("Longitude:"), gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        locationPanel.add(longitudeTF, gbc);
        longitudeTF.setToolTipText("The latitude and longitude are used to calculate the sunrise and sunset");

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        locationPanel.add(new JLabel("Altitude:"), gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        locationPanel.add(altitudeTF, gbc);
        altitudeTF.setToolTipText("Altitude is used to calculate barometric pressure offset");
        
        TilePane innerPanel = new TilePane(1, 0);
        JPanel windPanel = new JPanel(new GridBagLayout());
        setDefaultBorder(windPanel, "Wind Parameters");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        windPanel.add(new JLabel("Number of Wind Direction Slices:"), gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        windPanel.add(windSliceCountRB, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        windPanel.add(new JLabel("Number of Wind Speed Bins:"), gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        windPanel.add(windSpeedBinCountRB, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        windPanel.add(new JLabel("Wind Speed Bin Interval:"), gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        windSpeedBinIntervalTF.setColumns(5);
        windPanel.add(windSpeedBinIntervalTF, gbc);
        innerPanel.add(windPanel);
        setDefaultBorder(temperatureBinEditor, "Temperature Bins");
        innerPanel.add(temperatureBinEditor);

        JPanel minMaxPanel = new JPanel(new GridLayout(1,0));
        
        setDefaultBorder(minMaxPanel, "Ranges");
        JPanel p1 = new JPanel();
        setDefaultBorder(p1, "Thermometer (" + Temperature.getDefaultUnit() + ")");
        p1.add(new LabeledFieldPanel("Minimum:", minThermometerValueTF));
        p1.add(new LabeledFieldPanel("Maximum:", maxThermometerValueTF));
        minMaxPanel.add(p1);

        p1 = new JPanel();
        setDefaultBorder(p1, "Barometer (" + Pressure.getDefaultUnit() + ")");
        p1.add(new LabeledFieldPanel("Minimum:", minBarometerValueTF));
        p1.add(new LabeledFieldPanel("Maximum:", maxBarometerValueTF));
        minMaxPanel.add(p1);

        p1 = new JPanel();
        setDefaultBorder(p1, "Maximum Rain (" + Depth.getDefaultUnit() + ")");
        p1.add(new LabeledFieldPanel("Daily:", maxDailyRainTF));
        p1.add(new LabeledFieldPanel("Monthly:", maxMonthlyRainTF));
        p1.add(new LabeledFieldPanel("Yearly:", maxYearlyRainTF));
        minMaxPanel.add(p1);
        
        JPanel weatherUndergroundPanel = new JPanel();
        weatherUndergroundPanel.setLayout(new BoxLayout(weatherUndergroundPanel, BoxLayout.X_AXIS));
        setDefaultBorder(weatherUndergroundPanel, "Weather Underground Settings");
        weatherUndergroundPanel.add(new JLabel("Station ID:"));
        weatherUndergroundPanel.add(weatherUndergroundIdTF);
        weatherUndergroundPanel.add(Box.createHorizontalStrut(10));
        weatherUndergroundPanel.add(new JLabel("Password:"));
        weatherUndergroundPanel.add(weatherUndergroundPasswordTF);
        
        JPanel dopplerUrlPanel = new JPanel();
        setDefaultBorder(dopplerUrlPanel, "Doppler Radar");
        dopplerUrlPanel.add(new JLabel("Doppler Radar Image URL:"));
        dopplerUrlPanel.add(dopplerRadarUrlTF);
        urlTest = new JButton("Test URL");
        dopplerUrlPanel.add(urlTest);
        urlTest.addActionListener((ActionEvent e) -> {
            try {
                URL url = new URL(dopplerRadarUrlTF.getText());
                BufferedImage bi = ImageIO.read(url);
                if (bi == null)
                    JOptionPane.showMessageDialog(null, "URL does not refer to an image", "", JOptionPane.WARNING_MESSAGE);
                else {
                    ImageIcon image = new ImageIcon(bi);
                    JOptionPane.showMessageDialog(null, null, "URL is good", JOptionPane.INFORMATION_MESSAGE, image);
                }
            }
            catch (IOException ex) {
                Logger.getLogger(WeatherStationMgr.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "Invalid URL", "Invalid URL", JOptionPane.WARNING_MESSAGE);
            }
        });

        paramPanel.add(wsInfoPanel);
        paramPanel.add(Box.createVerticalStrut(10));
        paramPanel.add(locationPanel);
        paramPanel.add(Box.createVerticalStrut(10));
        paramPanel.add(innerPanel);
        paramPanel.add(Box.createVerticalStrut(10));
        paramPanel.add(minMaxPanel);
        paramPanel.add(Box.createVerticalStrut(10));
        paramPanel.add(weatherUndergroundPanel);
        paramPanel.add(Box.createVerticalStrut(10));
        paramPanel.add(dopplerUrlPanel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(paramPanel, BorderLayout.CENTER);

        BorderPane buttonPanel = new JPanel();
        buttonPanel.setBorder(new LineBorder(Color.black));
        Button b = new Button("OK");
        b.addActionListener(this);
        b.setActionCommand("OK");
        buttonPanel.add(b);

        b = new Button("Cancel");
        b.addActionListener(this);
        b.setActionCommand("Cancel");
        buttonPanel.add(b);

        b = new Button("Help");
        b.addActionListener(this);
        b.setActionCommand("Help");
        buttonPanel.add(b);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        pack();

        setLocationRelativeTo(frame);

        loadWsData();
*/
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
*/
    @FXML
    public void saveWsData() {
        System.out.println("Manufacturer: " + manufacturer.getText());
    }
        /*
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

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "Cancel":
                this.hide();
                break;

            case "OK":
            default:
                //saveWsData();
                this.hide();
                break;

        }
    }

    private void setDefaultBorder(Region r) {
        //r.setBorder(new Border(BorderStroke.THICK));

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
