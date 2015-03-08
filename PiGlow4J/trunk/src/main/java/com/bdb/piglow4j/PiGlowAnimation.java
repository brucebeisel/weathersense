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

/**
 *
 * @author Bruce Beisel
 */
public interface PiGlowAnimation {
    /**
     * Initialize any counters or times that are needed to track the animation.
     */
    public void initialize();

    /**
     * How many milliseconds to wait before the next step of the animation must run.
     */
    public long nextStepMillis(long now);

    /**
     * Change the LEDs if the current time is equal to or past the next step time.
     */
    public void executeNextStep(long now);
}