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
package com.bdb.weather.display.freeplot;

import java.awt.Color;
import java.awt.Stroke;
import java.awt.event.ActionListener;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import javafx.scene.Node;

import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;

import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.SpeedBin;
import com.bdb.weather.common.SummaryRecord;
import com.bdb.weather.common.TemperatureBinMgr;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.db.TemperatureBinTable;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.SolarRadiation;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;
import com.bdb.weather.display.ComponentContainer;
import com.bdb.weather.display.axis.HumidityRangeAxis;
import com.bdb.weather.display.axis.PressureRangeAxis;
import com.bdb.weather.display.axis.RainRangeAxis;
import com.bdb.weather.display.axis.SolarRadiationAxis;
import com.bdb.weather.display.axis.TemperatureRangeAxis;
import com.bdb.weather.display.axis.WindSpeedRangeAxis;
import com.bdb.weather.display.freeplot.FreePlot.SeriesCollectionFactory;
import com.bdb.weather.display.freeplot.FreePlotSeriesCollection.SeriesFactory;

/**
 * Abstract class for implementing the Free Plot of summary data.
 * 
 * @author Bruce
 *
 */
public abstract class SummaryFreePlot implements ComponentContainer, SeriesFactory<SummaryRecord>, SeriesCollectionFactory {
    private static final String TEMPERATURE_COLLECTION_NAME = "Temperature";
    private static final String HUMIDITY_COLLECTION_NAME = "Humidity";
    private static final String PRESSURE_COLLECTION_NAME = "Pressure";
    private static final String WIND_COLLECTION_NAME = "Wind";
    private static final String RAIN_COLLECTION_NAME = "Rain";
    private static final String SOLAR_COLLECTION_NAME = "Solar";
    private static final int    NUM_COLLECTIONS = 6;
    
    private static final String HIGH_TEMPERATURE_SERIES_NAME = "High Temperature";
    private static final String LOW_TEMPERATURE_SERIES_NAME = "Low Temperature";
    private static final String MEAN_TEMPERATURE_SERIES_NAME = "Mean Temperature";
    private static final String MAX_OUTDOOR_HUMIDITY_SERIES_NAME = "Max Outdoor Humidity";
    private static final String MIN_OUTDOOR_HUMIDITY_SERIES_NAME = "Min Outdoor Humidity";
    private static final String MEAN_OUTDOOR_HUMIDITY_SERIES_NAME = "Mean Outdoor Humidity";
    private static final String MAX_BARO_PRESSURE_SERIES_NAME = "Max Barometric Pressure";
    private static final String MIN_BARO_PRESSURE_SERIES_NAME = "Min Barometric Pressure";
    private static final String MEAN_BARO_PRESSURE_SERIES_NAME = "Mean Barometric Pressure";
    private static final String AVG_WIND_SERIES_NAME = "Avg Wind Speed";
    private static final String MAX_WIND_SERIES_NAME = "Max Wind Speed";
    private static final String MAX_GUST_SERIES_NAME = "Max Wind Gust";
    private static final String RAIN_SERIES_NAME = "Rainfall Total";
    private static final String ET_SERIES_NAME = "ET Total";
    private static final String MAX_SOLAR_RADIATION_NAME = "Max Solar Radiation";
    private static final String MEAN_SOLAR_RADIATION_NAME = "Mean Solar Radiation";
    private static final String MAX_UV_INDEX_NAME = "Max UV Index";
    private static final String MEAN_UV_INDEX_NAME = "Mean UV Index";

    
    private final FreePlot              freePlot;
    private final WeatherStation        ws;
    private final List<SpeedBin>        speedBins;
    private final TemperatureBinMgr     temperatureBinMgr;
    private final TemperatureBinTable   temperatureBinTable;
    private final Class<?>              period;
    private Function<SummaryRecord,TemporalAccessor> dateMethod;
    
    /**
     * Constructor.
     * 
     * @param ws The weather station for which this window will display data
     * @param connection Connection to the database
     * @param period The time period of the graph
     */
    protected SummaryFreePlot(WeatherStation ws, DBConnection connection, Class<?> period) {
        freePlot = new FreePlot(this);
        this.period = period;
        temperatureBinTable = new TemperatureBinTable(connection);
        this.ws = ws;
        speedBins = ws.getWindParameters().getSpeedBinList();
        temperatureBinMgr = new TemperatureBinMgr(connection);
        temperatureBinMgr.refresh();
    }
    
