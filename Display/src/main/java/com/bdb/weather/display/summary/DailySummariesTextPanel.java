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
package com.bdb.weather.display.summary;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import org.jfree.data.category.DefaultCategoryDataset;

import com.bdb.weather.common.DayHourRain;
import com.bdb.weather.common.Extreme;
import com.bdb.weather.common.Statistics;
import com.bdb.weather.common.TemperatureRecordType;
import com.bdb.weather.common.WeatherAverage;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Temperature;
import com.bdb.weather.display.DisplayConstants;
/**
 * Need fields for:
 * Temperatures:
 *      Min Low, Max Low, Min High, Max High with times - DONE
 *      Avg (s), avg high, avg low - DONE
 *      Largest temperature range with day - DONE
 *      Smallest temperature range with day - DONE
 *      List of records set with values and dates
 *      number of days high is above and below normal - DONE
 *      number of days low is above and below normal - DONE
 *      Over/Under tables with HH:MM above/below, Days above/below, %days above/below
 *      Time/days below freezing, could be part of above with 32F being a special value
 * Rain - DONE
 *      Max rain day - amount and day (s)
 *      Max rain rate - rate and time (s)
 *      Number of rain days
 *      Total rain
 *      Avg per/day
 *      Rain in each hour
 * Wind
 *      Max wind with time (s) - DONE
 *      Max gust with time (s) - DONE
 *      Avg wind (s) - DONE
 *      Day with highest average (s) - DONE
 *      Direction ?
 * Pressure - DONE
 *      Max with time (s)
 *      Min with time (s)
 *      Max range with date
 *      Min range with date
 *      Avg. (s)
 * Humidity - DONE
 *      Max with time (s)
 *      Min with time (s)
 *      Max range with date
 *      Min range with date
 *      Avg. (s)
 *      
 * @author Bruce
 *
 */

@SuppressWarnings("serial")
public class DailySummariesTextPanel extends VBox {
    private static final int DATE_FIELD_LENGTH = 10;
    private static final int DATETIME_FIELD_LENGTH = 15;
    private static final int PRESSURE_FIELD_LENGTH = 5;
    private static final int HUMIDITY_FIELD_LENGTH = 4;
    private static final int RAIN_FIELD_LENGTH = 6;
    private final JTextField   totalRain = new JTextField("0.00", RAIN_FIELD_LENGTH);
    private final JTextField   maxRainRate = new JTextField("0.00/hr", RAIN_FIELD_LENGTH + "hour".length());
    private final JTextField   maxRainRateTime = new JTextField(DATETIME_FIELD_LENGTH);
    private final JTextField   rainDays = new JTextField(3);
    private final JTextField   maxRainDayDepth = new JTextField(RAIN_FIELD_LENGTH);
    private final JTextField   maxRainDayDate = new JTextField(DATE_FIELD_LENGTH);
    private final JTextField   avgRainPerDay = new JTextField(RAIN_FIELD_LENGTH);
    private final JTextField   maxWindSpeed = new JTextField(5);
    private final JTextField   maxWindSpeedTime = new JTextField(DATETIME_FIELD_LENGTH);
    private final JTextField   maxWindGust = new JTextField(5);
    private final JTextField   maxWindGustTime = new JTextField(DATETIME_FIELD_LENGTH);
    private final JTextField   avgWindSpeed = new JTextField(5);
    private final DefaultCategoryDataset hourRainDataset = new DefaultCategoryDataset();
    private final JTextField   maxAvgWindSpeed = new JTextField(5);
    private final JTextField   maxAvgWindSpeedDate = new JTextField(DATE_FIELD_LENGTH);
    private final JTextField   minPressure = new JTextField(PRESSURE_FIELD_LENGTH);
    private final JTextField   minPressureTime = new JTextField(DATETIME_FIELD_LENGTH);
    private final JTextField   maxPressure = new JTextField(PRESSURE_FIELD_LENGTH);
    private final JTextField   maxPressureTime = new JTextField(DATETIME_FIELD_LENGTH);
    private final JTextField   avgPressure = new JTextField(PRESSURE_FIELD_LENGTH);
    private final JTextField   smallestPressureRange = new JTextField(PRESSURE_FIELD_LENGTH * 3);
    private final JTextField   smallestPressureRangeDate = new JTextField(DATE_FIELD_LENGTH);
    private final JTextField   largestPressureRange = new JTextField(PRESSURE_FIELD_LENGTH * 3);
    private final JTextField   largestPressureRangeDate = new JTextField(DATE_FIELD_LENGTH);
    private final JTextField   minHumidity = new JTextField(HUMIDITY_FIELD_LENGTH);
    private final JTextField   minHumidityTime = new JTextField(DATETIME_FIELD_LENGTH);
    private final JTextField   maxHumidity = new JTextField(HUMIDITY_FIELD_LENGTH);
    private final JTextField   maxHumidityTime = new JTextField(DATETIME_FIELD_LENGTH);
    private final JTextField   avgHumidity = new JTextField(HUMIDITY_FIELD_LENGTH);
    private final JTextField   smallestHumidityRange = new JTextField(HUMIDITY_FIELD_LENGTH * 3);
    private final JTextField   smallestHumidityRangeDate = new JTextField(DATE_FIELD_LENGTH);
    private final JTextField   largestHumidityRange = new JTextField(HUMIDITY_FIELD_LENGTH * 3);
    private final JTextField   largestHumidityRangeDate = new JTextField(DATE_FIELD_LENGTH);
    private final DefaultTableModel   tableModel = new DefaultTableModel();
    private final JTable       recordTable = new JTable(tableModel);
    private final TemperatureSummaryPane temperatureSummaryPane = new TemperatureSummaryPane();
    private final RainSummaryPane rainSummaryPane = new RainSummaryPane();
    private static final String[] COLUMN_NAMES = {
        "Date", "Record", "Type", "Previous Record Date", "Previous Record"
    };
    
