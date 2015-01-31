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

import java.time.Duration;
import java.time.LocalDateTime;

import junit.framework.Assert;
import org.junit.Test;

public class MeasurementStatisticsTest {

    @Test(expected=IllegalArgumentException.class)
    public void testMeasurementStatistics() {
        MeasurementStatistics<TestDistance> stats = new MeasurementStatistics<>(new TestDistance(0.0));
        Assert.assertNull(stats.getMaximumValue());
        Assert.assertNull(stats.getMaximumTime());
        Assert.assertNull(stats.getMinimumValue());
        Assert.assertNull(stats.getMinimumTime());
        Assert.assertEquals(Duration.ZERO, stats.getAverageDuration());
        new MeasurementStatistics<>(new TestDistance(1.0));
    }

    @Test
    public void testApplyValue() {
        MeasurementStatistics<TestDistance> stats = new MeasurementStatistics<>(new TestDistance(0.0));
        TestDistance d = new TestDistance(10.0);
        LocalDateTime time = LocalDateTime.now();
        Duration duration = Duration.ofSeconds(10);
        stats.applyValue(d, time, duration);
        Assert.assertEquals(d, stats.getMaximumValue());
        Assert.assertEquals(d, stats.getMinimumValue());
        Assert.assertEquals(d, stats.getAverage());
        Assert.assertEquals(duration, stats.getAverageDuration());
    }

    @Test
    public void testApplyStatistics() {
        MeasurementStatistics<TestDistance> stats = new MeasurementStatistics<>(new TestDistance(0.0));
        MeasurementStatistics<TestDistance> stats2 = new MeasurementStatistics<>(new TestDistance(0.0));
        TestDistance d = new TestDistance(10.0);
        LocalDateTime time = LocalDateTime.now();
        Duration duration = Duration.ofSeconds(10);
        stats.applyValue(d, time, duration);
        stats2.applyStatistics(stats);
        Assert.assertEquals(stats, stats2);
    }

    @Test
    public void testSetMinimum() {
        MeasurementStatistics<TestDistance> stats = new MeasurementStatistics<>(new TestDistance(0.0));
        TestDistance d = new TestDistance(10.0);
        LocalDateTime time = LocalDateTime.now();
        stats.setMinimum(d, time);
        Assert.assertEquals(d, stats.getMinimumValue());
        Assert.assertEquals(time, stats.getMinimumTime());
    }

    @Test
    public void testSetMaximum() {
        MeasurementStatistics<TestDistance> stats = new MeasurementStatistics<>(new TestDistance(0.0));
        TestDistance d = new TestDistance(10.0);
        LocalDateTime time = LocalDateTime.now();
        stats.setMaximum(d, time);
        Assert.assertEquals(d, stats.getMaximumValue());
        Assert.assertEquals(time, stats.getMaximumTime());
    }

    @Test
    public void testSetAverage() {
        MeasurementStatistics<TestDistance> stats = new MeasurementStatistics<>(new TestDistance(0.0));
        TestDistance d = new TestDistance(10.0);
        Duration duration = Duration.ofSeconds(100);
        stats.setAverage(d, duration);
        Assert.assertEquals(d, stats.getAverage());
        Assert.assertEquals(duration, stats.getAverageDuration());
    }
}