    /**
     * Return the Swing component that is the container of this plot.
     * 
     * @return The swing container
     */
    @Override
    public Node getComponent() {
        return null;
    }
    
   /*
    * (non-Javadoc)
    * @see com.bdb.weather.display.freeplot.FreePlot.SeriesCollectionFactory#createSeriesGroupControls(java.awt.event.ActionListener)
    */
    @Override
    public Map<String,SeriesGroupControl> createSeriesGroupControls(ActionListener listener) {
        Map<String,SeriesGroupControl> list = new TreeMap<>();
        
        SeriesGroupControl groupControl = new SeriesGroupControl(TEMPERATURE_COLLECTION_NAME, new TemperatureRangeAxis(), listener);
        groupControl.addSeriesControl(new SeriesControl(HIGH_TEMPERATURE_SERIES_NAME, listener));
        groupControl.addSeriesControl(new SeriesControl(LOW_TEMPERATURE_SERIES_NAME, listener));
        groupControl.addSeriesControl(new SeriesControl(MEAN_TEMPERATURE_SERIES_NAME, listener));
        list.put(groupControl.getGroupName(), groupControl);
        
        groupControl = new SeriesGroupControl(HUMIDITY_COLLECTION_NAME, new HumidityRangeAxis(), listener);
        groupControl.addSeriesControl(new SeriesControl(MAX_OUTDOOR_HUMIDITY_SERIES_NAME, listener));
        groupControl.addSeriesControl(new SeriesControl(MIN_OUTDOOR_HUMIDITY_SERIES_NAME, listener));
        groupControl.addSeriesControl(new SeriesControl(MEAN_OUTDOOR_HUMIDITY_SERIES_NAME, listener));
        list.put(groupControl.getGroupName(), groupControl);
        
        groupControl = new SeriesGroupControl(PRESSURE_COLLECTION_NAME, new PressureRangeAxis(), listener);
        groupControl.addSeriesControl(new SeriesControl(MAX_BARO_PRESSURE_SERIES_NAME, listener));
        groupControl.addSeriesControl(new SeriesControl(MIN_BARO_PRESSURE_SERIES_NAME, listener));
        groupControl.addSeriesControl(new SeriesControl(MEAN_BARO_PRESSURE_SERIES_NAME, listener));
        list.put(groupControl.getGroupName(), groupControl);
        
        groupControl = new SeriesGroupControl(WIND_COLLECTION_NAME, new WindSpeedRangeAxis(), listener);
        groupControl.addSeriesControl(new SeriesControl(AVG_WIND_SERIES_NAME, listener));
        groupControl.addSeriesControl(new SeriesControl(MAX_WIND_SERIES_NAME, listener));
        groupControl.addSeriesControl(new SeriesControl(MAX_GUST_SERIES_NAME, listener));
        list.put(groupControl.getGroupName(), groupControl);
        
        groupControl = new SeriesGroupControl(RAIN_COLLECTION_NAME, new RainRangeAxis(), listener);
        groupControl.addSeriesControl(new SeriesControl(RAIN_SERIES_NAME, listener));
        groupControl.addSeriesControl(new SeriesControl(ET_SERIES_NAME, listener));
        list.put(groupControl.getGroupName(), groupControl);

        groupControl = new SeriesGroupControl(SOLAR_COLLECTION_NAME, new SolarRadiationAxis(), listener);
        groupControl.addSeriesControl(new SeriesControl(MEAN_SOLAR_RADIATION_NAME, listener));
        groupControl.addSeriesControl(new SeriesControl(MAX_SOLAR_RADIATION_NAME, listener));
        list.put(groupControl.getGroupName(), groupControl);
        
        return list;
    }
    
    /*
     * (non-Javadoc)
     * @see com.bdb.weather.display.freeplot.FreePlotSeriesCollection.SeriesFactory#createSeriesGroup(java.lang.String, java.awt.Stroke)
     */
    @Override
    public List<FreePlotSeries<SummaryRecord>> createSeriesGroup(String groupName, Stroke stroke) {
        List<FreePlotSeries<SummaryRecord>> list = new ArrayList<>();

        dateMethod = SummaryRecord::getDate;
        switch (groupName) {
            case TEMPERATURE_COLLECTION_NAME:
                list = createTemperatureSeries(stroke);
                break;
            case HUMIDITY_COLLECTION_NAME:
                list = createHumiditySeries(stroke);
                break;
            case PRESSURE_COLLECTION_NAME:
                list = createPressureSeries(stroke);
                break;
            case WIND_COLLECTION_NAME:
                list = createWindSeries(stroke);
                break;
            case RAIN_COLLECTION_NAME:
                list = createRainSeries(stroke);
                break;
                
            case SOLAR_COLLECTION_NAME:
                list = createSolarRadiationSeries(stroke);
                break;
        }
        
        return list;       
    }

