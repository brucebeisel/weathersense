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
package com.bdb.weather.display.summary;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import com.bdb.weather.common.Statistics;

/**
 *
 * @author bruce
 */
public class WindSummaryPane extends GridPane {
    @FXML private TextField maximumSpeed;
    @FXML private TextField maximumGust;
    @FXML private TextField windiestDayAverage;
    @FXML private TextField windSpeedAverage;
    @FXML private TextField maximumSpeedTime;
    @FXML private TextField maximumGustTime;
    @FXML private TextField windiestDayAverageDate;

    @SuppressWarnings("LeakingThisInConstructor")
    public WindSummaryPane() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/WindSummary.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        }
	catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void loadData(Statistics rec) {
        DateTimeFormatter dateTime = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
        DateTimeFormatter dateOnly = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

        maximumSpeed.setText("" + rec.getMaxWindSpeed());
        maximumSpeedTime.setText(dateTime.format(rec.getMaxWindSpeedTime()));
        maximumGust.setText("" + rec.getMaxWindGust());
        maximumGustTime.setText(dateTime.format(rec.getMaxWindGustTime()));
        windSpeedAverage.setText("" + rec.getAvgWindSpeed());
        windiestDayAverage.setText("" + rec.getMaxAvgWindSpeed().getValue());
        windiestDayAverageDate.setText(dateOnly.format(rec.getMaxAvgWindSpeed().getTime()));

    }
}
