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

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bruce Beisel
 */
public class PiGlowAnimator implements Runnable {
    private final ScheduledExecutorService executor;
    private final PiGlowAnimation animation;
    private final PiGlow piGlow;
    private static final Logger logger = Logger.getLogger(PiGlowAnimator.class.getName());

    public PiGlowAnimator(PiGlow piGlow, PiGlowAnimation animation) {
        this.animation = animation;
        this.piGlow = piGlow;
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        long now = System.currentTimeMillis();
	logger.info("Starting animation at " + now);
        animation.initialize();
	scheduleNextStep();
    }

    public void stop() {
        executor.shutdown();
    }

    private void scheduleNextStep() {
	long now = System.currentTimeMillis();
	long millis = animation.nextStepMillis(now);
	logger.info("Next step in " + millis + " milliseconds");
	if (millis > 0)
	    executor.schedule(this, millis, TimeUnit.MILLISECONDS);
	else
	    executor.shutdown();
    }


    @Override
    public void run() {
        try {
	    animation.executeNextStep();
            piGlow.updateLEDs();
            scheduleNextStep();
        }
        catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
