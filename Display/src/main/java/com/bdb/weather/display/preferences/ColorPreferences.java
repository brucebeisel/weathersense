/*
 * Copyright (C) 2016 bruce
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
package com.bdb.weather.display.preferences;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javafx.scene.paint.Color;

import com.bdb.weather.display.ErrorDisplayer;
import com.bdb.weather.display.WeatherSense;

/**
 *
 * @author bruce
 */
public class ColorPreferences {
    private class ColorPreference {
        public String name;
        public Color defaultValue;
        public ColorPreference(String preferenceName, Color defaultValue) {
            this.name = preferenceName;
            this.defaultValue = defaultValue;
        }
    }
    private static final String COLOR_NODE = "Color";
    public static final String GUAGE_BACKGROUND = "Guage Background Color";
    public static final Color  GUAGE_BACKGROUND_DEFAULT = Color.GRAY;
    public static final String GUAGE_DIAL_COLOR = "Guage Dial Color";
    public static final Color  GUAGE_DIAL_COLOR_DEFAULT = Color.BLUE;
    public static final String GUAGE_VALUE = "Guage Value Color";
    public static final Color  GUAGE_VALUE_DEFAULT = Color.CYAN;
    public static final String GUAGE_SCALE_TEXT = "Guage Scale Text Color";
    public static final Color  GUAGE_SCALE_TEXT_DEFAULT = Color.BLACK;
    public static final String GUAGE_SCALE_TICK = "Guage Scale Tick Color";
    public static final Color  GUAGE_SCALE_TICK_DEFAULT = Color.BLACK;
    public static final String GUAGE_VALUE_RANGE = "Guage Value Range Color";
    public static final Color  GUAGE_VALUE_RANGE_DEFAULT = Color.BLACK;
    public static final String WIND_GUAGE_DIRECTION_POINTER = "Wind Guage Direction Pointer Color";
    public static final String WIND_GUAGE_SPEED_POINTER = "Wind Guage Speed Pointer Color";
    public static final String WIND_GUAGE_GUST_POINTER = "Wind Guage Gust Pointer Color";
    public static final String WIND_GUAGE_DOMINANT_DIR_POINTER = "Wind Guage Dominant Direction Pointer Color";
    public static final String THERMOMETER_MERCURY = "Thermometer Mercury Color";
    public static final String THERMOMETER_HIGH_TEMPERAURE = "Thermometer High Temperature Color";
    public static final String THERMOMETER_LOW_TEMPERAURE = "Thermometer Low Temperature Color";
    public static final String RAIN_GUAGE_WATER = "Rain Guage Water Color";
    public static final String RAIN_GUAGE_BEAKER = "Rain Guage Beaker Color";

    public static final String OUTDOOR_TEMP = "Outdoor Temp Color";
    public static final String HIGH_OUTDOOR_TEMP = "High Outdoor Temp Color";
    public static final String LOW_OUTDOOR_TEMP = "Low Outdoor Temp Color";
    public static final String MEAN_OUTDOOR_TEMP = "Mean Outdoor Temp Color";
    
    public static final String RECORD_HIGH_OUTDOOR_TEMP = "Record High Outdoor Temp Color";
    public static final String RECORD_LOW_OUTDOOR_TEMP = "Record Low Outdoor Temp Color";
    
    public static final String AVG_HIGH_OUTDOOR_TEMP = "Average High Outdoor Temp Color";
    public static final String AVG_LOW_OUTDOOR_TEMP = "Average Low Outdoor Temp Color";
    public static final String AVG_MEAN_OUTDOOR_TEMP = "Average Mean Outdoor Temp Color";
   
    public static final String INDOOR_TEMP = "Indoor Temp Color";
    public static final String HIGH_INDOOR_TEMP = "High Indoor Temp Color";
    public static final String LOW_INDOOR_TEMP = "Low Indoor Temp Color";
    public static final String MEAN_INDOOR_TEMP = "Mean Indoor Temp Color";
    
    public static final String OUTDOOR_HUMIDITY = "Outdoor Humidity Color";
    public static final String HIGH_OUTDOOR_HUMIDITY = "High Outdoor Humidity Color";
    public static final String LOW_OUTDOOR_HUMIDITY = "Low Outdoor Humidity Color";
    public static final String MEAN_OUTDOOR_HUMIDITY = "Mean Outdoor Humidity Color";
   
    public static final String INDOOR_HUMIDITY = "Indoor Humidity Color";
    public static final String HIGH_INDOOR_HUMIDITY = "High Indoor Humidity Color";
    public static final String LOW_INDOOR_HUMIDITY = "Low Indoor Humidity Color";
    public static final String MEAN_INDOOR_HUMIDITY = "Mean Indoor Humidity Color";
    
