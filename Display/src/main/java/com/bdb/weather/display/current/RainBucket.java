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

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.display.JRainBucket;
import com.bdb.weather.display.JScalableLabel;

/**
 * Class that graphically displays a rainfall amount.
 * 
 * @author Bruce
 *
 */
public class RainBucket {
    private JRainBucket    rainBucket;
    private JScalableLabel label;
    private final JPanel   panel = new JPanel(new BorderLayout());
    
    /**
     * Constructor.
     * 
     * @param labelString The label
     * @param maxValue The maximum amount of rain for this bucket
     * @param average The average rainfall for the period that this bucket is representing
     * @param averageToDate The average amount of rainfall that should have occurred to the current date
     */
    public RainBucket(String labelString, Depth maxValue, Depth average, Depth averageToDate) {
        Depth.Unit unit = Depth.getDefaultUnit();
        rainBucket = new JRainBucket(maxValue.get(), unit.getFormatter(), unit.toString(), average.get(), averageToDate.get());
        rainBucket.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        label = new JScalableLabel(labelString);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        panel.add(rainBucket, BorderLayout.CENTER);
        panel.add(label, BorderLayout.SOUTH);
    }
    
    /**
     * Constructor.
     * 
     * @param labelString The label
     * @param maxValue The maximum amount of rain for this bucket
     * @param average The average rainfall for the period that this bucket is representing
     */
    public RainBucket(String labelString, Depth maxValue, Depth average) {
        this(labelString, maxValue, average, new Depth(0.0));
    }
    
    /**
     * Constructor that defaults the average value to 0.0.
     * 
     * @param labelString The label
     * @param maxValue The maximum amount of rain for this bucket
     */
    public RainBucket(String labelString, Depth maxValue) {
    	this(labelString, maxValue, new Depth(0.0));
    }
    
    /**
     * Get the swing component that contains the Rain Bucket.
     * 
     * @return The swing container
     */
    public JComponent getComponent() {
        return panel;
    }
    
    /**
     * Set the label of the rain bucket.
     * 
     * @param text The new label
     */
    public void setLabel(String text) {
        label.setText(text);
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
