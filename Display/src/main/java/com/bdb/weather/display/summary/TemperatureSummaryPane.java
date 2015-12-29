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
    @FXML private TextField averageLow;
    @FXML private TextField daysLowBelowNormal;
    @FXML private TextField seasonalLowAverage;
    @FXML private TextField minimumHigh;
    @FXML private TextField minimumHighTime;
    @FXML private TextField maximumHigh;
    @FXML private TextField maximumHighTime;
    @FXML private TextField averageHigh;
    @FXML private TextField daysHighAboveNormal;
    @FXML private TextField seasonalHighAverage;
    @FXML private TextField minimumMean;
    @FXML private TextField minimumMeanDate;
    @FXML private TextField maximumMean;
    @FXML private TextField maximumMeanDate;
    @FXML private TextField averageMean;
    @FXML private TextField daysMeanAboveNormal;
    @FXML private TextField seasonalMeanAverage;
    @FXML private TextField smallestTemperatureRange;
    @FXML private TextField largestTemperatureRange;
    @FXML private TextField smallestTemperatureRangeDate;
    @FXML private TextField largestTemperatureRangeDate;
    @FXML private TemperatureBinSummaryPlot temperatureBinSummaryPlot;

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

        temperatureBinSummaryPlot.loadData(rec.getTemperatureBinData());

        minimumLow.setText(rec.getMinOutdoorTemp().toString());
        minimumLowTime.setText(dateTime.format(rec.getMinOutdoorTempTime()));
        maximumLow.setText(rec.getMaxLowOutdoorTemperature().getValue().toString());
        maximumLowTime.setText(dateTime.format(rec.getMaxLowOutdoorTemperature().getTime()));
        averageLow.setText(rec.getAvgLowTemperature().toString());
        seasonalLowAverage.setText(seasonalAverages.getLowTemperature().toString());
        daysLowBelowNormal.setText("" + rec.getDaysLowBelowNormal() + " of " + rec.getNumberOfDays());

        minimumHigh.setText(rec.getMinHighOutdoorTemperature().getValue().toString());
        minimumHighTime.setText(dateTime.format(rec.getMinHighOutdoorTemperature().getTime()));
        maximumHigh.setText(rec.getMaxOutdoorTemp().toString());
        maximumHighTime.setText(dateTime.format(rec.getMaxOutdoorTempTime()));
        averageHigh.setText(rec.getAvgHighTemperature().toString());
        seasonalHighAverage.setText(seasonalAverages.getHighTemperature().toString());
        daysHighAboveNormal.setText("" + rec.getDaysHighAboveNormal() + " of " + rec.getNumberOfDays());

        minimumMean.setText(rec.getMinMeanOutdoorTemperature().getValue().toString());
        minimumMeanDate.setText(dateOnly.format(rec.getMinMeanOutdoorTemperature().getTime()));
        maximumMean.setText(rec.getMaxMeanOutdoorTemperature().getValue().toString());
        maximumMeanDate.setText(dateOnly.format(rec.getMaxMeanOutdoorTemperature().getTime()));
        averageMean.setText(rec.getAvgMeanTemperature().toString());
        seasonalMeanAverage.setText(seasonalAverages.getMeanTemperature().toString());
        daysMeanAboveNormal.setText("" + rec.getDaysMeanAboveNormal() + " of " + rec.getNumberOfDays());

        smallestTemperatureRange.setText("" + rec.getSmallestTemperatureRange().getRange() +
                                   " (" + rec.getSmallestTemperatureRange().getMin() +
                                   ", " + rec.getSmallestTemperatureRange().getMax() + ")");
        smallestTemperatureRangeDate.setText(dateOnly.format(rec.getSmallestTemperatureRange().getDate()));
        largestTemperatureRange.setText("" + rec.getLargestTemperatureRange().getRange() +
                " (" + rec.getLargestTemperatureRange().getMin() +
                ", " + rec.getLargestTemperatureRange().getMax() + ")");
        largestTemperatureRangeDate.setText(dateOnly.format(rec.getLargestTemperatureRange().getDate()));
    }
}