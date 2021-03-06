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

import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.fx.interaction.ChartMouseEventFX;
import org.jfree.chart.fx.interaction.ChartMouseListenerFX;
import org.jfree.chart.labels.HighLowItemLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Year;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.ui.RectangleEdge;

import com.bdb.util.measurement.Measurement;

import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.display.ChartDataPane;
import com.bdb.weather.display.DisplayConstants;
import com.bdb.weather.display.ViewLauncher;

public abstract class HighLowPanel<T extends Measurement> extends ChartDataPane implements ChartMouseListenerFX {
    private static final String HIGH_COL_HEADING = "High";
    private static final String LOW_COL_HEADING = "Low";
    private static final String AVG_COL_HEADING = "Avg";

    public static class SeriesInfo<T extends Measurement> implements Callback<TableColumn.CellDataFeatures<SummaryRecord,String>,ObservableValue<String>> {
        private final String seriesName;
        private final Function<SummaryRecord,T>  maxMethod;
        private final Function<SummaryRecord,T>  minMethod;
        private final Function<SummaryRecord,T>  avgMethod;

        public SeriesInfo(String seriesName, Function<SummaryRecord,T> max, Function<SummaryRecord,T> min, Function<SummaryRecord,T> avg) {
            this.seriesName = seriesName;
            this.maxMethod = max;
            this.minMethod = min;
            this.avgMethod = avg;
        }

        public String getSeriesName() {
            return seriesName;
        }

        public T getMaxValue(SummaryRecord record) {
            return maxMethod.apply(record);
        }

        public T getMinValue(SummaryRecord record) {
            return minMethod.apply(record);
        }

        public T getAvgValue(SummaryRecord record) {
            return avgMethod.apply(record);
        }

	@Override
	public ObservableValue<String> call(TableColumn.CellDataFeatures<SummaryRecord,String> cdf) {
	    SummaryRecord r = cdf.getValue();
            Measurement m = null;
            switch ((String)cdf.getTableColumn().getUserData()) {
                case HIGH_COL_HEADING:
                    m = getMaxValue(r);
                    break;
                case LOW_COL_HEADING:
                    m = getMinValue(r);
                    break;
                case AVG_COL_HEADING:
                    m = getAvgValue(r);
                    break;
            }

	    String value = DisplayConstants.UNKNOWN_VALUE_STRING;
	    if (m != null)
		value = m.toString();

            return new ReadOnlyStringWrapper(value);
	}
    }

    private final OHLCSeriesCollection     seriesCollection = new OHLCSeriesCollection();
    private final XYPlot                   plot;
    private final JFreeChart               chart;
    private final HighLowRenderer          renderer;
    private final DateAxis                 dateAxis;
    private final SeriesInfo<T>[]          seriesInfo;
    private final OHLCSeries[]             series;
    private final TableView<SummaryRecord> dataTable = new TableView<>();
    private final SummaryInterval          interval;
    private final ViewLauncher             launcher;
    private final SummarySupporter         supporter;
    private final static Logger            logger = Logger.getLogger(HighLowPanel.class.getName());
    
