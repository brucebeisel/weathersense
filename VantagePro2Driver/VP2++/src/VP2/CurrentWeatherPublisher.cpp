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
#include <sys/socket.h>
#include <sys/types.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <unistd.h>
#include <ifaddrs.h>
#include <string.h>
#include <string>
#include "VP2Logger.h"
#include "CurrentWeatherPublisher.h"
#include "CurrentWeather.h"

using namespace std;

namespace vp2 {
const std::string CurrentWeatherPublisher::MULTICAST_HOST = "224.0.0.120";

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
CurrentWeatherPublisher::CurrentWeatherPublisher() : log(VP2Logger::getLogger("CurrentWeatherPublisher"))
{
    createSocket();
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
CurrentWeatherPublisher::~CurrentWeatherPublisher()
{
    if (socketId != NO_SOCKET)
        close(socketId);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
CurrentWeatherPublisher::sendCurrentWeather(const CurrentWeather & cw)
{
    if (socketId == NO_SOCKET)
        return;

    std::string s = cw.formatXML();
    const char * data = s.c_str();
    size_t length = strlen(data);
    if (sendto(socketId, data, length, 0, reinterpret_cast<struct sockaddr *>(&groupAddr), sizeof(groupAddr)) != length) {
        int e = errno;
        log.log(VP2Logger::VP2_WARNING) <<  "sendto() for current weather failed. Errno = " << e << endl;
    }
    else
        log.log(VP2Logger::VP2_INFO) << "Published current weather: " << data << endl;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
CurrentWeatherPublisher::getLocalIpAddress(struct sockaddr_in & saddr)
{
    bool rv = false;
    struct ifaddrs *addrs;
    struct ifaddrs *tmp;

    getifaddrs(&addrs);
    tmp = addrs;

    while (tmp) {
        if (tmp->ifa_addr && tmp->ifa_addr->sa_family == AF_INET) {
            struct sockaddr_in *pAddr = reinterpret_cast<struct sockaddr_in *>(tmp->ifa_addr);
            if (strncmp(tmp->ifa_name, "lo", 2) != 0) {
                printf("%s: %s\n", tmp->ifa_name, inet_ntoa(pAddr->sin_addr));
                saddr = *pAddr;
                rv = true;
                break;
            }
        }

        tmp = tmp->ifa_next;
    }

    freeifaddrs(addrs);

    return rv;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
CurrentWeatherPublisher::createSocket()
{
    if ((socketId = socket(AF_INET, SOCK_DGRAM, 0)) < 0) {
        socketId = NO_SOCKET;
        return false;
    }

    memset(reinterpret_cast<char *>(&groupAddr), 0, sizeof(groupAddr));
    groupAddr.sin_family = AF_INET;
    groupAddr.sin_addr.s_addr = inet_addr(MULTICAST_HOST.c_str());
    groupAddr.sin_port = htons(MULTICAST_PORT);

    struct sockaddr_in saddr;
    if (!getLocalIpAddress(saddr)) {
        log.log(VP2Logger::VP2_ERROR) <<  "setsockopt() getting local IP address failed." << endl;
        close(socketId);
        socketId = NO_SOCKET;
        return false;
    }

    if (setsockopt(socketId, IPPROTO_IP, IP_MULTICAST_IF, reinterpret_cast<char *>(&saddr.sin_addr), sizeof(saddr.sin_addr)) < 0) {
        int e = errno;
        log.log(VP2Logger::VP2_ERROR) <<  "setsockopt() for local interface failed. Errno = " << e << endl;
        close(socketId);
        socketId = NO_SOCKET;
        return false;
    }

    unsigned char ttl = 2;
    if (setsockopt(socketId, IPPROTO_IP, IP_MULTICAST_TTL, reinterpret_cast<char *>(&ttl), sizeof(ttl)) < 0) {
        int e = errno;
        log.log(VP2Logger::VP2_ERROR) <<  "setsockopt() for TTL failed. Errno = " << e << endl;
        close(socketId);
        socketId = NO_SOCKET;
        return false;
    }

    log.log(VP2Logger::VP2_INFO) << "Multicast socket created successfully" << endl;
    return true;
}

}
