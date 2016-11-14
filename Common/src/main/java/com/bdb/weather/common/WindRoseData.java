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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.bdb.weather.common.measurement.Heading;
import com.bdb.weather.common.measurement.Speed;

/**
 *
 * @author bruce
 */
public class WindRoseData {
    LocalDate	    date;
    WindSlice	    slices[];
    Duration	    totalSeconds = Duration.ZERO; // Total seconds for this period
    Duration	    calmSeconds = Duration.ZERO;  // Seconds the wind is calm
    List<SpeedBin>  speedBins;

    /**
     *
     * @param date
     * @param windParameters
     */
    public WindRoseData(LocalDate date, WindParameters windParameters) {
        speedBins = windParameters.getSpeedBinList();
        this.date = date;
        slices = new WindSlice[windParameters.getNumWindDirectionSlices()];

        for (int i = 0; i < windParameters.getNumWindDirectionSlices(); i++) {
            slices[i] = new WindSlice(date, i, speedBins, windParameters);
        }
    }

    /**
     *
     * @param duration
     * @param wind
     */
    public void measurement(Duration duration, Wind wind) {
        Speed speed = wind.getSpeed();
        Heading heading = wind.getDirection();

        if (speed == null || heading == null)
            return;
        
        totalSeconds = totalSeconds.plus(duration);

        if (speed.get() == 0.0)
            calmSeconds = calmSeconds.plus(duration);

        for (WindSlice slice : slices) {
            slice.applyWindSample(duration, wind);
            slice.setTotalDuration(totalSeconds);
            slice.setWindyDuration(totalSeconds.minus(calmSeconds));
        }
    }

    /**
     *
     * @param wrd
     */
    public void addWindRoseData(WindRoseData wrd) {
        if (wrd.speedBins.size() != speedBins.size() || wrd.slices.length != slices.length)
            throw new IllegalArgumentException("WindRoseData structure does not match structure of data to be added");

        totalSeconds = totalSeconds.plus(wrd.totalSeconds);
        calmSeconds = calmSeconds.plus(wrd.calmSeconds);

        for (int i = 0; i < wrd.slices.length; i++) {
            slices[i].applyWindSlice(wrd.slices[i]);
            slices[i].setTotalDuration(totalSeconds);
            slices[i].setWindyDuration(totalSeconds.minus(calmSeconds));
        }
    }

    /**
     *
     * @return
     */
    public LocalDate getTime() {
        return date;
    }

    /**
     *
     * @return
     */
    public int getNumSlices() {
        return slices.length;
    }

    /**
     *
     * @param index
     * @return
     */
    public WindSlice getSlice(int index) {
        return slices[index];
    }

    /**
     *
     * @return
     */
    public List<WindSlice> getSlices() {
        return Collections.unmodifiableList(Arrays.asList(slices));
    }

    /**
     *
     * @return
     */
    public Duration getCalmDuration() {
        return calmSeconds;
    }

    /**
     *
     * @return
     */
    public Duration getTotalDuration() {
        return totalSeconds;
    }

    /**
     *
     * @param duration
     */
    public void setTotalDuration(Duration duration) {
        totalSeconds = duration;
    }

    /**
     *
     * @param duration
     */
    public void setCalmDuration(Duration duration) {
        calmSeconds = duration;
    }

    /**
     *
     * @return
     */
    public List<SpeedBin> getSpeedBins() {
        return Collections.unmodifiableList(speedBins);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.date);
        hash = 47 * hash + Arrays.deepHashCode(this.slices);
        hash = 47 * hash + Objects.hashCode(this.totalSeconds);
        hash = 47 * hash + Objects.hashCode(this.calmSeconds);
        hash = 47 * hash + Objects.hashCode(this.speedBins);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        final WindRoseData other = (WindRoseData)obj;
        if (!Objects.equals(this.date, other.date))
            return false;

        if (!Arrays.deepEquals(this.slices, other.slices))
            return false;

        if (!Objects.equals(this.totalSeconds, other.totalSeconds))
            return false;

        if (!Objects.equals(this.calmSeconds, other.calmSeconds))
            return false;

        return Objects.equals(this.speedBins, other.speedBins);
    }

    @Override
    public String toString()
    {
        float calmPercent;

        if (!totalSeconds.isZero())
            calmPercent = (float)calmSeconds.getSeconds() / (float)totalSeconds.getSeconds() * 100.0f;
        else
            calmPercent = 0.0f;

        StringBuilder sb = new StringBuilder(String.format("Total time: %d%nCalm:%d %.1f%n", totalSeconds.getSeconds(), calmSeconds.getSeconds(), calmPercent));
        for (WindSlice slice : slices)
            sb.append(slice).append("\n");

        return sb.toString();
    }
}
