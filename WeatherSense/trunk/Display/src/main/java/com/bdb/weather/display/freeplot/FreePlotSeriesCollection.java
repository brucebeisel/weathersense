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
package com.bdb.weather.display.freeplot;

import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.data.time.TimeSeriesCollection;

import com.bdb.util.measurement.Unit;

/**
 * Class that handles a collection of series.
 * 
 * @author Bruce
 * @param <T>
 *
 */
public class FreePlotSeriesCollection<T> {
    /**
     * An interface for a factory class to create the series of this collection
     * 
     * @author Bruce
     *
     */
    interface SeriesFactory<T> {
        /**
         * Create a group of series that are part of a dataset group (e.g. Temperature)
         * 
         * @param groupName The name of the group
         * @param stroke The stroke used to draw the plots
         * 
         * @return A list of series
         */
        List<FreePlotSeries<T>> createSeriesGroup(String groupName, Stroke stroke); 
        
        /**
         * Update the renderer for a single series
         * 
         * @param series The series for which the renderer is to be updated
         * @param renderer The renderer to modify
         */
        void updateRenderer(FreePlotSeries<T> series, DefaultXYItemRenderer renderer);
    }

    private final String                groupName;
    private final int                   datasetIndex;
    private TimeSeriesCollection        dataset = new TimeSeriesCollection();
    private List<FreePlotSeries<T>>     series = new ArrayList<>();
    private final DefaultXYItemRenderer renderer;
    private final SeriesFactory         factory;
    private final XYPlot                plot;

    /**
     * Constructor.
     * 
     * @param groupName The name of this group
     * @param units The units of this collection, used to setup the Range Axis
     * @param datasetIndex The dataset index from a JFreePlot perspective
     * @param domainAxisIndex The index of the domain axis, from a JFreeChart perspective
     * @param plot The plot to which the dataset and axis will be added
     * @param stroke The Stroke used to draw the series of this collection
     * @param factory The factory used to create the series
     */
    FreePlotSeriesCollection(String groupName, Unit units, int datasetIndex, int domainAxisIndex, XYPlot plot, Stroke stroke, SeriesFactory<T> factory) {
        this.groupName = groupName;
        this.dataset = new TimeSeriesCollection();
        this.datasetIndex = datasetIndex;
        this.factory = factory;
        this.plot = plot;
        renderer = new DefaultXYItemRenderer();
        renderer.setBaseShapesVisible(false);
        renderer.setBaseToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
        renderer.setDrawSeriesLineAsPath(true);
        plot.setDataset(datasetIndex, dataset);
        plot.setRenderer(datasetIndex, renderer);
        series = factory.createSeriesGroup(groupName, stroke);
        plot.mapDatasetToDomainAxis(datasetIndex, domainAxisIndex);
    }
    
    /**
     * Get the name of this group.
     * 
     * @return The group name
     */
    public String getGroupName() {
        return groupName;
    }
    
    /**
     * Get the renderer used to draw the plots of this group.
     * 
     * @return The renderer
     */
    public AbstractXYItemRenderer getRenderer() {
        return renderer;
    }
    
    /**
     * Get the dataset index for this group.
     * 
     * @return The database index
     */
    public int getDatasetIndex() {
        return datasetIndex;
    }
    
    /**
     * Set the range axis index so that the dataset and the axis can be mapped together
     * 
     * @param rangeAxisIndex The index of the range axis of this collection
     */
    public void setRangeAxisIndex(int rangeAxisIndex) {
        plot.mapDatasetToRangeAxis(datasetIndex, rangeAxisIndex);
    }
    
    /**
     * Load the data for this collection
     * 
     * @param data The data to be loaded into the plot
     */
    public void loadData(List<T> data) {
        dataset.removeAllSeries();
        series.stream().forEach((s) -> {
            s.loadData(data);
        });
    }

    /**
     * Display the data series
     * 
     * @param visibleSeries The list of series (by name) that are supposed to be visible
     */
    public void displayData(List<String> visibleSeries) {
        dataset.removeAllSeries();
        int seriesIndex = 0;
        for (FreePlotSeries s : series) {   
            if (visibleSeries.contains(s.getName())) {
                s.setSeriesIndex(seriesIndex++);
                dataset.addSeries(s.getSeries());
                s.updateRenderer(renderer);
                factory.updateRenderer(s, renderer);
            }
        }
    }
}