    public DailySummariesTextPanel() {
        super();
        
        totalRain.setEditable(false);
        maxRainRate.setEditable(false);
        maxRainRateTime.setEditable(false);
        rainDays.setEditable(false);
        maxRainDayDepth.setEditable(false);
        maxRainDayDate.setEditable(false);
        avgRainPerDay.setEditable(false);
        maxWindSpeed.setEditable(false);
        maxWindSpeedTime.setEditable(false);
        maxWindGust.setEditable(false);
        maxWindGustTime.setEditable(false);
        avgWindSpeed.setEditable(false);
        maxAvgWindSpeed.setEditable(false);
        maxAvgWindSpeedDate.setEditable(false);
        minPressure.setEditable(false);
        minPressureTime.setEditable(false);
        maxPressure.setEditable(false);
        maxPressureTime.setEditable(false);
        avgPressure.setEditable(false);
        smallestPressureRange.setEditable(false);
        smallestPressureRangeDate.setEditable(false);
        largestPressureRange.setEditable(false);
        largestPressureRangeDate.setEditable(false);
        minHumidity.setEditable(false);
        minHumidityTime.setEditable(false);
        maxHumidity.setEditable(false);
        maxHumidityTime.setEditable(false);
        avgHumidity.setEditable(false);
        smallestHumidityRange.setEditable(false);
        smallestHumidityRangeDate.setEditable(false);
        largestHumidityRange.setEditable(false);
        largestHumidityRangeDate.setEditable(false);
        
        TitledPane pane = new TitledPane();
        pane.setText("Temperature (" + Temperature.getDefaultUnit() + ")");
        pane.setCollapsible(false);
        pane.setContent(temperatureSummaryPane);
        this.getChildren().add(pane);

        pane = new TitledPane();
        pane.setText("Rain (" + Depth.getDefaultUnit() + ")");
        pane.setCollapsible(false);
        pane.setContent(rainSummaryPane);
        this.getChildren().add(pane);
        /*
        
        tempTextPanel.add(extremesPanel);
        
        BorderPane rainPanel = new BorderPane();
        
        JPanel windPressureHumidityPanel = new JPanel(new GridLayout(1,0));
        
        GridPane windPanel = new GridPane();
        //windPanel.setBorder(new TitleBorder(innerBorder, "Wind (" + Speed.getDefaultUnit() + ")"));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        windPanel.add(new Label("Maximum Speed:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        windPanel.add(maxWindSpeed, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.CENTER;
        windPanel.add(new Label("at"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        windPanel.add(maxWindSpeedTime, gbc);      
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        windPanel.add(new Label("Maximum Gust:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        windPanel.add(maxWindGust, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.CENTER;
        windPanel.add(new Label("at"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        windPanel.add(maxWindGustTime, gbc);      
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        windPanel.add(new Label("Windiest Day:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        windPanel.add(maxAvgWindSpeed, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.CENTER;
        windPanel.add(new Label("on"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        windPanel.add(maxAvgWindSpeedDate, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        windPanel.add(new Label("Average:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        windPanel.add(avgWindSpeed, gbc);
        
        windPressureHumidityPanel.add(windPanel);
        
        JPanel pressurePanel = new JPanel(new GridBagLayout());
        pressurePanel.setBorder(new TitledBorder(innerBorder, "Pressure (" + Pressure.getDefaultUnit() + ")"));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        pressurePanel.add(new Label("Minimum:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        pressurePanel.add(minPressure, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.CENTER;
        pressurePanel.add(new Label("at"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        pressurePanel.add(minPressureTime, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        pressurePanel.add(new Label("Maximum:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        pressurePanel.add(maxPressure, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.CENTER;
        pressurePanel.add(new Label("at"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        pressurePanel.add(maxPressureTime, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        pressurePanel.add(new Label("Average:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        pressurePanel.add(avgPressure, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        pressurePanel.add(new Label("Smallest Range:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        pressurePanel.add(smallestPressureRange, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.CENTER;
        pressurePanel.add(new Label("on"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        pressurePanel.add(smallestPressureRangeDate, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        pressurePanel.add(new Label("Largest Range:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        pressurePanel.add(largestPressureRange, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.CENTER;
        pressurePanel.add(new Label("on"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        pressurePanel.add(largestPressureRangeDate, gbc);
        
        windPressureHumidityPanel.add(pressurePanel);
        
        GridPane humidityPanel = new GridPane();
        //humidityPanel.setBorder(new TitledBorder(innerBorder, "Humidity (%)"));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        humidityPanel.add(new JBoldLabel("Minimum:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        humidityPanel.add(minHumidity, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.CENTER;
        humidityPanel.add(new JLabel("at"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        humidityPanel.add(minHumidityTime, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        humidityPanel.add(new JBoldLabel("Maximum:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        humidityPanel.add(maxHumidity, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.CENTER;
        humidityPanel.add(new JLabel("at"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        humidityPanel.add(maxHumidityTime, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        humidityPanel.add(new JBoldLabel("Average:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        humidityPanel.add(avgHumidity, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        humidityPanel.add(new JBoldLabel("Smallest Range:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        humidityPanel.add(smallestHumidityRange, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.CENTER;
        humidityPanel.add(new JLabel("on"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        humidityPanel.add(smallestHumidityRangeDate, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        humidityPanel.add(new JBoldLabel("Largest Range:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        humidityPanel.add(largestHumidityRange, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.CENTER;
        humidityPanel.add(new JLabel("on"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        humidityPanel.add(largestHumidityRangeDate, gbc);
        
        windPressureHumidityPanel.add(humidityPanel);
        
        add(windPressureHumidityPanel);
        
        DefaultTableColumnModel colModel = new DefaultTableColumnModel();
        recordTable.setColumnModel(colModel);

        recordTable.setAutoCreateColumnsFromModel(false);

        
        for (int i = 0; i < COLUMN_NAMES.length; i++) {
            TableColumn col = new TableColumn();
            col.setHeaderValue(COLUMN_NAMES[i]);
            col.setModelIndex(i);
            colModel.addColumn(col);
        }

        tableModel.setColumnCount(COLUMN_NAMES.length);
        
        JPanel recordsPanel = new JPanel(new BorderLayout());
        recordsPanel.setBorder(new TitledBorder(innerBorder, "Records"));
        recordsPanel.add(new JScrollPane(recordTable), BorderLayout.CENTER);
        
        add(recordsPanel);
*/
    }
    
