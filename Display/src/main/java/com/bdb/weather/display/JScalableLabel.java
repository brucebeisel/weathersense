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
package com.bdb.weather.display;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class JScalableLabel extends Label {
    /**
     * Constructor.
     */
    public JScalableLabel() {
        this("", null);
    }

    /**
     * @param icon
     */
    public JScalableLabel(ImageView icon) {
        this("", icon);
    }

    /**
     * @param text
     */
    public JScalableLabel(String text) {
        this(text, null);
    }

    /**
     * @param text
     * @param icon
     */
    public JScalableLabel(String text, ImageView icon) {
        super(text, icon);
        this.widthProperty().addListener((listener) -> setScaling());
        this.heightProperty().addListener((listener) -> setScaling());
    }



    private void setScaling() {
        //
        // If the Node has been resized smaller than the preferred size, then scale
        //
        //if (actualSize.width < preferredSize.width || actualSize.height < preferredSize.height)
        //{
        double wscale = this.getWidth() / this.getPrefWidth();
        double hscale = this.getHeight() / this.getPrefHeight();
        double scale = Math.min(wscale, hscale);
        this.setScaleX(scale);
        this.setScaleY(scale);
    }
}