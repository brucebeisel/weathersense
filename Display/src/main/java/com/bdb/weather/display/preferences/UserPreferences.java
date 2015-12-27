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
package com.bdb.weather.display.preferences;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.bdb.weather.common.db.DatabaseConstants;
import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Distance;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;
import com.bdb.weather.display.ErrorDisplayer;
import com.bdb.weather.display.WeatherSense;

public class UserPreferences {
    private static final String DISPLAY_NODE = "Display";
    private static final String UNITS_NODE = "Units";
    private static final String TEMP_UNITS_PREF = "Temperature Units";
    private static final String PRESSURE_UNITS_PREF = "Pressure Units";
    private static final String SPEED_UNITS_PREF = "Speed Units";
    private static final String RAINFALL_UNITS_PREF = "Rainfall Units";
    private static final String ELEVATION_UNITS_PREF = "Elevation Units";

    private static final String PLOT_COLOR_NODE = "Plot Color";
    
    public static final String OUTDOOR_TEMP_COLOR_PREF = "Outdoor Temp Color";
    public static final String HIGH_OUTDOOR_TEMP_COLOR_PREF = "High Outdoor Temp Color";
    public static final String LOW_OUTDOOR_TEMP_COLOR_PREF = "Low Outdoor Temp Color";
    public static final String MEAN_OUTDOOR_TEMP_COLOR_PREF = "Mean Outdoor Temp Color";
    
    public static final String RECORD_HIGH_OUTDOOR_TEMP_COLOR_PREF = "Record High Outdoor Temp Color";
    public static final String RECORD_LOW_OUTDOOR_TEMP_COLOR_PREF = "Record Low Outdoor Temp Color";
    
    public static final String AVG_HIGH_OUTDOOR_TEMP_COLOR_PREF = "Average High Outdoor Temp Color";
    public static final String AVG_LOW_OUTDOOR_TEMP_COLOR_PREF = "Average Low Outdoor Temp Color";
    public static final String AVG_MEAN_OUTDOOR_TEMP_COLOR_PREF = "Average Mean Outdoor Temp Color";
   
    public static final String INDOOR_TEMP_COLOR_PREF = "Indoor Temp Color";
    public static final String HIGH_INDOOR_TEMP_COLOR_PREF = "High Indoor Temp Color";
    public static final String LOW_INDOOR_TEMP_COLOR_PREF = "Low Indoor Temp Color";
    public static final String MEAN_INDOOR_TEMP_COLOR_PREF = "Mean Indoor Temp Color";
    
    public static final String OUTDOOR_HUMIDITY_COLOR_PREF = "Outdoor Humidity Color";
    public static final String HIGH_OUTDOOR_HUMIDITY_COLOR_PREF = "High Outdoor Humidity Color";
    public static final String LOW_OUTDOOR_HUMIDITY_COLOR_PREF = "Low Outdoor Humidity Color";
    public static final String MEAN_OUTDOOR_HUMIDITY_COLOR_PREF = "Mean Outdoor Humidity Color";
   
    public static final String INDOOR_HUMIDITY_COLOR_PREF = "Indoor Humidity Color";
    public static final String HIGH_INDOOR_HUMIDITY_COLOR_PREF = "High Indoor Humidity Color";
    public static final String LOW_INDOOR_HUMIDITY_COLOR_PREF = "Low Indoor Humidity Color";
    public static final String MEAN_INDOOR_HUMIDITY_COLOR_PREF = "Mean Indoor Humidity Color";
    
    public static final String DEW_POINT_COLOR_PREF = "Dew Point Color";
    public static final String WIND_CHILL_COLOR_PREF = "Wind Chill Color";
    public static final String HEAT_INDEX_COLOR_PREF = "Heat Index Color";
    
    public static final String BARO_PRESSURE_COLOR_PREF = "Baro Pressure Color";
    public static final String HIGH_BARO_PRESSURE_COLOR_PREF = "High Baro Pressure Color";
    public static final String LOW_BARO_PRESSURE_COLOR_PREF = "Low Baro Pressure Color";
    public static final String MEAN_BARO_PRESSURE_COLOR_PREF = "Mean Baro Pressure Color";
    
    public static final String RAIN_COLOR_PREF = "Rain Color";
    public static final String RECORD_RAIN_COLOR_PREF = "Record Rain Color";
    public static final String AVG_RAIN_COLOR_PREF = "Avg Rain Color";
    
    public static final String WIND_SPEED_COLOR_PREF = "Wind Speed Color";
    public static final String AVG_WIND_SPEED_COLOR_PREF = "Avg Wind Speed Color";
    public static final String MAX_WIND_SPEED_COLOR_PREF = "Max Wind Speed Color";
    public static final String WIND_GUST_COLOR_PREF = "Wind Gust Speed Color";
    public static final String MAX_WIND_GUST_COLOR_PREF = "Max Wind Gust Color";
    
    private static final String DATABASE_NODE = "DB";
    private static final String DB_HOST_PREF = "DB Host";
    private static final String DB_PORT_PREF = "DB Port";
    //private static final String FORMATTING_NODE = "Formatting";

