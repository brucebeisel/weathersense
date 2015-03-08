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
package com.bdb.piglow4j;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bruce
 */
public class PiGlowBlinker implements PiGlowPattern, PiGlowAnimation {
    private final int delay;
    private final int interval;
    private final int lowIntensity;
    private final int highIntensity;
    private final int steps;
    private final boolean lowToHigh;
    private final boolean reverse;
    private final int repetitions;
    private final List<PiGlowLED> leds;

    private int currentStep = 0;
    private int currentIntensity;
    private long startTime;
    private long nextStepTime;
    private int deltaIntensity;
    private int count;

    public PiGlowBlinker(int delayMillis, int intervalMillis, int lowIntensity, int highIntensity, int steps, boolean lowToHigh, boolean reverse, int repetitions, List<PiGlowLED> leds) {
        this.delay = delayMillis;
        this.interval = intervalMillis;
        this.lowIntensity = lowIntensity;
        this.highIntensity = highIntensity;
        this.steps = steps;
        this.lowToHigh = lowToHigh;
        this.reverse = reverse;
        this.repetitions = repetitions;
        this.leds = new ArrayList<>(leds);
    }

    @Override
    public void addLEDs(PiGlowLED... leds) {
    }

    @Override
    public void addLEDs(List<PiGlowLED> leds) {
    }

    @Override
    public void removeLED(PiGlowLED led) {
    }

    @Override
    public void start(long now) {
        startTime = now;
        nextStepTime = now + delay;
    }

    @Override
    public void stop() {
    }

    @Override
    public void initialize() {
	long now = System.currentTimeMillis();
        startTime = now;
        nextStepTime = now + delay;
        currentStep = 0;
	count = 0;
        deltaIntensity = (highIntensity - lowIntensity) / steps;
        if (lowToHigh) {
            currentIntensity = lowIntensity;
        }
        else {
            currentIntensity = highIntensity;
            deltaIntensity = -deltaIntensity;
        }
    }

    @Override
    public long nextStepMillis(long now) {
	if (count >= repetitions)
	    return -1;
	else
	    return nextStepTime - now;
    }

    @Override
    public void executeNextStep(long now) {
        if (now < nextStepTime)
            return;

        leds.stream().forEach((led) -> {
            led.setIntensity(currentIntensity);
        });

        currentStep++;
        currentIntensity += deltaIntensity;
	nextStepTime += interval;
        if (currentStep > steps) {
	    currentIntensity = lowIntensity;
	    count++;
	    currentStep = 0;
	}

    }
}
