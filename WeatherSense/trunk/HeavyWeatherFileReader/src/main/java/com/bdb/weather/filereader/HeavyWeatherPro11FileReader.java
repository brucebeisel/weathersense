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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.weather.common.Depth;
import com.bdb.weather.common.Heading;
import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.Humidity;
import com.bdb.weather.common.Pressure;
import com.bdb.weather.common.SensorStation;
import com.bdb.weather.common.SensorType;
import com.bdb.weather.common.Speed;
import com.bdb.weather.common.Temperature;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.WeatherUtils;
import com.bdb.weather.common.Wind;

/**
 * Reads the weather file for the Heavy Weather Pro version 1.1
 * The records of this file do not contain a duration, just a time.
 * In order to support the duration field required by the collector the
 * duration is calculated by calculating the delta time between the previous
 * record and the current record. This means that the first record in the file
 * is not sent to the collector but is only used to calculate the duration of the
 * second record in the file.
 * 
 * @author Bruce
 * @since 1.0
 *
 */
public class HeavyWeatherPro11FileReader extends HistoryFileReader
{
    private static final int RECORD_NOT_FOUND = -1;
    private static final int RECORD_LENGTH = 56;
    private static final int UNKNOWN_HUMIDITY = 110;
    private static final float UNKNOWN_WIND_DIR = (float)360.0;
    private static final float UNKNOWN_BARO_PRESSURE = (float)990.0;
    private static final double UNKNOWN_MAX_TEMP = 81.0;         // If greater then unknown value
    private static final double UNKNOWN_MAX_WIND_SPEED = 180.0;  // If greater then unknown value
    private static final double UNKNOWN_GUST_SPEED = 51.0;  // If greater then unknown value

    private FileInputStream  m_fis;
    private FileChannel      m_channel;
    private ByteBuffer	     m_buffer;
    private int	             m_numRecords;
    private int              m_curRecordNumber;
    private Calendar	     m_lastTime;
    private List<SensorStation>     m_sensors = new ArrayList<>();
    private static final Logger m_logger = Logger.getLogger(HeavyWeatherPro11FileReader.class.getName());

