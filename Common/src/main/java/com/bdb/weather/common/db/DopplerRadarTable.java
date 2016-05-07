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
package com.bdb.weather.common.db;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.util.jdbc.DBTable;

import com.bdb.weather.common.DopplerRadarImage;

/**
 * Database table class for storing Doppler Radar images. The images are associated with a location code.
 * This allows multiple weather stations to use the same Doppler images.
 * 
 * @author Bruce
 *
 */
public class DopplerRadarTable extends DBTable<DopplerRadarImage> {
    private static final String TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".doppler_radar_images";
    
    private static final String SEQUENCE_COLUMN = "sequence";
    private static final String TIME_COLUMN = "time";
    private static final String IMAGE_COLUMN = "image";
    
    private static final int MAX_IMAGES_PER_URL = 100;
    private static final int FIRST_SEQUENCE = 1;
    
    private static final Logger logger = Logger.getLogger(DopplerRadarTable.class.getName());
    
    /**
     * Constructor
     * 
     * @param connection A database connection
     */
    public DopplerRadarTable(DBConnection connection)
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
            LocalDateTime time = rs.getTimestamp(TIME_COLUMN).toLocalDateTime();
            DopplerRadarImage image = null;
            try {
                image = new DopplerRadarImage(time, blobToImage(blob));
            }
            catch (IOException e) {
                logger.log(Level.SEVERE, "Caught IOException", e);
            }
	    blob.free();
            return image;
        });
        
        return list;
    }
    
    /**
     * Trim the table for the specified weather station to the maximum number of images
     * 
     */
    public void trimTable() {
        logger.fine("Trimming Doppler Radar table");
        
        int maxSequence = getNewestSequence();

        //
        // Delete any records that are more than 24 hours old
        //
        int deletedRows = 0;
        if (maxSequence > 0) {
            LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
            String dateStr = DBTable.dateTimeFormatter().format(oneDayAgo);
            int trimEnd = maxSequence - MAX_IMAGES_PER_URL + 1;
            
            String sql = "delete from " + TABLE_NAME + " where " + SEQUENCE_COLUMN + "<" + trimEnd + " or " + TIME_COLUMN + "<\"" + dateStr + "\"";
            deletedRows = executeDeleteRows(sql);
        }
           
        logger.log(Level.FINE, "Finished trimming Doppler Radar table. Deleted {0} rows", deletedRows);
    }
    
    /**
     * Add a Doppler radar image to the database, trimming the number of rows for this station, if necessary
     * 
     * @param image The image to be saved to the database
     * @return True if image saved successfully
     */
    public boolean addRadarImage(BufferedImage image) {
         DopplerRadarImage dri = new DopplerRadarImage(LocalDateTime.now(), image);
         return addRadarImage(dri);
    }
    
    /**
     * Add a Doppler radar image to the table.
     * 
     * @param image The image to add
     * @return true if the image was added successfully
     */
    public boolean addRadarImage(DopplerRadarImage image) {
        boolean success;
	String stmtString = "insert into " + TABLE_NAME + " values(?,?,?)";
        try (PreparedStatement stmt = getConnection().getConnection().prepareStatement(stmtString)) {
            Blob blob = imageToBlob(image.getImage());

            int sequence = getNewestSequence();

            if (sequence == -1)
                sequence = FIRST_SEQUENCE;
            else
                sequence++;

            stmt.setInt(1, sequence);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setBlob(3, blob);

            success = executeAddRow(stmt);

            if (success)       
                trimTable();
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
     * Get all of the Doppler radar images in the table.
     * 
     * @return The list of images
     */
    public List<DopplerRadarImage> getRadarImages() {
        return getNewerRadarImages(Integer.MAX_VALUE);
    }
    
    /**
     * Get the newest (highest number) sequence of the specified URL
     * 
     * @param url The URL used in the lookup
     * @return The newest sequence number for the URL or -1 if an error or no rows
     * @throws SQLException See JDBC documentation
     */
    private int getNewestSequence() {
        String sql = "select max(" + SEQUENCE_COLUMN + ") from " + TABLE_NAME;
        
        logger.fine("Trimming Doppler Radar table");
        
        List<Integer> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            int maxSequence = rs.getInt(1);
            return maxSequence;
        });
        
        int sequence = -1;
        
        if (list.size() > 0)
            sequence = list.get(0);
        
        return sequence;
    }
    
    /**
     * Get the newer Doppler Radar images, retrieving up to the number of images specified
     * 
     * @param maxRetrieves The maximum number of images to get
     * @return The list of images retrieved from the database
     */
    public List<DopplerRadarImage> getNewerRadarImages(int maxRetrieves) {
        List<DopplerRadarImage> images = new ArrayList<>();

        int sequence = getNewestSequence();

        if (sequence != -1) {
        
            sequence = Math.max(sequence - maxRetrieves + 1, FIRST_SEQUENCE);
            
            String clause = " where " + SEQUENCE_COLUMN + ">=" + sequence;
            
            images = query(clause);
            
            if (images.size() > maxRetrieves)
                throw new IllegalStateException("Retrieved too many images (" + images.size() + "). Should have been <= " + maxRetrieves);
        }
        
        return images;
    }
    
    /**
     * Get the newest Doppler radar image from the table.
     * 
     * @return The newest image
     * @throws IOException A database error occurred
     */
    public BufferedImage getNewestRadarImage() throws IOException {
        int maxSequence = getNewestSequence();
        
        if (maxSequence == -1)
            return null;
        
        List<DopplerRadarImage> images = getRadarImages();
        
        if (images.isEmpty())
            return null;
        else
            return images.get(images.size() - 1).getImage();
    }
    
    /**
     * Convert a database BLOB to an image.
     * 
     * @param blob The BLOB to convert to an image
     * @return The image
     * @throws SQLException The BLOB input stream could not be opened
     * @throws IOException The image could not be created from an input stream
     */
    public static BufferedImage blobToImage(Blob blob) throws SQLException, IOException {
        BufferedImage image;
        try (InputStream is = blob.getBinaryStream()) {
            image = ImageIO.read(is);
        }
        return image;
    }
    
    /**
     * Convert an image to a database BLOB.
     * 
     * @param image The image to convert to a BLOB
     * @return The created BLOB
     * @throws SQLException The BLOB could not be created
     * @throws IOException The output stream to write the BLOB could not be created
     */
    public static Blob imageToBlob(BufferedImage image) throws SQLException, IOException {
        byte[] bytesOut;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpeg", os);
            bytesOut = os.toByteArray();
        }
        
        Blob blob = new SerialBlob(bytesOut);
        
        return blob;
    }
}