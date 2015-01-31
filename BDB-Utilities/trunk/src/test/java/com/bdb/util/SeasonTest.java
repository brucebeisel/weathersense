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
package com.bdb.util;

import java.time.LocalDate;
import java.time.Month;

import junit.framework.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class SeasonTest {

    @Test
    public void testGetStartMonth() {
        assertEquals(Season.WINTER.getStartMonth(), Month.DECEMBER);
    }

    @Test
    public void testGetEndMonth() {
        assertEquals(Season.WINTER.getEndMonth(), Month.MARCH);
    }

    @Test
    public void testSeasonStart() {
        LocalDate c = LocalDate.of(2000, Month.JANUARY, 1);
        c = Season.WINTER.seasonStart(c);
        assertEquals(c.getMonth(), Month.DECEMBER);
        assertEquals(c.getDayOfMonth(), Season.SEASON_START_DAY_OF_MONTH);
        assertEquals(c.getYear(), 1999);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testSeasonStartException() {
        LocalDate c = LocalDate.of(2000, Month.JUNE, 1);
        Season.WINTER.seasonStart(c);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testSeasonEndException() {
        LocalDate c = LocalDate.of(2000, Month.JUNE, 1);
        Season.WINTER.seasonEnd(c);
    }

    @Test
    public void testSeasonEnd() {
        LocalDate c = LocalDate.of(1999, Month.DECEMBER, Season.SEASON_START_DAY_OF_MONTH);
        c = Season.WINTER.seasonEnd(c);
        assertEquals(c.getMonth(), Month.MARCH);
        assertEquals(c.getDayOfMonth(), Season.SEASON_START_DAY_OF_MONTH - 1);
        assertEquals(c.getYear(), 2000);
    }

    @Test
    public void testIsInSeason() {
        LocalDate c = LocalDate.of(2000, Month.MARCH, Season.SEASON_START_DAY_OF_MONTH);
        Assert.assertTrue(Season.SPRING.isInSeason(c));
        Assert.assertFalse(Season.WINTER.isInSeason(c));
        c = LocalDate.of(2000, Month.JANUARY, 1);
        Assert.assertTrue(Season.WINTER.isInSeason(c));
        c = LocalDate.of(2000, Month.DECEMBER, Season.SEASON_START_DAY_OF_MONTH);
        assertTrue(Season.WINTER.isInSeason(c));
    }

    @Test
    public void testWhichSeason() {
        LocalDate c = LocalDate.of(2000, Month.MARCH, Season.SEASON_START_DAY_OF_MONTH);
        Season s = Season.whichSeason(c);
        assertEquals(Season.SPRING, s);
    }
    
    @Test
    public void testPreviousSeason() {
        LocalDate c = LocalDate.of(2000, Month.MARCH, Season.SEASON_START_DAY_OF_MONTH);
        Season s = Season.whichSeason(c);
        assertEquals(Season.SPRING, s);
        Season previous = s.previousSeason(c);
        assertEquals(Season.WINTER, previous);
        c = c.minusDays(1);
        c = previous.seasonStart(c);
        previous = s.previousSeason(c);
        assertEquals(Season.FALL, previous);
        c = c.minusDays(1);
        c = previous.seasonStart(c);
        previous = s.previousSeason(c);
        assertEquals(Season.SUMMER, previous);
        c = c.minusDays(1);
        c = previous.seasonStart(c);
        previous = s.previousSeason(c);
        assertEquals(Season.SPRING, previous);
    }

    @Test
    public void testNextSeason() {
        LocalDate c = LocalDate.of(2000, Month.MARCH, Season.SEASON_START_DAY_OF_MONTH);
        Season s = Season.whichSeason(c);
        c = s.seasonEnd(c);
        assertEquals(Season.SPRING, s);
        Season next = s.nextSeason(c);
        assertEquals(Season.SUMMER, next);
        c = c.plusDays(1);
        c = next.seasonEnd(c);
        next = s.nextSeason(c);
        assertEquals(Season.FALL, next);
        c = c.plusDays(1);
        c = next.seasonEnd(c);
        next = s.nextSeason(c);
        assertEquals(Season.WINTER, next);
        c = c.plusDays(1);
        c = next.seasonEnd(c);
        next = s.nextSeason(c);
        assertEquals(Season.SPRING, next);
    }
}
