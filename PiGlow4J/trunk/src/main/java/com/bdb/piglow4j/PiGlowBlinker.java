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
package com.bdb.piglow4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Animation that blinks one or more LEDs. The blinking can be a simple on off, or the increase in intensity can be stepped both
 * upward and downward.
 * 
 * @author Bruce Beisel
 */
public class PiGlowBlinker implements PiGlowAnimation {
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
    private long nextStepTime;
    private int deltaIntensity;
    private int count;

    /**
     * Constructor.
     * 
     * @param delayMillis The initial delay before the animation starts
     * @param intervalMillis The interval between repetitions
     * @param lowIntensity The starting intensity
     * @param highIntensity The highest intensity
     * @param steps The number of steps required to go from the low to the high intensity. Note that (high - low) % step must equal 0
     * @param lowToHigh Whether to animate low to high or high to low
     * @param reverse Whether the animation will reverse when the high intensity is reached.
     * @param repetitions The number of times the animation will repeat
     * @param leds The list of LEDs that will be animated
     */
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
    public void initialize(long now) {
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
	    return PiGlowAnimation.ANIMATION_COMPLETE;
	else
	    return nextStepTime - now;
    }

    @Override
    public void executeNextStep(long now) {
        if (now < nextStepTime)
            return;

        leds.forEach((led) -> led.setIntensity(currentIntensity) );

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