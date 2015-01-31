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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ColorScheme implements Comparable<ColorScheme> {
    private final String           name;
    private final ArrayList<Color> colorList = new ArrayList<>();
    
    public ColorScheme(String name, Color... colors) {
        this(name, Arrays.asList(colors));
    }
    
    public ColorScheme(String name, List<Color> colors) {
        this.name = name;
        colorList.addAll(colors);
    }
    
    public String getName() {
        return name;
    }
    
    public int getSchemeColorCount() {
        return colorList.size();
    }
    
    public Color getColor(int index) {
        return colorList.get(index);
    }
    
    public List<Color> getColors() {
        return Collections.unmodifiableList(colorList);
    }

    @Override
    public int compareTo(ColorScheme o) {
        return name.compareTo(o.name);
    }
}
