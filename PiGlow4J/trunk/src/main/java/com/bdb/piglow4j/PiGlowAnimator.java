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
import java.util.ArrayList;
import java.util.List;
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
    private final List<PiGlowAnimation> animations;
    private final PiGlow piGlow;
    private static final Logger logger = Logger.getLogger(PiGlowAnimator.class.getName());

    public PiGlowAnimator(PiGlow piGlow) {
        animations = new ArrayList<>();
        this.piGlow = piGlow;
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void addAnimation(PiGlowAnimation animation) {
        animations.add(animation);
    }

    public void start() {
        long now = System.currentTimeMillis();
	logger.info("Starting animation at " + now);
        animations.forEach((animation)->animation.initialize());
	scheduleNextStep(now);
    }

    public void stop() {
        executor.shutdown();
    }

    public void waitForTermination(long millis) throws InterruptedException {
        if (!executor.awaitTermination(millis, TimeUnit.MILLISECONDS))
	    logger.info("Timed out waiting for executor termination");
    }

    private void scheduleNextStep(long now) {
        long millis = Long.MAX_VALUE;
        for (PiGlowAnimation animation : animations) {
            long nextStepMillis = animation.nextStepMillis(now);
            if (nextStepMillis >= 0)
		millis = Math.min(millis, nextStepMillis);
        }

	logger.info("Next step in " + millis + " milliseconds");
        if (millis == Long.MAX_VALUE)
	    executor.shutdown();
	else if (millis == 0)
	    executor.execute(this);
	else
	    executor.schedule(this, millis, TimeUnit.MILLISECONDS);
    }


    @Override
    public void run() {
        try {
            long now = System.currentTimeMillis();
	    animations.forEach((animation)->animation.executeNextStep(now));
            piGlow.updateLEDs();
            scheduleNextStep(now);
        }
        catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
