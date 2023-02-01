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
package com.bdb.weather.display;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

import com.bdb.weather.common.measurement.Heading;

/**
 * Formatter for compass headings.
 * 
 * @author Bruce
 *
 */
public class CompassHeadingFormat extends NumberFormat {
	private static final long serialVersionUID = 2805544232534153298L;

	/* (non-Javadoc)
     * @see java.text.NumberFormat#format(double, java.lang.StringBuffer, java.text.FieldPosition)
     */
    @Override
    public StringBuffer format(double heading, StringBuffer buf, FieldPosition pos) {
        Heading h = new Heading((float)heading);
        
        buf.append(h.getCompassLabel());
        
        return buf;
    }
    
    /* (non-Javadoc)
     * @see java.text.NumberFormat#format(long, java.lang.StringBuffer, java.text.FieldPosition)
     */
    @Override
    public StringBuffer format(long heading, StringBuffer buf, FieldPosition pos) {
        double value = (double)heading;
        return format(value, buf, pos);
    }

    /* (non-Javadoc)
     * @see java.text.NumberFormat#parse(java.lang.String, java.text.ParsePosition)
     */
    @Override
    public Number parse(String s, ParsePosition pos) {
        throw new UnsupportedOperationException("CompassHeadingFormat.parse() not supported");
    }
}
