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
import java.text.DecimalFormat;

import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialCap;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.DialTextAnnotation;
import org.jfree.chart.plot.dial.DialValueIndicator;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialRange;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.data.general.DefaultValueDataset;

import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.common.WeatherTrend;

/**
 * Gauge for displaying humidity.
 * 
 * @author Bruce
 *
 */
public class Hygrometer extends BorderPane {
    private final DefaultValueDataset humidityDataset = new DefaultValueDataset(50.0);
    private final DialPlot            humidityPlot = new DialPlot(humidityDataset);
    private StandardDialRange         range;
    private final DialTextAnnotation  trendAnnotation = new DialTextAnnotation("Trend");
    private final Label               title = new Label();
    private final StringProperty      titleProperty = new SimpleStringProperty();

    public Hygrometer() {
	this("");
    }

    /**
     * Constructor.
     * 
     * @param titleString The title to add to the panel that contains the gauge
     */
    public Hygrometer(String titleString) {
	this.setPrefSize(250.0, 250.0);

	SwingNode swingNode = new SwingNode();
	SwingUtilities.invokeLater(()-> createChartElements(swingNode));
	this.setTop(title);
	this.setCenter(swingNode);
	BorderPane.setAlignment(title, Pos.CENTER);
	title.textProperty().bind(titleProperty);
	setTitle(titleString);
    }

    private void createChartElements(SwingNode swingNode) {
        humidityPlot.addLayer(new DialBackground(new GradientPaint(0.0f, 0.0f, Color.LIGHT_GRAY, 100.0f, 0.0f, Color.blue)));
        StandardDialScale scale = new StandardDialScale(Humidity.MIN_HUMIDITY.get(), Humidity.MAX_HUMIDITY.get(), 240.0, -300.0, 10.0, 9);
        scale.setTickRadius(.9);
        scale.setTickLabelFormatter(new DecimalFormat("#"));
        scale.setTickLabelOffset(.2);
        scale.setTickLabelPaint(Color.BLACK);
        humidityPlot.addScale(0, scale);
        humidityPlot.setDialFrame(new StandardDialFrame());
        
        DialValueIndicator valueInd = new DialValueIndicator(0);
        valueInd.setNumberFormat(new DecimalFormat("# '%rH'"));
        Color c = new Color(255, 255, 255, 0);
        valueInd.setBackgroundPaint(c);
        valueInd.setOutlinePaint(c);
        valueInd.setPaint(Color.cyan);
        humidityPlot.addLayer(valueInd);
        
        double angle = valueInd.getAngle();
        double radius = valueInd.getRadius();
        
        trendAnnotation.setPaint(Color.cyan);
        trendAnnotation.setAngle(angle);
        trendAnnotation.setRadius(radius + .1);
        humidityPlot.addLayer(trendAnnotation);
        
        DialPointer.Pointer pointer = new DialPointer.Pointer(0);
        humidityPlot.addPointer(pointer);
        
        DialCap cap = new DialCap();
        cap.setRadius(cap.getRadius() * 1.5);
        humidityPlot.setCap(cap);
        
        range = new StandardDialRange(Humidity.MIN_HUMIDITY.get(), Humidity.MAX_HUMIDITY.get(), Color.BLACK);
        range.setInnerRadius(.40);
        range.setOuterRadius(.45);
        range.setScaleIndex(0);
        humidityPlot.addLayer(range);

        JFreeChart chart = new JFreeChart(humidityPlot);
        chart.setBackgroundPaint(Color.GRAY);
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMinimumDrawHeight(250);
        chartPanel.setMinimumDrawWidth(250);
        chartPanel.setBackground(Color.GRAY);
        chartPanel.setBorder(new BevelBorder(BevelBorder.RAISED));

	swingNode.setContent(chartPanel);
    }
    
    public String getTitle() {
	return titleProperty.getValue();
    }

    public final void setTitle(String title) {
	titleProperty.setValue(title);
    }

    public StringProperty titleProperty() {
	return titleProperty;
    }

    /**
     * Load the current value and the min and max values of the day.
     * 
     * @param current The current value
     * @param min The minimum value of the day
     * @param max The maximum value of the day
     * @param delta The amount the pressure has changed over the last hour
     * @param trend The current trend of the barometer
     */
    public void loadData(Humidity current, Humidity min, Humidity max, Humidity delta, WeatherTrend trend) {
	SwingUtilities.invokeLater(() -> {
	    humidityDataset.setValue(current.get());
	    
	    if (min != null && max != null) {
		if (!min.equals(max)) {
		    humidityPlot.addLayer(range);
		    range.setLowerBound(min.get());
		    range.setUpperBound(max.get());
		}
		else if (humidityPlot.getLayerIndex(range) >= 0)
		    humidityPlot.removeLayer(range);
	    }
	    
	    if (trend == WeatherTrend.STEADY)
		trendAnnotation.setLabel(WeatherTrend.STEADY.toString());
	    else {
		String trendText = trend.getSymbol() + delta + "/hr";
		trendAnnotation.setLabel(trendText);
	    }
	});
    }
}