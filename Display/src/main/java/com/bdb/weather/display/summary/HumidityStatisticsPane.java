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
public class HumidityStatisticsPane extends GridPane {
    @FXML private TextField minimumHumidity;
    @FXML private TextField maximumHumidity;
    @FXML private TextField averageHumidity;
    @FXML private TextField smallestRange;
    @FXML private TextField largestRange;
    @FXML private TextField minimumHumidityTime;
    @FXML private TextField maximumHumidityTime;
    @FXML private TextField smallestRangeDate;
    @FXML private TextField largestRangeDate;

    @SuppressWarnings("LeakingThisInConstructor")
    public HumidityStatisticsPane() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/HumidityStatistics.fxml"));
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

        minimumHumidity.setText(rec.getMinOutdoorHumidity().toString());
        minimumHumidityTime.setText(dateTime.format(rec.getMinOutdoorHumidityTime()));
        maximumHumidity.setText(rec.getMaxOutdoorHumidity().toString());
        maximumHumidityTime.setText(dateTime.format(rec.getMaxOutdoorHumidityTime()));
        averageHumidity.setText(rec.getAvgOutdoorHumidity().toString());
        
        smallestRange.setText("" + rec.getSmallestHumidityRange().getRange() +
                " (" + rec.getSmallestHumidityRange().getMin() +
                ", " + rec.getSmallestHumidityRange().getMax() + ")");
        smallestRangeDate.setText(dateOnly.format(rec.getSmallestHumidityRange().getDate()));
        largestRange.setText("" + rec.getLargestHumidityRange().getRange() +
                " (" + rec.getLargestHumidityRange().getMin() +
                ", " + rec.getLargestHumidityRange().getMax() + ")");
        largestRangeDate.setText(dateOnly.format(rec.getLargestHumidityRange().getDate()));

    }
    
}
