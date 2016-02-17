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
import javafx.stage.Window;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.CurrentWeatherSubscriber;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.display.current.CurrentWeatherCharts;
import com.bdb.weather.display.current.CurrentWeatherForm;
import com.bdb.weather.display.day.DaySummaryGraphPane;
import com.bdb.weather.display.day.TodayGraphPane;
import com.bdb.weather.display.freeplot.DailyFreePlot;
import com.bdb.weather.display.historytable.DayHistoryTable;
import com.bdb.weather.display.freeplot.HistoricalFreePlot;
import com.bdb.weather.display.freeplot.MonthlyFreePlot;
import com.bdb.weather.display.historyeditor.HistoryEditorPanel;
import com.bdb.weather.display.preferences.ColorPreferencePanel;
import com.bdb.weather.display.preferences.UnitsPreferenceDialog;
import com.bdb.weather.display.preferences.UserPreferences;
import com.bdb.weather.display.sensors.SensorPanel;
import com.bdb.weather.display.sensors.SensorStationPanel;
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
public class WeatherSenseController implements ViewLauncher, CurrentWeatherSubscriber.CurrentWeatherHandler {
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

    private Stage launchStage(Parent root, String title, boolean maximize, boolean modal) {
	Stage stage = new Stage();
        if (modal)
            stage.initModality(Modality.APPLICATION_MODAL);

        stage.initOwner(topLevelStage);
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/weathersense.css");
        stage.setTitle(title);
        stage.setScene(scene);
        stage.sizeToScene();

        if (root instanceof Hideable) {
            stage.setOnHidden((event) -> ((Hideable)((Window)event.getSource()).getScene().getRoot()).hide());
        }

        if (!modal)
            stage.setMaximized(maximize);

        if (modal)
            stage.showAndWait();
        else
            stage.show();

        return stage;

    }
    private Stage launchStage(Parent root, String title, boolean maximize) {
        return launchStage(root, title, maximize, false);
    }

    private Stage launchModalStage(Parent root, String title) {
        return launchStage(root, title, false, true);
    }

    @FXML
    public void launchCurrentWeatherFormView() {
	CurrentWeatherForm form = new CurrentWeatherForm(ws);
        Stage stage = launchStage(form, "Current Weather", false);
	cwpList.add(form);
        stage.setOnCloseRequest((handler) -> cwpList.remove(form));
    }

    @FXML @Override
    public void launchCurrentWeatherView() {
	CurrentWeatherCharts cwCharts = new CurrentWeatherCharts(ws, connection);
	Stage stage = launchStage(cwCharts, "Current Weather", false);
	cwpList.add(cwCharts);
        stage.setOnCloseRequest((handler) -> cwpList.remove(cwCharts));

    }

    @FXML @Override
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
        HistoryEditorPanel editor = new HistoryEditorPanel(ws, connection);
        launchStage(editor, "Archive Editor", true);
    }

    @FXML
    public void launchDaySummaryView() {
        launchDaySummaryView(LocalDate.now());
    }

    @FXML
    public void launchDailySummariesView() {
        DailySummariesPanel panel = new DailySummariesPanel(ws, connection, this, LocalDate.now().minusDays(30), LocalDate.now(), DateInterval.LAST_30_DAYS);
        launchStage(panel, "Summary by Day", true);
    }

    @FXML
    public void launchMonthlySummariesView() {
        LocalDate end = LocalDate.now();
        LocalDate start = LocalDate.of(end.getYear(), Month.JANUARY, 1);

        MonthlySummariesPanel monthlySummaryPanel = new MonthlySummariesPanel(ws, connection, this, start, end, DateInterval.THIS_YEAR);
        launchStage(monthlySummaryPanel, "", true);
    }

    @FXML
    public void launchYearlySummariesView() {
        YearlySummariesPanel yearlySummaryPanel = new YearlySummariesPanel(ws, connection, this);
        launchStage(yearlySummaryPanel, "", true);
    }

    @FXML @Override
    public void launchHistoricalFreePlotView() {
        HistoricalFreePlot freePlot = new HistoricalFreePlot(ws, connection);
        launchStage(freePlot.getNode(), "Archive Free Plot", true);
    }

    @FXML @Override
    public void launchDailyFreePlotView() {
        DailyFreePlot freePlot = new DailyFreePlot(ws, connection);
    }

    @FXML @Override
    public void launchMonthlyFreePlotView() {
        MonthlyFreePlot freePlot = new MonthlyFreePlot(ws, connection);
        launchStage(freePlot.getNode(), "Monthly Free Plot", true);
    }

    @FXML @Override
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
        SensorPanel sensorPanel = new SensorPanel(connection);
        launchStage(sensorPanel, "Sensors", false);
    }

    @FXML
    public void launchSensorStations() {
        SensorStationPanel sensorStationPanel = new SensorStationPanel(connection);
        launchStage(sensorStationPanel, "Sensor Stations", false);
        sensorStationPanel.loadData();
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

    @Override
    public void launchDaySummaryView(LocalDate date) {
        DaySummaryGraphPane graphPanel = new DaySummaryGraphPane(ws, connection, date);
	launchStage(graphPanel, "Weather", true);
        graphPanel.loadData();
    }

    @Override
    public void launchDailySummariesView(LocalDate start, LocalDate end) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void launchDailySummariesView(DateInterval interval) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void launchMonthlySummariesView(DateInterval interval) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void launchHistoryEditor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void launchStormView() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}