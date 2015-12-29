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
import javafx.scene.layout.BorderPane;

import com.bdb.weather.common.Statistics;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.display.DisplayConstants;

/**
 *
 * @author bruce
 */
public class RainSummaryPane extends BorderPane {
    @FXML private RainHourChart rainHourChart;
    @FXML private TextField     totalRainfall;
    @FXML private TextField     maxRainfallRate;
    @FXML private TextField     maxRainfallRateTime;

    @SuppressWarnings("LeakingThisInConstructor")
    public RainSummaryPane() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/RainSummary.fxml"));
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

        rainHourChart.loadData(rec);

        totalRainfall.setText(rec.getTotalRainfall().toString());
        Depth maxRainfallRateValue = rec.getMaxRainfallRate();
        if (maxRainfallRateValue != null && maxRainfallRateValue.get() != 0.0) {
            maxRainfallRate.setText("" + rec.getMaxRainfallRate() + "/hour");
            maxRainfallRateTime.setText(dateTime.format(rec.getMaxRainfallRateTime()));
        }
        else {
            maxRainfallRate.setText(DisplayConstants.UNKNOWN_VALUE_STRING);
            maxRainfallRateTime.setText(DisplayConstants.UNKNOWN_VALUE_STRING);
        }
        /*
        //
        // This can be null if the data was collected before 2.2 or there was no rain the entire time period
        //
        rainDays.setText("" + rec.getRainDays());
        maxRainDayDepth.setText(rec.getMaxDayRainDepth().toString());
        maxRainDayDate.setText(dateOnly.format(rec.getMaxDayRainDate()));
        avgRainPerDay.setText(rec.getAvgRainPerDay().toString());
*/
    }
}