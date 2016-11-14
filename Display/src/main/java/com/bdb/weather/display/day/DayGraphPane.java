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
package com.bdb.weather.display.day;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

import com.bdb.weather.common.DailyRecords;
import com.bdb.weather.common.GeographicLocation;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.WeatherAverage;
import com.bdb.weather.display.windrose.WindRosePane;

/**
 * Main container class for all plots that display a day's worth of historical data.
 * 
 * @author Bruce
 *
 */
public class DayGraphPane extends GridPane {
    @FXML private DayHumidityPane humidityPlot;
    @FXML private DayRainPane     rainPlot;
    @FXML private DayXYPlotPane   temperaturePlot;
    @FXML private DayXYPlotPane   pressurePlot;
    @FXML private DayWindPane     windPlot;
    @FXML private WindDirPane      windDirectionPlot;
    @FXML private WindRosePane     windRose;

    /**
     * Constructor.
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public DayGraphPane() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/TodayPlots.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        }
	catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Load the data into the sub-plots.
     * 
     * @param date
     * @param list The list of data to load
     * @param summaryRecord The summary record for the day (may be null)
     * @param records The weather records for this day
     * @param averages The averages for this day
     * @param location The location of this weather station
     */
    public void loadData(LocalDate date, List<HistoricalRecord> list, SummaryRecord summaryRecord, DailyRecords records, WeatherAverage averages, GeographicLocation location) {
        //
        // Right after midnight the summary record might be null
        //
        if (summaryRecord != null) {
            windRose.loadData(summaryRecord.getWindRoseData());
            rainPlot.loadData(summaryRecord.getHourlyRainfall(), list);
        }
        else
            windRose.loadData(null);
        
        temperaturePlot.loadData(date, list, summaryRecord, records, averages, location);
        pressurePlot.loadData(date, list, summaryRecord, records, averages, location);
        windPlot.loadData(date, list, summaryRecord, records, averages, location);
        humidityPlot.loadData(date, list, summaryRecord, records, averages, location);
        windDirectionPlot.loadData(list);
    }
}
