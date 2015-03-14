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

import java.awt.BorderLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.TimeZone;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.bdb.util.TimeUtils;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.display.DisplayConstants;
import com.bdb.weather.display.ViewLauncher;
import com.bdb.weather.display.axis.RainRangeAxis;

/**
 * Plot to summarize rain over a specified period of time
 */
public class RainSummary implements ChartMouseListener {
    private final JTabbedPane       component = new JTabbedPane();
    private final XYPlot            rainPlot;
    private final JFreeChart        chart;
    private final ChartPanel        chartPanel;
    private final JTable            dataTable;
    private final ValueAxis         valueAxis = new RainRangeAxis();
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final ViewLauncher      viewLauncher;
    private final SummaryInterval   interval;
    private final SummarySupporter  supporter;
    private static final String     RAIN_ROW_KEY = "Rain";
    private static final String     ET_ROW_KEY = "ET";
    private static final int        INTERVAL_COLUMN = 0;
    private static final int        RAINFALL_COLUMN = 1;
    private static final int        ET_COLUMN = 2;
    
    private static final String TABLE_HEADINGS[] = {
        "Date",
        "Rainfall",
        "ET"
    };

 
    @SuppressWarnings("LeakingThisInConstructor")
    public RainSummary(SummaryInterval interval, ViewLauncher launcher, SummarySupporter supporter) {
        this.interval = interval;
        chart = ChartFactory.createXYBarChart("Water Cycle", "Date", true, "", null, PlotOrientation.VERTICAL, true, true, false);
        chartPanel = new ChartPanel(chart);
        chartPanel.addChartMouseListener(this);
        viewLauncher = launcher;
        this.supporter = supporter;

        rainPlot = (XYPlot)chart.getPlot();
        DateAxis dateAxis = (DateAxis)rainPlot.getDomainAxis();
        dateAxis.setDateFormatOverride(interval.getLegacyFormat());
        ClusteredXYBarRenderer renderer = new ClusteredXYBarRenderer();
        rainPlot.setRenderer(renderer);
        //renderer.setBarAlignmentFactor(0.0);
        renderer.setMargin(0.2);
        renderer.setShadowVisible(false);

        StandardXYToolTipGenerator ttgen = new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, interval.getLegacyFormat(), Depth.getDefaultFormatter());
        
        renderer.setBaseToolTipGenerator(ttgen);
        //renderer.setSeriesPaint(0, Color.BLUE); // TODO Use color preferences
        //renderer.setSeriesPaint(1, Color.RED); // TODO Use color preferences
        
        rainPlot.setRangeAxis(valueAxis);

        rainPlot.getDomainAxis().setVerticalTickLabels(true);
        
        component.addTab(DisplayConstants.GRAPH_TAB_NAME, chartPanel);

        DefaultTableColumnModel colModel = new DefaultTableColumnModel();

        dataTable = new JTable();
        dataTable.setModel(tableModel);
        dataTable.setColumnModel(colModel);
        dataTable.setAutoCreateColumnsFromModel(false);

        for (int i = 0; i < TABLE_HEADINGS.length; i++) {
            TableColumn col = new TableColumn();
            col.setHeaderValue(TABLE_HEADINGS[i]);
            col.setModelIndex(i);
            colModel.addColumn(col);
        }

        tableModel.setColumnCount(TABLE_HEADINGS.length);

        JScrollPane sp = new JScrollPane(dataTable);

        JPanel p = new JPanel(new BorderLayout());

        p.add(sp, BorderLayout.CENTER);

        component.addTab(DisplayConstants.DATA_TAB_NAME, p);
    }
    
    /**
     * Get the swing component that contains the plot.
     * 
     * @return The swing container
     */
    public JComponent getComponent() {
        return component;
    }

    /**
     * Load the data into the plot.
     * 
     * @param list The summary data
     */
    public void loadData(List<SummaryRecord> list) {
        TimeSeriesCollection ds = new TimeSeriesCollection();
        TimeSeries etSeries = new TimeSeries(ET_ROW_KEY);
        TimeSeries rainSeries = new TimeSeries(RAIN_ROW_KEY);
        
        int n = 0;

        tableModel.setRowCount(list.size());

        Depth totalRain = new Depth(0.0);
        Depth totalET = new Depth(0.0);

        //
        // Load each record
        //
        for (SummaryRecord rec : list) {
            Depth rain = rec.getTotalRainfall();

            String dateString = interval.getFormat().format(rec.getDate());
            tableModel.setValueAt(dateString, n, INTERVAL_COLUMN);
            RegularTimePeriod tp = RegularTimePeriod.createInstance(interval.getFreeChartClass(), TimeUtils.localDateTimeToDate(rec.getDate()), TimeZone.getDefault());

            if (rain != null) {
                rainSeries.add(tp, rain.get());
                tableModel.setValueAt(Depth.getDefaultFormatter().format(rain.get()), n, RAINFALL_COLUMN);
                totalRain = totalRain.add(rain);
            }

            Depth et = rec.getTotalET();
            if (et != null) {
                etSeries.add(tp, et.get());
                tableModel.setValueAt(Depth.getDefaultFormatter().format(et.get()), n, ET_COLUMN);
                totalET = totalET.add(et);
            }

            n++;
        }

        //ds.addValue(totalRain.get(), RAIN_ROW_KEY, "Total");
        //ds.addValue(totalET.get(), ET_ROW_KEY, "Total");
        ds.addSeries(etSeries);
        ds.addSeries(rainSeries);
        rainPlot.setDataset(ds);
    }

    /*
     * (non-Javadoc)
     * @see org.jfree.chart.ChartMouseListener#chartMouseClicked(org.jfree.chart.ChartMouseEvent)
     */
    @Override
    public void chartMouseClicked(ChartMouseEvent event) {
        ChartEntity entity = event.getEntity();
        //
        // Was a point on the plot selected?
        //
        if (entity instanceof CategoryItemEntity) {
            try {
                CategoryItemEntity itemEntity = (CategoryItemEntity)entity;
                LocalDate date = LocalDate.from(interval.getFormat().parse((String)itemEntity.getColumnKey()));

                if (event.getTrigger().getClickCount() == 2)
                    supporter.launchView(viewLauncher, date);
            }
            catch (DateTimeParseException e) {
                // This will never happen because the same date formatter is used to create the category labels and parse the column key
            }
            
        }
    }

    /*
     * (non-Javadoc)
     * @see org.jfree.chart.ChartMouseListener#chartMouseMoved(org.jfree.chart.ChartMouseEvent)
     */
    @Override
    public void chartMouseMoved(ChartMouseEvent event) {
        // Do nothing with mouse movement
    }
}
