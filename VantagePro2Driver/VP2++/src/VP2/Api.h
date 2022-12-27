/* 
 * Copyright (C) 2022 Bruce Beisel
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
#ifndef API_H
#define API_H

#include "Weather.h"
/**
 * Class(es) for the RESTful API for the Vantage Pro 2 service.
 */

namespace vp2 {
class VantagePro2Station;

class Api {
public:
    Api(VantagePro2Station & station);

    void processCommand(std::string & command);

    /**
     * Request the list of sensor stations that the console can hear.
     * This data should be requested the first time the software starts.
     */
    void requestSensorStationIds();

    /**
     * Request the current weather, that will be sent in the response.
     */
    void requestCurrentWeather();

    /**
     * Request archive data that is after the specified time.
     *
     * @param after Any archive data that is older than this will be sent in the response
     */
    void requestArchiveData(DateTime after);

    /**
     * Request the configuration information. This will include a number of parameters, such as wind cup size, and
     * the sensor stations and sensors that the console is monitoring.
     */
    void requestConfigurationInformation();

    /**
     * Update the configuration information. This will include a number of parameters, such as wind cup size, and
     * the sensor stations and sensors that the console is monitoring.
     */
    void updateConfigurationInformation();

    /**
     * Request the current status of the sensor stations. This will include whether the sensor is currently online, 
     * the battery status and, if available, the reception percentage.
     */
    void requestSensorStationStatus();

    /**
     * Set the yearly ET to the specified value.
     *
     * @param ET in inches with which to set the yearly rainfall value
     */
     void updateYearlyET(double yearlyET);

    /**
     * Set the yearly rainfall to the specified value.
     *
     * @param Rainfall in inches with which to set the yearly rainfall value
     */
     void updateYearlyRainfall(double yearlyRain);

    /**
     * Request the High/Low data for today. Note that if the time crosses midnight between the time the request is
     * made and the response is sent, the data will be for the next day, not the day on which it was requested.
     */
    void requestHighLowData();

    /**
     * Clear the archive memory.
     */
    void clearArchive();

    /**
     * Clear the specified high/low values.
     *
     * @param dailyLows    If true, clear all of the daily low values
     * @param monthlyLows  If true, clear all of the monthly low values
     * @param yearlyLows   If true, clear all of the yearly low values
     * @param dailyHighs   If true, clear all of the daily high values
     * @param monthlyHighs If true, clear all of the monthly high values
     * @param yearlyHighs  If true, clear all of the yearly high values
     */
    void clearExtremeValues(bool dailyLows, bool monthlyLows, bool yearlyLows, bool dailyHighs, bool monthlyHighs, bool yearlyHighs);

    /**
     * Clear all of the active alarms. Note that the alarms will trigger again if the current data falls outside the
     * alarm threshold.
     */
    void clearAlarms();

    /**
     * Change all the values on the console to dashes. Note that the values change to current values as the sensors
     * report new data.
     */
    void clearCurrentData();

    /**
     * Clear all of the data used to create the graphs on the console.
     */
    void clearGraphData();

    /**
     * Clear the rain or ET accumlation value.
     *
     * @param which The name of the cumulative value to clear
     */
    void clearCumulativeValue(const std::string & which);

    /**
     * Synchronize the console time with the host computer. It is assumed the host computer's time has been synchronized with
     * the Internet.
     */
    void synchronizeConsoleTime();

    /**
     * Request the console time.
     */
    void requestConsoleTime();

    /**
     * Change how often records are written to the archive. Note that this command will clear the archive memory.
     *
     * @param intervalMinutes The number of minutes between archive records, must be one of: 1, 5, 10, 15, 30, 60, 120
     */
    void updateArchiveInterval(int intervalMinutes);

    /**
     * Set the console lamp on or off. Note that leaving the lamp on can affect the indoor temperature and humidity.
     *
     * @param on If true, turn the lamp on
     */
    void controlConsoleLamp(bool on);

    /**
     * Return the time zones that the console supports.
     */
    void requestTimeZoneChoices();

private:
    void sendCommandResponse(const std::string & command, const std::string & result);
    void sendCommandResponse(const std::string & command, bool success);
    void sendResponse(const std::string & response);
    VantagePro2Station & station;
};

}

#endif
