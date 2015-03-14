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
 * Measurement class for speed.
 * 
 * @author Bruce
 *
 */
public class Speed extends Measurement {
    private static final long serialVersionUID = 3905322365691227024L;

    public enum Unit implements com.bdb.util.measurement.Unit {
        KM_PER_HOUR("KPH", 3.6, "0.0"),
        METERS_PER_SEC("MPS", 1.0, "0.00"), // Internal Storage Unit
        MILES_PER_HOUR("MPH", 2.23693, "0.0"),
        KNOTS("KTS", 1.94384, "0.0");

        private final String label;
        private final double scale;
        private final DecimalFormat formatter;
        private final DecimalFormat formatterWithUnit;

        Unit(String s, double scale, String format) {
            label = s;
            this.scale = scale;
            formatter = new DecimalFormat(format);
            formatterWithUnit = new DecimalFormat(format + " '" + label + "'");
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
        public final NumberFormat getFormatter() {
            return formatter;
        }

        @Override
        public final NumberFormat getFormatterWithUnit() {
            return formatterWithUnit;
        }

        @Override
        public final String toString() {
            return label;
        }
    }

    static {
        Measurement.setDefaultUnit(Speed.class, Unit.METERS_PER_SEC);
    }

    private static final int COMPARE_PRECISION = 2;

    /**
     * Constructor that uses the default unit.
     * 
     * @param speed The speed
     */
    public Speed(double speed) {
        this(speed, getDefaultUnit());
    }

    /**
     * Constructor.
     * 
     * @param speed The speed
     * @param unit The units of the speed
     */
    public Speed(double speed, Unit unit) {
        super(speed, unit, COMPARE_PRECISION, Speed::new);

        if (speed < 0.0)
            throw new IllegalArgumentException("Speed must be > 0");
    }
    
    private Speed() {
        this(0.0);
    }

    public static Unit getDefaultUnit() {
        return (Unit) getDefaultUnit(Speed.class);
    }

    public static void setDefaultUnit(Unit unit) {
        setDefaultUnit(Speed.class, unit);
    }

    public static NumberFormat getDefaultFormatter() {
        return getDefaultUnit().getFormatter();
    }
}