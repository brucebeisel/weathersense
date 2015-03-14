using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO.Ports;
using System.Diagnostics;

namespace VantagePro2 {
    public class VantagePro2Station {
        private static readonly int NUM_ARCHIVE_PAGES = 512;
        private static readonly int ARCHIVE_PAGE_SIZE = 265;
        private static readonly int LOOP_PACKET_SIZE = 99;
        private static readonly int RECORDS_PER_ARCHIVE_PAGE = 5;
        private static readonly int BYTES_PER_ARCHIVE_RECORD = 52;
        private static readonly int CRC_BYTES = 2;
        private static readonly int TIME_RESPONSE_LENGTH = 6;
        private static readonly int DUMP_AFTER_RESPONSE_LENGTH = 4;
        private static readonly int TIME_LENGTH = 4;
        private static readonly int WAKEUP_TRIES = 5;
        private static readonly int SET_TIME_LENGTH = 6;
        private static readonly int LOOP_PACKET_WAIT = 2000;
 
        private SerialPort serialPort;
        private String portName;
        private int baudRate;
        private byte[] buffer = null;
        private float rainCollectorSize = 0.0F;
        private WindDirectionSlices pastWindDirs = new WindDirectionSlices();
        private float windGust10Minute;
        private int windGustDirection10Minute;
        private Callback callback;
        private bool firstLoopPacket = true;
        public float RainCollectorSize {
            get { return rainCollectorSize; }
        }
        public int archivePeriod;
        public int ArchivePeriod {
            get { return archivePeriod; }
        }
        public List<SensorStation> sensorStations = new List<SensorStation>();
        public List<SensorStation> SensorStations {
            get { return sensorStations; }
        }

        public List<Sensor> sensors = new List<Sensor>();
        public List<Sensor> Sensors {
            get { return sensors; }
        }

        private float consoleBatteryVoltage;

        public interface Callback {
            bool processCurrentWeather(CurrentWeather cw);
            bool processArchivePage(List<ArchivePacket> page);
        }

        public VantagePro2Station(String portName, int baudRate, Callback callback) {
            this.portName = portName;
            this.baudRate = baudRate;
            this.callback = callback;
            buffer = new byte[512];
        }

        /// <summary>
        /// Open the console.
        /// </summary>
        /// <returns>True if the console was opened successfully</returns>
        public bool Open() {
            serialPort = new SerialPort();
            serialPort.PortName = portName;
            serialPort.BaudRate = baudRate;
            serialPort.Parity = Parity.None;
            serialPort.DataBits = 8;
            serialPort.StopBits = StopBits.One;
            serialPort.Handshake = Handshake.None;

            serialPort.ReadTimeout = 5000;
            serialPort.WriteTimeout = 5000;

            try {
                serialPort.Open();
                return true;
            }
            catch (Exception) {
                return false;
            }
        }

        /// <summary>
        /// Close the console
        /// </summary>
        public void Close() {
            serialPort.Close();
        }

        /// <summary>
        /// Get the time from the console.
        /// </summary>
        /// <returns>The time or Jan 1, 1900 if the time could not be read from the console</returns>
        public DateTime GetTime() {
            DateTime time = new DateTime(1900, 1, 1);

            if (!sendAckedCommand(CommandStrings.GET_TIME))
                return time;

            if (Read(TIME_RESPONSE_LENGTH + CRC_BYTES) && checkCRC(buffer, TIME_RESPONSE_LENGTH))
                time = new DateTime(buffer[5] + 1900, buffer[4], buffer[3], buffer[2], buffer[1], buffer[0]);

            return time;
        }

        /// <summary>
        /// Wake up the console.
        /// </summary>
        /// <returns>True of the console is awake</returns>
        public bool Wakeup() {
            bool awake = false;

            for (int i = 0; i < WAKEUP_TRIES && !awake; i++) {
                Debug.Print("Attempting to wakeup console");
                serialPort.Write(CommandStrings.WAKEUP_COMMAND);
                System.Threading.Thread.Sleep(100);
              
                //
                // After sending the wakeup command the console will respond with <LF><CR>
                //
                if (Read(2) && (char)buffer[0] == CommandStrings.LINE_FEED && (char)buffer[1] == CommandStrings.CARRIAGE_RETURN) {
                    awake = true;
                    Debug.Print("Console is awake");
                }
                else
                    serialPort.DiscardInBuffer();
            }

            return awake;
        }

