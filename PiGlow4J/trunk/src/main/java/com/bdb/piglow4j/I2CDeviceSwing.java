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
package com.bdb.piglow4j;

import java.io.IOException;
import java.util.Arrays;

import javax.swing.SwingUtilities;

import com.pi4j.io.i2c.I2CDevice;

/**
 *
 * @author Bruce Beisel
 */
public class I2CDeviceSwing implements I2CDevice {
    private PiGlowGUI gui;

    public I2CDeviceSwing() {
        gui = new PiGlowGUI();
        SwingUtilities.invokeLater(()->gui.createElements());
    }

    @Override
    public void write(byte b) throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void write(byte[] bytes, int offset, int size) throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void write(int address, byte b) throws IOException {
        byte buffer[] = {b};
        gui.processBytes(address, buffer, 1);
    }

    @Override
    public void write(int address, byte[] bytes, int offset, int size) throws IOException {
        byte buffer[] = Arrays.copyOfRange(bytes, offset, size - offset);
        gui.processBytes(address, buffer, size);
    }

    @Override
    public int read() throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public int read(byte[] bytes, int i, int i1) throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public int read(int i) throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public int read(int i, byte[] bytes, int i1, int i2) throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public int read(byte[] bytes, int i, int i1, byte[] bytes1, int i2, int i3) throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }
}