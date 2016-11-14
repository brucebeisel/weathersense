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
#include "Weather.h"
#include "WindSlice.h"

using namespace std;

namespace vp2 {

WindSlice::WindSlice() {
    slice = 0;
    lowHeading = static_cast<Heading>(0.0);
    highHeading = static_cast<Heading>(0.0);
}

WindSlice::WindSlice(int slice, Heading low, Heading high) : slice(slice), lowHeading(low), highHeading(high){
}

WindSlice::~WindSlice() {
}

void
WindSlice::setValues(int slice, Heading low, Heading high) {
    this->slice = slice;
    this->lowHeading = low;
    this->highHeading = high;
}

Heading
WindSlice::getCenter() const {
    return lowHeading + ((highHeading - lowHeading) / static_cast<Heading>(2.0));
}

bool
WindSlice::isInSlice(Heading heading) const {
    return heading > lowHeading && heading <= highHeading;
}

void
WindSlice::addSample(DateTime time) {
    samples.insert(samples.begin(), time);
}

void
WindSlice::removeOldSamples(DateTime time) {
   for (vector<DateTime>::iterator it = samples.begin(); it != samples.end(); ++it) {
        if (*it <= time) {
            samples.erase(it, samples.end());
            break;
        }
    }
}

int
WindSlice::getSampleSize() const {
    return samples.size();
}

bool
operator<(const WindSlice & x, const WindSlice & y) {
    int sizex = x.samples.size();
    int sizey = y.samples.size();
    if (sizey != sizex)
        return sizex < sizey;
    else if (sizex == 0)
        return false;
    else
        return x.samples.at(0) < y.samples.at(0);
}

ostream & operator<<(ostream & os, const WindSlice & slice) {
    os << "Low: " << slice.lowHeading << " High: " << slice.highHeading << " ";
    for (vector<DateTime>::const_iterator it = slice.samples.begin(); it != slice.samples.end(); ++it) {
        os << *it << " ";
    }
    os << endl;

    return os;
}
}