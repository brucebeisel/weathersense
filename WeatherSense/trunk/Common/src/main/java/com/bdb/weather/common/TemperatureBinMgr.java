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
 *
 * @author Bruce
 */
public final class TemperatureBinMgr {
    private final Map<Integer,TemperatureBin> bins = new TreeMap<>();
    private final TemperatureBinTable table;

    public TemperatureBinMgr(DBConnection connection) {
        table = new TemperatureBinTable(connection);
    }

    public TemperatureBin getBin(int id) {
        return bins.get(id);
    }

    public boolean isTemperatureInBin(int id, Temperature t) {
        TemperatureBin bin = getBin(id);
        if (bin == null)
            return false;

        return bin.isInBin(t);
    }

    public int getBinId(Temperature threshold, ThresholdType type) {
        for (TemperatureBin bin : bins.values()) {
            if (bin.getThresholdType() == type && bin.getThreshold().equals(threshold))
                return bin.getBinId();
        }

        return -1;
    }

    public Collection<TemperatureBin> getAllBins() {
        return Collections.unmodifiableCollection(bins.values());
    }

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


    public void refresh() {
        List<TemperatureBin> binList = table.retrieveBins();
        replaceBins(binList, false);
    }

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