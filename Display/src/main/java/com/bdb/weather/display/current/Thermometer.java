/* 
 * Copyright (C) 2016 Bruce Beisel
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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

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
    private Temperature               minValue;
    private Temperature               maxValue;
    private final ObjectProperty<Temperature.Unit> unitProperty = new SimpleObjectProperty<>();
 
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
	this.setPrefSize(150.0, 300.0);
        minValue = min;
        maxValue = max;
        unitProperty.setValue(Temperature.getDefaultUnit());
        setUnits(unitProperty.getValue());
	ChartViewer chartViewer = createChartElements();
	this.setCenter(chartViewer);
        HBox p = new HBox();
        p.setAlignment(Pos.CENTER);
        Label label = new Label("High: ");
        label.setStyle("-fx-font-weight: bold");
        p.getChildren().addAll(label, highLabel);
	this.setTop(p);
        p = new HBox();
        p.setAlignment(Pos.CENTER);
        label = new Label("Low: ");
        label.setStyle("-fx-font-weight: bold");
        p.getChildren().addAll(label, lowLabel);
	this.setBottom(p);

        unitProperty.addListener((ObservableValue<? extends Temperature.Unit> observable, Temperature.Unit oldValue, Temperature.Unit newValue) -> {
            setUnits(newValue);
        });
    }
        
    private ChartViewer createChartElements() {
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
        thermometerPlot.setBackgroundPaint(Color.GRAY);
        thermometerPlot.setOutlineVisible(false);

        chart = new JFreeChart(thermometerPlot);
        chart.setTitle(titleProperty.getValue());
        chart.setBackgroundPaint(Color.GRAY);

        ChartViewer chartViewer = new ChartViewer(chart);
        chartViewer.setMinHeight(200);
        chartViewer.setMinWidth(150);
        return chartViewer;
    }

    private void setUnits(Temperature.Unit unit) {
        System.out.println("Setting temperature units");
        thermometerPlot.setRange(minValue.get(unit), maxValue.get(unit));
        thermometerPlot.setValueFormat(unit.getFormatter());
        switch (unit) {
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

    public String getTitle() {
	return titleProperty.getValue();
    }

    public final void setTitle(String title) {
	titleProperty.setValue(title);
        chart.setTitle(title);
    }

    public StringProperty titleProperty() {
	return titleProperty;
    }

    public void setUnit(Temperature.Unit unit) {
        unitProperty.setValue(unit);
    }

    public ObjectProperty<Temperature.Unit> unitProperty() {
        return unitProperty;
    }
    
    public void setMinValue(Temperature min) {
        minValue = min;
    }

    public void setMaxValue(Temperature max) {
        maxValue = max;
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

        temperatureDataset.setValue(current.get(unitProperty.getValue()));
        thermometerPlot.setSubrange(0, low.get(unitProperty.getValue()), low.get(unitProperty.getValue()));
        thermometerPlot.setSubrange(1, high.get(unitProperty.getValue()), high.get(unitProperty.getValue()));

	lowLabel.setText(low.toString(unitProperty.getValue()));
	highLabel.setText(high.toString(unitProperty.getValue()));
    }
}