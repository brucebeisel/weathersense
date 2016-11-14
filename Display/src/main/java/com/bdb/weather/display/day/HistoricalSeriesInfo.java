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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import javafx.scene.paint.Color;

import com.bdb.util.measurement.Measurement;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.Sensor;
import com.bdb.weather.display.StageUtilities;
import com.bdb.weather.display.preferences.ColorPreferences;

/**
 *
 * @author Bruce
 */
public class HistoricalSeriesInfo {
    public static final String INDOOR_TEMPERATURE_SERIES = "Indoor Temperature";
    public static final String AVG_OUTDOOR_TEMPERATURE_SERIES = "Average Outdoor Temperature";
    public static final String HIGH_OUTDOOR_TEMPERATURE_SERIES = "High Outdoor Temperature";
    public static final String LOW_OUTDOOR_TEMPERATURE_SERIES = "Low Outdoor Temperature";
    public static final String OUTDOOR_HUMIDITY_SERIES = "Outdoor Humidity";
    public static final String INDOOR_HUMIDITY_SERIES = "Indoor Humidity";
    public static final String DEW_POINT_SERIES = "Dew Point";
    public static final String HEAT_INDEX_SERIES = "Heat Index";
    public static final String WIND_CHILL_SERIES = "Wind Chill";
    public static final String BAROMETER_SERIES = "Barometer";
    public static final String AVG_SOLAR_RADIATION_SERIES = "Avg Solar Radiation";
    public static final String HIGH_SOLAR_RADIATION_SERIES = "High Solar Radiation";
    public static final String AVG_UV_INDEX_SERIES = "Avg UV Index";
    public static final String HIGH_UV_INDEX_SERIES = "High UV Index";
    public static final String HIGH_WIND_SPEED_SERIES = "High Wind Speed";
    public static final String HIGH_WIND_DIRECTION_SERIES = "High Wind Direction";
    public static final String AVG_WIND_SPEED_SERIES = "Avg Wind Speed";
    public static final String AVG_WIND_DIRECTION_SERIES = "Avg Wind Direction";
    public static final String WIND_GUST_SPEED_SERIES = "Wind Gust Speed";
    public static final String WIND_GUST_DIRECTION_SERIES = "Wind Gust Direction";
    private static final HistoricalSeriesInfo AVG_OUTDOOR_TEMPERATURE = new HistoricalSeriesInfo(AVG_OUTDOOR_TEMPERATURE_SERIES, HistoricalRecord::getAvgOutdoorTemperature, ColorPreferences.OUTDOOR_TEMP);
    private static final HistoricalSeriesInfo HIGH_OUTDOOR_TEMPERATURE = new HistoricalSeriesInfo(HIGH_OUTDOOR_TEMPERATURE_SERIES, HistoricalRecord::getHighOutdoorTemperature, ColorPreferences.HIGH_OUTDOOR_TEMP);
    private static final HistoricalSeriesInfo LOW_OUTDOOR_TEMPERATURE = new HistoricalSeriesInfo(LOW_OUTDOOR_TEMPERATURE_SERIES, HistoricalRecord::getLowOutdoorTemperature, ColorPreferences.LOW_OUTDOOR_TEMP);
    private static final HistoricalSeriesInfo INDOOR_TEMPERATURE = new HistoricalSeriesInfo(INDOOR_TEMPERATURE_SERIES, HistoricalRecord::getIndoorTemperature, ColorPreferences.INDOOR_TEMP);
    private static final HistoricalSeriesInfo INDOOR_HUMIDITY = new HistoricalSeriesInfo(INDOOR_HUMIDITY_SERIES, HistoricalRecord::getIndoorHumidity, ColorPreferences.INDOOR_HUMIDITY);
    private static final HistoricalSeriesInfo OUTDOOR_HUMIDITY = new HistoricalSeriesInfo(OUTDOOR_HUMIDITY_SERIES, HistoricalRecord::getOutdoorHumidity, ColorPreferences.OUTDOOR_HUMIDITY);
    private static final HistoricalSeriesInfo DEW_POINT = new HistoricalSeriesInfo(DEW_POINT_SERIES, HistoricalRecord::getDewPoint, ColorPreferences.DEW_POINT);
    private static final HistoricalSeriesInfo HEAT_INDEX = new HistoricalSeriesInfo(HEAT_INDEX_SERIES, HistoricalRecord::getHeatIndex, ColorPreferences.HEAT_INDEX);
    private static final HistoricalSeriesInfo WIND_CHILL = new HistoricalSeriesInfo(WIND_CHILL_SERIES, HistoricalRecord::getWindChill, ColorPreferences.WIND_CHILL);
    private static final HistoricalSeriesInfo BAROMETER = new HistoricalSeriesInfo(BAROMETER_SERIES, HistoricalRecord::getBaroPressure, ColorPreferences.BARO_PRESSURE);
    private static final HistoricalSeriesInfo AVG_SOLAR_RADIATION = new HistoricalSeriesInfo(AVG_SOLAR_RADIATION_SERIES, HistoricalRecord::getAvgSolarRadiation, ColorPreferences.RAIN);
    private static final HistoricalSeriesInfo HIGH_SOLAR_RADIATION = new HistoricalSeriesInfo(HIGH_SOLAR_RADIATION_SERIES, HistoricalRecord::getHighSolarRadiation, ColorPreferences.RAIN);
    //private static final HistoricalSeriesInfo AVG_UV_INDEX = new HistoricalSeriesInfo(AVG_UV_INDEX_SERIES, HistoricalRecord::getAvgUvIndex, ColorPreferences.RAIN);
    private static final HistoricalSeriesInfo HIGH_WIND_SPEED = new HistoricalSeriesInfo(HIGH_WIND_SPEED_SERIES, HistoricalRecord::getHighWindSpeed, ColorPreferences.WIND_SPEED);
    private static final HistoricalSeriesInfo HIGH_WIND_DIRECTION = new HistoricalSeriesInfo(HIGH_WIND_DIRECTION_SERIES, HistoricalRecord::getHighWindDirection, ColorPreferences.WIND_SPEED);
    private static final HistoricalSeriesInfo AVG_WIND_SPEED = new HistoricalSeriesInfo(AVG_WIND_SPEED_SERIES, HistoricalRecord::getAvgWindSpeed, ColorPreferences.WIND_SPEED);
    private static final HistoricalSeriesInfo AVG_WIND_DIRECTION = new HistoricalSeriesInfo(AVG_WIND_DIRECTION_SERIES, HistoricalRecord::getAvgWindDirection, ColorPreferences.WIND_SPEED);
    private static final HistoricalSeriesInfo WIND_GUST_SPEED = new HistoricalSeriesInfo(WIND_GUST_SPEED_SERIES, HistoricalRecord::getWindGustSpeed, ColorPreferences.WIND_SPEED);
    private static final HistoricalSeriesInfo WIND_GUST_DIRECTION = new HistoricalSeriesInfo(WIND_GUST_DIRECTION_SERIES, HistoricalRecord::getWindGustDirection, ColorPreferences.WIND_SPEED);
    private static final Map<String,HistoricalSeriesInfo> seriesInfo = new HashMap<>();
    private final String seriesName;
    private final int sensorId;
    private final Function<HistoricalRecord,Measurement> accessor;
    private final BiFunction<HistoricalRecord,Integer,Measurement> sensorAccessor;
    private final String colorPreferenceName;
    private Color paint;

