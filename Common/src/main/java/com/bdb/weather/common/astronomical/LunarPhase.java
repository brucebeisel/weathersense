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
package com.bdb.weather.common.astronomical;

public enum LunarPhase {
    NEW_MOON(0.0, .125, true, true),
    WAXING_CRESENT(.125, .375, true, false),
    FIRST_QUARTER(.375, .625, true, false),
    WAXING_GIBBOUS(.625, .875, true, false),
    FULL_MOON(.875, 1.0, true, true),
    WANING_GIBBOUS(.625, .875, false, true),
    LAST_QUARTER(.375, .625, false, true),
    WANING_CRESENT(.125, .375, false, true);

    private final double minIllumination;
    private final double maxIllumination;
    private final boolean waxing;
    private final boolean waning;

    private LunarPhase(double minIllumination, double maxIllumination, boolean waxing, boolean waning) {
        this.minIllumination = minIllumination;
        this.maxIllumination = maxIllumination;
        this.waxing = waxing;
        this.waning = waning;
    }

    public static LunarPhase whichPhase(double illumination, boolean waxing) {
        LunarPhase rv = NEW_MOON;
        for (LunarPhase phase : LunarPhase.values()) {
            if (illumination > phase.minIllumination && illumination <= phase.maxIllumination &&
                    (waxing == phase.waxing || !waxing == phase.waning)) {
                rv = phase;
                break;
            }
        }

        return rv;
    }
}