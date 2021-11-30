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
#ifndef PARAMETERS_MESSAGE_H
#define PARAMETERS_MESSAGE_H
#include <string>

namespace vp2 {

/**
 * A class that is a container for various VP2 parameters and also builds the parameters messages that is sent to the collector.
 */
class ParametersMessage {
public:
    /**
     * Constructor.
     */
    ParametersMessage();

    /**
     * Destructor.
     */
    virtual ~ParametersMessage();

    std::string getFirmwareDate() const;
    void        setFirmwareDate(const std::string & value);

    std::string getFirmwareVersion() const;
    void        setFirmwareVersion(const std::string & value);

    double      getLatitude() const;
    void        setLatitude(double value);

    double      getLongitude() const;
    void        setLongitude(double value);

    int         getElevation() const;
    void        setElevation(int value);

    int         getRainSeasonStart() const;
    void        setRainSeasonStart(int value);

    int         getArchivePeriod() const;
    void        setArchivePeriod(int value);

    int         getWindCupSize() const;
    void        setWindCupSize(int value);

    Rainfall    getRainCollectorSize() const;
    void        setRainCollectorSize(Rainfall value);

    std::string formatMessage() const;

private:
    //
    // The manufacturer and model are hard-coded.
    //
    static const std::string MANUFACTURER;
    static const std::string MODEL;
    //
    // The parameters that are collected and sent to the collector.
    //
    std::string firmwareDate;
    std::string firmwareVersion;
    double      latitude;
    double      longitude;
    int         elevation;
    int         rainSeasonStart;
    int         archivePeriod;
    int         windCupSize;
    Rainfall    rainCollectorSize;
};
}
#endif /* PARAMETERS_MESSAGE_H */