    @SuppressWarnings("LeakingThisInConstructor")
    public HighLowPanel(String title, SummaryInterval interval, ViewLauncher launcher, SummarySupporter supporter, ValueAxis rangeAxis, String domainAxisLabel, SeriesInfo<T>[] seriesList, NumberFormat format) {
        this.setPrefSize(500, 300);
        this.interval = interval;
        this.launcher = launcher;
        this.supporter = supporter;
        
        chart = ChartFactory.createHighLowChart(title, domainAxisLabel, "", seriesCollection, true);
        chart.getLegend().setPosition(RectangleEdge.RIGHT);
        
        plot = (XYPlot)chart.getPlot();
        renderer = (HighLowRenderer)plot.getRenderer();
        renderer.setDrawCloseTicks(false);
        
        plot.setRangeAxis(rangeAxis);
        
        dateAxis = (DateAxis)plot.getDomainAxis();
        dateAxis.setDateFormatOverride(interval.getLegacyFormat());
        dateAxis.setVerticalTickLabels(true);
        dateAxis.setTickMarkPosition(DateTickMarkPosition.START);
        //dateAxis.setTickUnit(interval.getDateTickUnit());
        //dateAxis.setTickUnit(new DateTickUnit(DateTickUnitType.MONTH, 2));
        
 
        ChartViewer chartViewer = new ChartViewer(chart);
        chartViewer.addChartMouseListener(this);
        chartViewer.setPrefSize(500, 300);
        
        series = new OHLCSeries[seriesList.length];
        
        for (int i = 0; i < seriesList.length; i++) {
            series[i] = new OHLCSeries(seriesList[i].getSeriesName());       
            seriesCollection.addSeries(series[i]);
        }
        
        seriesInfo = Arrays.copyOf(seriesList, seriesList.length);
        
        TableColumn<SummaryRecord,String> column = new TableColumn<>("Date");
        column.setCellValueFactory((rec)->new ReadOnlyStringWrapper(DisplayConstants.formatDate(rec.getValue().getDate())));

        dataTable.getColumns().add(column);

        String headingPrefix[] = {HIGH_COL_HEADING, LOW_COL_HEADING, AVG_COL_HEADING};
        
        for (SeriesInfo<T> seriesColumn : seriesList) {
            for (String heading : headingPrefix) {
                column = new TableColumn<>(heading + " - " + seriesColumn.getSeriesName());
                column.setCellValueFactory(seriesColumn);
                column.setUserData(heading);
                dataTable.getColumns().add(column);
            }
        }

        this.setTabContents(chartViewer, dataTable);

        HighLowItemLabelGenerator ttg = new HiLoItemLabelGenerator(interval.getLegacyFormat(), format);
        plot.getRenderer().setDefaultToolTipGenerator(ttg);
    }
    
    protected XYPlot getPlot() {
        return plot;
    }

    public void loadData(List<SummaryRecord> records) {   
        for (int i = 0; i < seriesInfo.length; i++) {
            series[i].clear();
            loadData(series[i], seriesInfo[i], records, i);
        }
    }
    
    private void loadData(OHLCSeries series, SeriesInfo<T> info, List<SummaryRecord> records, int seriesIndex) {
	ObservableList<SummaryRecord> dataModel = FXCollections.observableList(records);
	dataTable.setItems(dataModel);
        for (SummaryRecord record : records) {
            T avg = info.getAvgValue(record);
            T min = info.getMinValue(record);
            T max = info.getMaxValue(record);

            LocalDate date = record.getDate();
            // TODO: Figure out how to create a time period based on the specified interval
            RegularTimePeriod period = null;
            switch (interval) {
                case DAY_INTERVAL:
                    period = new Hour(seriesIndex * 4, date.getDayOfMonth(), date.getMonth().getValue(), date.getYear());
                    break;
                case MONTH_INTERVAL:
                    period = new Day(seriesIndex * 4 + 1,  date.getMonth().getValue(), date.getYear());
                    break;
                case YEAR_INTERVAL:
                    period = new Year(date.getYear());
                    break;
                default:
                    period = null;
                    break;
            }

            if (avg != null && min != null && max != null) {
                series.add(period, avg.get(), max.get(), min.get(), min.get());
            }
        }
    }
    
    /* (non-Javadoc)
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
            LocalDate date = LocalDate.from(Instant.ofEpochMilli(x.longValue()));
            boolean doubleClick = event.getTrigger().getClickCount() == 2;
            if (doubleClick) {
                supporter.launchView(launcher, date);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.jfree.chart.ChartMouseListener#chartMouseMoved(org.jfree.chart.ChartMouseEvent)
     */
    @Override
    public void chartMouseMoved(ChartMouseEventFX event) {
        // Ignore mouse movement
    }

}
