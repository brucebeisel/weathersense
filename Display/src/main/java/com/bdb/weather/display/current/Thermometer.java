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
import java.awt.GradientPaint;

import javax.swing.SwingUtilities;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.ThermometerPlot;
import org.jfree.data.general.DefaultValueDataset;

import com.bdb.weather.common.measurement.Temperature;

/**
 * A thermometer class.
 * 
 * @author Bruce
 *
 */
public class Thermometer extends BorderPane {
    private final DefaultValueDataset temperatureDataset = new DefaultValueDataset(0.0);
    private final ThermometerPlot     thermometerPlot = new ThermometerPlot(temperatureDataset);
    private final Label               highLabel = new Label();
    private final Label               lowLabel = new Label();
    private final StringProperty      titleProperty = new SimpleStringProperty();
    private JFreeChart                chart;
    private final DoubleProperty      minValue = new SimpleDoubleProperty();
    private final DoubleProperty      maxValue = new SimpleDoubleProperty();
 
    public Thermometer() {
	this("", new Temperature(-50, Temperature.Unit.CELSIUS), new Temperature(50, Temperature.Unit.CELSIUS));
    }
    /**
     * Constructor.
     * 
     * @param title The title to display in the containing panel
     * @param min The minimum value for the thermometer scale
     * @param max The maximum value for the thermometer scale
     */
    public Thermometer(String title, Temperature min, Temperature max) {
	this.setPrefSize(150.0, 270.0);
	ChartViewer chartViewer = createChartElements(min, max);
	this.setCenter(chartViewer);
	this.setTop(highLabel);
	this.setBottom(lowLabel);
    }
        
    private ChartViewer createChartElements(Temperature min, Temperature max) {
        if (null != Temperature.getDefaultUnit()) {
            switch (Temperature.getDefaultUnit()) {
		case FAHRENHEIT:
		    thermometerPlot.setUnits(ThermometerPlot.UNITS_FAHRENHEIT);
		    break;

		case CELSIUS:
		    thermometerPlot.setUnits(ThermometerPlot.UNITS_CELCIUS);
		    break;

		case KELVIN:
		    thermometerPlot.setUnits(ThermometerPlot.UNITS_KELVIN);
		    break;
	    }
	}

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

        chart = new JFreeChart(thermometerPlot);
        chart.setTitle(titleProperty.getValue());
        chart.setBackgroundPaint(Color.GRAY);

        ChartViewer chartViewer = new ChartViewer(chart);
        chartViewer.setMinHeight(200);
        chartViewer.setMinWidth(200);
        //chartViewer.setBackground(Color.GRAY);
        //chartViewer.setBorder(new BevelBorder(BevelBorder.RAISED));
        return chartViewer;
    }

    public String getTitle() {
	return titleProperty.getValue();
    }

    public final void setTitle(String title) {
	titleProperty.setValue(title);
	if (chart != null)
	    SwingUtilities.invokeLater(() -> chart.setTitle(title));
    }

    public StringProperty titleProperty() {
	return titleProperty;
    }

    public Temperature getMinValue() {
	return new Temperature(minValue.getValue(), Temperature.Unit.CELSIUS);
    }

    public void setMinValue(Temperature value) {
	minValue.setValue(value.get(Temperature.Unit.CELSIUS));
	SwingUtilities.invokeLater(()->thermometerPlot.setLowerBound(value.get()));
    }

    public DoubleProperty minValueProperty() {
	return minValue;
    }
    
    public Temperature getMaxValue() {
	return new Temperature(maxValue.getValue(), Temperature.Unit.CELSIUS);
    }

    public void setMaxValue(Temperature value) {
	maxValue.setValue(value.get(Temperature.Unit.CELSIUS));
	SwingUtilities.invokeLater(()->thermometerPlot.setUpperBound(value.get()));
    }

    public DoubleProperty maxValueProperty() {
	return maxValue;
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

	SwingUtilities.invokeLater(() -> {
	    temperatureDataset.setValue(current.get());
	    thermometerPlot.setSubrange(0,low.get(), low.get());
	    thermometerPlot.setSubrange(1, high.get(), high.get());
	});

	lowLabel.setText(low.toString());
	highLabel.setText(high.toString());
    }
}