/*
 * Copyright (C) 2017 bruce
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

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.weather.common.DopplerRadarImage;
import com.bdb.weather.common.db.DatabaseConstants;
import com.bdb.weather.common.db.DopplerRadarTable;
import com.bdb.weather.display.current.DopplerRadar;

/**
 *
 * @author bruce
 */
public class DopplerRadarAnimationTest extends Application {
    private final List<ImageView>           thumbnails = new ArrayList<>();
    private boolean                         animate = true;
    private int                             animationFrame = 0;
    private final Group                     radarImage = new Group();
    private final Label                     frameInfo = new Label();
    private final DateTimeFormatter         sdf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT);    
    private DopplerRadarTable               dopplerRadarTable = null;
    private final ScheduledExecutorService  executor = Executors.newSingleThreadScheduledExecutor();
    private WebEngine                       webEngine;
    private String databaseUrl;
    private DBConnection connection;
    private static final Logger             logger = Logger.getLogger(DopplerRadar.class.getName());

    public void loadImages() {
        try {
            logger.fine("Loading doppler radar images");


            List<DopplerRadarImage> list = dopplerRadarTable.query(null);

            for (DopplerRadarImage doppler : list) {
                doppler.getImage().flush();
            }

            for (int i = 0; i < list.size(); i++)
                list.set(i, null);

            list.clear();

            /*
            for (DopplerRadarImage doppler : dopplerRadarImages) {
                WritableImage thumbnail = SwingFXUtils.toFXImage(doppler.getImage(), null);
                doppler.getImage().flush();
                ImageView iv = new ImageView(thumbnail);
                iv.setFitWidth(doppler.getImage().getWidth() / 2);
                iv.setFitHeight(doppler.getImage().getHeight() / 2);
                iv.setPreserveRatio(true);
                iv.setSmooth(true);
                thumbnails.add(iv);
            }
*/

            logger.log(Level.FINE, "Currently animating %1 thumbnails", thumbnails.size());
        }
        catch (Exception e) {
            logger.log(Level.WARNING, "Caught exception while loading Doppler radar image", e);
        }
    }

    private void openDatabase() {
	String databaseHost;

        databaseHost = "192.168.1.102";

        databaseUrl = String.format(DatabaseConstants.DATABASE_URL_FORMATTER, DatabaseConstants.DATABASE_SERVER, databaseHost, DatabaseConstants.DATABASE_PORT, DatabaseConstants.DATABASE_NAME);

        connection = new DBConnection(databaseUrl, DatabaseConstants.DATABASE_USER, DatabaseConstants.DATABASE_PASSWORD);

        connection.connect();

        dopplerRadarTable = new DopplerRadarTable(connection);
    }

    @Override
    public void start(Stage stage) throws Exception {
        openDatabase();
        for (int i = 0; i < 1000000; i++) {
            System.out.println("Pass " + i);
            loadImages();
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(DopplerRadarAnimationTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    public static void main(String args[]) {
        launch();
    }
    
}
