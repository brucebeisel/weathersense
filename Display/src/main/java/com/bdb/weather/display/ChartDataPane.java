/*
 * Copyright (C) 2015 bruce
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

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 *
 * @author bruce
 */
public abstract class ChartDataPane extends TabPane {
    private final Tab chartTab = new Tab(DisplayConstants.GRAPH_TAB_NAME);
    private final Tab dataTab = new Tab(DisplayConstants.DATA_TAB_NAME);

    public ChartDataPane() {
        this.getTabs().add(chartTab);
        this.getTabs().add(dataTab);
        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
    }

    protected final void setTabContents(Node chartNode, Node dataNode) {
        chartTab.setContent(chartNode);
        dataTab.setContent(dataNode);
    }
}