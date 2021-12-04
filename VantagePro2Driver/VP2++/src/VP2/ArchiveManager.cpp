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
#include <time.h>
#include <iostream>
#include <fstream>
#include <vector>
#include "ArchivePacket.h"
#include "ArchiveManager.h"
#include "VantagePro2Station.h"

using namespace std;

namespace vp2 {

static constexpr int SYNC_RETRIES = 5;

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
ArchiveManager::ArchiveManager(const std::string & archiveFilename, VantagePro2Station & station) :
                                                                    archiveFile(archiveFilename),
                                                                    station(station),
                                                                    log(VP2Logger::getLogger("ArchiveManager")) {
    findPacketTimeRange();
    
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
ArchiveManager::~ArchiveManager() {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
ArchiveManager::synchronizeArchive() {
    vector<ArchivePacket> list;
    bool result = false;

    for (int i = 0; i < SYNC_RETRIES && !result; i++) {
        list.clear();
        if (station.wakeupStation() && station.dumpAfter(newestPacketTime, list)) {
            addPackets(list);
            result = true;
            break;
        }
    }

    if (list.size() > 0)
        cout << "Archive: " << list[list.size() - 1].formatMessage() << endl;

    return result;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
DateTime
ArchiveManager::getArchiveRecordsAfter(DateTime afterTime, std::vector<ArchivePacket>& list) {
    log.log(VP2Logger::VP2_DEBUG1) << "Reading packets after " << Weather::formatDateTime(afterTime) << endl;
    DateTime timeOfLastRecord = 0;
    byte buffer[ArchivePacket::BYTES_PER_PACKET];
    ifstream stream(archiveFile.c_str(), ios::in | ios::binary);
    stream.seekg(-ArchivePacket::BYTES_PER_PACKET, ios::end);
    streampos streamPosition;
    DateTime packetTime;

    list.clear();

    //
    // If the start time is newer than the oldest packet in the archive, look for the packet that is after the specified time.
    // Otherwise the start time is before the beginning of the file, so just start at the beginning.
    //
    if (afterTime > oldestPacketTime) {
        do {
            streamPosition = stream.tellg();
            stream.read(buffer, sizeof(buffer));
            ArchivePacket packet(buffer, 0);
            stream.seekg(-(ArchivePacket::BYTES_PER_PACKET * 2), ios::cur);
            packetTime = packet.getDateTime();
        } while (afterTime < packetTime && streamPosition > 0);

        //
        // The stream will not be good if the final seekg() call went past the beginning of the file.
        // Clear the error and position the next to be the beginning of the file.
        //
        if (!stream.good()) {
            stream.clear();
            stream.seekg(0, ios::beg);
        }
        else
            stream.seekg(ArchivePacket::BYTES_PER_PACKET * 2, ios::cur);
    }
    else
        stream.seekg(0, ios::beg);
    
    //
    // Cap the number of records to the number of archive records that the console holds.
    // If there are more records, then the caller needs to call this method until the
    // list returns empty.
    //
    while (list.size() < VP2Constants::NUM_ARCHIVE_RECORDS) {
        stream.read(buffer, sizeof(buffer));

        if (stream.eof())
            break;

        ArchivePacket packet(buffer, 0);
        list.push_back(packet);
        timeOfLastRecord = packet.getDateTime();
    }

    stream.close();

    return timeOfLastRecord;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
ArchiveManager::getNewestRecord(ArchivePacket & packet) const {
    ifstream stream(archiveFile.c_str(), ios::in | ios::binary | ios::ate);
    streampos fileSize = stream.tellg();

    if (fileSize >= ArchivePacket::BYTES_PER_PACKET) {
        byte buffer[ArchivePacket::BYTES_PER_PACKET];
        stream.seekg(-ArchivePacket::BYTES_PER_PACKET, ios::end);
        stream.read(buffer, sizeof(buffer));
        packet.updateArchiveData(buffer, 0);
        return true;
    }
    else
        return false;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
ArchiveManager::addPacket(const ArchivePacket & packet) {
    vector<ArchivePacket> list;
    list.push_back(packet);
    addPackets(list);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
ArchiveManager::addPackets(const vector<ArchivePacket> & packets) {
    if (packets.size() == 0)
        return;

    ofstream stream;
    stream.open(archiveFile.c_str(), ofstream::out | ios::app | ios::binary);
    for (vector<ArchivePacket>::const_iterator it = packets.begin(); it != packets.end(); ++it) {
        if (newestPacketTime < it->getDateTime()) {
            stream.write(it->getBuffer(), ArchivePacket::BYTES_PER_PACKET);
            newestPacketTime = it->getDateTime();
            log.log(VP2Logger::VP2_DEBUG1) << "Archived packet with time: " << Weather::formatDateTime(it->getDateTime()) << endl;
        }
        else
            log.log(VP2Logger::VP2_INFO) << "Skipping archive of packet with time " << Weather::formatDateTime(it->getDateTime()) << endl;
    }
    stream.close();
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
ArchiveManager::findPacketTimeRange() {
    ifstream stream(archiveFile.c_str(), ios::in | ios::binary | ios::ate);

    streampos fileSize = stream.tellg();
    if (fileSize >= ArchivePacket::BYTES_PER_PACKET) {
        byte buffer[ArchivePacket::BYTES_PER_PACKET];

        //
        // Read the packet at the beginning of the file
        //
        stream.seekg(0, ios::beg);
        stream.read(buffer, sizeof(buffer));
        ArchivePacket packet(buffer, 0);
        oldestPacketTime = packet.getDateTime();

        //
        // Read the packet at the end of the file
        //
        stream.seekg(-ArchivePacket::BYTES_PER_PACKET, ios::end);
        stream.read(buffer, sizeof(buffer));
        packet.updateArchiveData(buffer, 0);
        newestPacketTime = packet.getDateTime();
    }
    else {
        oldestPacketTime = 0;
        newestPacketTime = 0;
    }

    stream.close();
}

}
