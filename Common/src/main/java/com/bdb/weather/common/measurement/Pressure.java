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
 * A class that represents a measurement of pressure.
 * 
 * @author bruce
 */
public class Pressure extends Measurement {
    private static final long serialVersionUID = 828793598435665033L;

    /**
     * The unit of pressure measurement
     */
    public enum Unit implements com.bdb.util.measurement.Unit {
        /**
         * Pressure measured in KiloPascals
         */
        KILO_PASCAL("kPa", 10.0, "#.0"),
        /**
         * Pressure measured in HectoPascals
         */
        HECTO_PASCAL("hPa", 1.0, "#"),
        /**
         * Pressure measured in Millibars
         */
        MILLIBAR("mb", 1.0, "#"), // Internal storage unit
        /**
         * Pressure measured in inches of mercury
         */
        IN_HG("InHg", 33.86386, "#.00");
        private final String label;
        private final double scale;
        private final DecimalFormat formatter;
        private final DecimalFormat formatterWithUnit;

        /**
         * Constructor.
         * 
         * @param s The name of the unit
         * @param scale The scale conversion
         * @param format The number format for this unit
         */
        Unit(String s, double scale, String format) {
            this.label = s;
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
    private static final double ALT_METERS_PER_MILLIBAR = 8.23;

    static {
        Measurement.setDefaultUnit(Pressure.class, Unit.MILLIBAR);
    }
    private static final int COMPARE_PRECISION = 1;

    /**
     * Constructor.
     * 
     * @param pressure The initial value
     */
    public Pressure(double pressure) {
        this(pressure, getDefaultUnit());
    }

    /**
     * Constructor.
     * 
     * @param pressure The initial value
     * @param unit The unit that the value is in
     */
    public Pressure(double pressure, Unit unit) {
        super(pressure, unit, COMPARE_PRECISION, Pressure::new);
    }
    
    private Pressure() {
        this(0.0);
    }

    /**
     * Adjust the pressure for the altitude.
     * 
     * @param altitude The altitude to adjust for
     * @return The adjusted pressure
     */
    public Pressure adjustForAltitude(Distance altitude) {
        return adjustForAltitude(altitude, true);
    }

    /**
     * Adjust the pressure for the altitude.
     * 
     * @param altitude The altitude to adjust for
     * @param add Whether to add or subtract the pressure
     * @return The adjusted pressure
     */
    public Pressure adjustForAltitude(Distance altitude, boolean add) {
        // 1.006 inHg per 1000 ft
        // 1 millibar per 8 meters
        // 1 millibar per 8.23 meters
        // 1 millibar per 10 meters
        double delta = altitude.get(Depth.Unit.METERS) / ALT_METERS_PER_MILLIBAR;
        if (add)
            return new Pressure(get(Pressure.Unit.MILLIBAR) + delta, Pressure.Unit.MILLIBAR);
        else
            return new Pressure(get(Pressure.Unit.MILLIBAR) - delta, Pressure.Unit.MILLIBAR);
    }

    /**
     * Get the default unit for Pressure.
     * 
     * @return The default unit
     */
    public static Unit getDefaultUnit() {
        return (Unit)getDefaultUnit(Pressure.class);
    }

    /**
     * Set the default unit for Pressure.
     * 
     * @param unit The default unit
     */
    public static void setDefaultUnit(Unit unit) {
        setDefaultUnit(Pressure.class, unit);
    }

    /**
     * Get the number formatter for the default unit.
     * 
     * @return The number formatter
     */
    public static NumberFormat getDefaultFormatter() {
        return getDefaultUnit().getFormatter();
    }

    /**
     * Get the barometric offset for the given altitude.
     * 
     * @param altitude The altitude
     * @return The barometric adjustment
     */
    public static Pressure barometricOffset(Depth altitude) {
        return new Pressure(altitude.get(Depth.Unit.METERS) * ALT_METERS_PER_MILLIBAR);
    }
}