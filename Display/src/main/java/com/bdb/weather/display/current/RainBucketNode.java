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
package com.bdb.weather.display.current;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;

import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.display.RainBucket;

/**
 * Class that graphically displays a rainfall amount.
 * 
 * @author Bruce
 *
 */
public class RainBucketNode extends BorderPane {
    private RainBucket       rainBucket;
    private Label            title;
    private final StringProperty   titleProperty = new SimpleStringProperty();

    public RainBucketNode() {
	this("", new Depth(1.0));
    }
    /**
     * Constructor.
     * 
     * @param labelString The label
     * @param maxValue The maximum amount of rain for this bucket
     * @param average The average rainfall for the period that this bucket is representing
     * @param averageToDate The average amount of rainfall that should have occurred to the current date
     */
    public RainBucketNode(String labelString, Depth maxValue, Depth average, Depth averageToDate) {
        Depth.Unit unit = Depth.getDefaultUnit();
        rainBucket = new com.bdb.weather.display.RainBucket(maxValue.get(), unit.getFormatter(), unit.toString(), average.get(), averageToDate.get());
        //rainBucket.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        title = new Label(labelString);
        title.setTextAlignment(TextAlignment.CENTER);
        //label.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        setCenter(rainBucket);
        BorderPane.setAlignment(title, Pos.CENTER);
        setBottom(title);
        title.textProperty().bindBidirectional(titleProperty);
    }
    
    /**
     * Constructor.
     * 
     * @param labelString The label
     * @param maxValue The maximum amount of rain for this bucket
     * @param average The average rainfall for the period that this bucket is representing
     */
    public RainBucketNode(String labelString, Depth maxValue, Depth average) {
        this(labelString, maxValue, average, new Depth(0.0));
    }
    
    /**
     * Constructor that defaults the average value to 0.0.
     * 
     * @param labelString The label
     * @param maxValue The maximum amount of rain for this bucket
     */
    public RainBucketNode(String labelString, Depth maxValue) {
    	this(labelString, maxValue, new Depth(0.0));
    }
    
    public void setTitle(String value) {
        titleProperty.set(value);
    }

    public String getTitle() {
        return titleProperty.get();
    }

    public StringProperty titleProperty() {
        return titleProperty;
    }

    public void setMaxValue(Depth max) {
        rainBucket.setMaxValue(max.get());
    }

    public void setAverages(Depth average, Depth toDateAverage) {
        if (average != null)
            rainBucket.setAverage(average.get());
        else
            rainBucket.setAverage(0.0);

        if (toDateAverage != null)
            rainBucket.setToDateAverage(toDateAverage.get());
        else
            rainBucket.setToDateAverage(0.0);
    }
    
    /**
     * Set the amount of rain in this bucket.
     * 
     * @param value The amount of rain
     */
    public void setValue(double value) {
        rainBucket.setRainfallAmount(value);
    }
}