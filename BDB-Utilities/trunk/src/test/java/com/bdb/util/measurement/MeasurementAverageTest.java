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
import java.time.Duration;

import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("serial")
class MeasurementTester extends Measurement {

    enum TestUnit implements Unit {
        UNIT;
        
        static {
            Measurement.setDefaultUnit(MeasurementTester.class, UNIT);
        }

        @Override
        public double fromUnit(double value) {
            return value;
        }

        @Override
        public NumberFormat getFormatter() {
            return new DecimalFormat("#");
        }

        @Override
        public NumberFormat getFormatterWithUnit() {
            return getFormatter();
        }

        @Override
        public double toUnit(double value) {
            return value;
        }
    }
    
    public MeasurementTester(double value) {
        super(value, MeasurementTester.TestUnit.UNIT, 5, MeasurementTester::new);
    }
}

public class MeasurementAverageTest {
    @Test
    public void constructor() {
        MeasurementAverage<MeasurementTester> avg = new MeasurementAverage<>(new MeasurementTester(10.0), Duration.ofSeconds(1));
        
        Assert.assertEquals(avg.getAverage(), new MeasurementTester(10.0));
    }
    
    @Test
    public void applyValue() {
        MeasurementAverage<MeasurementTester> avg = new MeasurementAverage<>(new MeasurementTester(10.0), Duration.ofSeconds(2));
        avg.applyValue(new MeasurementTester(20.0), Duration.ofSeconds(3));
        Assert.assertEquals(new MeasurementTester(16.0), avg.getAverage());
    }
}
