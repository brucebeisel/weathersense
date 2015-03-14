using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace VantagePro2 {
    /// <summary>
    /// Class that represents a sensor station. A sensor station has one or more sensors attached to it and
    /// it communicates with the weather station console.
    /// </summary>
    public class SensorStation {
        /// <summary>
        /// The types of sensor stations supported by WeatherSense. This is the same list that the Vantage Pro 2 supports.
        /// </summary>
        public enum SensorStationType {
            INTEGRATED_SENSOR_STATION = 0,
            TEMPERATURE_ONLY = 1,
            HUMIDITY_ONLY = 2,
            TEMPERATURE_HUMIDITY = 3,
            ANEMOMETER = 4,
            RAIN = 5,
            LEAF = 6,
            SOIL = 7,
            SOIL_LEAF = 8,
            NO_STATION = 10
        };
        private SensorStationType type;
        public SensorStationType Type {
            get { return type; }
        }

        private int sensorIndex;
        public int SensorIndex {
            get { return sensorIndex; }
        }

        private bool batteryStatus;
        public bool BatteryStatus {
            get { return batteryStatus; }
            set { batteryStatus = value; }
        }

        private int linkQuality;
        public int LinkQuality {
            get { return linkQuality; }
            set { linkQuality = value; }
        }

        public SensorStation(SensorStationType type, int sensorIndex) {
            this.type = type;
            this.sensorIndex = sensorIndex;
        }


        public override String ToString() {
            StringBuilder sb = new StringBuilder();
            sb.AppendFormat("Station Type: {0}, Station Index: {1}", type, sensorIndex);
            return sb.ToString();
        }

        /// <summary>
        /// Build the sensor station message that contains all the sensor stations that this weather station knows about
        /// </summary>
        /// <param name="list">A list of sensor stations</param>
        /// <returns>The formatted XML message</returns>
        public static String formatSensorStationMessage(List<SensorStation> list) {
            StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            sb.Append("<sensorStationMessage>");
            foreach (SensorStation station in list) {
                sb.Append("<sensorStation>");
                sb.AppendFormat("<name>Sensor Station - {0}</name><type>{1}</type><sensorStationId>{2}</sensorStationId>", station.sensorIndex, station.type, station.sensorIndex);
                sb.Append("</sensorStation>");
            }

            sb.Append("</sensorStationMessage>");

            return sb.ToString();
        }

        /// <summary>
        /// Build the sensor station status message that contains all the sensor stations that this weather station knows about
        /// </summary>
        /// <param name="list">A list of sensor stations</param>
        /// <returns>The formatted XML sensor status message</returns>
        public static String formatSensorStationStatusMessage(List<SensorStation> list, DateTime time) {
            StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            sb.Append("<sensorStationStatusMessage>");
            foreach (SensorStation station in list) {
                sb.Append("<sensorStationStatus>");
                sb.AppendFormat("<time>{0:O}</time><sensorStationId>{1}</sensorStationId><batteryOk>{2}</batteryOk>", time, station.SensorIndex, station.BatteryStatus ? "true" : "false");
                if (station.Type == SensorStationType.INTEGRATED_SENSOR_STATION)
                    sb.AppendFormat("<linkQuality>{0}</linkQuality>", station.LinkQuality);
                sb.Append("</sensorStationStatus>");
            }
            sb.Append("</sensorStationStatusMessage>");

            return sb.ToString();
        }
    }
}
