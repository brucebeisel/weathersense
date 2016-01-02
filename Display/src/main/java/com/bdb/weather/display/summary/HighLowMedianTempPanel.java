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

import java.awt.Color;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.TimeZone;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import org.jfree.chart.ChartFactory;
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
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import com.bdb.util.TimeUtils;

import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.WeatherAverage;
import com.bdb.weather.common.WeatherAverages;
import com.bdb.weather.common.measurement.Temperature;
import com.bdb.weather.display.ChartDataPane;
import com.bdb.weather.display.ViewLauncher;
import com.bdb.weather.display.axis.TemperatureRangeAxis;

/**
 * JavaFX component that displays a graph of actual high/low/mean temperatures against location averages (sometimes referred to as "normal").
 * 
 * @author Bruce
 *
 */
public class HighLowMedianTempPanel extends ChartDataPane implements ChartMouseListenerFX { 
    private static final int LOW_DATASET = 0;
    private static final int HIGH_DATASET = 1;
    private static final int MEDIAN_DATASET = 2;
    private static final int ACTUAL_SERIES = 0;
    private static final int AVERAGE_SERIES = 1;
    private static final int DATE_COLUMN = 0;
    private static final int HIGH_TEMP_COLUMN = 1;
    private static final int AVG_HIGH_TEMP_COLUMN = 2;
    private static final int LOW_TEMP_COLUMN = 3;
    private static final int AVG_LOW_TEMP_COLUMN = 4;
    private static final int MEDIAN_TEMP_COLUMN = 5;
    private static final int AVG_MEAN_TEMP_COLUMN = 6;
    
    private final XYPlot               plot;
    private final JFreeChart           chart;
    private final ChartViewer          chartViewer;
    private final TableView            dataTable;
    private final DateAxis             dateAxis;
    private final NumberAxis           valueAxis = new TemperatureRangeAxis();
    //private final DefaultTableModel    tableModel = new DefaultTableModel();
    protected DateTimeFormatter        dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    private final String               tableHeadings[];
    private final XYDifferenceRenderer diffRenderer = new XYDifferenceRenderer(new Color(100, 200, 100, 128), new Color(200, 100, 100, 128), false);
    private TimeSeriesCollection       highDataset;
    private TimeSeriesCollection       lowDataset;
    private TimeSeriesCollection       meanDataset;
    private final ViewLauncher         viewLauncher;
    private final SummaryInterval      interval;
    private final SummarySupporter     supporter;

    /**
     * Constructor.
     * 
     * @param interval The interval for which this graph is being used. Intervals are typically day, week, month or year.
     * @param launcher A class that is used to launch sub-views when a data item is double-clicked
     * @param supporter A class that aids in the generalization of this graph
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public HighLowMedianTempPanel(SummaryInterval interval, ViewLauncher launcher, SummarySupporter supporter) {
        this.setPrefSize(500, 300);
        this.interval = interval;
        tableHeadings = getTableColumnLabels();
        chart = ChartFactory.createXYLineChart("", "", "", null, PlotOrientation.VERTICAL, true, true, true);
        plot = (XYPlot)chart.getPlot();
        viewLauncher = launcher;
        this.supporter = supporter;

        StandardXYToolTipGenerator ttgen = new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                                                                          interval.getLegacyFormat(),
                                                                          Temperature.getDefaultFormatter());
        
        diffRenderer.setBaseToolTipGenerator(ttgen);
        
        plot.setRenderer(LOW_DATASET, diffRenderer);
        plot.setRenderer(HIGH_DATASET, diffRenderer);
        plot.setRenderer(MEDIAN_DATASET, diffRenderer);

        valueAxis.setAutoRangeIncludesZero(false);

        plot.setRangeCrosshairLockedOnData(true);
        plot.setRangeCrosshairVisible(true); plot.setDomainCrosshairLockedOnData(true);
        plot.setDomainCrosshairVisible(true);

        plot.setRangeAxis(valueAxis);
        dateAxis = new DateAxis("Date");
        dateAxis.setDateFormatOverride(interval.getLegacyFormat());
        dateAxis.setVerticalTickLabels(true);
        //dateAxis.setTickUnit(interval.getDateTickUnit());

        plot.setDomainAxis(dateAxis);
        
        chartViewer = new ChartViewer(chart);
        chartViewer.setPrefSize(500, 300);
        chartViewer.addChartMouseListener(this);


        //DefaultTableColumnModel colModel = new DefaultTableColumnModel();

        dataTable = new TableView();
        //dataTable.setModel(tableModel);
        //dataTable.setColumnModel(colModel);

        //dataTable.setAutoCreateColumnsFromModel(false);

        for (int i = 0; i < tableHeadings.length; i++) {
            TableColumn col = new TableColumn();
            //col.setHeaderValue(tableHeadings[i]);
            //col.setModelIndex(i);
            //colModel.addColumn(col);
        }

        this.setTabContents(chartViewer, dataTable);
    }
    
    /**
     * Get the labels for the JTable containing the values.
     * 
     * @return An array of strings that are the column labels
     */
    private String[] getTableColumnLabels() {
        String labels[] = { "Date", "High Temp", "Seasonal High Temp", "Low Temp", "Seasonal Low Temp", "Mean Temp", "Seasonal Mean Temp"
        };

        return labels;
    }

