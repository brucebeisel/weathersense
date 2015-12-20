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
package com.bdb.weather.display.stripchart;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.db.HistoryTable;
import com.bdb.weather.display.CurrentWeatherProcessor;

/**
 *
 * @author Bruce
 */
public class StripChartPane extends BorderPane implements CurrentWeatherProcessor {
    private static final int MAX_HOURS = 72;
    private static final String LEFT_AXIS_ACTION_CMD = "left";
    private static final String RIGHT_AXIS_ACTION_CMD = "right";
    private static final String[] TEMPERATURE_DATASETS = {
        "Outdoor Temperature", "Indoor Temperature", "Heat Index", "Wind Chill", "Dew Point" // Dependent on sensors available
    };
    private static final String[] HUMIDITY_DATASETS = {
        "Outdoor Humidity", "Indoor Humidity"
    };
    private static final String[] WIND_DATASETS = {
        "Wind"
    };
    private static final String[] RAINFALL_DATASETS = {
        "Rainfall Hour", "Rainfall Today", "Rainfall Rate"
    };
    private static final String[] PRESSURE_DATASETS = {
        "Barometric Pressure"
    };
    private static final String[] SOLAR_RADIATION_DATASETS = {
        "Solar Radiation"
    };
    private static final String[] UV_INDEX_DATASETS = {
        "UV Index"
    };
    private static final String[] NONE_DATASETS = {};
    @FXML private FlowPane seriesControlPane;
    @FXML private StripChart stripChart;
    private final MeasurementType leftAxisType;
    private final MeasurementType rightAxisType;
    private final HistoryTable historyTable;
    private int span = 1;
    private final List<HistoricalRecord> initialData = new ArrayList<>();

    @SuppressWarnings("LeakingThisInConstructor")
    public StripChartPane(DBConnection con, MeasurementType leftAxisType, MeasurementType rightAxisType) {
        historyTable = new HistoryTable(con);
        this.leftAxisType = leftAxisType;
        this.rightAxisType = rightAxisType;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/StripChart.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        }
	catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        if (leftAxisType != MeasurementType.NONE) {
            stripChart.setLeftAxis(leftAxisType);
            createDatasetCheckBoxes(leftAxisType, true);
        }

