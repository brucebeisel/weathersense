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
package com.bdb.weather.display;


import java.awt.BasicStroke;
import java.awt.Color;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import javax.swing.JComponent;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.ui.RectangleEdge;

import com.bdb.util.TimeUtils;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.display.axis.RainRangeAxis;

/**
 * Panel to display historical  rain.
 * 
 * @author Bruce
 *
 */
public class RainPlot {
public static class RainEntry {
    public LocalDateTime time;
    public Depth    rainfall;
    public Depth    rainfallRate;
    public RainEntry(LocalDateTime time, Depth rainfall, Depth rainfallRate) {
        this.time = time;
        this.rainfall = rainfall;
        this.rainfallRate = rainfallRate;
    }
};
private static final String RAIN_DOMAIN = "Rainfall";
private static final String RAIN_RATE_DOMAIN_PREFIX = "Rate (%s/hr)";
private JFreeChart                           chart;
private ChartPanel                           chartPanel;
private CombinedDomainXYPlot                 plot;
private XYPlot                               rainPlot;
private XYPlot                               rainRatePlot;
private TimeSeriesCollection                 rainDataset;
private TimeSeriesCollection                 rainRateDataset;
private TimeSeries                           rainSeries;
private TimeSeries                           rainRateSeries;
private final String                         rateDomain;
private final DateTimeFormatter              formatter = DateTimeFormatter.ofPattern("HH:mm"); // TODO use preferences
    
    /**
     * Class to generate the labels for the tool tips
     * 
     * @author Bruce
     *
     */
    private class PlotLabel implements Comparable<PlotLabel> {
        private final LocalDateTime time;

        /**
         * Constructor.
         * 
         * @param c The date of the data
         */
        public PlotLabel(LocalDateTime time) {
            this.time = time;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(PlotLabel other) {
            return time.compareTo(other.time);
        }

        /**
         * Generate the tool tip string.
         * 
         * @return The tool tip string
         */
        public String toolTip() {
            return formatter.format(time);
        }

        @Override
        public boolean equals(Object other) {
            if (other.getClass() != PlotLabel.class)
                return false;

            return time.equals(((PlotLabel)other).time);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + Objects.hashCode(this.time);
            return hash;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            if (time.getMinute() < 5) // FIXME This does not work with interval < 5 minutes
                return formatter.format(time);
            else
                return "";
        }
    }
    
    /**
     * Constructor.
     */
    @SuppressWarnings("serial")
    public RainPlot() {
        String unitString = Depth.getDefaultUnit().toString();
        rateDomain = String.format(RAIN_RATE_DOMAIN_PREFIX, unitString);
        rainPlot = new XYPlot();
        rainPlot.setRangeAxis(new RainRangeAxis());
        rainRatePlot = new XYPlot();
        rainRatePlot.setRangeAxis(new RainRangeAxis());

        plot = new CombinedDomainXYPlot();
        plot.setDomainAxis(new DateAxis("Time"));

        plot.add(rainRatePlot);
        plot.add(rainPlot);

        chart = new JFreeChart(plot);
        chart.getLegend().setPosition(RectangleEdge.RIGHT);
        
        chartPanel = new ChartPanel(chart);
        chartPanel.setMaximumDrawHeight(10000);
        chartPanel.setMaximumDrawWidth(10000);
        chartPanel.setMinimumDrawHeight(0);
        chartPanel.setMinimumDrawWidth(0);

        rainDataset = new TimeSeriesCollection();
        rainSeries = new TimeSeries(RAIN_DOMAIN);
        rainDataset.addSeries(rainSeries);
        rainPlot.setDataset(rainDataset);

        rainRateDataset = new TimeSeriesCollection();
        rainRateSeries = new TimeSeries(rateDomain);
        rainRateDataset.addSeries(rainRateSeries);
        rainRatePlot.setDataset(rainRateDataset);
        
        XYToolTipGenerator ttg = new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, DateFormat.getTimeInstance(), Depth.getDefaultFormatter());
        
        XYBarRenderer renderer = new XYBarRenderer();
        renderer.setShadowVisible(false);
        renderer.setBarPainter(new StandardXYBarPainter());
        renderer.setBasePaint(Color.BLUE);
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setBaseToolTipGenerator(ttg);
        plot.setRenderer(renderer);
        
        XYItemRenderer rateRenderer = new XYLineAndShapeRenderer(true, false);
        rateRenderer.setBasePaint(Color.RED);
        rateRenderer.setSeriesPaint(0, Color.RED);
        rateRenderer.setBaseToolTipGenerator(ttg);
        rainRatePlot.setRenderer(rateRenderer);
    }
    
    /**
     * Return the swing component that contains the graph.
     * 
     * @return The swing container
     */
    public JComponent getComponent() {
        return chartPanel;
    }
    
    /**
     * Load the rain data.
     * 
     * @param list The list of historical records for the rainfall graph.
     */
    public void setRainData(List<RainEntry> list) {
        rainSeries.clear();
        rainRateSeries.clear();
        
        if (list.size() > 0) {
            rainPlot.clearDomainMarkers();
            //
            // Load the graph
            //
            for (RainEntry r : list) {
                RegularTimePeriod p = RegularTimePeriod.createInstance(Minute.class, TimeUtils.localDateTimeToDate(r.time), TimeZone.getDefault());

                if (r.rainfall != null) {
                    TimeSeriesDataItem item = new TimeSeriesDataItem(p, r.rainfall.get());
                    rainSeries.add(item);
                }

                if (r.rainfallRate != null) {
                    TimeSeriesDataItem item = new TimeSeriesDataItem(p, r.rainfallRate.get());
                    rainRateSeries.add(item);
                }
            }

            rainPlot.getRangeAxis().setAutoRange(true);

            addMarker(list.get(list.size() - 1).time);
        }
    }

    public void addMarker(LocalDateTime markerTime) {
        rainPlot.clearDomainMarkers();
        rainRatePlot.clearDomainMarkers();

        ValueMarker marker = new ValueMarker(TimeUtils.localDateTimeToEpochMillis(markerTime));
        marker.setPaint(new Color(0x8000FF00, true));
        marker.setStroke(new BasicStroke(2.0F));
        rainPlot.addDomainMarker(marker);
        rainRatePlot.addDomainMarker(marker);
        plot.addDomainMarker(marker);
    }
}