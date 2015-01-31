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

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DayOfYearCollection<T extends Comparable<T>> {
    private Map<Integer,T> collection = new HashMap<>();
    
    public DayOfYearCollection() {
    }
    
    private DayOfYearCollection(Map<Integer,T> collection) {
        this.collection.putAll(collection);
    }
    
    public DayOfYearCollection<T> unmodifiableClone() {
        DayOfYearCollection<T> c = new DayOfYearCollection<>(collection);
        c.makeUnmodifiable();
        return c;
    }
    
    public void addItem(LocalDate date, T item) {
        collection.put(makeKey(date), item);
    }
    
    public void addItem(Month month, int day, T item) {
        collection.put(makeKey(month, day), item);
    }
    
    public void putItem(LocalDate date, T item) {
        addItem(date, item);
    }
    
    public void putItem(Month month, int day, T item) {
        addItem(month, day, item);
    }
    
    public T item(Month month, int day) {
        return collection.get(makeKey(month, day));
    }
    
    public T item(LocalDate date) {
        return collection.get(makeKey(date));
    }
    
    public List<T> getAverages() {
        ArrayList<T> list = new ArrayList<>(collection.values());
        Collections.sort(list);
        return list;
    }
    
    private void makeUnmodifiable() {
        collection = Collections.unmodifiableMap(collection);
    }
    
    private int makeKey(LocalDate date) {
        return makeKey(date.getMonth(), date.getDayOfMonth());
    }
    
    private int makeKey(Month month, int day) {
        if (month == Month.FEBRUARY && day == 29)
        {
            month = Month.MARCH;
            day = 1;
        }
        
        return (month.getValue() * 100) + day;
    }
}