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
#ifdef _WIN32
#include <basetsd.h>
#include <winsock2.h>
#include <winioctl.h>
#include <io.h>
typedef long ssize_t;
#define ioctl ioctlsocket
#define close closesocket
#else
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <string.h>
#include <stdio.h>
#include <errno.h>
#endif
#include <iostream>
#include "WeatherSenseSocket.h"

using namespace std;

namespace vp2 {

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
WeatherSenseSocket::WeatherSenseSocket(const string & host, unsigned short port) : reader(nullptr),
                                                                        host(host),
                                                                        port(port),
                                                                        socketId(NO_SOCKET),
                                                                        logger(VP2Logger::getLogger("WeatherSenseSocket")) {
#ifdef _WIN32
{
    WSAData data;
    if (WSAStartup(MAKEWORD(2,2), &data) != 0) {
        logger.log(VP2Logger::VP2_ERROR) << "WSAStartup() failed" << endl;
    }
}
#endif
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
WeatherSenseSocket::~WeatherSenseSocket() {
#ifdef _WIN32
    WSACleanup();
#endif
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
WeatherSenseSocket::setHistoricalReader(HistoricalReader & reader) {
    this->reader = &reader;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
WeatherSenseSocket::disconnectSocket() {
    if (socketId != NO_SOCKET) {
        close(socketId);
        socketId = NO_SOCKET;
    }
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
WeatherSenseSocket::isSocketConnected() const {
    return socketId != NO_SOCKET;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
WeatherSenseSocket::writeString(const string & s) {
    if (socketId != NO_SOCKET) {
        string stringWithNewline = s + "\n";
        const char * buffer = stringWithNewline.c_str();
        int bytesToWrite = stringWithNewline.size();
        if (send(socketId, buffer, bytesToWrite, 0) != bytesToWrite) {
            logger.log(VP2Logger::VP2_ERROR) << "Send on collector socket failed" << endl;
            disconnectSocket();
        }
    }
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
WeatherSenseSocket::connectSocket() {
    //
    // Skip it if we are already connected
    //
    if (socketId == NO_SOCKET) {           
        socketId = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

        if (socketId == NO_SOCKET) {
#ifdef _WIN32
            int err = WSAGetLastError();
#else
            int err = errno;
#endif
            logger.log(VP2Logger::VP2_ERROR) << "Failed to create socket to communicate with collector. Error = " << err << endl;
            return;
        }

        struct sockaddr_in sa;
        memset(&sa, 0, sizeof(sa));
        sa.sin_family = AF_INET;
        sa.sin_port = htons(port);
        sa.sin_addr.s_addr = inet_addr(host.c_str());
        
        if (connect(socketId, (const sockaddr *)&sa, sizeof(sa)) != 0) {
#ifdef _WIN32
            int err = WSAGetLastError();
#else
            int err = errno;
#endif
            logger.log(VP2Logger::VP2_ERROR) << "Failed to connect with the collector. Error = " << err << endl;
            disconnectSocket();
            return;
        }

 
        //
        // Once we connect, send the collector an initialize command to which it should
        // respond with the date of the most newest record in its database
        //
        writeString("message_type=initialize;");

        //
        // Wait for data to be available for about 5 seconds
        unsigned long availableBytes = 0;
        for (int i = 0; i < 50 && availableBytes < 13; i++) {
            ioctl(socketId, FIONREAD, &availableBytes);
            Weather::sleep(1000);
        }

        //
        // If data is available, read it and parse the data as a date
        //
        if (availableBytes > 0) {
            byte buffer[100];
            ssize_t bytesRead = recv(socketId, buffer, sizeof(buffer), 0);
            buffer[bytesRead] = '\0';
            logger.log(VP2Logger::VP2_DEBUG1) << "Read " << bytesRead << " bytes: '" << buffer << "' from collector" << endl;
            time_t now = ::time(0);
            struct tm tm;
            Weather::localtime(now, tm);
#ifdef _WIN32
            sscanf_s(buffer, "%4d%2d%2d %2d%2d", &tm.tm_year, &tm.tm_mon, &tm.tm_mday, &tm.tm_hour, &tm.tm_min);
#else
            sscanf(buffer, "%4d%2d%2d %2d%2d", &tm.tm_year, &tm.tm_mon, &tm.tm_mday, &tm.tm_hour, &tm.tm_min);
#endif
            tm.tm_year -= TIME_STRUCT_YEAR_OFFSET;
            tm.tm_mon--;
            tm.tm_sec = 0;
            DateTime time = mktime(&tm);

            reader->connected(time);
        }
        else {
            disconnectSocket();
            logger.log(VP2Logger::VP2_WARNING) << "No response received from collector. Closing socket." << endl;
        }
    }
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
WeatherSenseSocket::sendData(const string & data) {
    
    connectSocket();
    writeString(data);
    
    return true;
}
}
