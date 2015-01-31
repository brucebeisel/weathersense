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
package com.bdb.weather.common;

public enum Tendency {
    RISING_RAPIDLY("Rising Rapidly", 2),
    RISING_SLOWLY("Rising Slowly", 1),
    STEADY("Steady", 0),
    FALLING_SLOWLY("Falling Slowly", -1),
    FALLING_RAPIDLY("Falling Rapidly", -2);

    private final String string;
    private final int    value;

    Tendency(String s, int value) {
	this.string = s;
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return string;
    }
}
