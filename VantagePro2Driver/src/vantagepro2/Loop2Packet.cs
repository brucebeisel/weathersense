using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;

namespace VantagePro2 {
    /// <summary>
    /// Class that contains the data from the LOOP 2 packet that the WeatherSense product is interested in.
    /// </summary>
    public class Loop2Packet {
        /// <summary>
        /// 10 Minutes Wind Gust Speed Property
        /// </summary>
        /// <value>The speed of the highest wind gust over the past 10 minutes.</value>
        private float windGust10Minute;
        public float WindGust10Minute {
            get { return windGust10Minute; }
            set { windGust10Minute = value; }
        }

        private int windGustDirection10Minute;
        public int WindGustDirection10Minute {
            get { return windGustDirection10Minute; }
        }

        private float windSpeed2MinuteAvg;
        public float WindSpeed2MinuteAvg {
            get { return windSpeed2MinuteAvg; }
            set { windSpeed2MinuteAvg = value; }
        }

        private float rain15Minute;
        public float Rain15Minute {
            get { return rain15Minute; }
            set { rain15Minute = value; }
        }

        private float rainHour;
        public float RainHour {
            get { return rainHour; }
            set { rainHour = value; }
        }

        private float rain24Hour;
        public float Rain24Hour {
            get { return rain24Hour; }
            set { rain24Hour = value; }
        }

        private float dewPoint;
        public float DewPoint {
            get { return dewPoint; }
            set { dewPoint = value; }
        }

        private float heatIndex;
        public float HeatIndex {
            get { return heatIndex; }
            set { heatIndex = value; }
        }

        private float windChill;
        public float WindChill {
            get { return windChill; }
            set { windChill = value; }
        }

        private float thsw;
        public float Thsw {
            get { return thsw; }
        }

        private bool thswValid;
        public bool ThswValid {
            get { return thswValid; }
        }

        private float atmPressure;
        public float AtmPressure {
            get { return atmPressure; }
            set { atmPressure = value; }
        }

        /// <summary>
        /// Parse the LOOP 2 packet
        /// </summary>
        /// <param name="buffer">The buffer containing the LOOP 2 packet</param>
        /// <param name="rainInterval">The amount of rain 1 bucket tip represents</param>
        /// <returns>True if the packet was parsed successfully and passed the CRC check</returns>
        public bool parseLoop2Packet(byte[] buffer, float rainInterval) {
            if ((char)buffer[0] != 'L' || (char)buffer[1] != 'O' || (char)buffer[2] != 'O')
                return false;

            //
            // Check for correct packet type
            //
            int packetType = (int)buffer[4];
            if (packetType != 1) {
                Debug.Print("Invalid packet type for LOOP2 packet: {0}", packetType);
                return false;
            }

            windGust10Minute = UnitConverter.toMetersPerSecond((float)BitConverter.ToInt16(buffer, 22));
            windGustDirection10Minute = BitConverter.ToInt16(buffer, 24);
            if (windGustDirection10Minute == 360)
                windGustDirection10Minute = 0;

            windSpeed2MinuteAvg = UnitConverter.toMetersPerSecond((float)BitConverter.ToInt16(buffer, 20) / 10.0F);
            rain15Minute = UnitConverter.toMillimeter((float)BitConverter.ToInt16(buffer, 52) * rainInterval);
            rainHour = UnitConverter.toMillimeter((float)BitConverter.ToInt16(buffer, 54) * rainInterval);
            rain24Hour = UnitConverter.toMillimeter((float)BitConverter.ToInt16(buffer, 58) * rainInterval);
            dewPoint = UnitConverter.toCelcius(BitConverter.ToInt16(buffer, 30));
            heatIndex = UnitConverter.toCelcius(BitConverter.ToInt16(buffer, 35));
            windChill = UnitConverter.toCelcius(BitConverter.ToInt16(buffer, 37));
            int thswRaw = BitConverter.ToInt16(buffer, 37);
            thswValid = thswRaw != 32767;
            thsw = UnitConverter.toCelcius(thswRaw);
            atmPressure = (float)BitConverter.ToInt16(buffer, 65) / 1000.0F;


            if ((char)buffer[95] != CommandStrings.LINE_FEED || (char)buffer[96] != CommandStrings.CARRIAGE_RETURN) {
                Console.WriteLine("<LF><CR> not found");
                return false;
            }

            //Console.WriteLine(this.ToString());

            return VantagePro2Station.checkCRC(buffer, 97);

        }
    }
}