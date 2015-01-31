using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;

namespace VantagePro2 {
    /// <summary>
    /// Class that holds a single archive packet. It also formats the WeatherSense XML message.
    /// </summary>
    public class ArchivePacket {
        /// <summary>
        /// Time property
        /// </summary>
        /// <value>The time that this archive packet was recorded</value>
        private DateTime time;
        public DateTime Time {
            get { return time; }
        }

        private int windSampleCount;
        public int WindSampleCount {
            get { return windSampleCount; }
        }
        
        public static readonly int BYTES_PER_RECORD = 52;
        private int archivePeriod;
        private float rainInterval;
        private float windGust;
        private int windGustDirection;

        private byte[] buffer = new byte[BYTES_PER_RECORD];
        public byte[] Buffer {
            get { return buffer; }
        }

        public static readonly int NUM_EXTRA_TEMPERATURES = 3;
        public static readonly int NUM_LEAF_TEMPERATURES = 2;
        public static readonly int NUM_LEAF_WETNESSES = 2;
        public static readonly int NUM_SOIL_TEMPERATURES = 3;
        public static readonly int NUM_EXTRA_HUMIDITIES = 2;
        public static readonly int NUM_SOIL_MOISTURES = 4;
        private static readonly int UNKNOWN_TEMPERATURE = 32767;
        private static readonly int UNKNOWN_HIGH_TEMPERATURE = -32768;
        private static readonly int UNKNOWN_SOLAR_RADIATION = 32767;
        private static readonly int UNKNOWN_HUMIDITY = 255;
        private static readonly int UNKNOWN_WIND_DIRECTION = 255;
        private static readonly int UNKNOWN_WIND_SPEED = 255;
        private static readonly int UNKNOWN_PRESSURE = 0;
        private static readonly int UNKNOWN_UV_INDEX = 255;
        private static readonly int UNKNOWN_EXTRA_TEMPERATURE = 255;
        private static readonly int UNKNOWN_LEAF_WETNESS = 255;
        private static readonly int UNKNOWN_SOIL_MOISTURE = 255;
        private static readonly int UNKNOWN_ET = 0;
        private static readonly float TEMPERATURE_SCALE = 10.0F;
        private static readonly float BAROMETER_SCALE = 1000.0F;
        private static readonly float UV_INDEX_SCALE = 10.0F;
        private static readonly float ET_SCALE = 1000.0F;
        private static readonly int EXTRA_TEMPERATURE_OFFSET = 90;
        private static readonly float DEGREES_PER_SLICE = 22.5F;

        /// <summary>
        /// Constuctor.
        /// </summary>
        /// <param name="buffer">The buffer from which to extract the data for the archive packet</param>
        /// <param name="offset">The offset within the buffer that the packet starts</param>
        /// <param name="rainInterval">The amount of rain each tip of the rain bucket represents</param>
        /// <param name="archivePeriod">The archive period (in minutes)</param>
        /// <param name="windGust">The current wind gust value from the loop 2 packet</param>
        /// <param name="windGustDirection">The current wind gust direction from the loop 2 packet</param>
        public ArchivePacket(byte[] buffer, int offset, float rainInterval, int archivePeriod, float windGust, int windGustDirection) {
            this.archivePeriod = archivePeriod;
            this.rainInterval = rainInterval;
            this.windGust = windGust;
            this.windGustDirection = windGustDirection;
            for (int i = 0; i < BYTES_PER_RECORD; i++) {
                this.buffer[i] = buffer[offset + i];
            }

            windSampleCount = BitConverter.ToInt16(this.buffer, 18);
            time = extractDate();
        }

        private DateTime extractDate() {
            short date = BitConverter.ToInt16(buffer, 0);
            short time = BitConverter.ToInt16(buffer, 2);
            int year = ((date >> 9) & 0x3F) + 2000;
            int month = (date >> 5) & 0xF;
            int day = date & 0x1F;
            int hour = time / 100;
            int minute = time % 100;

            DateTime dateTime = new DateTime(year, month, day, hour, minute, 0, DateTimeKind.Local);
            return dateTime;
        }