    // TODO Get the simple date formatter from a global class, like one that contains some sort of preferences
    public void loadData(Statistics rec, WeatherAverage seasonalAverages, List<Extreme<Temperature,TemperatureRecordType>> records) {
        temperatureSummaryPane.loadData(rec, seasonalAverages);
        rainSummaryPane.loadData(rec);

        DateTimeFormatter dateTime = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
        DateTimeFormatter dateOnly = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

        
        
        maxWindSpeed.setText("" + rec.getMaxWindSpeed());
        maxWindSpeedTime.setText(dateTime.format(rec.getMaxWindSpeedTime()));
        maxWindGust.setText("" + rec.getMaxWindGust());
        maxWindGustTime.setText(dateTime.format(rec.getMaxWindGustTime()));
        avgWindSpeed.setText("" + rec.getAvgWindSpeed());
        maxAvgWindSpeed.setText("" + rec.getMaxAvgWindSpeed().getValue());
        maxAvgWindSpeedDate.setText(dateOnly.format(rec.getMaxAvgWindSpeed().getTime()));
        
        minPressure.setText(rec.getMinBaroPressure().toString());
        minPressureTime.setText(dateTime.format(rec.getMinBaroPressureTime()));
        maxPressure.setText(rec.getMaxBaroPressure().toString());
        maxPressureTime.setText(dateTime.format(rec.getMaxPressureTime()));
        avgPressure.setText(rec.getAvgBaroPressure().toString());
        
        smallestPressureRange.setText("" + rec.getSmallestPressureRange().getRange() +
                " (" + rec.getSmallestPressureRange().getMin() +
                ", " + rec.getSmallestPressureRange().getMax() + ")");
        smallestPressureRangeDate.setText(dateOnly.format(rec.getSmallestPressureRange().getDate()));
        largestPressureRange.setText("" + rec.getLargestPressureRange().getRange() +
                " (" + rec.getLargestPressureRange().getMin() +
                ", " + rec.getLargestPressureRange().getMax() + ")");
        largestPressureRangeDate.setText(dateOnly.format(rec.getLargestPressureRange().getDate()));
        
        minHumidity.setText(rec.getMinOutdoorHumidity().toString());
        minHumidityTime.setText(dateTime.format(rec.getMinOutdoorHumidityTime()));
        maxHumidity.setText(rec.getMaxOutdoorHumidity().toString());
        maxHumidityTime.setText(dateTime.format(rec.getMaxOutdoorHumidityTime()));
        avgHumidity.setText(rec.getAvgOutdoorHumidity().toString());
        
        smallestHumidityRange.setText("" + rec.getSmallestHumidityRange().getRange() +
                " (" + rec.getSmallestHumidityRange().getMin() +
                ", " + rec.getSmallestHumidityRange().getMax() + ")");
        smallestHumidityRangeDate.setText(dateOnly.format(rec.getSmallestHumidityRange().getDate()));
        largestHumidityRange.setText("" + rec.getLargestHumidityRange().getRange() +
                " (" + rec.getLargestHumidityRange().getMin() +
                ", " + rec.getLargestHumidityRange().getMax() + ")");
        largestHumidityRangeDate.setText(dateOnly.format(rec.getLargestHumidityRange().getDate()));
        
        tableModel.setNumRows(records.size());
        
        /*
        int n = 0;
        for (Extreme<Temperature,TemperatureRecordType> record : records) {
            tableModel.setValueAt(dateOnly.format(record.getDate()), n, 0);
            Temperature t = record.getValue();
            String val = t.toString();
            tableModel.setValueAt(val, n, 1);
            tableModel.setValueAt(record.getType(), n, 2);
            tableModel.setValueAt(dateOnly.format(record.getPreviousDate()), n, 3);
            tableModel.setValueAt(record.getPreviousValue(), n++, 4);
        }
*/
    }
}