        /// <summary>
        /// Read an EEPROM memory location
        /// </summary>
        /// <param name="address">The address to be read</param>
        /// <param name="count">The number of bytes to read</param>
        /// <returns>True if the response was received and is valid.</returns>
        private bool readEEPROM(String address, int count) {
            StringBuilder command = new StringBuilder(CommandStrings.READ_EEPROM);
            command.Append(" ").Append(address);
            command.AppendFormat(" {0:x2}", count);
            if (!sendAckedCommand(command.ToString()))
                return false;

            if (!Read(count + CRC_BYTES) || !checkCRC(buffer, count))
                return false;

            return true;
        }

        /// <summary>
        /// Ask the console what is the size of the rain bucket. This is needed because the console on reports
        /// rain in terms of bucket tips, not actual rainfall.
        /// </summary>
        /// <returns>True if the value was read</returns>
        public bool retrieveSetupValues() {
            Debug.Print("Determining rain collector interval");
            if (!readEEPROM(CommandStrings.EE_SETUP_BITS, 1))
                return false;

            int rainType = buffer[0] & 0x30;

            if (rainType == 0)
                rainCollectorSize = .01F;      // .01 inch
            else if (rainType == 0x10)
                rainCollectorSize = .007874F; // .2 mm
            else if (rainType == 0x20)
                rainCollectorSize = .003937F; // .1 mm
            else
                return false;

            Debug.Print("Rain collector interval: {0}", rainCollectorSize);

            return true;
        }

        /// <summary>
        /// Ask the console what the current archive period is
        /// </summary>
        /// <returns>True if the data was read</returns>
        public bool determineArchivePeriod() {
            Debug.Print("Determining archive interval");
            if (!readEEPROM(CommandStrings.EE_ARCHIVE_PERIOD, 1))
                return false;

            archivePeriod = buffer[0];

            return true;
        }

        /// <summary>
        /// Retreive the sensor station information from the console
        /// </summary>
        /// <returns>True if the data was retreived successfully</returns>
        public bool sensorStationInfo() {
            Debug.Print("Getting Sensor information");
            if (!readEEPROM(CommandStrings.EE_STATION_LIST, 16))
                return false;

            for (int i = 0; i < 16; i += 2) {
                SensorStation.SensorStationType sensorType = (SensorStation.SensorStationType)(buffer[i] & 0xF);
                if (sensorType != SensorStation.SensorStationType.NO_STATION)
                    sensorStations.Add(new SensorStation(sensorType, (i / 2) + 1));
            }

            foreach (SensorStation sensor in sensorStations)
                Debug.WriteLine(sensor);

            return true;
        }

        /// <summary>
        /// Utility operation to retreive a variable length string value from the console.
        /// </summary>
        /// <param name="command">The command to send</param>
        /// <returns>The response from the console or null if an error occurred</returns>
        private String getStringValue(String command) {
            bool success = false;
            if (!sendOKedCommand(command))
                return null;

            StringBuilder sb = new StringBuilder();
            while (Read(1)) {
                if (buffer[0] != CommandStrings.LINE_FEED && buffer[0] != CommandStrings.CARRIAGE_RETURN)
                    sb.Append((char)buffer[0]);

                if (buffer[0] == CommandStrings.CARRIAGE_RETURN) {
                    success = true;
                    break;
                }
            }

            if (success)
                return sb.ToString();
            else
                return null;
        }

