using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO.Ports;
using System.Threading;
using System.Diagnostics;

namespace VantagePro2 {
    /// <summary>
    /// This is the main loop that contains the business logic for talking to the Vantage Pro console.
    /// </summary>
    public class VantagePro2Driver : VantagePro2.WeatherSenseSocket.HistoricalReader, VantagePro2.VantagePro2Station.Callback {
        private VantagePro2Station station;
        private WeatherSenseSocket socket;
        private ArchiveManager archiveManager = new ArchiveManager("C:\\WeatherSense\\2.3\\archive\\archive.vp2");
        private bool firstPass = true;
        private bool firstCurrentWeather = true;
        private bool exit = false;
        private Thread thread;
        private int nextRecord = -1;
        private int previousNextRecord = -1;
        private readonly int LOOP_PACKET_CYCLES = 12;
        private ParametersMessage parameters;
        private DateTime lastPacketTime = new DateTime(2000, 6, 22, 17, 0, 0);
        private DateTime consoleTimeSetTime = new DateTime(2000, 1, 1, 0, 0, 0);
        private DateTime sensorStationSendTime = new DateTime(2000, 1, 1, 0, 0, 0);

        /// <summary>
        /// Constructor.
        /// </summary>
        /// <param name="port">The serial port to open</param>
        /// <param name="baudRate">The baud rate at which to read from the port</param>
        public VantagePro2Driver(String port, int baudRate) {
            Debug.AutoFlush = true;
           
            station = new VantagePro2Station(port, baudRate, this);
            socket = new WeatherSenseSocket(this);
        }

        /// <summary>
        /// Called by the socket class when the response is received from the WeatherSense collector. The response contains
        /// the date of the newest archive record in the database. This is the basis for the first dump after command.
        /// </summary>
        /// <param name="time">The time of the newest record in the database</param>
        public void newestHistoryRecordTime(DateTime time) {
            lastPacketTime = time;
            nextRecord = -1;
        }

        /// <summary>
        /// Called by the socket class when it connects to the WeatherSense collector.
        /// </summary>
        public void connected() {
            firstPass = true;
            firstCurrentWeather = true;
        }

        /// <summary>
        /// Entry point for the main loop
        /// </summary>
        public void start() {

            if (!station.Open()) {
                //System.Diagnostics.EventLog.
                Debug.Print("Failed to open weather station");
                Environment.Exit(1);
            }
            else {
                Debug.Print("Port is open");
            }

            if (!station.Wakeup()) {
                Debug.Print("Failed to wake up weather station");
                Environment.Exit(1);
            }
            else {
                Debug.Print("Weather Station is awake");
            }

            //
            // Get the information from the console that we will need for future calculations
            //
            if (!station.retrieveSetupValues() || !station.determineArchivePeriod() || !station.sensorStationInfo()) {
                Debug.Print("Failed to retrieve configuration information");
                Environment.Exit(2);
            }

            //
            // Kick off our own thread
            //
            thread = new Thread(new ThreadStart(this.run));
            thread.Start();

            //
            // Once the thread is alive this thread can exit
            //
            while (!thread.IsAlive) ;
        }

        /// <summary>
        /// Stop the thread and close all of the resources.
        /// </summary>
        public void stop() {
            exit = true;
            thread.Join();
            station.Close();
            socket.disconnect();
        }

        /// <summary>
        /// Callback used to process a current weather record.
        /// </summary>
        /// <param name="cw">The current weather</param>
        /// <returns>True if the loop packet loop should continue, false if it should exit and try an achive dump</returns>
        public bool processCurrentWeather(CurrentWeather cw) {
            //
            // First time through figure out what sensors are connected to the weather station using the values in the
            // LOOP packet
            //
            if (firstCurrentWeather) {
                firstCurrentWeather = false;
                String sensorMessage = Sensor.formatMessage(station.Sensors);
                socket.sendData(sensorMessage);
            }

            String currentWeatherMessage = cw.formatMessage();
            nextRecord = cw.NextPacket;
            socket.sendData(currentWeatherMessage);
            Debug.Print("Previous Next Record: {0}  Next Record: {1}", previousNextRecord, nextRecord);
            return previousNextRecord == nextRecord;
        }

