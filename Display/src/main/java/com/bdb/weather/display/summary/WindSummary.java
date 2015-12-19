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
package com.bdb.weather.display.summary;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.TimeZone;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.fx.interaction.ChartMouseEventFX;
import org.jfree.chart.fx.interaction.ChartMouseListenerFX;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import com.bdb.util.TimeUtils;

import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.display.DisplayConstants;
import com.bdb.weather.display.ViewLauncher;
import com.bdb.weather.display.WeatherSenseConstants;
import com.bdb.weather.display.axis.WindSpeedRangeAxis;

/**
 * Plot to display the summary of winds.
 * 
 * @author Bruce
 *
 */
public class WindSummary extends TabPane implements ChartMouseListenerFX {
    private XYPlot                    plot;
    private DateAxis                  dateAxis;
    private NumberAxis                valueAxis;
    private TableView                 dataTable;
    private final DefaultTableModel   tableModel = new DefaultTableModel();
    private final SummaryInterval     interval;
    private final ViewLauncher        launcher;
    private final SummarySupporter    supporter;
    private static final String COLUMN_HEADINGS[] = {
        "Date", "Avg Wind", "Max Wind", "Max Gust"
    };
    private static final int DATE_COLUMN = 0;
    private static final int AVG_WIND_COLUMN = 1;
    private static final int MAX_WIND_COLUMN = 2;
    private static final int MAX_GUST_COLUMN = 3;
    
    private static final int SUSTAINED_WIND_SERIES = 0;
    private static final int GUST_SERIES = 1;
    
    public WindSummary(SummaryInterval interval, ViewLauncher launcher, SummarySupporter supporter) {
        this.interval = interval;
        this.launcher = launcher;
        this.supporter = supporter;
        ChartViewer panel = createPlot();
        Node node = createTable();
        
        Tab tab = new Tab(DisplayConstants.GRAPH_TAB_NAME);
        tab.setContent(panel);
        this.getTabs().add(tab);

        tab = new Tab(DisplayConstants.DATA_TAB_NAME);
        tab.setContent(node);
        this.getTabs().add(tab);
    }
    
    /**
     * Create the plot.
     * 
     * @return The panel that contains the plot
     */
    private ChartViewer createPlot() {
        JFreeChart chart = ChartFactory.createXYLineChart("", "", "", null, PlotOrientation.VERTICAL, true, true, true);
        plot = (XYPlot)chart.getPlot();
        ChartViewer panel = new ChartViewer(chart);
        panel.addChartMouseListener(this);
        
        //
        // Set up the Domain Axis (X)
        //
        dateAxis = new DateAxis("Time");
        dateAxis.setDateFormatOverride(interval.getLegacyFormat());
        dateAxis.setVerticalTickLabels(true);
        plot.setDomainAxis(dateAxis);
        
        valueAxis = new WindSpeedRangeAxis();
        plot.setRangeAxis(valueAxis);
        
        //
        // Set up the renderer to generate tool tips, not show shapes
        //
        DefaultXYItemRenderer renderer = new DefaultXYItemRenderer();
        renderer.setBaseShapesVisible(false);
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                                                                        interval.getLegacyFormat(),
                                                                        Speed.getDefaultFormatter()));
        renderer.setDefaultEntityRadius(1);
        plot.setRenderer(SUSTAINED_WIND_SERIES, renderer);
        
        renderer = new DefaultXYItemRenderer();
        WeatherSenseConstants.configureGustRenderer(renderer, 0);
        plot.setRenderer(GUST_SERIES, renderer);
        
        return panel;
    }
    
    /**
     * Create the JTable.
     * 
     * @return The swing component
     */
    private Node createTable() {
        //
        // Build the table for the data tab
        //
        DefaultTableColumnModel colModel = new DefaultTableColumnModel();
        
        dataTable = new TableView();
        dataTable.setModel(tableModel);
        dataTable.setColumnModel(colModel);

        dataTable.setAutoCreateColumnsFromModel(false);

        //
        // Get the table columns labels from the derived class
        //
        for (int i = 0; i < COLUMN_HEADINGS.length; i++) {
            TableColumn col = new TableColumn();
            col.setHeaderValue(COLUMN_HEADINGS[i]);
            col.setModelIndex(i);
            colModel.addColumn(col);
        }

        tableModel.setColumnCount(COLUMN_HEADINGS.length);

        //
        // Insert the JTable component into a scroll pane so that we have scroll bars
        //
        ScrollPane sp = new ScrollPane(dataTable);
        
        return sp;
    }
    
    /**
     * Load the data into the plot.
     * 
     * @param records The summary records
     */
    public void loadData(List<SummaryRecord> records) {
        TimeSeriesCollection sustainedDataset = new TimeSeriesCollection();
        TimeSeries avgSpeedSeries = new TimeSeries("Average Sustained");
        TimeSeries maxSpeedSeries = new TimeSeries("Maximum Sustained");
        TimeSeriesCollection gustDataset = new TimeSeriesCollection();
        TimeSeries windGustSeries = new TimeSeries("Maximum Gust");
        
        tableModel.setRowCount(records.size());
     
        for (int i = 0; i < records.size(); i++) {
            RegularTimePeriod p = RegularTimePeriod.createInstance(interval.getFreeChartClass(), TimeUtils.localDateTimeToDate(records.get(i).getDate().atStartOfDay()), TimeZone.getDefault());
            maxSpeedSeries.add(p, records.get(i).getMaxWindSpeed().get());
            avgSpeedSeries.add(p, records.get(i).getAvgWindSpeed().get());
            Speed gust = records.get(i).getMaxWindGust();
            
            if (gust != null) {
                windGustSeries.add(p, gust.get());
                tableModel.setValueAt(gust, i, MAX_GUST_COLUMN);
            }
            else
                tableModel.setValueAt(DisplayConstants.UNKNOWN_VALUE_STRING, i, MAX_GUST_COLUMN);
                
            tableModel.setValueAt(interval.getFormat().format(records.get(i).getDate()), i, DATE_COLUMN);
            tableModel.setValueAt(records.get(i).getMaxWindSpeed(), i, MAX_WIND_COLUMN);
            tableModel.setValueAt(records.get(i).getAvgWindSpeed(), i, AVG_WIND_COLUMN);
                     
        }
        sustainedDataset.addSeries(avgSpeedSeries);
        sustainedDataset.addSeries(maxSpeedSeries);
        gustDataset.addSeries(windGustSeries);
        
        plot.setDataset(SUSTAINED_WIND_SERIES, sustainedDataset);
        plot.setDataset(GUST_SERIES, gustDataset);
    }

    @Override
    public void chartMouseClicked(ChartMouseEventFX event) {
        ChartEntity entity = event.getEntity();
        //
        // Was a point on the plot selected?
        //
        if (entity instanceof XYItemEntity) {
            XYItemEntity itemEntity = (XYItemEntity)entity;
            XYDataset dataset = itemEntity.getDataset();
            Number x = dataset.getXValue(itemEntity.getSeriesIndex(), itemEntity.getItem());
            LocalDate date = LocalDate.from(Instant.ofEpochMilli(x.longValue()));
            
            if (event.getTrigger().getClickCount() == 2)
                supporter.launchView(launcher, date);
        }
    }

    @Override
    public void chartMouseMoved(ChartMouseEventFX event) {
        // Do nothing
    }
}
