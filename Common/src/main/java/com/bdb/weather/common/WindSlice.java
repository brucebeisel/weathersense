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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.bdb.util.measurement.MeasurementAverage;

import com.bdb.weather.common.db.DatabaseUnits;
import com.bdb.weather.common.measurement.Heading;
import com.bdb.weather.common.measurement.Speed;

/**
 *
 * @author bruce
 */
public class WindSlice {
    private final LocalDate date;
    private final int headingIndex;
    private Duration sliceDuration = Duration.ZERO; // Duration the wind was blowing in this directional slice
    private Duration totalDuration = Duration.ZERO;
    private Duration windyDuration = Duration.ZERO;
    private Speed maxSpeed = new Speed(0.0);
    private float percentageOfTotal = 0.0f;
    private float percentageOfWind = 0.0f;
    private final List<SpeedBin> speedBins;
    private final List<SpeedBinDuration> binData = new ArrayList<>();
    private final WindParameters windParams;
    private MeasurementAverage<Speed> avgSpeed = new MeasurementAverage<>(new Speed(0.0), Duration.ZERO);

    /**
     *
     * @param date
     * @param headingIndex
     * @param speedBins
     * @param windParams
     */
    public WindSlice(LocalDate date, int headingIndex, List<SpeedBin> speedBins, WindParameters windParams) {
        this.date = date;
        this.headingIndex = headingIndex;
        this.speedBins = speedBins;
        this.windParams = windParams;

        for (int i = 0; i < speedBins.size(); i++)
            binData.add(new SpeedBinDuration(i, headingIndex, date));
    }

    /**
     *
     * @param durations
     */
    public void setBinDurations(List<SpeedBinDuration> durations) {
        if (durations.size() != speedBins.size())
            throw new IllegalArgumentException("SpeedBinDuration list size mismatch. Received " + durations.size()
                    + " expected " + speedBins.size());

        binData.clear();
        binData.addAll(durations);
    }

    /**
     *
     * @param duration
     */
    public void addBinDuration(SpeedBinDuration duration) {
        binData.add(duration);
        if (binData.size() > speedBins.size())
            binData.remove(0);
    }

    /**
     *
     * @return
     */
    public List<SpeedBinDuration> getBinDurations() {
        return Collections.unmodifiableList(binData);
    }

    /**
     *
     * @param headingIndex
     * @return
     */
    public boolean inSlice(int headingIndex) {
        return headingIndex == this.headingIndex;
    }

    /**
     *
     * @param heading
     * @return
     */
    public boolean inSlice(Heading heading) {
        double headingVal = heading.get();
        //
        // If there are 360 slices then the index is the heading
        //
        if (windParams.getNumWindDirectionSlices() == 360)
            return inSlice((int)heading.get());

        double arcLength = 360.0 / windParams.getNumWindDirectionSlices();
        double startAngle = 0.0;
        double endAngle = arcLength / 2.0;
        int index = 0;
        for (int i = 0; i < windParams.getNumWindDirectionSlices(); i++) {
            if (headingVal >= startAngle && headingVal < endAngle) {
                index = i;
                break;
            }
            else {
                startAngle = endAngle;
                endAngle += arcLength;
            }
        }

        return inSlice(index);
    }

    /**
     *
     * @param windSlice
     */
    public void applyWindSlice(WindSlice windSlice) {
        if (windSlice.sliceDuration.isZero())
            return;

        if (windSlice.maxSpeed.get() > maxSpeed.get())
            maxSpeed = windSlice.maxSpeed;

        avgSpeed.applyValue(windSlice.getAvgSpeed(), windSlice.sliceDuration);

        sliceDuration = sliceDuration.plus(windSlice.sliceDuration);

        for (int i = 0; i < binData.size(); i++)
            binData.get(i).addDuration(windSlice.binData.get(i).getDuration());

    }

    /**
     *
     * @param sampleDuration
     * @param sample
     */
    public void applyWindSample(Duration sampleDuration, Wind sample) {
        double speed = sample.getSpeed().get(DatabaseUnits.SPEED);

        //
        // If this wind sample is not in this slice, ignore it
        //
        if (!inSlice(sample.getDirection()) || speed == 0.0)
            return;

        Duration duration = sampleDuration.plus(sliceDuration);

        avgSpeed.applyValue(sample.getSpeed(), sampleDuration);

        sliceDuration = duration;

        if (!totalDuration.isZero())
            percentageOfTotal = (float)sliceDuration.getSeconds() / (float)totalDuration.getSeconds() * 100.0f;
        else
            percentageOfTotal = 0.0f;

        if (!windyDuration.isZero())
            percentageOfWind = (float)sliceDuration.getSeconds() / (float)windyDuration.getSeconds() * 100.0f;
        else
            percentageOfWind = 0.0f;

        if (speed > maxSpeed.get())
            maxSpeed = sample.getSpeed();

        for (int i = 0; i < speedBins.size(); i++) {
            if (speedBins.get(i).inSpeedBin(sample.getSpeed()))
                binData.get(i).addDuration(sampleDuration);
        }
    }

