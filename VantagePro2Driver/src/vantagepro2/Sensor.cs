using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace VantagePro2 {
    /// <summary>
    /// Class that holds information about the sensors attached to this weather station
    /// </summary>
    public class Sensor {
        public enum SensorType {
            THERMOMETER, ULTRAVIOLET, ANEMOMETER, LEAF_WETNESS, LEAF_TEMPERATURE, SOLAR_RADIATION, SOIL_MOISTURE, SOIL_TEMPERATURE, HYGROMETER, RAIN_COLLECTOR, BAROMETER
        }
        public static readonly int LAST_DEFAULT_SENSOR_ID = 99;
        public static readonly int THERMOMETER_BASE_SENSOR_ID = 100;
        public static readonly int HYGROMETER_BASE_SENSOR_ID = 200;
        public static readonly int LEAF_WETNESS_BASE_SENSOR_ID = 400;
        public static readonly int LEAF_TEMPERATURE_BASE_SENSOR_ID = 500;
        public static readonly int SOIL_MOISTURE_BASE_SENSOR_ID = 600;
        public static readonly int SOIL_TEMPERATURE_BASE_SENSOR_ID = 700;
        public static readonly int BAROMETER_SENSOR_ID = 1;
        public static readonly int WIND_SENSOR_ID = 2;
        public static readonly int UV_SENSOR_ID = 3;
        public static readonly int SOLAR_RADIATION_SENSOR_ID = 4;
        public static readonly int RAIN_COLLECTOR_SENSOR_ID = 5;
        public static readonly int INDOOR_THERMOMETER_SENSOR_ID = 6;
        public static readonly int OUTDOOR_THERMOMETER_SENSOR_ID = 7;
        public static readonly int INDOOR_HYGROMETER_SENSOR_ID = 8;
        public static readonly int OUTDOOR_HYGROMETER_SENSOR_ID = 9;
        private SensorType sensorType;
        private int sensorId;

        /// <summary>
        /// Constructor.
        /// </summary>
        /// <param name="type">The sensor type</param>
        /// <param name="id">The ID of the sensor</param>
        public Sensor(SensorType type, int id) {
            sensorType = type;
            sensorId = id;
        }

        /// <summary>
        /// Figure out what sensors this weather station has based on the LOOP packet
        /// </summary>
        /// <param name="loop">The loop packet</param>
        /// <param name="sensors">The list to which to add the detected sensors</param>
        public static void detectSensors(LoopPacket loop, List<Sensor> sensors) {
            sensors.Add(new Sensor(SensorType.THERMOMETER, OUTDOOR_THERMOMETER_SENSOR_ID));
            sensors.Add(new Sensor(SensorType.THERMOMETER, INDOOR_THERMOMETER_SENSOR_ID));
            sensors.Add(new Sensor(SensorType.HYGROMETER, OUTDOOR_HYGROMETER_SENSOR_ID));
            sensors.Add(new Sensor(SensorType.HYGROMETER, INDOOR_HYGROMETER_SENSOR_ID));
            sensors.Add(new Sensor(SensorType.BAROMETER, BAROMETER_SENSOR_ID));
            sensors.Add(new Sensor(SensorType.ANEMOMETER, WIND_SENSOR_ID));
            sensors.Add(new Sensor(SensorType.RAIN_COLLECTOR, RAIN_COLLECTOR_SENSOR_ID));

            if (loop.isUvIndexValid())
                sensors.Add(new Sensor(SensorType.ULTRAVIOLET, UV_SENSOR_ID));

            if (loop.isSolarRadiationValid())
                sensors.Add(new Sensor(SensorType.SOLAR_RADIATION, SOLAR_RADIATION_SENSOR_ID));

            for (int i = 0; i < LoopPacket.NUM_EXTRA_HUMIDITIES; i++) {
                if (loop.isExtraHumidityValid(i))
                    sensors.Add(new Sensor(SensorType.HYGROMETER, HYGROMETER_BASE_SENSOR_ID + i));
            }

            for (int i = 0; i < LoopPacket.NUM_EXTRA_TEMPERATURES; i++) {
                if (loop.isExtraTemperatureValid(i))
                    sensors.Add(new Sensor(SensorType.THERMOMETER, THERMOMETER_BASE_SENSOR_ID + i));
            }

            for (int i = 0; i < LoopPacket.NUM_LEAF_WETNESSES; i++) {
                if (loop.isLeafWetnessValid(i))
                    sensors.Add(new Sensor(SensorType.LEAF_WETNESS, LEAF_WETNESS_BASE_SENSOR_ID + i));
            }

            for (int i = 0; i < LoopPacket.NUM_SOIL_MOISTURES; i++) {
                if (loop.isSoilMoistureValid(i))
                    sensors.Add(new Sensor(SensorType.SOIL_MOISTURE, SOIL_MOISTURE_BASE_SENSOR_ID + i));
            }

            for (int i = 0; i < LoopPacket.NUM_SOIL_TEMPERATURES; i++) {
                if (loop.isSoilTemperatureValid(i))
                    sensors.Add(new Sensor(SensorType.SOIL_TEMPERATURE, SOIL_TEMPERATURE_BASE_SENSOR_ID + i));
            }

            for (int i = 0; i < LoopPacket.NUM_LEAF_TEMPERATURES; i++) {
                if (loop.isLeafTemperatureValid(i))
                    sensors.Add(new Sensor(SensorType.LEAF_TEMPERATURE, LEAF_TEMPERATURE_BASE_SENSOR_ID + i));
            }
        }

        /// <summary>
        /// Format the sensor message
        /// </summary>
        /// <param name="list">The list from which to build the message</param>
        /// <returns>The message as a string</returns>
        public static String formatMessage(List<Sensor> list) {
            StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            sb.AppendFormat("<sensorMessage>");
            foreach (Sensor sensor in list) {
                sb.AppendFormat("<sensor><type>{0}</type><sensorId>{1}</sensorId></sensor>", sensor.sensorType, sensor.sensorId);
            }
            sb.Append("</sensorMessage>");

            return sb.ToString();
        }
    }
}
