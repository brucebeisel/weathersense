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

/**
 *
 * @author Bruce
 */
public final class CollectorCommand {
    public enum CollectorCommandState {
        NEW,
        EXECUTING,
        COMPLETE
    }
    private final int id;
    private final String command;
    private final CollectorCommandState state;

    public static final String COMMAND_DATE_FORMAT = "yyyyMMdd";
    public static final String SUMMARIZE_COMMAND = "summarize";
    public static final String SUMMARIZE_ALL = "all";

    public CollectorCommand(int id, String command, CollectorCommandState state) {
        this.id = id;
        this.command = command;
        this.state = state;
    }
    
    public int getId() {
        return id;
    }

    public String getCommand() {
        return command;
    }

    public CollectorCommandState getState() {
        return state;
    }
}