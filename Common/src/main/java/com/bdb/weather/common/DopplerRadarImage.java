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

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

public class DopplerRadarImage {
    private final LocalDateTime time;
    private final BufferedImage image;
    
    /**
     * Constructor.
     * 
     * @param time Time the Doppler radar image was retrieved
     * @param image The Doppler radar image
     */
    public DopplerRadarImage(LocalDateTime time, BufferedImage image) {
        this.time = time;
        this.image = image;
    }
    
    /**
     * @return the time
     */
    public LocalDateTime getTime() {
        return time;
    }

    /**
     * @return the image
     */
    public BufferedImage getImage() {
        return image;
    }
}
