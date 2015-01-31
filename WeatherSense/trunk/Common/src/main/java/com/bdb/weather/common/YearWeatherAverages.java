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

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Bruce
 */
public class YearWeatherAverages implements WeatherAverages {
    private final WeatherAverage yearAverages;

    public YearWeatherAverages(WeatherAverage avg) {
        yearAverages = avg;
    }

    @Override
    public void putAverage(WeatherAverage avg, LocalDate c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public WeatherAverage getAverage(LocalDate c) {
        return yearAverages;
    }

    @Override
    public List<WeatherAverage> getAllAverages() {
        return null;
    }
}