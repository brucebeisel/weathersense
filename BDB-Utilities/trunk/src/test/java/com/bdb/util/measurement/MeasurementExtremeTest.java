package com.bdb.util.measurement;

import java.time.LocalDateTime;

import junit.framework.Assert;
import org.junit.Test;

public class MeasurementExtremeTest {

    @Test
    public void testMeasurementExtremeExtremeType() {
        MeasurementExtreme<TestDistance> extreme = new MeasurementExtreme<>(MeasurementExtreme.ExtremeType.MINIMUM);
        Assert.assertEquals(MeasurementExtreme.ExtremeType.MINIMUM, extreme.getType());
        
        extreme = new MeasurementExtreme<>(MeasurementExtreme.ExtremeType.MAXIMUM);
        Assert.assertEquals(MeasurementExtreme.ExtremeType.MAXIMUM, extreme.getType());
        Assert.assertNull(extreme.getExtremeTime());
        Assert.assertNull(extreme.getExtreme());
    }

    @Test
    public void testMeasurementExtremeExtremeTypeMCalendar() {
        LocalDateTime time = LocalDateTime.now();
        TestDistance d = new TestDistance(10.0);
        MeasurementExtreme<TestDistance> extreme = new MeasurementExtreme<>(MeasurementExtreme.ExtremeType.MINIMUM, d, time); Assert.assertEquals(time, extreme.getExtremeTime());
        Assert.assertEquals(d, extreme.getExtreme());
    }

    @Test
    public void testApplyValue() {
        MeasurementExtreme<TestDistance> min = new MeasurementExtreme<>(MeasurementExtreme.ExtremeType.MINIMUM);
        TestDistance d = new TestDistance(5.0);
        LocalDateTime time = LocalDateTime.now();
        min.applyValue(d, time);
        d = new TestDistance(2.0);
        time = LocalDateTime.now();
        min.applyValue(d, time);
        Assert.assertEquals(d, min.getExtreme());
        Assert.assertEquals(time, min.getExtremeTime());
        TestDistance d2 = new TestDistance(10.0);
        min.applyValue(d2, LocalDateTime.now());
        Assert.assertEquals(d, min.getExtreme());
        Assert.assertEquals(time, min.getExtremeTime());
        
        MeasurementExtreme<TestDistance> max = new MeasurementExtreme<>(MeasurementExtreme.ExtremeType.MAXIMUM);
        d = new TestDistance(2.0);
        max.applyValue(d, LocalDateTime.now());
        d = new TestDistance(5.0);
        time = LocalDateTime.now();
        max.applyValue(d, time);
        Assert.assertEquals(d, max.getExtreme());
        Assert.assertEquals(time, max.getExtremeTime());
        d2 = new TestDistance(3.0);
        max.applyValue(d2, LocalDateTime.now());
        Assert.assertEquals(d, max.getExtreme());
        Assert.assertEquals(time, max.getExtremeTime());
        
        System.out.println(max);
    }
}
