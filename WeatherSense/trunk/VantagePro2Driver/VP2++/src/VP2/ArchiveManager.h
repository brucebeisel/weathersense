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
#ifndef ARCHIVE_MANAGER_H
#define ARCHIVE_MANAGER_H

#include <string>
#include <vector>
#include "VP2Logger.h"
#include "Weather.h"

namespace vp2 {
class ArchivePacket;
class VantagePro2Station;

/**
 * The ArchiveManager class manages a file that contains the raw data read dump and dmpaft command of the Vantage Pro 2 weather console.
 * This archive acts as a fail-safe in case the data is not able to be saved by the collector. 
 */
class ArchiveManager {
public:
    /**
     * Constructor.
     * 
     * @param archiveFilename The file in which the archive will be maintained
     */
    ArchiveManager(const std::string & archiveFilename, VantagePro2Station & station);

    /**
     * Destructor
     */
    ~ArchiveManager();

    bool readArchive();

    void setNewestRecordTime(DateTime time);

    void getArchiveRecords(std::vector<ArchivePacket> & list);

    /**
     * Return the packets that fall between the specified start end end time (inclusive).
     * 
     * @param packets The container to which the read packets will be added
     * @param startTime The start time of the search
     * @return A reference to the packets argument that will allow the return value to be used in a cascaded call
     */
    void readPackets(std::vector<ArchivePacket> & packets, DateTime startTime);

private:
    /**
     * Add a single packet to the archive.
     * @param packet The packet to add to the archive
     */
    void addPacket(const ArchivePacket & packet);

    /**
     * Add a list of packets to the archive.
     * 
     * @param packets The packets to the added to the archive
     */
    void addPackets(const std::vector<ArchivePacket> & packets);

    /**
     * Finds the time range of the archive.
     * 
     */
    void findPacketTimeRange();

    std::string archiveFile;
    DateTime newestPacketTime;
    DateTime oldestPacketTime;
    DateTime timeOfLastPacketSent;
    VantagePro2Station * station;
    VP2Logger log;
};
}

#endif