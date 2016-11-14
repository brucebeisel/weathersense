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
package com.bdb.weather.common.measurement;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.bdb.util.measurement.Measurement;

/**
 * Class that represents a temperature measurement.
 * 
 * @author bruce
 */
public class Temperature extends Measurement {
    private static final long serialVersionUID = 7511549339349527355L;
    /**
     * The hex representation of the degree symbol
     */
    public static final char DEGREE_SYMBOL = 0xB0;
    private static final double KELVIN_OFFSET = 273.0;
    private static final double FAHRENHEIT_SCALE = 1.8;
    private static final double FAHRENHEIT_OFFSET = 32.0;

    /**
     * The enumeration of possible temperature units
     */
    public enum Unit implements com.bdb.util.measurement.Unit {
        /**
         * Temperature unit of Celsius
         */
        CELSIUS("C", 1.0, 0.0, ".0"),
        /**
         * Temperature unit of Fahrenheit
         */
        FAHRENHEIT("F", FAHRENHEIT_SCALE, FAHRENHEIT_OFFSET, ".0"),
        /**
         * Temperature unit of Kelvin
         */
        KELVIN("K", 1.0, KELVIN_OFFSET, ".0");
        private final String label;
        private final double offset;
        private final double scale;
        private final NumberFormat formatter;
        private final NumberFormat formatterWithUnit;

        /**
         * Constructor.
         * 
         * @param s The label for this unit
         * @param scale The scale from the internal unit
         * @param offset The offset from the internal unit
         * @param formatString The format string
         */
        Unit(String s, double scale, double offset, String formatString) {
            label = s;
            this.scale = scale;
            this.offset = offset;
            formatter = new DecimalFormat(formatString);
            formatterWithUnit = new DecimalFormat(formatString + " '" + label + "'");
        }

        @Override
        public final double toUnit(double value) {
            return (value * scale) + offset;
        }

        @Override
        public final double fromUnit(double value) {
            return (value - offset) / scale;
        }

        @Override
        public final NumberFormat getFormatter() {
            return formatter;
        }

        @Override
        public final NumberFormat getFormatterWithUnit() {
            return formatterWithUnit;
        }

        @Override
        public final String toString() {
            return DEGREE_SYMBOL + label;
        }
    }

    static {
        Measurement.setDefaultUnit(Temperature.class, Unit.CELSIUS);
    }
    private static final int COMPARE_PRECISION = 1;

    /**
     * Constructor.
     * 
     * @param temperature The initial value
     */
    public Temperature(double temperature) {
        this(temperature, getDefaultUnit());
    }

    /**
     * Constructor.
     * 
     * @param temperature The initial value
     * @param unit The unit of the initial value
     */
    public Temperature(double temperature, Unit unit) {
        super(temperature, unit, COMPARE_PRECISION, Temperature::new);
    }
    
    private Temperature() {
        this(0.0);
    }

    /**
     * Get the default unit.
     * 
     * @return The unit
     */
    public static Unit getDefaultUnit() {
        return (Unit)getDefaultUnit(Temperature.class);
    }

    /**
     * Set the default unit.
     * 
     * @param unit The unit
     */
    public static void setDefaultUnit(Unit unit) {
        setDefaultUnit(Temperature.class, unit);
    }

    /**
     * Get the number formatter for the default unit.
     * 
     * @return The number formatter
     */
    public static NumberFormat getDefaultFormatter() {
        return getDefaultUnit().getFormatter();
    }
}