        if (rightAxisType != MeasurementType.NONE) {
            stripChart.setRightAxis(rightAxisType);
            createDatasetCheckBoxes(rightAxisType, false);
        }
    }
    /*
        //setBackground(Color.BLUE);
 
        createOptionPanelComponents();
        createNewStripChartPanelComponents();
        setTop(optionsPanel);
        setCenter(stripChartContainer);
        setBottom(newStripChartPanel);
        rightAxisCB.setSelectedIndex(1);
        hoursCB.setOnAction((actionEvent) -> {
            int localSpan = Integer.parseInt(HOUR_CHOICES[hoursCB.getSelectionModel().getSelectedIndex()]);
            changeStripChartSpan(span);
            span = localSpan;
        });
    }

    private void createOptionPanelComponents() {
        //optionsPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
        optionsPanel.getChildren().add(new Label("Hours to Display:"));
        optionsPanel.getChildren().add(hoursCB);
        optionsPanel.getChildren().add(new Label("Saved Layouts:"));
        ComboBox<String> savedLayoutsCB = new ComboBox<String>(layoutManager.getSavedLayoutNames());
        optionsPanel.getChildren().add(savedLayoutsCB);
        Button b = new Button("Load Layout");
        optionsPanel.getChildren().add(b);
        b = new Button("Save Layout");
        optionsPanel.getChildren().add(b);
    }

    private void createNewStripChartPanelComponents() {
        //newStripChartPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
        newStripChartPanel.getChildren().add(new Label("Select Axis Types"));
        newStripChartPanel.getChildren().add(new LabeledFieldPanel<>("Left Axis:", leftAxisCB));
        newStripChartPanel.getChildren().add(new LabeledFieldPanel<>("Right Axis:", rightAxisCB));
        Button addButton = new Button("Add Chart");
        newStripChartPanel.getChildren().add(addButton);
        addButton.setOnAction((e) -> {
            stripChartContainer.addStripChart((MeasurementType) leftAxisCB.getSelectedItem(),
                                              (MeasurementType) rightAxisCB.getSelectedItem(),
                                              retrieveInitialData(),
                                              span, MAX_HOURS);
        });

        leftAxisCB.addActionListener((ActionEvent e) -> {
            keepAxisSelectionsDifferent(leftAxisCB, rightAxisCB);
        });

        rightAxisCB.addActionListener((ActionEvent e) -> {
            keepAxisSelectionsDifferent(rightAxisCB, leftAxisCB);
        });
    }

    private void keepAxisSelectionsDifferent(JComboBox one, JComboBox two) {
        if (one.getSelectedIndex() == two.getSelectedIndex()) {
            int idx = 0;
            if (one.getSelectedIndex() == idx) {
                idx = 1;
            }

            two.setSelectedIndex(idx);
        }
    }

    */
    /**
     * Create the menu that is used to show and hide series of a dataset, there is one menu per dataset.
     * 
     * @param axisType The measurement type for which the menu is created
     * @param leftAxis Whether the menu is for the dataset registered with the left or right axis
     * @return The menu
     */
    private void createDatasetCheckBoxes(MeasurementType axisType, boolean leftAxis) {
        String[] datasetNames = null;
        switch (axisType) {
            case TEMPERATURE:
                datasetNames = TEMPERATURE_DATASETS;
                break;
            case HUMIDITY:
                datasetNames = HUMIDITY_DATASETS;
                break;
            case WIND:
                datasetNames = WIND_DATASETS;
                break;
            case RAINFALL:
                datasetNames = RAINFALL_DATASETS;
                break;
            case PRESSURE:
                datasetNames = PRESSURE_DATASETS;
                break;
            case SOLAR_RADIATION:
                datasetNames = SOLAR_RADIATION_DATASETS;
                break;
            case UV_INDEX:
                datasetNames = UV_INDEX_DATASETS;
                break;
        }

        for (String label : datasetNames) {
            CheckBox item = new CheckBox(label);
            item.setSelected(true);
            item.setUserData(leftAxis ? LEFT_AXIS_ACTION_CMD : RIGHT_AXIS_ACTION_CMD);
            seriesControlPane.getChildren().add(item);
            item.setOnAction((actionEvent) -> {
                CheckBox item1 = (CheckBox)actionEvent.getSource();
                if (item1.isSelected())
                    stripChart.showSeries(item1.getText(), ((String)item1.getUserData()).equals(LEFT_AXIS_ACTION_CMD));
                else
                    stripChart.hideSeries(item1.getText(), ((String)item1.getUserData()).equals(LEFT_AXIS_ACTION_CMD));
            });
            stripChart.addSeries(label, leftAxis, Color.BLACK);
            stripChart.showSeries(label, leftAxis);
        }
    }

    private void changeStripChartSpan(int span) {
        stripChart.setSpanHours(span);
        this.span = span;
    }

    @Override
    public void updateCurrentWeather(CurrentWeather cw) {
        updateDatasets(cw, leftAxisType);
        updateDatasets(cw, rightAxisType);
    }

    /**
     * Update the datasets for the specified measurement type with the specified current weather.
     * 
     * @param cw The current weather
     * @param axisType The measurement type to update
     */
    private void updateDatasets(CurrentWeather cw, MeasurementType axisType) {
        double values[] = new double[10]; // ugly, should be based on some maxium
        String[] datasetNames;
        switch (axisType) {
            case TEMPERATURE:
                datasetNames = TEMPERATURE_DATASETS;
                values[0] = cw.getOutdoorTemperature().get();
                values[1] = cw.getIndoorTemperature().get();
                values[2] = cw.getHeatIndex().get();
                values[3] = cw.getWindChill().get();
                values[4] = cw.getDewPoint().get();
                break;
            case HUMIDITY:
                datasetNames = HUMIDITY_DATASETS;
                values[0] = cw.getOutdoorHumidity().get();
                values[1] = cw.getIndoorHumidity().get();
                break;
            case WIND:
                datasetNames = WIND_DATASETS;
                values[0] = cw.getWind().getSpeed().get();
                break;
            case RAINFALL:
                datasetNames = RAINFALL_DATASETS;
                values[0] = cw.getRainHour().get();
                values[1] = cw.getRainToday().get();
                values[2] = cw.getRainRate().get();
                break;
            case PRESSURE:
                datasetNames = PRESSURE_DATASETS;
                values[0] = cw.getBaroPressure().get();
                break;
            case SOLAR_RADIATION:
                datasetNames = SOLAR_RADIATION_DATASETS;
                values[0] = cw.getSolarRadiation().get();
                break;
            case UV_INDEX:
                datasetNames = UV_INDEX_DATASETS;
                values[0] = cw.getUvIndex().getIndex();
                break;
            case NONE:
                datasetNames = NONE_DATASETS;
                break;
            default:
                datasetNames = NONE_DATASETS;
                break;
        }

        LocalDateTime time = cw.getTime();
        for (int i = 0; i < datasetNames.length; i++) {
            stripChart.addItem(datasetNames[i], time, values[i]);
        }
    }

    /**
     * Get the initial data to load the strip chart, it always loads the initial data with
     * the maximum amount of time the graph supports.
     * 
     * @return The list of records
     */
    public List<HistoricalRecord> retrieveInitialData() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start;
        if (initialData.isEmpty()) {
            start = now.minusHours(MAX_HOURS);
        }
        else {
            HistoricalRecord rec = initialData.get(initialData.size() - 1);
            start = rec.getTime().plusSeconds(1);
        }
        initialData.addAll(historyTable.queryRecordsForTimePeriod(start, now));
        return initialData;
    }
}