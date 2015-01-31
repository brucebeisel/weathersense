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
package com.bdb.weather.display.windplot;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeriesDataItem;

/**
 *
 * @author Bruce
 * @param <E> The time period for this wind plot
 */
@SuppressWarnings("serial")
public class WindSeriesDataItem extends TimeSeriesDataItem {
    /** The wind direction associated with the time period */
    private Number windDirection;

    /**
     * Constructs a new data item that associates a value with a time period.
     *
     * @param period  the time period (<code>null</code> not permitted).
     * @param windSpeed  The wind speed (<code>null</code> permitted).
     * @param windDirection  The wind direction (<code>null</code> permitted).
     */
    public WindSeriesDataItem(RegularTimePeriod period, Number windSpeed, Number windDirection) {
        super(period, windSpeed);
        this.windDirection = windDirection;
    }

    /**
     * Constructs a new data item that associates a value with a time period.
     *
     * @param period  the time period (<code>null</code> not permitted).
     * @param windSpeed  The wind speed (<code>null</code> permitted).
     * @param windDirection  The wind direction (<code>null</code> permitted).
     */
    public WindSeriesDataItem(RegularTimePeriod period, double windSpeed, float windDirection) {
        this(period, Double.valueOf(windSpeed), Float.valueOf(windDirection));
    }
    
    /**
     * Returns the wind speed.
     *
     * @return The speed of the wind (<code>null</code> possible).
     */
    public Number getWindSpeed() {
        return getValue();
    }
    
    /**
     * Returns the wind direction.
     *
     * @return The direction of the wind (<code>null</code> possible).
     */
    public Number getWindDirection() {
        return this.windDirection;
    }

    /**
     * Sets the wind speed for this data item.
     *
     * @param windSpeed  the wind speed (<code>null</code> permitted).
     */
    public void setWindSpeed(Number windSpeed) {
        setValue(windSpeed);
    }
    
    /**
     * Sets the wind direction for this data item.
     *
     * @param windDirection  the direction (<code>null</code> permitted).
     */
    public void setWindDirection(Number windDirection) {
        this.windDirection = windDirection;
    }

    /**
     * Tests this object for equality with an arbitrary object.
     *
     * @param o  the other object.
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (!(o instanceof WindSeriesDataItem)) {
            return false;
        }
 
        if (!super.equals(o))
            return false;
        
        WindSeriesDataItem windSeriesDataItem = (WindSeriesDataItem)o;
 
        if (windDirection == windSeriesDataItem.windDirection)
            return true;
        
        if (windDirection == null || windSeriesDataItem.windDirection == null)
            return false;
        
        return windDirection.equals(windSeriesDataItem.windDirection);
    }

    /**
     * Returns a hash code.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 53 * result + (windDirection != null ? windDirection.hashCode() : 0);
        return result;
    }
}
