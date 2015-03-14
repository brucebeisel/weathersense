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

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.TextAnchor;

import com.bdb.util.TimeUtils;

import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.Wind;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.display.DisplayConstants;
import com.bdb.weather.display.WeatherSenseConstants;
import com.bdb.weather.display.axis.WindSpeedRangeAxis;
import com.bdb.weather.display.windplot.WindItemRenderer;
import com.bdb.weather.display.windplot.WindSeries;
import com.bdb.weather.display.windplot.WindSeriesDataItem;

/**
 * Display the wind data for a day.
 * 
 * @author Bruce
 *
 */
public class DayWindPanel extends DayXYPlotPanel {
    private static final int GUST_SERIES_INDEX = 1;
    private static final int WIND_SPEED_COLUMN = 1;
    private static final int DIRECTION_COLUMN = 2;
    private static final int MAX_WIND_SPEED_COLUMN = 3;
    private static final int MAX_WIND_DIRECTION_COLUMN = 4;
    private static final int GUST_COLUMN = 5;
    private static final int GUST_DIRECTION_COLUMN = 6;

    private static final String COLUMN_LABELS[] = { "Time", "Wind Speed", "Direction", "Max Wind Speed", "Max Direction", "Gust", "Gust Direction"};
    private final WindSeries speedSeries = new WindSeries(COLUMN_LABELS[WIND_SPEED_COLUMN]);
    private final TimeSeries gustSeries = new TimeSeries(COLUMN_LABELS[GUST_COLUMN]);
    private TimeSeriesCollection dataset;

    public static DayWindPanel createDayWindPanel(WeatherStation ws) {
        DayWindPanel plot = new DayWindPanel(ws);
        plot.createElements();
        return plot;
    }
    
    /**
     * Constructor.
     * 
     * @param ws The weather station for which the data is being plotted
     */
    private DayWindPanel(WeatherStation ws) {
        super(ws, new WindSpeedRangeAxis(), null);

        
    }

    @Override
    public List<SeriesControl> configure(JMenu menu) {
        List<SeriesControl> controls = new ArrayList<>();
        controls.add(new SeriesControl(HistoricalSeriesInfo.AVG_WIND_SPEED_SERIES, false));
        controls.add(new SeriesControl(HistoricalSeriesInfo.AVG_WIND_DIRECTION_SERIES, false));
        controls.add(new SeriesControl(HistoricalSeriesInfo.HIGH_WIND_SPEED_SERIES, false));
        controls.add(new SeriesControl(HistoricalSeriesInfo.HIGH_WIND_DIRECTION_SERIES, false));
        controls.add(new SeriesControl(HistoricalSeriesInfo.WIND_GUST_SPEED_SERIES, false));
        controls.add(new SeriesControl(HistoricalSeriesInfo.WIND_GUST_DIRECTION_SERIES, false));

        WindItemRenderer renderer = new WindItemRenderer();
        getPlot().setRenderer(renderer);
        WeatherSenseConstants.configureGustRenderer(renderer, GUST_SERIES_INDEX);

        StandardXYToolTipGenerator ttgen = new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                                                                          DateFormat.getTimeInstance(DateFormat.SHORT),
                                                                          Speed.getDefaultFormatter());

        getPlot().getRenderer().setBaseToolTipGenerator(ttgen);