    private final Preferences rootPref = Preferences.userNodeForPackage(WeatherSense.class);
    private final Preferences unitNode = rootPref.node(UNITS_NODE);
    private final Preferences plotColorNode = rootPref.node(PLOT_COLOR_NODE);
    private final Preferences dbNode = rootPref.node(DATABASE_NODE);
    private static UserPreferences instance = null;

    private final ObjectProperty<Temperature.Unit> temperatureUnitProperty = new SimpleObjectProperty<>(Temperature.getDefaultUnit());
    private final ObjectProperty<Depth.Unit> rainfallUnitProperty = new SimpleObjectProperty<>(Depth.getDefaultUnit());
    private final ObjectProperty<Distance.Unit> elevationUnitProperty = new SimpleObjectProperty<>(Distance.getDefaultUnit());
    private final ObjectProperty<Pressure.Unit> pressureUnitProperty = new SimpleObjectProperty<>(Pressure.getDefaultUnit());
    private final ObjectProperty<Speed.Unit> speedUnitProperty = new SimpleObjectProperty<>(Speed.getDefaultUnit());

    private static final Logger logger = Logger.getLogger(UserPreferences.class.getName());

    static {
        instance = new UserPreferences();
    }

    /**
     * Constructor
     * This constructor will get all of the user preferences and set the default values in the
     * code
     */
    private UserPreferences() {
        loadTemperatureUnitsPref();
        loadRainfallUnitsPref();
        loadElevationUnitsPref();
        loadPressureUnitsPref();
        loadSpeedUnitsPref();
    }

    public static synchronized UserPreferences getInstance() {
        return instance;
    }

    public void sync() {
        try {
            unitNode.sync();
            plotColorNode.sync();
        }
        catch (BackingStoreException e) {
            ErrorDisplayer.getInstance().displayError("User preference storage error. Please contact support");
        }
    }

    public void setTemperatureUnit(Temperature.Unit unit) {
        temperatureUnitProperty.set(unit);
        Temperature.setDefaultUnit(unit);
        logger.log(Level.FINER,"Setting preference " + TEMP_UNITS_PREF + " to {0}", unit.name());
        unitNode.put(TEMP_UNITS_PREF, unit.name());
    }

    public Temperature.Unit getTemperatureUnit() {
        return temperatureUnitProperty.get();
    }

    public ObjectProperty<Temperature.Unit> temperatureUnitProperty() {
        return temperatureUnitProperty;
    }

    private void loadTemperatureUnitsPref() {
        //
        // If the preference is not legal then set the preference to the default
        // value of the Unit
        //
        try {
            String pref = unitNode.get(TEMP_UNITS_PREF, Temperature.getDefaultUnit().name());
            logger.log(Level.FINER, "Temperature unit prefereuce = {0}", pref);
            setTemperatureUnit(Temperature.Unit.valueOf(Temperature.Unit.class, pref));
        }
        catch (IllegalArgumentException e) {
            unitNode.put(TEMP_UNITS_PREF, Temperature.getDefaultUnit().name());
        }
    }

    public void setRainfallUnit(Depth.Unit unit) {
        rainfallUnitProperty.set(unit);
        Depth.setDefaultUnit(unit);
        logger.log(Level.FINER,"Setting preference " + RAINFALL_UNITS_PREF + " to {0}", unit.name());
        unitNode.put(RAINFALL_UNITS_PREF, unit.name());
    }

    public Depth.Unit getRainfallUnit() {
        return rainfallUnitProperty.get();
    }

    public ObjectProperty<Depth.Unit> rainfallUnitProperty() {
        return rainfallUnitProperty;
    }

    private void loadRainfallUnitsPref() {
        //
        // If the preference is not legal then set the preference to the default
        // value of the Unit
        //
        try {
            String pref = unitNode.get(RAINFALL_UNITS_PREF, Depth.getDefaultUnit().name());
            logger.log(Level.FINER, "Rainfall unit prefereuce = {0}", pref);
            setRainfallUnit(Depth.Unit.valueOf(Depth.Unit.class, pref));
        }
        catch (IllegalArgumentException e) {
            unitNode.put(RAINFALL_UNITS_PREF, Depth.getDefaultUnit().name());
        }
    }

    public void setElevationUnit(Distance.Unit unit) {
        elevationUnitProperty.set(unit);
        Distance.setDefaultUnit(unit);
        logger.log(Level.FINER,"Setting preference " + ELEVATION_UNITS_PREF + " to {0}", unit.name());
        unitNode.put(ELEVATION_UNITS_PREF, unit.name());
    }

    public Distance.Unit getElevationUnit() {
        return elevationUnitProperty.get();
    }

    public ObjectProperty<Distance.Unit> elevationUnitProperty() {
        return elevationUnitProperty;
    }

    private void loadElevationUnitsPref() {
        //
        // If the preference is not legal then set the preference to the default
        // value of the Unit
        //
        try {
            String pref = unitNode.get(ELEVATION_UNITS_PREF, Distance.getDefaultUnit().name());
            logger.log(Level.FINER, "Elevation unit prefereuce = {0}", pref);
            setElevationUnit(Distance.Unit.valueOf(Distance.Unit.class, pref));
        }
        catch (IllegalArgumentException e) {
            unitNode.put(ELEVATION_UNITS_PREF, Distance.getDefaultUnit().name());
        }
    }

