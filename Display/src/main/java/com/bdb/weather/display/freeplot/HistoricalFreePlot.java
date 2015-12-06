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
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import javafx.scene.Node;

import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.data.time.Minute;

import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.Sensor;
import com.bdb.weather.common.SensorType;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.db.HistoryTable;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Humidity;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.SolarRadiation;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;
import com.bdb.weather.display.ComponentContainer;
import com.bdb.weather.display.WeatherSenseConstants;
import com.bdb.weather.display.axis.HumidityRangeAxis;
import com.bdb.weather.display.axis.PressureRangeAxis;
import com.bdb.weather.display.axis.RainRangeAxis;
import com.bdb.weather.display.axis.SolarRadiationAxis;
import com.bdb.weather.display.axis.TemperatureRangeAxis;
import com.bdb.weather.display.axis.UvIndexAxis;
import com.bdb.weather.display.axis.WindSpeedRangeAxis;
import com.bdb.weather.display.freeplot.FreePlot.SeriesCollectionFactory;
import com.bdb.weather.display.freeplot.FreePlotSeriesCollection.SeriesFactory;
import com.bdb.weather.display.preferences.UserPreferences;

/**
 * Class to plot historical records (the smallest data).
 * 
 * @author Bruce
 */
public class HistoricalFreePlot implements ComponentContainer, SeriesFactory, SeriesCollectionFactory {
    private static final String TEMPERATURE_COLLECTION_NAME = "Temperature";
    private static final String HUMIDITY_COLLECTION_NAME = "Humidity";
    private static final String PRESSURE_COLLECTION_NAME = "Pressure";
    private static final String WIND_COLLECTION_NAME = "Wind";
    private static final String RAIN_COLLECTION_NAME = "Rain";
    private static final String SOLAR_RADIATION_COLLECTION_NAME = "Solar Radiation";
    private static final String UV_INDEX_COLLECTION_NAME = "UV Index";
    private static final int    NUM_COLLECTIONS = 7;
    
    private static final String OUTDOOR_TEMPERATURE_SERIES_NAME = "Outdoor Temperature";
    private static final String INDOOR_TEMPERATURE_SERIES_NAME = "Indoor Temperature";
    private static final String OUTDOOR_HUMIDITY_SERIES_NAME = "Outdoor Humidity";
    private static final String INDOOR_HUMIDITY_SERIES_NAME = "Indoor Humidity";
    private static final String DEW_POINT_SERIES_NAME = "Dew Point";
    private static final String HEAT_INDEX_SERIES_NAME = "Heat Index";
    private static final String WIND_CHILL_SERIES_NAME = "Wind Chill";
    private static final String SUSTAINED_WIND_SERIES_NAME = "Sustained Wind";
    private static final String HIGH_WIND_SERIES_NAME = "High Wind";
    private static final String WIND_GUST_SERIES_NAME = "Wind Gust";
    private static final String RAIN_SERIES_NAME = "Rainfall";
    private static final String HIGH_RAIN_RATE_SERIES_NAME = "High Rainfall Rate";
    private static final String BAROMETRIC_PRESSURE_SERIES_NAME = "Barometric Pressure";

    private static final String SOLAR_RADIATION_SERIES_NAME = "Solar Radiation";
    private static final String UV_INDEX_SERIES_NAME = "UV Index";
    
    private static final Class<?> INTERVAL_CLASS = Minute.class;
    
    private final FreePlot freePlot;
    private final HistoryTable table;
    private final WeatherStation ws;
    
    /**
     * Constructor.
     * 
     * @param ws The weather station
     * @param connection The connection to the database
     */
    public HistoricalFreePlot(WeatherStation ws, DBConnection connection) {
        this.ws = ws;
        freePlot = new FreePlot(this);
        table = new HistoryTable(connection);
    }
    