        /// <summary>
        /// Retrieve the data used for the weather station parameters message.
        /// </summary>
        /// <returns>The parameters message or null if an error occurred</returns>
        public ParametersMessage getParameters() {
            Debug.Print("Getting parameters");
            Debug.Print("Getting firmware date");
            ParametersMessage parameters = new ParametersMessage();
            parameters.FirmwareDate = getStringValue(CommandStrings.FIRMWARE_DATE_REQUEST);
            parameters.FirmwareVersion = getStringValue(CommandStrings.FIRMWARE_VERSION_REQUEST);

            if (!readEEPROM(CommandStrings.EE_LATITUDE, 2))
                return null;

            parameters.Latitude = (double)BitConverter.ToInt16(buffer, 0) / 10.0;

            if (!readEEPROM(CommandStrings.EE_LONGITUDE, 2))
                return null;

            parameters.Longitude = (double)BitConverter.ToInt16(buffer, 0) / 10.0;

            if (!readEEPROM(CommandStrings.EE_ELEVATION, 2))
                return null;

            parameters.Elevation = BitConverter.ToInt16(buffer, 0);

            if (!readEEPROM(CommandStrings.EE_ARCHIVE_PERIOD, 1))
                return null;

            parameters.ArchivePeriod = buffer[0];

            if (!readEEPROM(CommandStrings.EE_RAIN_SEASON_START, 1))
                return null;

            parameters.RainSeasonStart = buffer[0];

            if (!readEEPROM(CommandStrings.EE_SETUP_BITS, 1))
                return null;

            parameters.WindCupSize = buffer[0] & 0x8;
            parameters.RainCollectorSize = rainCollectorSize;

            return parameters;
        }

        /// <summary>
        /// Set the time on the console. The Vantage Pro 2 does synch with the WWV signal, so the time must
        /// be updated at a regular interval.
        /// </summary>
        /// <returns>True if the console accepted the time</returns>
        public bool setConsoleTime() {
            if (!sendAckedCommand(CommandStrings.SET_TIME))
                return false;

            DateTime time = DateTime.Now;
            Debug.Print("Setting console time to {0}", time);
            buffer[0] = (byte)time.Second;
            buffer[1] = (byte)time.Minute;
            buffer[2] = (byte)time.Hour;
            buffer[3] = (byte)time.Day;
            buffer[4] = (byte)time.Month;
            buffer[5] = (byte)(time.Year - 1900);

            int crc = calculateCRC(buffer, SET_TIME_LENGTH);
            byte[] crcBytes = BitConverter.GetBytes(crc);

            buffer[7] = crcBytes[0];
            buffer[6] = crcBytes[1];

            serialPort.Write(buffer, 0, SET_TIME_LENGTH + CRC_BYTES);

            return consumeAck();
        }

        /// <summary>
        /// Read a single loop packet
        /// </summary>
        /// <param name="loopPacket">The loop packet object to load with the data read</param>
        /// <returns>True if the data was read successfully and it was valid</returns>
        private bool readLoopPacket(LoopPacket loopPacket) {
            Debug.Print("Reading LOOP Packet");
            if (!Read(LOOP_PACKET_SIZE))
                return false;

            if (!loopPacket.parseLoopPacket(buffer, rainCollectorSize))
                return false;

            //
            // First time through determine what sensors are attached to the weather station based on the valid data in
            // the LOOP packet.
            //
            if (firstLoopPacket) {
                firstLoopPacket = false;
                Sensor.detectSensors(loopPacket, sensors);
            }

            //
            // Pull out the battery status for the sensor stations
            //
            consoleBatteryVoltage = loopPacket.ConsoleBatteryVoltage;
            foreach (SensorStation sensorStation in sensorStations) {
                sensorStation.BatteryStatus = loopPacket.isTransmitterBatteryGood(sensorStation.SensorIndex);
            }

            Debug.Print("LOOP packet read successfully");
            return true;
        }

        /// <summary>
        /// Read a LOOP2 packet
        /// </summary>
        /// <param name="loop2Packet">The LOOP 2 packet to load with the data read</param>
        /// <returns>True of the data was read successfully and the data was valid</returns>
        private bool readLoop2Packet(Loop2Packet loop2Packet) {
            Debug.Print("Reading LOOP2 Packet");
            if (!Read(LOOP_PACKET_SIZE))
                return false;

            if (!loop2Packet.parseLoop2Packet(buffer, rainCollectorSize))
                return false;

            Debug.Print("LOOP2 packet read successfully");
            return true;
        }

