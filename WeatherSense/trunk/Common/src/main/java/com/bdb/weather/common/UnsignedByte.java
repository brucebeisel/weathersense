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
package com.bdb.weather.common;

public final class UnsignedByte {
    private int value = 0;

    public UnsignedByte(int value) {
        put(value);
    }

    int get() {
        return value;
    }

    void put(int value) {
        this.value = value;
        this.value &= 0xFF;
    }

    boolean isBitSet(int bit) throws IllegalArgumentException {
        if (bit < 0 || bit > 7)
            throw new IllegalArgumentException("Out of range bit");

        return ((1 << bit) & value) != 0;
    }

    void shiftLeft(int bits) throws IllegalArgumentException {
        if (bits < 0 || bits > 7)
            throw new IllegalArgumentException("Out of range bit");

        value <<= bits;

        value &= 0xFF;

    }

    void shiftRight(int bits) throws IllegalArgumentException {
        if (bits < 0 || bits > 7)
            throw new IllegalArgumentException("Out of range bit");

        value >>= bits;
    }
}
