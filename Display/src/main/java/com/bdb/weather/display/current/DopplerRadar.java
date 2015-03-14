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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.DopplerRadarImage;
import com.bdb.weather.common.db.DopplerRadarTable;

/**
 * A Swing component that animates the series of Doppler radar images that are stored in the database.
 * 
 * @author Bruce
 *
 */
public class DopplerRadar implements ActionListener {
    private static final int DOPPLER_IMAGE_REFRESH_INTERVAL = 120000;
    private static final int ANIMATION_INTERVAL = 500;
    private static final int NUM_ANIMATION_IMAGES = 20;
    private static final int DELAY_FRAMES = 2;
    private final JComponent              component = new JPanel(new BorderLayout());
    private final List<ImageIcon>         thumbnails = new ArrayList<>();
    private List<DopplerRadarImage>       dopplerRadarImages;
    private boolean                       animate = true;
    private final Timer                   animationTimer = new Timer(ANIMATION_INTERVAL, this);
    private final Timer                   loadImageTimer = new Timer(DOPPLER_IMAGE_REFRESH_INTERVAL, this);
    private int                           animationFrame = 0;
    private final JLabel                  radarImage = new JLabel();
    private final JLabel                  frameInfo = new JLabel("", SwingConstants.CENTER);
    private final DateTimeFormatter       sdf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT);    
    private final DopplerRadarTable       dopplerRadarTable;
    
    private static final Logger    logger = Logger.getLogger(DopplerRadar.class.getName());
    
    /**
     * Constructor.
     * 
     * @param connection The connection to the database
     * @param url The URL from where the Doppler radar image should be retrieved
     */
    public DopplerRadar(DBConnection connection, URL url) {
        dopplerRadarTable = new DopplerRadarTable(connection);    
        animationTimer.start();
        loadImageTimer.start();
        radarImage.setBackground(Color.gray);
        radarImage.setBorder(new BevelBorder(BevelBorder.RAISED));
        radarImage.setIconTextGap(0);
        component.add(radarImage, BorderLayout.CENTER);
        component.add(frameInfo, BorderLayout.NORTH);
        if (url != null)
            radarImage.setToolTipText("<html><img src=" + url + "></html>");
        loadImage();
    }
    
    /**
     * Get the Swing component that contains the Doppler Radar image panel.
     * 
     * @return The Swing component containing the Doppler Radar image panel
     */
    public JComponent getComponent() {
        return component;
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

        try {
            dopplerRadarImages = dopplerRadarTable.getNewerRadarImages(NUM_ANIMATION_IMAGES);

            thumbnails.clear();

            for (DopplerRadarImage doppler : dopplerRadarImages) {
                ImageIcon thumbnail = new ImageIcon(doppler.getImage().getScaledInstance(doppler.getImage().getWidth() / 2,
                                                                                         doppler.getImage().getHeight() / 2, Image.SCALE_DEFAULT));
                thumbnails.add(thumbnail);
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
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == animationTimer) {
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
                radarImage.setIcon(thumbnails.get(animationFrame++));
                frameInfo.setText("" + animationFrame + " of " + thumbnails.size() + " (" + sdf.format(time) + ")");
            }
        }
        else if (event.getSource() == loadImageTimer)
            loadImage();
    }
}
