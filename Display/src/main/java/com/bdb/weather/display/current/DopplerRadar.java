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
package com.bdb.weather.display.current;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;

import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.DopplerRadarImage;
import com.bdb.weather.common.db.DopplerRadarTable;

/**
 * A Swing component that animates the series of Doppler radar images that are stored in the database.
 * 
 * @author Bruce
 *
 */
public class DopplerRadar extends BorderPane {
    private static final int DOPPLER_IMAGE_REFRESH_INTERVAL = 120000;
    private static final int ANIMATION_INTERVAL = 500;
    private static final int NUM_ANIMATION_IMAGES = 20;
    private static final int DELAY_FRAMES = 2;
    private final List<ImageView>         thumbnails = new ArrayList<>();
    private List<DopplerRadarImage>       dopplerRadarImages;
    private boolean                       animate = true;
//    private final Timer                   loadImageTimer = new Timer(DOPPLER_IMAGE_REFRESH_INTERVAL, this);
    private int                           animationFrame = 0;
    private final Label                   radarImage = new Label();
    private final Label                   frameInfo = new Label();
    private final DateTimeFormatter       sdf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT);    
    private DopplerRadarTable             dopplerRadarTable = null;
    
    private static final Logger           logger = Logger.getLogger(DopplerRadar.class.getName());
    
    public DopplerRadar() {
	this(null, null);
    }

    /**
     * Constructor.
     * 
     * @param connection The connection to the database
     * @param url The URL from where the Doppler radar image should be retrieved
     */
    public DopplerRadar(DBConnection connection, URL url) {
        frameInfo.setTextAlignment(TextAlignment.CENTER);
        //loadImageTimer.start();
        //radarImage.setBackground(Color.gray);
        //radarImage.setBorder(new BevelBorder(BevelBorder.RAISED));
        radarImage.setGraphicTextGap(0);
        this.setCenter(radarImage);
        BorderPane.setAlignment(frameInfo, Pos.CENTER);
        this.setTop(frameInfo);
        if (url != null)
            setTooltip(url.toString());

        loadImage();

	Timeline timeline = new Timeline(
	    new KeyFrame(Duration.ZERO, (actionEvent) -> nextFrame()),
	    new KeyFrame(Duration.millis(ANIMATION_INTERVAL))
	);

	timeline.setCycleCount(Timeline.INDEFINITE);
	timeline.play();
    }

    private void setTooltip(String url) {
        if (url != null) {
            WebView  web = new WebView();
            WebEngine webEngine = web.getEngine();
            webEngine.loadContent("<html><img src=" + url + "></html>");
            Tooltip  tip = new Tooltip();
            tip.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            tip.setGraphic(web);
            radarImage.setTooltip(tip);
        }
    }

    public void configure(DBConnection connection, String url) {
        setTooltip(url);
        dopplerRadarTable = new DopplerRadarTable(connection);    
	loadImage();
    }
    
    /**
     * Returns whether the Doppler Radar images are being animated.
     * 
     * @return The animating state
     */
    public boolean isAnimating() {
        return animate;
    }
    
    /**
     * Enable or disable the animating of the Doppler radar images
     * 
     * @param animate Whether to animate or not
     */
    public void setAnimate(boolean animate) {
        this.animate = animate;
    }
    
    /**
     * Load the Doppler radar images from the database.
     */
    private void loadImage() {
        logger.fine("Loading doppler radar images");
	if (dopplerRadarTable == null)
	    return;

        try {
            dopplerRadarImages = dopplerRadarTable.getNewerRadarImages(NUM_ANIMATION_IMAGES);

            thumbnails.clear();

            for (DopplerRadarImage doppler : dopplerRadarImages) {
                WritableImage thumbnail = SwingFXUtils.toFXImage(doppler.getImage(), null);
                ImageView iv = new ImageView(thumbnail);
                iv.setFitWidth(doppler.getImage().getWidth() / 2);
                iv.setFitHeight(doppler.getImage().getHeight() / 2);
                iv.setPreserveRatio(true);
                iv.setSmooth(true);
                thumbnails.add(iv);
            }

            logger.fine("Currently animating " + thumbnails.size() + " thumbnails");
        }
        catch (Exception e) {
            logger.log(Level.WARNING, "Caught exception while loading Doppler radar image", e);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    private void nextFrame() {
	if (!animate)
	    return;

	//
	// If the extra delay frames have passed then go back to the beginning of the loop
	//
	if (animationFrame >= thumbnails.size() + DELAY_FRAMES)
	    animationFrame = 0;
	else if (animationFrame >= thumbnails.size())
	    animationFrame++;
	else {
	    //
	    // Load the radar image and the date/time text
	    //
	    LocalDateTime time = dopplerRadarImages.get(animationFrame).getTime();
	    radarImage.setGraphic(thumbnails.get(animationFrame++));
	    frameInfo.setText("" + animationFrame + " of " + thumbnails.size() + " (" + sdf.format(time) + ")");
	}
        //else if (event.getSource() == loadImageTimer)
        //    loadImage();
    }
}
