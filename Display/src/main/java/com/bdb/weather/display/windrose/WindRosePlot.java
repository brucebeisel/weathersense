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
package com.bdb.weather.display.windrose;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.bdb.weather.common.SpeedBin;
import com.bdb.weather.common.WindRoseData;
import com.bdb.weather.common.WindSlice;
import com.bdb.weather.common.measurement.Heading;
import com.bdb.weather.display.CompassPolarItemRenderer;

/**
 * A specialized polar plot that displays wind speed and direction summary graphically.
 * 
 * @author Bruce
 *
 */
public class WindRosePlot extends PolarPlot {
    private final DefaultPolarItemRenderer renderer;
    private WindRoseData	     data;
    private static final Logger      logger = Logger.getLogger(WindRosePlot.class.getName());
    private final Color binColor[] = {
            Color.green,
            Color.blue,
            Color.black,
            Color.cyan,
            Color.red,
            Color.yellow,
            Color.green.darker(),
            Color.blue.darker()
    };

    /**
     * Constructor.
     */
    public WindRosePlot() {
        super();

        NumberAxis axis = new NumberAxis("%");
        axis.setTickUnit(new NumberTickUnit(10.0));
        setAxis(axis);

        renderer = new CompassPolarItemRenderer();
        renderer.setDefaultPaint(Color.black);
        renderer.setShapesVisible(false);
        renderer.setDrawOutlineWhenFilled(false);
        renderer.setFillComposite(AlphaComposite.Src);
        setRenderer(renderer);
        setAngleGridlinesVisible(true);
        setAngleGridlinePaint(Color.black);
        setRadiusGridlinesVisible(true);
        setRadiusGridlinePaint(Color.BLACK);
    }

    /**
     * Load the wind rose data into the dataset.
     * 
     * @param d The wind rose data
     */
    public void setDataset(WindRoseData d) {
        data = d;
        if (d == null) {
            setDataset((XYSeriesCollection)null);
            return;
        }

        int numSlices = d.getNumSlices();
        double arcLength = (360.0 /  numSlices) / 2.0;
        double halfArcLength = arcLength / 2.0;

        XYSeriesCollection dataset = new XYSeriesCollection();

        logger.log(Level.FINEST, "Creating Wind Rose dataset with {0} slices. arcLength = {1}", new Object[]{numSlices, arcLength});

        //
        // First go through the wind direction slices. Each weather station can have a different number of
        // direction slices. The minimum is probably 4 and the maximum is probably 360.
        //
        int seriesNumber = 0;
        for (int i = 0; i < numSlices; i++) {
            WindSlice slice = d.getSlice(i);

            //
            // The length of the pie slice is determined by the percentage of the wind that was blowing within
            // the slice. Calm winds entries are ignored.
            //
            double percent = slice.getPercentageOfWind();

            double lastLength = 0.0;

            logger.log(Level.FINER, "Slice {0} has {1} speed bins", new Object[]{i, slice.getNumSpeedBins()});

            //
            // Each slice is then divided by speed bins. Each bin is represented by a color and the length
            // of each slice segment is determined by the percentage of time the wind was blowing within the
            // speed bin.
            //
            for (int j = 0; j < slice.getNumSpeedBins(); j++) {
                Heading heading = Heading.headingForSlice(i, numSlices);
                XYSeries series = new XYSeries(heading.toString() + j, false);

                double headingValue = heading.get();
                double binPercentage = slice.speedBinPercentage(j);

                double length = lastLength;

                if (binPercentage != 0.0)
                    length += percent * (binPercentage / 100.0);

                double left = headingValue - halfArcLength;
                double right = headingValue + halfArcLength;

                logger.log(Level.FINEST, "Adding series with data: {0},{1},{2}:{3}", new Object[]{left, right, length, slice.speedBinPercentage(j)});

                //
                // Each slices speed segment is drawn as a separate series with its own color
                //
                series.add(left, lastLength);
                series.add(right, lastLength);

                series.add(right, length);
                series.add(left, length);

                dataset.addSeries(series);

                renderer.setSeriesPaint(seriesNumber, binColor[j]);
                renderer.setSeriesFilled(seriesNumber++, true);

                lastLength = length;
            }

        }

        setDataset(dataset);
    }

    /*
     * (non-Javadoc)
     * @see org.jfree.chart.plot.PolarPlot#getLegendItems()
     */
    @Override
    public LegendItemCollection getLegendItems() {
        //
        // Create the legend item to differentiate the speed bins
        //
        LegendItemCollection items = new LegendItemCollection();

        LegendItem defaultItem = new LegendItem("Dummy");

        if (data != null) {
            List<SpeedBin> bins = data.getSpeedBins();

            for (int j = 0; j < bins.size(); j++) {
                LegendItem item = new LegendItem(bins.get(j).speedString(), "Speed Bin", "", "",
                                                 defaultItem.getShape(),
                                                 binColor[j],
                                                 defaultItem.getOutlineStroke(),
                                                 defaultItem.getOutlinePaint());

                items.add(item);
            }
        }
        return items;
    }
}
