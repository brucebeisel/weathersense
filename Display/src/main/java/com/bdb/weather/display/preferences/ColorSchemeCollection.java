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
package com.bdb.weather.display.preferences;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javafx.scene.paint.Color;


public final class ColorSchemeCollection {
    private static final Map<String,ColorScheme> schemes;
    
    static {
        schemes = new TreeMap<>();
        schemes.put("Scheme 41", new ColorScheme("Scheme 41", Color.web("0x79BEDB"), Color.web("0x593E1A"), Color.web("0x6B78B4"), Color.web("0x266A2E")));
        schemes.put("Scheme 50", new ColorScheme("Scheme 50", Color.web("0x005A04"), Color.web("0xCCFFBB"), Color.web("0x3A5F0B"), Color.web("0x005502")));
        schemes.put("Scheme 13", new ColorScheme("Scheme 13", Color.web("0x443266"), Color.web("0xC3C3E5"), Color.web("0xF1F0FF"), Color.web("0x8C489F")));
        schemes.put("Scheme 33", new ColorScheme("Scheme 33", Color.web("0x660033"), Color.web("0xE0E6FF"), Color.web("0x777B88"), Color.web("0x330033")));
        schemes.put("Scheme 52", new ColorScheme("Scheme 52", Color.web("0x003366"), Color.web("0x3399FF"), Color.web("0xFFCC99"), Color.web("0xFF9966")));
        schemes.put("Scheme 56", new ColorScheme("Scheme 56", Color.web("0x003333"), Color.web("0xCCCC99"), Color.web("0x666699"), Color.web("0x003366")));
        schemes.put("Scheme 58", new ColorScheme("Scheme 58", Color.web("0x333333"), Color.web("0xFFCC00"), Color.web("0x669966"), Color.web("0x993366")));
        schemes.put("Scheme 77", new ColorScheme("Scheme 77", Color.web("0x666633"), Color.web("0x999966"), Color.web("0xCC6666"), Color.web("0x663333")));
        schemes.put("Scheme 111", new ColorScheme("Scheme 111", Color.web("0xCE0000"), Color.web("0x000063"), Color.web("0x5A79A5"), Color.web("0x9CAAC6"), Color.web("0xDEE7EF")));
        schemes.put("Scheme 114", new ColorScheme("Scheme 114", Color.web("0x009A31"), Color.web("0x84CF96"), Color.web("0xC6E7CE"), Color.web("0xCEFFCE")));
        schemes.put("Scheme 117", new ColorScheme("Scheme 117", Color.web("0x005B9A"), Color.web("0x0191C8"), Color.web("0x74C2E1"), Color.web("0x8C8984")));
        schemes.put("Scheme 134", new ColorScheme("Scheme 134", Color.web("0x9DAF72"), Color.web("0x566047"), Color.web("0x562F32"), Color.web("0x462D44"), Color.web("0x859731"), Color.web("0x640E27")));
        schemes.put("Scheme 137", new ColorScheme("Scheme 137", Color.web("0x6BCAE2"), Color.web("0x51A5BA"), Color.web("0x41924B"), Color.web("0xAFEAAA"), Color.web("0x87E293"), Color.web("0xFE8402")));
        schemes.put("Scheme 141", new ColorScheme("Scheme 141", Color.web("0x4D8963"), Color.web("0x69A583"), Color.web("0xE1B378"), Color.web("0xE0CC97"), Color.web("0xEC799A"), Color.web("0x9F0251")));
    }

    public static int getColorSchemeCount() {
        return schemes.size();
    }
    
    public static Set<String> getColorSchemeNames() {
        return schemes.keySet();
    }
    
    public static ColorScheme getScheme(String name) {
        return schemes.get(name);
    }
    
    public static boolean addCustomScheme(ColorScheme scheme) {
        if (schemes.containsKey(scheme.getName()))
            return false;
        
        schemes.put(scheme.getName(), scheme);
        return true;
    }
}
