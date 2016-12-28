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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.text.DecimalFormat;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialCap;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.DialTextAnnotation;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialRange;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.data.general.DefaultValueDataset;

import com.bdb.weather.common.Wind;
import com.bdb.weather.common.measurement.Heading;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.display.CompassHeadingFormat;
import com.bdb.weather.display.DisplayConstants;

/**
 * Gauge for display wind speed, wind gusts and direction.
 * 
 * @author Bruce
 * 
 */
public class WindGauge extends BorderPane {
    private static final int WIND_SPEED_DATASET_INDEX = 0;
    private static final int WIND_GUST_DATASET_INDEX = 1;
    private static final int MAX_WIND_SPEED_DATASET_INDEX = 2;
    private static final int MAX_WIND_GUST_DATASET_INDEX = 3;
    private static final int WIND_DIR_DATASET_INDEX_BASE = 4;
    private static final int WIND_DIR_SCALE = 0;
    private static final int WIND_SPEED_SCALE = 1;
    private static final int WIND_DIR_ITEMS = 5;
    private static final double WIND_SPEED_PIN_RADIUS = .45;
    private static final double WIND_GUST_PIN_RADIUS = .4;
    private static final double MAX_WIND_SPEED_PIN_RADIUS = .45;
    private static final double MAX_WIND_GUST_PIN_RADIUS = .4;
    private static final float WIND_SPEED_PIN_WIDTH = 3.0f;
    private static final float MAX_WIND_SPEED_PIN_WIDTH = 1.0f;
    private static final Color WIND_SPEED_PIN_COLOR = Color.cyan;
    private static final Color WIND_GUST_PIN_COLOR = Color.green;
    private final DialPlot plot;
    private final DefaultValueDataset datasets[] = new DefaultValueDataset[WIND_DIR_ITEMS];
    private final DefaultValueDataset speedDataset = new DefaultValueDataset(0.0);
    private final DefaultValueDataset gustDataset = new DefaultValueDataset(0.0);
    private final DefaultValueDataset maxSpeedDataset = new DefaultValueDataset(0.0);
    private final DefaultValueDataset maxGustDataset = new DefaultValueDataset(0.0);
    private final DialTextAnnotation speedAnnotation = new DialTextAnnotation("");
    private final DialTextAnnotation avgAnnotation = new DialTextAnnotation("");
    private final ChartViewer chartViewer;
    private double lastHeading;
    private double currentHeading;
    private double headingInterval;
    private double lastSpeed;
    private double currentSpeed;
    private double speedInterval;
    private final Label title = new Label();
    private final Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), (actionEvent) -> nextFrame()));
    private final StringProperty titleProperty = new SimpleStringProperty();

    /**
     * Constructor.
     */
    public WindGauge() {
	this.setPrefSize(200.0, 200.0);
        lastHeading = 0.0;
        lastSpeed = 0.0;
        plot = new DialPlot();
        for (int i = 0; i < WIND_DIR_ITEMS; i++) {
            datasets[i] = new DefaultValueDataset();
            plot.setDataset(WIND_DIR_DATASET_INDEX_BASE + i, datasets[i]);
        }
        
        plot.setDataset(WIND_SPEED_DATASET_INDEX, speedDataset);
        plot.setDataset(WIND_GUST_DATASET_INDEX, gustDataset);
        plot.setDataset(MAX_WIND_SPEED_DATASET_INDEX, maxSpeedDataset);
        plot.setDataset(MAX_WIND_GUST_DATASET_INDEX, maxGustDataset);

        plot.addLayer(new DialBackground(new GradientPaint(0.0f, 0.0f, Color.LIGHT_GRAY, 100.0f, 0.0f, Color.blue)));

        StandardDialScale scale = new StandardDialScale(0.0, 360.0, 90.0, -360.0, 45.0, 1);
        scale.setTickLabelFont(scale.getTickLabelFont().deriveFont(14.0F).deriveFont(Font.PLAIN));
        scale.setTickRadius(.9);
        scale.setTickLabelFormatter(new CompassHeadingFormat());
        scale.setTickLabelOffset(0.06);
        scale.setMajorTickPaint(new Color(0, 0, 0, 0));
        scale.setTickLabelPaint(Color.BLACK);
        scale.setMinorTickLength(scale.getMajorTickLength());
        scale.setMinorTickStroke(scale.getMajorTickStroke());
        plot.addScale(WIND_DIR_SCALE, scale);

        scale = new StandardDialScale(0.0, 50.0, 225.0, -270.0, 10.0, 9);
        scale.setTickLabelFont(scale.getTickLabelFont().deriveFont(14.0F).deriveFont(Font.PLAIN));
        scale.setTickRadius(.4);
        scale.setTickLabelFormatter(new DecimalFormat("##"));
        scale.setTickLabelOffset(.15);
        scale.setTickLabelPaint(Color.BLACK);
        plot.addScale(WIND_SPEED_SCALE, scale);

        DialPointer.Pointer pointer;
        for (int i = 1; i < WIND_DIR_ITEMS; i++) {
            pointer = new WindDirPointer(.72, .2, WIND_DIR_DATASET_INDEX_BASE + i, false);
            pointer.setOutlinePaint(Color.RED);
            plot.addPointer(pointer);
        }
        
        plot.setDialFrame(new StandardDialFrame());
        pointer = new WindDirPointer(.72, .2, WIND_DIR_DATASET_INDEX_BASE, true);
        Color fill = Color.CYAN;
        pointer.setFillPaint(fill);
        pointer.setOutlinePaint(Color.BLACK);
        plot.addPointer(pointer);
        

        DialCap cap = new DialCap();
        plot.setCap(cap);

        DialPointer.Pin speedPin = new DialPointer.Pin(WIND_SPEED_DATASET_INDEX);
        speedPin.setPaint(WIND_SPEED_PIN_COLOR);
        speedPin.setRadius(WIND_SPEED_PIN_RADIUS);
        speedPin.setStroke(new BasicStroke(WIND_SPEED_PIN_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        plot.addPointer(speedPin);

        DialPointer.Pin gustPin = new DialPointer.Pin(WIND_GUST_DATASET_INDEX);
        gustPin.setPaint(WIND_GUST_PIN_COLOR);
        gustPin.setRadius(WIND_GUST_PIN_RADIUS);
        gustPin.setStroke(new BasicStroke(WIND_SPEED_PIN_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        plot.addPointer(gustPin);

        DialPointer.Pin maxSpeedPin = new DialPointer.Pin(MAX_WIND_SPEED_DATASET_INDEX);
        maxSpeedPin.setPaint(WIND_SPEED_PIN_COLOR);
        maxSpeedPin.setRadius(MAX_WIND_SPEED_PIN_RADIUS);
        maxSpeedPin.setStroke(new BasicStroke(MAX_WIND_SPEED_PIN_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        plot.addPointer(maxSpeedPin);

        DialPointer.Pin maxGustPin = new DialPointer.Pin(MAX_WIND_GUST_DATASET_INDEX);
        maxGustPin.setPaint(WIND_GUST_PIN_COLOR);
        maxGustPin.setRadius(MAX_WIND_GUST_PIN_RADIUS);
        maxGustPin.setStroke(new BasicStroke(MAX_WIND_SPEED_PIN_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        plot.addPointer(maxGustPin);

        speedAnnotation.setAngle(270.0);
        speedAnnotation.setRadius(.35);
        speedAnnotation.setPaint(Color.cyan);
        plot.addLayer(speedAnnotation);

        double angle = speedAnnotation.getAngle();
        double radius = speedAnnotation.getRadius();

        avgAnnotation.setPaint(Color.cyan);
        avgAnnotation.setAngle(angle);
        avgAnnotation.setRadius(radius + .1);
        plot.addLayer(avgAnnotation);

        for (int i = 0; i < WIND_DIR_ITEMS; i++)
            plot.mapDatasetToScale(WIND_DIR_DATASET_INDEX_BASE + i, WIND_DIR_SCALE);
        
        plot.mapDatasetToScale(WIND_SPEED_DATASET_INDEX, WIND_SPEED_SCALE);
        plot.mapDatasetToScale(WIND_GUST_DATASET_INDEX, WIND_SPEED_SCALE);
        plot.mapDatasetToScale(MAX_WIND_SPEED_DATASET_INDEX, WIND_SPEED_SCALE);
        plot.mapDatasetToScale(MAX_WIND_GUST_DATASET_INDEX, WIND_SPEED_SCALE);

        StandardDialRange range = new StandardDialRange(0.0, 360.0, Color.BLACK);
        range.setInnerRadius(.70);
        range.setOuterRadius(.72);
        range.setScaleIndex(WIND_DIR_SCALE);
        plot.addLayer(range);

        JFreeChart chart = new JFreeChart(plot);
        chart.setBackgroundPaint(Color.GRAY);

        chartViewer = new ChartViewer(chart);
        //chartViewer.setMinHeight(100);
        //chartViewer.setMinWidth(100);
        //chartViewer.setMaxHeight(400);
        //chartViewer.setMaxWidth(400);
        //chartViewer.setBackground(Color.GRAY);
        //chartViewer.setBorder(new BevelBorder(BevelBorder.RAISED));

	this.setCenter(chartViewer);
	this.setTop(title);
	BorderPane.setAlignment(title, Pos.CENTER);
	title.textProperty().bind(titleProperty);
	setTitle("Wind");

	timeline.setCycleCount(9);
        timeline.setOnFinished((event) -> {
            datasets[0].setValue(currentHeading);
            speedDataset.setValue(currentSpeed);
            lastHeading = currentHeading;
            lastSpeed = currentSpeed;
        });
    }

    public final void setTitle(final String title) {
	titleProperty.setValue(title);
    }

    public String getTitle() {
	return titleProperty.getValue();
    }

    public StringProperty titleProperty() {
	return titleProperty;
    }

    /**
     * Load the wind data into the gauge.
     * 
     * @param wind The prevailing wind data
     * @param gust The wind gust data
     * @param maxWindSpeed The maximum wind speed so far this day
     * @param maxWindGust The maximum wind gust so far this day
     * @param avgWindSpeed The average wind speed so far this day
     * @param windDirs The extra wind directions
     */
    public void loadData(Wind wind, Wind gust, Speed maxWindSpeed, Speed maxWindGust, Speed avgWindSpeed, List<Heading> windDirs) {
        if (wind == null)
            return;

        currentHeading = wind.getDirection().get();
        headingInterval = (currentHeading - lastHeading) / 10.0;
        if (headingInterval > 18.0)
            headingInterval -= 36.0;
        else if (headingInterval < -18.0)
            headingInterval += 36.0;

        lastHeading += headingInterval;
        datasets[0].setValue(lastHeading);

        for (int i = 1; i < datasets.length; i++) {
            if (windDirs.size() >= i)
                datasets[i].setValue(windDirs.get(i - 1).get());
            else
                datasets[i].setValue(wind.getDirection().get());
        }
        
        currentSpeed = wind.getSpeed().get();
        speedInterval = (currentSpeed - lastSpeed) / 10.0;
        lastSpeed += speedInterval;

        speedDataset.setValue(lastSpeed);

        String gustValue = DisplayConstants.UNKNOWN_VALUE_STRING;
        if (gust != null) {
            Speed gustSpeed = gust.getSpeed();

            if (gustSpeed != null) {
                gustValue = Speed.getDefaultFormatter().format(gustSpeed.get());
                gustDataset.setValue(gustSpeed.get());
                plot.setDataset(WIND_GUST_DATASET_INDEX, gustDataset);
            }
            else
                plot.setDataset(WIND_GUST_DATASET_INDEX, null);
        }

        if (maxWindSpeed != null)
            maxSpeedDataset.setValue(maxWindSpeed.get());
        else
            maxSpeedDataset.setValue(null);

        if (maxWindGust != null)
            maxGustDataset.setValue(maxWindGust.get());
        else
            maxGustDataset.setValue(null);

        String sustainedValue = Speed.getDefaultFormatter().format(wind.getSpeed().get());
        String s = sustainedValue + "/" + gustValue + " " + Speed.getDefaultUnit();
        speedAnnotation.setLabel(s);

        if (avgWindSpeed != null)
            avgAnnotation.setLabel("Avg: " + avgWindSpeed);
        else
            avgAnnotation.setLabel("");

	timeline.jumpTo(Duration.ZERO);
	timeline.play();
    }

    private void nextFrame() {
        lastHeading += headingInterval;
        datasets[0].setValue(lastHeading);
        lastSpeed += speedInterval;
        speedDataset.setValue(lastSpeed);
    }
}