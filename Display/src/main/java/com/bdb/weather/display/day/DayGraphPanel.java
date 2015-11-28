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
package com.bdb.weather.display.day;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.LocalDate;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import com.bdb.weather.common.DailyRecords;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.WeatherAverage;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.display.ComponentContainer;
import com.bdb.weather.display.windrose.WindRosePane;

/**
 * Main container class for all plots that display a day's worth of historical data.
 * 
 * @author Bruce
 *
 */
public class DayGraphPanel extends GridPane implements ComponentContainer {
    private final JComponent       component;
    private final DayHumidityPanel humidityPanel;
    private final DayRainPanel     rainPanel;
    private final DayXYPlotPanel   temperaturePanel;
    private final DayXYPlotPanel   pressurePanel;
    private final DayWindPanel     windPanel;
    private final WindDirPlot      windDirPanel;
    private final WindRosePane    windRose;

    /**
     * Constructor.
     * 
     * @param ws The weather station for which this graph displays data
     */
    public DayGraphPanel(WeatherStation ws) {
        component = new JPanel();
        windRose = new WindRosePane();
        temperaturePanel = new DayTemperaturePlot();
        humidityPanel = new DayHumidityPanel();
        pressurePanel = new DayPressurePanel();
        rainPanel = new DayRainPanel();
        windPanel = DayWindPanel.createDayWindPanel();
        windDirPanel = new WindDirPlot();

        component.setBackground(Color.BLACK);
        component.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = .5;
        gbc.weighty = 1.0;
        component.add(temperaturePanel gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        component.add(humidityPanel, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        component.add(rainPanel.getComponent(), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        component.add(pressurePanel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        component.add(windPanel.getComponent(), gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 1;
        gbc.weightx = .25;
        component.add(windDirPanel, gbc);
        gbc.gridx = 2;
        component.add(windRose.getComponent(), gbc);
    }

    /**
     * Get the Swing container component for this set of plots.
     * 
     * @return The Swing container
     */
    @Override
    public Node getComponent() {
        return this;
    }

    /**
     * Load the data into the sub-plots.
     * 
     * @param date
     * @param list The list of data to load
     * @param summaryRecord The summary record for the day (may be null)
     * @param records The weather records for this day
     * @param averages The averages for this day
     */
    public void loadData(LocalDate date, List<HistoricalRecord> list, SummaryRecord summaryRecord, DailyRecords records, WeatherAverage averages) {
        //
        // Right after midnight the summary record might be null
        //
        if (summaryRecord != null)
            windRose.loadData(summaryRecord.getWindRoseData());
        else
            windRose.loadData(null);
        
        temperaturePanel.loadData(date, list, summaryRecord, records, averages);
        pressurePanel.loadData(date, list, summaryRecord, records, averages);
        windPanel.loadData(date, list, summaryRecord, records, averages);
        humidityPanel.loadData(date, list, summaryRecord, records, averages);
        rainPanel.loadData(summaryRecord, list);
        windDirPanel.loadData(list);
    }
}
