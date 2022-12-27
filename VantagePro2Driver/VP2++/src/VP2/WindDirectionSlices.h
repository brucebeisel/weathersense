/* 
 * Copyright (C) 2022 Bruce Beisel
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
#ifndef WIND_DIRECTION_SLICES_H
#define WIND_DIRECTION_SLICES_H

#include <vector>
#include "Weather.h"
#include "WindSlice.h"

namespace vp2 {
/**
 * Class that keeps track of the most recent wind direction tendencies.
 */
class WindDirectionSlices {
public:
    /**
     * Constructor.
     */
    WindDirectionSlices();

    /**
     * Destructor.
     */
    virtual ~WindDirectionSlices();

    /**
     * Add a heading to the tendencies
     * 
     * @param heading The direction of the non-zero speed wind
     */
    void addHeading(Heading heading);

    /**
     * Process a wind sample with a zero speed.
     */
    void processCalmWindSample();


    /**
     * Return the past heading tendencies.
     * 
     * @param headings The directions that the wind has been blowing most often in the last hour
     */
    void pastHeadings(std::vector<int> & headings) const;

private:
    void removeOldSamples(DateTime time);
    void find10MinuteDominantWindDirection(DateTime time);

    static constexpr Heading MAX_HEADING = 360.0;

    /**
     * Each wind slice (N, NNE, NE...) will be tracked for direction tendency
     */
    static const int NUM_SLICES = 16;

    static const std::string SLICE_NAMES[NUM_SLICES];

    /**
     * The number of degrees each wind slice occupies.
     */
    static constexpr Heading DEGREES_PER_SLICE = 22.0;
    static constexpr Heading HALF_SLICE = DEGREES_PER_SLICE / 2.0;

    /**
     * The wind over the past 10 minutes is used to determine the direction tendencies.
     */
    static const int AGE_SPAN = 10 * 60;

    /**
     * The number of samples needed to be part of the direction tendencies
     */
    static const int SECONDS_PER_SAMPLE = 4;

    /**
     * Number of directions that are reported to the collector.
     */
    static const int MAX_PAST_HEADINGS = 4;

    static const int HOUR_SECONDS = 3600;

    int       totalSamples;
    WindSlice windSlices[NUM_SLICES];
};
}

#endif /* WIND_DIRECTION_SLICES_H */
