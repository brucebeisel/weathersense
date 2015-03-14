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
#include <time.h>
#include <iostream>
#include <fstream>
#include <vector>
#include "ArchivePacket.h"
#include "ArchiveManager.h"
#include "VantagePro2Station.h"

using namespace std;

namespace vp2 {

ArchiveManager::ArchiveManager(const std::string & archiveFilename, VantagePro2Station & station) :
                                                                    archiveFile(archiveFilename),
                                                                    station(&station),
                                                                    timeOfLastPacketSent(0),
                                                                    log(VP2Logger::getLogger("ArchiveManager")) {
    findPacketTimeRange();
    
}

ArchiveManager::~ArchiveManager() {
}

bool
ArchiveManager::readArchive() {
    vector<ArchivePacket> list;
    bool result = false;

    for (int i = 0; i < 5 && !result; i++) {
        list.clear();
        if (station->wakeupStation() && station->dumpAfter(newestPacketTime, list)) {
            addPackets(list);
            result = true;
            break;
        }
    }

    return result;
}

void
ArchiveManager::setNewestRecordTime(DateTime time) {
    timeOfLastPacketSent = time;
}

void
ArchiveManager::addPacket(const ArchivePacket & packet) {
    vector<ArchivePacket> list;
    list.push_back(packet);
    addPackets(list);
}

void
ArchiveManager::addPackets(const vector<ArchivePacket> & packets) {
    if (packets.size() == 0)
        return;

    ofstream stream;
    stream.open(archiveFile.c_str(), ofstream::out | ios::app | ios::binary);
    for (vector<ArchivePacket>::const_iterator it = packets.begin(); it != packets.end(); ++it) {
        if (newestPacketTime < it->getDateTime()) {
            stream.write(it->getBuffer(), VP2Constants::APB_BYTES_PER_RECORD);
            newestPacketTime = it->getDateTime();
            log.log(VP2Logger::VP2_DEBUG1) << "Archived packet with time: " << Weather::formatDateTime(it->getDateTime()) << endl;
        }
        else
            log.log(VP2Logger::VP2_INFO) << "Skipping archive of packet with time " << Weather::formatDateTime(it->getDateTime()) << endl;
    }
    stream.close();
}

void
ArchiveManager::findPacketTimeRange() {
    ifstream stream(archiveFile.c_str(), ios::in | ios::binary | ios::ate);
    streampos fileSize = stream.tellg();
    if (fileSize > VP2Constants::APB_BYTES_PER_RECORD) {
        byte buffer[VP2Constants::APB_BYTES_PER_RECORD];
        stream.seekg(0, ios::beg);
        stream.read(buffer, sizeof(buffer));
        ArchivePacket packet = station->convertBufferToArchivePacket(buffer, 0);
        oldestPacketTime = packet.getDateTime();

        stream.seekg(-VP2Constants::APB_BYTES_PER_RECORD, ios::end);
        stream.read(buffer, sizeof(buffer));
        packet = station->convertBufferToArchivePacket(buffer, 0);
        newestPacketTime = packet.getDateTime();
    }
    else {
        oldestPacketTime = 0;
        newestPacketTime = 0;
    }

    stream.close();
}

void
ArchiveManager::getArchiveRecords(std::vector<ArchivePacket>& list) {
    if (timeOfLastPacketSent != 0)
        readPackets(list, timeOfLastPacketSent);
}

void
ArchiveManager::readPackets(vector<ArchivePacket> & list, DateTime startTime) {
    log.log(VP2Logger::VP2_DEBUG1) << "Reading packets after " << Weather::formatDateTime(startTime) << endl;
    byte buffer[VP2Constants::APB_BYTES_PER_RECORD];
    ifstream stream(archiveFile.c_str(), ios::in | ios::binary);
    stream.seekg(-VP2Constants::APB_BYTES_PER_RECORD, ios::end);
    streampos streamPosition;
    DateTime packetTime;

    list.clear();

    //
    // If the start time is newer than the oldest packet in the archive, look for the packet that is after the specified time.
    // Otherwise the start time is before the beginning of the file, so just start at the beginning.
    //
    if (startTime > oldestPacketTime) {
        do {
            streamPosition = stream.tellg();
            stream.read(buffer, sizeof(buffer));
            ArchivePacket packet = station->convertBufferToArchivePacket(buffer, 0);
            stream.seekg(-(VP2Constants::APB_BYTES_PER_RECORD * 2), ios::cur);
            packetTime = packet.getDateTime();
        } while (startTime < packetTime && streamPosition > 0);

        //
        // The stream will not be good if the final seekg() call went past the beginning of the file.
        // Clear the error and position the next to be the beginning of the file.
        //
        if (!stream.good()) {
            stream.clear();
            stream.seekg(0, ios::beg);
        }
        else
            stream.seekg(VP2Constants::APB_BYTES_PER_RECORD * 2, ios::cur);
    }
    else
        stream.seekg(0, ios::beg);
    
    while (list.size() < VP2Constants::NUM_ARCHIVE_RECORDS) {
        stream.read(buffer, sizeof(buffer));
        if (stream.eof())
            break;

        ArchivePacket packet = station->convertBufferToArchivePacket(buffer, 0);
        list.push_back(packet);
        timeOfLastPacketSent = packet.getDateTime();
    }

    stream.close();
}
}