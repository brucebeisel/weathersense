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
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.CurrentWeatherSubscriber;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.db.DatabaseConstants;
import com.bdb.weather.common.db.WeatherStationTable;
import com.bdb.weather.display.day.HistoricalSeriesInfo;
import com.bdb.weather.display.preferences.UserPreferences;

public class WeatherSense extends Application implements Runnable, CurrentWeatherSubscriber.CurrentWeatherHandler {
    private static final int REFRESH_INTERVAL = 30;
    private final DBConnection connection;
    private final WeatherStationTable stationTable;
    private WeatherStation ws;
    private final List<Refreshable> refreshList = new ArrayList<>();
    private ScheduledThreadPoolExecutor timer;
    private final Preferences rootPref = Preferences.userNodeForPackage(WeatherSense.class);
    private final Preferences prefs = rootPref.node("window-geometry");
    private final List<CurrentWeatherProcessor> cwpList = new ArrayList<>();
    private static final Logger logger = Logger.getLogger(WeatherSense.class.getName());

    @Override
    public void start(Stage stage) throws Exception {
        CurrentWeatherSubscriber.createSubscriber(this);
	timer = new ScheduledThreadPoolExecutor(1);
	timer.scheduleAtFixedRate(this, REFRESH_INTERVAL, REFRESH_INTERVAL, TimeUnit.SECONDS);

	Image icon = new Image("com/bdb/weathersense/WeatherSense.jpg");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WeatherSense.fxml"), ResourceBundle.getBundle("com.bdb.weathersense.Localization"));
        loader.load();
        BorderPane root = loader.getRoot();
        
        Scene scene = new Scene(root, 800, 800);
        scene.getStylesheets().add("/styles/weathersense.css");
        stage.setTitle("WeatherSense 3.0");
	stage.getIcons().add(icon);
        stage.setScene(scene);
        WeatherSenseController controller = loader.getController();
        stage.sizeToScene();
        stage.show();

        //
        // If there is no weather station in the database, then prompt user for the weather station information
        //
        //if (ws == null) {
        //    WeatherStationMgr.editWeatherStation(frame, connection);
        //}
        //else
	HistoricalSeriesInfo.addExtraSensors(ws.getSensorManager().getAllSensors());
    }

    public WeatherSense(String databaseHost) {
        
        
        String databaseUrl = String.format(DatabaseConstants.DATABASE_URL_FORMATTER, databaseHost, DatabaseConstants.DATABASE_PORT, DatabaseConstants.DATABASE_NAME);

        connection = new DBConnection(databaseUrl,
                                        DatabaseConstants.DATABASE_USER,
                                        DatabaseConstants.DATABASE_PASSWORD);


        if (!connection.connect()) {
	    Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to connect to the data. Please contact your administrator", ButtonType.OK);
	    alert.showAndWait();
            System.exit(1);
        }
        
        stationTable = new WeatherStationTable(connection);
        ws = stationTable.getWeatherStation();
    }


    @Override
    public void run() {
	logger.info("Refreshing screens");
	refreshList.stream().forEach((refresh) -> { refresh.refresh(); });
    }

    @Override
    public void handleCurrentWeather(CurrentWeather currentWeather) {
        final CurrentWeather curWeather = currentWeather;
        logger.fine(String.format("Updating %s current weather processors", cwpList.size()));
        
        Platform.runLater(() -> { cwpList.stream().forEach((cwp) -> { cwp.updateCurrentWeather(curWeather); }); });
    }

    public static void main(String args[]) {
        try { InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("logging.properties");

            if (is != null)
                LogManager.getLogManager().readConfiguration(is);

            UserPreferences.getInstance();

            String dbHost = DatabaseConstants.DATABASE_HOST;

            if (args.length > 0)
                dbHost = args[0];

	    launch(args);
        }
        catch (IOException | SecurityException e) {
            ErrorDisplayer.getInstance().displayMessageLater("Failed to initialize (" + e.getMessage() + ")", Alert.AlertType.ERROR);
            System.exit(1);
        }
    }
}