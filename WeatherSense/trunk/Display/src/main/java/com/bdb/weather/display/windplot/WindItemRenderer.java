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
package com.bdb.weather.display.windplot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

/**
 *
 * @author Bruce
 */
@SuppressWarnings("serial")
public class WindItemRenderer extends XYLineAndShapeRenderer {
    private Paint windDirectionPaint = Color.BLACK;
    private Stroke windDirectionStroke = new BasicStroke();
    private int   directionLineLength = 10;
    private boolean showWindDirectionLines = true;
    private State state;
    
    public WindItemRenderer() {
        super();
        setBaseShapesVisible(false);
    }
    
    public void setWindDirectionPaint(Paint paint) {
        windDirectionPaint = paint;
    }
    
    public Paint getWindDirectionPaint() {
        return windDirectionPaint;
    }
    
    public void setWindDirectionStroke(Stroke stroke) {
        windDirectionStroke = stroke;
    }
    
    public Stroke getWindDirectionStroke() {
        return windDirectionStroke;
    }
    
    public void setDirectionLineLength(int length) {
        directionLineLength = length;
    }
    
    public int getDirectionLineLength() {
        return directionLineLength;
    }
    
    public void showWindDirectionLines(boolean show) {
        showWindDirectionLines = show;
    }
    
    public boolean areWindDirectionLinesShown() {
        return showWindDirectionLines;
    }
    
    @Override
    public int getPassCount() {
        return 3;
    }
    
    @Override
    public XYItemRendererState initialise(Graphics2D g2,
                                          Rectangle2D dataArea,
                                          XYPlot plot,
                                          XYDataset data,
                                          PlotRenderingInfo info) {
        super.initialise(g2, dataArea, plot, data, info);
        state = new State(info);
        state.seriesPath = new GeneralPath();
        return state;
    }
    
    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param rendererState  the renderer state.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color
     *              information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot
     *                        (<code>null</code> permitted).
     * @param pass  the pass index.
     */
    @Override
    public void drawItem(Graphics2D g2,
                         XYItemRendererState rendererState,
                         Rectangle2D dataArea,
                         PlotRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset dataset,
                         int series,
                         int item,
                         CrosshairState crosshairState,
                         int pass) {
        //
        // Let the base class handle drawing the line and the shapes (passes 0 and 1). This class will handle drawing the
        // wind direction lines.
        //
        if (pass < 2)
            super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState, pass);
        else {
            if (!(dataset instanceof TimeSeriesCollection) || !showWindDirectionLines)
                return;
            
            if (item == 0)
                state.resetLastDirection();
            
            RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge yAxisLocation = plot.getRangeAxisEdge();

            TimeSeriesCollection collection = (TimeSeriesCollection)dataset;
            TimeSeries timeSeries = collection.getSeries(series);
            
            if (!(timeSeries instanceof WindSeries))
                return;
            
            WindSeries windSeries = (WindSeries)timeSeries;
            WindSeriesDataItem windItem = windSeries.getWindDataItem(item);
            double speed = windItem.getWindSpeed().doubleValue();
            double time = dataset.getXValue(series, item);
            double dir = windItem.getWindDirection().doubleValue();
            
            if (speed > 0.0 && dir != state.getLastDirection()) {
                state.setLastDirection(dir);
                double radians = Math.toRadians(dir - 90.0);
                double dirXOffset = directionLineLength * Math.cos(radians);
                double dirYOffset = directionLineLength * Math.sin(radians);

                double transTime = domainAxis.valueToJava2D(time, dataArea, xAxisLocation);
                double transSpeed = rangeAxis.valueToJava2D(speed, dataArea, yAxisLocation);
                double dirX = transTime + dirXOffset;
                double dirY = transSpeed + dirYOffset;

                // update path to reflect latest point
                if (!Double.isNaN(transTime) && !Double.isNaN(transSpeed)) {
                    int x1 = (int) transTime;
                    int y1 = (int) transSpeed;
                    int x2 = (int) dirX;
                    int y2 = (int) dirY;
                    PlotOrientation orientation = plot.getOrientation();
                    if (orientation == PlotOrientation.HORIZONTAL) {
                        x1 = (int) transSpeed;
                        y1 = (int) transTime;
                        x2 = (int) dirY;
                        y2 = (int) dirX;
                    }
                    g2.setPaint(windDirectionPaint);
                    g2.setStroke(windDirectionStroke);
                    g2.drawLine(x1, y1, x2, y2);
                }
            }
        }
    }
    
    public static class State extends XYLineAndShapeRenderer.State {
        private static final double UNKNOWN_LAST_DIRECTION = 500.0;
        private double lastDirection = UNKNOWN_LAST_DIRECTION;
        public State(PlotRenderingInfo info) {
            super(info);
        }
        
        public void setLastDirection(double lastDir) {
            lastDirection = lastDir;
        }
        
        public double getLastDirection() {
            return lastDirection;
        }
        
        public void resetLastDirection() {
            lastDirection = UNKNOWN_LAST_DIRECTION;
        }
        
        public boolean isLastDirectionValid() {
            return lastDirection != UNKNOWN_LAST_DIRECTION;
        }
    }
}
