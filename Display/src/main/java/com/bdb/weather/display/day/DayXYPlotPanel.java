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

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLine3DRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.bdb.util.TimeUtils;
import com.bdb.util.measurement.Measurement;
import com.bdb.weather.common.DailyRecords;
import com.bdb.weather.common.GeographicLocation;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.WeatherAverage;
import com.bdb.weather.common.astronomical.SolarEventCalculator;
import com.bdb.weather.display.ChartDataPane;
import com.bdb.weather.display.DisplayConstants;


/**
 * An XY Plot for a single day of data. This class provides a tabbed pane, one pane for the plot
 * the other table contains a JTable for the data. This plot has day/night indicators on the plot.
 * These indicators can be turned off using the plots context menu.
 * 
 * @author Bruce
 */
abstract public class DayXYPlotPanel extends ChartDataPane implements EventHandler<ActionEvent> {
    protected static final String TIME_HEADING = "Time";
    protected static final int TIME_COLUMN = 0;

    private XYPlot               plot;
    private JFreeChart           chart;
    private ChartViewer          chartViewer;
    private DateAxis             dateAxis;
    private final ValueAxis      leftAxis;
    private final ValueAxis      rightAxis;
    private Menu                 displayMenu;
    private boolean              displayDayNightIndicators = true;
    private CheckMenuItem        dayNightItem;
    private LocalDate            currentDate;
    private LocalDateTime        sunrise;
    private LocalDateTime        sunset;
    private TableView<HistoricalRecord> dataTable;
    private final TimeSeriesCollection datasetLeft;
    private final TimeSeriesCollection datasetRight;
    private List<SeriesEntry>    entries;

    protected DayXYPlotPanel(ValueAxis leftAxis, ValueAxis rightAxis) {
        this.setPrefSize(400, 300);
        this.leftAxis = leftAxis;
        this.rightAxis = rightAxis;
        datasetLeft = new TimeSeriesCollection();
        datasetRight = new TimeSeriesCollection();
        entries = new ArrayList<>();
    }
        
    protected final void createElements() {
        createChartElements();

        //
        // Build the table for the data tab
        //
        dataTable = new TableView<>();

        this.setTabContents(chartViewer, dataTable);

        TableColumn<HistoricalRecord,String> col = new TableColumn<>(TIME_HEADING);
        col.setCellValueFactory((rec)->new ReadOnlyStringWrapper(DisplayConstants.formatTime(rec.getValue().getTime().toLocalTime())));

	dataTable.getColumns().add(col);
        doConfigure(displayMenu);
	this.layout();
    }

    private void createChartElements(){
        //
        // Set up the Domain Axis (X)
        //
        plot = new XYPlot();
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
        XYLineAndShapeRenderer renderer = new XYLine3DRenderer();
        renderer.setBaseShapesVisible(false);
        renderer.setBaseToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
        //renderer.setDefaultEntityRadius(1);
        plot.setRenderer(0, renderer);

        renderer = new XYLine3DRenderer();
        renderer.setBaseShapesVisible(false);
        renderer.setBaseToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
        //renderer.setDefaultEntityRadius(1);
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
        chartViewer = new ChartViewer(chart);

        chartViewer.setMaxHeight(500);
        chartViewer.setMaxWidth(800);

        //
        // Add the Day/Night indicator option to the chart panels context menu
        //
        ContextMenu menu = chartViewer.getContextMenu();

        displayMenu = new Menu("Display");

        dayNightItem = new CheckMenuItem("Day/Night Indicators");
        dayNightItem.setSelected(true);
        displayMenu.getItems().add(dayNightItem);
        dayNightItem.setOnAction(this);
        menu.getItems().add(displayMenu);
    }
    
