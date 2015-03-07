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
package com.bdb.piglow4j;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
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
    private static final Logger logger = Logger.getLogger(PiGlow.class.getName());

    public PiGlow() {
    }

    /**
     * This method fixes a bug in pi4j that does not report the board type correctly for RPi 2 Rev B
     * 
     * @return The board type
     */
    private SystemInfo.BoardType getBoardType() {
        try {
            String revision = SystemInfo.getRevision();
            long irevision = Long.parseLong(revision, 16);
            long scheme = (irevision >> 20) & 0xF;
            long ram = (irevision >> 16) & 0xF;
            long manufacturer = (irevision >> 12) & 0xF;
            long processor = (irevision >> 8) & 0xF;
            long type = (irevision >> 4) & 0xFF;
            long rev = irevision & 0xF;

            logger.fine(String.format("Board Revision: Scheme: %d RAM: %d Manufacturer %d Processor: %d Type: %d Revision: %d",
                                      scheme, ram, manufacturer, processor, type, rev));
            if (scheme == 0)
                return SystemInfo.getBoardType();
            else if (type == 4)
                return SystemInfo.BoardType.Model2B_Rev1;
            else
                return SystemInfo.BoardType.UNKNOWN;
        }
        catch (IOException | InterruptedException ex) {
            logger.log(Level.SEVERE, "Failed to determine RPi board type", ex);
            return SystemInfo.BoardType.UNKNOWN;
        }
    }

    public void initialize() throws IOException, InterruptedException {
        SystemInfo.BoardType boardType = getBoardType();
        int busNumber = I2CBus.BUS_0;
        switch (boardType) {
            case ModelA_Rev1:
            case ModelB_Rev1:
            case ModelB_Rev2:
                busNumber = I2CBus.BUS_0;
                break;

            case ModelA_Plus_Rev1:
            case ModelB_Plus_Rev1:
            case Model2B_Rev1:
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
        device.write(led.getAddress(), intensity);
        commit();
    }

    public void updateLEDs() throws IOException {
        PiGlowLED.allLEDs().stream().forEach((led) -> {
            intensities[led.getAddress() - FIRST_LED_ADDR] = (byte)led.getIntensity();
        });

        device.write(FIRST_LED_ADDR, intensities, 0, intensities.length);
        commit();
    }

    public void allOff() {
        System.out.println("Turning all off");
        try {
            device.write(FIRST_LED_ADDR, ALL_OFF, 0, ALL_OFF.length);
            commit();
        }
        catch (IOException ex) {
            Logger.getLogger(PiGlow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static final void main(String args[]) {
        try {
            PiGlow pg = new PiGlow();
            Runtime.getRuntime().addShutdownHook(new Thread(()->pg.allOff()));
            pg.initialize();
            List<PiGlowLED> leds = PiGlowLED.allLEDs();
            for (int i = 0; i < 10; i++) 
		for (PiGlowLED led : leds) {
		    pg.setLEDIntensity(led, (byte)10);
		    Thread.sleep(100);
		    pg.setLEDIntensity(led, (byte)0);
		}

	
	    for (PiGlowLED led : leds)
                led.setIntensity(1);

            pg.updateLEDs();
            Thread.sleep(2000);
        }
        catch (IOException | InterruptedException ex) {
            Logger.getLogger(PiGlow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
