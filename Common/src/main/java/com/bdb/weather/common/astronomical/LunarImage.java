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
package com.bdb.weather.common.astronomical;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 *
 * @author bruce
 */
public class LunarImage extends Application {

    private void drawMoon(StackPane pane) {
        ImageView imageView = new ImageView();
	Image icon = new Image("moon.jpg");
        double iconWidth = icon.getWidth();
        double moonRadius = (iconWidth / 2.0) - 9;
        final Circle clip = new Circle(iconWidth / 2.0, iconWidth / 2.0, moonRadius);
        imageView.setClip(clip);
        //Light.Distant light = new Light.Distant();
        //light.setAzimuth(0.0);
        //light.setElevation(90);
        //Lighting lighting = new Lighting();
        //lighting.setLight(light);
        //lighting.setSurfaceScale(10.0);
        //InnerShadow shadow = new InnerShadow();
        //shadow.setRadius(icon.getHeight());
        //shadow.setBlurType(BlurType.ONE_PASS_BOX);
        //Color color = new Color(.1, .1, .1, .10);
        //shadow.setColor(color);
        //shadow.setOffsetX(icon.getWidth() * .75);
        //shadow.setOffsetY(icon.getHeight() / 2.0);
        //shadow.setWidth(icon.getWidth());
        //imageView.setEffect(shadow);
        imageView.setImage(icon);
        Color color = new Color(.1, .1, .1, .90);
        Circle shadow = new Circle(iconWidth / 2.0, iconWidth / 2.0, moonRadius, color);
        shadow.setLayoutX( moonRadius / 2.0);
        shadow.setTranslateX( moonRadius / 2.0);
        final Circle clip2 = new Circle(iconWidth / 2.0, iconWidth / 2.0, moonRadius);
        shadow.setClip(clip2);
        pane.getChildren().addAll(imageView, shadow);
    }

    @Override
    public void start(Stage stage) throws Exception {
        StackPane root = new StackPane();
        drawMoon(root);
        Scene scene = new Scene(root, 800, 800);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();

    }

    public static void main(String args[]) {
	launch(args);
    }
    
}
