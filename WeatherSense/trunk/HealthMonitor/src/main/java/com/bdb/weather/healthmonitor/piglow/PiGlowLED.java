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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
    private final int address;
    private int intensity;
    private static final Map<Integer,PiGlowLED> leds = new HashMap<>();
    private static final Map<PiGlowArm,List<PiGlowLED>> arms = new HashMap<>();
    private static final Map<PiGlowColor,List<PiGlowLED>> colors = new HashMap<>();
    private static final List<PiGlowLED> ledList = new ArrayList<>();

    static {
        for (PiGlowArm arm : PiGlowArm.values())
            arms.put(arm, new ArrayList<>());

        for (PiGlowColor color : PiGlowColor.values())
            colors.put(color, new ArrayList<>());

        createLED(PiGlowArm.LEFT, PiGlowColor.WHITE, 13);
        createLED(PiGlowArm.LEFT, PiGlowColor.BLUE, 15);
        createLED(PiGlowArm.LEFT, PiGlowColor.GREEN, 4);
        createLED(PiGlowArm.LEFT, PiGlowColor.YELLOW, 3);
        createLED(PiGlowArm.LEFT, PiGlowColor.ORANGE, 2);
        createLED(PiGlowArm.LEFT, PiGlowColor.RED, 1);

        createLED(PiGlowArm.TOP, PiGlowColor.WHITE, 10);
        createLED(PiGlowArm.TOP, PiGlowColor.BLUE, 5);
        createLED(PiGlowArm.TOP, PiGlowColor.GREEN, 6);
        createLED(PiGlowArm.TOP, PiGlowColor.YELLOW, 9);
        createLED(PiGlowArm.TOP, PiGlowColor.ORANGE, 8);
        createLED(PiGlowArm.TOP, PiGlowColor.RED, 7);

        createLED(PiGlowArm.RIGHT, PiGlowColor.WHITE, 11);
        createLED(PiGlowArm.RIGHT, PiGlowColor.BLUE, 12);
        createLED(PiGlowArm.RIGHT, PiGlowColor.GREEN, 14);
        createLED(PiGlowArm.RIGHT, PiGlowColor.YELLOW, 16);
        createLED(PiGlowArm.RIGHT, PiGlowColor.ORANGE, 17);
        createLED(PiGlowArm.RIGHT, PiGlowColor.RED, 18);
    }

    private static void createLED(PiGlowArm arm, PiGlowColor color, int address) {
        PiGlowLED led = new PiGlowLED(arm, color, address);
        leds.put(led.getIdentifier(), led);
        arms.get(led.getArm()).add(led);
        colors.get(led.getColor()).add(led);
        ledList.add(led);

    }

    public static PiGlowLED findLed(PiGlowArm arm, PiGlowColor color) {
        int id = LedIdentifier(arm, color);
        return leds.get(id);
    }

    public static List<PiGlowLED> armLEDs(PiGlowArm arm) {
        return Collections.unmodifiableList(arms.get(arm));
    }

    public static List<PiGlowLED> colorLEDs(PiGlowColor color) {
        return Collections.unmodifiableList(colors.get(color));
    }

    public static List<PiGlowLED> allLEDs() {
        return Collections.unmodifiableList(ledList);
    }

    private static int LedIdentifier(PiGlowArm arm, PiGlowColor color) {
        return arm.ordinal() << 8 | color.ordinal();
    }

    private PiGlowLED(PiGlowArm arm, PiGlowColor color, int address) {
        this.arm = arm;
        this.color = color;
        this.address = address;
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

    public int getAddress() {
        return address;
    }

    public int getIntensity() {
        return intensity;
    }

    public int getIdentifier() {
        return LedIdentifier(arm, color);
    }
}