        /// <summary>
        /// Perform the current weather loop. This loop is designed to produce the specified number of current weather messages before returning.
        /// This method will return if there is an error or it is determined that a new archive packet has been created and needs to be read and
        /// sent to the collector.
        /// </summary>
        /// <param name="records">The number of current weather records to produce before returning</param>
        public void currentValuesLoop(int records) {
            LoopPacket loopPacket = new LoopPacket();
            Loop2Packet loop2Packet = new Loop2Packet();
            bool more = true;
            bool resetNeeded = false;

            String command = CommandStrings.LOOP2 + (records * 2);

            if (!sendAckedCommand(command))
                return;

            for (int i = 0; i < records && more && !resetNeeded; i++) {
                resetNeeded = false;
                Debug.Print("Getting Current Weather ---------------------------------");
                //
                // Loop packet comes first
                //
                if (readLoopPacket(loopPacket)) {
                    //
                    // Per the Vantage Pro serial communication document, sleep 2 seconds between
                    // the loop the loop 2 packets
                    //
                    System.Threading.Thread.Sleep(LOOP_PACKET_WAIT);
                    if (readLoop2Packet(loop2Packet)) {
                        //
                        // Build a current weather message from the loop packets
                        //
                        CurrentWeather cw = new CurrentWeather(loopPacket, loop2Packet, pastWindDirs);

                        //
                        // Build a list of past wind directions. This is to mimic what is shown on the
                        // console
                        //
                        pastWindDirs.addHeading(loopPacket.WindDirection);

                        //
                        // Keep the wind gust data in order to populate the next archive packet. This may
                        // not be necessary, but since the Vantage Pro 2 does not report wind gust in the
                        // archive packet, this may be the only way to get a wind gust value.
                        //
                        windGust10Minute = loop2Packet.WindGust10Minute;
                        windGustDirection10Minute = loop2Packet.WindGustDirection10Minute;

                        //
                        // Send the message for processing
                        //
                        more = callback.processCurrentWeather(cw);

                        //
                        // Per the Vantage Pro serial communication document, sleep 2 seconds between
                        // the loop 2 the loop packets
                        //
                        System.Threading.Thread.Sleep(LOOP_PACKET_WAIT);
                    }
                    else
                        resetNeeded = true;
                }
                else
                    resetNeeded = true;
               
                Debug.Print("End Current Weather ---------------------------------");
            }

            //
            // If a reset is needed due to an error or the callback client wants the LOOP loop to stop, use the wakeup command
            // to stop the LOOP packet cycle.
            //
            if (resetNeeded || !more)
                Wakeup();
        }

        /// <summary>
        /// Check if an archive packet contains data. Per the serial protocol document a packet with no data is filled with 0xFF values.
        /// </summary>
        /// <param name="buffer">The buffer that contains the packet to check</param>
        /// <param name="offset">The offset into the buffer to check</param>
        /// <returns>True if the packet contains data</returns>
        private bool archivePacketContainsData(byte[] buffer, int offset) {
            bool containsData = false;

            //
            // Any bytes that is not equal to 0xFF means that there is data
            //
            for (int i = 0; i < BYTES_PER_ARCHIVE_RECORD; i++) {
                if (buffer[offset + i] != 0xFF) {
                    containsData = true;
                    break;
                }
            }

            return containsData;
        }

