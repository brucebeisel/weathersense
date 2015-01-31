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
package com.bdb.util;

import java.util.Objects;

/**
 * Class to hold a pair of arbitrary objects. This is typically used to return multiple values of differing types.
 * @author Bruce
 * @param <T1> Type of the first parameter
 * @param <T2> Type of the second parameter
 */
public final class Pair<T1, T2> {
    /**
     * The first element. Though public is cannot be set.
     */
    public final T1 first;
    /**
     * The second element. Though public is cannot be set.
     */
    public final T2 second;

    /**
     * Constructor.
     * 
     * @param first The value of the first element
     * @param second The value of the second element
     */
    public Pair(T1 first, T2 second) {
       this.first = first;
       this.second = second;
    }
    
    /**
     * @return the first
     */
    public T1 getFirst() {
            return first;
    }

    /**
     * @return the second
     */
    public T2 getSecond() {
            return second;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.first);
        hash = 37 * hash + Objects.hashCode(this.second);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        // Previous check ensures that the cast will not fail
        @SuppressWarnings("unchecked")
        final Pair<T1,T2> other = (Pair<T1,T2>)obj;

        return other.first.equals(first) && other.second.equals(second);
    }
}