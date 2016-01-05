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
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.fxml.FXML;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.CurrentWeatherSubscriber;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.display.current.CurrentWeatherCharts;
import com.bdb.weather.display.current.CurrentWeatherForm;
import com.bdb.weather.display.day.DaySummaryGraphPane;
import com.bdb.weather.display.day.TodayGraphPane;
import com.bdb.weather.display.historytable.DayHistoryTable;
import com.bdb.weather.display.freeplot.HistoricalFreePlot;
import com.bdb.weather.display.preferences.ColorPreferencePanel;
import com.bdb.weather.display.preferences.UnitsPreferenceDialog;
import com.bdb.weather.display.preferences.UserPreferences;
import com.bdb.weather.display.storm.StormPanel;
import com.bdb.weather.display.stripchart.MeasurementType;
import com.bdb.weather.display.stripchart.StripChartManager;
import com.bdb.weather.display.stripchart.StripChartPane;
import com.bdb.weather.display.summary.DailySummariesPanel;
import com.bdb.weather.display.summary.MonthlySummariesPanel;
import com.bdb.weather.display.summary.YearlySummariesPanel;

/**
 * FXML Controller class
 *
 * @author bruce
 */
public class WeatherSenseController implements CurrentWeatherSubscriber.CurrentWeatherHandler {
    private WeatherStation ws;
    private DBConnection connection;
    private Stage topLevelStage;
    private final CurrentWeatherSubscriber subscriber;
    private final List<CurrentWeatherProcessor> cwpList = new ArrayList<>();
    private final UserPreferences prefs = UserPreferences.getInstance();
    private static final Logger logger = Logger.getLogger(WeatherSenseController.class.getName());

    @SuppressWarnings("LeakingThisInConstructor")
    public WeatherSenseController() {
        subscriber = CurrentWeatherSubscriber.createSubscriber(this);
    }

    public void setData(WeatherStation ws, DBConnection connection, Stage stage) {
	this.ws = ws;
	this.connection = connection;
        topLevelStage = stage;
    }

    public void stop() {
	subscriber.requestExit();
    }

    @Override
    public void handleCurrentWeather(CurrentWeather currentWeather) {
        final CurrentWeather curWeather = currentWeather;
        logger.fine(String.format("Updating %s current weather processors", cwpList.size()));
        
        WeatherDataMgr.getInstance().fillInCurrentWeather(curWeather);
        Platform.runLater(() -> cwpList.forEach((cwp) -> cwp.updateCurrentWeather(curWeather)));
    }

    private Stage launchStage(Parent root, String title, boolean maximize) {
	Stage stage = new Stage();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/weathersense.css");
        stage.setTitle(title);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setMaximized(maximize);
        stage.show();
        return stage;
    }

    private Stage launchModalStage(Parent root, String title) {
	Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(topLevelStage);
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/weathersense.css");
        stage.setTitle(title);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.showAndWait();
        return stage;
    }

    @FXML
    public void launchCurrentWeatherFormView() {
	CurrentWeatherForm form = new CurrentWeatherForm(ws);
        Stage stage = launchStage(form, "Current Weather", false);
	cwpList.add(form);
        stage.setOnCloseRequest((handler) -> cwpList.remove(form));
    }

    @FXML
    public void launchCurrentWeatherView() {
	CurrentWeatherCharts cwCharts = new CurrentWeatherCharts(ws, connection);
	Stage stage = launchStage(cwCharts, "Current Weather", false);
	cwpList.add(cwCharts);
        stage.setOnCloseRequest((handler) -> cwpList.remove(cwCharts));

    }

    @FXML
    public void launchTodayView() {
        TodayGraphPane todayGraphPanel = new TodayGraphPane(connection);
	launchStage(todayGraphPanel, "Today Weather", true);
        todayGraphPanel.refresh();
    }

    @FXML
    public void launchStormsView() {
        StormPanel stormPanel = new StormPanel(connection);
        launchStage(stormPanel, "Storms", false);
    }