        /// <summary>
        /// Parse a single archive page giving a list of up to 5 packets
        /// </summary>
        /// <param name="buffer">The buffer containing the archive page</param>
        /// <param name="firstRecord">The first record in the archive page that is valid.</param>
        /// <param name="rainCollectorSize">The size of the rain collector bucket in inches</param>
        /// <param name="archivePeriod">The archive period in minutes</param>
        /// <returns>The list of valid archive packets from the page</returns>
        private List<ArchivePacket> parseArchivePage(byte[] buffer, int firstRecord, DateTime newestPacketTime) {
            List<ArchivePacket> list = new List<ArchivePacket>();

            //
            // Which page this is in a DMP or DMPAFT command
            //
            int pageSequence = buffer[0];

            //
            // The first record value may not be zero in the case of a dump after command. The first record after the specified time may not be at the
            // beginning of a page so the others must be skipped.
            //
            for (int i = firstRecord; i < RECORDS_PER_ARCHIVE_PAGE; i++) {
                if (archivePacketContainsData(buffer, 1 + (BYTES_PER_ARCHIVE_RECORD * i))) {
                    Debug.Print("Using wind gust speed {0}, direction {1}", windGust10Minute, windGustDirection10Minute);
                    ArchivePacket packet = new ArchivePacket(buffer, 1 + (BYTES_PER_ARCHIVE_RECORD * i), rainCollectorSize, archivePeriod, windGust10Minute, windGustDirection10Minute);
                   
                    //
                    // In the case of a dump after command the last page may contain packets from the beginning of the circular
                    // archive buffer. In this case the packets will have earlier dates than the last packet of the previous page
                    // or the time of the DMPAFT command.
                    //
                    if (packet.Time.CompareTo(newestPacketTime) > 0)
                        list.Add(packet);
                }
            }

            Debug.Print("Page " + pageSequence + " contained " + list.Count + " records");

            return list;
        }

        /// <summary>
        /// Read and process a single archive page
        /// </summary>
        /// <param name="firstRecord">Which packet in the page is the first valid packet</param>
        /// <returns></returns>
        private List<ArchivePacket> processArchivePage(int firstRecord, DateTime newestPacketTime) {
            List<ArchivePacket> list = null;

            //
            // Try to read the page 3 times
            //
            for (int i = 0; i < 3 && list == null; i++) {
                if (Read(ARCHIVE_PAGE_SIZE + CRC_BYTES)) {
                    if (VantagePro2Station.checkCRC(buffer, ARCHIVE_PAGE_SIZE))
                        list = parseArchivePage(buffer, firstRecord, newestPacketTime);
                    else {
                        Debug.Print("CRC check failed on archive page. Try # " + (i + 1));
                        serialPort.Write(CommandStrings.RESEND_PAGE);
                    }
                }
                else {
                    serialPort.Write(CommandStrings.CANCEL_DOWNLOAD);
                    break;
                }
            }

            if (list == null)
                throw new ProtocolException("Attempts to read page exceeded");
            else
                return list;

        }

        /// <summary>
        /// Dump the entire archive. This command is not used by WeatherSense. It is provided for completeness.
        /// </summary>
        public void dump() {
            Debug.Print("Dumping archive...");
            List<ArchivePacket> list = new List<ArchivePacket>();

            if (sendAckedCommand(CommandStrings.DUMP)) {
                for (int i = 0; i < NUM_ARCHIVE_PAGES; i++) {
                    list = processArchivePage(0, DateTime.Now);
                    if (callback.processArchivePage(list)) {
                        serialPort.Write(CommandStrings.SEND_NEXT_PAGE);
                    }
                    else {
                        serialPort.Write(CommandStrings.CANCEL_DOWNLOAD);
                        break;
                    }
                }
            }
        }

