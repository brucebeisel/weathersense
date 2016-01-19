/*
 * Copyright (C) 2016 bruce
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

/**
 * The collector package contains the classes needed collect data and populate
 * the weathersense database. This package does the following:
 * <ol>
 * <li>Communicates with a weather station driver to receive current weather that is then published
 * using Multicast UDP</li>
 * <li>Communicates with a weather station driver to collect archived data and save it in the database</li>
 * <li>Create summary records for each day in which there are archive records</li>
 * <li>Retrieve Doppler radar images from the Internet and save them in the database as both
 * temporary images or as images permanently stored in association with a storm</li>
 * <li>Monitors a table in the database for commands to recalculate the summary records</li>
 */
package com.bdb.weather.collector;
