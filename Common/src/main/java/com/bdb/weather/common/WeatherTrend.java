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
package com.bdb.weather.common;

/**
 *
 * @author bruce
 */
public enum WeatherTrend {

    /**
     *
     */
    FALLING_RAPIDLY("Falling Rapidly", "--"),

    /**
     *
     */
    FALLING_SLOWLY("Falling", "-"),

    /**
     *
     */
    STEADY("Steady", "="),

    /**
     *
     */
    RISING_SLOWLY("Rising", "+"),

    /**
     *
     */
    RISING_RAPIDLY("Rising Rapidly", "++");
    
    private final String symbol;
    private final String name;
    
    WeatherTrend(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }
    
    /**
     *
     * @return
     */
    public String getSymbol() {
        return symbol;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
