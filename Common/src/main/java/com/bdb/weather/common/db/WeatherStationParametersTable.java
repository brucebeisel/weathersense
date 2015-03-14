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
package com.bdb.weather.common.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bdb.util.Pair;
import com.bdb.util.jdbc.DBConnection;
import com.bdb.util.jdbc.DBTable;

import com.bdb.weather.common.messages.WsParametersMessage;


/**
 *
 * @author Bruce
 */
public class WeatherStationParametersTable extends DBTable<Pair<String,String>> {
    private static final String TABLE_NAME = DatabaseConstants.DATABASE_NAME + ".weather_station_parameters";
    private static final String INSERT_SQL = "insert into " + TABLE_NAME + " values(?,?)";
    private static final Logger logger = Logger.getLogger(WeatherStationParametersTable.class.getName());
    
    public WeatherStationParametersTable(DBConnection connection) {
        super(TABLE_NAME,  connection);
    }

    @Override
    public List<Pair<String,String>> query(String claus) {
        String sql = "select * from " + TABLE_NAME + " ";

        List<Pair<String,String>> list = executeQuery(sql, (ResultSet rs, Object... args) -> {
            Pair<String,String> parameter = new Pair<>(rs.getString(1), rs.getString(2));
            return parameter;
        });

        return list;
    }

    @Override
    public boolean addRow(Pair<String,String> parameter) {
        try {
            try (PreparedStatement stmt = getConnection().getConnection().prepareStatement(INSERT_SQL)) {
                stmt.setString(1, parameter.first);
                stmt.setString(2, parameter.second);
                return stmt.executeUpdate() == 1;
            }
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Add row failed", e);
            return false;
        }
    }

    public void getParameters(WsParametersMessage parameters) {
        List<Pair<String,String>> list = query(null);

        list.stream().forEach((parameter) -> {
            parameters.addParameter(parameter.first, parameter.second);
        });
    }

    public void setParameters(WsParametersMessage parameters) {
        this.getConnection().startTransaction();
        deleteAllRows();
        for (String key : parameters.getKeys()) {
            if (!addRow(new Pair<>(key, parameters.getValueForKey(key)))) {
                this.getConnection().rollback();
                logger.severe("Add row failed, rolling back transaction");
                return;
            }
        }

        if (!this.getConnection().commit()) {
            this.getConnection().rollback();
            logger.severe("Commit failed, rolling back transaction");
        }
    }
}