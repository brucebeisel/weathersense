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