    /*
     * (non-Javadoc)
     * @see com.bdb.weather.display.freeplot.FreePlot.SeriesCollectionFactory#createSeriesCollections(org.jfree.chart.plot.XYPlot, int, java.awt.Stroke)
     */
    @Override
    public List<FreePlotSeriesCollection> createSeriesCollections(XYPlot plot, int domainAxisIndex, Stroke stroke) {
        List<FreePlotSeriesCollection> list = new ArrayList<>();
        list.add(new FreePlotSeriesCollection(TEMPERATURE_COLLECTION_NAME, Temperature.getDefaultUnit(), (NUM_COLLECTIONS * domainAxisIndex) + 0, domainAxisIndex, plot, stroke, this));
        list.add(new FreePlotSeriesCollection(HUMIDITY_COLLECTION_NAME, Humidity.Unit.RELATIVE_HUMIDITY, (NUM_COLLECTIONS * domainAxisIndex) + 1, domainAxisIndex, plot, stroke, this));
        list.add(new FreePlotSeriesCollection(PRESSURE_COLLECTION_NAME, Pressure.getDefaultUnit(), (NUM_COLLECTIONS * domainAxisIndex) + 2, domainAxisIndex, plot, stroke, this));
        list.add(new FreePlotSeriesCollection(WIND_COLLECTION_NAME, Speed.getDefaultUnit(), (NUM_COLLECTIONS * domainAxisIndex) + 3, domainAxisIndex, plot, stroke, this));
        list.add(new FreePlotSeriesCollection(RAIN_COLLECTION_NAME, Depth.getDefaultUnit(), (NUM_COLLECTIONS * domainAxisIndex) + 4, domainAxisIndex, plot, stroke, this));
        list.add(new FreePlotSeriesCollection(SOLAR_COLLECTION_NAME, SolarRadiation.getDefaultUnit(), (NUM_COLLECTIONS * domainAxisIndex) + 5, domainAxisIndex, plot, stroke, this));
        return list;
    }
    
    /*
     * (non-Javadoc)
     * @see com.bdb.weather.display.freeplot.FreePlotSeriesCollection.SeriesFactory#updateRenderer(com.bdb.weather.display.freeplot.FreePlotSeries, org.jfree.chart.renderer.xy.DefaultXYItemRenderer)
     */
    @Override
    public void updateRenderer(FreePlotSeries series, DefaultXYItemRenderer renderer) {
    }
    
    // TODO Change the paints to use preferences 
    /**
     * Create the JFreeChart series for the temperature values.
     * 
     * @param stroke The stroke for the series
     */
    private List<FreePlotSeries<SummaryRecord>> createTemperatureSeries(Stroke stroke) {
        List<FreePlotSeries<SummaryRecord>> list = new ArrayList<>();
 
        int n = 0;
        list.add(new FreePlotSeries<>(HIGH_TEMPERATURE_SERIES_NAME, n++, Color.RED, stroke, SummaryRecord::getMaxOutdoorTemp, dateMethod, period));
        list.add(new FreePlotSeries<>(LOW_TEMPERATURE_SERIES_NAME, n++, Color.BLUE, stroke, SummaryRecord::getMinOutdoorTemp, dateMethod, period));
        list.add(new FreePlotSeries<>(MEAN_TEMPERATURE_SERIES_NAME, n++, Color.CYAN, stroke, SummaryRecord::getAvgOutdoorTemp, dateMethod, period));
        return list;
    }
    