    /**
     * Return the swing component that is the container for this plot.
     * 
     * @return The swing container
     */
    @Override
    public Node getComponent() {
        //return freePlot.getComponent();
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
        groupControl.addSeriesControl(new SeriesControl(OUTDOOR_TEMPERATURE_SERIES_NAME, listener));
        groupControl.addSeriesControl(new SeriesControl(INDOOR_TEMPERATURE_SERIES_NAME, listener));
        groupControl.addSeriesControl(new SeriesControl(DEW_POINT_SERIES_NAME, listener));
        groupControl.addSeriesControl(new SeriesControl(HEAT_INDEX_SERIES_NAME, listener));
        groupControl.addSeriesControl(new SeriesControl(WIND_CHILL_SERIES_NAME, listener));
        for (Sensor sensor : ws.getSensorManager().getExtraSensors(SensorType.THERMOMETER)) {
            groupControl.addSeriesControl(new SeriesControl(sensor.getName(), listener));
        }
        list.put(groupControl.getGroupName(), groupControl);
        
        groupControl = new SeriesGroupControl(HUMIDITY_COLLECTION_NAME, new HumidityRangeAxis(), listener);
        groupControl.addSeriesControl(new SeriesControl(OUTDOOR_HUMIDITY_SERIES_NAME, listener));
        groupControl.addSeriesControl(new SeriesControl(INDOOR_HUMIDITY_SERIES_NAME, listener));
        list.put(groupControl.getGroupName(), groupControl);
        
        groupControl = new SeriesGroupControl(PRESSURE_COLLECTION_NAME, new PressureRangeAxis(), listener);
        groupControl.addSeriesControl(new SeriesControl(BAROMETRIC_PRESSURE_SERIES_NAME, listener));
        list.put(groupControl.getGroupName(), groupControl);
        
        groupControl = new SeriesGroupControl(WIND_COLLECTION_NAME, new WindSpeedRangeAxis(), listener);
        groupControl.addSeriesControl(new SeriesControl(SUSTAINED_WIND_SERIES_NAME, listener));
        groupControl.addSeriesControl(new SeriesControl(HIGH_WIND_SERIES_NAME, listener));
        groupControl.addSeriesControl(new SeriesControl(WIND_GUST_SERIES_NAME, listener));
        list.put(groupControl.getGroupName(), groupControl);
        
        groupControl = new SeriesGroupControl(RAIN_COLLECTION_NAME, new RainRangeAxis(), listener);
        groupControl.addSeriesControl(new SeriesControl(RAIN_SERIES_NAME, listener));
        groupControl.addSeriesControl(new SeriesControl(HIGH_RAIN_RATE_SERIES_NAME, listener));
        list.put(groupControl.getGroupName(), groupControl);
        
        groupControl = new SeriesGroupControl(SOLAR_RADIATION_COLLECTION_NAME, new SolarRadiationAxis(), listener);
        groupControl.addSeriesControl(new SeriesControl(SOLAR_RADIATION_SERIES_NAME, listener));
        list.put(groupControl.getGroupName(), groupControl);
        
        groupControl = new SeriesGroupControl(UV_INDEX_COLLECTION_NAME, new UvIndexAxis(), listener);
        groupControl.addSeriesControl(new SeriesControl(UV_INDEX_SERIES_NAME, listener));
        list.put(groupControl.getGroupName(), groupControl);
        
        return list;
    }
    
