using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace VantagePro2 {
    /// <summary>
    /// Class that provides methods for converting to WeatherSense units
    /// </summary>
    class UnitConverter {
        public static float toCelcius(float temperature) {
            return (temperature - 32.0F) / 1.8F;
        }

        public static float toMillimeter(float depth) {
            return depth * 25.4F;
        }

        public static float toInchesHg(float pressure) {
            return pressure * 33.86386F;
        }

        public static float toMetersPerSecond(float speed) {
            return speed / 2.23693F;
        }

        public static float feetToMillimeters(float elevation) {
            return elevation * 304.8F;
        }
    }
}