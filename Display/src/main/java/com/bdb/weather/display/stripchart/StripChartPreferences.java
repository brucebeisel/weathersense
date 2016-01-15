/*
 * Copyright (C) 2015 bruce
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
package com.bdb.weather.display.stripchart;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import com.bdb.weather.display.WeatherSense;

/**
 *
 * @author bruce
 */
public class StripChartPreferences {
    private final Preferences rootPref = Preferences.userNodeForPackage(WeatherSense.class);
    private final Preferences stripChartPref = rootPref.node("Strip Chart");
    private final String STRIP_CHART_LEFT_AXIS_TYPE = "Strip Chart Left Axis";
    private final String STRIP_CHART_RIGHT_AXIS_TYPE = "Strip Chart Right Axis";
    private final ObservableList<StripChartEntry> list = new SimpleListProperty<>();
    private final static StripChartPreferences instance;

    static {
        instance = new StripChartPreferences();
    }

    public static StripChartPreferences getInstance() {
        return instance;
    }

    private StripChartPreferences() {
        try {
            for (String prefName : stripChartPref.childrenNames()) {
                Preferences pref = stripChartPref.node(prefName);
                StripChartEntry entry = new StripChartEntry(pref.name(),
                                                            pref.get(STRIP_CHART_LEFT_AXIS_TYPE, ""),
                                                            pref.get(STRIP_CHART_RIGHT_AXIS_TYPE, ""));
                list.add(entry);
            }
        } catch (BackingStoreException ex) {
            Logger.getLogger(StripChartPreferences.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addStripChart(StripChartEntry entry) {
        try {
            Preferences pref = stripChartPref.node(entry.getName());
            pref.put(STRIP_CHART_LEFT_AXIS_TYPE, entry.getLeftAxisType());
            pref.put(STRIP_CHART_RIGHT_AXIS_TYPE, entry.getRightAxisType());
            stripChartPref.sync();
        } catch (BackingStoreException ex) {
            Logger.getLogger(StripChartPreferences.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteStripChart(String name) {
        try {
            Preferences pref = stripChartPref.node(name);
            pref.removeNode();
            stripChartPref.sync();
        } catch (BackingStoreException ex) {
            Logger.getLogger(StripChartPreferences.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ObservableList<StripChartEntry> getSaveStripCharts() {
        return list;
    }

    public class StripChartEntry {
        private final StringProperty name = new SimpleStringProperty();
        private final StringProperty leftAxisType = new SimpleStringProperty();
        private final StringProperty rightAxisType = new SimpleStringProperty();

        public StripChartEntry(String name, String leftAxisType, String rightAxisType) {
            this.name.setValue(name);
            this.leftAxisType.setValue(leftAxisType);
            this.rightAxisType.setValue(rightAxisType);
        }

        public void setName(String value) {
            name.set(value);
        }

        public String getName() {
            return name.get();
        }

        public StringProperty nameProperty() {
            return name;
        }

        public void setLeftAxisType(String value) {
            leftAxisType.set(value);
        }

        public String getLeftAxisType() {
            return leftAxisType.get();
        }

        public StringProperty leftAxisTypeProperty() {
            return leftAxisType;
        }

        public void setRightAxisType(String value) {
            rightAxisType.set(value);
        }

        public String getRightAxisType() {
            return rightAxisType.get();
        }

        public StringProperty rightAxisTypeProperty() {
            return rightAxisType;
        }
    }
}