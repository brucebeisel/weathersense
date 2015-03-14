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
package com.bdb.weather.display.day;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import com.bdb.weather.common.DayHourRain;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.display.DisplayConstants;
import com.bdb.weather.display.RainItemLabelGenerator;
import com.bdb.weather.display.axis.RainRangeAxis;

/**
 * Class to display a day's worth of rain data.
 * 
 * @author Bruce
 *
 */
public class DayRainPanel {
    private static final int HOUR_COLUMN = 0;
    private static final int RAINFALL_COLUMN = 1;
    private static final int ET_COLUMN = 2;
    private static final String RAIN_ROW_KEY = "Rain";
    private static final String ET_ROW_KEY = "ET";

    private final JTabbedPane         component = new JTabbedPane();
    private final CategoryPlot        rainPlot;
    private final JFreeChart          chart;
    private final ChartPanel          chartPanel;
    private final JTable              dataTable;
    private final NumberAxis          valueAxis = new RainRangeAxis();
    private final DefaultTableModel   tableModel = new DefaultTableModel();
    private final DateTimeFormatter   hourFormatter = DateTimeFormatter.ofPattern("h a");
    private LocalDateTime             timeCache = LocalDate.now().atStartOfDay();
    private final String              tableHeadings[] = {
        "Hour",
        "Rainfall",
        "ET"
    };

    /**
     * Constructor.
     */
    public DayRainPanel() {
        chart = ChartFactory.createBarChart("Water Cycle", "Hour", "", null, PlotOrientation.VERTICAL, true, true, false);

        chartPanel = new ChartPanel(chart);

        rainPlot = (CategoryPlot)chart.getPlot();
        rainPlot.setNoDataMessage("There is no data for the specified day");
        
        BarRenderer renderer = (BarRenderer)rainPlot.getRenderer();
        renderer.setBasePaint(Color.BLUE);
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesPaint(1, Color.RED);
        //renderer.setBaseItemLabelsVisible(true);
        renderer.setSeriesItemLabelGenerator(0, new RainItemLabelGenerator(StandardCategoryItemLabelGenerator.DEFAULT_LABEL_FORMAT_STRING, Depth.getDefaultFormatter()));
        StandardCategoryToolTipGenerator ttgen = new StandardCategoryToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, Depth.getDefaultFormatter());
        rainPlot.getRenderer().setSeriesToolTipGenerator(0, ttgen);

        NumberFormat etFormatter = (NumberFormat)Depth.getDefaultFormatter().clone();
        etFormatter.setMaximumFractionDigits(etFormatter.getMaximumFractionDigits() + 1);
        renderer.setSeriesItemLabelGenerator(1, new RainItemLabelGenerator(StandardCategoryItemLabelGenerator.DEFAULT_LABEL_FORMAT_STRING, etFormatter));
        ttgen = new StandardCategoryToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, etFormatter);
        rainPlot.getRenderer().setSeriesToolTipGenerator(1, ttgen);


        rainPlot.setRangeAxis(valueAxis);
        rainPlot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_90);

        component.addTab(DisplayConstants.GRAPH_TAB_NAME, chartPanel);

        DefaultTableColumnModel colModel = new DefaultTableColumnModel();

        dataTable = new JTable();
        dataTable.setModel(tableModel);
        dataTable.setColumnModel(colModel);
        dataTable.setRowSorter(new TableRowSorter<>(tableModel));

        dataTable.setAutoCreateColumnsFromModel(false);

        for (int i = 0; i < tableHeadings.length; i++) {
            TableColumn col = new TableColumn();
            col.setHeaderValue(tableHeadings[i]);
            col.setModelIndex(i);
            colModel.addColumn(col);
        }

        tableModel.setColumnCount(tableHeadings.length);

        JScrollPane sp = new JScrollPane(dataTable);

        JPanel p = new JPanel(new BorderLayout());

        p.add(sp, BorderLayout.CENTER);

        component.addTab(DisplayConstants.DATA_TAB_NAME, p);
    }
    
    /**
     * Get the swing component that contains this plot.
     * 
     * @return The swing component
     */
    public JComponent getComponent() {
        return component;
    }
    
    /**
     * Build the label for the domain axis
     * 
     * @param hour The hour for which the label will be created
     * @return The axis label
     */
    private String buildHourLabel(int hour) {
        timeCache = timeCache.withHour(hour);
        return hourFormatter.format(timeCache);
    }

    /**
     * Load the data into the plot.
     * 
     * @param data The data to load
     * @param records The list of historical records
     */
    public void loadData(SummaryRecord data, List<HistoricalRecord> records) {
        if (data == null) {
            tableModel.setRowCount(0);
            rainPlot.setDataset(null);
            return;
        }
        
        DefaultCategoryDataset rainDataset = new DefaultCategoryDataset();

        DayHourRain hourlyRain = data.getHourlyRainfall();

        int n = 0;

        Set<Integer> hours = hourlyRain.getHourValues();
        tableModel.setRowCount(hours.size());
        Depth totalET = new Depth(0.0);

        for (int hour : hours) {
            String label = buildHourLabel(hour);
            rainDataset.addValue(hourlyRain.getRain(hour).get(), RAIN_ROW_KEY, label);
            tableModel.setValueAt(label, n, HOUR_COLUMN);
            tableModel.setValueAt(hourlyRain.getRain(hour), n, RAINFALL_COLUMN);
            Depth et = new Depth(0.0);
            for (HistoricalRecord record : records) {
                if (record.getTime().getHour() == n && record.getEvapotranspiration() != null)
                    et = et.add(record.getEvapotranspiration());
            }
            if (et.get() > 0.0)
                tableModel.setValueAt(et, n, ET_COLUMN);
            else
                tableModel.setValueAt(DisplayConstants.UNKNOWN_VALUE_STRING, n, ET_COLUMN);

            rainDataset.addValue(et.get(), ET_ROW_KEY, label);

            n++;
            totalET = totalET.add(et);
        }

        //rainDataset.addValue(data.getTotalRainfall().get(), RAIN_ROW_KEY, "Total");
        //rainDataset.addValue(totalET.get(), ET_ROW_KEY, "Total");

        rainPlot.setDataset(rainDataset);
    }
}
