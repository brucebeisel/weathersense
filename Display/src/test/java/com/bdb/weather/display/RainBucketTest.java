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

import java.text.DecimalFormat;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

/**
 *
 * @author Bruce Beisel
 */
public class RainBucketTest extends Application {
    private static RainBucket b;

    @Override
    public final void start(Stage stage) {
        BorderPane pane = new BorderPane();
        TilePane tile = new TilePane();
        pane.setCenter(tile);
        Scene scene = new Scene(pane);

        RainBucket btmp = null;

        for (int i = 0; i < 5; i++) {
            btmp = new RainBucket(10.0, new DecimalFormat("0.0"), "\"", 8.0, 5.0);
            final Double amount = 6.0;
            btmp.setRainfallAmount(amount);
            tile.getChildren().add(btmp);
        }

        b = btmp;

        tile.setVisible(true);
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String args[]) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if (b == null)
                                    return;
                                b.setRainfallAmount(b.getRainfallAmount() + .1);
                                if (b.getRainfallAmount() >= 12.0)
                                    b.setRainfallAmount(0.0);
                            }
                        });

                        Thread.sleep(1000);
                    }
                    catch (Exception e) {
                    }
                }
            }
        });
        t.start();

        launch();
    }
    
}
