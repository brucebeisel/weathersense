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
package com.bdb.weather.common.astronomical;
 
import java.time.LocalDate;
import java.time.temporal.JulianFields;
import java.util.concurrent.atomic.AtomicReference;

import com.bdb.util.Pair;
 
 
/// LunarPhaseCalculator - phase of the moon calculations
// <P>
// Adapted from "moontool.c" by John Walker, Release 2.0.
// <P>
// <A HREF="/resources/classes/Acme/Phase.java">Fetch the software.</A><BR>
// <A HREF="/resources/classes/Acme.tar.Z">Fetch the entire Acme package.</A>
public class LunarPhaseCalculator {
 
    // Astronomical constants.
    // 1980 January 0.0
    private static final double EPOCH = 2444238.5D;
 
    // Constants defining the Sun's apparent orbit.
    // ecliptic longitude of the Sun at EPOCH 1980.0
    private static final double SUN_ECLIPTIC_LONGITUDE_AT_EPOCH = 278.833540D;
 
    // ecliptic longitude of the Sun at perigee
    private static final double SUN_ECLIPTIC_LONGITUDE_AT_PERIGEE = 282.596403D;
 
    // eccentricity of Earth's orbit
    private static final double EARTH_ORBIT_ECCENTRICITY = 0.016718D;
 
    // semi-major axis of Earth's orbit, km
    private static final double EARTH_ORBIT_SEMI_MAJOR_AXIS_KM = 1.495985e8D;
 
    // sun's angular size, degrees, at semi-major axis distance
    private static final double SUN_ANGULAR_SIZE_IN_DEGREES = 0.533128D;
 
    // Elements of the Moon's orbit, EPOCH 1980.0.
    // moon's mean lonigitude at the EPOCH
    private static final double MOON_MEAN_LONGITUDE_AT_EPOCH = 64.975464D;
 
    // mean longitude of the perigee at the EPOCH
    private static final double MOON_MEAN_LONGITUDE_OF_PERIGEE_AT_EPOCH = 349.383063D;
 
    // eccentricity of the Moon's orbit
    private static final double MOON_ECCENTRICITY = 0.054900D;
 
    // moon's angular size at distance a from Earth
    private static final double MOON_ANGULAR_SIZE = 0.5181D;
 
    // semi-major axis of Moon's orbit in km
    private static final double MOON_ORBIT_SEMI_MAJOR_AXIS_KM = 384401.0D;
 
    // synodic month (new Moon to new Moon)
    private static final double SYNODIC_MONTH = 29.53058868D;
 
    // Mathematical constants.
    private static final double EPSILON = 1E-6D;
 
    // Handy mathematical functions.
    // Fix angle.
    private static double fixangle(double angle) {
        //
        // Can't use Math.IEEEremainder here because remainder differs
        // from modulus for negative numbers.
        //
        double fixedAngle = angle - 360.0 * Math.floor(angle / 360.0D);
        return fixedAngle;
    }
 
    // Degrees to radians.
    private static double torad(double degrees) {
        return degrees * Math.PI / 180.0D;
    }
 
    // Radians to degrees.
    private static double todeg(double radians) {
        return radians * 180.0D / Math.PI;
    }
 
    // kepler - solve the equation of Kepler
    private static double kepler(double m, double ecc) {
        double e = m = torad(m);
        double delta;
        do {
            delta = e - ecc * Math.sin(e) - m;
            e -= delta / (1 - ecc * Math.cos(e));
        } while (Math.abs(delta) > EPSILON);
        return e;
    }
 