    @FXML
    public void launchArchiveTableView() {
        DayHistoryTable dayHistoryTable = new DayHistoryTable(connection);
        launchStage(dayHistoryTable, "Day Historical Table", false);
    }

    @FXML
    public void launchArchiveEditorView() {
    }

    @FXML
    public void launchDaySummaryView() {
        DaySummaryGraphPane graphPanel = new DaySummaryGraphPane(ws, connection, LocalDate.now());
	launchStage(graphPanel, "Weather", true);
        graphPanel.loadData();
    }

    @FXML
    public void launchDailySummariesView() {
        DailySummariesPanel panel = new DailySummariesPanel(ws, connection, null, LocalDate.now().minusDays(30), LocalDate.now(), DateInterval.LAST_30_DAYS);
        launchStage(panel, "Summary by Day", true);
    }

    @FXML
    public void launchMonthlySummariesView() {
        LocalDate end = LocalDate.now();
        LocalDate start = LocalDate.of(end.getYear(), Month.JANUARY, 1);

        MonthlySummariesPanel monthlySummaryPanel = new MonthlySummariesPanel(ws, connection, null, start, end, DateInterval.THIS_YEAR);
        launchStage(monthlySummaryPanel, "", true);
    }

    @FXML
    public void launchYearlySummariesView() {
        YearlySummariesPanel yearlySummaryPanel = new YearlySummariesPanel(ws, connection, null);
        launchStage(yearlySummaryPanel, "", true);
    }

    @FXML
    public void launchHistoricalFreePlotView() {
        HistoricalFreePlot freePlot = new HistoricalFreePlot(ws, connection);
        launchStage(freePlot.getNode(), "Archive Free Plot", true);
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
	launchStage(stripChartManager, "Strip Chart Manager", false);
    }

    @FXML
    public void launchRainStripChart() {
        StripChartPane stripChartPane = new StripChartPane(connection, MeasurementType.NONE, MeasurementType.RAINFALL);
	Stage stage = launchStage(stripChartPane, "Strip Chart - Rain", false);
	cwpList.add(stripChartPane);
        stage.setOnCloseRequest((handler) -> cwpList.remove(stripChartPane));
    }

    @FXML
    public void launchRainAndWindStripChart() {
        StripChartPane stripChartPane = new StripChartPane(connection, MeasurementType.WIND, MeasurementType.RAINFALL);
	Stage stage = launchStage(stripChartPane, "Strip Chart - Wind and Rain", false);
	cwpList.add(stripChartPane);
        stage.setOnCloseRequest((handler) -> cwpList.remove(stripChartPane));
    }

    @FXML
    public void launchTemperatureStripChart() {
        StripChartPane stripChartPane = new StripChartPane(connection, MeasurementType.NONE, MeasurementType.TEMPERATURE);
	Stage stage = launchStage(stripChartPane, "Strip Chart - Temperature", false);
	cwpList.add(stripChartPane);
        stage.setOnCloseRequest((handler) -> cwpList.remove(stripChartPane));
    }

    @FXML
    public void launchColorPreference() {
        ColorPreferencePanel dialog = new ColorPreferencePanel();
        launchModalStage(dialog, "Color Preferences");
    }

    @FXML
    public void launchUnitsPreference() {
        UnitsPreferenceDialog dialog = new UnitsPreferenceDialog();
        launchModalStage(dialog, "Units Preferences");
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
        DayAveragesEditor pane = new DayAveragesEditor(connection, "");
	launchStage(pane, "Day Average Editor", false);
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
    public void launchStormView() {
        StormPanel stormPanel = new StormPanel(connection);
        launchView("", stormPanel, new Dimension(800,600), false);
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
    public void launchHistoryEditor() {
        HistoryEditorPanel editor = new HistoryEditorPanel(ws, connection);
        launchView(HISTORY_EDITOR_CMD, editor, new Dimension(800, 600), true);
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
