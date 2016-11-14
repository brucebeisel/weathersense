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

import com.bdb.weather.common.measurement.Speed;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Container class that holds various parameters regarding local wind conditions.
 *
 * @author Bruce
 */
public final class WindParameters {
    private final Speed          windSpeedBinInterval;
    private final int            numWindSpeedBins;
    private final int            numWindDirectionSlices;
    private final List<SpeedBin> speedBinList;
    
    /**
     * Constructor.
     */
    public WindParameters() {
        this(new Speed(5.0, Speed.Unit.MILES_PER_HOUR), 5, 16);
    }
    
    /**
     * Constructor.
     *
     * @param interval The interval of the speed bins
     * @param binCount The number of speed bins
     * @param windSliceCount The number of wind direction slices
     */
    public WindParameters(Speed interval, int binCount, int windSliceCount) {
        windSpeedBinInterval = interval;
        numWindSpeedBins = binCount;
        numWindDirectionSlices = windSliceCount;
        speedBinList = createSpeedBinList();
    }
    
    /**
     * Get the wind speed bin interval.
     *
     * @return The interval
     */
    public Speed getWindSpeedBinInterval() {
        return windSpeedBinInterval;
    }
    
    /**
     * Get the number of wind speed bins.
     *
     * @return The number of wind speed bins
     */
    public int getNumWindSpeedBins() {
        return numWindSpeedBins;
    }
    
    /**
     * Get the number of wind direction slices.
     *
     * @return The number of wind direction slices
     */
    public int getNumWindDirectionSlices() {
        return numWindDirectionSlices;
    }
    
    /**
     * Get the list of wind speed bins.
     *
     * @return The list of wind speed bins
     */
    public List<SpeedBin> getSpeedBinList() {
        return Collections.unmodifiableList(speedBinList);
    }
    
    private List<SpeedBin> createSpeedBinList() {
        List<SpeedBin> list = new ArrayList<>(numWindSpeedBins);
        Speed minSpeed = SpeedBin.MIN_SPEED;
        Speed maxSpeed = windSpeedBinInterval;
        
        for (int i = 0; i < numWindSpeedBins - 1; i++) {
            list.add(new SpeedBin(i, minSpeed, maxSpeed));
            minSpeed = (Speed)minSpeed.add(windSpeedBinInterval);
            maxSpeed = (Speed)maxSpeed.add(windSpeedBinInterval);
        }
        
        list.add(new SpeedBin(numWindSpeedBins - 1, minSpeed, SpeedBin.MAX_SPEED));
        
        return list;
    }
}