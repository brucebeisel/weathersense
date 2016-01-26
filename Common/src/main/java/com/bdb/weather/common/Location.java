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
package com.bdb.weather.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.Month;
import java.util.logging.Logger;

import com.bdb.weather.common.measurement.Depth;
import com.bdb.weather.common.measurement.Temperature;

/**
 * A class that represents a location.
 * 
 * @author Bruce
 * @since 1.0
 */
public class Location
{
    private static final int TOKENS_PER_LINE = 6;
    private static final Logger logger = Logger.getLogger(Location.class.getName());
    
    /**
     * Import a CSV file into the location dialog table
     * 
     * @param file The file to import
     * @param locationCode The location of the data to be imported, this will be verified against the data in the file
     * @param dayWeatherAverages
     * @throws IOException The file could not be read
     */
    public static void importCSVFile(File file, String locationCode, DayWeatherAverages dayWeatherAverages) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line1 = br.readLine();
        String line2 = br.readLine(); // Line 2 is just header information for readability. Ignore it.
        
        if (line2 == null || line1 == null) {
            logger.warning("File is less than 2 lines long");
            return;
        }
        
        String tokens[] = line1.split(",");
        
        if (tokens.length != 6) {
            logger.warning("First line has " + tokens.length + " tokens not 6");
            return;
        }
        
        if (!tokens[1].equals(locationCode)) {
            logger.warning("Location code in file does not match location code");
            return;
        }
        
        Temperature.Unit temperatureUnit = Temperature.Unit.valueOf(tokens[3]);
        Depth.Unit rainfallUnit = Depth.Unit.valueOf(tokens[5]);
        
        DayOfYearCollection<WeatherAverage> collection = new DayOfYearCollection<>();
        
        for (int i = 0; i < 365; i++) {
            String line = br.readLine();
            
            if (line == null) {
                logger.warning("Less than 365 lines in file. Only found " + (i + 1));
                return;
            }
            
            tokens = line.split(",");
            
            if (tokens.length != TOKENS_PER_LINE) {
                logger.warning("Incorrect number of tokens on line " + i + ".");
                return;
            }
            
 
            Temperature low = new Temperature(Double.parseDouble(tokens[2]), temperatureUnit);
            Temperature mean = new Temperature(Double.parseDouble(tokens[3]), temperatureUnit);
            Temperature high = new Temperature(Double.parseDouble(tokens[4]), temperatureUnit);
            Depth rainfall = new Depth(Double.parseDouble(tokens[5]), rainfallUnit);
            
            WeatherAverage avgs = new WeatherAverage(Month.valueOf(tokens[0]), Integer.parseInt(tokens[1]), high, low, mean, rainfall);
            
            collection.addItem(avgs.getMonth(), avgs.getDay(), avgs);        
        }
        
        for (WeatherAverage avg : collection.getAverages()) {
            LocalDate date = LocalDate.of(1970 , avg.getMonth(), avg.getDay());
            dayWeatherAverages.putAverage(avg, date);
        }
    }
    
    /**
     * Export the location data to a CSV file.
     * 
     * @param file The file to which the data will be exported
     * @param locationCode
     * @param dayAverages
     * @throws FileNotFoundException The specified file could not be written to
     */
    public static void exportCSVFile(File file, String locationCode, DayWeatherAverages dayAverages) throws FileNotFoundException {
        try (PrintStream ps = new PrintStream(file)) {
            ps.println("Location Code," + locationCode +
                       ",Temperature Units," + Temperature.getDefaultUnit().name() +
                       ",Depth Units," + Depth.getDefaultUnit().name());
            ps.println("Month,Day,Low Temperature,Mean Temperature,High Temperature,Rainfall");
            for (WeatherAverage avgs : dayAverages.getAllAverages()) {
                String line = String.format("%d,%d,%s,%s,%s,%s",
                                            avgs.getMonth(),
                                            avgs.getDay(),
                                            avgs.getLowTemperature().toString(),
                                            avgs.getMeanTemperature().toString(),
                                            avgs.getHighTemperature().toString(),
                                            avgs.getRainfall().toString()
                                            );
                ps.println(line);
            }
        }
    }
}