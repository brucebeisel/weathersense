/*
 * Copyright (C) 2015 Bruce
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
package com.bdb.weather.healthmonitor.piglow;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Bruce
 */
public class PiGlowLED {
    public int MIN_INTENSITY = 0;
    public int MAX_INTENSITY = 255;
    private final PiGlowArm arm;
    private final PiGlowColor color;
    private int intensity;
    private static final Map<Integer,PiGlowLED> leds = new HashMap<>();

    static {
        for (PiGlowArm arm : PiGlowArm.values()) {
            for (PiGlowColor color : PiGlowColor.values()) {
                int id = LedIdentifier(arm, color);
                PiGlowLED led = new PiGlowLED(arm, color);
                leds.put(id, led);
            }
        }
    }

    public static PiGlowLED findLed(PiGlowArm arm, PiGlowColor color) {
        int id = LedIdentifier(arm, color);
        return leds.get(id);
    }

    private static int LedIdentifier(PiGlowArm arm, PiGlowColor color) {
        return arm.ordinal() << 8 | color.ordinal();
    }

    private PiGlowLED(PiGlowArm arm, PiGlowColor color) {
        this.arm = arm;
        this.color = color;
    }

    public void setIntensity(int value) {
        intensity = value;
    }

    public PiGlowArm getArm() {
        return arm;
    }

    public PiGlowColor getColor() {
        return color;
    }

    public int getIntensity() {
        return intensity;
    }
}