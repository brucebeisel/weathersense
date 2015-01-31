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

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HWReader
{
    private static final String WIND_DIR_STR[] = {
	"N", "NNE", "NE", "ENE",
	"E", "ESE", "SE", "SSE",
	"S", "SSW", "SW", "WSW",
	"W", "WNW", "NW", "NNW"
    };

    public static final void main(String args[])
    {
	try
	{
	    FileInputStream fis = new FileInputStream("History20060917.dat");

	    int count = 0;

	    FileChannel fc = fis.getChannel();

	    ByteBuffer bb = ByteBuffer.allocate(56);
	    bb.order(ByteOrder.LITTLE_ENDIAN);

	    int br;
	    while ((br = fc.read(bb)) == 56)
	    {
		count++;
		long pos = fc.position();

		System.out.println("Read " + br + " bytes. New position = " + pos);

		double time = bb.getDouble(0);
		float absPressure = bb.getFloat(8);
		float relPressure = bb.getFloat(12);
		float windSpeed = bb.getFloat(16);
		int windDir = bb.getInt(20);
		float windGust = bb.getFloat(24);
		float totalRainfall = bb.getFloat(28);
		float newRainfall = bb.getFloat(32);
		float indoorTemp = bb.getFloat(36);
		float outdoorTemp = bb.getFloat(40);
		float indoorHumid = bb.getFloat(44);
		float outdoorHumid = bb.getFloat(48);

		int days = (int)time;
		double minutes = time - (double)days;

                Calendar c = Calendar.getInstance();

		c.set(1899,11,30,0,0); // init delphi version of start of time months go 0..11

	        c.add(Calendar.DATE, days); // add in the days
		c.add(Calendar.MINUTE, (int)((60.0 * 24.0) * minutes)); // add the minutes
                SimpleDateFormat sdf = new SimpleDateFormat();

		System.out.println("Date = " + time + " " + sdf.format(c.getTime()));
		System.out.println("Abs Pressure = " + absPressure);
		System.out.println("Rel Pressure = " + relPressure);
		System.out.println("Wind Speed = " + windSpeed);
		System.out.println("Wind Dir = " + WIND_DIR_STR[windDir]);
		System.out.println("Gust = " + windGust);
		System.out.println("Total Rainfall = " + totalRainfall);
		System.out.println("New Rainfall = " + newRainfall);
		System.out.println("Indoor Temp = " + indoorTemp);
		System.out.println("Outdoor Temp = " + outdoorTemp);
		System.out.println("Indoor Humid = " + indoorHumid);
		System.out.println("Outdoor Humid = " + outdoorHumid);

		bb.clear();
		if (count == 3)
		{
		    System.exit(0);
		}
	    }

	    System.out.println("Read " + br + " bytes");
	    fc.close();
	}
	catch (Exception e)
	{
	    System.out.println(e);
	    e.printStackTrace();
	}

	
    }
}
