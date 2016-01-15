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

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Distance;
import com.bdb.weather.common.measurement.Pressure;
import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;
import com.bdb.weather.display.ErrorDisplayer;
import com.bdb.weather.display.WeatherSense;

public class UnitsPreferences {
    private static final String UNITS_NODE = "Units";
    private static final String TEMP_UNITS_PREF = "Temperature Units";
    private static final String PRESSURE_UNITS_PREF = "Pressure Units";
    private static final String SPEED_UNITS_PREF = "Speed Units";
    private static final String RAINFALL_UNITS_PREF = "Rainfall Units";
    private static final String ELEVATION_UNITS_PREF = "Elevation Units";

    private final Preferences rootPref = Preferences.userNodeForPackage(WeatherSense.class);
    private final Preferences unitNode = rootPref.node(UNITS_NODE);
    private static UnitsPreferences instance = null;

    private final ObjectProperty<Temperature.Unit> temperatureUnitProperty = new SimpleObjectProperty<>(Temperature.getDefaultUnit());
    private final ObjectProperty<Depth.Unit> rainfallUnitProperty = new SimpleObjectProperty<>(Depth.getDefaultUnit());
    private final ObjectProperty<Distance.Unit> elevationUnitProperty = new SimpleObjectProperty<>(Distance.getDefaultUnit());
    private final ObjectProperty<Pressure.Unit> pressureUnitProperty = new SimpleObjectProperty<>(Pressure.getDefaultUnit());
    private final ObjectProperty<Speed.Unit> speedUnitProperty = new SimpleObjectProperty<>(Speed.getDefaultUnit());

    private static final Logger logger = Logger.getLogger(UnitsPreferences.class.getName());

    static {
        instance = new UnitsPreferences();
    }

    /**
     * Constructor
     * This constructor will get all of the user preferences and set the default values in the
     * code
     */
    private UnitsPreferences() {
        loadTemperatureUnitsPref();
        loadRainfallUnitsPref();
        loadElevationUnitsPref();
        loadPressureUnitsPref();
        loadSpeedUnitsPref();
    }

    public static synchronized UnitsPreferences getInstance() {
        return instance;
    }

    public void sync() {
        try {
            unitNode.sync();
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
}
