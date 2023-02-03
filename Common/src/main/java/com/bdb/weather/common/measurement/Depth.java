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
 * Class the represents a value of Depth. Different units can be used. The storage unit is millimeters
 */
public class Depth extends Measurement {
    private static final long serialVersionUID = 8677600886116392761L;

    /**
     * Enum for the depth units.
     */
    public enum Unit implements com.bdb.util.measurement.Unit {
        /**
         * Depth in millimeters
         */
        MILLIMETERS("mm", 1.0, ".0"), // Internal storage unit
        /**
         * Depth in centimeters
         */
        CENTIMETERS("cm", .1, ".00"),
        /**
         * Depth in meters
         */
        METERS("m", .001, ".000"),
        /**
         * Depth in kilometers
         */
        KILOMETERS("km", .000001, ".0"),
        /**
         * Depth in inches
         */
        INCHES("in", 1.0 / 25.4, ".00"),
        /**
         * Depth in feet
         */
        FEET("ft", 1.0 / 25.4 / 12.0, ".000");
        private final String label;
        private final double scale;
        private final DecimalFormat formatter;
        private final DecimalFormat formatterWithUnit;

        /**
         * Constructor for Depth.Unit class
         *
         * @param s Name of the unit. Good for output
         * @param scale Scale of this unit against the storage unit
         */
        Unit(String s, double scale, String formatString) {
            this.label = s;
            this.scale = scale;
            this.formatter = new DecimalFormat(formatString);
            this.formatterWithUnit = new DecimalFormat(formatString + " '" + label + "'");
        }

        @Override
        public final double toUnit(double value) {
            return value * scale;
        }

        @Override
        public final double fromUnit(double value) {
            return value / scale;
        }

        @Override
        public NumberFormat getFormatter() {
            return formatter;
        }

        @Override
        public NumberFormat getFormatterWithUnit() {
            return formatterWithUnit;
        }

        @Override
        public final String toString() {
            return label;
        }
    }

    static {
        Measurement.setDefaultUnit(Depth.class, Unit.MILLIMETERS);
    }

    private static final int COMPARE_PRECISION = 1;

    /**
     * Construct a Depth measurement object using the default units.
     * @param depth The value to initial the depth measurement with
     */
    public Depth(double depth) {
        this(depth, getDefaultUnit());
    }

    public Depth(int depth) {
        this(depth, getDefaultUnit());
    }

    /**
     * Construct a Depth measurement object using the specified unit.
     * 
     * @param depth The value for the measurement
     * @param unit The unit of the value
     */
    public Depth(double depth, Unit unit) {
        super(depth, unit, COMPARE_PRECISION, Depth::new);
    }
    
    @SuppressWarnings("unused")
	private Depth() {
        this(0.0);
    }

    /**
     * Get the current default unit for the Depth measurement objects.
     * 
     * @return The default unit
     */
    public static Unit getDefaultUnit() {
        return (Unit) getDefaultUnit(Depth.class);
    }

    /**
     * Set the default unit for Depth measurement objects.
     * @param unit 
     */
    public static void setDefaultUnit(Unit unit) {
        setDefaultUnit(Depth.class, unit);
    }

    /**
     * Get a formatter for the current default unit of the Depth objects.
     * 
     * @return The number formatter
     */
    public static NumberFormat getDefaultFormatter() {
        return getDefaultUnit().getFormatter();
    }
}
