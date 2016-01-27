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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.weather.common.db.TemperatureBinTable;
import com.bdb.weather.common.measurement.Temperature;

/**
 * Class that manages the temperature bin list.
 *
 * @author Bruce
 */
public final class TemperatureBinMgr {
    private final Map<Integer,TemperatureBin> bins = new TreeMap<>();
    private final TemperatureBinTable table;

    /**
     * Constructor.
     *
     * @param connection The connection to the database server
     */
    public TemperatureBinMgr(DBConnection connection) {
        table = new TemperatureBinTable(connection);
    }

    /**
     * Get the temperature bin with the specified ID.
     *
     * @param id The bin ID
     * @return The temperature bin or null if not found
     */
    public TemperatureBin getBin(int id) {
        return bins.get(id);
    }

    /**
     * Check if the provided temperature is within the bin with the specified ID.
     *
     * @param id The bin ID
     * @param temperature The temperature to check
     * @return True of the temperature is within the bin
     */
    public boolean isTemperatureInBin(int id, Temperature temperature) {
        TemperatureBin bin = getBin(id);
        if (bin == null)
            return false;

        return bin.isInBin(temperature);
    }

    /**
     * Get the bin ID given a threshold and threshold type.
     *
     * @param threshold The threshold
     * @param type The threshold type
     * @return The bin ID or -1 if not found
     */
    public int getBinId(Temperature threshold, ThresholdType type) {
        for (TemperatureBin bin : bins.values()) {
            if (bin.getThresholdType() == type && bin.getThreshold().equals(threshold))
                return bin.getBinId();
        }

        return -1;
    }

    /**
     * Get all of the temperature bins
     *
     * @return The temperature bins
     */
    public Collection<TemperatureBin> getAllBins() {
        return Collections.unmodifiableCollection(bins.values());
    }

    /**
     * Replace the temperature bins with the provided list and synchronize the database.
     *
     * @param binList The new bin list
     */
    public void replaceBins(List<TemperatureBin> binList) {
        replaceBins(binList, true);
    }

    private void replaceBins(List<TemperatureBin> binList, boolean doSync) {
        bins.clear();
        binList.stream().forEach((bin) -> {
            bins.put(bin.getBinId(), bin);
        });

        if (doSync)
            sync();
    }

    /**
     * Refresh the temperature bins from the database.
     */
    public void refresh() {
        List<TemperatureBin> binList = table.retrieveBins();
        replaceBins(binList, false);
    }

    /**
     * Save the temperature bins in the database.
     * 
     * @return True if sync was successful
     */
    public boolean sync() {
        boolean rv = true;
        //
        // Save the bins, then get them again to set the IDs correctly
        //
        if (table.saveBins(bins.values()))
            refresh();
        else
            rv = false;

        return rv;
    }
}