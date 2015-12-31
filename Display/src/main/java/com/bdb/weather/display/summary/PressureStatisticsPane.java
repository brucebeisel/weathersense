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
public class PressureStatisticsPane extends GridPane {
    @FXML private TextField minimumPressure;
    @FXML private TextField maximumPressure;
    @FXML private TextField averagePressure;
    @FXML private TextField smallestRange;
    @FXML private TextField largestRange;
    @FXML private TextField minimumPressureTime;
    @FXML private TextField maximumPressureTime;
    @FXML private TextField smallestRangeDate;
    @FXML private TextField largestRangeDate;

    @SuppressWarnings("LeakingThisInConstructor")
    public PressureStatisticsPane() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PressureStatistics.fxml"));
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

        minimumPressure.setText(rec.getMinBaroPressure().toString());
        minimumPressureTime.setText(dateTime.format(rec.getMinBaroPressureTime()));
        maximumPressure.setText(rec.getMaxBaroPressure().toString());
        maximumPressureTime.setText(dateTime.format(rec.getMaxPressureTime()));
        averagePressure.setText(rec.getAvgBaroPressure().toString());
        
        smallestRange.setText("" + rec.getSmallestPressureRange().getRange() +
                " (" + rec.getSmallestPressureRange().getMin() +
                ", " + rec.getSmallestPressureRange().getMax() + ")");
        smallestRangeDate.setText(dateOnly.format(rec.getSmallestPressureRange().getDate()));
        largestRange.setText("" + rec.getLargestPressureRange().getRange() +
                " (" + rec.getLargestPressureRange().getMin() +
                ", " + rec.getLargestPressureRange().getMax() + ")");
        largestRangeDate.setText(dateOnly.format(rec.getLargestPressureRange().getDate()));

    }
}