    static {
        seriesInfo.put(INDOOR_TEMPERATURE_SERIES, INDOOR_TEMPERATURE);
        seriesInfo.put(AVG_OUTDOOR_TEMPERATURE_SERIES, AVG_OUTDOOR_TEMPERATURE);
        seriesInfo.put(HIGH_OUTDOOR_TEMPERATURE_SERIES, HIGH_OUTDOOR_TEMPERATURE);
        seriesInfo.put(LOW_OUTDOOR_TEMPERATURE_SERIES, LOW_OUTDOOR_TEMPERATURE);
        seriesInfo.put(INDOOR_HUMIDITY_SERIES, INDOOR_HUMIDITY);
        seriesInfo.put(OUTDOOR_HUMIDITY_SERIES, OUTDOOR_HUMIDITY);
        seriesInfo.put(DEW_POINT_SERIES, DEW_POINT);
        seriesInfo.put(HEAT_INDEX_SERIES, HEAT_INDEX);
        seriesInfo.put(WIND_CHILL_SERIES, WIND_CHILL);
        seriesInfo.put(BAROMETER_SERIES, BAROMETER);
        seriesInfo.put(AVG_SOLAR_RADIATION_SERIES, AVG_SOLAR_RADIATION);
        seriesInfo.put(HIGH_SOLAR_RADIATION_SERIES, HIGH_SOLAR_RADIATION);
        seriesInfo.put(HIGH_WIND_SPEED_SERIES, HIGH_WIND_SPEED);
        seriesInfo.put(HIGH_WIND_DIRECTION_SERIES, HIGH_WIND_DIRECTION);
        seriesInfo.put(AVG_WIND_SPEED_SERIES, AVG_WIND_SPEED);
        seriesInfo.put(AVG_WIND_DIRECTION_SERIES, AVG_WIND_DIRECTION);
        seriesInfo.put(WIND_GUST_SPEED_SERIES, WIND_GUST_SPEED);
        seriesInfo.put(WIND_GUST_DIRECTION_SERIES, WIND_GUST_DIRECTION);
    }

