using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace VantagePro2 {
    /// <summary>
    /// Class that represents the WeatherSense current weather message which is made up of the LOOP and LOOP 2 packets.
    /// </summary>
    public class CurrentWeather {
        private LoopPacket loopPacket;
        private Loop2Packet loop2Packet;
        private WindDirectionSlices pastWindDirs;
        public int NextPacket {
            get { return loopPacket.NextRecord; }
        }

        /// <summary>
        /// Constructor.
        /// </summary>
        /// <param name="loopPacket">The LOOP packet</param>
        /// <param name="loop2Packet">The LOOP 2 packet</param>
        /// <param name="pastWindDirs">The past 4 wind directions</param>
        public CurrentWeather(LoopPacket loopPacket, Loop2Packet loop2Packet, WindDirectionSlices pastWindDirs) {
            this.loopPacket = loopPacket;
            this.loop2Packet = loop2Packet;
            this.pastWindDirs = pastWindDirs;
        }

        /// <summary>
        /// Format the WeatherSense current weather XML message.
        /// </summary>
        /// <returns>The message as a string</returns>
        public String formatMessage() {
            StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
	    sb.AppendFormat("<currentWeather>");
            sb.AppendFormat("<time>{0:O}</time>", DateTime.Now);
            sb.AppendFormat("<indoorTemperature><value>{0}</value></indoorTemperature>", loopPacket.InsideTemp);
	    sb.AppendFormat("<indoorHumidity><value>{0}</value></indoorHumidity>", loopPacket.InsideHumidity);
            sb.AppendFormat("<outdoorTemperature><value>{0}</value></outdoorTemperature>", loopPacket.OutsideTemp);
	    sb.AppendFormat("<outdoorHumidity><value>{0}</value></outdoorHumidity>", loopPacket.OutsideHumidity);
	    sb.AppendFormat("<dewPoint><value>{0}</value></dewPoint>", loop2Packet.DewPoint);
	    sb.AppendFormat("<windChill><value>{0}</value></windChill>", loop2Packet.WindChill);
	    sb.AppendFormat("<heatIndex><value>{0}</value></heatIndex>", loop2Packet.HeatIndex);
            if (loop2Packet.ThswValid)
                sb.AppendFormat("<thsw><value>{0}</value></thsw>", loop2Packet.Thsw);
            sb.AppendFormat("<wind><speed><value>{0}</value></speed><direction><value>{1}</value></direction></wind>", loopPacket.WindSpeed, loopPacket.WindDirection);
            sb.AppendFormat("<windGust><speed><value>{0}</value></speed><direction><value>{1}</value></direction></windGust>", loop2Packet.WindGust10Minute, loop2Packet.WindGustDirection10Minute);

	    sb.AppendFormat("<windSpeed10MinAvg><value>{0}</value></windSpeed10MinAvg>", loopPacket.AvgWindSpeed10Min);
	    sb.AppendFormat("<windSpeed2MinAvg><value>{0}</value></windSpeed2MinAvg>", loop2Packet.WindSpeed2MinuteAvg);

            List<int> pastWindDirsList = pastWindDirs.pastHeadings();
            for (int i = 0; i < pastWindDirsList.Count; i++)
                sb.AppendFormat("<windDir{0}><value>{1}</value></windDir{2}>", i + 2, pastWindDirsList.ElementAt(i), i + 2);

            sb.AppendFormat("<baroPressure><value>{0}</value></baroPressure>", loopPacket.Barometer);
            sb.AppendFormat("<baroTrend>{0}</baroTrend>", loopPacket.BaroTrend);
            sb.AppendFormat("<rainRate><value>{0}</value></rainRate>", loopPacket.RainRate);
            sb.AppendFormat("<rainToday><value>{0}</value></rainToday>", loopPacket.DayRain);
	    sb.AppendFormat("<rain15Minute><value>{0}</value></rain15Minute>", loop2Packet.Rain15Minute);
	    sb.AppendFormat("<rainHour><value>{0}</value></rainHour>", loop2Packet.RainHour);
	    sb.AppendFormat("<rain24Hour><value>{0}</value></rain24Hour>", loop2Packet.Rain24Hour);
	    sb.AppendFormat("<rainMonth><value>{0}</value></rainMonth>", loopPacket.MonthRain);
	    sb.AppendFormat("<rainWeatherYear><value>{0}</value></rainWeatherYear>", loopPacket.YearRain);
            
            if (loopPacket.isSolarRadiationValid())
	        sb.AppendFormat("<solarRadiation><value>{0}</value></solarRadiation>", loopPacket.SolarRadiation);

            if (loopPacket.DayET > 0.0)
                sb.AppendFormat("<dayET><value>{0}</value></dayET>", loopPacket.DayET);

            if (loopPacket.MonthET > 0.0)
                sb.AppendFormat("<monthET><value>{0}</value></monthET>", loopPacket.MonthET);

            if (loopPacket.YearET > 0.0)
                sb.AppendFormat("<yearET><value>{0}</value></yearET>", loopPacket.YearET);

            if (loopPacket.isUvIndexValid())
	        sb.AppendFormat("<uvIndex><index>{0}</index></uvIndex>", loopPacket.UvIndex);

            if (loopPacket.isStormOngoing())
                sb.AppendFormat("<stormStart>{0:O}</stormStart><stormRain><value>{1}</value></stormRain>", loopPacket.StormStart, loopPacket.StormRain);

            sb.Append("<forecastRule>").Append(ForecastRule.forecastString(loopPacket.ForecastRule)).Append("</forecastRule>");
            sb.AppendFormat("<forecast>{0}</forecast>", loopPacket.ForecastIcon);

	    sb.Append("<temperatureSensorEntries>");
            for (int i = 0; i < LoopPacket.NUM_EXTRA_TEMPERATURES; i++) {
                if (loopPacket.isExtraTemperatureValid(i)) {
		    sb.AppendFormat("<entry><key>{0}</key><value><sensorId>{1}</sensorId><sensorType>THERMOMETER</sensorType>", 100 + i, 100 + i);
                    sb.Append("<measurement xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"temperature\">");
		    sb.AppendFormat("<value>{0}</value></measurement></value></entry>", loopPacket.getExtraTemperature(i));
                }
            }
	    sb.Append("</temperatureSensorEntries>");

	    sb.Append("<humiditySensorEntries>");
            for (int i = 0; i < LoopPacket.NUM_EXTRA_HUMIDITIES; i++) {
                if (loopPacket.isExtraHumidityValid(i)) {
		    sb.AppendFormat("<entry><key>{0}</key><value><sensorId>{1}</sensorId><sensorType>HYGROMETER</sensorType>", 200 + i, 200 + i);
                    sb.Append("<measurement xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"humidity\">");
		    sb.AppendFormat("<value>{0}</value></measurement></value></entry>", loopPacket.getExtraHumidity(i));
                }
            }
	    sb.Append("</humiditySensorEntries>");

	    sb.Append("<soilMoistureSensorEntries>");
            for (int i = 0; i < LoopPacket.NUM_SOIL_MOISTURES; i++) {
                if (loopPacket.isSoilMoistureValid(i)) {
		    sb.AppendFormat("<entry><key>{0}</key><value><sensorId>{1}</sensorId><sensorType>SOIL_MOISTURE</sensorType>", 600 + i, 600 + i);
                    sb.Append("<measurement xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"soilMoisture\">");
		    sb.AppendFormat("<value>{0}</value></measurement></value></entry>", loopPacket.getSoilMoisture(i));
                }
            }
	    sb.Append("</soilMoistureSensorEntries>");

	    sb.Append("<leafWetnessSensorEntries>");
            for (int i = 0; i < LoopPacket.NUM_LEAF_WETNESSES; i++) {
                if (loopPacket.isLeafWetnessValid(i)) {
		    sb.AppendFormat("<entry><key>{0}</key><value><sensorId>{1}</sensorId><sensorType>LEAF_WETNESS</sensorType>", 500 + i, 500 + i);
                    sb.Append("<measurement xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"leafWetness\">");
		    sb.AppendFormat("<value>{0}</value></measurement></value></entry>", loopPacket.getLeafWetness(i));
                }
            }
	    sb.Append("</leafWetnessSensorEntries>");

	    sb.AppendFormat("</currentWeather>");
            return sb.ToString();
        }
    }
}
