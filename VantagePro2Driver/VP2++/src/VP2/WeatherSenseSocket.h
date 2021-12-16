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
#ifndef WEATHERSENSE_SOCKET_H
#define WEATHERSENSE_SOCKET_H

#include <string>
#include "VP2Logger.h"
#include "Weather.h"

namespace vp2 {
/**
 * Class that communicates with a Vantage Pro 2 client using a TCP/IP socket.
 * The Vantage Pro 2 driver is the server socket.
 */
class WeatherSenseSocket {
public:
    /**
     * Indicates that there is no socket connection.
     */
    static const int NO_SOCKET = -1;

    /**
     * Interface used to notify a client that a connection has been established with the collector.
     * 
     * @param time The newest archive record that the collector reported
     */
    class HistoricalReader {
    public:
    	virtual ~HistoricalReader() {}
        virtual void connected(DateTime time) = 0;
    };

    /**
     * Constructor.
     * 
     * @param host The host to which to connect
     * @param port The port on which to connect
     */
    WeatherSenseSocket(const std::string & host, unsigned short port);

    /**
     * Destructor.
     */
    virtual ~WeatherSenseSocket();

    /**
     * Set the historical reader client, only one allowed.
     * 
     * @param The reader
     */
    void setHistoricalReader(HistoricalReader &);

    /**
     * Disconnect from the WeatherSense collector.
     */
    void disconnectSocket();

    /**
     * Check to see of there is a connection with the WeatherSense collector.
     * 
     * @return True if a connection exists
     */
    bool isSocketConnected() const;

    /**
     * Send data to the WeatherSense collector, connecting of necessary.
     * 
     * @param data The data to send
     * 
     * @return True if the data was sent successfully
     */
    bool sendData(const std::string & data);

private:
    /**
     * Connect with the WeatherSense collector.
     */
    void connectSocket();

    /**
     * Perform the write to the socket.
     * 
     * @param The string to write to the socket
     */
    void writeString(const std::string &);

    std::string        host;
    unsigned short     port;
    int                socketId;
    HistoricalReader * reader;
    VP2Logger          logger;
};
}

#endif /* WEATHERSENSE_SOCKET_H */
