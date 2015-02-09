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

import com.bdb.weather.common.CurrentWeatherSubscriber;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import com.bdb.util.BBDesktopManager;
import com.bdb.util.jdbc.DBConnection;

import com.bdb.weather.common.CurrentWeather;
import com.bdb.weather.common.DateRange;
import com.bdb.weather.common.WeatherStation;
import com.bdb.weather.common.db.DatabaseConstants;
import com.bdb.weather.common.db.WeatherStationTable;
import com.bdb.weather.display.current.CurrentWeatherPanel;
import com.bdb.weather.display.currenttable.CurrentWeatherText;
import com.bdb.weather.display.day.DaySummaryGraphPanel;
import com.bdb.weather.display.day.HistoricalSeriesInfo;
import com.bdb.weather.display.day.TodayGraphPanel;
import com.bdb.weather.display.freeplot.DailyFreePlot;
import com.bdb.weather.display.freeplot.HistoricalFreePlot;
import com.bdb.weather.display.freeplot.MonthlyFreePlot;
import com.bdb.weather.display.freeplot.SummaryFreePlot;
import com.bdb.weather.display.historyeditor.HistoryEditorPanel;
import com.bdb.weather.display.historytable.DayHistoryTable;
import com.bdb.weather.display.preferences.ColorPreferencePanel;
import com.bdb.weather.display.preferences.UnitsPreferenceDialog;
import com.bdb.weather.display.preferences.UserPreferenceDialog;
import com.bdb.weather.display.preferences.UserPreferences;
import com.bdb.weather.display.sensors.SensorPanel;
import com.bdb.weather.display.sensors.SensorStationPanel;
import com.bdb.weather.display.storm.StormPanel;
import com.bdb.weather.display.stripchart.StripChartPanel;
import com.bdb.weather.display.summary.DailySummariesPanel;
import com.bdb.weather.display.summary.MonthlySummariesPanel;
import com.bdb.weather.display.summary.YearlySummariesPanel;

public class WeatherSense implements ViewLauncher, ActionListener, ComponentListener, Runnable, CurrentWeatherSubscriber.CurrentWeatherHandler {
    private static final int REFRESH_INTERVAL_MILLIS = 30000;
    private static final String PREFERENCES_CMD = "Preferences...";
    private static final String UNITS_PREFERENCES = "Units...";
    private static final String COLOR_PREFERENCES = "Colors...";
    private static final String TIMER_CMD = "timer";
    private static final String HISTORICAL_FREE_PLOT_VIEW_NAME = "Historical Free Plot";
    private static final String HISTORICAL_TABLE_VIEW_NAME = "Historical Table";
    private static final String DAILY_SUMMARY_FREE_PLOT_VIEW_NAME = "Daily Free Plot";
    private static final String MONTHLY_FREE_PLOT_VIEW_NAME = "Monthly Free Plot";
    private static final String DAILY_SUMMARIES_CMD = "Daily Summaries";
    private static final String MONTHLY_SUMMARIES_CMD = "Monthly Summaries";
    private static final String YEARLY_SUMMARIES_CMD = "Yearly Summaries";
    private static final String CURRENT_WEATHER_CMD = "Current Weather";
    private static final String DAY_AVERAGES_CMD = "Day Averages...";
    private static final String TODAY_CMD = "Today";
    private static final String DAY_SUMMARY_CMD = "Day Summary";
    private static final String STORMS_CMD = "Storms";
    private static final String HISTORY_EDITOR_CMD = "History Editor";
    private static final String WEATHER_STATIONS_CMD = "Weather Station...";
    private static final String STRIP_CHART_CMD = "Strip Chart...";
    private static final String SENSOR_CMD = "Sensors...";
    private static final String SENSOR_STATIONS_CMD = "Sensor Stations...";
    private static final String EXIT_CMD = "Exit";
    private final DBConnection connection;
    private final WeatherStationTable stationTable;
    private WeatherStation ws;
    private TodayGraphPanel todayGraphPanel;
    private CurrentWeatherPanel currentWeatherPanel;
    private final BBDesktopManager desktopManager = new BBDesktopManager();
    private final JDesktopPane desktopPane = new JDesktopPane();
    private JInternalFrame todayFrame;
    private JInternalFrame currentWeatherFrame;
    private final JFrame frame = new JFrame("Weather Sense");
    private final List<Refreshable> refreshList = new ArrayList<>();
    private final Timer timer = new Timer(REFRESH_INTERVAL_MILLIS, this);
    private static WeatherSense weatherSense;
    private final Image frameImage;
    private static final Logger logger = Logger.getLogger(WeatherSense.class.getName());
    private final Preferences rootPref = Preferences.userNodeForPackage(WeatherSense.class);
    private final Preferences prefs = rootPref.node("window-geometry");
    private final List<CurrentWeatherProcessor> cwpList = new ArrayList<>();