    /**
     * Load the data into the graph and the JTable.
     * 
     * @param summaryList The list of summary records to be loaded
     * @param lowDataset The graph's dataset that holds the low temperature values
     * @param highDataset The graph's dataset that holds the high temperature values
     * @param meanDataset The graph's dataset that holds the mean temperature values
     * @param tableModel The JTable model to be loaded
     * @param averagesCollection The averages to load into the graph and table
     */
    private void loadDataSeries(List<SummaryRecord> summaryList,
                                TimeSeriesCollection lowDataset,
                                TimeSeriesCollection highDataset,
                                TimeSeriesCollection meanDataset,
                                WeatherAverages averagesCollection) {

        TimeSeries highSeries = new TimeSeries("High");
        TimeSeries lowSeries = new TimeSeries("Low");
        TimeSeries meanSeries = new TimeSeries("Mean");
        TimeSeries averageLowSeries = new TimeSeries("Average Low");
        TimeSeries averageHighSeries = new TimeSeries("Average High");
        TimeSeries averageMeanSeries = new TimeSeries("Average Mean");
        
        int n = 0;

        for (SummaryRecord r : summaryList) {
            //tableModel.setValueAt(dateFormat.format(r.getDate()), n, DATE_COLUMN);

            RegularTimePeriod p = RegularTimePeriod.createInstance(interval.getFreeChartClass(), TimeUtils.localDateTimeToDate(r.getDate().atStartOfDay()), TimeZone.getDefault());

            WeatherAverage averages = averagesCollection.getAverage(r.getDate());
            averageLowSeries.add(p, averages.getLowTemperature().get());
            averageHighSeries.add(p, averages.getHighTemperature().get());
            averageMeanSeries.add(p, averages.getMeanTemperature().get());
            //tableModel.setValueAt(averages.getHighTemperature().toString(), n, AVG_HIGH_TEMP_COLUMN);
            //tableModel.setValueAt(averages.getLowTemperature().toString(), n, AVG_LOW_TEMP_COLUMN);
            //tableModel.setValueAt(averages.getMeanTemperature().toString(), n, AVG_MEAN_TEMP_COLUMN);

            Temperature t = supporter.retrieveHighOutdoorTemperature(r);

            if (t != null) {
                highSeries.add(p, t.get());
                //tableModel.setValueAt(t.toString(), n, HIGH_TEMP_COLUMN);
            }

            t = supporter.retrieveLowOutdoorTemperature(r);

            if (t != null) {
                lowSeries.add(p, t.get());
                //tableModel.setValueAt(t.toString(), n, LOW_TEMP_COLUMN);
            }

            t = supporter.retrieveAvgOutdoorTemperature(r);

            if (t != null) {
                meanSeries.add(p, t.get());
                //tableModel.setValueAt(t.toString(), n, MEDIAN_TEMP_COLUMN);
            }

            n++;
        }
        
        highDataset.addSeries(highSeries);
        highDataset.addSeries(averageHighSeries);
        
        lowDataset.addSeries(lowSeries);
        lowDataset.addSeries(averageLowSeries);
        
        meanDataset.addSeries(meanSeries);
        meanDataset.addSeries(averageMeanSeries);
        
        
        diffRenderer.setSeriesPaint(ACTUAL_SERIES, Color.RED);
        diffRenderer.setSeriesPaint(AVERAGE_SERIES, Color.RED.darker());
        
        diffRenderer.setSeriesPaint(ACTUAL_SERIES, Color.BLUE);
        diffRenderer.setSeriesPaint(AVERAGE_SERIES, Color.BLUE.darker());
        
        diffRenderer.setSeriesPaint(ACTUAL_SERIES, Color.GREEN);
        diffRenderer.setSeriesPaint(AVERAGE_SERIES, Color.GREEN.darker());
    }

    /**
     * Load the data into the graph and table.
     * 
     * @param summaryList The list of summary records to load
     * @param averages The averages to load
     */
    public void loadData(List<SummaryRecord> summaryList, WeatherAverages averages) {
        //tableModel.setRowCount(summaryList.size());

        highDataset = new TimeSeriesCollection();
        lowDataset = new TimeSeriesCollection();
        meanDataset = new TimeSeriesCollection();

        loadDataSeries(summaryList, lowDataset, highDataset, meanDataset, averages);

        plot.setDataset(LOW_DATASET, lowDataset);
        plot.setDataset(HIGH_DATASET, highDataset);
        plot.setDataset(MEDIAN_DATASET, meanDataset);
    }

    /*
     * (non-Javadoc)
     * @see org.jfree.chart.ChartMouseListener#chartMouseClicked(org.jfree.chart.ChartMouseEvent)
     */
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
            LocalDateTime time = LocalDateTime.from(Instant.ofEpochMilli(x.longValue()));
            boolean doubleClick = event.getTrigger().getClickCount() == 2;
            if (doubleClick)
                supporter.launchView(viewLauncher, time.toLocalDate());
        }
    }

    /*
     * (non-Javadoc)
     * @see org.jfree.chart.ChartMouseListener#chartMouseMoved(org.jfree.chart.ChartMouseEvent)
     */
    @Override
    public void chartMouseMoved(ChartMouseEventFX event) {
        // Do nothing   
    }
}
