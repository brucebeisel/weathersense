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
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
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

import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.WeatherTrend;
import com.bdb.weather.display.StageUtilities;
import com.bdb.weather.display.preferences.ColorPreferences;

/**
 * A gauge for displaying barometric pressure
 * 
 * @author Bruce
 * 
 */
public class Barometer extends BorderPane {
    private final DefaultValueDataset dataset = new DefaultValueDataset(50.0);
    private final DialPlot            plot = new DialPlot(dataset);
    private StandardDialRange         range;
    private final Label               title = new Label();
    private StandardDialScale         scale;
    private final DialTextAnnotation  trendAnnotation = new DialTextAnnotation("Trend");
    private final StringProperty      titleProperty = new SimpleStringProperty();
    private final DoubleProperty      minValue = new SimpleDoubleProperty();
    private final DoubleProperty      maxValue = new SimpleDoubleProperty();
    private final ColorPreferences    colorPrefs = ColorPreferences.getInstance();

    public static Barometer create() {
        Barometer b = new Barometer();
        b.setPrefSize(200, 200);
        return b;
    }

    private Barometer() {
	this("", new Pressure(900.0, Pressure.Unit.MILLIBAR), new Pressure(1100.0, Pressure.Unit.MILLIBAR));
    }
    /**
     * Constructor.
     * 
     * @param titleString The title to display with the dial
     * @param min The minimum of the dial's scale
     * @param max The maximum of the dial's scale
     */
    private Barometer(String titleString, Pressure min, Pressure max) {
	title.setText(titleString);

	ChartViewer chartViewer = createChartElements(min, max);
	this.setTop(title);
         this.setCenter(chartViewer);
	BorderPane.setAlignment(title, Pos.CENTER);
    }

    private ChartViewer createChartElements(Pressure min, Pressure max) {
        Color backgroundDialColor = StageUtilities.toAwtColor(colorPrefs.getColorPref(ColorPreferences.GUAGE_DIAL_COLOR));
        Paint backgroundPaint = new GradientPaint(0.0f, 0.0f, Color.LIGHT_GRAY, 100.0f, 0.0f, backgroundDialColor);
        DialBackground background = new DialBackground(backgroundPaint);
        plot.addLayer(background);
        double dialTickIncrements = .2;
        switch (Pressure.getDefaultUnit()) {
            case IN_HG:
                dialTickIncrements = .2;
                break;
            case HECTO_PASCAL:
            case MILLIBAR:
                dialTickIncrements = 10.0;
                break;
        }

        scale = new StandardDialScale(min.get(), max.get(), 240.0, -300.0, dialTickIncrements, 10);
        scale.setTickLabelFont(scale.getTickLabelFont().deriveFont(14.0F).deriveFont(Font.PLAIN));
        scale.setTickRadius(.9);
        scale.setTickLabelFormatter(Pressure.getDefaultFormatter());
        scale.setTickLabelOffset(.25);
        scale.setTickLabelPaint(StageUtilities.toAwtColor(colorPrefs.getColorPref(ColorPreferences.GUAGE_SCALE_TEXT)));
        scale.setMajorTickPaint(StageUtilities.toAwtColor(colorPrefs.getColorPref(ColorPreferences.GUAGE_SCALE_TICK)));
        scale.setMinorTickPaint(StageUtilities.toAwtColor(colorPrefs.getColorPref(ColorPreferences.GUAGE_SCALE_TICK)));
        plot.addScale(0, scale);
        plot.setDialFrame(new StandardDialFrame());
        DialValueIndicator valueInd = new DialValueIndicator(0);
        //
        // Set background and outline paint to be completely transparent so they do not show at all
        //
        valueInd.setBackgroundPaint(new Color(255, 255, 255, 0));
        valueInd.setOutlinePaint(new Color(255, 255, 255, 0));
        valueInd.setPaint(StageUtilities.toAwtColor(colorPrefs.getColorPref(ColorPreferences.GUAGE_VALUE)));
        valueInd.setNumberFormat(Pressure.getDefaultUnit().getFormatterWithUnit());
        plot.addLayer(valueInd);
        
        double angle = valueInd.getAngle();
        double radius = valueInd.getRadius();
        
        trendAnnotation.setPaint(StageUtilities.toAwtColor(colorPrefs.getColorPref(ColorPreferences.GUAGE_VALUE)));
        trendAnnotation.setAngle(angle);
        trendAnnotation.setRadius(radius + .1);
        plot.addLayer(trendAnnotation);
        
        DialPointer.Pointer pointer = new DialPointer.Pointer(0);
        plot.addPointer(pointer);

        DialCap cap = new DialCap();
        cap.setRadius(cap.getRadius() * 1.5);
        plot.setCap(cap);

        range = new StandardDialRange(0.0, 360.0, StageUtilities.toAwtColor(colorPrefs.getColorPref(ColorPreferences.GUAGE_VALUE_RANGE)));
        range.setInnerRadius(.40);
        range.setOuterRadius(.45);
        range.setScaleIndex(0);
        plot.addLayer(range);

        JFreeChart chart = new JFreeChart(plot);
        chart.setBackgroundPaint(StageUtilities.toAwtColor(colorPrefs.getColorPref(ColorPreferences.GUAGE_BACKGROUND)));

        ChartViewer chartViewer = new ChartViewer(chart);
        chartViewer.setMinHeight(200);
        chartViewer.setMinWidth(200);
        chartViewer.setMaxHeight(10000);
        chartViewer.setMaxWidth(10000);

        //chartViewer.setBorder(new BevelBorder(BevelBorder.RAISED));

        return chartViewer;
    }

    public Pressure getMinValue() {
	return new Pressure(minValue.getValue(), Pressure.Unit.MILLIBAR);
    }

    public void setMinValue(Pressure value) {
	minValue.setValue(value.get(Pressure.Unit.MILLIBAR));
	scale.setLowerBound(value.get());
    }

    public DoubleProperty minValueProperty() {
	return minValue;
    }
    
    public Pressure getMaxValue() {
	return new Pressure(maxValue.getValue(), Pressure.Unit.MILLIBAR);
    }

    public void setMaxValue(Pressure value) {
	maxValue.setValue(value.get(Pressure.Unit.MILLIBAR));
	scale.setUpperBound(value.get());
    }

    public DoubleProperty maxValueProperty() {
	return maxValue;
    }
    
    public String getTitle() {
	return titleProperty.getValue();
    }

    public void setTitle(String value) {
	titleProperty.setValue(value);
	title.setText(value);
    }

    public StringProperty titleProperty() {
	return titleProperty;
    }

    /**
     * Set the value of the barometric pressure.
     * 
     * @param current The current pressure
     * @param min The minimum value for the day
     * @param max The maximum value for the day
     * @param delta The change in pressure over the last hour
     * @param trend The direction the value is trending
     */
    public void loadData(Pressure current, Pressure min, Pressure max, Pressure delta, WeatherTrend trend) {
        dataset.setValue(current.get());
        if (!min.equals(max)) {
            range.setBounds(min.get(), max.get());
        }
        
        if (trend == WeatherTrend.STEADY)
            trendAnnotation.setLabel(WeatherTrend.STEADY.toString());
        else {
            String trendText = trend.getSymbol() + delta + "/hr";
            trendAnnotation.setLabel(trendText);
        }
    }
}