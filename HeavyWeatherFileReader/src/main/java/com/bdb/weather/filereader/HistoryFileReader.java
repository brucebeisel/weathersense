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
import java.io.IOException;
import java.util.Calendar;

import com.bdb.weather.common.HistoricalRecord;

/**
 * Abstract class for the weather history file reader
 * 
 * @author Bruce
 * @since 1.0
 *
 */
public abstract class HistoryFileReader
{
    private File m_file;

    /**
     * Constructor
     * 
     * @param file The file to be opened
     * @throws FileNotFoundException The provided file was not found
     */
    protected HistoryFileReader(File file) throws FileNotFoundException
    {
	m_file = file;
        
        if (!m_file.exists())
            throw new FileNotFoundException("Could not find history file '" + m_file.getAbsolutePath() + "'");
    }

    public File getFile()
    {
	return m_file;
    }

    public boolean validateHistoryFile()
    {
	return true;
    }

    /**
     * Read the historical record that follows the given date
     * 
     * @param cal The date
     * @return The historical record or NULL if no records are after the given date
     * @throws IOException Error reading the file
     */
    abstract HistoricalRecord readNextRecord(Calendar cal) throws IOException;
    
    /**
     * Read the "next" record in the file. Note that the readNextRecord must be called first.
     * 
     * @return The next record in the file or NULL if the end of the file was reached
     * @throws IOException Error reading the file
     */
    abstract HistoricalRecord readNextRecord() throws IOException;
    
    /**
     * Open the weather history file provided in the constructor.
     * 
     * @throws FileNotFoundException The file was not found
     * @throws IOException Error opening the file
     */
    abstract void openHistoryFile() throws FileNotFoundException, IOException;
    
    /**
     * Close the weather history file
     */
    abstract void closeHistoryFile();
}
