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
#ifndef CURRENTWEATHER_H
#define	CURRENTWEATHER_H

#include <string>
#include "LoopPacket.h"
#include "Loop2Packet.h"
#include "WindDirectionSlices.h"

namespace vp2 {
/**
 * Class that contains the data needed to create a current weather message. The Vantage Pro 2 has two packets that report the
 * current weather, the LOOP packet and the LOOP2 packet. This class combines those packets together with a set of wind directions
 * that mimic the recent wind direction indicators on the Vantage Pro 2 console.
 */
class CurrentWeather {
public:
    /**
     * Constructor.
     * 
     * @param loopPacket The LOOP packet that was read in the most recent loop through the current weather processor
     * @param loop2Packet The LOOP2 packet that was read in the most recent loop through the current weather processor
     * @param The dominant directions that the wind has been blowing over the last hour
     */
    CurrentWeather(const LoopPacket & loopPacket, const Loop2Packet & loop2Packet, WindDirectionSlices & windDirectionSlices);

    /**
     * Destuctor.
     */
    virtual ~CurrentWeather();

    /**
     * Get the next packet field that was extracted from the LOOP packet.
     * 
     * @return The next packet field
     */
    int getNextPacket() const;

    /**
     * Format the Current Weather message to tbe sent to the collector.
     * 
     * @return The Current Weather message
     */
    std::string formatMessage() const;

private:
    LoopPacket loopPacket;
    Loop2Packet loop2Packet;
    WindDirectionSlices pastWindDirs;
};
}

#endif	/* CURRENTWEATHER_H */