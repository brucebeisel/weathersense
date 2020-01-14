/* 
 * Copyright (C) 2020 Bruce Beisel
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
#ifndef WIND_SLICE_H
#define	WIND_SLICE_H

#include <vector>
#include <iostream>
#include "Weather.h"

namespace vp2 {
/**
 * Class that keeps track of the number of wind samples that have occured in a given slice of wind direction.
 */
class WindSlice {
public:
    /**
     * Default constructor used in array declarations.
     */
    WindSlice();

    /**
     * Constructor.
     * 
     * @param slice The slice number from 1 - max
     * @param low The lowest direction in this slice (exclusive)
     * @param high The highest direction in this slice (inclusive)
     */
    WindSlice(int slice, Heading low, Heading high);

    /**
     * Destructor.
     */
    virtual ~WindSlice();

    /**
     * Set the values that describe the slice.
     * 
     * @param slice The slice number from 1 - max
     * @param low The lowest direction in this slice (exclusive)
     * @param high The highest direction in this slice (inclusive)
     */
    void setValues(int slice, Heading low, Heading high);

    /**
     * Get the heading at the center of this slice.
     * 
     * @return The center heading
     */
    Heading getCenter() const;

    /**
     * Check if a heading is in the wind slice.
     * 
     * @param heading The heading to check
     * @return  True if the heading is within this slice
     */
    bool isInSlice(Heading heading) const;

    /**
     * Remove any wind samples that are before the specified time.
     * 
     * @param time The time
     */
    void removeOldSamples(DateTime time);

    /**
     * Add a sample to this slice.
     * A sample is nothing more than a time that the wind was blowing within this slice.
     * 
     * @param time The time of the wind sample
     */
    void addSample(DateTime time);

    /**
     * Get the number of samples currently stored by this slice.
     * 
     * @return The sample size
     */
    int getSampleSize() const;

    /**
     * Less than operator needed for sorting.
     */
    friend bool operator<(const WindSlice & lhs, const WindSlice & rhs);

    /**
     * ostream operator for output.
     */
    friend std::ostream & operator<<(std::ostream &, const WindSlice &);

private:
    int slice;
    Heading lowHeading;
    Heading highHeading;
    std::vector<DateTime> samples;
};
}

#endif	/* WIND_SLICE_H */
