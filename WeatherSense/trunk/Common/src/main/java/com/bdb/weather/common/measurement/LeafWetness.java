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
public class LeafWetness extends Measurement {
    private static final long serialVersionUID = 1;
    public static final LeafWetness MAX_LEAF_WETNESS = new LeafWetness(15);
    public static final LeafWetness MIN_LEAF_WETNESS = new LeafWetness(0);

    /**
     * The unit of the Humidity measurement
     *
     * @author Bruce
     * @since 1.0
     */
    public enum Unit implements com.bdb.util.measurement.Unit {
        WETNESS;
        private static final String FORMAT_STRING = "0";
        private static final String UNIT_LABEL = "";
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
        Measurement.setDefaultUnit(LeafWetness.class, LeafWetness.Unit.WETNESS);
    }

    private static final int COMPARE_PRECISION = 0;

    /**
     * Constructor.
     *
     * @param value The value
     * @throws IllegalArgumentException The argument is < 0 or > 100.
     */
    public LeafWetness(int value) throws IllegalArgumentException {
        super(value, LeafWetness.Unit.WETNESS, COMPARE_PRECISION, LeafWetness::new);

        if (value < 0 || value > 15)
            throw new IllegalArgumentException("Out of range Leaf Wetness (" + value + "). Valid range 0 through 15");
    }

    public LeafWetness(double value) {
        this((int)value);
    }
    
    private LeafWetness() {
        this(0);
    }
}