    /**
     * Create the JFreeChart series for the humidity values.
     * 
     * @param stroke The stroke for the series
     */
    private List<FreePlotSeries<SummaryRecord>> createHumiditySeries(Stroke stroke) {
        List<FreePlotSeries<SummaryRecord>> list = new ArrayList<>();

        int n = 0;
        list.add(new FreePlotSeries<>(MAX_OUTDOOR_HUMIDITY_SERIES_NAME, n++, Color.GRAY, stroke, SummaryRecord::getMaxOutdoorHumidity, dateMethod, period));
        list.add(new FreePlotSeries<>(MIN_OUTDOOR_HUMIDITY_SERIES_NAME, n++, Color.BLACK, stroke, SummaryRecord::getMinOutdoorHumidity, dateMethod, period));
        list.add(new FreePlotSeries<>(MEAN_OUTDOOR_HUMIDITY_SERIES_NAME, n++, Color.BLACK, stroke, SummaryRecord::getAvgOutdoorHumidity, dateMethod, period));
        return list;
    }
    /**
     * Create the JFreeChart series for the pressure values.
     * 
     * @param stroke The stroke for the series
     */
    private List<FreePlotSeries<SummaryRecord>> createPressureSeries(Stroke stroke) {
        List<FreePlotSeries<SummaryRecord>> list = new ArrayList<>();
        int n = 0;
        list.add(new FreePlotSeries<>(MAX_BARO_PRESSURE_SERIES_NAME, n++, Color.ORANGE, stroke, SummaryRecord::getMaxBaroPressure, dateMethod, period));
        list.add(new FreePlotSeries<>(MIN_BARO_PRESSURE_SERIES_NAME, n++, Color.PINK, stroke, SummaryRecord::getMinBaroPressure, dateMethod, period));
        list.add(new FreePlotSeries<>(MEAN_BARO_PRESSURE_SERIES_NAME, n++, Color.PINK, stroke, SummaryRecord::getAvgBaroPressure, dateMethod, period));
        
        return list;
    }
    
    /**
     * Create the JFreeChart series for the wind values.
     * 
     * @param stroke The stroke for the series
     */
    private List<FreePlotSeries<SummaryRecord>> createWindSeries(Stroke stroke) {
        List<FreePlotSeries<SummaryRecord>> list = new ArrayList<>();
        int n = 0;
        list.add(new FreePlotSeries<>(AVG_WIND_SERIES_NAME, n++, Color.CYAN, stroke, SummaryRecord::getAvgWindSpeed, dateMethod, period));
        list.add(new FreePlotSeries<>(MAX_WIND_SERIES_NAME, n++, Color.yellow, stroke, SummaryRecord::getMaxWindSpeed, dateMethod, period));
        list.add(new FreePlotSeries<>(MAX_GUST_SERIES_NAME, n++, Color.yellow, stroke, SummaryRecord::getMaxWindGust, dateMethod, period));
        
        return list;
    }
    
    /**
     * Create the JFreeChart series for the rain values.
     * 
     * @param stroke The stroke for the series
     */
    private List<FreePlotSeries<SummaryRecord>> createRainSeries(Stroke stroke) {
        List<FreePlotSeries<SummaryRecord>> list = new ArrayList<>();
        int n = 0;
        list.add(new FreePlotSeries<>(RAIN_SERIES_NAME, n++, Color.BLUE, stroke, SummaryRecord::getTotalRainfall, dateMethod, period));
        list.add(new FreePlotSeries<>(ET_SERIES_NAME, n++, Color.RED, stroke, SummaryRecord::getTotalET, dateMethod, period));
        
        return list;
    }
    
    /**
     * Create the JFreeChart series for the Solar Radiation values.
     * 
     * @param stroke The stroke for the series
     */
    private List<FreePlotSeries<SummaryRecord>> createSolarRadiationSeries(Stroke stroke) {
        List<FreePlotSeries<SummaryRecord>> list = new ArrayList<>();
        int n = 0;
        list.add(new FreePlotSeries<>(MAX_SOLAR_RADIATION_NAME, n++, Color.ORANGE, stroke, SummaryRecord::getMaxSolarRadiation, dateMethod, period));
        list.add(new FreePlotSeries<>(MEAN_SOLAR_RADIATION_NAME, n++, Color.PINK, stroke, SummaryRecord::getAvgSolarRadiation, dateMethod, period));
        
        return list;
    }
    
    /**
     * Get the speed bins that correspond to the weather station of this plot
     * 
     * @return The speed bins
     */
    protected List<SpeedBin> getSpeedBins() {
        return speedBins;
    }
    
    /**
     * Get the temperature bins that correspond to the weather station of this plot
     * 
     * @return The temperature bins
     */
    protected TemperatureBinMgr getTemperatureBinMgr() {
        return temperatureBinMgr;
    }
}
