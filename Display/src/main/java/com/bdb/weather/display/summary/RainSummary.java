/* 
 * Copyright (C) 2016 Bruce Beisel
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

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.fx.interaction.ChartMouseEventFX;
import org.jfree.chart.fx.interaction.ChartMouseListenerFX;
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
import com.bdb.weather.display.ChartDataPane;
import com.bdb.weather.display.DisplayConstants;
import com.bdb.weather.display.axis.RainRangeAxis;

/**
 * Plot to summarize rain over a specified period of time
 */
public class RainSummary extends ChartDataPane implements ChartMouseListenerFX {
    private XYPlot                  rainPlot;
    private JFreeChart              chart;
    private final TableView         dataTable;
    private final ValueAxis         valueAxis = RainRangeAxis.create();
    private final SummaryInterval   interval;
    private final SummarySupporter  supporter;
    private static final String     RAIN_ROW_KEY = "Rain";
    private static final String     ET_ROW_KEY = "ET";
    
    public RainSummary() {
        this(SummaryInterval.DAY_INTERVAL, null);
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public RainSummary(SummaryInterval interval, SummarySupporter supporter) {
        this.setPrefSize(500, 300);
        this.interval = interval;
        this.supporter = supporter;
        dataTable = new TableView();
        Node component = createChartElements();

        TableColumn<SummaryRecord,String> col = new TableColumn<>("Date");
        col.setCellValueFactory((rec)->new ReadOnlyStringWrapper(DisplayConstants.formatDate(rec.getValue().getDate())));
	dataTable.getColumns().add(col);
        
        TableColumn<SummaryRecord,Depth> rainfallColumn = new TableColumn<>("Rainfall");
        rainfallColumn.setCellValueFactory((rec) -> new ReadOnlyObjectWrapper(rec.getValue().getTotalRainfall()));
        dataTable.getColumns().add(rainfallColumn);
        
        TableColumn<SummaryRecord,Depth> etColumn = new TableColumn<>("ET");
        etColumn.setCellValueFactory((rec) -> new ReadOnlyObjectWrapper(rec.getValue().getTotalET()));
        dataTable.getColumns().add(etColumn);

        this.setTabContents(component, dataTable);
    }

    private Node createChartElements() {
        chart = ChartFactory.createXYBarChart("Water Cycle", "Date", true, "", null, PlotOrientation.VERTICAL, true, true, false);
        ChartViewer chartViewer = new ChartViewer(chart);
        chartViewer.setPrefSize(500, 300);
        chartViewer.addChartMouseListener(this);

        rainPlot = (XYPlot)chart.getPlot();
        DateAxis dateAxis = (DateAxis)rainPlot.getDomainAxis();
        dateAxis.setDateFormatOverride(interval.getLegacyFormat());
        ClusteredXYBarRenderer renderer = new ClusteredXYBarRenderer();
        rainPlot.setRenderer(renderer);
        //renderer.setBarAlignmentFactor(0.0);
        renderer.setMargin(0.2);
        renderer.setShadowVisible(false);

        StandardXYToolTipGenerator ttgen = new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, interval.getLegacyFormat(), Depth.getDefaultFormatter());
        
        renderer.setDefaultToolTipGenerator(ttgen);
        //renderer.setSeriesPaint(0, Color.BLUE); // TODO Use color preferences
        //renderer.setSeriesPaint(1, Color.RED); // TODO Use color preferences
        
        rainPlot.setRangeAxis(valueAxis);
        rainPlot.getDomainAxis().setVerticalTickLabels(true);

        return chartViewer;
    }
    
    /**
     * Load the data into the plot.
     * 
     * @param list The summary data
     */
    public void loadData(List<SummaryRecord> list) {
        dataTable.setItems(FXCollections.observableList(list));
        TimeSeriesCollection ds = new TimeSeriesCollection();
        TimeSeries etSeries = new TimeSeries(ET_ROW_KEY);
        TimeSeries rainSeries = new TimeSeries(RAIN_ROW_KEY);
        
        Depth totalRain = new Depth(0.0);
        Depth totalET = new Depth(0.0);

        //
        // Load each record
        //
        for (SummaryRecord rec : list) {
            Depth rain = rec.getTotalRainfall();

            RegularTimePeriod tp = RegularTimePeriod.createInstance(interval.getFreeChartClass(), TimeUtils.localDateTimeToDate(rec.getDate()), TimeZone.getDefault(), Locale.getDefault());

            if (rain != null) {
                rainSeries.add(tp, rain.get());
                totalRain = totalRain.add(rain);
            }

            Depth et = rec.getTotalET();
            if (et != null) {
                etSeries.add(tp, et.get());
                totalET = totalET.add(et);
            }
        }

        ds.addSeries(etSeries);
        ds.addSeries(rainSeries);
        rainPlot.setDataset(ds);
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
        if (entity instanceof CategoryItemEntity) {
            try {
                CategoryItemEntity itemEntity = (CategoryItemEntity)entity;
                LocalDate date = LocalDate.from(interval.getFormat().parse((String)itemEntity.getColumnKey()));

                //if (event.getTrigger().getClickCount() == 2)
                //    supporter.launchView(viewLauncher, date);
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
    public void chartMouseMoved(ChartMouseEventFX event) {
        // Do nothing with mouse movement
    }
}