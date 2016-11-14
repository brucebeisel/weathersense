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
 * An immutable Measurement class for humidity. This class only support relative humidity as a percentage.
 *
 * @author Bruce
 * @since 1.0
 */
public class SolarRadiation extends Measurement {
    /**
     * The unit of the SolarRadiation measurement
     *
     * @author Bruce
     * @since 1.0
     */
    public enum Unit implements com.bdb.util.measurement.Unit {
        /**
         * Solar radiation only support the unit of Watts per meter squared (W/m2)
         */
        WATTS_PER_METER_SQUARED;
        private static final String FORMAT_STRING = "#";
        private static final String UNIT_LABEL = "W/m2";
        private final NumberFormat formatter;
        private final NumberFormat formatterWithUnit;

        /**
         * Constructor.
         */
        private Unit() {
            formatter = new DecimalFormat(FORMAT_STRING);
            formatterWithUnit = new DecimalFormat(FORMAT_STRING + " '" + UNIT_LABEL + "'");
        }

        @Override
        public double fromUnit(double value) {
            return value;
        }

        @Override
        public String toString() {
            return UNIT_LABEL;
        }

        @Override
        public double toUnit(double value) {
            return value;
        }

        @Override
        public NumberFormat getFormatter() {
            return formatter;
        }

        @Override
        public NumberFormat getFormatterWithUnit() {
            return formatterWithUnit;
        }
    }

    /**
     * Initialize the default unit for the Humidity measurement.
     */
    static {
        Measurement.setDefaultUnit(SolarRadiation.class, SolarRadiation.Unit.WATTS_PER_METER_SQUARED);
    }

    private static final int COMPARE_PRECISION = 0;

    /**
     * Constructor.
     *
     * @param value The value
     * @throws IllegalArgumentException The argument is < 0 or > 100.
     */
    public SolarRadiation(double value) throws IllegalArgumentException {
        super(value, SolarRadiation.Unit.WATTS_PER_METER_SQUARED, COMPARE_PRECISION, SolarRadiation::new);
    }
    
    private SolarRadiation() {
        this(0.0);
    }

    /**
     * Get the current default unit for solar radiation.
     * 
     * @return The default unit
     */
    public static Unit getDefaultUnit() {
        return (Unit)getDefaultUnit(SolarRadiation.class);
    }

    /**
     * Set the default unit for solar radiation.
     * @param unit The default unit
     */
    public static void setDefaultUnit(Unit unit) {
        setDefaultUnit(SolarRadiation.class, unit);
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