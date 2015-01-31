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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.junit.Ignore;

@SuppressWarnings("serial")
@Ignore
class TestDistance extends Measurement {
    public enum Unit implements com.bdb.util.measurement.Unit {
        MILLIMETERS("mm", 1.0, ".0"),
        CENTIMETERS("cm", .1, ".00"),
        METERS("m", .001, ".000"),
        INCHES("in", 1.0 / 25.4, ".00"),
        FEET("ft", 1.0 / 25.4 / 12.0, ".000");

        private final String m_label;
        private final double m_scale;
        private final DecimalFormat m_formatter;
        private final DecimalFormat m_formatterWithUnit;

        /**
         * Constructor for Depth.Unit class
         * 
         * @param s
         *            Name of the unit. Good for output
         * @param scale
         *            Scale of this unit against the storage unit
         */
        Unit(String s, double scale, String formatString) {
            m_label = s;
            m_scale = scale;
            m_formatter = new DecimalFormat(formatString);
            m_formatterWithUnit = new DecimalFormat(formatString + " '"
                    + m_label + "'");
        }

        /**
         * Convert a value to this Unit
         * 
         * @param value
         *            Value to convert to this Unit
         * @return The converted value
         */
        @Override
        public final double toUnit(double value) {
            return value * m_scale;
        }

        @Override
        public final double fromUnit(double value) {
            return value / m_scale;
        }

        @Override
        public NumberFormat getFormatter() {
            return m_formatter;
        }

        @Override
        public NumberFormat getFormatterWithUnit() {
            return m_formatterWithUnit;
        }

        @Override
        public String toString() {
            return m_label;
        }
    }

    static {
        Measurement.setDefaultUnit(TestDistance.class, Unit.MILLIMETERS);
    }

    private static final int PRECISION = 2;

    public TestDistance(double value) {
        this(value, TestDistance.getDefaultUnit());
    }
    
    public TestDistance(double value, Unit unit) {
        super(value, unit, PRECISION, TestDistance::new);
    }

    public TestDistance(TestDistance d) {
        super(d);
    }

    public static void setDefaultUnit(Unit unit) {
        Measurement.setDefaultUnit(TestDistance.class, unit);
    }

    public static Unit getDefaultUnit() {
        return (TestDistance.Unit) Measurement.getDefaultUnit(TestDistance.class);
    }
}