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
import java.util.Calendar;

import junit.framework.TestCase;

import com.bdb.weather.common.HistoricalRecord;

/**
 * Second record in file is
 *     10/15/06 3:10 PM
 *     
 * @author Bruce
 *
 */
public class HeavyWeatherPro11FileReaderTest extends TestCase
{
    private static final String RESOURCE_DIR = "src/test/resources";
    private static final File EMPTY_FILE = new File(RESOURCE_DIR + "/EmptyHistory.dat");
    private static final File BAD_SIZE_FILE = new File(RESOURCE_DIR + "/BadSizeHistory.dat");
    private static final File ONE_RECORD_FILE = new File(RESOURCE_DIR + "/OneRecordHistory.dat");
    private static final File TWO_RECORD_FILE = new File(RESOURCE_DIR + "/TwoRecordHistory.dat");
    private static final File FIVE_RECORD_FILE = new File(RESOURCE_DIR + "/FiveRecordHistory.dat");


    public HeavyWeatherPro11FileReaderTest(String arg0)
    {
        super(arg0);
    }

    public void testValidateHistoryFile()
    {
        try
        {
            HeavyWeatherPro11FileReader reader = new HeavyWeatherPro11FileReader(BAD_SIZE_FILE);
            if (reader.validateHistoryFile())
                fail("Bad file was not detected as invalid");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Unexpected exception caught");
        }
    }

    public void testReadNextRecordCalendar()
    {
        try
        {
            HeavyWeatherPro11FileReader reader = new HeavyWeatherPro11FileReader(EMPTY_FILE);           
            reader.openHistoryFile();
            HistoricalRecord rec = reader.readNextRecord(null);
            reader.closeHistoryFile();
            
            if (rec != null)
                fail("Empty file returned a record. It was not supposed to");
            
            reader = new HeavyWeatherPro11FileReader(ONE_RECORD_FILE);
            reader.openHistoryFile();
            rec = reader.readNextRecord(null);
            reader.closeHistoryFile();
            
            if (rec != null)
                fail("One record file returned a record. It was not supposed to");
  
           reader = new HeavyWeatherPro11FileReader(TWO_RECORD_FILE);
           reader.openHistoryFile();
           rec = reader.readNextRecord(null);
           reader.closeHistoryFile();
            
            if (rec == null)
                fail("Two record file did not returned a record. It was supposed to");
            else
                System.out.println(rec);
            
            reader = new HeavyWeatherPro11FileReader(FIVE_RECORD_FILE);           
            reader.openHistoryFile();
            
            Calendar c = Calendar.getInstance();
            c.clear();
            c.set(2006, Calendar.OCTOBER, 15, 15, 10, 0);
            rec = reader.readNextRecord(c);
            
            if (rec == null)
                fail("No record of specified date");
            
            
            if (rec.getTime().getTimeInMillis() - (rec.getDuration() * 1000) != c.getTimeInMillis())
            {
                System.out.println(rec);
                fail("Found record does not match queried date. Queried = " + c.getTimeInMillis() + " Found = " + rec.getTime().getTimeInMillis());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Unexpected exception caught");
        }
    }

    public void testReadNextRecord()
    {
        try
        {
            HeavyWeatherPro11FileReader reader = new HeavyWeatherPro11FileReader(ONE_RECORD_FILE);           
            reader.openHistoryFile();
            HistoricalRecord rec = reader.readNextRecord(null);
            rec = reader.readNextRecord();
            reader.closeHistoryFile();
            
            if (rec != null)
                fail("readNextRecord() returned a record when it should not");
            
            reader = new HeavyWeatherPro11FileReader(TWO_RECORD_FILE);           
            reader.openHistoryFile();
            rec = reader.readNextRecord(null);
            
            if (rec == null)
                fail("readNextRecord() returned null");
            
            rec = reader.readNextRecord();
            
            if (rec != null)
                fail("readNextRecord() did not return null");
            
            reader.closeHistoryFile();
            
            reader = new HeavyWeatherPro11FileReader(FIVE_RECORD_FILE);           
            reader.openHistoryFile();
            rec = reader.readNextRecord(null);
            
            if (rec == null)
                fail("readNextRecord() returned null");
            
            for (int i = 0; i < 3; i++)
            {
                rec = reader.readNextRecord();

                if (rec == null)
                    fail("readNextRecord() returned null");
            }
            
            rec = reader.readNextRecord();
            
            if (rec != null)
                fail("Did not return null");
            
            reader.closeHistoryFile();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Caught unexpected exception");
        }
    }

    public void testOpenHistoryFile()
    {
        try
        {
            HeavyWeatherPro11FileReader reader = new HeavyWeatherPro11FileReader(EMPTY_FILE);
            reader.openHistoryFile();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Unexpected exception caught");
        }
    }

    public void testHeavyWeatherPro11FileReader()
    {
        try
        {
            new HeavyWeatherPro11FileReader(EMPTY_FILE);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            fail("Unexpected exception caught");
        }
    }

    public void testHasRecord()
    {
        try
        {
            HeavyWeatherPro11FileReader reader = new HeavyWeatherPro11FileReader(EMPTY_FILE);           
            reader.openHistoryFile();
            boolean b = reader.hasRecord(1);
            reader.closeHistoryFile();
            
            if (b)
                fail("Empty file found record 1");
            
            reader = new HeavyWeatherPro11FileReader(ONE_RECORD_FILE);
            reader.openHistoryFile();
            b = reader.hasRecord(0);
            reader.closeHistoryFile();
            
            if (!b)
                fail("One record file did not find record 0");
            
            b = reader.hasRecord(1);
            
            if (b)
                fail("One record file found record 1");
            
            reader = new HeavyWeatherPro11FileReader(FIVE_RECORD_FILE);           
            reader.openHistoryFile();
 
            b = reader.hasRecord(4);
            
            if (!b)
                fail("Five record file did not file record 4");
            
            b = reader.hasRecord(5);
            
            if (b)
                fail("Five record file found record 5");
            
            try
            {
                b = reader.hasRecord(-1);
                fail("Exception not thrown when record number -1 used");
            }
            catch (IllegalArgumentException e)
            {
                // expected
            }
 
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Unexpected exception caught");
        }
    }

}
