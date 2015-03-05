/*
 * Copyright (C) 2015 Bruce
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
package com.bdb.weather.healthmonitor.piglow;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.system.SystemInfo;

/**
 *
 * @author Bruce
 */
public class PiGlow {
    private static final int ENABLE_OUTPUT_ADDR = 0x0;
    private static final int FIRST_LED_ADDR = 0x1;
    private static final int ENABLE_TOP_ARM_ADDR = 0x13;
    private static final int ENABLE_LEFT_ARM_ADDR = 0x14;
    private static final int ENABLE_RIGHT_ARM_ADDR = 0x15;
    private static final int COMMIT_ADDR = 0x16;
    private static final int I2C_ADDR = 0x54;
    private static final byte VALUE = (byte)0xFF;
    private static final byte ALL_OFF[] = {0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};
    private I2CBus bus;
    private I2CDevice device;
    private final byte[] intensities = new byte[18];

    public PiGlow() {
    }

    public void initialize() throws IOException, InterruptedException {
        SystemInfo.BoardType boardType = SystemInfo.getBoardType();
        int busNumber;
        switch (boardType) {
            case ModelA_Rev0:
            case ModelB_Rev1:
            case ModelB_Rev2:
                busNumber = I2CBus.BUS_0;
                break;
            default:
                busNumber = I2CBus.BUS_1;
                break;
        }

        bus = I2CFactory.getInstance(busNumber);
        device = bus.getDevice(I2C_ADDR);

        device.write(ENABLE_OUTPUT_ADDR, (byte)0x1);
        device.write(ENABLE_TOP_ARM_ADDR, VALUE);
        device.write(ENABLE_LEFT_ARM_ADDR, VALUE);
        device.write(ENABLE_RIGHT_ARM_ADDR, VALUE);
    }

    public void commit() throws IOException {
        device.write(COMMIT_ADDR, VALUE);
    }

    public void setLEDIntensity(PiGlowLED led, byte intensity) throws IOException {
        led.setIntensity(intensity);
        commit();
    }

    public void updateLEDs() throws IOException {
        PiGlowLED.allLEDs().stream().forEach((led) -> {
            intensities[led.getAddress() - FIRST_LED_ADDR] = (byte)led.getIntensity();
        });

        device.write(FIRST_LED_ADDR, intensities, 0, intensities.length);
    }

    public void allOff() throws IOException {
        device.write(FIRST_LED_ADDR, ALL_OFF, 0, ALL_OFF.length);
    }

    public static final void main(String args[]) {
        try {
            PiGlow pg = new PiGlow();
            pg.initialize();
            pg.setLEDIntensity(PiGlowLED.findLed(PiGlowArm.TOP, PiGlowColor.RED), (byte)128);
        }
        catch (IOException | InterruptedException ex) {
            Logger.getLogger(PiGlow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}