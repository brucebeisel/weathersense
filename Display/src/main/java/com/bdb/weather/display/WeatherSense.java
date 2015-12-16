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
package com.bdb.weather.display;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.db.DatabaseConstants;
import com.bdb.weather.common.db.WeatherStationTable;
import com.bdb.weather.display.day.HistoricalSeriesInfo;

public class WeatherSense extends Application {
    private static final int REFRESH_INTERVAL = 30;
    private DBConnection connection;
    private WeatherStationTable stationTable;
    private WeatherStation ws;
    private final List<Refreshable> refreshList = new ArrayList<>();
    private ScheduledExecutorService timer;
    private WeatherSenseController controller;
    private String databaseUrl;
    private static final Logger logger = Logger.getLogger(WeatherSense.class.getName());

    private void openDatabase(List<String> args) {
	String databaseHost;

	if (!args.isEmpty())
	    databaseHost = args.get(0);
	else
	    //databaseHost = DatabaseConstants.DATABASE_HOST;
            databaseHost = "192.168.1.100";

	databaseHost = "192.168.1.100";
        databaseUrl = String.format(DatabaseConstants.DATABASE_URL_FORMATTER, databaseHost, DatabaseConstants.DATABASE_PORT, DatabaseConstants.DATABASE_NAME);

        connection = new DBConnection(databaseUrl, DatabaseConstants.DATABASE_USER, DatabaseConstants.DATABASE_PASSWORD);

        connection.connect();
    }

    @Override
    public void init() {
        try (InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("logging.properties")) {

            if (is != null)
                LogManager.getLogManager().readConfiguration(is);
        }
        catch (IOException | SecurityException e) {
            ErrorDisplayer.getInstance().displayMessageLater("Failed to initialize (" + e.getMessage() + ")", Alert.AlertType.ERROR);
            Platform.exit();
        }

	Application.Parameters params = getParameters();
	List<String> args = params.getRaw();
	openDatabase(args);
        WeatherStationMgr.initialize(connection);

        timer = Executors.newSingleThreadScheduledExecutor();
	timer.scheduleAtFixedRate(() -> {
            WeatherDataMgr.getInstance().refreshData();
	    logger.info("Refreshing screens");
	    refreshList.stream().forEach((refresh) -> refresh.refresh());
	}, REFRESH_INTERVAL, REFRESH_INTERVAL, TimeUnit.SECONDS);

    }

    @Override
    public void start(Stage stage) throws Exception {
	if (connection.getConnection() == null) {
	    Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to connect to the database. Please contact your administrator", ButtonType.OK);
	    alert.showAndWait();
	    Platform.exit();
        }

        //
        // If there is no weather station in the database, then prompt user for the weather station information
        //
        stationTable = new WeatherStationTable(connection);
        ws = stationTable.getWeatherStation();
        if (ws == null) {
            WeatherStationMgr.editWeatherStation();
        }
        else
	    HistoricalSeriesInfo.addExtraSensors(ws.getSensorManager().getAllSensors());

        WeatherDataMgr.getInstance().initialize(databaseUrl, ws.getWeatherYearStartMonth());

	Image icon = new Image("com/bdb/weathersense/WeatherSense.jpg");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WeatherSense.fxml"), ResourceBundle.getBundle("com.bdb.weathersense.Localization"));
        loader.load();
        BorderPane root = loader.getRoot();
        
        Scene scene = new Scene(root, 800, 800);
        scene.getStylesheets().add("/styles/weathersense.css");
        stage.setTitle("WeatherSense 3.0");
	stage.getIcons().add(icon);
        stage.setScene(scene);
        controller = loader.getController();
	controller.setData(ws, connection);
        stage.sizeToScene();
        stage.show();
    }

    @Override
    public void stop() {
	timer.shutdownNow();
        controller.stop();

    }

    public static void setStageTitle(Node node, String title) {
        Window window = node.getScene().getWindow();
        if (window instanceof Stage)
            ((Stage)window).setTitle(title);
    }
    public static String getStageTitle(Node node) {
        Window window = node.getScene().getWindow();
        if (window instanceof Stage)
            return ((Stage)window).getTitle();
        else
            return "";
    }

    public static void main(String args[]) {
	launch(args);
    }
}