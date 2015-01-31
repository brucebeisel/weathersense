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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.bdb.util.TimeUtils;
import com.bdb.util.measurement.Measurement;

import com.bdb.weather.common.DailyRecords;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.WeatherAverage;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.display.DisplayConstants;

/**
 * An XY Plot for a single day of data. This class provides a tabbed pane, one pane for the plot
 * the other table contains a JTable for the data. This plot has day/night indicators on the plot.
 * These indicators can be turned off using the plots context menu.
 * 
 * @author Bruce
 */
abstract public class DayXYPlotPanel implements ActionListener {
    protected static final String TIME_HEADING = "Time";
    protected static final int TIME_COLUMN = 0;

    private JTabbedPane          component;
    private XYPlot               plot;
    private JFreeChart           chart;
    private ChartPanel           chartPanel;
    private JTable               dataTable;
    private DateAxis             dateAxis;
    private final ValueAxis      leftAxis;
    private final ValueAxis      rightAxis;
    private DefaultTableModel    tableModel;
    private JMenu                displayMenu;
    private boolean              displayDayNightIndicators = true;
    private JCheckBoxMenuItem    dayNightItem;
    private LocalDate            currentDate;
    private final TimeSeriesCollection datasetLeft;
    private final TimeSeriesCollection datasetRight;
    private List<SeriesEntry>    entries;
    private final WeatherStation ws;

    protected DayXYPlotPanel(WeatherStation ws, ValueAxis leftAxis, ValueAxis rightAxis) {
        this.ws = ws;
        this.leftAxis = leftAxis;
        this.rightAxis = rightAxis;
        datasetLeft = new TimeSeriesCollection();
        datasetRight = new TimeSeriesCollection();
        entries = new ArrayList<>();
    }
        
    public void createElements() {
        //
        // Set up the Domain Axis (X)
        //
        plot = new XYPlot();
        tableModel = new DefaultTableModel();
        component = new JTabbedPane();
        dateAxis = new DateAxis("Time");
        dateAxis.setAutoRange(false);
        dateAxis.setTickUnit(new DateTickUnit(DateTickUnitType.HOUR, 1, new SimpleDateFormat("h a")));
        dateAxis.setVerticalTickLabels(true);
        plot.setDomainAxis(dateAxis);
        plot.setRangeAxis(leftAxis);
        plot.setDataset(0, datasetLeft);
        if (rightAxis != null) {
            plot.setRangeAxis(1, rightAxis);
            plot.mapDatasetToRangeAxis(1, 1);
            plot.setDataset(1, datasetRight);
        }
        plot.setNoDataMessage("There is no data for the specified day");

        //
        // Set up the renderer to generate tool tips, not show shapes
        //
        DefaultXYItemRenderer renderer = new DefaultXYItemRenderer();
        renderer.setBaseShapesVisible(false);
        renderer.setBaseToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
        renderer.setDefaultEntityRadius(1);
        plot.setRenderer(0, renderer);

        renderer = new DefaultXYItemRenderer();
        renderer.setBaseShapesVisible(false);
        renderer.setBaseToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
        renderer.setDefaultEntityRadius(1);
        plot.setRenderer(1, renderer);

        //
        // Setup the cross hairs that are displayed when the user clicks on the plot
        //
        plot.setRangeCrosshairLockedOnData(true);
        plot.setRangeCrosshairVisible(true);
        plot.setDomainCrosshairLockedOnData(true);
        plot.setDomainCrosshairVisible(true);

        //
        // Create the chart that contains the plot and the panel that contains the chart
        //
        chart = new JFreeChart(plot);
        ChartFactory.getChartTheme().apply(chart);
        chartPanel = new ChartPanel(chart);

        //chartPanel.setMaximumDrawHeight(10000);
        //chartPanel.setMaximumDrawWidth(10000);

        //
        // Create a panel to hold the chart panel
        //
        JPanel p = new JPanel(new BorderLayout());

        p.add(chartPanel, BorderLayout.CENTER);

        component.addTab(DisplayConstants.GRAPH_TAB_NAME, p);

        //
        // Build the table for the data tab
        //
        DefaultTableColumnModel colModel = new DefaultTableColumnModel();
        
        dataTable = new JTable();
        dataTable.setModel(tableModel);
        dataTable.setColumnModel(colModel);

        dataTable.setAutoCreateColumnsFromModel(false);

        //
        // Insert the JTable component into a scroll pane so that we have scroll bars
        //
        JScrollPane sp = new JScrollPane(dataTable);

        p = new JPanel(new BorderLayout());

        p.add(sp, BorderLayout.CENTER);

        component.addTab(DisplayConstants.DATA_TAB_NAME, p);

        //
        // Add the Day/Night indicator option to the chart panels context menu
        //
        JPopupMenu menu = chartPanel.getPopupMenu();

        displayMenu = new JMenu("Display");
        menu.add(displayMenu);

        dayNightItem = new JCheckBoxMenuItem("Day/Night Indicators", true);
        displayMenu.add(dayNightItem);
        dayNightItem.addActionListener(this);

        TableColumn col = new TableColumn();
        col.setHeaderValue(TIME_HEADING);
        col.setModelIndex(TIME_COLUMN);
        colModel.addColumn(col);
        doConfigure(displayMenu, colModel);

        tableModel.setColumnCount(entries.size() + 1);
        dataTable.setRowSorter(new TableRowSorter<>(tableModel));
    }
    
