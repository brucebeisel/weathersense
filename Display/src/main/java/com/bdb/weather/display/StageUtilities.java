/*
 * Copyright (C) 2016 bruce
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

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author bruce
 */
public abstract class StageUtilities {

    public static void setStageTitle(Node node, String title) {
        Window window = node.getScene().getWindow();
        if (window instanceof Stage) {
            ((Stage) window).setTitle(title);
        }
    }

    public static String getStageTitle(Node node) {
        Window window = node.getScene().getWindow();
        if (window instanceof Stage) {
            return ((Stage) window).getTitle();
        } else {
            return "";
        }
    }

    public static void sizeStageToScene(Node node) {
        Window window = node.getScene().getWindow();
        if (window instanceof Stage) {
            ((Stage) window).sizeToScene();
        }
    }

    public static java.awt.Color toAwtColor(Color fxColor) {
        java.awt.Color awtColor = new java.awt.Color((float)fxColor.getRed(), (float)fxColor.getGreen(), (float)fxColor.getBlue(), (float)fxColor.getOpacity());
        return awtColor;
    } 

    
}
