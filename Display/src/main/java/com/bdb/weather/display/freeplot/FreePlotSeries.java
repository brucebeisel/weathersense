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
package com.bdb.weather.display.freeplot;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Function;

import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;

import com.bdb.util.TimeUtils;
import com.bdb.util.measurement.Measurement;

/**
 * Class to control a series within a Series Collection
 * 
 * @author Bruce
 * @param <T> Type from which the data will be extracted and plotted
 *
 */
public class FreePlotSeries<T> {
    private final String     name;
    private int              seriesIndex;
    private final Color      paint;
    private final Stroke     stroke;
    private final TimeSeries series;
    private final Function<T,? extends Measurement>     getDataMethod;
    //private final Object[]   dataMethodArgs;
    private final Function<T,? extends TemporalAccessor>    getTimeMethod;
    private final Class<?>   timePeriod;

    /**
     * Constructor.
     * 
     * @param name The name of the series, this must match the name of the corresponding control
     * @param seriesIndex The index of this series from a JFreePlot point of view
     * @param paint The Paint to be used to draw this series
     * @param stroke The Stroke to be used to draw this series
     * @param getDataMethod The method used to get the data of this series (Reflection used)
     * @param getTimeMethod The method used to get the time of the data of this series (Reflection used)
     * @param timePeriod The time increment of this data.
     */
    public FreePlotSeries(String name, int seriesIndex, Color paint, Stroke stroke, Function<T,Measurement> getDataMethod, Function<T,TemporalAccessor> getTimeMethod, Class<?> timePeriod) {
        this.name = name;
        this.seriesIndex = seriesIndex;
        this.paint = paint;
        this.stroke = stroke;
        this.series = new TimeSeries(name);
        this.getDataMethod = getDataMethod;
        //this.dataMethodArgs = dataMethodArgs;
        this.getTimeMethod = getTimeMethod;
        this.timePeriod = timePeriod;
    }
    
    public String getName() {
        return name;
    }
    
    public int getSeriesIndex() {
        return seriesIndex;
    }
    
    public void setSeriesIndex(int index) {
        seriesIndex = index;
    }

    public Paint getPaint() {
        return paint;
    }
    
    public Stroke getStroke() {
        return stroke;
    }

    public TimeSeries getSeries() {
        return series;
    }
    
    public void clearData() {
        series.clear();
    }
    
    /**
     * Update the renderer for this series
     * 
     * @param renderer The Renderer to update
     */
    public void updateRenderer(DefaultXYItemRenderer renderer) {
        // TODO add tooltip generators
        renderer.setSeriesPaint(seriesIndex, paint);
        renderer.setSeriesStroke(seriesIndex, stroke);
    }
    
    /**
     * Load the data for this series.
     * 
     * @param data A generic list on which the methods passed into the constructor will be called
     */
    public void loadData(List<T> data) {
        series.setNotify(false);
        series.clear();
        data.stream().forEach((obj) -> {
            TemporalAccessor time = getTimeMethod.apply(obj);
            RegularTimePeriod period = RegularTimePeriod.createInstance(timePeriod, TimeUtils.localDateTimeToDate(time), TimeZone.getDefault(), Locale.getDefault());
            Measurement m;
            m = getDataMethod.apply(obj);
            if (m != null) {
                double value = m.get();
                series.add(period, value);
            }
        });
        series.fireSeriesChanged();
        series.setNotify(true);
    }
}
