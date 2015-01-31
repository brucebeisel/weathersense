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
package com.bdb.weather.common;

import java.time.Duration;
import java.time.LocalDate;

import com.bdb.weather.common.measurement.Heading;
import com.bdb.weather.common.measurement.Speed;

import org.junit.Test;

public class WindRoseDataTest {

    @Test
    public void testMeasurement() {
        Wind values[] = {
                new Wind(new Speed(3.200000048),     new Heading(202.5f)),
                new Wind(new Speed(1.0),       new Heading(247.5f)),
                new Wind(new Speed(12.0),       new Heading(225.0f)),
                new Wind(new Speed(2.099999905),     new Heading(225.0f)),
                new Wind(new Speed(0.0),       new Heading(225.0f)),
                new Wind(new Speed(1.399999976),     new Heading(225.0f)),
                new Wind(new Speed(1.399999976),     new Heading(225.0f)),
                new Wind(new Speed(1.399999976),     new Heading(247.5f)),
                new Wind(new Speed(0.200000003),     new Heading(337.5f)),
                new Wind(new Speed(1.899999976),     new Heading(45.0f)),
                new Wind(new Speed(0.0),       new Heading(135.0f)),
                new Wind(new Speed(1.0),       new Heading(157.5f)),
                new Wind(new Speed(1.200000048),     new Heading(180.0f)),
                new Wind(new Speed(2.5),     new Heading(247.5f)),
                new Wind(new Speed(1.100000024),     new Heading(292.5f)),
                new Wind(new Speed(1.399999976),     new Heading(135.0f)),
                new Wind(new Speed(2.0),       new Heading(135.0f)),
                new Wind(new Speed(0.5),     new Heading(157.5f)),
                new Wind(new Speed(2.200000048),     new Heading(157.5f)),
                new Wind(new Speed(0.0),       new Heading(202.5f)),
                new Wind(new Speed(0.0),       new Heading(225f)),
                new Wind(new Speed(0.0),       new Heading(112.5f)),
                new Wind(new Speed(0.0),       new Heading(112.5f)),
                new Wind(new Speed(1.200000048),     new Heading(270f)),
                new Wind(new Speed(1.0),       new Heading(292.5f))
        };

        WindRoseData wrd = new WindRoseData(LocalDate.now(), new WindParameters(new Speed(10.0), 2, 16));
        
        for (Wind wind : values) {
            wrd.measurement(Duration.ofSeconds(300), wind);
            System.out.println(wind);
            System.out.println(wrd);
        }
    }
}
