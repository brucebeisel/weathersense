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
package com.bdb.weather.display.stripchart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 * @author Bruce
 */
public class StripChartLayout {
    public class AxisLayout {
        public String axisType;
        public List<String> visibleSeries = new ArrayList<>();
    }
    
    public class StripChartPlotLayout {
        public AxisLayout leftAxisLayout;
        public AxisLayout rightAxisLayout;
    }
    
    private static final String SPAN_NODE = "Span";
    private static final String PLOTS_NODE = "Plots";
    private static final String LEFT_AXIS_NODE = "LeftAxis";
    private static final String RIGHT_AXIS_NODE = "RightAxis";
    private static final String AXIS_TYPE_PREF = "AxisType";
    private static final String VISIBLE_SERIES_NODE = "VisibleSeries";
    private Preferences layoutNode;
    private String name;
    private String span;
    private final List<StripChartPlotLayout> plots = new ArrayList<>();
    
    public StripChartLayout(Preferences stripChartNode, String name) {
        this(stripChartNode, name, null, null);
    }
    
    public StripChartLayout(Preferences stripChartNode, String name, String span, List<StripChartPlotLayout> plots) {
        layoutNode = stripChartNode.node(name);
        this.name = name;
        
        if (span == null)
            loadLayout();
        else {
            this.span = span;
            plots.addAll(plots);
        }
    }
    
    public String getName() {
        return name;
    }
    
    public String getSpan() {
        return span;
    }
    
    public List<StripChartPlotLayout> getPlots() {
        return Collections.unmodifiableList(plots);
    }
    
    private void loadAxis(Preferences axisNode, AxisLayout axisLayout) throws BackingStoreException {
        axisLayout.axisType = axisNode.get(AXIS_TYPE_PREF, "");
        Preferences visibleSeriesNode = axisNode.node(VISIBLE_SERIES_NODE);
        axisLayout.visibleSeries.addAll(Arrays.asList(visibleSeriesNode.keys()));
    }
    
    public final void loadLayout() {
        try {
            span = layoutNode.get(SPAN_NODE, "1");
            Preferences plotsNode = layoutNode.node(PLOTS_NODE);
            int numPlots = plotsNode.childrenNames().length;
            
            for (int i = 0; i < numPlots; i++) {
                String plotName = "" + i;
                Preferences plotNode = plotsNode.node(plotName);
                
                StripChartPlotLayout plotLayout = new StripChartPlotLayout();
                plots.add(plotLayout);
  
                Preferences leftAxisNode = plotNode.node(LEFT_AXIS_NODE);
                loadAxis(leftAxisNode, plotLayout.leftAxisLayout);
                
                Preferences rightAxisNode = plotNode.node(RIGHT_AXIS_NODE);
                loadAxis(rightAxisNode, plotLayout.rightAxisLayout);
            }
        }
        catch (BackingStoreException e) {
            
        }
    }
    
    private void saveAxis(Preferences axisNode, AxisLayout axisLayout) {
        axisNode.put(AXIS_TYPE_PREF, axisLayout.axisType);
        Preferences visibleSeriesNode = axisNode.node(VISIBLE_SERIES_NODE);
        axisLayout.visibleSeries.stream().forEach((series) -> {
            visibleSeriesNode.put(series, "");
        });
    }
    
    public final void saveLayout() throws BackingStoreException {
        layoutNode.put(SPAN_NODE, span);
        Preferences plotsNode = layoutNode.node(PLOTS_NODE);
        plotsNode.clear();
        for (int i = 0; i < plots.size(); i++) {
            String plotName = "" + i;
            Preferences plotNode = plotsNode.node(plotName);
            StripChartPlotLayout plotLayout = plots.get(i);
 
            Preferences leftAxisNode = plotNode.node(LEFT_AXIS_NODE);
            saveAxis(leftAxisNode, plotLayout.leftAxisLayout);

            Preferences rightAxisNode = plotNode.node(RIGHT_AXIS_NODE);
            saveAxis(rightAxisNode, plotLayout.rightAxisLayout);
        }
    }
}