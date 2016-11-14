/* 
 * Copyright (C) 2016 Bruce Beisel
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
package com.bdb.weather.common.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.jdbc.DBConnection;
import com.bdb.util.jdbc.DBTable;

import com.bdb.weather.common.CollectorCommand;
import com.bdb.weather.common.CollectorCommand.CollectorCommandState;

/**
 * Class that provides access to the collector command database table.
 *
 * @author Bruce
 */
public class CollectorCommandsTable extends DBTable<CollectorCommand> {
    /**
     * The table name
     */
    protected static final String TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".collector_commands";
    private static final String ID_COLUMN = "id";
    private static final String COMMAND_COLUMN = "command";
    private static final String STATE_COLUMN = "state";
    private static final Logger logger = Logger.getLogger(CollectorCommandsTable.class.getName());

    /**
     * Constructor.
     * 
     * @param connection The connection to the database server
     */
    public CollectorCommandsTable(DBConnection connection) {
        super(TABLE_NAME, connection);
    }
    
    /*
     * (non-Javadoc)
     * @see com.bdb.util.jdbc.DBTable#query(java.lang.String)
     */
    @Override
    public List<CollectorCommand> query(String clause) {
        String  sql = "select * from " + TABLE_NAME + " ";

        if (clause != null)
            sql = sql + clause;
        
        List<CollectorCommand> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            int id = rs.getInt(ID_COLUMN);
            String command = rs.getString(COMMAND_COLUMN);
            CollectorCommandState state = CollectorCommandState.valueOf(rs.getString(STATE_COLUMN));
            
            return new CollectorCommand(id, command, state);
        });
        
        return list;
    }

    /**
     * Retrieve all commands that have not been processed.
     * 
     * @return The list of commands
     */
    public List<CollectorCommand> retrieveNewCommands() {
        return query("where " + STATE_COLUMN + "='" + CollectorCommandState.NEW + "'");
    }

    @Override
    public boolean addRow(CollectorCommand record) {
        String sql = "insert into " + TABLE_NAME + " (" + COMMAND_COLUMN + ") " + "values (?)";
                                      
        try {
            try (PreparedStatement stmt = getConnection().getConnection().prepareStatement(sql)) {
                stmt.setString(1, record.getCommand());
                return stmt.executeUpdate() == 1;
            }
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving collector command.", e);
            return false;
        }
    }

    @Override
    public boolean deleteRow(CollectorCommand record) {
        String sql = "delete from " + TABLE_NAME + " where " + ID_COLUMN + "=" + record.getId();
        return getConnection().executeUpdate(sql) == 1;

    }

    /**
     * Add a new command to the table.
     * 
     * @param command The command
     * @return True of the command was added successfully
     */
    public boolean addCommand(String command) {
        return addRow(new CollectorCommand(0, command, CollectorCommandState.NEW));
    }

    /**
     * Update the state of a command.
     * 
     * @param id The ID of the command
     * @param state The new state of the command
     * @return True if the command was updated successfully
     */
    public boolean updateCommandState(int id, CollectorCommandState state) {
        final String sql = "update " + TABLE_NAME + " set " + STATE_COLUMN + "='" + state + "' where " + ID_COLUMN + "=" + id;

        return getConnection().executeUpdate(sql) == 1;
    }
}
