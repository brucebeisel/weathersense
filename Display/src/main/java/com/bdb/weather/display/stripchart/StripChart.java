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
package com.bdb.weather.display.stripchart;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleEdge;

/**
 * Display a strip chart with up to two separate Y axes.
 * 
 * @author Bruce
 */
class StripChart extends ChartPanel {
    public static final boolean MAP_TO_LEFT_AXIS = true;
    public static final boolean MAP_TO_RIGHT_AXIS = false;
    private final JFreeChart chart;
    private final XYPlot plot = new XYPlot();
    private final DateAxis dateAxis;
    private final TimeSeriesCollection leftAxisCollection = new TimeSeriesCollection();
    private final TimeSeriesCollection rightAxisCollection = new TimeSeriesCollection();
    private final Map<String, TimeSeries> series = new TreeMap<>();
    private int span;
    private final int maxSpanHours;

    /**
     * Constructor.
     * 
     * @param leftAxisType The type of data that is associated with the left axis
     * @param rightAxisType The type of data that is associated with the right axis
     * @param categorySpanHours How many hours that the X axis should initially display
     * @param maxSpanHours The maximum amount of data to keep in the plots dataset
     */
    public StripChart(MeasurementType leftAxisType, MeasurementType rightAxisType, int categorySpanHours, int maxSpanHours) {
        super(null, true);
        span = categorySpanHours;
        this.maxSpanHours = maxSpanHours;

        //
        // Set up the Y axes
        //
        NumberAxis axis = null;
        if (leftAxisType != MeasurementType.NONE) {
            axis = leftAxisType.createRangeAxis();
            plot.setRangeAxis(0, axis);
        }

        if (rightAxisType != MeasurementType.NONE) {
            axis = rightAxisType.createRangeAxis();
            plot.setRangeAxis(1, axis);
        }

        //
        // Set up the X axis
        //
        dateAxis = new DateAxis("Time");
        dateAxis.setAutoRange(false);
        dateAxis.setDateFormatOverride(new SimpleDateFormat("h:mm a"));
        dateAxis.setVerticalTickLabels(true);
        dateAxis.setUpperMargin(.10);

        plot.setDomainAxis(dateAxis);
        adjustDateAxis(Calendar.getInstance());
        chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, true);

        //
        // TODO figure out how to manage the renderers
        //
        DefaultXYItemRenderer renderer = new DefaultXYItemRenderer();
        renderer.setBaseShapesVisible(false);
        
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                                                                        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG),
                                                                        axis.getNumberFormatOverride()));
        renderer.setDefaultEntityRadius(1);
        plot.setRenderer(0, renderer);
        plot.setDataset(0, leftAxisCollection);

        renderer = new DefaultXYItemRenderer();
        renderer.setBaseShapesVisible(false);
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                                                                        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG),
                                                                        axis.getNumberFormatOverride()));
        renderer.setDefaultEntityRadius(1);
        renderer.setBasePaint(Color.BLUE);
        plot.setRenderer(1, renderer);
        plot.setDataset(1, rightAxisCollection);

        ChartFactory.getChartTheme().apply(chart);
        setChart(chart);
        chart.getLegend().setPosition(RectangleEdge.RIGHT);
        plot.mapDatasetToRangeAxis(0, 0);
        plot.mapDatasetToRangeAxis(1, 1);
    }

    /**
     * Adjust the range of the domain axis based on the current time and the span selected by the user.
     * 
     * @param t0 The current time
     */
    private void adjustDateAxis(Calendar t0) {
        Calendar now = t0;
        Calendar intercept = (Calendar) now.clone();
        intercept.add(Calendar.HOUR, -span);
        now.add(Calendar.MINUTE, 5);
        dateAxis.setRange(intercept.getTime(), now.getTime());
    }

    /**
     * Add a series to either the right or left dataset.
     * 
     * @param seriesName The name of the series
     * @param mapToLeftAxis Whether this is for the left or right axis
     * @param color The color to display the line
     */
    public void addSeries(String seriesName, boolean mapToLeftAxis, Color color) {
        TimeSeries timeSeries = new TimeSeries(seriesName);
        series.put(seriesName, timeSeries);
        timeSeries.setMaximumItemAge(maxSpanHours * 3600);
    }

    /**
     * Hide the specified series.
     * 
     * @param seriesName The name of the series
     * @param leftAxis Whether this series is mapped to the left of right axis
     */
    public void hideSeries(String seriesName, boolean leftAxis) {
        TimeSeries timeSeries = series.get(seriesName);

        if (timeSeries != null) {
            TimeSeriesCollection dataset;
            if (leftAxis) {
                dataset = leftAxisCollection;
            }
            else {
                dataset = rightAxisCollection;
            }

            dataset.removeSeries(timeSeries);
        }
    }

    /**
     * Show the specified series.
     * 
     * @param seriesName The name of the series
     * @param leftAxis Whether this series is mapped to the left of right axis
     */
    public void showSeries(String seriesName, boolean leftAxis) {
        TimeSeries timeSeries = series.get(seriesName);

        if (timeSeries != null) {
            TimeSeriesCollection dataset;
            if (leftAxis) {
                dataset = leftAxisCollection;
            }
            else {
                dataset = rightAxisCollection;
            }

            dataset.addSeries(timeSeries);
        }
    }

    /**
     * Add an item to a series.
     * 
     * @param seriesName The name of the series to which the data is to be added
     * @param time The time of the data
     * @param value The value of the data
     */
    public void addItem(String seriesName, LocalDateTime time, double value) {
        TimeSeries timeSeries = series.get(seriesName);
        Instant instant = Instant.from(time.atZone(ZoneId.systemDefault()));
        Date res = Date.from(instant);

        if (timeSeries != null) {
            timeSeries.removeAgedItems(false);
            timeSeries.addOrUpdate(new Second(res), value);
            Calendar c = Calendar.getInstance();
            c.setTime(res);
            adjustDateAxis(c);
        }
    }

    /**
     * Refresh the plots
     */
    public void refresh() {
        for (TimeSeries timeSeries : (List<TimeSeries>) leftAxisCollection.getSeries()) {
            timeSeries.fireSeriesChanged();
        }
    }

    /**
     * Set the span of the domain axis.
     * 
     * @param span The new domain axis span in hours
     */
    public void setSpanHours(int span) {
        this.span = span;
        adjustDateAxis(Calendar.getInstance());
    }

    public static void main(String args[]) {
        final StripChart chart = new StripChart(MeasurementType.TEMPERATURE, MeasurementType.HUMIDITY, 1, 10);
        chart.addSeries("Outdoor", MAP_TO_LEFT_AXIS, Color.BLUE);
        chart.addSeries("Humidity", MAP_TO_RIGHT_AXIS, Color.CYAN);
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(chart);
            frame.pack();
            frame.setVisible(true);
        });

        int delay = 1000; //milliseconds
        ActionListener taskPerformer = new ActionListener() {

            int timeOffset = 10 * 3600;
            int counter = 0;

            @Override
            public void actionPerformed(ActionEvent evt) {
                LocalDateTime now = LocalDateTime.now().minusSeconds(timeOffset);
                chart.addItem("Outdoor", now, now.atZone(ZoneId.systemDefault()).toEpochSecond());
                chart.refresh();
                System.out.println("Added new item and refreshed");
                timeOffset -= 300;
                if (++counter == 10) {
                    chart.setSpanHours(48);
                }
                else if (counter == 30) {
                    chart.setSpanHours(24);
                }
            }
        };
        new Timer(delay, taskPerformer).start();
    }
}