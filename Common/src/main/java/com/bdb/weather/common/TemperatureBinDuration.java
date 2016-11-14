/* 
 * Copyright (C) 2016 Bruce Beisel
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
package com.bdb.weather.common;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;

import com.bdb.weather.common.measurement.Temperature;

/**
 *
 * @author bruce
 */
public class TemperatureBinDuration {
    private final TemperatureBin temperatureBin;
    private final LocalDate date;
    private Duration duration;

    /**
     *
     * @param bin
     * @param date
     */
    public TemperatureBinDuration(TemperatureBin bin, LocalDate date) {
        this(bin, date, Duration.ZERO);
    }

    /**
     *
     * @param bin
     * @param date
     * @param duration
     */
    public TemperatureBinDuration(TemperatureBin bin, LocalDate date, Duration duration) {
        this.date = date;
        this.duration = duration;
        this.temperatureBin = bin;
    }

    /**
     *
     * @return
     */
    public int getBinId() {
        return temperatureBin.getBinId();
    }

    /**
     *
     * @return
     */
    public TemperatureBin getTemperatureBin() {
        return temperatureBin;
    }

    /**
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * @return the duration
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     *
     * @param duration
     */
    public void addDuration(Duration duration) {
        this.duration = this.duration.plus(duration);
    }

    /**
     * Add the duration to this bin if the temperature belongs to this bin
     *
     * @param duration
     * @param t
     */
    public void addDuration(Temperature t, Duration duration) {
        if (isInBin(t))
            addDuration(duration);
    }

    /**
     *
     * @param t
     * @return
     */
    public boolean isInBin(Temperature t) {
        return temperatureBin.isInBin(t);
    }

    /**
     *
     * @param other
     * @return
     */
    public boolean isSameBin(TemperatureBinDuration other) {
        return getBinId() == other.getBinId();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.temperatureBin.getBinId());
        hash = 83 * hash + Objects.hashCode(this.date);
        hash = 83 * hash + Objects.hashCode(this.duration);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        final TemperatureBinDuration other = (TemperatureBinDuration)obj;

        if (this.temperatureBin.getBinId() != other.temperatureBin.getBinId())
            return false;

        if (!Objects.equals(this.date, other.date))
            return false;

        return Objects.equals(this.duration, other.duration);
    }
}