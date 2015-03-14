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
package com.bdb.weather.display.stripchart;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.weather.display.WeatherSense;

import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.Preferences;

import com.bdb.weather.display.ErrorDisplayer;

/**
 *
 * @author Bruce
 */
public class StripChartLayoutManager {
    private static final String STRIP_CHART_PREFERENCE_NODE = "StripChart";
    private final Preferences stripChartPref;
    private static StripChartLayoutManager instance = null;
    private static final Logger logger = Logger.getLogger(StripChartLayoutManager.class.getName());

    public static StripChartLayoutManager getInstance() {
        if (instance == null) {
            instance = new StripChartLayoutManager();
        }

        return instance;
    }

    private StripChartLayoutManager() {
        Preferences rootPref = Preferences.userNodeForPackage(WeatherSense.class);
        stripChartPref = rootPref.node(STRIP_CHART_PREFERENCE_NODE);
    }

    public Preferences getLayoutByName(String name) {
        Preferences layoutPref = null;

        if (name != null) {
            layoutPref = stripChartPref.node(name);
        }

        return layoutPref;
    }

    public String[] getSavedLayoutNames() {
        try {
            return stripChartPref.childrenNames();
        }
        catch (BackingStoreException ex) {
            ErrorDisplayer.getInstance().displayError("Failed to save strip chart layout");
            logger.log(Level.SEVERE, "Backing store failed for strip chart layouts", ex);
            return new String[0];
        }
    }

    public void registerForSavedLayoutChanges(NodeChangeListener listener) {
        stripChartPref.addNodeChangeListener(listener);
    }
}