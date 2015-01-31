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
package com.bdb.util.measurement;

import java.text.NumberFormat;

/**
 * Interface used as a base class for all Measurement units.
 * 
 */
public interface Unit
{
    /**
     * Convert from the canonical unit to this unit. It is up to the implementing enum to
     * determine the storage unit and be sure that toUnit does not change the value if
     * the storage unit is specified.
     * 
     * @param value The value to convert
     * @return The converted value
     */
    double toUnit(double value);

    /**
     * Convert from this unit to the storage unit. It is up to the implementing enum to
     * determine the storage unit and be sure that toUnit does not change the value if
     * the storage unit is specified.
     * 
     * @param value The value to convert
     * @return The converted value
     */
    double fromUnit(double value);

    /**
     * Get the string representation of the unit, this is typically the unit abbreviation (e.g. F = Fahrenheit).
     * 
     * @return The string
     */
    @Override
    String toString();

    /**
     * Get the formatter for this unit.
     * 
     * @return The formatter
     */
    NumberFormat getFormatter();

    /**
     * Get the formatter for this unit with the unit string appended.
     * 
     * @return The formatter
     */
    NumberFormat getFormatterWithUnit();
}