    public static final String DEW_POINT = "Dew Point Color";
    public static final String WIND_CHILL = "Wind Chill Color";
    public static final String HEAT_INDEX = "Heat Index Color";
    
    public static final String BARO_PRESSURE = "Baro Pressure Color";
    public static final String HIGH_BARO_PRESSURE = "High Baro Pressure Color";
    public static final String LOW_BARO_PRESSURE = "Low Baro Pressure Color";
    public static final String MEAN_BARO_PRESSURE = "Mean Baro Pressure Color";
    
    public static final String RAIN = "Rain Color";
    public static final String RECORD_RAIN = "Record Rain Color";
    public static final String AVG_RAIN = "Avg Rain Color";
    
    public static final String WIND_SPEED = "Wind Speed Color";
    public static final String AVG_WIND_SPEED = "Avg Wind Speed Color";
    public static final String MAX_WIND_SPEED = "Max Wind Speed Color";
    public static final String WIND_GUST = "Wind Gust Speed Color";
    public static final String MAX_WIND_GUST = "Max Wind Gust Color";
    
    private final Preferences rootPref = Preferences.userNodeForPackage(WeatherSense.class);
    private final Preferences colorNode = rootPref.node(COLOR_NODE);
    private final static ColorPreferences instance;

    private final Map<String,Color> preferenceMap = new HashMap<>();
    private final ColorPreference preferences[] = {
        new ColorPreference(GUAGE_BACKGROUND, GUAGE_BACKGROUND_DEFAULT),
        new ColorPreference(GUAGE_DIAL_COLOR, GUAGE_DIAL_COLOR_DEFAULT),
        new ColorPreference(GUAGE_VALUE, GUAGE_VALUE_DEFAULT),
        new ColorPreference(GUAGE_SCALE_TEXT, GUAGE_SCALE_TEXT_DEFAULT),
        new ColorPreference(GUAGE_SCALE_TICK, GUAGE_SCALE_TICK_DEFAULT),
        new ColorPreference(GUAGE_VALUE_RANGE, GUAGE_VALUE_RANGE_DEFAULT),
    };

    private static final Logger logger = Logger.getLogger(ColorPreferences.class.getName());

    static {
        instance = new ColorPreferences();
    }

    private ColorPreferences() {
        for (ColorPreference pref : preferences) {
            preferenceMap.put(pref.name, pref.defaultValue);
        }
    }

    public static ColorPreferences getInstance() {
        return instance;
    }

    public void sync() {
        try {
            colorNode.sync();
        }
        catch (BackingStoreException e) {
            ErrorDisplayer.getInstance().displayError("User preference storage error. Please contact support");
        }
    }

    private static String colorToString(Color color) {
        return String.format("#%02x%02x%02x", (int)(color.getRed() * 255), (int)(color.getGreen() * 255), (int)(color.getBlue() * 255));
    }

    public void putColorPref(String preferenceName, Color color) {
        colorNode.put(preferenceName, colorToString(color));
    }
    
    private Color getColorPref(String preferenceName, Color defaultColor) {
        try {
            String value = colorNode.get(preferenceName, null);
            if (value != null)
                return Color.web(value);
            else
                return defaultColor;
        }
        catch (IllegalArgumentException e) {
            return defaultColor;
        }
    }

    public Color getColorPref(String preferenceName) {
        Color defaultColor = preferenceMap.get(preferenceName);
        if (defaultColor == null)
            defaultColor = Color.RED;

        return getColorPref(preferenceName, defaultColor);
    }
    
    public void putHighOutdoorTempColorPref(Color color) {
        colorNode.put(HIGH_OUTDOOR_TEMP, colorToString(color));
    }

    public Color getHighOutdoorTempColorPref() {
        return getColorPref(HIGH_OUTDOOR_TEMP, Color.RED);
    }

    public void putLowOutdoorTempColorPref(Color color) {
        colorNode.put(LOW_OUTDOOR_TEMP, colorToString(color));
    }

    public Color getLowOutdoorTempColorPref() {
        return getColorPref(LOW_OUTDOOR_TEMP, Color.BLUE);
    }

    public void putIndoorTempColorPref(Color color) {
        colorNode.put(INDOOR_TEMP, colorToString(color));
    }

    public Color getIndoorTempColorPref() {
        return getColorPref(INDOOR_TEMP, Color.CYAN);
    }

    public void putOutdoorTempColorPref(Color color) {
        colorNode.put(OUTDOOR_TEMP, colorToString(color));
    }

    public Color getOutdoorTempColorPref() {
        return getColorPref(OUTDOOR_TEMP, Color.MAGENTA);
    }
}