    /*
     * (non-Javadoc)
     * @see com.bdb.weather.display.freeplot.FreePlotSeriesCollection.SeriesFactory#createSeriesGroup(java.lang.String, java.awt.Stroke)
     */
    @Override
    public List<FreePlotSeries<HistoricalRecord>> createSeriesGroup(String groupName, Stroke stroke) {
        List<FreePlotSeries<HistoricalRecord>> list = new ArrayList<>();
        
        Function<HistoricalRecord,TemporalAccessor> timeMethod = HistoricalRecord::getTime;
        switch (groupName) {
            case TEMPERATURE_COLLECTION_NAME:
                list = createTemperatureSeries(stroke, timeMethod);
                break;
            case HUMIDITY_COLLECTION_NAME:
                list = createHumiditySeries(stroke, timeMethod);
                break;
            case PRESSURE_COLLECTION_NAME:
                list = createPressureSeries(stroke, timeMethod);
                break;
            case WIND_COLLECTION_NAME:
                list = createWindSeries(stroke, timeMethod);
                break;
            case RAIN_COLLECTION_NAME:
                list = createRainSeries(stroke, timeMethod);
                break;
            case SOLAR_RADIATION_COLLECTION_NAME:
                list = createSolarRadiationSeries(stroke, timeMethod);
                break;
            case UV_INDEX_COLLECTION_NAME:
                list = createUvIndexSeries(stroke, timeMethod);
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
        list.add(new FreePlotSeriesCollection(SOLAR_RADIATION_COLLECTION_NAME, SolarRadiation.getDefaultUnit(), (NUM_COLLECTIONS * domainAxisIndex) + 5, domainAxisIndex, plot, stroke, this));
        list.add(new FreePlotSeriesCollection(UV_INDEX_COLLECTION_NAME, null, (NUM_COLLECTIONS * domainAxisIndex) + 6, domainAxisIndex, plot, stroke, this));

        return list;
    }
    
    /*
     * (non-Javadoc)
     * @see com.bdb.weather.display.freeplot.FreePlotSeriesCollection.SeriesFactory#updateRenderer(com.bdb.weather.display.freeplot.FreePlotSeries, org.jfree.chart.renderer.xy.DefaultXYItemRenderer)
     */
    @Override
    public void updateRenderer(FreePlotSeries series, DefaultXYItemRenderer renderer) {
        int index = series.getSeriesIndex();
        if (series.getName().equals(WIND_GUST_SERIES_NAME))
            WeatherSenseConstants.configureGustRenderer(renderer, index);
        else if (series.getName().equals(SUSTAINED_WIND_SERIES_NAME)) {
            renderer.setSeriesLinesVisible(index, true);
            renderer.setSeriesShapesVisible(index, false);
        }
    }
    
    // TODO Change the paints to use preferences 
    /**
     * Create the series for temperature values.
     * 
     * @param stroke The stroke that is used to draw the series
     * @param timeMethod The method that is used to "get" the date from the record
     */
    private List<FreePlotSeries<HistoricalRecord>> createTemperatureSeries(Stroke stroke, Function<HistoricalRecord,TemporalAccessor> timeMethod) {
        List<FreePlotSeries<HistoricalRecord>> list = new ArrayList<>();
        int n = 0;
        list.add(new FreePlotSeries<>(OUTDOOR_TEMPERATURE_SERIES_NAME, n++, UserPreferences.getInstance().getOutdoorTempColorPref(), stroke, HistoricalRecord::getAvgOutdoorTemperature, timeMethod, INTERVAL_CLASS));
        list.add(new FreePlotSeries<>(INDOOR_TEMPERATURE_SERIES_NAME, n++, UserPreferences.getInstance().getIndoorTempColorPref(), stroke, HistoricalRecord::getIndoorTemperature, timeMethod, INTERVAL_CLASS));
        list.add(new FreePlotSeries<>(DEW_POINT_SERIES_NAME, n++, Color.BLUE, stroke, HistoricalRecord::getDewPoint, timeMethod, INTERVAL_CLASS));
        list.add(new FreePlotSeries<>(HEAT_INDEX_SERIES_NAME, n++, Color.MAGENTA, stroke, HistoricalRecord::getHeatIndex, timeMethod, INTERVAL_CLASS));
        list.add(new FreePlotSeries<>(WIND_CHILL_SERIES_NAME, n++, Color.PINK, stroke, HistoricalRecord::getWindChill, timeMethod, INTERVAL_CLASS));
        for (Sensor sensor : ws.getSensorManager().getExtraSensors(SensorType.THERMOMETER)) {
            Object[] args = {sensor.getSensorId()};
            Class[] argTypes = {int.class};
            //list.add(new FreePlotSeries(sensor.getName(), n++, Color.BLUE.brighter(), stroke, HistoricalRecord.class.getMethod("getTemperatureForSensor", argTypes), args, timeMethod, INTERVAL_CLASS));
        }
        
        return list;
    }
    
    /**
     * Create the series for humidity values.
     * 
     * @param stroke The stroke that is used to draw the series
     * @param timeMethod The method that is used to "get" the date from the record
     */
    private List<FreePlotSeries<HistoricalRecord>> createHumiditySeries(Stroke stroke, Function<HistoricalRecord,TemporalAccessor> timeMethod) {
        List<FreePlotSeries<HistoricalRecord>> list = new ArrayList<>();
        int n = 0;
        list.add(new FreePlotSeries<>(OUTDOOR_HUMIDITY_SERIES_NAME, n++, Color.GRAY, stroke, HistoricalRecord::getOutdoorHumidity, timeMethod, INTERVAL_CLASS));
        list.add(new FreePlotSeries<>(INDOOR_HUMIDITY_SERIES_NAME, n++, Color.BLACK, stroke, HistoricalRecord::getIndoorHumidity, timeMethod, INTERVAL_CLASS));
        
        return list;
    }
    
    /**
     * Create the series for pressure values.
     * 
     * @param stroke The stroke that is used to draw the series
     * @param timeMethod The method that is used to "get" the date from the record
     */
    private List<FreePlotSeries<HistoricalRecord>> createPressureSeries(Stroke stroke, Function<HistoricalRecord,TemporalAccessor> timeMethod) {
        List<FreePlotSeries<HistoricalRecord>> list = new ArrayList<>();
        int n = 0;
        list.add(new FreePlotSeries<>(BAROMETRIC_PRESSURE_SERIES_NAME, n++, Color.ORANGE, stroke, HistoricalRecord::getBaroPressure, timeMethod, INTERVAL_CLASS));
        
        return list;
    }
    
    /**
     * Create the series for wind values.
     * 
     * @param stroke The stroke that is used to draw the series
     * @param timeMethod The method that is used to "get" the date from the record
     */
    private List<FreePlotSeries<HistoricalRecord>> createWindSeries(Stroke stroke, Function<HistoricalRecord,TemporalAccessor> timeMethod) {
        List<FreePlotSeries<HistoricalRecord>> list = new ArrayList<>();
        int n = 0;
        list.add(new FreePlotSeries<>(SUSTAINED_WIND_SERIES_NAME, n++, Color.CYAN, stroke, HistoricalRecord::getAvgWindSpeed, timeMethod, INTERVAL_CLASS));
        list.add(new FreePlotSeries<>(SUSTAINED_WIND_SERIES_NAME, n++, Color.ORANGE, stroke, HistoricalRecord::getHighWindSpeed, timeMethod, INTERVAL_CLASS));
        list.add(new FreePlotSeries<>(WIND_GUST_SERIES_NAME, n++, Color.yellow, stroke, HistoricalRecord::getWindGustSpeed, timeMethod, INTERVAL_CLASS));
        
        return list;
    }
   
    /**
     * Create the series for rain values.
     * 
     * @param stroke The stroke that is used to draw the series
     * @param timeMethod The method that is used to "get" the date from the record
     */
    private List<FreePlotSeries<HistoricalRecord>> createRainSeries(Stroke stroke, Function<HistoricalRecord,TemporalAccessor> timeMethod) {
        List<FreePlotSeries<HistoricalRecord>> list = new ArrayList<>();
        int n = 0;
        list.add(new FreePlotSeries<>(RAIN_SERIES_NAME, n++, Color.BLUE, stroke, HistoricalRecord::getRainfall, timeMethod, INTERVAL_CLASS));
        list.add(new FreePlotSeries<>(HIGH_RAIN_RATE_SERIES_NAME, n++, Color.CYAN, stroke, HistoricalRecord::getHighRainfallRate, timeMethod, INTERVAL_CLASS));
        
        return list;
    }

    /**
     * Create the series for pressure values.
     * 
     * @param stroke The stroke that is used to draw the series
     * @param timeMethod The method that is used to "get" the date from the record
     */
    private List<FreePlotSeries<HistoricalRecord>> createSolarRadiationSeries(Stroke stroke, Function<HistoricalRecord,TemporalAccessor> timeMethod) {
        List<FreePlotSeries<HistoricalRecord>> list = new ArrayList<>();
        int n = 0;
        list.add(new FreePlotSeries<>(SOLAR_RADIATION_SERIES_NAME, n++, Color.ORANGE, stroke, HistoricalRecord::getAvgSolarRadiation, timeMethod, INTERVAL_CLASS));
        
        return list;
    }
    /**
     * Create the series for pressure values.
     * 
     * @param stroke The stroke that is used to draw the series
     * @param timeMethod The method that is used to "get" the date from the record
     */
    private List<FreePlotSeries<HistoricalRecord>> createUvIndexSeries(Stroke stroke, Function<HistoricalRecord,TemporalAccessor> timeMethod) {
        List<FreePlotSeries<HistoricalRecord>> list = new ArrayList<>();
        int n = 0;
        //list.add(new FreePlotSeries<>(UV_INDEX_SERIES_NAME, n++, Color.ORANGE, stroke, HistoricalRecord::getAvgUvIndex, timeMethod, INTERVAL_CLASS));
        return list;
    }
    /*
     * (non-Javadoc)
     * @see com.bdb.weather.display.freeplot.FreePlot.SeriesCollectionFactory#retrieveData(java.lang.String, java.util.Calendar, java.util.Calendar)
     */
    @Override
    public List<?> retrieveData(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        List<HistoricalRecord> list = table.queryRecordsForTimePeriod(startDate, endDate);
        return list;
    }
}