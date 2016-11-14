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
#include "UnitConverter.h"

namespace vp2 {
UnitConverter::UnitConverter() {
}

UnitConverter::~UnitConverter() {
}

Temperature
UnitConverter::toCelcius(Temperature temperature) {
    return (temperature - 32.0F) / 1.8F;
}

Rainfall
UnitConverter::toMillimeter(Rainfall depth) {
    return depth * 25.4F;
}

Pressure
UnitConverter::toMillibars(Pressure pressure) {
    return pressure * 33.86386F;
}

Speed
UnitConverter::toMetersPerSecond(Speed speed) {
    return speed / 2.23693F;
}

double
UnitConverter::feetToMillimeters(double elevation) {
    return elevation * 304.8F;
}
}