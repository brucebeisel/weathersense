/*
 * Copyright (C) 2016 bruce
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

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.bdb.weather.display.stripchart.MeasurementType;
import com.bdb.weather.display.stripchart.StripChart;
import static com.bdb.weather.display.stripchart.StripChart.MAP_TO_LEFT_AXIS;
import static com.bdb.weather.display.stripchart.StripChart.MAP_TO_RIGHT_AXIS;

/**
 *
 * @author bruce
 */
public class StripChartTest extends Application {
    public static void main(String args[]) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        final StripChart chart = new StripChart(MeasurementType.TEMPERATURE, MeasurementType.HUMIDITY, 1, 10);
        chart.addSeries("Outdoor", MAP_TO_LEFT_AXIS, Color.BLUE);
        chart.addSeries("Humidity", MAP_TO_RIGHT_AXIS, Color.CYAN);

        Scene scene = new Scene(chart);

        stage.setScene(scene);
        stage.show();

        long delay = 1000; //milliseconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(delay), (actionEvent) -> {

            int timeOffset = 10 * 3600;
            int counter = 0;

            LocalDateTime now = LocalDateTime.now().minusSeconds(timeOffset);
            chart.addItem("Outdoor", now, now.atZone(ZoneId.systemDefault()).toEpochSecond());
            Platform.runLater(() -> chart.refresh());
            System.out.println("Added new item and refreshed");
            timeOffset -= 300;
            if (++counter == 10) {
                Platform.runLater(() -> chart.setSpanHours(48));
            }
            else if (counter == 30) {
                Platform.runLater(() -> chart.setSpanHours(24));
            }
        }));
    }
}
