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
package com.bdb.weather.collector;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.bdb.weather.common.DopplerRadarImage;

/**
 * Retrieve a Doppler radar image from the Internet and store the image in the database. It will also
 * manage the size of the table.
 * 
 * @author Bruce
 * @since 1.0
 */
public class DopplerRadarManager {
    private static final int IMAGE_CACHE_SIZE = 5;
    private static final int DOPPLER_RADAR_UPDATE_INTERVAL = 300000;
    
    private final ScheduledExecutorService executor;
    private URL                           imageUrl;
    private final WeatherDataWriter       writer;
    private final List<DopplerRadarImage> images;
    private final long                    interval;
    private static final Logger           logger = Logger.getLogger(DopplerRadarManager.class.getName());
    
    /**
     * Constructor that will load the newest images into a buffer
     * 
     * @param url The URL string that points to a Doppler radar image
     * @param writer The database writer to send the image
     * @param imageCheckInterval How often to check to see if the Doppler Radar images has changed (milliseconds)
     * 
     */
    public DopplerRadarManager(URL url, WeatherDataWriter writer, long imageCheckInterval) {
        interval = imageCheckInterval;
        imageUrl = url;
        this.writer = writer;
        images = new ArrayList<>();
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.execute(()->{
            Thread.currentThread().setName("DopplerRadarManager");
        });
        executor.scheduleAtFixedRate(() -> {
            checkForNewRadarImage();
        }, 0, interval, TimeUnit.MILLISECONDS);
    }

    /**
     * Constructor that will load the newest images into a buffer
     * 
     * @param url The URL string that points to a Doppler radar image
     * @param writer The database writer to send the image
     * 
     */
    public DopplerRadarManager(URL url, WeatherDataWriter writer) {
        this(url, writer, DOPPLER_RADAR_UPDATE_INTERVAL);
    }

    /**
     * Constructor that does not take a URL
     * 
     * @param writer The database writer
     */
    public DopplerRadarManager(WeatherDataWriter writer) {
        this(null, writer);
    }
    
    /**
     * Shutdown this thread.
     */
    public void terminate() {
        executor.shutdownNow();
    }

    /**
     * Set the URL of where the Doppler Radar image is on the Internet
     * 
     * @param url The URL
     */
    public void setDopplerUrl(URL url) {
        imageUrl = url;
    }
    
    /**
     * Check the Internet for a new radar image. Since the radar images do not change that often
     * this method looks for an image that changed.
     * 
     * @throws SQLException See JDBC documentation
     */
    private void checkForNewRadarImage() {
        logger.log(Level.FINE, "Checking for new Doppler Radar image");
        if (imageUrl == null)
            return;

        DopplerRadarImage dri;

        try {
            //
            // Get the image from the Internet
            //
            BufferedImage image = ImageIO.read(imageUrl);

            if (image == null)
                return;

            //
            // If the image has changed then add the image to the database, else ignore it
            //
            if (checkForImageChange(image)) {
                dri = new DopplerRadarImage(LocalDateTime.now(), image);


                if (images.size() == IMAGE_CACHE_SIZE) {
                    images.set(0, null);
                    images.remove(0);
                }

                images.add(dri);
                writer.addDopplerRadarImage(dri);
            }
            else
                logger.fine("Radar image has not changed");
        }
        catch (IOException e) {
            logger.log(Level.WARNING, "Caught exception", e);
            logger.log(Level.WARNING, "Failed to read doppler image from internet or write to database. URL = {}", imageUrl);
        }
    }
    
    /**
     * Check the latest downloaded image against a cache of images. The images are cached due
     * to an an observed behavior where images are repeated for a period of time. You may download
     * the images in the following order: 1 2 3 2 3 4 5
     * 
     * @param image The image to be compared against the images in the cache
     * 
     * @return True if this is a new image, else false
     */
    private boolean checkForImageChange(BufferedImage image) {
        //
        // If the list is empty then just add the image to the list
        //
        if (images.isEmpty())
            return true;
        
        boolean changed = false;
        int width = image.getWidth();
        int height = image.getHeight();
        int currentImagePixels[] = new int[width * height];
        PixelGrabber currentGrabber = new PixelGrabber(image, 0, 0, width, height, currentImagePixels, 0, width);
        
        try {
            currentGrabber.grabPixels();
        }
        catch (InterruptedException e) {
            logger.log(Level.WARNING, "Caught exception", e);
            return false;
        }
        
        int latestImagePixels[] = new int[width * height];

        //
        // Compare each image in the cache against the passed in image. The comparison is done
        // pixel by pixel
        //
        for (DopplerRadarImage latestImage : images) {
            changed = false;
            width = latestImage.getImage().getWidth();
            height = latestImage.getImage().getHeight();

            if (width * height != latestImagePixels.length) {
                logger.warning("Radar images of different sizes were downloaded");
                continue;
            }
            
            PixelGrabber latestGrabber = new PixelGrabber(latestImage.getImage(), 0, 0, width, height, latestImagePixels, 0, width);

            try {
                latestGrabber.grabPixels();
                
                //
                // Compare the pixels
                //
                for (int i = 0; i < latestImagePixels.length; i++) {
                    if (latestImagePixels[i] != currentImagePixels[i])
                        changed = true;
                }
                
                if (!changed)
                    break;
            }
            catch (InterruptedException e) {
                logger.log(Level.WARNING, "Caught exception", e);
            }
        }
        
        return changed;
    }
}