        /// <summary>
        /// Callback to process a page of archive packets.
        /// </summary>
        /// <param name="page">A list of archive packets that make up the page</param>
        /// <returns>True if all of the packets were sent to the WeatherSense collector</returns>
        public bool processArchivePage(List<ArchivePacket> page) {
            archiveManager.addPackets(page);
            if (firstPass)
                return true;

            foreach (ArchivePacket packet in page) {
                //
                // If the page has a null entry then this is the last page of the DMPAFT command.
                // Set the previousNextRecord to the nextRecord so that we know when the next record
                // changes again and we need to do another DMPAFT.
                //
                if (packet == null) {
                    previousNextRecord = nextRecord;
                }
                else {
                    DateTime now = DateTime.Now;
                    TimeSpan age = now.Subtract(packet.Time);
                    if (age.TotalHours < 1) {
                        int maxPackets = (int)((station.ArchivePeriod * 60F) / ((41F + 1F - 1F) / 16F));
                        int actualPackets = packet.WindSampleCount;
                        int issReception = (actualPackets * 100) / maxPackets;
                        if (issReception > 100)
                            issReception = 100;

                        Debug.Print("IIS Reception for archive interval ending at {0} is {1}. Max Packets = {2}, Actual Packets - {3}",
                                     packet.Time, issReception, maxPackets, actualPackets);

                        List<SensorStation> sensorStations = station.SensorStations;
                        foreach (SensorStation sensorStation in sensorStations) {
                            if (sensorStation.Type == SensorStation.SensorStationType.INTEGRATED_SENSOR_STATION)
                                sensorStation.LinkQuality = issReception;
                        }

                        String ssMessage = SensorStation.formatSensorStationStatusMessage(sensorStations, packet.Time);
                        socket.sendData(ssMessage);
                    }

                    String message = packet.formatMessage();
                    Debug.Print("=== Achive === " + packet.Time + " =============");
                    if (socket.sendData(message))
                        lastPacketTime = packet.Time;
                    else
                        return false;
                }
            }

            return true;
        }

        /// <summary>
        /// Thread entry point that is the main loop of the Vantage Pro driver
        /// </summary>
        public void run() {
            DateTime time = station.GetTime();

            Debug.Print("Station Time: {0}", time);
            DateTime archiveTime = archiveManager.findNewestPacketTime();
            station.dumpAfter(archiveTime);
            
            while (!exit) {

                try {
                    station.Wakeup();

                    //
                    // First time through get various parameters about the weather station and send them to the collector
                    //
                    if (firstPass) {
                        parameters = station.getParameters();
                        if (parameters != null) {
                            String parametersMessage = parameters.formatMessage();
                            Debug.Print(parametersMessage);
                            socket.sendData(parametersMessage);
                            firstPass = false;
                        }

                        String message = SensorStation.formatSensorStationMessage(station.SensorStations);
                        socket.sendData(message);
                    }

                    //
                    // If it has been more than a day since the time was set, set the time
                    //
                    DateTime now = DateTime.Now;
                    if (consoleTimeSetTime.AddDays(1.0).CompareTo(now) <= 0) {
                        if (station.setConsoleTime())
                            consoleTimeSetTime = now;                     
                    }

                    //
                    // Get the current weather values for about a minute
                    //
                    station.currentValuesLoop(LOOP_PACKET_CYCLES);

                    //
                    // If the LOOP packet data indicates that a new archive packet is available
                    // go get it.
                    //
                    if (previousNextRecord != nextRecord && socket.isConnected()) {
                        station.dumpAfter(lastPacketTime);
                    }
                }
                catch (Exception e) {
                    Debug.Print("Caught exception: {0}\n{1}", e.Message, e.StackTrace);     
                } 
            }
        }
    }
}