    /// Calculate phase of moon as a fraction.
    // <P>
    // @param pdate time for which the phase is requested, as from jtime()
    // @param pphaseR Ref for illuminated fraction of Moon's disk
    // @param mageR Ref for age of moon in days
    // @param distR Ref for distance in km from center of Earth
    // @param angdiaR Ref for angular diameter in degrees as seen from Earth
    // @param sudistR Ref for distance in km to Sun
    // @param suangdiaR Ref for Sun's angular diameter
    // @return terminator phase angle as a fraction of a full circle (i.e., 0 to 1)
    //
    public static double phase(double pdate,
                               AtomicReference<Double> pphaseR,
                               AtomicReference<Double> mageR,
                               AtomicReference<Double> distR,
                               AtomicReference<Double> angdiaR,
                               AtomicReference<Double> sudistR,
                               AtomicReference<Double> suangdiaR) {
 
        // Calculation of the Sun's position.
        double Day = pdate - EPOCH;                                // date within EPOCH
        double N = fixangle((360 / 365.2422) * Day);               // mean anomaly of the Sun
        double M = fixangle(N + SUN_ECLIPTIC_LONGITUDE_AT_EPOCH - SUN_ECLIPTIC_LONGITUDE_AT_PERIGEE);                  // convert from perigee co-ordinates to EPOCH 1980.0
        double Ec = kepler(M, EARTH_ORBIT_ECCENTRICITY);                             // solve equation of Kepler
        Ec = Math.sqrt((1 + EARTH_ORBIT_ECCENTRICITY) / (1 - EARTH_ORBIT_ECCENTRICITY)) * Math.tan(Ec / 2);
        Ec = 2 * todeg(Math.atan(Ec));                             // true anomaly
        double Lambdasun = fixangle(Ec + SUN_ECLIPTIC_LONGITUDE_AT_PERIGEE);                  // Sun's geocentric ecliptic longitude
        // Orbital distance factor.
        double F = ((1 + EARTH_ORBIT_ECCENTRICITY * Math.cos(torad(Ec))) / (1 - EARTH_ORBIT_ECCENTRICITY * EARTH_ORBIT_ECCENTRICITY));
        double SunDist = EARTH_ORBIT_SEMI_MAJOR_AXIS_KM / F;                              // distance to Sun in km
        double SunAng = F * SUN_ANGULAR_SIZE_IN_DEGREES;                             // Sun's angular size in degrees
 
        // Calculation of the Moon's position.
        // Moon's mean longitude.
        double ml = fixangle(13.1763966 * Day + MOON_MEAN_LONGITUDE_AT_EPOCH);
 
        // Moon's mean anomaly.
        double MM = fixangle(ml - 0.1114041 * Day - MOON_MEAN_LONGITUDE_OF_PERIGEE_AT_EPOCH);
 
        // Evection.
        double Ev = 1.2739 * Math.sin(torad(2 * (ml - Lambdasun) - MM));
 
        // Annual equation.
        double Ae = 0.1858 * Math.sin(torad(M));
 
        // Correction term.
        double A3 = 0.37 * Math.sin(torad(M));
 
        // Corrected anomaly.
        double MmP = MM + Ev - Ae - A3;
 
        // Correction for the equation of the centre.
        double mEc = 6.2886 * Math.sin(torad(MmP));
 
        // Another correction term.
        double A4 = 0.214 * Math.sin(torad(2 * MmP));
 
        // Corrected longitude.
        double lP = ml + Ev + mEc - Ae + A4;
 
        // Variation.
        double V = 0.6583 * Math.sin(torad(2 * (lP - Lambdasun)));
 
        // True longitude.
        double lPP = lP + V;
 
                // Calculation of the phase of the Moon.
        // Age of the Moon in degrees.
        double MoonAge = lPP - Lambdasun;
 
        // LunarPhaseCalculator of the Moon.
        double MoonPhase = (1 - Math.cos(torad(MoonAge))) / 2;
 
                // Calculate distance of moon from the centre of the Earth.
        double MoonDist = (MOON_ORBIT_SEMI_MAJOR_AXIS_KM * (1 - MOON_ECCENTRICITY * MOON_ECCENTRICITY))
                / (1 + MOON_ECCENTRICITY * Math.cos(torad(MmP + mEc)));
 
        // Calculate Moon's angular diameter.
        double MoonDFrac = MoonDist / MOON_ORBIT_SEMI_MAJOR_AXIS_KM;
        double MoonAng = MOON_ANGULAR_SIZE / MoonDFrac;
 
        pphaseR.set(MoonPhase);
        mageR.set(SYNODIC_MONTH * (fixangle(MoonAge) / 360.0));
        distR.set(MoonDist);
        angdiaR.set(MoonAng);
        sudistR.set(SunDist);
        suangdiaR.set(SunAng);
        return torad(fixangle(MoonAge));
    }

    public static Pair<Double,Boolean> moonPhase(LocalDate date) {
        double julianDate = (double)date.getLong(JulianFields.JULIAN_DAY);

        AtomicReference<Double> pphaseR = new AtomicReference<>();
        AtomicReference<Double> mageR = new AtomicReference<>();
        AtomicReference<Double> distR = new AtomicReference<>();
        AtomicReference<Double> angdiaR = new AtomicReference<>();
        AtomicReference<Double> sudistR = new AtomicReference<>();
        AtomicReference<Double> suangdiaR = new AtomicReference<>();
        double phaseValue = phase(julianDate, pphaseR, mageR, distR, angdiaR, sudistR, suangdiaR);
        boolean waxing = phaseValue < Math.PI;

        return new Pair<>(pphaseR.get(), waxing);
    }
    public static void main(String args[]) {
        LocalDate now = LocalDate.now();
        for (int i = 0; i < 31; i++) {
            double date = (double)now.getLong(JulianFields.JULIAN_DAY);
            //double date = 2456718.0;
            AtomicReference<Double> pphaseR = new AtomicReference<>();
            AtomicReference<Double> mageR = new AtomicReference<>();
            AtomicReference<Double> distR = new AtomicReference<>();
            AtomicReference<Double> angdiaR = new AtomicReference<>();
            AtomicReference<Double> sudistR = new AtomicReference<>();
            AtomicReference<Double> suangdiaR = new AtomicReference<>();
     
            double phaseValue = phase(date, pphaseR, mageR, distR, angdiaR, sudistR, suangdiaR);
     
            System.out.println(String.format("Date = %s  Phase = %.2f  Terminator Phase = %.4f",
                    now.toString(), pphaseR.get(), phaseValue));

            Pair<Double,Boolean> moon = moonPhase(now);
            LunarPhase phase = LunarPhase.whichPhase(moon.first, moon.second);
            System.out.println(String.format("Date = %s  Phase = %.2f  Waxing = %s " ,
                    now.toString(), moon.first, moon.second.toString()) + phase);
            now = now.plusDays(1);
        }
    }
}