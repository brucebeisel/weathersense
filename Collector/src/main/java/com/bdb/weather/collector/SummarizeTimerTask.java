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
package com.bdb.weather.collector;

import java.time.LocalDate;
import java.util.Objects;
import java.util.TimerTask;
import java.util.concurrent.Executor;

/**
 * A timer task that delays the calculation of the daily summary. If the history records are coming in fast this will reduce the
 * number of times the daily summary needs to be calculated.
 *
 * @author Bruce
 * @since 1.0
 */
class SummarizeTimerTask extends TimerTask {
    private final LocalDate day;
    private final Executor executor;
    private final Runnable command;

    /**
     * Constructor.
     *
     * @param day The day that will be summarized
     * @param executor The receiver of the command that gets sent when the timer expires
     * @param command The timer expiration command that will trigger a daily summary calculation
     */
    public SummarizeTimerTask(LocalDate day, Executor executor, Runnable command) {
        this.executor = executor;
        this.command = command;
        this.day = day;
    }

    /**
     * Check if this timer task is a duplicate for the given parameters
     *
     * @param other The date
     * @return True if this is a duplicate for the given parameters
     */
    public boolean isDuplicate(LocalDate other) {
        return day.equals(other);
    }
    
    /**
     * Entry point for the TimerTask Runnable.
     */
    @Override
    public void run() {
        executor.execute(command);
    }

    /**
     * Check equality with another SummerizerTimerTask
     *
     * @param obj The item to be compared against
     *
     * @return True if the objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (getClass() != obj.getClass()) 
            return false;

        final SummarizeTimerTask other = (SummarizeTimerTask) obj;
        if (!Objects.equals(this.day, other.day))
            return false;

        return this.executor == other.executor && this.command == other.command;
    }

    /**
     * Calculate the hash code.
     *
     * @return The hash code
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.day);
        return hash;
    }
}