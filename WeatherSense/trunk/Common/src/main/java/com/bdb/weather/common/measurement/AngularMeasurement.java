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
 * Class for representing an Angular Measurement such as a latitude
 * 
 * @author Bruce
 *
 */
public class AngularMeasurement extends Measurement {
    private static final long serialVersionUID = 4670341447010985761L;

    /**
     * Units Enum of the AngularMeasurement class
     * 
     */
    public enum Unit implements com.bdb.util.measurement.Unit
    {
        DEGREES("Degrees", 1.0, "#.0000"),
        RADIANS("Radians", 360.0 / (2 * Math.PI), "#.0000");

        private final String              label;
        private final double              scale;
        private final DecimalFormat       formatter;
        private final DecimalFormat       formatterWithUnit;

        /**
         * Constructor.
         * 
         * @param label The label for this unit
         * @param scale The scale of this unit in relation to the default storage
         * @param format The formatter for this unit
         */
        Unit(String label, double scale, String format) {
            this.label = label;
            this.scale = scale;
            formatter = new DecimalFormat(format);
            formatterWithUnit = new DecimalFormat(format + " '" + label + "'");
        }

        @Override
        public final double toUnit(double value) {
            return value / scale;
        }

        @Override
        public final double fromUnit(double value) {
            return value * scale;
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
        Measurement.setDefaultUnit(AngularMeasurement.class, Unit.DEGREES);
    }

    private static final int COMPARE_PRECISION = 4;
      
    /**
     * Get the current default unit.
     * 
     * @return The current default unit for the AngularMeasurement
     */
    public static Unit getDefaultUnit() {
        return (Unit)getDefaultUnit(AngularMeasurement.class);
    }

    /**
     * Set the default unit for the Angular measurement
     * 
     * @param unit The new default unit
     */
    public static void setDefaultUnit(Unit unit) {
        setDefaultUnit(AngularMeasurement.class, unit);
    }

    /**
     * Constructor that uses the default unit
     * 
     * @param angle The value of this measurement
     */
    public AngularMeasurement(double angle) {
        this(angle, getDefaultUnit());
    }

    private AngularMeasurement() {
        this(0.0);
    }

    /**
     * Constructor that requires a unit parameter
     * 
     * @param angle The value of this angle
     * @param unit The units of the specified value
     */
    public AngularMeasurement(double angle, Unit unit) {
        super(angle, unit, COMPARE_PRECISION, AngularMeasurement::new);
    }
}
