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

import javax.swing.JInternalFrame;

import javafx.fxml.FXML;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

import com.bdb.weather.common.DateRange;
import com.bdb.weather.display.current.CurrentWeatherPanel;
import com.bdb.weather.display.currenttable.CurrentWeatherText;
import com.bdb.weather.display.day.DaySummaryGraphPanel;
import com.bdb.weather.display.day.TodayGraphPanel;
import com.bdb.weather.display.freeplot.DailyFreePlot;
import com.bdb.weather.display.freeplot.HistoricalFreePlot;
import com.bdb.weather.display.freeplot.MonthlyFreePlot;
import com.bdb.weather.display.freeplot.SummaryFreePlot;
import com.bdb.weather.display.historyeditor.HistoryEditorPanel;
import com.bdb.weather.display.historytable.DayHistoryTable;
import com.bdb.weather.display.sensors.SensorPanel;
import com.bdb.weather.display.sensors.SensorStationPanel;
import com.bdb.weather.display.storm.StormPanel;
import com.bdb.weather.display.stripchart.StripChartPanel;
import com.bdb.weather.display.summary.DailySummariesPanel;
import com.bdb.weather.display.summary.MonthlySummariesPanel;
import com.bdb.weather.display.summary.YearlySummariesPanel;

import java.awt.Dimension;
import java.time.LocalDate;
import java.time.Month;

import javafx.application.Platform;

/**
 * FXML Controller class
 *
 * @author bruce
 */