    /**
     * Constructor
     * 
     * @param f The file to open
     * @throws FileNotFoundException The specified file was not found
     */
    HeavyWeatherPro11FileReader(File f) throws  FileNotFoundException
    {
        super(f);

        m_logger.fine("Constructing with args: File = " + f);

        m_buffer = ByteBuffer.allocate(RECORD_LENGTH);
        m_buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Open the file that was passed into the constructor. Since this file could
     * be open by the weather station software, make multiple attempts
     */
    @Override
    public void openHistoryFile() throws FileNotFoundException, IOException
    {
        m_logger.entering(getClass().getName(), "openHistoryFile");

        for (int i = 0; i < 20; i++)
        {
            try
            {
                m_fis = new FileInputStream(getFile());
                m_channel = m_fis.getChannel();
            }
            catch (SecurityException e)
            {
                m_logger.log(Level.INFO, "Attempt " + (i + 1) + " of 20 failed to open history file", e);
            }
        }

        if (m_channel == null || m_fis == null)
        {
            closeHistoryFile();
            throw new IOException("openHistoryFile() exceeded tries");
        }

    }

    /**
     * Close the history file
     */
    @Override
    public void closeHistoryFile()
    {
        m_logger.entering(getClass().getName(), "closeHistoryFile");

        try
        {
            if (m_channel != null)
                m_channel.close();

            if (m_fis != null)
                m_fis.close();
        }
        catch (IOException e)
        {
        }

        m_channel = null;
        m_fis = null;
    }

    /**
     * Validate the history file.
     */
    @Override
    public boolean validateHistoryFile()
    {
        long fileLength = getFile().length();

        m_numRecords = (int)(fileLength / (long)RECORD_LENGTH);

        return fileLength % RECORD_LENGTH == 0;
    }

    /**
     * Check whether this file has a record with the number passed as the argument.
     * This is really a file length check function
     * 
     * @param recordNumber The record number which is to be checked for existence
     * @return True if the file is long enough to hold a record of the given number
     */
    boolean hasRecord(int recordNumber)
    {
        if (recordNumber < 0)
            throw new IllegalArgumentException("Negative record number not allowed");

        m_numRecords = (int)(getFile().length() / (long)RECORD_LENGTH);

        return recordNumber < m_numRecords;
    }

    /**
     * Read the record of the given record number
     * 
     * @param recordNumber The record number to read
     * 
     * @return The record that was read or null if the file is too small for this record number
     * 
     * @throws IOException The IO Channel is not open or read() failed to read the correct number of bytes
     */
    private HistoricalRecord readRecord(int recordNumber) throws IOException
    {
        m_logger.finer("Reading record " + recordNumber);

        //
        // If the file is not big enough, return
        //
        if (!hasRecord(recordNumber))
        {
            m_logger.finer("File is too small for this record");
            return null;
        }

        //
        // If the file is not open, return
        //
        if (m_channel == null)
            throw new IOException("Channel is not open");

        //
        // Position the channel to the location of the record
        //
        m_channel.position(recordNumber * RECORD_LENGTH);

        //
        // Read the record
        //
        int bytesRead = m_channel.read(m_buffer);

        //
        // This should not happen, we have already check for the file size
        //
        if (bytesRead == -1)
            throw new IOException("Error on channel read");

        if (bytesRead != RECORD_LENGTH)
        {
            throw new IOException("Did not read complete record " + bytesRead);
        }
        /* 
        String s = "";
        for (int i = 0; i < RECORD_LENGTH; i++)
        {
            byte b = m_buffer.get(i);
            s += String.format("%02x ", b);
        }

        System.out.println(s);
         */
        double time = m_buffer.getDouble(0);

        int days = (int)time;
        double dayFraction = time - (double)days;
        m_logger.finest("Record = " + recordNumber + " Time = " + time);

        Calendar c = Calendar.getInstance();
        c.clear();

        //
        // Convert from a Delphi date. A Delphi date is a IEEE double where
        // the integral portion is the number of days since Dec 30, 1899 and
        // the fractional portion is the fraction of the day. Remember that
        // Java uses 0..11 for the month.
        //
        c.set(1899,11,30,0,0);
        c.add(Calendar.DATE, days);
        int offset = c.get(Calendar.DST_OFFSET);
        int secondInDay = (int)(86400.0 * dayFraction);
        c.add(Calendar.SECOND, secondInDay);

        // 
        // Round to the nearest minute
        //
        if (c.get(Calendar.SECOND) >= 30)
            c.add(Calendar.MINUTE, 1);

        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        //
        // This is a really ugly adjust for DST. The 3610 just ignores the fact that
        // when DST shuts off in the fall that the day is actually 90000 seconds long
        // instead of 86400. To compensate keep the DST offset active from 2:00 AM (which is actually
        // the second 1:00 AM, until the end of the day
        //
        if (offset != c.get(Calendar.DST_OFFSET) && offset != 0 && secondInDay >= 7200)
        {
            c.set(Calendar.DST_OFFSET, -offset);
        }

        Pressure absPressure = new Pressure(m_buffer.getFloat(8), Pressure.Unit.MILLIBAR);
        Pressure relPressure = new Pressure(m_buffer.getFloat(12), Pressure.Unit.MILLIBAR);
        Speed windSpeed = new Speed(m_buffer.getFloat(16), Speed.Unit.METERS_PER_SEC);
        int windDirIndex = m_buffer.getInt(20);
        float windDir = (float)windDirIndex * (float)22.5;
        Speed windGust = new Speed(m_buffer.getFloat(24), Speed.Unit.METERS_PER_SEC);
        float totalRainfall = m_buffer.getFloat(28);
        Depth newRainfall = new Depth(m_buffer.getFloat(32), Depth.Unit.MILLIMETERS);
        Temperature indoorTemp = new Temperature(m_buffer.getFloat(36), Temperature.Unit.CELSIUS);
        Temperature outdoorTemp = new Temperature(m_buffer.getFloat(40), Temperature.Unit.CELSIUS);
        int indoorHumid = (int)m_buffer.getFloat(44);
        int outdoorHumid = (int)m_buffer.getFloat(48);

        m_buffer.clear();

        HistoricalRecord rec = new HistoricalRecord(m_sensors, c, 0);

        if (indoorTemp.get() < UNKNOWN_MAX_TEMP)
            rec.setIndoorTemperature(indoorTemp);

        if (outdoorTemp.get() < UNKNOWN_MAX_TEMP)
            rec.setAvgOutdoorTemperature(outdoorTemp);

        if (windGust.get() == UNKNOWN_GUST_SPEED)
            windGust = null;

        if (windDir != UNKNOWN_WIND_DIR)
            rec.setAvgWind(new Wind(windSpeed, new Heading(windDir)));

        if (absPressure.get() != UNKNOWN_BARO_PRESSURE)
            rec.setBaroPressure(absPressure);

        if (indoorHumid != UNKNOWN_HUMIDITY)
            rec.setIndoorHumidity(new Humidity(indoorHumid));

        if (outdoorHumid != UNKNOWN_HUMIDITY)
            rec.setOutdoorHumidity(new Humidity(outdoorHumid));

        rec.setRainfall(newRainfall);


        return rec;
    }

    /**
     * Perform a binary search for the record after the specified time. We are looking for an exact match
     * since the newest record must be from the file.
     * 
     * @param c
     * @return
     */
    private int search(Calendar c) throws IOException
    {
        validateHistoryFile();

        if (m_numRecords < 2)
            return RECORD_NOT_FOUND;

        int curRecord = m_numRecords / 2;
        int slice = curRecord / 2;

        HistoricalRecord rec;

        //
        // TODO This algorithm could cause an endless loop if the time given does not match any
        // record in the file.
        // An algorithm from the web starts with a min, max and mid. mid = max - min / 2
        // if search value > mid then min = mid + 1, else max = min -1
        // This algorithm can be recursive.
        //
        while (true)
        {
            rec = readRecord(curRecord);

            if (rec.getTime().equals(c))
                return curRecord;

            if (rec.getTime().after(c))
                curRecord -= slice;
            else
                curRecord += slice;

            if (slice != 1)
                slice /= 2;

            if (curRecord < 0)
                return RECORD_NOT_FOUND;
        }
    }

    /**
     * Read the record that is after the given time
     *
     * @param cal Read the record after this time
     * 
     * @return The record that was read of null if no record exists after the given time
     * 
     * @throws IOException Pass through of exception from readRecord()
     */
    @Override
    public HistoricalRecord readNextRecord(Calendar cal) throws IOException
    {
        if (cal != null)
            m_logger.fine("Trying to read record after " + WeatherUtils.formatDate(cal));
        else
            m_logger.fine("Trying to read the first valid record");

        //
        // Read the first record in order to establish the duration for record 1 and beyond
        //
        m_curRecordNumber = 0;
        HistoricalRecord record = readRecord(m_curRecordNumber);

        //
        // If there are no records in the file, return
        //
        if (record == null)
            return null;

        m_lastTime = record.getTime();

        //
        // The first record does not count, it is only used to calculate the duration of record #2
        //
        if (m_numRecords == 1)
            return null;

        //
        // If no calendar was supplied, then the requester is looking for the first record in the file
        //
        if (cal == null)
            m_curRecordNumber = 0;
        else
            m_curRecordNumber = search(cal);

        if (m_curRecordNumber == RECORD_NOT_FOUND)
            return null;

        record = readRecord(m_curRecordNumber);
        m_logger.fine("Found match at record #" + m_curRecordNumber + ": " + record);
        m_lastTime = record.getTime();
        record = readNextRecord();
        m_logger.fine("Next record #" + m_curRecordNumber + ": " + record);
        return record;
    }

    /**
     * Read the next record in the history file. The readNextRecord(Calendar) method must be
     * called before this one.
     * 
     * @return The next Historical Record or null if end-of-file has been reached
     * 
     * @throws IOException readNextRecord(Calendar) was not called first or readRecord() threw the exception
     */
    public HistoricalRecord readNextRecord() throws IOException
    {
        if (m_lastTime == null)
            throw new IOException("readNextRecord() called before call to readNextRecord(Calendar)");

        HistoricalRecord record = readRecord(m_curRecordNumber + 1);

        if (record == null)
            return null;

        //
        // There are times when the record in the file is corrupt (it could be the way the
        // weather station marks deleted records). Ignore any records where the date is before
        // the date of the previous record
        //
        if (record.getTime().before(m_lastTime))
        {
            m_curRecordNumber++;
            return readNextRecord();
        }


        m_curRecordNumber++;

        //
        // Since the WC-3610 does not save the length of time over which the record applies to we
        // must calculate the duration based on the time of the previous record in the file.
        //
        long duration = (record.getTime().getTimeInMillis() - m_lastTime.getTimeInMillis()) / (long)1000;

        record.setDuration((int)duration);

        m_lastTime = record.getTime();

        return record;
    }
}
