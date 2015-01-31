using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.IO;
using System.Threading;
using System.Globalization;
using System.Diagnostics;

namespace VantagePro2 {
    /// <summary>
    /// Class that communicates with the WeatherSense collector.
    /// </summary>
    public class WeatherSenseSocket {
        /// <summary>
        /// Callback interface used to report connections and newest record time
        /// </summary>
        public interface HistoricalReader {
            void newestHistoryRecordTime(DateTime time);
            void connected();
        }

        private TcpClient socket = null;
        private NetworkStream stream;
        private HistoricalReader reader;

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="reader">Callback</param>
        public WeatherSenseSocket(HistoricalReader reader) {
            this.reader = reader;
        }

        /// <summary>
        /// Disconnect from the collector
        /// </summary>
        public void disconnect() {
            stream.Close();
            socket.Close();
            stream = null;
            socket = null;
        }

        /// <summary>
        /// Check if there is a connector with the collector
        /// </summary>
        /// <returns>True if a connection exists</returns>
        public bool isConnected() {
            return stream != null;
        }

        /// <summary>
        /// Write a string to the collector
        /// </summary>
        /// <param name="s">The string to write</param>
        private void write(String s) {
            if (stream != null) {
                byte[] buffer = System.Text.Encoding.ASCII.GetBytes(s + "\n");
                stream.Write(buffer, 0, buffer.Length);
                stream.Flush();
            }
        }

        /// <summary>
        /// Connect to the collector
        /// </summary>
        private void connect() {
            //
            // Skip it if we are already connected
            //
            if (stream == null) {           
                socket = new TcpClient();
                try {
                    socket.Connect("127.0.0.1", 11462);
                }
                catch (SocketException e) {
                    Debug.Print("Connection to collector failed: {0}", e.Message);
                    return;
                }
                stream = socket.GetStream();
                Debug.Print("Connected to collector");

                reader.connected();
  
                //
                // Once we connect, send the collector an initialize command to which it should
                // respond with the date of the most newest record in its database
                //
                write(String.Format("message_type=initialize;"));

                //
                // Wait for data to be available for about 5 seconds
                for (int i = 0; i < 50 && !stream.DataAvailable; i++)
                    Thread.Sleep(100);

                //
                // If data is available, read it and parse the data as a date
                //
                if (stream.DataAvailable) {
                    stream.ReadTimeout = 5000;
                    byte[] buffer = new byte[100];
                    int bytesRead = stream.Read(buffer, 0, buffer.Length);
                    String s = System.Text.Encoding.ASCII.GetString(buffer, 0, bytesRead);
                    Debug.Print("Read {0} bytes: '{1}' from collector", bytesRead, s);
                    DateTime time = DateTime.ParseExact(s, "yyyyMMdd HHmm", CultureInfo.InvariantCulture);
                    Debug.Print("Converted to date: {0}", time);

                    reader.newestHistoryRecordTime(time);
                }
                else {
                    disconnect();
                    Debug.Print("No response received from collector. Closing socket.");
                }
            }
        }

        /// <summary>
        /// Send data to the WeatherSense collector
        /// </summary>
        /// <param name="data">The data to send</param>
        /// <returns>True if the data was sent</returns>
        public bool sendData(String data) {
            try {
                //
                // Connect will do nothing if we are already connected
                //
                connect();
                write(data);
                return true;
            }
            catch (IOException e1) {
                Debug.Print("Caught Exception: {0}\n{1}", e1.Message, e1.StackTrace);
                disconnect();
            }
            catch (Exception e2) {
                Debug.Print("Caught Exception: {0}", e2.StackTrace);
            }
            return false;
        }
    }
}
