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
package com.bdb.weather.filereader;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.bdb.util.LabeledFieldPanel;
import com.bdb.weather.collector.Collector;
import com.bdb.weather.common.HistoricalRecord;

@SuppressWarnings("serial")
public class FileReaderDisplay extends JFrame implements ActionListener
{
    private JTextField m_stationTF = new JTextField("beisel");
    private JTextField m_timeTF = new JTextField(25);
    private JTextArea m_statusTA = new JTextArea();
    private Collector m_collector;
    
    public FileReaderDisplay() throws RemoteException, NotBoundException
    {
        super("File Reader");
        this.setLayout(new BorderLayout());
        JPanel fieldPanel = new JPanel(new GridLayout(0,1));
        JPanel p = new LabeledFieldPanel("Station ID:", m_stationTF);
        fieldPanel.add(p);
        p = new LabeledFieldPanel("Time Before Hole: ", m_timeTF);
        fieldPanel.add(p);
        add(fieldPanel, BorderLayout.NORTH);
        add(new JScrollPane(m_statusTA), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        JButton button = new JButton("Find");
        button.addActionListener(this);
        button.setActionCommand("find");
        buttonPanel.add(button);
        
        button = new JButton("List");
        button.addActionListener(this);
        button.setActionCommand("list");
        buttonPanel.add(button);
        
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        Registry registry = LocateRegistry.getRegistry();
        m_collector = (Collector)registry.lookup(Collector.COLLECTOR_NAME);
    }

    public void actionPerformed(ActionEvent event)
    {
        try
        {
            HistoryFileReader reader = WeatherFileReaderFactory.weatherFileReader("HeavyWeatherPro1.1",
                                                                                  new File("C:/Data/HeavyWeather/History1.dat"));
            reader.openHistoryFile();
            
            Calendar c = Calendar.getInstance();
            c.clear();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = sdf.parse(m_timeTF.getText());
            c.setTime(d);
                
            if (event.getActionCommand().equals("find"))
            {
                
                m_statusTA.append("Looking for record after: " + sdf.format(c.getTime()));
                m_statusTA.append("\n");
                
                HistoricalRecord record = reader.readNextRecord(c);
                
                m_statusTA.append("Found Record: " + record.toString());
                m_statusTA.append("\n");
                
                m_collector.addHistoricalRecord(record);
                
                m_statusTA.append("Added Record: " + record.toString());
                m_statusTA.append("\n");
                
                reader.closeHistoryFile();
            }
            else
            {
                HistoricalRecord record = reader.readNextRecord(c);
                m_statusTA.append(record.toString());
                m_statusTA.append("\n");
                for (int i = 0; i < 24; i++)
                {
                    record = reader.readNextRecord();
                    m_statusTA.append(record.toString());
                    m_statusTA.append("\n");
                }
                
            }
        }
        catch (Exception e)
        {
            m_statusTA.append(e.getMessage());
        }
    }
    
    public static void main(String[] args)
    {
        try
        {
            JFrame frame = new FileReaderDisplay();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
}