        return controls;
    }
    
    /*
     * (non-Javadoc)
     * @see com.bdb.weather.display.day.DayXYPlotPanel#addAnnotations(org.jfree.chart.plot.XYPlot, com.bdb.weather.common.SummaryRecord)
     */
    @Override
    protected void addAnnotations(XYPlot plot, SummaryRecord summaryRecord) {
        plot.clearAnnotations();
        if (summaryRecord == null)
            return;
        

        LocalDateTime maxSpeedTime = summaryRecord.getMaxWindSpeedTime();
        if (maxSpeedTime != null) {
            double maxSpeed = summaryRecord.getMaxWindSpeed().get();

            String maxSpeedAnnotation = Speed.getDefaultFormatter().format(maxSpeed) + Speed.getDefaultUnit() + " "
                    + DisplayConstants.formatTime(maxSpeedTime.toLocalTime());

            XYTextAnnotation a = new XYTextAnnotation(maxSpeedAnnotation, (double)TimeUtils.localDateTimeToEpochMillis(maxSpeedTime), maxSpeed);
            a.setTextAnchor(TextAnchor.BASELINE_CENTER);
            plot.addAnnotation(a);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.weather.display.day.DayXYPlotPanel#loadDataSeries(java.util.List, org.jfree.data.time.TimeSeriesCollection, javax.swing.table.DefaultTableModel)
     */
    @Override
    protected void loadDataSeries(List<HistoricalRecord> list, DefaultTableModel tableModel) {
        super.loadDataSeries(list, tableModel);
        speedSeries.clear();
        gustSeries.clear();

        int n = 0;

        for (HistoricalRecord r : list) {
            Wind w = r.getAvgWind();

            if (w != null) {
                Minute period = new Minute(Date.from(r.getTime().atZone(ZoneId.systemDefault()).toInstant()));
                WindSeriesDataItem item = new WindSeriesDataItem(period, w.getSpeed().get(), w.getDirection().get());

                speedSeries.add(item);

                //
                // If the wind is calm, direction is pointless
                //
                if (w.getSpeed().get() == 0.0)
                    tableModel.setValueAt(DisplayConstants.WIND_DIR_WHEN_NO_WIND, n, DIRECTION_COLUMN);
                else
                    tableModel.setValueAt(w.getDirection().getCompassLabel(), n, DIRECTION_COLUMN);

                w = r.getHighWind();
                if (w != null) {
                    tableModel.setValueAt(w.getSpeed(), n, MAX_WIND_SPEED_COLUMN);
                    if (w.getSpeed().get() == 0.0)
                        tableModel.setValueAt(DisplayConstants.WIND_DIR_WHEN_NO_WIND, n, MAX_WIND_DIRECTION_COLUMN);
                    else
                        tableModel.setValueAt(w.getDirection().getCompassLabel(), n, MAX_WIND_DIRECTION_COLUMN);
                }
                else {
                    tableModel.setValueAt(DisplayConstants.WIND_DIR_WHEN_NO_WIND, n, MAX_WIND_DIRECTION_COLUMN);
                }


                w = r.getWindGust();
                if (w != null) {
                    //tableModel.setValueAt(w.getSpeed(), n, GUST_COLUMN);

                    if (w.getSpeed().get() == 0.0)
                        tableModel.setValueAt(DisplayConstants.WIND_DIR_WHEN_NO_WIND, n, GUST_DIRECTION_COLUMN);
                    else
                        tableModel.setValueAt(w.getDirection().getCompassLabel(), n, GUST_DIRECTION_COLUMN);
                }
                else {
                    tableModel.setValueAt(DisplayConstants.WIND_DIR_WHEN_NO_WIND, n, GUST_DIRECTION_COLUMN);
                }

                if (w != null && w.getSpeed().get() > 0.0) {
                    period = new Minute(TimeUtils.localDateTimeToDate(r.getTime()));
                    gustSeries.add(period, w.getSpeed().get());
                }
            }
            else {
                tableModel.setValueAt(DisplayConstants.UNKNOWN_VALUE_STRING, n, WIND_SPEED_COLUMN);
                tableModel.setValueAt(DisplayConstants.UNKNOWN_VALUE_STRING, n, GUST_COLUMN);
                tableModel.setValueAt(DisplayConstants.UNKNOWN_VALUE_STRING, n, DIRECTION_COLUMN);
            }

            n++;
        }

        dataset.removeAllSeries();
        dataset.addSeries(speedSeries);

        if (!gustSeries.isEmpty())
            dataset.addSeries(gustSeries);
    }

    @Override
    protected void displaySeries(TimeSeriesCollection left, TimeSeriesCollection right) {
        dataset = left;
    }
}