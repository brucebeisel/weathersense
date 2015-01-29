package com.bdb.util;

import java.time.LocalDate;
import java.time.Month;

/**
 * Enumeration for the weather seasons.
 * 
 * @author Bruce
 *
 */
public enum Season {

    WINTER(Month.DECEMBER, Month.MARCH),
    SPRING(Month.MARCH, Month.JUNE),
    SUMMER(Month.JUNE, Month.SEPTEMBER),
    FALL(Month.SEPTEMBER, Month.DECEMBER);

    static {
        WINTER.previousSeason = FALL;
        WINTER.nextSeason = SPRING;
        SPRING.previousSeason = WINTER;
        SPRING.nextSeason = SUMMER;
        SUMMER.previousSeason = SPRING;
        SUMMER.nextSeason = FALL;
        FALL.previousSeason = SUMMER;
        FALL.nextSeason = WINTER;
    }
    //
    // The seasons always start on the 21st. Due to leap year, sometimes it actually
    // starts on the 22nd, but we are going to ignore that in order to simplify the
    // concept of a season
    //
    public static final int SEASON_START_DAY_OF_MONTH = 21;
    
    private final Month startMonth;
    private final Month endMonth;
    private Season previousSeason;
    private Season nextSeason;

    /**
     * Constructor
     * 
     * @param startMonth The month in which a season starts
     * @param endMonth The month in which a season ends
     */
    Season(Month startMonth, Month endMonth) {
        this.startMonth = startMonth;
        this.endMonth = endMonth;
    }

    /**
     * Get the start month of a season
     * 
     * @return The month this season starts
     */
    public Month getStartMonth() {
        return startMonth;
    }
    
    /**
     * Get the end month of a season
     * 
     * @return The month this season ends
     */
    public Month getEndMonth() {
        return endMonth;
    }
    
    /**
     * Get a calendar that is set to the time that this season starts for the specified year
     * 
     * @param base The month and year for which the season start will be calculated
     * 
     * @return A calendar that is set to the season start
     * 
     * @throws IllegalArgumentException The date does not fall within the season
     */
    public LocalDate seasonStart(LocalDate base) throws IllegalArgumentException {
        if (!isInSeason(base))
            throw new IllegalArgumentException("Cannot calculate season start for a date not in the season " + this);
        
        LocalDate date = LocalDate.of(base.getYear(), startMonth, SEASON_START_DAY_OF_MONTH);
        
        //
        // Compensate for a season that spans the new year
        //
        if (date.isAfter(base)) {
            //
            // If the new calendar is after the provided calendar then subtract a year.
            // This compensates for checking against WINTER in January. 
            //
            date = date.minusYears(1);
        }
        
        return date;
    }
    
    /**
     * Get a date that is set to the time that this season ends for the specified year
     * 
     * @param base The year that the year in the calendar will be set
     * 
     * @return A date that is set to the season end
     * 
     * @throws IllegalArgumentException The date does not fall within the season
     */
    public LocalDate seasonEnd(LocalDate base) throws IllegalArgumentException {
        if (!isInSeason(base))
            throw new IllegalArgumentException("Cannot calculate season end for a date not in the season");
        
        LocalDate date = LocalDate.of(base.getYear(), endMonth, SEASON_START_DAY_OF_MONTH - 1);
        
        //
        // Compensate for a season that spans the new year
        //
        if (date.isBefore(base)) {
            //
            // If the new calendar is before the provided calendar then add a year.
            // This compensates for checking against WINTER in December. 
            //
            date = date.plusYears(1);
        }
        
        return date;
    }
    
    /**
     * Check if the specified calendar falls within this season
     * 
     * @param date The date to compare against this season
     * 
     * @return True if the calendar falls within this season, else false
     * 
     * @throws IllegalArgumentException The date does not fall within the season
     */
    public boolean isInSeason(LocalDate date) throws IllegalArgumentException
    {
        Month month = date.getMonth();
        int day = date.getDayOfMonth();
        
        if ((month == startMonth && day >= SEASON_START_DAY_OF_MONTH) ||
            (month == endMonth && day < SEASON_START_DAY_OF_MONTH))
            return true;

        if (startMonth.getValue() < endMonth.getValue())
            return month.getValue() > startMonth.getValue() && month.getValue() < endMonth.getValue();
        else
            return month.getValue() > startMonth.getValue() || month.getValue() < endMonth.getValue();

    }
    
    /**
     * Get the previous season based on a date.
     * 
     * @param date The date for the "this" season.
     * @return The previous season
     * @throws IllegalArgumentException The calendar does not call within the "this" season
     */
    public Season previousSeason(LocalDate date) throws IllegalArgumentException {
        return whichSeason(date).previousSeason;
    }
    
    /**
     * Get the next season based on a date.
     * 
     * @param date The date for the "this" season.
     * @return The next season
     * @throws IllegalArgumentException The calendar does not call within the "this" season
     */
    public Season nextSeason(LocalDate date) throws IllegalArgumentException {
        return whichSeason(date).nextSeason;
    }
    
    /**
     * Return the season in which the specified calendar falls
     * 
     * @param date The date to compare
     * 
     * @return The season in which the calendar falls
     */
    public static Season whichSeason(LocalDate date)
    {
        Season rval = WINTER;
        
        for (Season season : Season.values()) {
            if (season.isInSeason(date))
                rval = season;
        }
        
        return rval;
    }
    
    /**
     * Return the season that the current time falls
     * 
     * @return The current season
     */
    public static Season currentSeason()
    {
        return whichSeason(LocalDate.now());
    }
}
