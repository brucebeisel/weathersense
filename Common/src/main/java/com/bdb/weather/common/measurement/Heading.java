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

public class Heading extends Measurement {
    private static final long serialVersionUID = 2223252001788004134L;

    public enum Unit implements com.bdb.util.measurement.Unit {
        DEGREES;
        private static final DecimalFormat formatter = new DecimalFormat("000");
        private static final DecimalFormat formatterWithUnit = new DecimalFormat("000");

        @Override
        public final double toUnit(double value) {
            return value;
        }

        @Override
        public final double fromUnit(double value) {
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

        @Override
        public final String toString() {
            return "Heading";
        }
    };

    public static class CompassPoint {
        public Heading min;
        public Heading max;
        public String label;

        public CompassPoint(Heading _min, Heading _max, String _label) {
            min = _min;
            max = _max;
            label = _label;
        }
    };
    private final static int NUM_COMPASS_PTS = 16;
    private final static float COMPASS_PT_INT = 360f / NUM_COMPASS_PTS;
    private static CompassPoint COMPASS_LABEL[] = new CompassPoint[NUM_COMPASS_PTS];
    private static final String LABELS[] = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};

    static {
        COMPASS_LABEL[0] = new CompassPoint(new Heading(360.0f - (COMPASS_PT_INT / 2f)), new Heading(COMPASS_PT_INT), LABELS[0]);

        for (int i = 1; i < NUM_COMPASS_PTS; i++)
            COMPASS_LABEL[i] = new CompassPoint(new Heading((COMPASS_PT_INT * i) - (COMPASS_PT_INT / 2f)),
                                                new Heading((COMPASS_PT_INT * i) + (COMPASS_PT_INT / 2f)), LABELS[i]);
    }

    ;

    public enum CompareMode {
        INCLUSIVE, // >= low and <= high
        NON_INCLUSIVE, // >  low and <  high
        INCLUSIVE_NON_INCLUSIVE, // >= low and <  high
        NON_INCLUSIVE_INCLUSIVE    	// >  low and <= high
    };

    static {
        Measurement.setDefaultUnit(Heading.class, Unit.DEGREES);
    }

    private static final int COMPARE_PRECISION = 1;

    public Heading(double value) {
        super(value, Unit.DEGREES, COMPARE_PRECISION, Heading::new);

        if (value < 0.0)
            throw new IllegalArgumentException("Heading value out of range (" + value + "). Valid range 0.0 to 360.0 (360 is not valid)");

        if (value >= 360.0)
            value %= 360.0;
    }
    
    private Heading() {
        this(0.0);
    }

    public Heading add(double value) {
        double heading = this.get() + value;

        while (heading >= 360.0f)
            heading -= 360.0f;

        while (heading < 0.0f)
            heading += 360.0f;

        return new Heading(heading);
    }

    public static Heading headingForSlice(int sliceIndex, int numSlices) {
        float degreesPerSlice = 360.0F / numSlices;
        Heading heading = new Heading(degreesPerSlice * (double)sliceIndex);
        return heading;
    }

    @Override
    public int hashCode() {
        long hash = 5;
        hash = 23 * hash + Double.doubleToLongBits(this.get());
        return (int)hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        final Heading other = (Heading)obj;
        return Math.round(this.get() * 10.0) == Math.round(other.get() * 10.0);
    }

    public Heading add(Heading heading) {
        return add(heading.get());
    }

    public Heading subtract(double value) {
        return add(-(value));
    }

    public Heading subtract(Heading heading) {
        return subtract(heading.get());
    }

    public boolean isBetween(Heading low, Heading high, CompareMode mode) {
        return isBetween(low.get(), high.get(), mode);
    }

    public boolean isBetween(Heading center, double arcLength, CompareMode mode) {
        double low = center.get() - (arcLength / 2);

        if (low < 0.0f)
            low += 360f;

        double high = center.get() + (arcLength / 2);

        if (high >= 360.0f)
            high -= 360f;

        return isBetween(low, high, mode);

    }

    public boolean isBetween(double low, double high, CompareMode mode) {
        if (low < 0f || low >= 360f || high < 0f || high >= 360f)
            throw new IllegalArgumentException("Heading value out of range (" + low + "," + high + "). Valid range 0.0 to 360.0 (360 is not valid)");

        //
        // If the lowest is larger than the highest then the slice cuts across
        // north. Normalize the values.
        //
        double hvalue = this.get();

        if (low > high) {
            if (hvalue < high)
                hvalue += 360f;

            high += 360.0;
        }

        boolean result = true;

        switch (mode) {
            case INCLUSIVE:
                result = hvalue >= low && hvalue <= high;
                break;

            case NON_INCLUSIVE:
                result = hvalue > low && hvalue < high;
                break;

            case INCLUSIVE_NON_INCLUSIVE:
                result = hvalue >= low && hvalue < high;
                break;

            case NON_INCLUSIVE_INCLUSIVE:
                result = hvalue > low && hvalue <= high;
                break;
        }

        return result;
    }

    public String getCompassLabel() {
        String label = "UNK";

        for (CompassPoint COMPASS_LABEL1 : COMPASS_LABEL)
            if (isBetween(COMPASS_LABEL1.min, COMPASS_LABEL1.max, CompareMode.NON_INCLUSIVE_INCLUSIVE))
                return COMPASS_LABEL1.label;

        return label;
    }

    @Override
    public String toString() {
        return String.format("%.0f", get());
    }
}