        /// <summary>
        /// Dump the achive packets that are newer than the specified time.
        /// </summary>
        /// <param name="time">The date after which to dump archive pages. Note that this date must
        /// match a record in the archive or the entire archive will be dumped. The date is sent by the
        /// collector or is determined by the date of the last packet
        /// read from the previous dump after command.</param>
        public void dumpAfter(DateTime time) {
            Debug.Print("Dumping after {0}...", time);
            DateTime newestPacketTime = time;

            //
            // First send the dump after command and get an ACK back
            //
            if (!sendAckedCommand(CommandStrings.DUMP_AFTER))
                return;

            //
            // Next send the date with a checksum
            //
            int datestamp = time.Day + (time.Month * 32) + ((time.Year - 2000) * 512);
            int timestamp = (time.Hour * 100) + time.Minute;
            byte[] dateBytes = BitConverter.GetBytes(datestamp & 0xFFFF);
            byte[] timeBytes = BitConverter.GetBytes(timestamp & 0xFFFF);
            byte[] dateTimeBytes = new byte[TIME_LENGTH + CRC_BYTES];
            dateTimeBytes[0] = dateBytes[0];
            dateTimeBytes[1] = dateBytes[1];
            dateTimeBytes[2] = timeBytes[0];
            dateTimeBytes[3] = timeBytes[1];

            int crc = calculateCRC(dateTimeBytes, TIME_LENGTH);
            byte[] crcBytes = BitConverter.GetBytes(crc);

            dateTimeBytes[5] = crcBytes[0];
            dateTimeBytes[4] = crcBytes[1];

            serialPort.Write(dateTimeBytes, 0, TIME_LENGTH + CRC_BYTES);
  
            //
            // Another ACK
            //
            if (!consumeAck())
                return;

            //
            // Now the console sends 4 bytes indicating the number of pages to be
            // dumped and which record in the first page is valid for the date specified
            //
            if (Read(DUMP_AFTER_RESPONSE_LENGTH + CRC_BYTES) && checkCRC(buffer, DUMP_AFTER_RESPONSE_LENGTH)) {
                serialPort.Write(CommandStrings.ACK.ToString());

                short numPages = BitConverter.ToInt16(buffer, 0);
                short firstRecord = BitConverter.ToInt16(buffer, 2);


                Debug.Print("Dumping {0} pages. First Record = {1}", numPages, firstRecord);

                if (numPages == 0) {
                    List<ArchivePacket> zeroPageList = new List<ArchivePacket>();
                    zeroPageList.Add(null);
                    callback.processArchivePage(zeroPageList);
                    return;
                }

                for (int i = 0; i < numPages; i++) {
                    //
                    // Process a single page. This will return 1 - 5 packets
                    //
                    List<ArchivePacket> list = processArchivePage(firstRecord, newestPacketTime);

                    //
                    // Sometimes the last page of the dump contains zero records. We don't
                    // need to save the newest time because we are at the end of the dump
                    // anyway.
                    //
                    if (list.Count > 0)
                        newestPacketTime = list.ElementAt(list.Count - 1).Time;

                    //
                    // Add a null to the end of the last page so the callback object
                    // knows it the last page. If the callback returns false a problem
                    // occured writing the data to the collector, so stop the download,
                    // otherwise tell the console to send the next page.
                    //
                    if (i == numPages - 1)
                        list.Add(null);

                    if (callback.processArchivePage(list)) {
                        serialPort.Write(CommandStrings.SEND_NEXT_PAGE);
                    }
                    else {
                        serialPort.Write(CommandStrings.CANCEL_DOWNLOAD);
                        break;
                    }

                    firstRecord = 0;
                }
            }
        }

        /// <summary>
        /// Read the specified number of bytes from the console.
        /// </summary>
        /// <param name="expectedBytes">The number of bytes to read</param>
        /// <returns>True if the correct number of bytes were read</returns>
        public bool Read(int expectedBytes) {
            Debug.Print("Attempting to read {0} bytes", expectedBytes);
            int readIndex = 0;
            
            //
            // Keep reading until a timeout or the bytes read is correct
            //
            while (readIndex < expectedBytes) {
                try {
                    int nbytes = serialPort.Read(buffer, readIndex, expectedBytes - readIndex);
                    readIndex += nbytes;
                }
                catch (TimeoutException) {
                    Debug.Print("Read timeout");
                    break;
                }
            }

            if (readIndex < expectedBytes) {
                serialPort.DiscardInBuffer();
                Debug.Print("Failed to read requested bytes. Expected={0}, Actual={1}", expectedBytes, readIndex);
                return false;
            }
            else {
                return true;
            }
        }