    public void setPressureUnit(Pressure.Unit unit) {
        pressureUnitProperty.set(unit);
        Pressure.setDefaultUnit(unit);
        logger.log(Level.FINER,"Setting preference " + PRESSURE_UNITS_PREF + " to {0}", unit.name());
        unitNode.put(PRESSURE_UNITS_PREF, unit.name());
    }

    public Pressure.Unit getPressureUnit() {
        return pressureUnitProperty.get();
    }

    public ObjectProperty<Pressure.Unit> pressureUnitProperty() {
        return pressureUnitProperty;
    }

    private void loadPressureUnitsPref() {
        //
        // If the preference is not legal then set the preference to the default
        // value of the Unit
        //
        try {
            String pref = unitNode.get(PRESSURE_UNITS_PREF, Pressure.getDefaultUnit().name());
            logger.log(Level.FINER, "Pressure unit prefereuce = {0}", pref);
            setPressureUnit(Pressure.Unit.valueOf(Pressure.Unit.class, pref));
        }
        catch (IllegalArgumentException e) {
            unitNode.put(PRESSURE_UNITS_PREF, Pressure.getDefaultUnit().name());
        }
    }

    public void setSpeedUnit(Speed.Unit unit) {
        speedUnitProperty.set(unit);
        Speed.setDefaultUnit(unit);
        logger.log(Level.FINER,"Setting preference " + SPEED_UNITS_PREF + " to {0}", unit.name());
        unitNode.put(SPEED_UNITS_PREF, unit.name());
    }

    public Speed.Unit getSpeedUnit() {
        return speedUnitProperty.get();
    }

    public ObjectProperty<Speed.Unit> speedUnitProperty() {
        return speedUnitProperty;
    }

    private void loadSpeedUnitsPref() {
        //
        // If the preference is not legal then set the preference to the default
        // value of the Unit
        //
        try {
            String pref = unitNode.get(SPEED_UNITS_PREF, Speed.getDefaultUnit().name());
            logger.log(Level.FINER, "Speed unit prefereuce = {0}", pref);
            setSpeedUnit(Speed.Unit.valueOf(Speed.Unit.class, pref));
        }
        catch (IllegalArgumentException e) {
            unitNode.put(SPEED_UNITS_PREF, Speed.getDefaultUnit().name());
        }
    }

    public void addUnitListener(PreferenceChangeListener listener) {
        try {
            for (String child : unitNode.childrenNames()) {
                unitNode.node(child).addPreferenceChangeListener(listener);
            }
        }
        catch (BackingStoreException ex) {
            ErrorDisplayer.getInstance().displayError("User preference storage error. Please contact support");
        }
    }

    public void putColorPref(String preferenceName, Color color) {
        plotColorNode.putInt(preferenceName, color.getRGB());
    }
    
    public Color getColorPref(String preferenceName) {
        return new Color(plotColorNode.getInt(preferenceName, Color.RED.getRGB()));
    }
    
    public void putHighOutdoorTempColorPref(Color color) {
        plotColorNode.putInt(HIGH_OUTDOOR_TEMP_COLOR_PREF, color.getRGB());
    }

    public Color getHighOutdoorTempColorPref() {
        return new Color(plotColorNode.getInt(HIGH_OUTDOOR_TEMP_COLOR_PREF, Color.RED.getRGB()));
    }

    public void putLowOutdoorTempColorPref(Color color) {
        plotColorNode.putInt(LOW_OUTDOOR_TEMP_COLOR_PREF, color.getRGB());
    }

    public Color getLowOutdoorTempColorPref() {
        return new Color(plotColorNode.getInt(LOW_OUTDOOR_TEMP_COLOR_PREF, Color.BLUE.getRGB()));
    }

    public void putIndoorTempColorPref(Color color) {
        plotColorNode.putInt(INDOOR_TEMP_COLOR_PREF, color.getRGB());
    }

    public Color getIndoorTempColorPref() {
        return new Color(plotColorNode.getInt(INDOOR_TEMP_COLOR_PREF, Color.CYAN.getRGB()));
    }

    public void putOutdoorTempColorPref(Color color) {
        plotColorNode.putInt(OUTDOOR_TEMP_COLOR_PREF, color.getRGB());
    }

    public Color getOutdoorTempColorPref() {
        return new Color(plotColorNode.getInt(OUTDOOR_TEMP_COLOR_PREF, Color.MAGENTA.getRGB()));
    }

    public String getDbHostPref() {
        return dbNode.get(DB_HOST_PREF, null);
    }

    public void putDbHostPref(String pref) {
        dbNode.put(DB_HOST_PREF, pref);
    }

    public String getDbPortPref() {
        return dbNode.get(DB_PORT_PREF, DatabaseConstants.DATABASE_PORT);
    }

    public void putDbPortPref(String port) {
        dbNode.put(DB_PORT_PREF, port);
    }
}
