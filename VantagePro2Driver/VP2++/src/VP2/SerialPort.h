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
#ifndef SERIAL_PORT_H
#define SERIAL_PORT_H
#ifdef __CYGWIN__
#include <windows.h>
#else
typedef int HANDLE;
#endif
#include <string>

namespace vp2 {

/**
 * Class to communicate over the serial port to the VP2 console.
 */
class SerialPort {
public:
    /**
     * Constructor.
     * 
     * @param device The serial device to open
     * @param baudRate The baud rate at which to communicate with the console
     */
    SerialPort(const std::string & device, int baudRate);

    /**
     * Destructor.
     */
    ~SerialPort();

    /**
     * Open the serial port.
     * 
     * @return True if successful
     */
    bool open();

    /**
     * Close the serial port.
     */
    void close();

    /**
     * Read from the serial port.
     * 
     * @param buffer The buffer into which the data will be read
     * @param index  The index into the buffer where bytes read will be stored
     * @param nbytes The number of bytes to read
     *
     * @return The number of bytes actually read
     */
    int read(byte * buffer, int index, int nbytes);

    /**
     * Read from the serial port.
     * 
     * @param buffer The buffer into which the data will be read
     * @param nbytes The number of bytes to read
     *
     * @return The number of bytes actually read
     */
    bool read(byte * buffer, int nbytes);

    /**
     * Write a string to the serial port.
     * 
     * @param s The string to be converted to bytes then written to the serial port
     *
     * @return The number of bytes that were written
     */
    int write(const std::string & s);

    /**
     * Write to the serial port.
     * 
     * @param buffer The bytes to write to the serial port
     * @param nbytes The number of bytes to write
     *
     * @return The number of bytes that were written
     */
    int write(const void * buffer, int nbytes);

    /**
     * Discard any bytes in the read buffer.
     */
    void discardInBuffer();

    /**
     * Set the baud rate. Note the serial port must be closed or it must be closed then opened for this to take effect.
     *
     * @param rate The new baud rate
     */
    void setBaudRate(int rate);

    /**
     * Check if the serial port is open.
     *
     * @return True if the port is open
     */
    bool isOpen() const;

private:
    HANDLE      commPort;    // The file descriptor of the open port
    std::string device;     // The name of the serial port to be opened
    int         baudRate;   // The baud rate used to communicate over the serial port
    VP2Logger   log;
};
}
#endif
