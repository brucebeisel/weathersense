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
package com.bdb.weather.filereader;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Class that only provides the weatherFileReader creation method
 * 
 * @author Bruce
 * @since 1.0
 */
public final class WeatherFileReaderFactory
{
    
    /**
     * Create a HistoryFileReader based on the name provided.
     * 
     * @param name The name of the weather station model
     * @param file The file to open
     * @return The created reader of null if the name was not recognized
     * @throws FileNotFoundException
     */
    static final HistoryFileReader weatherFileReader(String name, File file) throws FileNotFoundException
    {
	if (name.equals("HeavyWeatherPro1.1"))
	    return new HeavyWeatherPro11FileReader(file);
	else if (name.equals("WeatherSenseDatabase"))
	    return new DatabaseReader(file);
	else
	    return null;
    }
    
    /**
     * Private constructor to prevent construction of an object
     */
    private WeatherFileReaderFactory()
    {
    }
}
