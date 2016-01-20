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

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.layout.BorderPane;

import com.bdb.util.DateButton;
import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.DailyRecords;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.TemperatureBinMgr;
import com.bdb.weather.common.WeatherAverage;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.db.DailyAveragesTable;
import com.bdb.weather.common.db.DailyRecordsTable;
import com.bdb.weather.common.db.DailySummaryTable;
import com.bdb.weather.common.db.HistoryTable;
import com.bdb.weather.display.DisplayConstants;
import com.bdb.weather.display.StageUtilities;
import com.bdb.weather.display.WeatherSense;

/**
 * Panel that displays a day's worth of data and handles the panel for advancing through the days or
 * choosing a specific date.
 * 
 * @author Bruce
 * TODO This name is confusing with the other summary graphs. Should change the name.
 */
public class DaySummaryGraphPane extends BorderPane {
    @FXML private DayGraphPane        graphPanel;
    @FXML private DatePicker          datePicker;
    private final DailySummaryTable   dailySummaryTable;
    private final DailyAveragesTable  dailyAveragesTable;
    private final DailyRecordsTable   dailyRecordsTable;
    private final HistoryTable        historyTable;
    private final WeatherStation      ws;
    private LocalDate                 date;
    private final TemperatureBinMgr   temperatureBinMgr;

    /**
     * Constructor.
     * 
     * @param ws The weather station for which the data is being displayed
     * @param connection The database connection
     * @param day The day whose data is being displayed
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public DaySummaryGraphPane(WeatherStation ws, DBConnection connection, LocalDate day) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DaySummaryGraphPane.class.getResource("/fxml/DayPlots.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        }
	catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.ws = ws;
        this.date = day;
        datePicker.setValue(date);
        dailySummaryTable = new DailySummaryTable(connection);
        historyTable = new HistoryTable(connection);
        dailyAveragesTable = new DailyAveragesTable(connection);
        dailyRecordsTable = new DailyRecordsTable(connection);
        temperatureBinMgr = new TemperatureBinMgr(connection);

        datePicker.setOnAction((event) -> {
            this.date = datePicker.getValue();
            loadData();
        });
    }
    
    /**
     * Set the title of the Frame.
     */
    public void setTitle() {
        String dateString = DisplayConstants.formatDate(date);
        StageUtilities.setStageTitle(this, dateString);
    }

    /**
     * Load the data into the plots.
     */
    public void loadData() {
        setTitle();
        List<HistoricalRecord> list = historyTable.queryRecordsForDay(date);

        temperatureBinMgr.refresh();
        SummaryRecord summaryRecord = dailySummaryTable.retrieveSummaryForDate(date, ws.getWindParameters(), temperatureBinMgr);
        DailyRecords records = dailyRecordsTable.retrieveRecordForDay(date);
        WeatherAverage averages = dailyAveragesTable.retrieveAveragesForDay(ws.getLocationCode(), date);
        graphPanel.loadData(date, list, summaryRecord, records, averages, ws.getGeographicLocation());
    }

    @FXML
    public void previousDay(ActionEvent event) {
        date = date.minusDays(1);
        datePicker.setValue(date);

    }

    @FXML
    public void nextDay(ActionEvent event) {
        date = date.plusDays(1);
        datePicker.setValue(date);
    }
}