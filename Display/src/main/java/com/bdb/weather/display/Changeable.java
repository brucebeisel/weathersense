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
package com.bdb.weather.display;

public interface Changeable {
    /**
     * Check if the data being managed by this class have changed.
     * 
     * @return True of the data has changed
     */
    boolean hasChanged();
    
    /**
     * Reset the data being managed by this class so that hasChanged() will return false.
     */
    void resetData();
    
    /**
     * Save the data.
     * 
     * @return True if the data was saved successfully
     */
    boolean saveData();
}
