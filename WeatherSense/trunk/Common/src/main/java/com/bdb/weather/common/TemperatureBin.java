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
package com.bdb.weather.common;

import com.bdb.weather.common.measurement.Temperature;
import java.util.Objects;

public class TemperatureBin implements Comparable<TemperatureBin> {
    private final int           binId;
    private final ThresholdType thresholdType;
    private final Temperature   threshold;
    
    public TemperatureBin(int binId, ThresholdType thresholdType, Temperature threshold) {
        this.binId = binId;
        this.thresholdType = thresholdType;
        this.threshold = threshold;
    }
    
    /**
     * @return The ID of the bin
     */
    public int getBinId() {
        return binId;
    }

    /**
     * @return the thresholdType
     */
    public ThresholdType getThresholdType() {
        return thresholdType;
    }

    /**
     * @return the threshold
     */
    public Temperature getThreshold() {
        return threshold;
    }
    
    public boolean isInBin(Temperature t) {
        if (thresholdType == ThresholdType.BELOW_THRESHOLD && t.compareTo(threshold) < 0)
            return true;
            
        return thresholdType == ThresholdType.ABOVE_THRESHOLD && t.compareTo(threshold) >= 0;
    }

    public boolean isSameBin(TemperatureBin other) {
        return thresholdType == other.thresholdType && threshold.equals(other.threshold);
    }
    
    @Override
    public String toString() {
        return "ID=" + binId + "  Type=" + thresholdType + "  Threshold=" + threshold + Temperature.getDefaultUnit();
    }
    
    @Override
    public int compareTo(TemperatureBin other) {
        if (binId == other.binId)
            return 0;

        if (thresholdType != other.thresholdType) {
            if (thresholdType == ThresholdType.BELOW_THRESHOLD)
                return -1;
            else
                return 1;
        }
        
        return threshold.compareTo(other.threshold);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.thresholdType);
        hash = 19 * hash + Objects.hashCode(this.threshold);
        hash = 19 * hash + binId;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        final TemperatureBin other = (TemperatureBin)obj;
        return binId == other.binId;
    }
}
