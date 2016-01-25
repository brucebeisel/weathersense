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

import java.time.LocalDateTime;

import javafx.scene.image.Image;

/**
 * Class that holds a Doppler Radar Image
 * 
 * @author bruce
 */
public class DopplerRadarImage {
    private final LocalDateTime time;
    private final Image image;
    
    /**
     * Constructor.
     * 
     * @param time The time the image was received
     * @param image The image
     */
    public DopplerRadarImage(LocalDateTime time, Image image) {
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
    public Image getImage() {
        return image;
    }
}
