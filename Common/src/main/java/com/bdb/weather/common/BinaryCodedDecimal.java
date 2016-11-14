/* 
 * Copyright (C) 2016 Bruce Beisel
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
package com.bdb.weather.common;

/**
 * Class that provides static methods for manipulating Binary Coded Decimals
 * @author bruce
 */
public final class BinaryCodedDecimal {

    private BinaryCodedDecimal() {
    }

    /**
     * Decode a BCD into an integer.
     * 
     * @param data The BCD data
     * @param index The index into the data to start decoding
     * @param digits The number of digits to be decoded
     * @return The decoded integer
     */
    public static int decode(int data[], int index, int digits) {
        int value = 0;

        for (int i = digits - 1; i >= 0; i--) {
            int b = data[index + (i / 2)];

            value *= 10;

            if (i % 2 == 0) {
                value += (b & 0xf);
            }
            else {
                value += ((b >> 4) & 0xf);
            }
        }

        return value;
    }
}
