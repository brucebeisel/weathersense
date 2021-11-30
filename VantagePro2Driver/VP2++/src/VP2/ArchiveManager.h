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
 * The ArchiveManager class manages a file that contains the raw data read from the DUMP and DMPAFT command of the Vantage Pro 2 weather console.
 * This archive acts as a augmented storage for the console.
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
     * Destructor.
     */
    ~ArchiveManager();

    /**
     * Synchronize the archive file with the contents from the weather station.
     *
     * @return True if successful
     */
    bool synchronizeArchive();

    /**
     * Get the archive records after the specified time.
     *
     * @param afterTime The time that is used to find archive records that are older
     * @param list      The list into which any found archive records will be added
     * @return The time of the last record in the list
     */ 
    DateTime getArchiveRecordsAfter(DateTime afterTime, std::vector<ArchivePacket> & list);

    /**
     * Get the newest record from the archive.
     *
     * @param packet The packet to which the newest record will be written
     * @return True if data was written to the "packet" argument
     */
    bool getNewestRecord(ArchivePacket & packet) const;

private:
    /**
     * Add a single packet to the archive.
     * @param packet The packet to add to the archive
     */
    void addPacket(const ArchivePacket & packet);

    /**
     * Add a list of packets to the archive.
     * 
     * @param packets The list packets to be added to the archive
     */
    void addPackets(const std::vector<ArchivePacket> & packets);

    /**
     * Finds the time range of the archive and set the packet time members.
     */
    void findPacketTimeRange();

    std::string          archiveFile;
    DateTime             newestPacketTime;
    DateTime             oldestPacketTime;
    VantagePro2Station & station;
    VP2Logger            log;
};
}

#endif
