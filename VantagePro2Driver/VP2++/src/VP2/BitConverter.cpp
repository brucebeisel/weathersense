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
#include "BitConverter.h"

namespace vp2 {

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
BitConverter::BitConverter() {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
BitConverter::~BitConverter() {
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
int
BitConverter::toInt8(const byte buffer[], int index) {
    return static_cast<int>(buffer[index]) & 0xFF;
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
int
BitConverter::toInt16(const byte buffer[], int index, bool littleEndian) {
    return bitsToInt<short>(&buffer[index], littleEndian);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
int
BitConverter::toInt32(const byte buffer[], int index, bool littleEndian) {
    return bitsToInt<int>(&buffer[index], littleEndian);
}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void
BitConverter::getBytes(int value, byte buffer[], int index, int nbytes, bool littleEndian) {
    int bufferIndex;
    for (int i = 0; i < nbytes; i++) {
        if (littleEndian)
            bufferIndex = index + i;
        else
            bufferIndex = index + (nbytes - 1 - i);

        buffer[bufferIndex] = (value >> (8 * i)) & 0xFF;
    }

}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
template <typename T>
int
BitConverter::bitsToInt(const byte * bits, bool littleEndian) {
    int result = 0;
    if (littleEndian) {
        for (int n = sizeof(T) - 1; n >= 0; n--) {
            result = (result << 8) + (static_cast<int>(bits[n]) & 0xFF);
        }
    }
    else {
        for (int n = 0; n < sizeof(T); n++)
            result = (result << 8) + (static_cast<int>(bits[n]) & 0xFF);
    }

    return result;
}
}
