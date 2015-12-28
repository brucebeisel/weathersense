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
import com.bdb.weather.common.WeatherAverage;

/**
 *
 * @author bruce
 */
public class TemperatureSummaryPane extends BorderPane {
    @FXML private TextField minimumLow;
    @FXML private TextField minimumLowTime;
    @FXML private TextField maximumLow;
    @FXML private TextField maximumLowTime;

    @SuppressWarnings("LeakingThisInConstructor")
    public TemperatureSummaryPane() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/TemperatureSummary.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        }
	catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void loadData(Statistics rec, WeatherAverage seasonalAverages) {
        DateTimeFormatter dateTime = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
        DateTimeFormatter dateOnly = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

        minimumLow.setText(rec.getMinOutdoorTemp().toString());
        minimumLowTime.setText(dateTime.format(rec.getMinOutdoorTempTime()));
        maximumLow.setText(rec.getMaxLowOutdoorTemperature().getValue().toString());
        maximumLowTime.setText(dateTime.format(rec.getMaxLowOutdoorTemperature().getTime()));

        /*
        minHighTempValue.setText(rec.getMinHighOutdoorTemperature().getValue().toString());
        minHighTempTime.setText(dateTime.format(rec.getMinHighOutdoorTemperature().getTime()));
        maxHighTempValue.setText(rec.getMaxOutdoorTemp().toString());
        maxHighTempTime.setText(dateTime.format(rec.getMaxOutdoorTempTime()));

        minMeanTempValue.setText(rec.getMinMeanOutdoorTemperature().getValue().toString());
        minMeanTempDate.setText(dateOnly.format(rec.getMinMeanOutdoorTemperature().getTime()));
        maxMeanTempValue.setText(rec.getMaxMeanOutdoorTemperature().getValue().toString());
        maxMeanTempDate.setText(dateOnly.format(rec.getMaxMeanOutdoorTemperature().getTime()));
        
        avgHighTemp.setText(rec.getAvgHighTemperature().toString());
        avgLowTemp.setText(rec.getAvgLowTemperature().toString());
        avgMeanTemp.setText(rec.getAvgMeanTemperature().toString());
        
        seasonalAvgLowTemp.setText(seasonalAverages.getLowTemperature().toString());
        seasonalAvgMeanTemp.setText(seasonalAverages.getMeanTemperature().toString());
        seasonalAvgHighTemp.setText(seasonalAverages.getHighTemperature().toString());
        daysHighAboveNormal.setText("" + rec.getDaysHighAboveNormal() + " of " + rec.getNumberOfDays());
        daysMeanAboveNormal.setText("" + rec.getDaysMeanAboveNormal() + " of " + rec.getNumberOfDays());
        daysLowBelowNormal.setText("" + rec.getDaysLowBelowNormal() + " of " + rec.getNumberOfDays());
*/
    }
}