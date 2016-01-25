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

/**
 * Class that defines constants for interfacing with the database.
 * 
 * @author Bruce
 *
 */
public final class DatabaseConstants {
    /**
     * Default database host
     */
    public static final String DATABASE_HOST = "localhost";
    /**
     * Default database port
     */
    public static final String DATABASE_PORT = "3306";
    /**
     * Database name
     */
    public static final String DATABASE_NAME = "weathersense24";
    /**
     * Formatter for creating the database URL
     */
    public static final String DATABASE_URL_FORMATTER = "jdbc:mysql://%s:%s/%s";
    /**
     * Default database URL
     */
    public static final String DATABASE_URL = String.format(DATABASE_URL_FORMATTER, DATABASE_HOST, DATABASE_PORT, DATABASE_NAME);
    /**
     * Default database user
     */
    public static final String DATABASE_USER = "weather";
    /**
     * Default database password
     */
    public static final String DATABASE_PASSWORD = "";
    /**
     * SQL type for location code
     */
    public static final String LOCATION_CODE_TYPE = "varchar(20)";
    /**
     * Database type for temperature values
     */
    public static final String TEMPERATURE_TYPE = "double";
    /**
     * Database type for pressure values
     */
    public static final String PRESSURE_TYPE = "double";
    /**
     * Database type for speed values
     */
    public static final String SPEED_TYPE = "double";
    /**
     * Database type for rainfall (depth) values
     */
    public static final String DEPTH_TYPE = "double"; 
    /**
     * Database type for humidity values
     */
    public static final String HUMIDITY_TYPE = "float";
    
    private DatabaseConstants() {}
}