        /// <summary>
        /// Read an ACK from the console
        /// </summary>
        /// <returns>True if an ACK was read, false if nothing was read or what was read was not an ACK</returns>
        private bool consumeAck() {
            if (Read(1))
                return (char)buffer[0] == CommandStrings.ACK;
            else
                return false;
        }

        /// <summary>
        /// Send a command that expects an <LF><CR>OK<LF><CR> response.
        /// </summary>
        /// <param name="command">The command to send</param>
        /// <returns>True if the proper response was received</returns>
        private bool sendOKedCommand(String command) {
            Debug.Print("Sending command '{0}' that expects and OK response", command);
            bool success = false;

            for (int i = 0; i < 5 && !success; i++) {
                serialPort.Write(command);
                serialPort.Write(CommandStrings.COMMAND_TERMINATOR);
                if (!Read(6))
                    success = false;
                else if ((char)buffer[0] != CommandStrings.LINE_FEED ||
                         (char)buffer[1] != CommandStrings.CARRIAGE_RETURN ||
                         (char)buffer[2] != 'O' ||
                         (char)buffer[3] != 'K' ||
                         (char)buffer[4] != CommandStrings.LINE_FEED ||
                         (char)buffer[5] != CommandStrings.CARRIAGE_RETURN)
                    success = false;
                else
                    success = true;

                if (!success)
                    Wakeup();
            }

            Debug.Print("Command {0} status is {1}", command, success);
            return success;
        }

        /// <summary>
        /// Send a command that expects an ACK in response
        /// </summary>
        /// <param name="command">The command to send</param>
        /// <returns>True if an ACK was received</returns>
        private bool sendAckedCommand(String command) {
            bool success = false;

            //
            // Try 5 times for completeness. If an ACK is not received then wakeup the console and
            // try again.
            //
            for (int i = 0; i < 5 && !success; i++) {
                serialPort.Write(command);
                serialPort.Write(CommandStrings.COMMAND_TERMINATOR);
                success = consumeAck();
                if (!success)
                    Wakeup();
            }
            Debug.Print("Command {0} status is {1}", command, success);
            return success;
        }

