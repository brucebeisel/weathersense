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
import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.fx.interaction.ChartMouseEventFX;
import org.jfree.chart.fx.interaction.ChartMouseListenerFX;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;

import com.bdb.util.TimeUtils;

import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.WeatherAverage;
import com.bdb.weather.common.WeatherAverages;
import com.bdb.weather.common.measurement.Temperature;
import com.bdb.weather.display.ChartDataPane;
import com.bdb.weather.display.ViewLauncher;

public class TemperatureDeviationPlotPanel extends ChartDataPane {
    private final XYPlot            deviationPlot;
    private final JFreeChart        chart;
    private final ChartViewer       chartViewer;
    private final TableView         dataTable;
    private final ValueAxis         valueAxis;
    private final SummaryInterval   interval;
    private final XYBarRenderer     lowRenderer = new XYBarRenderer(.7);
    private final XYBarRenderer     meanRenderer = new XYBarRenderer(.7);
    private final XYBarRenderer     highRenderer = new XYBarRenderer(.7);
    private final ViewLauncher      launcher;
    private final SummarySupporter  supporter;
    private static final int    DATE_COLUMN = 0;
    private static final int    LOW_COLUMN = 1;
    private static final int    MEAN_COLUMN = 2;
    private static final int    HIGH_COLUMN = 3;
    private static final int    LOW_SERIES_INDEX = 0;
    private static final int    MEAN_SERIES_INDEX = 1;
    private static final int    HIGH_SERIES_INDEX = 2;
    private static final String LOW_SERIES_NAME = "Low";
    private static final String MEAN_SERIES_NAME = "Mean";
    private static final String HIGH_SERIES_NAME = "High";
    private static final String	TABLE_HEADINGS[] = {
        "Date",
	"Low Deviation",
	"Mean Deviation",
	"High Deviation"
    };

