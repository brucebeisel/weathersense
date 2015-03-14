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

public class Pressure extends Measurement {
    private static final long serialVersionUID = 828793598435665033L;

    public enum Unit implements com.bdb.util.measurement.Unit {
        KILO_PASCAL("kPa", 10.0, "#.0"),
        HECTO_PASCAL("hPa", 1.0, "#"),
        MILLIBAR("mb", 1.0, "#"), // Internal storage unit
        IN_HG("InHg", 33.86386, "#.00");
        private final String label;
        private final double scale;
        private final DecimalFormat formatter;
        private final DecimalFormat formatterWithUnit;

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

    public Pressure(double pressure) {
        this(pressure, getDefaultUnit());
    }

    public Pressure(double pressure, Unit unit) {
        super(pressure, unit, COMPARE_PRECISION, Pressure::new);
    }
    
    private Pressure() {
        this(0.0);
    }

    public Pressure adjustForAltitude(Distance altitude) {
        return adjustForAltitude(altitude, true);
    }

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

    public static Unit getDefaultUnit() {
        return (Unit)getDefaultUnit(Pressure.class);
    }

    public static void setDefaultUnit(Unit unit) {
        setDefaultUnit(Pressure.class, unit);
    }

    public static NumberFormat getDefaultFormatter() {
        return getDefaultUnit().getFormatter();
    }

    public static Pressure barometricOffset(Depth altitude) {
        return new Pressure(altitude.get(Depth.Unit.METERS) * ALT_METERS_PER_MILLIBAR);
    }
}