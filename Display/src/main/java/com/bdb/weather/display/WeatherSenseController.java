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


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.fxml.FXML;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.CurrentWeatherSubscriber;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.display.current.CurrentWeatherCharts;
import com.bdb.weather.display.current.CurrentWeatherForm;
import com.bdb.weather.display.day.DaySummaryGraphPanel;
import com.bdb.weather.display.day.TodayGraphPanel;
import com.bdb.weather.display.preferences.UnitsPreferenceDialog;
import com.bdb.weather.display.stripchart.MeasurementType;
import com.bdb.weather.display.stripchart.StripChartManager;
import com.bdb.weather.display.stripchart.StripChartPane;

/**
 * FXML Controller class
 *
 * @author bruce
 */
public class WeatherSenseController implements CurrentWeatherSubscriber.CurrentWeatherHandler {
    private WeatherStation ws;
    private DBConnection connection;
    private final CurrentWeatherSubscriber subscriber;
    private final List<CurrentWeatherProcessor> cwpList = new ArrayList<>();
    private static final Logger logger = Logger.getLogger(WeatherSenseController.class.getName());

    @SuppressWarnings("LeakingThisInConstructor")
    public WeatherSenseController() {
        subscriber = CurrentWeatherSubscriber.createSubscriber(this);
    }

    public void setData(WeatherStation ws, DBConnection connection) {
	this.ws = ws;
	this.connection = connection;
    }

    public void stop() {
	subscriber.requestExit();
    }

    @Override
    public void handleCurrentWeather(CurrentWeather currentWeather) {
        final CurrentWeather curWeather = currentWeather;
        logger.fine(String.format("Updating %s current weather processors", cwpList.size()));
        
        WeatherDataMgr.getInstance().fillInCurrentWeather(curWeather);
        cwpList.forEach((cwp) -> Platform.runLater(() -> cwp.updateCurrentWeather(curWeather)));
    }

    private Stage launchStage(Parent root, String title) {
	Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
        return stage;
    }

    @FXML
    public void launchCurrentWeatherFormView() {
	CurrentWeatherForm form = new CurrentWeatherForm(ws);
        Stage stage = launchStage(form, "Current Weather");
	cwpList.add(form);
        stage.setOnCloseRequest((handler) -> cwpList.remove(form));
    }

    @FXML
    public void launchCurrentWeatherView() {
	CurrentWeatherCharts cwCharts = new CurrentWeatherCharts(ws, connection);
	Stage stage = launchStage(cwCharts, "Current Weather");
	cwpList.add(cwCharts);
        stage.setOnCloseRequest((handler) -> cwpList.remove(cwCharts));
    }

    @FXML
    public void launchTodayView() {
        TodayGraphPanel todayGraphPanel = new TodayGraphPanel(connection);
	launchStage(todayGraphPanel, "Today Weather");
        todayGraphPanel.refresh();
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
        DaySummaryGraphPanel graphPanel = new DaySummaryGraphPanel(ws, connection, LocalDate.now());
	launchStage(graphPanel, "Weather");
        graphPanel.loadData();
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
        StripChartManager stripChartManager = new StripChartManager();
	launchStage(stripChartManager, "Strip Chart Manager");
    }

    @FXML
    public void launchRainStripChart() {
        StripChartPane stripChartPane = new StripChartPane(connection, MeasurementType.NONE, MeasurementType.RAINFALL);
	Stage stage = launchStage(stripChartPane, "Strip Chart - Rain");
	cwpList.add(stripChartPane);
        stage.setOnCloseRequest((handler) -> cwpList.remove(stripChartPane));
    }

    @FXML
    public void launchRainAndWindStripChart() {
        StripChartPane stripChartPane = new StripChartPane(connection, MeasurementType.WIND, MeasurementType.RAINFALL);
	Stage stage = launchStage(stripChartPane, "Strip Chart - Wind and Rain");
	cwpList.add(stripChartPane);
        stage.setOnCloseRequest((handler) -> cwpList.remove(stripChartPane));
    }

    @FXML
    public void launchTemperatureStripChart() {
        StripChartPane stripChartPane = new StripChartPane(connection, MeasurementType.NONE, MeasurementType.TEMPERATURE);
	Stage stage = launchStage(stripChartPane, "Strip Chart - Temperature");
	cwpList.add(stripChartPane);
        stage.setOnCloseRequest((handler) -> cwpList.remove(stripChartPane));
    }

    @FXML
    public void launchColorPreference() {
    }

    @FXML
    public void launchUnitsPreference() {
        UnitsPreferenceDialog dialog = new UnitsPreferenceDialog();
        launchStage(dialog, "Units Preferences");

    }

    @FXML
    public void launchWeatherStation() {
        WeatherStationMgr.editWeatherStation();
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

    /*
    private JInternalFrame launchView(String title, ComponentContainer container, Dimension geometry, boolean maximize) {
        return null;
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
*/
}