        /// <summary>
        /// Calculate a CRC
        /// </summary>
        /// <param name="buffer">The buffer on which to calculate the CRC</param>
        /// <param name="length">The length of the buffer</param>
        /// <returns>The CRC</returns>
        public static int calculateCRC(byte[] buffer, int length) {
            int[] crc_table = {
                0x0000, 0x1021, 0x2042, 0x3063, 0x4084, 0x50a5, 0x60c6, 0x70e7,
                0x8108, 0x9129, 0xa14a, 0xb16b, 0xc18c, 0xd1ad, 0xe1ce, 0xf1ef,
                0x1231, 0x0210, 0x3273, 0x2252, 0x52b5, 0x4294, 0x72f7, 0x62d6,
                0x9339, 0x8318, 0xb37b, 0xa35a, 0xd3bd, 0xc39c, 0xf3ff, 0xe3de,
                0x2462, 0x3443, 0x0420, 0x1401, 0x64e6, 0x74c7, 0x44a4, 0x5485,
                0xa56a, 0xb54b, 0x8528, 0x9509, 0xe5ee, 0xf5cf, 0xc5ac, 0xd58d,
                0x3653, 0x2672, 0x1611, 0x0630, 0x76d7, 0x66f6, 0x5695, 0x46b4,
                0xb75b, 0xa77a, 0x9719, 0x8738, 0xf7df, 0xe7fe, 0xd79d, 0xc7bc,
                0x48c4, 0x58e5, 0x6886, 0x78a7, 0x0840, 0x1861, 0x2802, 0x3823,
                0xc9cc, 0xd9ed, 0xe98e, 0xf9af, 0x8948, 0x9969, 0xa90a, 0xb92b,
                0x5af5, 0x4ad4, 0x7ab7, 0x6a96, 0x1a71, 0x0a50, 0x3a33, 0x2a12,
                0xdbfd, 0xcbdc, 0xfbbf, 0xeb9e, 0x9b79, 0x8b58, 0xbb3b, 0xab1a,
                0x6ca6, 0x7c87, 0x4ce4, 0x5cc5, 0x2c22, 0x3c03, 0x0c60, 0x1c41,
                0xedae, 0xfd8f, 0xcdec, 0xddcd, 0xad2a, 0xbd0b, 0x8d68, 0x9d49,
                0x7e97, 0x6eb6, 0x5ed5, 0x4ef4, 0x3e13, 0x2e32, 0x1e51, 0x0e70,
                0xff9f, 0xefbe, 0xdfdd, 0xcffc, 0xbf1b, 0xaf3a, 0x9f59, 0x8f78,
                0x9188, 0x81a9, 0xb1ca, 0xa1eb, 0xd10c, 0xc12d, 0xf14e, 0xe16f,
                0x1080, 0x00a1, 0x30c2, 0x20e3, 0x5004, 0x4025, 0x7046, 0x6067,
                0x83b9, 0x9398, 0xa3fb, 0xb3da, 0xc33d, 0xd31c, 0xe37f, 0xf35e,
                0x2b1,  0x1290, 0x22f3, 0x32d2, 0x4235, 0x5214, 0x6277, 0x7256,
                0xb5ea, 0xa5cb, 0x95a8, 0x8589, 0xf56e, 0xe54f, 0xd52c, 0xc50d,
                0x34e2, 0x24c3, 0x14a0, 0x0481, 0x7466, 0x6447, 0x5424, 0x4405,
                0xa7db, 0xb7fa, 0x8799, 0x97b8, 0xe75f, 0xf77e, 0xc71d, 0xd73c,
                0x26d3, 0x36f2, 0x0691, 0x16b0, 0x6657, 0x7676, 0x4615, 0x5634,
                0xd94c, 0xc96d, 0xf90e, 0xe92f, 0x99c8, 0x89e9, 0xb98a, 0xa9ab,
                0x5844, 0x4865, 0x7806, 0x6827, 0x18c0, 0x08e1, 0x3882, 0x28a3,
                0xcb7d, 0xdb5c, 0xeb3f, 0xfb1e, 0x8bf9, 0x9bd8, 0xabbb, 0xbb9a,
                0x4a75, 0x5a54, 0x6a37, 0x7a16, 0x0af1, 0x1ad0, 0x2ab3, 0x3a92,
                0xfd2e, 0xed0f, 0xdd6c, 0xcd4d, 0xbdaa, 0xad8b, 0x9de8, 0x8dc9,
                0x7c26, 0x6c07, 0x5c64, 0x4c45, 0x3ca2, 0x2c83, 0x1ce0, 0x0cc1,
                0xef1f, 0xff3e, 0xcf5d, 0xdf7c, 0xaf9b, 0xbfba, 0x8fd9, 0x9ff8,
                0x6e17, 0x7e36, 0x4e55, 0x5e74, 0x2e93, 0x3eb2, 0x0ed1, 0x1ef0,
            };
            int crc = 0;

            for (int i = 0; i < length; i++)
                crc = ((crc_table[(crc >> 8) ^ (int)buffer[i]]) ^ ((crc << 8) & 0xFFFF)) & 0xFFFF;

            return crc;
        }

        /// <summary>
        /// Check the checksum of a packet received from the console
        /// </summary>
        /// <param name="buffer">The buffer to be checked</param>
        /// <param name="length">The number of bytes to be checked. The checksum is expected to be the two bytes following the data</param>
        /// <param name="checksumIndex">The location within the buffer that the checksum resides</param>
        /// <returns></returns>
        public static bool checkCRC(byte[] buffer, int length) {
            byte[] crcBytes = { buffer[length + 1], buffer[length] };

            int sentCRC = BitConverter.ToInt16(crcBytes, 0) & 0xFFFF;
            int calculatedCRC = VantagePro2Station.calculateCRC(buffer, length);

            Debug.Print("CRC Compare. Sent: {0}  Calculated: {1}", sentCRC, calculatedCRC);

            return sentCRC == calculatedCRC;
        }
    }
}