    /**
     *
     * @return
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     *
     * @return
     */
    public int getHeadingIndex() {
        return headingIndex;
    }

    /**
     *
     * @return
     */
    public Speed getAvgSpeed() {
        return avgSpeed.getAverage();
    }

    /**
     *
     * @param duration
     * @param avg
     */
    public void setAvgSpeed(Duration duration, Speed avg) {
        avgSpeed = new MeasurementAverage<>(avg, duration);
    }

    /**
     *
     * @return
     */
    public Speed getMaxSpeed() {
        return maxSpeed;
    }

    /**
     *
     * @param maxSpeed
     */
    public void setMaxSpeed(Speed maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    /**
     *
     * @return
     */
    public Duration getWindyDuration() {
        return windyDuration;
    }

    /**
     *
     * @param duration
     */
    public void setWindyDuration(Duration duration) {
        windyDuration = duration;

        if (!duration.isZero())
            percentageOfWind = (float)sliceDuration.getSeconds() / (float)windyDuration.getSeconds() * 100.0f;
        else
            percentageOfWind = 0.0f;
    }

    /**
     *
     * @return
     */
    public Duration getTotalDuration() {
        return totalDuration;
    }

    /**
     *
     * @param duration
     */
    public void setTotalDuration(Duration duration) {
        totalDuration = duration;
        percentageOfTotal = (float)sliceDuration.getSeconds() / (float)totalDuration.getSeconds() * 100.0f;
    }

    /**
     *
     * @return
     */
    public Duration getSliceDuration() {
        return sliceDuration;
    }

    /**
     *
     * @param duration
     */
    public void setSliceDuration(Duration duration) {
        sliceDuration = duration;
    }

    /**
     *
     * @return
     */
    public float getPercentageOfWind() {
        return percentageOfWind;
    }

    /**
     *
     * @param percentage
     */
    public void setPercentageOfWind(float percentage) {
        percentageOfWind = percentage;
    }

    /**
     *
     * @return
     */
    public float getPercentageOfTotal() {
        return percentageOfTotal;
    }

    /**
     *
     * @param percentage
     */
    public void setPercentageOfTotal(float percentage) {
        percentageOfTotal = percentage;
    }

    /**
     *
     * @return
     */
    public int getNumSpeedBins() {
        return binData.size();
    }

    //
    // TODO The fact that the index of the bin is passed as an argument is not good. I am not sure what the right
    // argument would be.
    //

    /**
     *
     * @param bin
     * @return
     */
    public float speedBinPercentage(int bin) {
        Duration duration = binData.get(bin).getDuration();

        if (sliceDuration.isZero())
            return 0.0f;
        else
            return ((float)duration.getSeconds() / (float)sliceDuration.getSeconds()) * 100.0F;
    }

    /**
     *
     * @param speed
     * @return
     */
    public SpeedBinDuration getSpeedBinDuration(Speed speed) {
        for (SpeedBin bin : speedBins)
            if (bin.inSpeedBin(speed))
                return binData.get(bin.getIndex());

        return null;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.date);
        hash = 29 * hash + this.headingIndex;
        hash = 29 * hash + Objects.hashCode(this.sliceDuration);
        hash = 29 * hash + Objects.hashCode(this.totalDuration);
        hash = 29 * hash + Objects.hashCode(this.windyDuration);
        hash = 29 * hash + Objects.hashCode(this.maxSpeed);
        hash = 29 * hash + Float.floatToIntBits(this.percentageOfTotal);
        hash = 29 * hash + Float.floatToIntBits(this.percentageOfWind);
        hash = 29 * hash + Objects.hashCode(this.speedBins);
        hash = 29 * hash + Objects.hashCode(this.binData);
        hash = 29 * hash + Objects.hashCode(this.avgSpeed);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        final WindSlice other = (WindSlice)obj;
        if (!Objects.equals(this.date, other.date))
            return false;

        if (this.headingIndex != other.headingIndex)
            return false;

        if (!Objects.equals(this.sliceDuration, other.sliceDuration))
            return false;

        if (!Objects.equals(this.totalDuration, other.totalDuration))
            return false;

        if (!Objects.equals(this.windyDuration, other.windyDuration))
            return false;

        if (!Objects.equals(this.maxSpeed, other.maxSpeed))
            return false;

        if (Math.abs(this.percentageOfTotal - other.percentageOfTotal) > 0.1)
            return false;

        if (Math.abs(this.percentageOfWind - other.percentageOfWind) > 0.1)
            return false;

        if (!Objects.equals(this.speedBins, other.speedBins))
            return false;

        return Objects.equals(this.binData, other.binData);
    }

    @Override
    public String toString() {
        String s = "Heading Index: " + headingIndex
                + " Windy Time: " + String.format("%6d", windyDuration.getSeconds())
                + " Max: " + maxSpeed
                + " Avg: " + avgSpeed
                + " % of Wind " + String.format("%.1f", percentageOfWind) + "%"
                + " % of Total " + String.format("%.1f", percentageOfTotal) + "% Bins: ";

        for (SpeedBinDuration bin : binData)
            s += String.format("(%d) ", bin.getDuration().getSeconds());

        return s;
    }
}
