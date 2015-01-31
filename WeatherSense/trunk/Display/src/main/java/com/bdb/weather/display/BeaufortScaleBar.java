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
package com.bdb.weather.display;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import com.bdb.weather.common.BeaufortScale;

public class BeaufortScaleBar {
    private static final long serialVersionUID = -9165453619473180539L;
    
    //private JProgressBar bar = new JProgressBar(0, BeaufortScale.MAX_FORCE);
    private final JSlider bar = new JSlider(0, 12);
    private final JPanel outerPanel = new JPanel(new GridLayout(2,0));

    public BeaufortScaleBar() {
 
        //BoundedRangeModel model = bar.getModel();
        //model.setValue(2);
        //bar.setStringPainted(true);
        //bar.setString("5");
        //bar.setExtent(0);
        bar.setPaintTicks(true);
        bar.setPaintLabels(true);
        bar.setMajorTickSpacing(1);
        bar.setSnapToTicks(true);
        bar.setValue(5);
        //bar.setEnabled(false);
//        BasicSliderUI ui = new javax.swing.plaf.synth.SynthSliderUI() {
//            public void paintThumb(Graphics g) {
//                super.paintThumb(g);
//                return;
//            }
//        };
//        bar.setUI(ui);
        
        outerPanel.add(bar);
        
        JPanel scalePanel = new JPanel(new BorderLayout());
        scalePanel.add(new JLabel("0"), BorderLayout.WEST);
        scalePanel.add(new JLabel("12"), BorderLayout.EAST);
        JPanel innerPanel = new JPanel(new GridLayout(1,0));
        for (int i = 1; i < BeaufortScale.MAX_FORCE; i++) {
            JLabel label = new JLabel("" + i);
            label.setBorder(LineBorder.createBlackLineBorder());
            label.setHorizontalAlignment(SwingConstants.CENTER);
            innerPanel.add(label);
        }
        scalePanel.add(innerPanel, BorderLayout.CENTER);
        
        outerPanel.add(scalePanel);
    }
    
    public JComponent getComponent() {
        return outerPanel;
    }
}