        /// <summary>
        /// Format the WeatherSense historical record XML message.
        /// <para>The message will only include those values that are valid (not dashed)</para>
        /// </summary>
        /// <returns>The message</returns>
        public String formatMessage() {
            StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            sb.Append("<historicalRecord>");
            DateTime time = extractDate();
            sb.AppendFormat("<time>{0:O}</time>", time);
            sb.AppendFormat("<duration>{0}</duration>", archivePeriod * 60);

            short value16 = BitConverter.ToInt16(buffer, 4);
            if (value16 != UNKNOWN_TEMPERATURE)
                sb.AppendFormat("<avgOutdoorTemperature><value>{0}</value></avgOutdoorTemperature>", UnitConverter.toCelcius((float)value16 / TEMPERATURE_SCALE));

            value16 = BitConverter.ToInt16(buffer, 6);
            if (value16 != UNKNOWN_HIGH_TEMPERATURE)
                sb.AppendFormat("<highOutdoorTemperature><value>{0}</value></highOutdoorTemperature>", UnitConverter.toCelcius((float)value16 / TEMPERATURE_SCALE));

            value16 = BitConverter.ToInt16(buffer, 8);
            if (value16 != UNKNOWN_TEMPERATURE)
                sb.AppendFormat("<lowOutdoorTemperature><value>{0}</value></lowOutdoorTemperature>", UnitConverter.toCelcius((float)value16 / TEMPERATURE_SCALE));

            value16 = BitConverter.ToInt16(buffer, 10);
            sb.AppendFormat("<rainfall><value>{0}</value></rainfall>", UnitConverter.toMillimeter((float)value16 * rainInterval));

            value16 = BitConverter.ToInt16(buffer, 12);
            sb.AppendFormat("<highRainfallRate><value>{0}</value></highRainfallRate>", UnitConverter.toMillimeter((float)value16 * rainInterval));

            value16 = BitConverter.ToInt16(buffer, 14);
            if (value16 != UNKNOWN_PRESSURE)
                sb.AppendFormat("<baroPressure><value>{0}</value></baroPressure>", UnitConverter.toInchesHg((float)value16 / BAROMETER_SCALE));

            value16 = BitConverter.ToInt16(buffer, 16);
            if (value16 != UNKNOWN_SOLAR_RADIATION)
                sb.AppendFormat("<avgSolarRadiation><value>{0}</value></avgSolarRadiation>", value16);
          
            value16 = BitConverter.ToInt16(buffer, 20);
            if (value16 != UNKNOWN_TEMPERATURE)
                sb.AppendFormat("<indoorTemperature><value>{0}</value></indoorTemperature>", UnitConverter.toCelcius((float)value16 / TEMPERATURE_SCALE));

            byte value8 = buffer[22];
            if (value8 != UNKNOWN_HUMIDITY)
                sb.AppendFormat("<indoorHumidity><value>{0}</value></indoorHumidity>", value8);

            value8 = buffer[23];
            if (value8 != UNKNOWN_HUMIDITY)
                sb.AppendFormat("<outdoorHumidity><value>{0}</value></outdoorHumidity>", value8);

            int windSpeed = buffer[24];
            if (windSpeed == UNKNOWN_WIND_SPEED)
                windSpeed = 0;
            int windDirection = buffer[27];
            sb.AppendFormat("<avgWind><speed><value>{0}</value></speed>", UnitConverter.toMetersPerSecond(windSpeed));
            if (windDirection != UNKNOWN_WIND_DIRECTION)
                sb.AppendFormat("<direction><value>{0}</value></direction>", windDirection * DEGREES_PER_SLICE);
            sb.Append("</avgWind>");

            windSpeed = buffer[25];
            if (windSpeed == UNKNOWN_WIND_SPEED)
                windSpeed = 0;
            windDirection = buffer[26];
            sb.AppendFormat("<highWind><speed><value>{0}</value></speed>", UnitConverter.toMetersPerSecond(windSpeed));
            if (windDirection != UNKNOWN_WIND_DIRECTION)
                sb.AppendFormat("<direction><value>{0}</value></direction>", windDirection * DEGREES_PER_SLICE);
            sb.Append("</highWind>");

            
            //
            // Use the wind gust data from the LOOP 2 packet if this archive packet is less than 10 minutes old,
            // otherwise just use the high wind speed as the gust
            //
            DateTime now = DateTime.Now;
            TimeSpan span = now.Subtract(time);
            if (span.TotalMinutes < 10) {
                if (windGust != 0.0)
                    sb.AppendFormat("<windGust><speed><value>{0}</value></speed><direction><value>{1}</value></direction></windGust>", windGust, windGustDirection);
            }
            else {
                windSpeed = buffer[25];
                if (windSpeed == UNKNOWN_WIND_SPEED)
                    windSpeed = 0;
                windDirection = buffer[26];
                sb.AppendFormat("<windGust><speed><value>{0}</value></speed>", UnitConverter.toMetersPerSecond(windSpeed));
                if (windDirection != UNKNOWN_WIND_DIRECTION)
                    sb.AppendFormat("<direction><value>{0}</value></direction>", windDirection * DEGREES_PER_SLICE);
                sb.Append("</windGust>");
            }

            value8 = buffer[28];
            if (value8 != UNKNOWN_UV_INDEX)
                sb.AppendFormat("<avgUvIndex><value>{0}</value></avgUvIndex>", (float)value8 / UV_INDEX_SCALE);

            value8 = buffer[29];
            if (value8 != UNKNOWN_ET)
                sb.AppendFormat("<evapotranspiration><value>{0}</value></evapotranspiration>", UnitConverter.toMillimeter((float)value8 / ET_SCALE));

            value16 = BitConverter.ToInt16(buffer, 30);
            if (value16 != UNKNOWN_SOLAR_RADIATION)
                sb.AppendFormat("<highSolarRadiation><value>{0}</value></highSolarRadiation>", value16);

            value8 = buffer[32];
            if (value8 != UNKNOWN_UV_INDEX)
                sb.AppendFormat("<highUvIndex><value>{0}</value></highUvIndex>", (float)value8 / UV_INDEX_SCALE);

            /*
            for (int i = 0; i < NUM_LEAF_TEMPERATURES; i++) {
                value8 = buffer[34 + i];
                if (value8 != UNKNOWN_EXTRA_TEMPERATURE)
                    sb.AppendFormat("leaf_temperature[{0}]={1};", i, value8 - EXTRA_TEMPERATURE_OFFSET);
            }
            */


            sb.Append("<leafWetnessSensorEntries>");
            for (int i = 0; i < NUM_LEAF_WETNESSES; i++) {
                int leafWetness = buffer[36 + i];
                if (leafWetness != UNKNOWN_LEAF_WETNESS) {
                    sb.AppendFormat("<entry><key>{0}</key><value><sensorId>{1}</sensorId><sensorType>LEAF_WETNESS</sensorType>", 500 + i, 500 + i);
                    sb.Append("<measurement xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"leafWetness\">");
                    sb.AppendFormat("<value>{0}</value></measurement></value></entry>", leafWetness);
                }
            }
            sb.Append("</leafWetnessSensorEntries>");

            /*
            for (int i = 0; i < NUM_SOIL_TEMPERATURES; i++) {
                value8 = buffer[38 + i];
                if (value8 != UNKNOWN_EXTRA_TEMPERATURE)
                    sb.AppendFormat("soil_temperature[{0}]={1};", i, value8 - EXTRA_TEMPERATURE_OFFSET);
            }
            */

            sb.Append("<humiditySensorEntries>");
            for (int i = 0; i < NUM_EXTRA_HUMIDITIES; i++) {
                int humidity = buffer[43 + i];
                if (humidity != UNKNOWN_HUMIDITY) {
                    sb.AppendFormat("<entry><key>{0}</key><value><sensorId>{1}</sensorId><sensorType>HYGROMETER</sensorType>", 200 + i, 200 + i);
                    sb.Append("<measurement xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"humidity\">");
                    sb.AppendFormat("<value>{0}</value></measurement></value></entry>", humidity);
                }
            }
            sb.Append("</humiditySensorEntries>");

            sb.Append("<temperatureSensorEntries>");
            for (int i = 0; i < NUM_EXTRA_TEMPERATURES; i++) {
                int temperature = buffer[45 + i];
                if (temperature != UNKNOWN_EXTRA_TEMPERATURE) {
                    sb.AppendFormat("<entry><key>{0}</key><value><sensorId>{1}</sensorId><sensorType>THERMOMETER</sensorType>", 100 + i, 100 + i);
                    sb.Append("<measurement xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"temperature\">");
                    sb.AppendFormat("<value>{0}</value></measurement></value></entry>", UnitConverter.toCelcius(temperature - EXTRA_TEMPERATURE_OFFSET));
                }
            }
            sb.Append("</temperatureSensorEntries>");

            sb.Append("<soilMoistureSensorEntries>");
            for (int i = 0; i < NUM_SOIL_MOISTURES; i++) {
                int soilMoisture = buffer[48 + i];
                if (soilMoisture != UNKNOWN_SOIL_MOISTURE) {
                    sb.AppendFormat("<entry><key>{0}</key><value><sensorId>{1}</sensorId><sensorType>SOIL_MOISTURE</sensorType>", 600 + i, 600 + i);
                    sb.Append("<measurement xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"soilMoisture\">");
                    sb.AppendFormat("<value>{0}</value></measurement></value></entry>", soilMoisture);
                }
            }
            sb.Append("</soilMoistureSensorEntries>");
            sb.Append("</historicalRecord>");
            return sb.ToString();
        }

        public override String ToString() {
            return formatMessage();
        }
    }
}
