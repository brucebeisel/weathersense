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

import com.bdb.weather.common.measurement.Speed;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Bruce
 */
public final class WindParameters {
    private final Speed          windSpeedBinInterval;
    private final int            numWindSpeedBins;
    private final int            numWindDirectionSlices;
    private final List<SpeedBin> speedBinList;
    
    public WindParameters() {
        this(new Speed(5.0, Speed.Unit.MILES_PER_HOUR), 5, 16);
    }
    
    public WindParameters(Speed interval, int binCount, int windSliceCount) {
        windSpeedBinInterval = interval;
        numWindSpeedBins = binCount;
        numWindDirectionSlices = windSliceCount;
        speedBinList = createSpeedBinList();
    }
    
    public Speed getWindSpeedBinInterval() {
        return windSpeedBinInterval;
    }
    
    public int getNumWindSpeedBins() {
        return numWindSpeedBins;
    }
    
    public int getNumWindDirectionSlices() {
        return numWindDirectionSlices;
    }
    
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
