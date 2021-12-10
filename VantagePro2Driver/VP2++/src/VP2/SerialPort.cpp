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
#ifndef __CYGWIN__
#include <fcntl.h>
#include <termios.h>
#include <errno.h>
#include <unistd.h>
static const int INVALID_HANDLE_VALUE = -1;
#else
#include <winsock2.h>
#endif
#include <iostream>
#include "VP2Logger.h"
#include "Weather.h"
#include "SerialPort.h"

using namespace std;

namespace vp2 {

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
SerialPort::SerialPort(const std::string & device, int baudRate) : commPort(INVALID_HANDLE_VALUE), device(device), baudRate(baudRate), log(VP2Logger::getLogger("SerialPort")) {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
SerialPort::~SerialPort(){
}

#ifdef __CYGWIN__
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
SerialPort::open() {
    commPort = CreateFile(device.c_str(), GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, 0, 0);

    if (commPort == INVALID_HANDLE_VALUE)
        return false;

    DCB dcb;
    GetCommState(commPort, &dcb);
    dcb.BaudRate = baudRate;
    dcb.Parity = NOPARITY;
    dcb.ByteSize = 8;
    dcb.StopBits = ONESTOPBIT;
    SetCommState(commPort, &dcb);

    COMMTIMEOUTS cto;
    cto.ReadIntervalTimeout = 100;
    cto.ReadTotalTimeoutMultiplier = 10;
    cto.ReadTotalTimeoutConstant = 1000;
    cto.WriteTotalTimeoutConstant = 5000;
    cto.WriteTotalTimeoutMultiplier = 1;
    return SetCommTimeouts(commPort, &cto) == TRUE;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
SerialPort::close() {
    CloseHandle(commPort);
}


////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
int
SerialPort::write(const void * buffer, int nbytes) {
    log.log(VP2Logger::VP2_DEBUG2) << "Writing " << nbytes << " bytes" << endl;
    DWORD dwWritten;
    if (!WriteFile(commPort, static_cast<LPCVOID>(buffer), nbytes, &dwWritten, nullptr))
        return -1;
    else
        return dwWritten;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
int
SerialPort::read(byte buffer[], int index, int nbytes) {
    DWORD dwRead;
    if (!ReadFile(commPort, &buffer[index], nbytes, &dwRead, nullptr)) {
        log.log(VP2Logger::VP2_INFO) << "Read " << dwRead << " bytes (failed)" << endl;
        return -1;
    }
    else {
        log.log(VP2Logger::VP2_DEBUG3) << "Read " << dwRead << " bytes" << endl;
        return dwRead;
    }
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
SerialPort::discardInBuffer() {
    PurgeComm(commPort, PURGE_TXABORT | PURGE_RXABORT | PURGE_TXCLEAR | PURGE_RXCLEAR);
}
#else
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
SerialPort::open() {
    commPort = ::open(device.c_str(), O_RDWR);
    if (commPort < 0)
        return false;

    struct termios tio;
    if (tcgetattr(commPort, &tio) != 0) {
        log.log(VP2Logger::VP2_ERROR) << "tcgetattr() failed" << endl;
        return false;
    }

    cfsetospeed(&tio, B19200);
    cfsetispeed(&tio, B0);
    cfmakeraw(&tio);
    tio.c_cflag &= ~(PARENB | CSTOPB);
    tio.c_cflag != CS8;
    int rv = tcsetattr(commPort, 0, &tio);

    if (rv != 0) {
        log.log(VP2Logger::VP2_ERROR) << "tcsetattr() failed" << endl;
        return false;
    }

    discardInBuffer();

    return true;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
SerialPort::close() {
    ::close(commPort);
    commPort = -1;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
int
SerialPort::write(const void * buffer, int nbytes) {
    ssize_t bytesWritten = ::write(commPort, buffer, nbytes);
    return bytesWritten;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
int
SerialPort::read(byte buffer[], int index, int nbytes) {
    ssize_t bytesRead = 0;
    struct timeval timeout = {2, 0};
    fd_set readSet;
    FD_ZERO(&readSet);
    FD_SET(commPort, &readSet);

    int numFdsSet = select(commPort + 1, &readSet, nullptr, nullptr, &timeout);
    if (numFdsSet < 0) {
        int err = errno;
        log.log(VP2Logger::VP2_WARNING) << "Select() failed. Errno = " << err << endl;
    }
    else if (numFdsSet == 0) {
        log.log(VP2Logger::VP2_DEBUG1) << "Select() timed out" << endl;
    }
    else {
        if (FD_ISSET(commPort, &readSet)) {
            bytesRead = ::read(commPort, &buffer[index], nbytes);
            log.log(VP2Logger::VP2_DEBUG2) << "Read " << bytesRead << " bytes" << endl;
        }
    }

    return bytesRead;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
SerialPort::discardInBuffer() {
    tcflush(commPort, TCIOFLUSH);
}
#endif

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
int
SerialPort::write(const string & s) {
    return write(s.c_str(), s.length());
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
SerialPort::read(byte buffer[], int expectedBytes) {
    log.log(VP2Logger::VP2_DEBUG2) << "Attempting to read " << expectedBytes << " bytes" << endl;
    int readIndex = 0;
    
    //
    // Keep reading until a timeout or the bytes read is correct
    //
    for (int i = 0; i < 2 && readIndex < expectedBytes; i++) {
        int nbytes = this->read(buffer, readIndex, expectedBytes - readIndex);
        if (nbytes > 0) {
            readIndex += nbytes;
            log.log(VP2Logger::VP2_DEBUG2) << "Read " << readIndex << " bytes of " << expectedBytes << " bytes" << endl;
        }
        else if (nbytes < 0) {
            break;
        }
    }

    if (readIndex < expectedBytes) {
        this->discardInBuffer();
        log.log(VP2Logger::VP2_INFO) << "Failed to read requested bytes. Expected=" << expectedBytes << ", Actual=" << readIndex << endl;
        return false;
    }
    else {
        log.log(VP2Logger::VP2_DEBUG3) << Weather::dumpBuffer(buffer, expectedBytes);
        return true;
    }
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
SerialPort::setBaudRate(int rate) {
    baudRate = rate;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
bool
SerialPort::isOpen() const {
    return commPort != INVALID_HANDLE_VALUE;
}
}
