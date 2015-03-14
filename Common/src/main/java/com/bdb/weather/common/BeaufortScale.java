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

import com.bdb.weather.common.measurement.Speed;

/*
0    Less than 1     Calm    Sea surface smooth and mirror-like      Calm, smoke rises vertically
1       1-3     Light Air       Scaly ripples, no foam crests   Smoke drift indicates wind direction, still wind vanes
2       4-6     Light Breeze    Small wavelets, crests glassy, no breaking      Wind felt on face, leaves rustle, vanes begin to move
3       7-10    Gentle Breeze   Large wavelets, crests begin to break, scattered whitecaps      Leaves and small twigs constantly moving, light flags extended
4       11-16   Moderate Breeze         Small waves 1-4 ft. becoming longer, numerous whitecaps         Dust, leaves, and loose paper lifted, small tree branches move
5       17-21   Fresh Breeze    Moderate waves 4-8 ft taking longer form, many whitecaps, some spray    Small trees in leaf begin to sway
6       22-27   Strong Breeze   Larger waves 8-13 ft, whitecaps common, more spray      Larger tree branches moving, whistling in wires
7       28-33   Near Gale       Sea heaps up, waves 13-20 ft, white foam streaks off breakers   Whole trees moving, resistance felt walking against wind
8       34-40   Gale    Moderately high (13-20 ft) waves of greater length, edges of crests begin to break into spindrift, foam blown in streaks        Whole trees in motion, resistance felt walking against wind
9       41-47   Strong Gale     High waves (20 ft), sea begins to roll, dense streaks of foam, spray may reduce visibility      Slight structural damage occurs, slate blows off roofs
10      48-55   Storm   Very high waves (20-30 ft) with overhanging crests, sea white with densely blown foam, heavy rolling, lowered visibility        Seldom experienced on land, trees broken or uprooted, "considerable structural damage"
11      56-63   Violent Storm   Exceptionally high (30-45 ft) waves, foam patches cover sea, visibility more reduced     
12      64+     Hurricane       Air filled with foam, waves over 45 ft, sea completely white with driving spray, visibility greatly reduced      
 */

/**
 * The Beaufort Wind Force Scale is a non-subjective method of estimating wind conditions. The scale is broken up into 13
 * force band, each band having a speed range. This class tries to replicate the tables found on the Internet. Unfortunately
 * the speed bands on the Internet are a bit problematic. Force 1 has a range of 1 - 3 MPH, while force 2 has a range of
 * 4 - 6 MPH. What happens from wind > 3 and wind < 4. I made the slight adjustment to the bands such that the range for
 * force 1 is <code>1 >= speed < 4</code> and for force 2 the range is <code>4 >= speed < 7</code>.
 */
public class BeaufortScale
{
    public static final int MAX_FORCE = 12;
    private static final BeaufortForce forces[] = {
            new BeaufortForce( 0, new Speed( 0.0, Speed.Unit.MILES_PER_HOUR), new Speed( 1.0, Speed.Unit.MILES_PER_HOUR), "Calm",            "Sea surface smooth and mirror-like", "Calm, smoke rises vertically"),
            new BeaufortForce( 1, new Speed( 1.0, Speed.Unit.MILES_PER_HOUR), new Speed( 4.0, Speed.Unit.MILES_PER_HOUR), "Light Air",       "Sea surface smooth and mirror-like", "Calm, smoke rises vertically"),
            new BeaufortForce( 2, new Speed( 4.0, Speed.Unit.MILES_PER_HOUR), new Speed( 7.0, Speed.Unit.MILES_PER_HOUR), "Light Breeze",    "Sea surface smooth and mirror-like", "Calm, smoke rises vertically"),
            new BeaufortForce( 3, new Speed( 7.0, Speed.Unit.MILES_PER_HOUR), new Speed(11.0, Speed.Unit.MILES_PER_HOUR), "Gentle Breeze",   "Sea surface smooth and mirror-like", "Calm, smoke rises vertically"),
            new BeaufortForce( 4, new Speed(11.0, Speed.Unit.MILES_PER_HOUR), new Speed(17.0, Speed.Unit.MILES_PER_HOUR), "Moderate Breeze", "Sea surface smooth and mirror-like", "Calm, smoke rises vertically"),
            new BeaufortForce( 5, new Speed(17.0, Speed.Unit.MILES_PER_HOUR), new Speed(22.0, Speed.Unit.MILES_PER_HOUR), "Fresh Breeze",    "Sea surface smooth and mirror-like", "Calm, smoke rises vertically"),
            new BeaufortForce( 6, new Speed(22.0, Speed.Unit.MILES_PER_HOUR), new Speed(28.0, Speed.Unit.MILES_PER_HOUR), "Strong Breeze",   "Sea surface smooth and mirror-like", "Calm, smoke rises vertically"),
            new BeaufortForce( 7, new Speed(28.0, Speed.Unit.MILES_PER_HOUR), new Speed(34.0, Speed.Unit.MILES_PER_HOUR), "Near Gale",       "Sea surface smooth and mirror-like", "Calm, smoke rises vertically"),
            new BeaufortForce( 8, new Speed(34.0, Speed.Unit.MILES_PER_HOUR), new Speed(41.0, Speed.Unit.MILES_PER_HOUR), "Gale",            "Sea surface smooth and mirror-like", "Calm, smoke rises vertically"),
            new BeaufortForce( 9, new Speed(41.0, Speed.Unit.MILES_PER_HOUR), new Speed(48.0, Speed.Unit.MILES_PER_HOUR), "Strong Gale",     "Sea surface smooth and mirror-like", "Calm, smoke rises vertically"),
            new BeaufortForce(10, new Speed(48.0, Speed.Unit.MILES_PER_HOUR), new Speed(56.0, Speed.Unit.MILES_PER_HOUR), "Storm",           "Sea surface smooth and mirror-like", "Calm, smoke rises vertically"),
            new BeaufortForce(11, new Speed(56.0, Speed.Unit.MILES_PER_HOUR), new Speed(64.0, Speed.Unit.MILES_PER_HOUR), "Violent Storm",   "Sea surface smooth and mirror-like", "Calm, smoke rises vertically"),
            new BeaufortForce(12, new Speed(64.0, Speed.Unit.MILES_PER_HOUR), new Speed(200.0, Speed.Unit.MILES_PER_HOUR), "Hurricane",      "Sea surface smooth and mirror-like", "Calm, smoke rises vertically")
    };
    
    private BeaufortScale()
    {
    }
    
    /**
     * Lookup the Beaufort Wind Force for the given speed
     * 
     * @param speed The speed for the lookup
     * @return The Beaufort Force object
     * 
     * @throws IllegalArgumentException If the speed is less than 0 or greater than 200 MPH
     */
    public static BeaufortForce lookupForce(Speed speed) throws IllegalArgumentException
    {
        for (BeaufortForce force : forces)
            if (force.isInForceBand(speed))
                return force;
        
        throw new IllegalArgumentException("Speed of " + speed + Speed.getDefaultUnit() + " is not within valid range");
            
    }
}