    public WeatherSense(String databaseHost, Image frameImage) {
        
        this.frameImage = frameImage;
        
        String databaseUrl = String.format(DatabaseConstants.DATABASE_URL_FORMATTER, databaseHost, DatabaseConstants.DATABASE_PORT, DatabaseConstants.DATABASE_NAME);

        connection = new DBConnection(databaseUrl,
                                        DatabaseConstants.DATABASE_USER,
                                        DatabaseConstants.DATABASE_PASSWORD);


        if (!connection.connect()) {
            JOptionPane.showMessageDialog(null, "Unable to connect to the database. Please contact your administrator");
            System.exit(1);
        }
        
        stationTable = new WeatherStationTable(connection);
        ws = stationTable.getWeatherStation();
    }

    public void createElements() {
        CurrentWeatherSubscriber.createSubscriber(this);
        
        if (frameImage != null)
            frame.setIconImage(frameImage);

        timer.setActionCommand(TIMER_CMD);
        desktopPane.setDesktopManager(desktopManager);

        JMenuBar menubar = new JMenuBar();

        JMenu menu = new JMenu("File");
        menubar.add(menu);

        JMenuItem menuItem = new JMenuItem(HISTORY_EDITOR_CMD);
        menu.add(menuItem);
        menuItem.addActionListener((ActionEvent e) -> {
            launchHistoryEditor();
        });

        menuItem = new JMenuItem(EXIT_CMD);
        menuItem.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });

        menu.add(menuItem);

        menu = new JMenu("Views");

        menuItem = new JMenuItem(CURRENT_WEATHER_CMD);
        menu.add(menuItem);
        menuItem.addActionListener((ActionEvent e) -> {
            launchCurrentWeatherView();
        });

        menuItem = new JMenuItem("Current Weather Table");
        menu.add(menuItem);
        menuItem.addActionListener((ActionEvent e) -> {
            launchCurrentWeatherTable();
        });

        menuItem = new JMenuItem(HISTORICAL_TABLE_VIEW_NAME);
        menu.add(menuItem);
        menuItem.addActionListener((ActionEvent e) -> {
            launchHistoricalTableView();
        });


        menuItem = new JMenuItem(TODAY_CMD);
        menu.add(menuItem);
        menuItem.addActionListener((ActionEvent e) -> {
            launchTodayView();
        });

        menuItem = new JMenuItem(STORMS_CMD);
        menu.add(menuItem);
        menuItem.addActionListener((ActionEvent e) -> {
            launchStormView();
        });

        menu.add(new JSeparator());

        menuItem = new JMenuItem(DAY_SUMMARY_CMD);
        menu.add(menuItem);
        menuItem.addActionListener((ActionEvent e) -> {
            launchDaySummaryView(LocalDate.now());
        });

        menuItem = new JMenuItem(DAILY_SUMMARIES_CMD);
        menu.add(menuItem);
        menuItem.addActionListener((ActionEvent e) -> {
            launchDailySummariesView(DateInterval.LAST_30_DAYS);
        });

        menuItem = new JMenuItem(MONTHLY_SUMMARIES_CMD);
        menu.add(menuItem);
        menuItem.addActionListener((ActionEvent e) -> {
            launchMonthlySummariesView(DateInterval.THIS_YEAR);
        });

        menuItem = new JMenuItem(YEARLY_SUMMARIES_CMD);
        menu.add(menuItem);
        menuItem.addActionListener((ActionEvent e) -> {
            launchYearlySummariesView();
        });

        menu.add(new JSeparator());

        menuItem = new JMenuItem(HISTORICAL_FREE_PLOT_VIEW_NAME);
        menu.add(menuItem);
        menuItem.addActionListener((ActionEvent e) -> {
            launchHistoricalFreePlotView();
        });

        menuItem = new JMenuItem(DAILY_SUMMARY_FREE_PLOT_VIEW_NAME);
        menu.add(menuItem);
        menuItem.addActionListener(new ActionListenerImpl());

        menuItem = new JMenuItem(MONTHLY_FREE_PLOT_VIEW_NAME);
        menu.add(menuItem);
        menuItem.addActionListener((ActionEvent e) -> {
            launchMonthlyFreePlotView();
        });

        menu.add(new JSeparator());

        menuItem = new JMenuItem(STRIP_CHART_CMD);
        menu.add(menuItem);
        menuItem.addActionListener((ActionEvent event) -> {
            launchStripChart();
        });

        menubar.add(menu);

        menu = new JMenu("Settings");
        JMenu preferenceMenu = new JMenu("Preferences");
        menu.add(preferenceMenu);

        menuItem = new JMenuItem(PREFERENCES_CMD);
        menuItem.addActionListener(this);
        menuItem.setActionCommand(PREFERENCES_CMD);
        menu.add(menuItem);

        menuItem = new JMenuItem(UNITS_PREFERENCES);
        preferenceMenu.add(menuItem);
        menuItem.addActionListener((ActionEvent e) -> {
            JDialog d = new UnitsPreferenceDialog();
            d.setVisible(true);
        });

        menuItem = new JMenuItem(COLOR_PREFERENCES);
        preferenceMenu.add(menuItem);
        menuItem.addActionListener((ActionEvent e) -> {
            JDialog d = new ColorPreferencePanel(frame, UserPreferences.getInstance());
            d.setVisible(true);
        });

        menuItem = new JMenuItem(WEATHER_STATIONS_CMD);
        menu.add(menuItem);
        menuItem.addActionListener((ActionEvent e) -> {
            WeatherStationMgr.editWeatherStation(frame, connection);
        });

        menuItem = new JMenuItem(SENSOR_CMD);
        menu.add(menuItem);
        menuItem.addActionListener((ActionEvent e) -> {
            launchSensorView();
        });

        menuItem = new JMenuItem(SENSOR_STATIONS_CMD);
        menu.add(menuItem);
        menuItem.addActionListener((ActionEvent e) -> {
            launchSensorStationView();
        });

        menuItem = new JMenuItem(DAY_AVERAGES_CMD);
        menu.add(menuItem);
        menuItem.addActionListener((ActionEvent e) -> {
            DayAveragesEditor.editAverages(frame, connection, "92064");
        });

        menubar.add(menu);

        menu = new JMenu("Help");
        menuItem = new JMenuItem("About WeatherSense...");
        menu.add(menuItem);

        menubar.add(menu);

        frame.setJMenuBar(menubar);

        JPanel desktopPanel = new JPanel(new BorderLayout());
        desktopPanel.add(desktopPane, BorderLayout.CENTER);
        desktopPanel.add(desktopManager.getTaskbar(), BorderLayout.SOUTH);

        frame.add(desktopPanel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        frame.pack();
        frame.setSize(800, 600);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);

        desktopPane.addContainerListener(new ContainerListener() {
            @Override
            public void componentAdded(ContainerEvent e) {
            }

            @Override
            public void componentRemoved(ContainerEvent e) {
                logger.log(Level.FINE, "Event = {0}", e);
                Object child = e.getChild();
                if (child == currentWeatherFrame) {
                    currentWeatherFrame = null;
                }
                if (child instanceof CurrentWeatherProcessor)
                    cwpList.remove((CurrentWeatherProcessor)child);
            }
        });

        frame.setVisible(true);
        SwingUtilities.invokeLater(() -> {
            timer.start();
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        logger.log(Level.FINER, "Event = {0}", e);


        if (e.getActionCommand() != null) {
            connection.connect();
            switch (e.getActionCommand()) {
                case PREFERENCES_CMD:
                    JDialog dialog = new UserPreferenceDialog();
                    dialog.setVisible(true);
                    break;

                case HISTORY_EDITOR_CMD:
                    launchHistoryEditor();
                    break;

                case TIMER_CMD:
                    logger.info("Refreshing screens");
                    refreshList.stream().forEach((refresh) -> { refresh.refresh(); });
                    break;
            }
        }
    }

    @Override
    public void run() {
        weatherSense.createElements();
        
        //
        // If there is no weather station in the database, then prompt user for the weather station information
        //
        if (ws == null) {
            WeatherStationMgr.editWeatherStation(frame, connection);
        }
        else
            HistoricalSeriesInfo.addExtraSensors(ws.getSensorManager().getAllSensors());
    }
    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentResized(ComponentEvent e) {
        try {
            prefs.putInt("height", ((JInternalFrame)e.getSource()).getHeight());
            prefs.putInt("width", ((JInternalFrame)e.getSource()).getWidth());
            prefs.flush();
        }
        catch (BackingStoreException ex) {
            ErrorDisplayer.getInstance().displayInformation("Failed to save window size for future");
        }
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    private JInternalFrame launchView(String title, ComponentContainer container, Dimension geometry, boolean maximize) {
        JInternalFrame internalFrame = new JInternalFrame(title);
        internalFrame.setIconifiable(true);
        internalFrame.setResizable(true);
        internalFrame.setClosable(true);
        internalFrame.setMaximizable(true);
        internalFrame.add(container.getComponent());
        desktopPane.add(internalFrame);
        internalFrame.pack();

        if (geometry != null)
            internalFrame.setSize(geometry);

        try {
            internalFrame.setMaximum(maximize);
        }
        catch (PropertyVetoException e) {
            logger.log(Level.INFO, "", e);
        }

        internalFrame.setVisible(true);

        return internalFrame;
    }

    @Override
    public void launchCurrentWeatherView() {
        if (currentWeatherFrame == null) {
            ws = stationTable.getWeatherStation();
            currentWeatherPanel = new CurrentWeatherPanel(ws, connection);
            cwpList.add(currentWeatherPanel);
            currentWeatherFrame = launchView("Current Weather", currentWeatherPanel, new Dimension(850, 850), false);
            currentWeatherFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        }
        else
            currentWeatherFrame.setVisible(true);
    }

    public void launchCurrentWeatherTable() {
        ws = stationTable.getWeatherStation();
        CurrentWeatherText cwt = new CurrentWeatherText(ws);
        cwpList.add(cwt);
        launchView("Current Weather", cwt, null, false);
    }

    public void launchHistoricalTableView() {
        DayHistoryTable dayHistoryTable = new DayHistoryTable(connection);
        launchView("Day Historical Table", dayHistoryTable, null, true);
    }

    @Override
    public void launchDaySummaryView(LocalDate day) {
        ws = stationTable.getWeatherStation();
        DaySummaryGraphPanel daySummaryGraphPanel = new DaySummaryGraphPanel(ws, connection, day);
        launchView("Day Summary", daySummaryGraphPanel, new Dimension(800,600), true);
        daySummaryGraphPanel.setTitle();
    }

    private void launchDailySummariesView(LocalDate start, LocalDate end, DateInterval interval) {
        ws = stationTable.getWeatherStation();
        DailySummariesPanel summaryPanel = new DailySummariesPanel(ws, connection, this, start, end, interval);
        launchView("Daily Summary", summaryPanel, new Dimension(800,600), true);
        summaryPanel.setWindowTitle();
    }

    @Override
    public void launchStormView() {
        StormPanel stormPanel = new StormPanel(connection);
        launchView("", stormPanel, new Dimension(800,600), false);
    }

    @Override
    public void launchDailySummariesView(LocalDate start, LocalDate end) {
        launchDailySummariesView(start, end, DateInterval.CUSTOM);
    }

    @Override
    public void launchDailySummariesView(DateInterval interval) {
        DateRange range = interval.range();
        launchDailySummariesView(range.getStart().toLocalDate(), range.getEnd().toLocalDate(), interval);
    }

    @Override
    public void launchMonthlySummariesView(DateInterval interval) {
        LocalDate end = LocalDate.now();
        LocalDate start = LocalDate.of(end.getYear(), Month.JANUARY, 1);

        MonthlySummariesPanel monthlySummaryPanel = new MonthlySummariesPanel(ws, connection, this, start, end, DateInterval.THIS_YEAR);
        launchView("", monthlySummaryPanel, new Dimension(800, 600), true);
    }

    public void launchYearlySummariesView() {
        YearlySummariesPanel yearlySummaryPanel = new YearlySummariesPanel(ws, connection, this);
        launchView("", yearlySummaryPanel, new Dimension(800, 600), true);
    }

    @Override
    public void launchHistoricalFreePlotView() {
        HistoricalFreePlot freePlot = new HistoricalFreePlot(ws, connection);
        launchView(HISTORICAL_FREE_PLOT_VIEW_NAME, freePlot, new Dimension(800, 600), true);
    }

    @Override
    public void launchDailyFreePlotView() {
        SummaryFreePlot freePlot = new DailyFreePlot(ws, connection);
        launchView(DAILY_SUMMARY_FREE_PLOT_VIEW_NAME, freePlot, new Dimension(800, 600), true);
    }

    @Override
    public void launchMonthlyFreePlotView() {
        MonthlyFreePlot freePlot = new MonthlyFreePlot(ws, connection);
        launchView(MONTHLY_FREE_PLOT_VIEW_NAME, freePlot, new Dimension(800, 600), true);
    }

    @Override
    public void launchTodayView() {
        if (todayGraphPanel == null) {
            todayGraphPanel = new TodayGraphPanel(ws, connection);
            refreshList.add(todayGraphPanel);
            todayFrame = launchView("Today", todayGraphPanel, new Dimension(800, 600), true);
            todayFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
            todayGraphPanel.refresh();
        }
        else
            todayFrame.setVisible(true);
    }

    @Override
    public void launchHistoryEditor() {
        HistoryEditorPanel editor = new HistoryEditorPanel(ws, connection);
        launchView(HISTORY_EDITOR_CMD, editor, new Dimension(800, 600), true);
    }

    @Override
    public void launchStripChart() {
        StripChartPanel stripChart = new StripChartPanel(connection, null);
        launchView(STRIP_CHART_CMD, stripChart, new Dimension(800, 600), false);
        cwpList.add(stripChart);
    }

    private void launchSensorView() {
        SensorPanel sensorPanel = new SensorPanel(connection);
        launchView("", sensorPanel, new Dimension(800, 600), true);
    }

    private void launchSensorStationView() {
        SensorStationPanel sensorStationPanel = new SensorStationPanel(connection);
        sensorStationPanel.loadData();
        launchView("Sensor Stations", sensorStationPanel, new Dimension(800, 600), false);
    }

    @Override
    public void handleCurrentWeather(CurrentWeather currentWeather) {
        final CurrentWeather curWeather = currentWeather;
        logger.fine("Updating " + cwpList.size() + " current weather processors");
        
        SwingUtilities.invokeLater(() -> { cwpList.stream().forEach((cwp) -> { cwp.updateCurrentWeather(curWeather); }); });
    }

    public static void main(String args[]) {
        try {
            LookAndFeelInfo lafs[] = UIManager.getInstalledLookAndFeels();
            for (LookAndFeelInfo laf : lafs)
                if (laf.getClassName().endsWith("NimbusLookAndFeel"))
                    UIManager.setLookAndFeel(laf.getClassName());

            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("logging.properties");

            if (is != null)
                LogManager.getLogManager().readConfiguration(is);

            UserPreferences.getInstance();

            ToolTipManager.sharedInstance().setInitialDelay(0);
            ToolTipManager.sharedInstance().setDismissDelay(60000);
            ToolTipManager.sharedInstance().setReshowDelay(1000);

            String dbHost = DatabaseConstants.DATABASE_HOST;

            if (args.length > 0)
                dbHost = args[0];

            URL url = ClassLoader.getSystemResource("com/bdb/weathersense/WeatherSense.jpg");

            Image image = null;

            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                image = icon.getImage();
            }

            weatherSense = new WeatherSense(dbHost, image);
            SwingUtilities.invokeAndWait(weatherSense);
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException | IOException | SecurityException | InterruptedException | InvocationTargetException e) {
            
            ErrorDisplayer.getInstance().displayMessageLater("Failed to initialize (" + e.getMessage() + ")", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private class ActionListenerImpl implements ActionListener {
        public ActionListenerImpl() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            launchDailyFreePlotView();
        }
    }

    public static void setFrameTitle(JComponent component, String title) {
        Container container = (Container)component;
        JFrame frame = null;
        JInternalFrame internalFrame = null;

        while (container != null && frame == null && internalFrame == null) {
            if (container instanceof JInternalFrame)
                internalFrame = (JInternalFrame)container;
            else if (container instanceof JFrame)
                frame = (JFrame)container;
            else
                container = container.getParent();
        }

        if (frame != null) {
            frame.setTitle(title);
        }
        else if (internalFrame != null)
            internalFrame.setTitle(title);
    }

    public static String getFrameTitle(JComponent component) {
        String title = null;

        Container container = (Container)component;
        JFrame frame = null;
        JInternalFrame internalFrame = null;

        while (container != null && frame == null && internalFrame == null) {
            if (container instanceof JInternalFrame)
                internalFrame = (JInternalFrame)container;
            else if (container instanceof JFrame)
                frame = (JFrame)container;
            else
                container = container.getParent();
        }

        if (frame != null) {
            title = frame.getTitle();
        }
        else if (internalFrame != null)
            title = internalFrame.getTitle();

        return title;
    }
}
