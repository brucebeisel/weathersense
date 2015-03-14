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

import com.bdb.weather.display.axis.HumidityRangeAxis;
import com.bdb.weather.display.axis.PressureRangeAxis;
import com.bdb.weather.display.axis.RainRangeAxis;
import com.bdb.weather.display.axis.TemperatureRangeAxis;
import com.bdb.weather.display.axis.WindSpeedRangeAxis;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.axis.NumberAxis;

import com.bdb.weather.display.axis.SolarRadiationAxis;
import com.bdb.weather.display.axis.UvIndexAxis;

/**
 *
 * @author Bruce
 */
public enum MeasurementType {
    NONE(null),
    TEMPERATURE(new TemperatureRangeAxis()),
    HUMIDITY (new HumidityRangeAxis()),
    RAINFALL(new RainRangeAxis()),
    PRESSURE(new PressureRangeAxis()),
    WIND(new WindSpeedRangeAxis()),
    SOLAR_RADIATION(new SolarRadiationAxis()),
    UV_INDEX(new UvIndexAxis());
    
    private final NumberAxis axis;
    
    MeasurementType(NumberAxis axis) {
        this.axis = axis;
    }
    
    public NumberAxis createRangeAxis() {
        try {
            return (NumberAxis)axis.clone();
        }
        catch (CloneNotSupportedException ex) {
            Logger.getLogger(MeasurementType.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}