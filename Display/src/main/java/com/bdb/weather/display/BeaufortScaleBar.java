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

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;

import com.bdb.weather.common.BeaufortScale;

public class BeaufortScaleBar extends TilePane {
    
    //private JProgressBar bar = new JProgressBar(0, BeaufortScale.MAX_FORCE);
    private final Slider bar = new Slider(0, 12, 5);

    public BeaufortScaleBar() {
        super(2, 0);
 
        bar.setShowTickMarks(true);
        bar.setShowTickLabels(true);
        bar.setMajorTickUnit(1);
        bar.setSnapToTicks(true);
        
        this.getChildren().add(bar);
        
        BorderPane scalePanel = new BorderPane();
        scalePanel.setLeft(new Label("0"));
        scalePanel.setRight(new Label("12"));
        TilePane innerPanel = new TilePane();
        innerPanel.setPrefRows(1);
        for (int i = 1; i < BeaufortScale.MAX_FORCE; i++) {
            Label label = new Label("" + i);
            //label.setBorder(LineBorder.createBlackLineBorder());
            label.setAlignment(Pos.CENTER);
            innerPanel.getChildren().add(label);
        }
        scalePanel.setCenter(innerPanel);
        
        this.getChildren().add(scalePanel);
    }
}