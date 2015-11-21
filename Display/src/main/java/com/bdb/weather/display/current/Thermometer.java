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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;

import javax.swing.border.BevelBorder;

import javafx.embed.swing.SwingNode;
import javafx.scene.Node;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ThermometerPlot;
import org.jfree.data.general.DefaultValueDataset;

import com.bdb.weather.common.measurement.Temperature;

/**
 * A thermometer class.
 * 
 * @author Bruce
 *
 */
public class Thermometer extends SwingNode {
    private final DefaultValueDataset temperatureDataset = new DefaultValueDataset(0.0);
    private final ThermometerPlot     thermometerPlot = new ThermometerPlot(temperatureDataset);
    private final ChartPanel          chartPanel;
 
    /**
     * Constructor.
     * 
     * @param title The title to display in the containing panel
     * @param min The minimum value for the thermometer scale
     * @param max The maximum value for the thermometer scale
     */
    public Thermometer(String title, Temperature min, Temperature max) {
        
        if (Temperature.getDefaultUnit() == Temperature.Unit.FAHRENHEIT)
            thermometerPlot.setUnits(ThermometerPlot.UNITS_FAHRENHEIT);
        else if (Temperature.getDefaultUnit() == Temperature.Unit.CELSIUS)
            thermometerPlot.setUnits(ThermometerPlot.UNITS_CELCIUS);
        else
            thermometerPlot.setUnits(ThermometerPlot.UNITS_KELVIN);

        thermometerPlot.setRange(min.get(), max.get());
        thermometerPlot.setMercuryPaint(new GradientPaint(0.0f, 0.0f, Color.RED.darker(), 5.0f, 5.0f, Color.RED, true));
        thermometerPlot.setUseSubrangePaint(false);
        thermometerPlot.setBulbRadius(15);
        thermometerPlot.setColumnRadius(10);
        thermometerPlot.setValueLocation(ThermometerPlot.RIGHT);
        thermometerPlot.setValuePaint(Color.BLACK);
        thermometerPlot.setSubrange(0, 80.0, 80.0);
        thermometerPlot.setSubrangePaint(0, Color.BLUE);
        thermometerPlot.setSubrangePaint(1, Color.RED);
        thermometerPlot.setSubrange(1, 1000.0, 1000.0);
        thermometerPlot.setSubrange(2, 1000.0, 1000.0);
        thermometerPlot.setValueFormat(Temperature.getDefaultFormatter());
        thermometerPlot.setBackgroundPaint(Color.GRAY);
        thermometerPlot.setOutlineVisible(false);

        JFreeChart chart = new JFreeChart(thermometerPlot);
        chart.setTitle(title);
        chart.setBackgroundPaint(Color.GRAY);

        chartPanel = new ChartPanel(chart);
        chartPanel.setMinimumDrawHeight(200);
        chartPanel.setMinimumDrawWidth(200);
        chartPanel.setPreferredSize(new Dimension(100, 200));
        chartPanel.setBackground(Color.GRAY);
        chartPanel.setBorder(new BevelBorder(BevelBorder.RAISED));

	this.setContent(chartPanel);
    }
    
    /**
     * Get the swing component that contains the thermometer
     * 
     * @return The swing container
     */
    public Node getComponent() {
        return this;
    }

    /**
     * Set the current value, the low and high for the day.
     * 
     * @param current
     *            The current temperature
     * @param low
     *            The low temperature for the day
     * @param high
     *            The high temperature for the day
     */
    public void loadData(Temperature current, Temperature low, Temperature high) {
        if (current == null || low == null || high == null)
            return;

        temperatureDataset.setValue(current.get());
        thermometerPlot.setSubrange(0,low.get(), low.get());
        thermometerPlot.setSubrange(1, high.get(), high.get());
    }
}