public class WeatherSenseController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
	// TODO
    }

    @FXML
    public void launchCurrentWeatherTextView() {
    }

    @FXML
    public void launchCurrentWeatherView() {
    }

    @FXML
    public void launchTodayView() {
    }

    @FXML
    public void launchStormsView() {
    }

    @FXML
    public void launchArchiveTableView() {
    }

    @FXML
    public void launchArchiveEditorView() {
    }

    @FXML
    public void launchDaySummaryView() {
    }

    @FXML
    public void launchDailySummariesView() {
    }

    @FXML
    public void launchMonthlySummariesView() {
    }

    @FXML
    public void launchYearlySummariesView() {
    }

    @FXML
    public void launchHistoricalFreePlotView() {
    }

    @FXML
    public void launchDailyFreePlotView() {
    }

    @FXML
    public void launchMonthlyFreePlotView() {
    }

    @FXML
    public void launchStripChart() {
    }

    @FXML
    public void launchColorPreference() {
    }

    @FXML
    public void launchUnitsPreference() {
    }

    @FXML
    public void launchWeatherStation() {
    }

    @FXML
    public void launchSensors() {
    }

    @FXML
    public void launchSensorStations() {
    }

    @FXML
    public void launchSeasonalAverages() {
    }

    @FXML
    public void launchAbout() {
    }

    @FXML
    public void exit() {
	Platform.exit();
    }

    private JInternalFrame launchView(String title, ComponentContainer container, Dimension geometry, boolean maximize) {
        return null;
    }

    @Override
    public void launchCurrentWeatherView() {
        if (currentWeatherFrame == null) {
            ws = stationTable.getWeatherStation();
            currentWeatherPanel = new CurrentWeatherPanel(ws, connection);
            cwpList.add(currentWeatherPanel);
            currentWeatherFrame = launchView("Current Weather", currentWeatherPanel, new Dimension(850, 850), false);
            currentWeatherFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        }
        else
            currentWeatherFrame.setVisible(true);
    }

    public void launchCurrentWeatherTable() {
        ws = stationTable.getWeatherStation();
        CurrentWeatherText cwt = new CurrentWeatherText(ws);
        cwpList.add(cwt);
        launchView("Current Weather", cwt, null, false);
    }

    public void launchHistoricalTableView() {
        DayHistoryTable dayHistoryTable = new DayHistoryTable(connection);
        launchView("Day Historical Table", dayHistoryTable, null, true);
    }

    @Override
    public void launchDaySummaryView(LocalDate day) {
        ws = stationTable.getWeatherStation();
        DaySummaryGraphPanel daySummaryGraphPanel = new DaySummaryGraphPanel(ws, connection, day);
        launchView("Day Summary", daySummaryGraphPanel, new Dimension(800,600), true);
        daySummaryGraphPanel.setTitle();
    }

    private void launchDailySummariesView(LocalDate start, LocalDate end, DateInterval interval) {
        ws = stationTable.getWeatherStation();
        DailySummariesPanel summaryPanel = new DailySummariesPanel(ws, connection, this, start, end, interval);
        launchView("Daily Summary", summaryPanel, new Dimension(800,600), true);
        summaryPanel.setWindowTitle();
    }

    @Override
    public void launchStormView() {
        StormPanel stormPanel = new StormPanel(connection);
        launchView("", stormPanel, new Dimension(800,600), false);
    }

    @Override
    public void launchDailySummariesView(LocalDate start, LocalDate end) {
        launchDailySummariesView(start, end, DateInterval.CUSTOM);
    }

    @Override
    public void launchDailySummariesView(DateInterval interval) {
        DateRange range = interval.range();
        launchDailySummariesView(range.getStart().toLocalDate(), range.getEnd().toLocalDate(), interval);
    }

    @Override
    public void launchMonthlySummariesView(DateInterval interval) {
        LocalDate end = LocalDate.now();
        LocalDate start = LocalDate.of(end.getYear(), Month.JANUARY, 1);

        MonthlySummariesPanel monthlySummaryPanel = new MonthlySummariesPanel(ws, connection, this, start, end, DateInterval.THIS_YEAR);
        launchView("", monthlySummaryPanel, new Dimension(800, 600), true);
    }

    public void launchYearlySummariesView() {
        YearlySummariesPanel yearlySummaryPanel = new YearlySummariesPanel(ws, connection, this);
        launchView("", yearlySummaryPanel, new Dimension(800, 600), true);
    }

    @Override
    public void launchHistoricalFreePlotView() {
        HistoricalFreePlot freePlot = new HistoricalFreePlot(ws, connection);
        launchView(HISTORICAL_FREE_PLOT_VIEW_NAME, freePlot, new Dimension(800, 600), true);
    }

    @Override
    public void launchDailyFreePlotView() {
        SummaryFreePlot freePlot = new DailyFreePlot(ws, connection);
        launchView(DAILY_SUMMARY_FREE_PLOT_VIEW_NAME, freePlot, new Dimension(800, 600), true);
    }

    @Override
    public void launchMonthlyFreePlotView() {
        MonthlyFreePlot freePlot = new MonthlyFreePlot(ws, connection);
        launchView(MONTHLY_FREE_PLOT_VIEW_NAME, freePlot, new Dimension(800, 600), true);
    }

    @Override
    public void launchTodayView() {
        if (todayGraphPanel == null) {
            todayGraphPanel = new TodayGraphPanel(ws, connection);
            refreshList.add(todayGraphPanel);
            todayFrame = launchView("Today", todayGraphPanel, new Dimension(800, 600), true);
            todayFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
            todayGraphPanel.refresh();
        }
        else
            todayFrame.setVisible(true);
    }

    @Override
    public void launchHistoryEditor() {
        HistoryEditorPanel editor = new HistoryEditorPanel(ws, connection);
        launchView(HISTORY_EDITOR_CMD, editor, new Dimension(800, 600), true);
    }

    @Override
    public void launchStripChart() {
        StripChartPanel stripChart = new StripChartPanel(connection, null);
        launchView(STRIP_CHART_CMD, stripChart, new Dimension(800, 600), false);
        cwpList.add(stripChart);
    }

    private void launchSensorView() {
        SensorPanel sensorPanel = new SensorPanel(connection);
        launchView("", sensorPanel, new Dimension(800, 600), true);
    }

    private void launchSensorStationView() {
        SensorStationPanel sensorStationPanel = new SensorStationPanel(connection);
        sensorStationPanel.loadData();
        launchView("Sensor Stations", sensorStationPanel, new Dimension(800, 600), false);
    }
}
