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
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

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
    private static final int TEMPERATURE_FIELD_LENGTH = 5;
    private static final int PRESSURE_FIELD_LENGTH = 5;
    private static final int HUMIDITY_FIELD_LENGTH = 4;
    private static final int RAIN_FIELD_LENGTH = 6;
    private final JTextField   minLowTempValue = new JTextField("0.0", TEMPERATURE_FIELD_LENGTH);
    private final JTextField   minLowTempTime = new JTextField(DATETIME_FIELD_LENGTH);
    private final JTextField   maxLowTempValue = new JTextField("0.0", TEMPERATURE_FIELD_LENGTH);
    private final JTextField   maxLowTempTime = new JTextField(DATETIME_FIELD_LENGTH);
    private final JTextField   avgLowTemp = new JTextField(TEMPERATURE_FIELD_LENGTH);
    private final JTextField   seasonalAvgLowTemp = new JTextField(TEMPERATURE_FIELD_LENGTH);
    private final JTextField   daysLowBelowNormal = new JTextField(7);
    private final JTextField   minHighTempValue = new JTextField("0.0", TEMPERATURE_FIELD_LENGTH);
    private final JTextField   minHighTempTime = new JTextField(DATETIME_FIELD_LENGTH);
    private final JTextField   maxHighTempValue = new JTextField("0.0", TEMPERATURE_FIELD_LENGTH);
    private final JTextField   maxHighTempTime = new JTextField(DATETIME_FIELD_LENGTH);
    private final JTextField   avgHighTemp = new JTextField(TEMPERATURE_FIELD_LENGTH);
    private final JTextField   seasonalAvgHighTemp = new JTextField(TEMPERATURE_FIELD_LENGTH);
    private final JTextField   daysHighAboveNormal = new JTextField(7);
    private final JTextField   minMeanTempValue = new JTextField("0.0", TEMPERATURE_FIELD_LENGTH);
    private final JTextField   minMeanTempDate = new JTextField(DATETIME_FIELD_LENGTH);
    private final JTextField   maxMeanTempValue = new JTextField("0.0", TEMPERATURE_FIELD_LENGTH);
    private final JTextField   maxMeanTempDate = new JTextField(DATETIME_FIELD_LENGTH);
    private final JTextField   avgMeanTemp = new JTextField(TEMPERATURE_FIELD_LENGTH);
    private final JTextField   seasonalAvgMeanTemp = new JTextField(TEMPERATURE_FIELD_LENGTH);
    private final JTextField   daysMeanAboveNormal = new JTextField(7);
    private final JTextField   largestTempRange = new JTextField("0.0", TEMPERATURE_FIELD_LENGTH * 3);
    private final JTextField   largestTempRangeDate = new JTextField(DATE_FIELD_LENGTH);
    private final JTextField   smallestTempRange = new JTextField("0.0", TEMPERATURE_FIELD_LENGTH * 3);
    private final JTextField   smallestTempRangeDate = new JTextField(DATE_FIELD_LENGTH);
    private final TemperatureBinSummaryPlot temperatureBinSummaryPlot = new TemperatureBinSummaryPlot();
    private final JTextField   totalRain = new JTextField("0.00", RAIN_FIELD_LENGTH);
    private final JTextField   maxRainRate = new JTextField("0.00/hr", RAIN_FIELD_LENGTH + "hour".length());
    private final JTextField   maxRainRateTime = new JTextField(DATETIME_FIELD_LENGTH);
    private final Border       innerBorder = new BevelBorder(BevelBorder.LOWERED);
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
    private static final String[] COLUMN_NAMES = {
        "Date", "Record", "Type", "Previous Record Date", "Previous Record"
    };
    
    public DailySummariesTextPanel() {
        super();
        
        minLowTempValue.setEditable(false);
        minLowTempTime.setEditable(false);
        maxLowTempValue.setEditable(false);
        maxLowTempTime.setEditable(false);
        avgLowTemp.setEditable(false);
        seasonalAvgLowTemp.setEditable(false);
        daysLowBelowNormal.setEditable(false); 
        minHighTempValue.setEditable(false);
        minHighTempTime.setEditable(false);
        maxHighTempValue.setEditable(false);
        maxHighTempTime.setEditable(false);
        avgHighTemp.setEditable(false);
        seasonalAvgHighTemp.setEditable(false);
        daysHighAboveNormal.setEditable(false);
        minMeanTempValue.setEditable(false);
        minMeanTempDate.setEditable(false);
        maxMeanTempValue.setEditable(false);
        maxMeanTempDate.setEditable(false);
        avgMeanTemp.setEditable(false);
        seasonalAvgMeanTemp.setEditable(false);
        daysMeanAboveNormal.setEditable(false);
        largestTempRange.setEditable(false);
        largestTempRangeDate.setEditable(false);
        smallestTempRange.setEditable(false);
        smallestTempRangeDate.setEditable(false);
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
        
        /*
        JPanel temperaturePanel = new JPanel(new BorderLayout());
        temperaturePanel.setBorder(new TitledBorder(innerBorder, "Temperature (" + Temperature.getDefaultUnit() + ")"));
        
        JPanel tempTextPanel = new JPanel();
        tempTextPanel.setLayout(new BoxLayout(tempTextPanel, BoxLayout.Y_AXIS));
        
        JPanel extremesPanel = new JPanel();
        extremesPanel.setLayout(new BoxLayout(extremesPanel, BoxLayout.Y_AXIS));
        extremesPanel.setBorder(new TitledBorder(innerBorder, "Extremes"));
        
        JPanel lowTemperaturePanel = new JPanel(new GridBagLayout());
        lowTemperaturePanel.setBorder(new TitledBorder(innerBorder, "Low"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        lowTemperaturePanel.add(new JBoldLabel("Minimum:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        lowTemperaturePanel.add(minLowTempValue, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.CENTER;
        lowTemperaturePanel.add(new JLabel("at"), gbc);
        gbc.gridx++;
        lowTemperaturePanel.add(minLowTempTime, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        lowTemperaturePanel.add(new JBoldLabel("Maximum:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        lowTemperaturePanel.add(maxLowTempValue, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.CENTER;
        lowTemperaturePanel.add(new JLabel("at"), gbc);
        gbc.gridx++;
        lowTemperaturePanel.add(maxLowTempTime, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        lowTemperaturePanel.add(new JBoldLabel("Average:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        lowTemperaturePanel.add(avgLowTemp, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        lowTemperaturePanel.add(new JBoldLabel("Seasonal:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        lowTemperaturePanel.add(seasonalAvgLowTemp, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        lowTemperaturePanel.add(new JBoldLabel("Days Below Normal:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        gbc.gridwidth = 2;
        lowTemperaturePanel.add(daysLowBelowNormal, gbc);
        
        extremesPanel.add(lowTemperaturePanel);
        
        JPanel meanTemperaturePanel = new JPanel(new GridBagLayout());
        meanTemperaturePanel.setBorder(new TitledBorder(innerBorder, "Mean"));
      
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        meanTemperaturePanel.add(new JBoldLabel("Minimum:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        meanTemperaturePanel.add(minMeanTempValue, gbc);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx++;
        meanTemperaturePanel.add(new JLabel("on"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        meanTemperaturePanel.add(minMeanTempDate, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        meanTemperaturePanel.add(new JBoldLabel("Maximum:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        meanTemperaturePanel.add(maxMeanTempValue, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.CENTER;
        meanTemperaturePanel.add(new JLabel("on"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        meanTemperaturePanel.add(maxMeanTempDate, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        meanTemperaturePanel.add(new JBoldLabel("Average:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        meanTemperaturePanel.add(avgMeanTemp, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        meanTemperaturePanel.add(new JBoldLabel("Seasonal:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        meanTemperaturePanel.add(seasonalAvgMeanTemp, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        meanTemperaturePanel.add(new JBoldLabel("Days Above Normal"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        gbc.gridwidth = 2;
        meanTemperaturePanel.add(daysMeanAboveNormal, gbc);
        
        extremesPanel.add(meanTemperaturePanel);
         
        JPanel highTemperaturePanel = new JPanel(new GridBagLayout());
        highTemperaturePanel.setBorder(new TitledBorder(innerBorder, "High"));
      
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        highTemperaturePanel.add(new JBoldLabel("Minimum:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        highTemperaturePanel.add(minHighTempValue, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.CENTER;
        highTemperaturePanel.add(new JLabel("at"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        highTemperaturePanel.add(minHighTempTime, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        highTemperaturePanel.add(new JBoldLabel("Maximum:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        highTemperaturePanel.add(maxHighTempValue, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.CENTER;
        highTemperaturePanel.add(new JLabel("at"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        highTemperaturePanel.add(maxHighTempTime, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        highTemperaturePanel.add(new JBoldLabel("Average:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        highTemperaturePanel.add(avgHighTemp, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        highTemperaturePanel.add(new JBoldLabel("Seasonal:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        highTemperaturePanel.add(seasonalAvgHighTemp, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        highTemperaturePanel.add(new JBoldLabel("Days Above Normal:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        gbc.gridwidth = 2;
        highTemperaturePanel.add(daysHighAboveNormal, gbc);
        
        extremesPanel.add(highTemperaturePanel);
        
        tempTextPanel.add(extremesPanel);
        
        GridPane tempRangePanel = new GridPane();
        //tempRangePanel.setBorder(new TitledBorder(innerBorder, "Temperature Range"));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        tempRangePanel.add(new JBoldLabel("Smallest:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        tempRangePanel.add(smallestTempRange, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.CENTER;
        tempRangePanel.add(new JLabel("on"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        tempRangePanel.add(smallestTempRangeDate, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        tempRangePanel.add(new JBoldLabel("Largest:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        tempRangePanel.add(largestTempRange, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.CENTER;
        tempRangePanel.add(new JLabel("on"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        tempRangePanel.add(largestTempRangeDate, gbc);
        
        tempTextPanel.add(tempRangePanel);
        temperaturePanel.setRight(tempTextPanel);
        
        BorderPane temperatureBinPanel = new BorderPane();
        //temperatureBinPanel.setBorder(new TitledBorder(innerBorder, "Bins"));
        temperatureBinPanel.setCenter(temperatureBinSummaryPlot);
        
        temperaturePanel.add(temperatureBinPanel, BorderLayout.CENTER);
        
        add(temperaturePanel);
        
        BorderPane rainPanel = new BorderPane();
        //rainPanel.setBorder(new TitledBorder(innerBorder, "Rain (" + Depth.getDefaultUnit() + ")"));
        
        BorderPane rainTextOuterPanel = new BorderPane();
        BorderPane rainTextPanel = new BorderPane();
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        rainTextPanel.add(new Label("Total"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        rainTextPanel.add(totalRain, gbc);
               
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        rainTextPanel.add(new Label("Maximum Rainfall Rate:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        rainTextPanel.add(maxRainRate, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.CENTER;
        rainTextPanel.add(new Label("at"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        rainTextPanel.add(maxRainRateTime, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        rainTextPanel.add(new Label("Days of Rain:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        rainTextPanel.add(rainDays, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        rainTextPanel.add(new Label("Max Rain Day:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        rainTextPanel.add(maxRainDayDepth, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.CENTER;
        rainTextPanel.add(new Label("on"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        rainTextPanel.add(maxRainDayDate, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        rainTextPanel.add(new Label("Avg Rain/Day:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;
        rainTextPanel.add(avgRainPerDay, gbc);
        
        rainTextOuterPanel.setTop(rainTextPanel);
        rainTextOuterPanel.setCenter(new FlowPane());
        
        rainPanel.setRight(rainTextOuterPanel);
        
        for (int i = 0; i < 24; i++)
            hourRainDataset.addValue(0.0, "Rain", "" + i);
        
        JFreeChart rainChart = ChartFactory.createBarChart(null, "Hour", "", hourRainDataset, PlotOrientation.VERTICAL, false, true, false);
        ChartViewer rainChartViewer = new ChartViewer(rainChart);
        rainChartViewer.setMaxHeight(10000);
        rainChartViewer.setMaxWidth(10000);
        rainChartViewer.setPrefSize(0, 200);
        ((CategoryPlot)rainChart.getPlot()).setRangeAxis(new RainRangeAxis());
        BorderPane hourlyRainPanel = new BorderPane();
        //hourlyRainPanel.setBorder(new TitleBorder(innerBorder, "Rain by Hour"));
        hourlyRainPanel.setCenter(rainChartViewer);
        
        rainPanel.setCenter(hourlyRainPanel);
        
        add(rainPanel);
        
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
        DateTimeFormatter dateTime = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
        DateTimeFormatter dateOnly = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
        minLowTempValue.setText(rec.getMinOutdoorTemp().toString());
        minLowTempTime.setText(dateTime.format(rec.getMinOutdoorTempTime()));
        maxLowTempValue.setText(rec.getMaxLowOutdoorTemperature().getValue().toString());
        maxLowTempTime.setText(dateTime.format(rec.getMaxLowOutdoorTemperature().getTime()));
        minHighTempValue.setText(rec.getMinHighOutdoorTemperature().getValue().toString());
        minHighTempTime.setText(dateTime.format(rec.getMinHighOutdoorTemperature().getTime()));
        maxHighTempValue.setText(rec.getMaxOutdoorTemp().toString());
        maxHighTempTime.setText(dateTime.format(rec.getMaxOutdoorTempTime()));
        minMeanTempValue.setText(rec.getMinMeanOutdoorTemperature().getValue().toString());
        minMeanTempDate.setText(dateOnly.format(rec.getMinMeanOutdoorTemperature().getTime()));
        maxMeanTempValue.setText(rec.getMaxMeanOutdoorTemperature().getValue().toString());
        maxMeanTempDate.setText(dateOnly.format(rec.getMaxMeanOutdoorTemperature().getTime()));
        
        avgHighTemp.setText(rec.getAvgHighTemperature().toString());
        avgLowTemp.setText(rec.getAvgLowTemperature().toString());
        avgMeanTemp.setText(rec.getAvgMeanTemperature().toString());
        
        seasonalAvgLowTemp.setText(seasonalAverages.getLowTemperature().toString());
        seasonalAvgMeanTemp.setText(seasonalAverages.getMeanTemperature().toString());
        seasonalAvgHighTemp.setText(seasonalAverages.getHighTemperature().toString());

        smallestTempRange.setText("" + rec.getSmallestTemperatureRange().getRange() +
                                   " (" + rec.getSmallestTemperatureRange().getMin() +
                                   ", " + rec.getSmallestTemperatureRange().getMax() + ")");
        smallestTempRangeDate.setText(dateOnly.format(rec.getSmallestTemperatureRange().getDate()));
        largestTempRange.setText("" + rec.getLargestTemperatureRange().getRange() +
                " (" + rec.getLargestTemperatureRange().getMin() +
                ", " + rec.getLargestTemperatureRange().getMax() + ")");
        largestTempRangeDate.setText(dateOnly.format(rec.getLargestTemperatureRange().getDate()));
        
        daysHighAboveNormal.setText("" + rec.getDaysHighAboveNormal() + " of " + rec.getNumberOfDays());
        daysMeanAboveNormal.setText("" + rec.getDaysMeanAboveNormal() + " of " + rec.getNumberOfDays());
        daysLowBelowNormal.setText("" + rec.getDaysLowBelowNormal() + " of " + rec.getNumberOfDays());
        
        temperatureBinSummaryPlot.loadData(rec.getTemperatureBinData());

        totalRain.setText(rec.getTotalRainfall().toString());
        //
        // This can be null if the data was collected before 2.2 or there was no rain the entire time period
        //
        Depth maxRainfallRate = rec.getMaxRainfallRate();
        if (maxRainfallRate != null && maxRainfallRate.get() != 0.0) {
            maxRainRate.setText("" + rec.getMaxRainfallRate() + "/hour");
            maxRainRateTime.setText(dateTime.format(rec.getMaxRainfallRateTime()));
        }
        else {
            maxRainRate.setText(DisplayConstants.UNKNOWN_VALUE_STRING);
            maxRainRateTime.setText(DisplayConstants.UNKNOWN_VALUE_STRING);
        }
        rainDays.setText("" + rec.getRainDays());
        maxRainDayDepth.setText(rec.getMaxDayRainDepth().toString());
        maxRainDayDate.setText(dateOnly.format(rec.getMaxDayRainDate()));
        avgRainPerDay.setText(rec.getAvgRainPerDay().toString());
        DayHourRain hourlyRain = rec.getHourlyRainfall();
        hourlyRain.getHourValues().stream().forEach((hour) -> {
            hourRainDataset.setValue(hourlyRain.getRain(hour).get(), "Rain", "" + hour);
        });
        
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
    }
}
