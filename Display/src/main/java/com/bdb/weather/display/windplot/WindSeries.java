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

import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;

/**
 *
 * @author Bruce
 */
@SuppressWarnings("serial")
public class WindSeries extends TimeSeries {
    /**
     * Creates a new (empty) time series.  By default, a daily time series is
     * created.  Use one of the other constructors if you require a different
     * time period.
     *
     * @param name  the series name (<code>null</code> not permitted).
     */
    public WindSeries(Comparable<String> name) {
        this(name, DEFAULT_DOMAIN_DESCRIPTION, DEFAULT_RANGE_DESCRIPTION);
    }

    /**
     * Creates a new time series that contains no data.
     * <P>
     * Descriptions can be specified for the domain and range.  One situation
     * where this is helpful is when generating a chart for the time series -
     * axis labels can be taken from the domain and range description.
     *
     * @param name  the name of the series (<code>null</code> not permitted).
     * @param domain  the domain description (<code>null</code> permitted).
     * @param range  the range description (<code>null</code> permitted).
     *
     * @since 1.0.13
     */
    public WindSeries(Comparable<String> name, String domain, String range) {
        super(name, domain, range);
    }
    
    public WindSeriesDataItem getWindDataItem(int itemIndex) {
        TimeSeriesDataItem item = getDataItem(itemIndex);
        if (item instanceof WindSeriesDataItem)
            return (WindSeriesDataItem)item;
        else
            return null;
    }
}
