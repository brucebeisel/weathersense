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
 * An animation that just sets a set of LEDs to an intensity.
 *
 * @author Bruce Beisel
 */
public class PiGlowOneShot implements PiGlowAnimation {
    private final List<PiGlowLED> leds;
    private final long delay;
    private final int intensity;
    private boolean hasRun;
    private long fireTime;

    public PiGlowOneShot(long delay, int intensity, List<PiGlowLED> leds) {
        this.leds = new ArrayList<>(leds);
        this.delay = delay;
        this.intensity = intensity;
        this.hasRun = false;
    }

    @Override
    public void initialize() {
        fireTime = System.currentTimeMillis() + delay;
    }

    @Override
    public long nextStepMillis(long now) {
        if (hasRun)
            return -1;

        return fireTime - now;
    }

    @Override
    public void executeNextStep(long now) {
        if (now < fireTime)
            return;

        leds.stream().forEach((led) -> {
            led.setIntensity(intensity);
        });

        hasRun = true;
    }
    
}
