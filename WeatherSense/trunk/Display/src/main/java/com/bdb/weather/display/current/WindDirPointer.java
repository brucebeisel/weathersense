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
package com.bdb.weather.display.current;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.DialScale;

/**
 * A dial pointer that is used for wind direction.
 * 
 * @author Bruce
 * 
 */
public class WindDirPointer extends DialPointer.Pointer {
    private static final long serialVersionUID = -6771147010152648848L;
    private static final double ARC_LENGTH = 10;
    private double outerRadius;
    private double innerOffset;
    private boolean fill;

    /**
     * Constructor.
     * 
     * @param outerRadius The outer radius of the pointer
     * @param innerOffset The inner radius of the pointer
     * @param datasetIndex The dataset index this wind direction data
     * @param fill Whether the arrow is filled
     */
    public WindDirPointer(double outerRadius, double innerOffset, int datasetIndex, boolean fill) {
        this(datasetIndex);
        this.outerRadius = outerRadius;
        this.innerOffset = innerOffset;
        this.fill = fill;
    }

    /**
     * Constructor.
     * 
     * @param datasetIndex The index for which this pointer is associated
     */
    public WindDirPointer(int datasetIndex) {
        super(datasetIndex);
    }

    /**
     * Draws the pointer.
     * 
     * @param g2 The graphics target.
     * @param plot The plot.
     * @param frame The dial's reference frame.
     * @param view The dial's view.
     */
    @Override
    public void draw(Graphics2D g2, DialPlot plot, Rectangle2D frame, Rectangle2D view) {
        g2.setPaint(Color.blue);
        g2.setStroke(new BasicStroke(1.0f));
        DialScale scale = plot.getScaleForDataset(getDatasetIndex());
        double value = plot.getValue(getDatasetIndex());
        double angle = scale.valueToAngle(value % 360.0);

        Rectangle2D outerRect = DialPlot.rectangleByRadius(frame, outerRadius, outerRadius);
        Rectangle2D innerRect = DialPlot.rectangleByRadius(frame, outerRadius - innerOffset, outerRadius - innerOffset);

        g2.setPaint(getOutlinePaint());
        Arc2D arc1 = new Arc2D.Double(outerRect, angle - (ARC_LENGTH / 2), ARC_LENGTH, Arc2D.OPEN);
        g2.draw(arc1);
        Arc2D arc2 = new Arc2D.Double(innerRect, angle, 0.0, Arc2D.OPEN);
        GeneralPath gp = new GeneralPath();
        gp.moveTo(arc1.getStartPoint().getX(), arc1.getStartPoint().getY());
        gp.lineTo(arc2.getStartPoint().getX(), arc2.getStartPoint().getY());
        gp.lineTo(arc1.getEndPoint().getX(), arc1.getEndPoint().getY());
        g2.draw(gp);
        if (fill) {
            g2.setPaint(getFillPaint());
            g2.fill(gp);
        }
    }
}
