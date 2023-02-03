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
 * Class that stores a heading measurement.
 * 
 * @author bruce
 */
public class Heading extends Measurement {
    private static final long serialVersionUID = 2223252001788004134L;

    /**
     * Units of a heading
     */
    public enum Unit implements com.bdb.util.measurement.Unit {
        /**
         * Only unit supported is degrees
         */
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

    /**
     * A compass point (e.g. N, NW, SW, etc).
     */
    public static class CompassPoint {
        /**
         * The minimum heading of the compass point
         */
        public Heading min;
        /**
         * The maximum heading of the compass point
         */
        public Heading max;
        /**
         * The label of the compass point
         */
        public String label;

        /**
         * Constructor.
         * 
         * @param min The minimum heading of the compass point
         * @param max The maximum heading of the compass point
         * @param label The label for this compass point
         */
        public CompassPoint(Heading min, Heading max, String label) {
            this.min = min;
            this.max = max;
            this.label = label;
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
    };

    /**
     * The mode of the comparison
     */
    public enum CompareMode {
        /**
         *  >= low and <= high
         */
        INCLUSIVE,
        /**
         *  >  low and <  high
         */
        NON_INCLUSIVE,
        /**
         *  >= low and <  high
         */
        INCLUSIVE_NON_INCLUSIVE,
        /**
         *  >  low and <= high
         */
        NON_INCLUSIVE_INCLUSIVE 
    };

    static {
        Measurement.setDefaultUnit(Heading.class, Unit.DEGREES);
    }

    private static final int COMPARE_PRECISION = 1;

    /**
     * Constructor.
     * 
     * @param value The heading value
     */
    public Heading(double value) {
        super(value, Unit.DEGREES, COMPARE_PRECISION, Heading::new);

        if (value < 0.0)
            throw new IllegalArgumentException("Heading value out of range (" + value + "). Valid range 0.0 to 360.0 (360 is not valid)");

        if (value >= 360.0)
            value %= 360.0;
    }
    
    public Heading(int value) {
    	this((double)value);
    }
    
    private Heading() {
        this(0.0);
    }

    /**
     * Add two headings.
     * 
     * @param value The value to add to this heading
     * @return The sum of the two headings
     */
    public Heading add(double value) {
        double heading = this.get() + value;

        while (heading >= 360.0f)
            heading -= 360.0f;

        while (heading < 0.0f)
            heading += 360.0f;

        return new Heading(heading);
    }

    /**
     * Calculate the center of a wind direction slice.
     * 
     * @param sliceIndex The heading index
     * @param numSlices The number of slices
     * @return The centerline of the slice
     */
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

    /**
     * Add two headings.
     * 
     * @param heading The heading to add to this heading
     * @return The resulting sum
     */
    public Heading add(Heading heading) {
        return add(heading.get());
    }

    /**
     * Subtract two headings.
     * 
     * @param value The heading to subtract from this heading
     * @return The resulting difference
     */
    public Heading subtract(double value) {
        return add(-(value));
    }

    /**
     * Subtract two headings.
     * 
     * @param heading The heading to subtract from this heading
     * @return The resulting difference
     */
    public Heading subtract(Heading heading) {
        return subtract(heading.get());
    }

    /**
     * Whether this heading is between two headings.
     * 
     * @param low The lower bounds of the comparison
     * @param high The higher bounds of the comparison
     * @param mode The type of comparison
     * @return True of this heading is between the two headings
     */
    public boolean isBetween(Heading low, Heading high, CompareMode mode) {
        return isBetween(low.get(), high.get(), mode);
    }

    /**
     * Whether this heading is within an arc.
     * 
     * @param center The center of the arc
     * @param arcLength The length of the arc
     * @param mode The type of comparison
     * @return True of this heading is within this arc
     */
    public boolean isBetween(Heading center, double arcLength, CompareMode mode) {
        double low = center.get() - (arcLength / 2);

        if (low < 0.0f)
            low += 360f;

        double high = center.get() + (arcLength / 2);

        if (high >= 360.0f)
            high -= 360f;

        return isBetween(low, high, mode);

    }

    /**
     * Whether this heading is between two headings.
     * 
     * @param low The lower bounds of the comparison
     * @param high The higher bounds of the comparison
     * @param mode The type of comparison
     * @return True of this heading is between the two headings
     */
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

    /**
     * Get the label for a compass point.
     * 
     * @return The compass point label
     */
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