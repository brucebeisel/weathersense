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
 * Class the represents a collector command that is sent to the collector from the user interface.
 * 
 * @author Bruce
 */
public final class CollectorCommand {

    /**
     * Enum for tracking the state of the collector commands.
     */
    public enum CollectorCommandState {
        /**
         * The command is new
         */
        NEW,
        /**
         * The command is being executed by the collector
         */
        EXECUTING,
        /**
         * The command has been executed by the collector
         */
        COMPLETE
    }
    private final int id;
    private final String command;
    private final CollectorCommandState state;

    /**
     * The format of dates that are used as command arguments
     */
    public static final String COMMAND_DATE_FORMAT = "yyyyMMdd";
    /**
     * Command token to request the collector to summarize days
     */
    public static final String SUMMARIZE_COMMAND = "summarize";
    /**
     * Argument to the summarize command that indicates all date should be summarized
     */
    public static final String SUMMARIZE_ALL = "all";

    /**
     * Constructor.
     * 
     * @param id Unique ID of the command
     * @param command The commands
     * @param state The current state of the command
     */
    public CollectorCommand(int id, String command, CollectorCommandState state) {
        this.id = id;
        this.command = command;
        this.state = state;
    }
    
    /**
     * Get the unique ID.
     * 
     * @return The unique ID
     */
    public int getId() {
        return id;
    }

    /**
     * Get the command string.
     * 
     * @return The command string
     */
    public String getCommand() {
        return command;
    }

    /**
     * Get the state of the command.
     * 
     * @return The command state
     */
    public CollectorCommandState getState() {
        return state;
    }
}