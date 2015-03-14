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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.LineBorder;

import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.HistoricalRecord;

/**
 * Class to control a strip chart
 * 
 * @author Bruce
 */
public class StripChartController extends JPanel {
    private final String name;
    private final StripChart chart;
    private final MeasurementType leftAxisType;
    private final MeasurementType rightAxisType;
    private JMenu rightAxisMenu;
    private JMenu leftAxisMenu;
    private final StripChartManager manager;
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
    private static final String LEFT_AXIS_ACTION_CMD = "left";
    private static final String RIGHT_AXIS_ACTION_CMD = "right";

    /**
     * Constructor.
     * 
     * @param theName The name of this controller which will be used when saving the strip chart parameters
     * @param leftAxisType The measurement type mapped to the left axis
     * @param rightAxisType The measurement type mapped to the right axis
     * @param initialData A list of the initial data with which to load the datasets
     * @param spanHours The initial span of the domain axis in hours
     * @param maxHours The maximum number of hours that will ever be displayed. This is used to trim the dataset as more data is added.
     * @param theManager
     */
    public StripChartController(String theName, MeasurementType leftAxisType, MeasurementType rightAxisType, List<HistoricalRecord> initialData, int spanHours, int maxHours, StripChartManager theManager) {
        super(new BorderLayout());
        this.name = theName;
        this.leftAxisType = leftAxisType;
        this.rightAxisType = rightAxisType;
        this.manager = theManager;
        this.setBorder(new LineBorder(Color.BLACK));
        chart = new StripChart(leftAxisType, rightAxisType, spanHours, maxHours);

        JPopupMenu plotMenu = chart.getPopupMenu();
        plotMenu.add(new JPopupMenu.Separator());
        
        JMenuItem item = new JMenuItem("Remove This Chart");
        plotMenu.add(item);
        item.setActionCommand(name);
        item.addActionListener((ActionEvent e) -> {
            manager.removeStripChart(name);
        });

        if (leftAxisType != MeasurementType.NONE) {
            leftAxisMenu = createDatasetMenu(leftAxisType, StripChart.MAP_TO_LEFT_AXIS);
            plotMenu.add(leftAxisMenu);
        }

        if (rightAxisType != MeasurementType.NONE) {
            rightAxisMenu = createDatasetMenu(rightAxisType, StripChart.MAP_TO_RIGHT_AXIS);
            plotMenu.add(rightAxisMenu);
        }

        add(chart, BorderLayout.CENTER);

        loadInitialData(initialData);
    }

    /**
     * Create the menu that is used to show and hide series of a dataset, there is one menu per dataset.
     * 
     * @param axisType The measurement type for which the menu is created
     * @param leftAxis Whether the menu is for the dataset registered with the left or right axis
     * @return The menu
     */
    private JMenu createDatasetMenu(MeasurementType axisType, boolean leftAxis) {
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

        JMenu menu = new JMenu(axisType.toString() + " Datasets");
        for (String label : datasetNames) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(label);
            item.setSelected(true);
            item.setActionCommand(leftAxis ? LEFT_AXIS_ACTION_CMD : RIGHT_AXIS_ACTION_CMD);
            menu.add(item);
            item.addActionListener((ActionEvent e) -> {
                JMenuItem item1 = (JMenuItem) e.getSource();
                if (item1.isSelected())
                    chart.showSeries(item1.getText(), e.getActionCommand().equals(LEFT_AXIS_ACTION_CMD));
                else
                    chart.hideSeries(item1.getText(), e.getActionCommand().equals(LEFT_AXIS_ACTION_CMD));
            });
            chart.addSeries(label, leftAxis, Color.BLACK);
            chart.showSeries(label, leftAxis);
        }

        return menu;
    }

    /**
     * Update the strip chart with the latest current weather.
     * 
     * @param cw The latest current weather
     */
    public void updateStripChart(CurrentWeather cw) {
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
            chart.addItem(datasetNames[i], time, values[i]);
        }
    }

    /**
     * Load the initial data for all datasets.
     * 
     * @param initialData The list of historical data
     */
    private void loadInitialData(List<HistoricalRecord> initialData) {
        for (HistoricalRecord rec : initialData) {
            updateDatasets(rec, leftAxisType);
            updateDatasets(rec, rightAxisType);
        }
    }

    /**
     * Update the datasets with the historical data.
     * 
     * @param rec The historical record
     * @param axisType The measurement type to load
     */
    private void updateDatasets(HistoricalRecord rec, MeasurementType axisType) {
        Double values[] = new Double[10]; // ugly, should be based on some maximum
        String[] datasetNames = null;
        switch (axisType) {
            case TEMPERATURE:
                datasetNames = TEMPERATURE_DATASETS;
                if (rec.getAvgOutdoorTemperature() != null)
                    values[0] = rec.getAvgOutdoorTemperature().get();

                if (rec.getIndoorTemperature() != null)
                    values[1] = rec.getIndoorTemperature().get();

                if (rec.getHeatIndex() != null)
                    values[2] = rec.getHeatIndex().get();

                if (rec.getWindChill() != null)
                    values[3] = rec.getWindChill().get();

                if (rec.getDewPoint() != null)
                    values[4] = rec.getDewPoint().get();
                break;

            case HUMIDITY:
                datasetNames = HUMIDITY_DATASETS;
                if (rec.getOutdoorHumidity() != null)
                    values[0] = rec.getOutdoorHumidity().get();

                if (rec.getIndoorHumidity() != null)
                    values[1] = rec.getIndoorHumidity().get();
                break;

            case WIND:
                datasetNames = WIND_DATASETS;
                if (rec.getAvgWind() != null && rec.getAvgWind().getSpeed() != null)
                    values[0] = rec.getAvgWind().getSpeed().get();
                break;

            case RAINFALL:
                // Rainfall data does not exist in the historical record
                datasetNames = null;
                break;

            case PRESSURE:
                datasetNames = PRESSURE_DATASETS;
                if (rec.getBaroPressure() != null)
                    values[0] = rec.getBaroPressure().get();
                break;

            case SOLAR_RADIATION:
                datasetNames = SOLAR_RADIATION_DATASETS;
                if (rec.getAvgSolarRadiation() != null)
                    values[0] = rec.getAvgSolarRadiation().get();
                break;

            case UV_INDEX:
                datasetNames = UV_INDEX_DATASETS;
                values[0] = (double)rec.getAvgUvIndex();
                break;
        }

        if (datasetNames != null) {
            for (int i = 0; i < datasetNames.length; i++) {
                if (values[i] != null)
                    chart.addItem(datasetNames[i], rec.getTime(), values[i]);
            }
        }
    }

    /**
     * Set the number of hours of data that the strip chart will display
     * 
     * @param span The number of hours the domain axis will display 
     */
    public void changeStripChartSpan(int span) {
        chart.setSpanHours(span);
    }
}
