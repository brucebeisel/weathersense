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
package com.bdb.weather.display.day;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.time.temporal.ChronoField;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.PolarChartPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import com.bdb.weather.common.HistoricalRecord;
import com.bdb.weather.common.Wind;
import com.bdb.weather.display.CompassPolarItemRenderer;

class ItemRenderer extends CompassPolarItemRenderer {
    private static final long serialVersionUID = 6655248313645839714L;
    /**
     * Creates a new instance of DayWindDirRenderer
     */
    public ItemRenderer() {
    }
   
    /**
     * Plots the data for a given series.
     * 
     * @param g2  the drawing surface.
     * @param dataArea  the data area.
     * @param info  collects plot rendering info.
     * @param plot  the plot.
     * @param dataset  the dataset.
     * @param seriesIndex  the series index.
     */
    @Override
    public void drawSeries(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info, PolarPlot plot, XYDataset dataset, int seriesIndex) {
        Shape point = new Rectangle2D.Double(-2, -2, 4, 4);

        int numPoints = dataset.getItemCount(seriesIndex);

        g2.setPaint(lookupSeriesPaint(seriesIndex));
        g2.setStroke(lookupSeriesStroke(seriesIndex));

        for (int i = 0; i < numPoints; i++) {
            double theta = dataset.getXValue(seriesIndex, i);
            double radius = dataset.getYValue(seriesIndex, i);

            Point p = plot.translateToJava2D(theta, radius, plot.getAxis(), dataArea);

            Shape shape = ShapeUtilities.createTranslatedShape(point, p.getX(), p.getY());

            g2.fill(shape);
        }

    }

    /**
     * Returns a clone of the renderer.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if the renderer cannot be cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

public class WindDirPlot extends PolarChartPanel {
    private static final long serialVersionUID = -3945455610185595801L;
    
    private PolarPlot   plot;
    private JFreeChart  chart;
    private XYSeries    windDirSeries = new XYSeries("Wind Direction", false);
    
    
    public WindDirPlot() {
        super(ChartFactory.createPolarChart(null, null, false, true, false));
        
        chart = getChart();
        ChartFactory.getChartTheme().apply(chart);

        plot = (PolarPlot)chart.getPlot();

        ItemRenderer renderer = new ItemRenderer();
        XYToolTipGenerator ttg = (XYDataset dataset, int series, int item) -> {
            double time = dataset.getXValue(series, item);
            long millis = Math.round(time);
            return "" + millis;
        };
        renderer.setBaseToolTipGenerator(ttg);
        plot.setRenderer(renderer);

        ((NumberAxis)plot.getAxis()).setRange(-240.0, 60.0 * 24);
        ((NumberAxis)plot.getAxis()).setAutoRange(false);
        ((NumberAxis)plot.getAxis()).setTickUnit(new NumberTickUnit(240.0, new DecimalFormat("00")) {
            private static final long serialVersionUID = 1L;
            @Override
            public String valueToString(double number) {
                if (number < 0.0 || (int)number % 240 != 0)
                    return "";
                else
                    return super.valueToString(number / 60.0);
            }
        });

        //plot.setAngleTicks(Arrays.asList(TICKS));
        plot.setDataset(new XYSeriesCollection(windDirSeries));
    }
    
    public void loadData(List<HistoricalRecord> list) {
        windDirSeries.clear();

        for (HistoricalRecord rec : list) {
            Wind wind = rec.getAvgWind();

            if (wind != null && wind.getSpeed().get() != 0.0) 
                windDirSeries.add(wind.getDirection().get(), rec.getTime().get(ChronoField.MINUTE_OF_DAY));
        }
    }
}