    private void doConfigure(JMenu menu, DefaultTableColumnModel colModel) {
        List<SeriesControl> controls = configure(displayMenu);
        int tableColumn = 1;
        for (SeriesControl control : controls) {
            HistoricalSeriesInfo info = HistoricalSeriesInfo.find(control.name);
            if (info != null) {
                TimeSeries timeSeries = new TimeSeries(info.getSeriesName());
                JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(info.getSeriesName());
                menuItem.setSelected(control.displayInitially);
                SeriesEntry entry = new SeriesEntry(info, timeSeries, tableColumn, menuItem, control.leftAxis);
                entries.add(entry);

                TableColumn col = new TableColumn();
                col.setHeaderValue(entry.seriesInfo.getSeriesName());
                col.setModelIndex(tableColumn);
                colModel.addColumn(col);

                menu.add(menuItem);
                menuItem.addActionListener(this);
                tableColumn++;
            }
        }
    }

    protected abstract List<SeriesControl> configure(JMenu menu);


    /**
     * Return the Swing component that contains the Day XY Plot.
     * 
     * @return The component
     */
    public JComponent getComponent() {
        return component;
    }

    /**
     * Get the plot used by this panel
     * 
     * @return The plot
     */
    protected XYPlot getPlot() {
        return plot;
    }

    /**
     * Get the ChartPanel for this plot.
     * 
     * @return The chart panel
     */
    protected ChartPanel getChartPanel() {
        return chartPanel;
    }

    /**
     * Get the menu that pops up when the user right-clicks on the plot.
     * 
     * @return The Swing menu
     */
    protected JMenu getDisplayMenu() {
        return displayMenu;
    }

    /**
     * Called to add extreme markers to the plot. This would typically display record high and lows for the day.
     * 
     * @param plot The plot
     * @param records
     * @param averages
     */
    protected void addExtremeMarkers(XYPlot plot, DailyRecords records, WeatherAverage averages) {
    }

    /**
     * Add annotations to the plot. This is typically used to add annotations for the high and low values of the day.
     * 
     * @param plot The plot
     * @param summaryRecord The summary record containing the high and low values
     */
    protected void addAnnotations(XYPlot plot, SummaryRecord summaryRecord) {
    }

    /**
     * Load the data into the JFreeChart time series and into the Table Model
     * 
     * @param list The list of historical records
     * @param dataset The datasetLeft to be loaded
     * @param tableModel The table model to be loaded
     */
    protected void loadDataSeries(List<HistoricalRecord> records, DefaultTableModel tableModel) {
        entries.stream().forEach((entry) -> {
            entry.timeSeries.clear();
        });
        
        getPlot().getRangeAxis().setAutoRange(true);

	int n = 0;

	for (HistoricalRecord r : records) {
	    tableModel.setValueAt(DisplayConstants.formatTime(r.getTime().toLocalTime()), n, TIME_COLUMN);
	    RegularTimePeriod p = RegularTimePeriod.createInstance(Minute.class, TimeUtils.localDateTimeToDate(r.getTime()), TimeZone.getDefault());

            for(SeriesEntry entry : entries) {
                Measurement m = entry.seriesInfo.getValue(r);
                if (m != null) {
                    entry.timeSeries.add(p, m.get());
                    tableModel.setValueAt(m, n, entry.tableColumn);
                }
                else
                    tableModel.setValueAt(DisplayConstants.UNKNOWN_VALUE_STRING, n, entry.tableColumn);
            }
	    n++;
        }

        displaySeries(datasetLeft, datasetRight);
    }

