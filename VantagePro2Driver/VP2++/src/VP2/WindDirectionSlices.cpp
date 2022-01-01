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
#include <time.h>
#include <cstring>
#include <vector>
#include <algorithm>
#include <iomanip>
#include "WindDirectionSlices.h"

using namespace std;

namespace vp2 {

//const Heading WindDirectionSlices::DEGREES_PER_SLICE = static_cast<Heading>(22.5);
//const Heading WindDirectionSlices::HALF_SLICE = DEGREES_PER_SLICE / static_cast<Heading>(2.0);

const std::string WindDirectionSlices::SLICE_NAMES[NUM_SLICES] = {
    "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"
};

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
WindDirectionSlices::WindDirectionSlices() : totalSamples(0) {
    Heading heading = -HALF_SLICE;
    for (int i = 0; i < NUM_SLICES; i++) {
        windSlices[i].setValues(i, SLICE_NAMES[i], heading, heading + DEGREES_PER_SLICE);
        heading += DEGREES_PER_SLICE;
    }
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
WindDirectionSlices::~WindDirectionSlices() {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
WindDirectionSlices::addHeading(Heading heading) {
    DateTime now = time(0);

    if (heading > MAX_HEADING - HALF_SLICE)
        heading -= static_cast<Heading>(MAX_HEADING);

    removeOldSamples(now);

    totalSamples = 0;
    for (int i = 0; i < NUM_SLICES; i++) {
        if (windSlices[i].isInSlice(heading)) {
            windSlices[i].addSample(now);
        }
        totalSamples += windSlices[i].getSampleSize();
    }

    //
    // Only find the dominant wind direction if enough wind samples have been collected
    //
    if (totalSamples > (AGE_SPAN / 2) / SECONDS_PER_SAMPLE)
        find10MinuteDominantWindDirection(now);

    //
    // Sort the array based on the last 10 minute dominant time
    //
    std::sort(windSlices, &windSlices[NUM_SLICES]);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
WindDirectionSlices::find10MinuteDominantWindDirection(DateTime now) {
    int highSampleCount = 0;
    int highCountIndex = -1;
    for (int i = 0; i < NUM_SLICES; i++) {
        if (windSlices[i].getSampleSize() > highSampleCount) {
            highSampleCount = windSlices[i].getSampleSize();
            highCountIndex = i;
        }

        if (now - windSlices[i].getLast10MinuteDominantTime() > HOUR_SECONDS)
            windSlices[i].setLast10MinuteDominantTime(0);
    }

    if (highCountIndex != -1)
        windSlices[highCountIndex].setLast10MinuteDominantTime(now);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
WindDirectionSlices::processCalmWindSample() {
    removeOldSamples(time(0));
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
WindDirectionSlices::removeOldSamples(DateTime now) {
    //
    // Get rid of the old samples
    //
    DateTime before = now - AGE_SPAN;

    for (int i = 0; i < NUM_SLICES; i++) {
        windSlices[i].removeOldSamples(before);
    }
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
WindDirectionSlices::pastHeadings(vector<int> & headings) const {
    cout << "Wind Direction Slices: Samples: " << totalSamples << endl;
    for (int i = 0; i < NUM_SLICES; i++) { 
        char buffer[100];
        DateTime dtime = windSlices[i].getLast10MinuteDominantTime();
        if (dtime > 0) {
            struct tm tm;
            Weather::localtime(dtime, tm);
            strftime(buffer, sizeof(buffer), "%H:%M:%S", &tm);
        }
        else
            strcpy(buffer, "Never");

        cout << "Direction: " << setw(3) << windSlices[i].getName() << " (" << setw(5) << windSlices[i].getCenter()
             << ") Count: " << setw(3) << windSlices[i].getSampleSize() << " Last Dominant Time: " << setw(8) << buffer
             << " (" << windSlices[i].getSampleSizeAtDominantTime() << ")"
             << endl;
    }

    //
    // Pull out the 4 that have been dominant most recently in the last hour
    //
    headings.clear();
    for (int i = 0; i < MAX_PAST_HEADINGS; i++) {
        if (windSlices[i].getLast10MinuteDominantTime() != 0)
            headings.push_back(static_cast<int>(windSlices[i].getCenter()));
    }
}
}
