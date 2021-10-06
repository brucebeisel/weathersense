/* 
 * Copyright (C) 2021 Bruce Beisel
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
#include <time.h>
#include <vector>
#include <algorithm>
#include "WindDirectionSlices.h"

using namespace std;

namespace vp2 {

const Heading WindDirectionSlices::DEGREES_PER_SLICE = static_cast<Heading>(22.5);
const Heading WindDirectionSlices::HALF_SLICE = DEGREES_PER_SLICE / static_cast<Heading>(2.0);

WindDirectionSlices::WindDirectionSlices() {
    Heading heading = -HALF_SLICE;
    for (int i = 0; i < NUM_SLICES; i++) {
        windSlices[i].setValues(i, heading, heading + DEGREES_PER_SLICE);
        heading += DEGREES_PER_SLICE;
    }
}

WindDirectionSlices::~WindDirectionSlices() {
}

void
WindDirectionSlices::addHeading(Heading heading) {
    if (heading > 360.0 - HALF_SLICE)
        heading -= static_cast<Heading>(360.0);

    for (int i = 0; i < NUM_SLICES; i++) {
        if (windSlices[i].isInSlice(heading)) {
            windSlices[i].addSample(time(0));
        }
    }

    //
    // Get rid of the old samples
    //
    DateTime now = time(0);
    DateTime before = now - AGE_SPAN;

    for (int i = 0; i < NUM_SLICES; i++) {
        windSlices[i].removeOldSamples(before);
 
    }

    //
    // Sort the array based on the number of samples
    //
    std::sort(windSlices, &windSlices[NUM_SLICES]);
}

/// <summary>
/// Get the list of past headings that have the most number of samples
/// </summary>
/// <returns>The list of past headings, up to a maximum of 4</returns>
void
WindDirectionSlices::pastHeadings(vector<int> & headings) const {
    //
    // Pull out the 4 with the highest number of samples
    //
    headings.clear();
    for (int i = NUM_SLICES - 1; i >= NUM_SLICES - MAX_PAST_HEADINGS; i--) {
        if (windSlices[i].getSampleSize() > 0)
            headings.push_back((int)windSlices[i].getCenter());
    }
}
}
