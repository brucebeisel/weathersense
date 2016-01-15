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

import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.bdb.weather.common.db.DatabaseConstants;
import com.bdb.weather.display.ErrorDisplayer;
import com.bdb.weather.display.WeatherSense;

public class UserPreferences {
    private static final String DISPLAY_NODE = "Display";

    private static final String DATABASE_NODE = "DB";
    private static final String DB_HOST_PREF = "DB Host";
    private static final String DB_PORT_PREF = "DB Port";
    //private static final String FORMATTING_NODE = "Formatting";

    private final Preferences rootPref = Preferences.userNodeForPackage(WeatherSense.class);
    private final Preferences dbNode = rootPref.node(DATABASE_NODE);
    private static UserPreferences instance = null;

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
    }

    public static synchronized UserPreferences getInstance() {
        return instance;
    }

    public void sync() {
        try {
            dbNode.sync();
        }
        catch (BackingStoreException e) {
            ErrorDisplayer.getInstance().displayError("User preference storage error. Please contact support");
        }
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