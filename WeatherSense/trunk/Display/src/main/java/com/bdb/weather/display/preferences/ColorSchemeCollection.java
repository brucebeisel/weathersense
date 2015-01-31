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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public final class ColorSchemeCollection {
    private static final Map<String,ColorScheme> schemes;
    
    static {
        schemes = new TreeMap<>();
        schemes.put("Scheme 41", new ColorScheme("Scheme 41", new Color(0x79BEDB), new Color(0x593E1A), new Color(0x6B78B4), new Color(0x266A2E)));
        schemes.put("Scheme 50", new ColorScheme("Scheme 50", new Color(0x005A04), new Color(0xCCFFBB), new Color(0x3A5F0B), new Color(0x005502)));
        schemes.put("Scheme 13", new ColorScheme("Scheme 13", new Color(0x443266), new Color(0xC3C3E5), new Color(0xF1F0FF), new Color(0x8C489F)));
        schemes.put("Scheme 33", new ColorScheme("Scheme 33", new Color(0x660033), new Color(0xE0E6FF), new Color(0x777B88), new Color(0x330033)));
        schemes.put("Scheme 52", new ColorScheme("Scheme 52", new Color(0x003366), new Color(0x3399FF), new Color(0xFFCC99), new Color(0xFF9966)));
        schemes.put("Scheme 56", new ColorScheme("Scheme 56", new Color(0x003333), new Color(0xCCCC99), new Color(0x666699), new Color(0x003366)));
        schemes.put("Scheme 58", new ColorScheme("Scheme 58", new Color(0x333333), new Color(0xFFCC00), new Color(0x669966), new Color(0x993366)));
        schemes.put("Scheme 77", new ColorScheme("Scheme 77", new Color(0x666633), new Color(0x999966), new Color(0xCC6666), new Color(0x663333)));
        schemes.put("Scheme 111", new ColorScheme("Scheme 111", new Color(0xCE0000), new Color(0x000063), new Color(0x5A79A5), new Color(0x9CAAC6), new Color(0xDEE7EF)));
        schemes.put("Scheme 114", new ColorScheme("Scheme 114", new Color(0x009A31), new Color(0x84CF96), new Color(0xC6E7CE), new Color(0xCEFFCE)));
        schemes.put("Scheme 117", new ColorScheme("Scheme 117", new Color(0x005B9A), new Color(0x0191C8), new Color(0x74C2E1), new Color(0x8C8984)));
        schemes.put("Scheme 134", new ColorScheme("Scheme 134", new Color(0x9DAF72), new Color(0x566047), new Color(0x562F32), new Color(0x462D44), new Color(0x859731), new Color(0x640E27)));
        schemes.put("Scheme 137", new ColorScheme("Scheme 137", new Color(0x6BCAE2), new Color(0x51A5BA), new Color(0x41924B), new Color(0xAFEAAA), new Color(0x87E293), new Color(0xFE8402)));
        schemes.put("Scheme 141", new ColorScheme("Scheme 141", new Color(0x4D8963), new Color(0x69A583), new Color(0xE1B378), new Color(0xE0CC97), new Color(0xEC799A), new Color(0x9F0251)));
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
