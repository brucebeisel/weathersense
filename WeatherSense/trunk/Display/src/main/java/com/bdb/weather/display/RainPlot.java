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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import javax.swing.JComponent;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;

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
    private CategoryPlot                         rainPlot;
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
        chart = ChartFactory.createBarChart("", "Time", "", null, PlotOrientation.VERTICAL, true, true, false);
        chart.getLegend().setPosition(RectangleEdge.RIGHT);
        
        chartPanel = new ChartPanel(chart);
        chartPanel.setMaximumDrawHeight(10000);
        chartPanel.setMaximumDrawWidth(10000);
        chartPanel.setMinimumDrawHeight(0);
        chartPanel.setMinimumDrawWidth(0);

        rainPlot = (CategoryPlot)chart.getPlot();
        rainPlot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
        rainPlot.setRangeAxis(0, new RainRangeAxis());
        RainRangeAxis rateAxis = new RainRangeAxis();
        rateAxis.setLabel(rateDomain);
        rainPlot.setRangeAxis(1, rateAxis);
        rainPlot.setDataset(0, new DefaultCategoryDataset());
        rainPlot.setDataset(1, new DefaultCategoryDataset());
        rainPlot.mapDatasetToRangeAxis(0, 0);
        rainPlot.mapDatasetToRangeAxis(1, 1);
        
        StandardCategoryToolTipGenerator tooltip = new StandardCategoryToolTipGenerator() {
            @Override
            public String generateToolTip(CategoryDataset dataset, int row, int column) {
                Depth d = new Depth(dataset.getValue(row, column).doubleValue());
                return ((PlotLabel)dataset.getColumnKey(column)).toolTip() + ", " + d;   
            }
        };
        
        BarRenderer renderer = (BarRenderer)rainPlot.getRenderer(0);
        renderer.setShadowVisible(false);
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setBasePaint(Color.BLUE);
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setBaseToolTipGenerator(tooltip);
        
        LineAndShapeRenderer rateRenderer = new LineAndShapeRenderer(true, false);
        rateRenderer.setBasePaint(Color.RED);
        rateRenderer.setSeriesPaint(0, Color.RED);
        rateRenderer.setBaseToolTipGenerator(tooltip);
        rainPlot.setRenderer(1, rateRenderer);
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
        
        if (list.size() > 0) {
            DefaultCategoryDataset rainfall = new DefaultCategoryDataset();
            DefaultCategoryDataset rate = new DefaultCategoryDataset();

            rainPlot.clearDomainMarkers();
            //
            // Load the graph
            //
            for (RainEntry r : list) {
                PlotLabel plotLabel = new PlotLabel(r.time);
                if (r.rainfall != null)
                    rainfall.addValue(r.rainfall.get(), RAIN_DOMAIN, plotLabel);

                if (r.rainfallRate != null)
                    rate.addValue(r.rainfallRate.get(), rateDomain, plotLabel); // TODO use preferences for in/hr label
            }

            rainPlot.setDataset(0, rainfall);
            rainPlot.setDataset(1, rate);

            addMarker(list.get(list.size() - 1).time);
        }
    }

    public void addMarker(LocalDateTime markerTime) {
        rainPlot.clearDomainMarkers();
        PlotLabel plotLabel = new PlotLabel(markerTime);

        CategoryMarker marker = new CategoryMarker(plotLabel);
        marker.setPaint(new Color(0x8000FF00, true));
        marker.setStroke(new BasicStroke(2.0F));
        rainPlot.addDomainMarker(marker);
    }
}