    protected void displaySeries(TimeSeriesCollection left, TimeSeriesCollection right) {
        int n = 0;
        XYItemRenderer renderer = getPlot().getRenderer(0);
        left.removeAllSeries();
        for (SeriesEntry entry : entries) {
            if (entry.checkbox == null || entry.checkbox.isSelected()) {
                if (entry.datasetLeft) {
                    left.addSeries(entry.timeSeries);
                    renderer.setSeriesPaint(n++, entry.seriesInfo.getPaint());
                }
            }
        }

        n = 0;
        renderer = getPlot().getRenderer(1);
        right.removeAllSeries();
        for (SeriesEntry entry : entries) {
            if (entry.checkbox == null || entry.checkbox.isSelected()) {
                if (!entry.datasetLeft) {
                    right.addSeries(entry.timeSeries);
                    renderer.setSeriesPaint(n++, entry.seriesInfo.getPaint());
                }
            }
        }
    }

    /**
     * Called after all of the methods in the load data sequence. This can be overridden to perform axis calculations
     * or add addition annotation that depend on all of the data being loaded
     */
    protected void finishLoadData() {
    }
    
    /**
     * Update the domain axis of the plot to the day passed in. This can be used to change the plot
     * to show a different day
     * 
     * @param date The date
     */
    private void updateDomainAxis(LocalDate date) {
        plot.clearRangeMarkers();

        LocalDateTime midnight = date.atStartOfDay();
        LocalDateTime endOfDay = midnight.plusDays(1).minusSeconds(1);

        dateAxis.setRange(TimeUtils.localDateTimeToDate(midnight), TimeUtils.localDateTimeToDate(endOfDay));

    }

    /**
     * Add the sunrise and sunset markers to the plot.
     * 
     * @param 
     */
    private void addSunriseSunsetMarkers() {
        plot.clearDomainMarkers();
        
        //
        // If the menu item is currently selected
        //
        if (!displayDayNightIndicators)
            return;

        LocalDateTime sunrise = ws.sunriseFor(currentDate);
        LocalDateTime sunset = ws.sunsetFor(currentDate);

        IntervalMarker marker = new IntervalMarker((double)TimeUtils.localDateTimeToEpochMillis(sunrise), (double)TimeUtils.localDateTimeToEpochMillis(sunset));
        Color color = new Color(Color.YELLOW.getRed(), Color.YELLOW.getGreen(), Color.YELLOW.getBlue(), 60);
        marker.setPaint(color);

        plot.addDomainMarker(marker);
    }

    /**
     * Load the data into the panel
     * 
     * @param date
     * @param list The list of records
     * @param summaryRecord The summary record for the day of the data
     * @param records
     * @param averages
     */
    public void loadData(LocalDate date, List<HistoricalRecord> list, SummaryRecord summaryRecord, DailyRecords records, WeatherAverage averages) {
        tableModel.setRowCount(list.size());
        
        currentDate = date;
        updateDomainAxis(currentDate);
        addSunriseSunsetMarkers();
        addExtremeMarkers(plot, records, averages);
        loadDataSeries(list, tableModel);

        addAnnotations(plot, summaryRecord);
        finishLoadData();
    }

    /**
     * Process the change in state of the day/night indicator selection
     * @param event
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == dayNightItem) {
            displayDayNightIndicators = dayNightItem.isSelected();
            addSunriseSunsetMarkers();
            return;
        }

        for (SeriesEntry entry : entries) {
            if (event.getSource() == entry.checkbox) {
                displaySeries(datasetLeft, datasetRight);
                break;
            }
        }
    }
    

    protected static class SeriesControl {
        String name;
        boolean displayInitially;
        boolean leftAxis;
        public SeriesControl(String name, boolean display, boolean left) {
            this.name = name;
            displayInitially = display;
            leftAxis = left;
        }

        public SeriesControl(String name, boolean display) {
            this(name, display, true);
        }
    }

    private static class SeriesEntry {
        public HistoricalSeriesInfo seriesInfo;
        public TimeSeries           timeSeries;
        public int                  tableColumn;
        public JCheckBoxMenuItem    checkbox;
        public boolean              datasetLeft;
        
        public SeriesEntry(HistoricalSeriesInfo info, TimeSeries ts, int tc, JCheckBoxMenuItem cb, boolean left) {
            seriesInfo = info;
            timeSeries = ts;
            tableColumn = tc;
            checkbox = cb;
            datasetLeft = left;
        }
    }
}
