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
package com.bdb.weather.common.messages;

import com.bdb.weather.common.measurement.Speed;
import com.bdb.weather.common.measurement.Temperature;

/**
 *
 * @author Bruce
 */
public class MessageConstants {
    public static final Temperature.Unit TEMPERATURE_UNIT = Temperature.Unit.FAHRENHEIT;
    public static final Speed.Unit SPEED_UNIT = Speed.Unit.MILES_PER_HOUR;
    
    private MessageConstants() {
        
    }
}