    private void doConfigure(Menu menu) {
        List<SeriesControl> controls = configure(displayMenu);
        int tableColumn = 1;
        for (SeriesControl control : controls) {
            HistoricalSeriesInfo info = HistoricalSeriesInfo.find(control.name);
            if (info != null) {
                TimeSeries timeSeries = new TimeSeries(info.getSeriesName());
                CheckMenuItem menuItem = new CheckMenuItem(info.getSeriesName());
                menuItem.setSelected(control.displayInitially);
                SeriesEntry entry = new SeriesEntry(info, timeSeries, tableColumn, menuItem, control.leftAxis);
                entries.add(entry);

                TableColumn<HistoricalRecord,String> col = new TableColumn<>(entry.seriesInfo.getSeriesName());
		col.setCellValueFactory(entry);
                dataTable.getColumns().add(col);

                menu.getItems().add(menuItem);
                menuItem.setOnAction(this);
                tableColumn++;
            }
        }
    }

    protected abstract List<SeriesControl> configure(Menu menu);

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
    protected ChartViewer getChartViewer() {
        return chartViewer;
    }

    /**
     * Get the menu that pops up when the user right-clicks on the plot.
     * 
     * @return The menu
     */
    protected Menu getDisplayMenu() {
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
     * @param records The list of historical records
     */
    protected void loadDataSeries(List<HistoricalRecord> records) {
        entries.stream().forEach((entry) -> {
            entry.timeSeries.clear();
        });

	ObservableList<HistoricalRecord> dataModel = FXCollections.observableList(records);
	dataTable.setItems(dataModel);
        
        getPlot().getRangeAxis().setAutoRange(true);

        records.stream().forEach((r) -> {
            RegularTimePeriod p = RegularTimePeriod.createInstance(Minute.class, TimeUtils.localDateTimeToDate(r.getTime()), TimeZone.getDefault());
            
            entries.stream().forEach((entry) -> {
                Measurement m = entry.seriesInfo.getValue(r);
                if (m != null) {
                    entry.timeSeries.add(p, m.get());
                }
            });
        });

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
     */
    private void addSunriseSunsetMarkers() {
        plot.clearDomainMarkers();
        
        //
        // If the menu item is currently selected
        //
        if (!displayDayNightIndicators)
            return;

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
     * @param location The location of the weather station that is used to calculate sunrise and sunset
     */
    public void loadData(LocalDate date, List<HistoricalRecord> list, SummaryRecord summaryRecord, DailyRecords records, WeatherAverage averages, GeographicLocation location) {
        
        currentDate = date;
        updateDomainAxis(currentDate);
        SolarEventCalculator solar = new SolarEventCalculator(location);
        Calendar c = Calendar.getInstance();
        c.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        
        this.sunrise = LocalDateTime.ofInstant(solar.computeSunriseCalendar(c).toInstant(), ZoneId.systemDefault());
        this.sunset = LocalDateTime.ofInstant(solar.computeSunsetCalendar(c).toInstant(), ZoneId.systemDefault());
        
        addSunriseSunsetMarkers();
        addExtremeMarkers(plot, records, averages);
        loadDataSeries(list);

        addAnnotations(plot, summaryRecord);
        finishLoadData();
    }

    /**
     * Process the change in state of the day/night indicator selection
     * @param event
     */
    @Override
    public void handle(ActionEvent event) {
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

    private static class SeriesEntry implements Callback<CellDataFeatures<HistoricalRecord,String>,ObservableValue<String>> {
        public HistoricalSeriesInfo seriesInfo;
        public TimeSeries           timeSeries;
        public int                  tableColumn;
        public CheckMenuItem        checkbox;
        public boolean              datasetLeft;
        
        public SeriesEntry(HistoricalSeriesInfo info, TimeSeries ts, int tc, CheckMenuItem cb, boolean left) {
            seriesInfo = info;
            timeSeries = ts;
            tableColumn = tc;
            checkbox = cb;
            datasetLeft = left;
        }

	@Override
	public ObservableValue<String> call(CellDataFeatures<HistoricalRecord,String> cdf) {
	    HistoricalRecord r = cdf.getValue();
	    Measurement m = seriesInfo.getValue(r);

	    String value = DisplayConstants.UNKNOWN_VALUE_STRING;
	    if (m != null)
		value = m.toString();

            return new ReadOnlyStringWrapper(value);
	}
    }
}