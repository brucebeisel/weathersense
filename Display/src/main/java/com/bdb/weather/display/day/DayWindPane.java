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
package com.bdb.weather.display.day;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.scene.control.Menu;

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
public class DayWindPane extends DayXYPlotPane {
    private static final int GUST_SERIES_INDEX = 1;

    private final WindSeries speedSeries = new WindSeries("Wind Speed");
    private final TimeSeries gustSeries = new TimeSeries("Gust");
    private TimeSeriesCollection dataset;

    public DayWindPane() {
        this(null);
    }
    /**
     * Constructor.
     * 
     * @param ws The weather station for which the data is being plotted
     */
    public DayWindPane(WeatherStation ws) {
        super(WindSpeedRangeAxis.create(), null);
        createElements();
    }

    @Override
    public List<SeriesControl> configure(Menu menu) {
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
     * @see com.bdb.weather.display.day.DayXYPlotPane#addAnnotations(org.jfree.chart.plot.XYPlot, com.bdb.weather.common.SummaryRecord)
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
     * @see com.bdb.weather.display.day.DayXYPlotPane#loadDataSeries(java.util.List, org.jfree.data.time.TimeSeriesCollection)
     */
    @Override
    protected void loadDataSeries(List<HistoricalRecord> list) {
        super.loadDataSeries(list);
        speedSeries.clear();
        gustSeries.clear();

        int n = 0;

        for (HistoricalRecord r : list) {
            Wind w = r.getAvgWind();

            if (w != null) {
                Minute period = new Minute(Date.from(r.getTime().atZone(ZoneId.systemDefault()).toInstant()));
                WindSeriesDataItem item = new WindSeriesDataItem(period, w.getSpeed().get(), w.getDirection().get());

                speedSeries.add(item);

                w = r.getWindGust();

                if (w != null && w.getSpeed().get() > 0.0) {
                    period = new Minute(TimeUtils.localDateTimeToDate(r.getTime()));
                    gustSeries.add(period, w.getSpeed().get());
                }
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