    public static HistoricalSeriesInfo find(String seriesName) {
        return seriesInfo.get(seriesName);
    }

    public static void setColorsFromPreferences() {
        seriesInfo.values().stream().forEach((info) -> {
            info.paint = ColorPreferences.getInstance().getColorPref(info.colorPreferenceName);
        });
    }

    public static void addExtraSensors(Collection<Sensor> sensors) {
        for (Sensor sensor : sensors) {
            HistoricalSeriesInfo info = null;
            switch (sensor.getType()) {
                case THERMOMETER:
                case SOIL_TEMPERATURE:
                case LEAF_TEMPERATURE:
                    info = new HistoricalSeriesInfo(sensor.getName(), sensor.getSensorId(), HistoricalRecord::getTemperatureForSensor, ColorPreferences.OUTDOOR_TEMP);
                    break;

                case HYGROMETER:
                    info = new HistoricalSeriesInfo(sensor.getName(), sensor.getSensorId(), HistoricalRecord::getHumidityForSensor, ColorPreferences.OUTDOOR_TEMP);
                    break;

                case LEAF_WETNESS:
                    info = new HistoricalSeriesInfo(sensor.getName(), sensor.getSensorId(), HistoricalRecord::getLeafWetnessForSensor, ColorPreferences.OUTDOOR_TEMP);
                    break;

                case SOIL_MOISTURE:
                    info = new HistoricalSeriesInfo(sensor.getName(), sensor.getSensorId(), HistoricalRecord::getSoilMoistureForSensor, ColorPreferences.OUTDOOR_TEMP);
                    break;
            }

            if (info != null)
                seriesInfo.put(sensor.getName(), info);
        }
    }

    private HistoricalSeriesInfo(String name, Function<HistoricalRecord,Measurement> accessor, String colorPreference) {
        if (accessor == null)
            throw new IllegalArgumentException("Accessor may not be null");

        seriesName = name;
        this.accessor = accessor;
        sensorAccessor = null;
        colorPreferenceName = colorPreference;
        paint = ColorPreferences.getInstance().getColorPref(colorPreference);
        sensorId = 0;
    }

    private HistoricalSeriesInfo(String name, int sensorId, BiFunction<HistoricalRecord,Integer,Measurement> accessor, String colorPreference) {
        if (accessor == null)
            throw new IllegalArgumentException("Sensor accessor may not be null");
        seriesName = name;
        this.accessor = null;
        this.sensorAccessor = accessor;
        colorPreferenceName = colorPreference;
        paint = ColorPreferences.getInstance().getColorPref(colorPreference);
        this.sensorId = sensorId;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public Measurement getValue(HistoricalRecord record) {
        if (accessor != null)
            return accessor.apply(record);
        else
            return sensorAccessor.apply(record, sensorId);
    }

    public java.awt.Color getPaint() {
        return StageUtilities.toAwtColor(paint);
    }
}