    public TemperatureDeviationPlotPanel(SummaryInterval interval, ViewLauncher theLauncher, SummarySupporter theSupporter) {
        this.interval = interval;
	chart = ChartFactory.createXYBarChart("Deviation from Average Temperature", "Date", true, "Deviation (" + Temperature.getDefaultUnit() + ")", null, PlotOrientation.VERTICAL, true, true, false);

	chartViewer = new ChartViewer(chart);
	chartViewer.addChartMouseListener(new ChartMouseListenerFX() {
            @Override
            public void chartMouseClicked(ChartMouseEventFX event) {
                ChartEntity entity = event.getEntity();
                //
                // Was a point on the plot selected?
                //
                if (entity instanceof XYItemEntity) {
                    XYItemEntity itemEntity = (XYItemEntity) entity;
                    XYDataset dataset = itemEntity.getDataset();
                    Number x = dataset.getXValue(itemEntity.getSeriesIndex(), itemEntity.getItem());
                    LocalDate date = LocalDate.from(Instant.ofEpochMilli(x.longValue()));
                    boolean doubleClick = event.getTrigger().getClickCount() == 2;
                    if (doubleClick) {
                        supporter.launchView(launcher, date);
                    }
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEventFX event) {
                // Do nothing
            }
        });
	deviationPlot = (XYPlot)chart.getPlot();
	this.launcher = theLauncher;
	this.supporter = theSupporter;
	
	DateFormat dateFormat = interval.getLegacyFormat();
        StandardXYItemLabelGenerator labelGen = new StandardXYItemLabelGenerator(StandardCategoryItemLabelGenerator.DEFAULT_LABEL_FORMAT_STRING,
                                                                                 dateFormat,
                                                                                 Temperature.getDefaultFormatter());
        
        StandardXYToolTipGenerator ttGen = new StandardXYToolTipGenerator(StandardCategoryToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT_STRING,
                                                                          dateFormat,
                                                                          Temperature.getDefaultFormatter());
        
	valueAxis = deviationPlot.getRangeAxis();
	valueAxis.setUpperMargin(.20);
	valueAxis.setLowerMargin(.20);
        
        deviationPlot.getDomainAxis().setVerticalTickLabels(true);
        DateAxis dateAxis = (DateAxis)deviationPlot.getDomainAxis();
        dateAxis.setDateFormatOverride(dateFormat);
        //dateAxis.setTickUnit(interval.getDateTickUnit());
        
	//DefaultTableColumnModel colModel = new DefaultTableColumnModel();

	dataTable = new TableView();
	//dataTable.setModel(tableModel);
	//dataTable.setColumnModel(colModel);

	//dataTable.setAutoCreateColumnsFromModel(false);

	for (int i = 0; i < TABLE_HEADINGS.length; i++) {
	    TableColumn col = new TableColumn();
	    col.setText(TABLE_HEADINGS[i]);
	    //col.setModelIndex(i);
	    //colModel.addColumn(col);
	}

	//tableModel.setColumnCount(TABLE_HEADINGS.length);

	ScrollPane sp = new ScrollPane(dataTable);
        this.setTabContents(chartViewer, sp);

	lowRenderer.setBasePaint(Color.BLUE);
	lowRenderer.setBaseItemLabelGenerator(labelGen);
	lowRenderer.setBaseToolTipGenerator(ttGen);
	lowRenderer.setBarAlignmentFactor(.6);
	lowRenderer.setShadowVisible(false);
	
	meanRenderer.setSeriesPaint(0, Color.CYAN);
	meanRenderer.setBaseItemLabelGenerator(labelGen);
        meanRenderer.setBaseToolTipGenerator(ttGen);
	meanRenderer.setBarAlignmentFactor(.3);
	meanRenderer.setShadowVisible(false);
	
	highRenderer.setSeriesPaint(0, Color.GRAY);
	highRenderer.setBaseItemLabelGenerator(labelGen);
        highRenderer.setBaseToolTipGenerator(ttGen);
	highRenderer.setShadowVisible(false);
    }
    
    public void loadData(List<SummaryRecord> list, WeatherAverages averagesCollection) {
	TimeSeriesCollection lowCollection = new TimeSeriesCollection();
	TimeSeriesCollection meanCollection = new TimeSeriesCollection();
	TimeSeriesCollection highCollection = new TimeSeriesCollection();
	TimeSeries highSeries = new TimeSeries(HIGH_SERIES_NAME);
	TimeSeries meanSeries = new TimeSeries(MEAN_SERIES_NAME);
	TimeSeries lowSeries = new TimeSeries(LOW_SERIES_NAME);
	

	int n = 0;

	//tableModel.setRowCount(list.size());
	
	DateTimeFormatter dateFormat = interval.getFormat();

	for (SummaryRecord record : list) {
	    WeatherAverage averages = averagesCollection.getAverage(record.getDate());
	    
	    String dateString = dateFormat.format(record.getDate());
	    //tableModel.setValueAt(dateString, n, DATE_COLUMN);

	    Temperature deviation = supporter.retrieveLowOutdoorTemperature(record).subtract(averages.getLowTemperature());
	    RegularTimePeriod period = RegularTimePeriod.createInstance(interval.getFreeChartClass(), TimeUtils.localDateTimeToDate(record.getDate().atStartOfDay()), TimeZone.getDefault());
	    TimeSeriesDataItem item = new TimeSeriesDataItem(period, deviation.get());
	    lowSeries.add(item);
	    //tableModel.setValueAt(deviation, n, LOW_COLUMN);

	    deviation = supporter.retrieveAvgOutdoorTemperature(record).subtract(averages.getMeanTemperature());
            item = new TimeSeriesDataItem(period, deviation.get());
            meanSeries.add(item);
	    //tableModel.setValueAt(deviation, n, MEAN_COLUMN);

	    deviation = supporter.retrieveHighOutdoorTemperature(record).subtract(averages.getHighTemperature());
            item = new TimeSeriesDataItem(period, deviation.get());
            highSeries.add(item);
	    //tableModel.setValueAt(deviation, n, HIGH_COLUMN);
	    
	    n++;
	}
	
	lowCollection.addSeries(lowSeries);
	meanCollection.addSeries(meanSeries);
	highCollection.addSeries(highSeries);

	deviationPlot.setDataset(LOW_SERIES_INDEX, lowCollection);
	deviationPlot.setDataset(MEAN_SERIES_INDEX, meanCollection);
	deviationPlot.setDataset(HIGH_SERIES_INDEX, highCollection);
	
	deviationPlot.setRenderer(LOW_SERIES_INDEX, lowRenderer);
	deviationPlot.setRenderer(MEAN_SERIES_INDEX, meanRenderer);
	deviationPlot.setRenderer(HIGH_SERIES_INDEX, highRenderer);
    }
}
