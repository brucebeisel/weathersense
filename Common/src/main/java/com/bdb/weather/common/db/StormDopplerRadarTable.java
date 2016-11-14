/* 
 * Copyright (C) 2016 Bruce Beisel
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
package com.bdb.weather.common.db;

import java.io.IOException;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.util.jdbc.DBTable;

import com.bdb.weather.common.DopplerRadarImage;

/**
 * Database table class for storing Doppler Radar images associated with a storm. The images are associated with a location code.
 * This allows multiple weather stations to use the same Doppler images.
 * 
 * @author Bruce
 *
 */
public class StormDopplerRadarTable extends DBTable<DopplerRadarImage> {
    private static final String TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".storm_doppler_images";
    
    private static final String STORM_START_COLUMN = "storm_start";
    private static final String IMAGE_TIME_COLUMN = "image_time";
    private static final String IMAGE_COLUMN = "image";
    
    private static final Logger logger = Logger.getLogger(StormDopplerRadarTable.class.getName());
    
    /**
     * Constructor
     * 
     * @param connection A database connection
     */
    public StormDopplerRadarTable(DBConnection connection)
    {
        super(TABLE_NAME, connection);
        ImageIO.setUseCache(false);
    }
    
    /**
     * Not implemented
     * 
     * @return True if the row was added
     */
    @Override
    public boolean addRow(DopplerRadarImage row) {
        return false;
    }

 
    /**
     * Not implemented
     * 
     * @return True if the row was deleted
     */
    @Override
    public boolean deleteRow(DopplerRadarImage row) {
        return false;
    }

    /**
     * Not implemented
     * 
     * @return The list of Doppler radar images
     */
    @Override
    public List<DopplerRadarImage> query(String clause) {
        String sql = "select * from " + TABLE_NAME + " ";
        
        if (clause != null)
            sql += " " + clause;

        List<DopplerRadarImage> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            Blob blob = rs.getBlob(IMAGE_COLUMN);
            LocalDateTime time = rs.getTimestamp(IMAGE_TIME_COLUMN).toLocalDateTime();
            DopplerRadarImage image = null;
            try {
                image = new DopplerRadarImage(time, DopplerRadarTable.blobToImage(blob));
            }
            catch (IOException e) {
                logger.log(Level.SEVERE, "Caught IOException", e);
            }
            return image;
        });
        
        return list;
    }
    
    /**
     * Add a Doppler radar image.
     * 
     * @param stormStart The start of the storm associated with this image
     * @param image The Doppler radar image
     * @return True if the image was added successfully
     */
    public boolean addRadarImage(LocalDateTime stormStart, DopplerRadarImage image) {
        boolean success;
	String stmtString = "insert into " + TABLE_NAME + " values(?,?,?)";
        try (PreparedStatement stmt = getConnection().getConnection().prepareStatement(stmtString)) {
            Blob blob = DopplerRadarTable.imageToBlob(image.getImage());

            stmt.setTimestamp(1, Timestamp.valueOf(stormStart));
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setBlob(3, blob);

            success = executeAddRow(stmt);
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Caught SQL Exception", e);
            success = false;
        }
        catch (IOException e2) {
            logger.log(Level.SEVERE, "Caught I/O Exception", e2);
            success = false;
        }

    	return success;
    }
    
    /**
     * Get the Doppler radar images for the specified storm.
     * 
     * @param stormStart The start of the storm
     * @return The list of Doppler radar images
     */
    public List<DopplerRadarImage> getRadarImagesForStorm(LocalDateTime stormStart) {
        List<DopplerRadarImage> images = query("where " + STORM_START_COLUMN + "=\"" + DBTable.dateTimeFormatter().format(stormStart) + "\"");
        return images;
    }

    /**
     * Trim the extra radar images that are stored after the last rainfall, but before the storm is declared as complete.
     * 
     * @param stormStart The start of the storm
     * @param lastRainfall The time of the last rainfall
     * @return The number of images deleted from the database
     */
    public int trimStormRadarImages(LocalDateTime stormStart, LocalDateTime lastRainfall) {
        final String sql = "delete from " + TABLE_NAME +
                           " where " + STORM_START_COLUMN + "='" +
                           DBTable.dateTimeFormatter().format(stormStart) +
                           "' and " + IMAGE_TIME_COLUMN + ">'" + DBTable.dateTimeFormatter().format(lastRainfall) + "'";

        int affectedRecords = getConnection().executeUpdate(sql);

        return affectedRecords;
    }
}