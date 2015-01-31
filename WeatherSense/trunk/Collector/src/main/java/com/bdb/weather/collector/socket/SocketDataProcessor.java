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
package com.bdb.weather.collector.socket;

import java.util.List;

/**
 *
 * @author Bruce
 */
public interface SocketDataProcessor {
    public static final int NO_TOKEN_FOUND = -1;
    /**
     * Consume the messages and provide a possible response
     * 
     * @param messages A list of messages to be consumed
     * @return The response that will be written to the socket or null if there is no response
     */
    public String consumeMessages(List<String> messages);

    /**
     * Find a token in the stream.
     * 
     * @param s The stream in which to look for a token
     * @return The last character of the token or NO_TOKEN_FOUND
     */
    public int findToken(String s);
}
