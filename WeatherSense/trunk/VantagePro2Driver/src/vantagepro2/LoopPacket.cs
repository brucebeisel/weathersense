using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;

namespace VantagePro2 {
    public class LoopPacket {
        public enum BaroTrendEnum {
            FALLING_RAPIDLY = 196,
            FALLING_SLOWLY = 236,
            STEADY = 0,
            RISING_SLOWLY = 20,
            RISING_RAPIDLY = 60
        }
        public enum Forecast {
            SUNNY = 8,
            PARTLY_CLOUDY = 6,
            MOSTLY_CLOUDY = 2,
            MOSTLY_CLOUDY_WITH_RAIN = 3,
            MOSTLY_CLOUDY_WITH_SNOW = 18,
            MOSTLY_CLOUDY_WITH_RAIN_OR_SNOW = 19,
            PARTLY_CLOUDY_WITH_RAIN_LATER = 7,
            PARTLY_CLOUDY_WITH_SNOW_LATER = 22,
            PARTLY_CLOUDY_WITH_RAIN_OR_SNOW_LATER = 23
        }

        private static readonly int TEMPERATURE_OFFSET = 90;
        public static readonly int NUM_EXTRA_TEMPERATURES = 7;
        public static readonly int NUM_EXTRA_HUMIDITIES = 7;
        public static readonly int NUM_SOIL_TEMPERATURES = 4;
        public static readonly int NUM_LEAF_TEMPERATURES = 3;
        public static readonly int NUM_SOIL_MOISTURES = 4;
        public static readonly int NUM_LEAF_WETNESSES = 3;
        private static readonly float TEMPERATURE_SCALE = 10.0f;
        private static readonly int INVALID_EXTRA_TEMPERATURE = 255;
        private static readonly int INVALID_EXTRA_HUMIDITY = 255;
        private static readonly int INVALID_UV_INDEX = 255;
        private static readonly int INVALID_LEAF_WETNESS = 255;
        private static readonly int INVALID_SOIL_MOISTURE = 255;
        private static readonly int INVALID_SOLAR_RADIATION = 32767;


        private int nextRecord;
        public int NextRecord {
            get { return nextRecord; }
            set { nextRecord = value; }
        }

        private BaroTrendEnum baroTrend;
        public BaroTrendEnum BaroTrend {
            get { return baroTrend; }
        }

	private float outsideTemp;
        public float OutsideTemp {
            get { return outsideTemp; }
        }
        
	private float insideTemp;
        public float InsideTemp {
            get { return insideTemp; }
        }

	private int outsideHum;
        public int OutsideHumidity {
            get { return outsideHum; }
        }

        private int insideHum;
        public int InsideHumidity {
            get { return insideHum; }
        }

        private float windSpeed;
        public float WindSpeed {
            get { return windSpeed; }
        }
        private float avgWindSpeed10Min;
        public float AvgWindSpeed10Min {
            get { return avgWindSpeed10Min; }
        }

        private int windDirection;
        public int WindDirection {
            get { return windDirection; }
        }

        private float barometer;
        public float Barometer {
            get { return barometer; }
        }

        private float rainRate;
        public float RainRate {
            get { return rainRate; }
        }

        private float stormRain;
        public float StormRain {
            get { return stormRain; }
        }

        private DateTime stormStart;
        public DateTime StormStart {
            get { return stormStart; }
        }

        private float dayRain;
        public float DayRain {
            get { return dayRain; }
        }

        private float monthRain;
        public float MonthRain {
            get { return monthRain; }
        }

        private float yearRain;
        public float YearRain {
            get { return yearRain; }
        }

        private int uvIndex;
        public int UvIndex {
            get { return uvIndex; }
            set { uvIndex = value; }
        }
        public bool isUvIndexValid() {
            return uvIndex != INVALID_UV_INDEX;
        }

        private int solarRadiation;
        public int SolarRadiation {
            get { return solarRadiation; }
            set { solarRadiation = value; }
        }

        public bool isSolarRadiationValid() {
            return solarRadiation != INVALID_SOLAR_RADIATION;
        }

        private float dayET;
        public float DayET {
            get { return dayET; }
            set { dayET = value; }
        }

        private float monthET;
        public float MonthET {
            get { return monthET; }
            set { monthET = value; }
        }

        private float yearET;
        public float YearET {
            get { return yearET; }
            set { yearET = value; }
        }

	private int[] soilMoist = new int[NUM_SOIL_MOISTURES];
        private int[] leafWetness = new int[NUM_LEAF_WETNESSES];
	private float[] leafTemp = new float[NUM_LEAF_TEMPERATURES];
        private bool[] leafTempValid = new bool[NUM_LEAF_TEMPERATURES];
        private float[] soilTemp = new float[NUM_SOIL_TEMPERATURES];
        private bool[] soilTempValid = new bool[NUM_SOIL_TEMPERATURES];

        private float[] tempExtra = new float[NUM_EXTRA_TEMPERATURES];
        private bool[] tempExtraValid = new bool[NUM_EXTRA_TEMPERATURES];
       
        private int[] humidityExtra = new int[NUM_EXTRA_HUMIDITIES];

        private int transmitterBatteryStatus;

        private float consoleBatteryVoltage;
        public float ConsoleBatteryVoltage {
            get { return consoleBatteryVoltage; }
        }

        private Forecast forecastIcon;
        public Forecast ForecastIcon {
            get { return forecastIcon; }
        }

        private int forecastRule;
        public int ForecastRule {
            get { return forecastRule; }
        }

	private DateTime sunRiseTime;
	private DateTime sunSetTime;

        public LoopPacket() {
        }

        public bool isTransmitterBatteryGood(int index) {
            return (transmitterBatteryStatus & (1 << (index - 1))) == 0;
        }

        public bool isExtraTemperatureValid(int index) {
            return tempExtraValid[index];
        }

        public float getExtraTemperature(int index) {
            return tempExtra[index];
        }

        public bool isExtraHumidityValid(int index) {
            return humidityExtra[index] != INVALID_EXTRA_HUMIDITY;
        }

        public int getExtraHumidity(int index) {
            return humidityExtra[index];
        }

        public int getLeafWetness(int index) {
            return leafWetness[index];
        }

        public bool isLeafWetnessValid(int index) {
            return leafWetness[index] != INVALID_LEAF_WETNESS;
        }

        public int getSoilMoisture(int index) {
            return soilMoist[index];
        }

        public bool isSoilMoistureValid(int index) {
            return soilMoist[index] != INVALID_SOIL_MOISTURE;
        }

        public float getSoilTemperature(int index) {
            return soilTemp[index];
        }

        public bool isSoilTemperatureValid(int index) {
            return soilTempValid[index];
        }

        public float getLeafTemperature(int index) {
            return leafTemp[index];
        }

        public bool isLeafTemperatureValid(int index) {
            return leafTempValid[index];
        }

        //
        // A storm is occuring if the storm start year is not 1. In C# a DateTime is initialized to
        // 1/1/1. So if the storm start time in the LOOP packet is -1, then the DateTime will be the
        // default value. In addition, the last couple of LOOP packets that have a valid storm start will
        // report a storm rain total of 0.0 inches. This may be an indicator that the storm has stopped,
        // but we are not using that at this point in time. By definition the storm rain has to be > 0, so
        // we will stop reporting an ongoing storm if the storm rain is 0.0
        //
        public bool isStormOngoing() {
            return stormStart.Year != 1 && stormRain > 0.0;
        }

        public bool parseLoopPacket(byte[] buffer, float rainInterval) {
            if ((char)buffer[0] != 'L' || (char)buffer[1] != 'O' || (char)buffer[2] != 'O')
                return false;

            if ((char)buffer[3] != 'P') {
                switch (buffer[3]) {
                    case 196:
                        baroTrend = BaroTrendEnum.FALLING_RAPIDLY;
                        break;
                    case 236:
                        baroTrend = BaroTrendEnum.FALLING_SLOWLY;
                        break;
                    case 0:
                        baroTrend = BaroTrendEnum.STEADY;
                        break;
                    case 20:
                        baroTrend = BaroTrendEnum.RISING_SLOWLY;
                        break;
                    case 60:
                        baroTrend = BaroTrendEnum.RISING_RAPIDLY;
                        break;
                    default:
                        return false;
                }
            }

            int packetType = (int)buffer[4];

            if (packetType != 0) {
                Debug.Print("Invalid packet type for LOOP packet: {0}", packetType);
                return false;
            }

            nextRecord = BitConverter.ToInt16(buffer,5);
            barometer = UnitConverter.toInchesHg((float)BitConverter.ToInt16(buffer, 7) / 1000.0F);
            insideTemp = UnitConverter.toCelcius((float)BitConverter.ToInt16(buffer, 9) / TEMPERATURE_SCALE);
            insideHum = buffer[11];
            outsideTemp = UnitConverter.toCelcius(BitConverter.ToInt16(buffer, 12) / TEMPERATURE_SCALE);
            windSpeed = UnitConverter.toMetersPerSecond((float)buffer[14]);
            avgWindSpeed10Min = UnitConverter.toMetersPerSecond((float)buffer[15]);
            windDirection = BitConverter.ToInt16(buffer, 16);
            //
            // Zero degrees from the weather station means no wind, translate 360 to 0 for north
            //
            if (windDirection == 360)
                windDirection = 0;

            for (int i = 0; i < NUM_EXTRA_TEMPERATURES; i++) {
                int temperature = buffer[18 + i];
                tempExtraValid[i] = temperature != INVALID_EXTRA_TEMPERATURE;
                tempExtra[i] = UnitConverter.toCelcius(temperature - TEMPERATURE_OFFSET);
            }

            for (int i = 0; i < NUM_SOIL_TEMPERATURES; i++) {
                int temperature = buffer[25 + i];
                soilTempValid[i] = temperature != INVALID_EXTRA_TEMPERATURE;
                soilTemp[i] = UnitConverter.toCelcius((float)temperature - TEMPERATURE_OFFSET);
            }

            for (int i = 0; i < NUM_LEAF_TEMPERATURES; i++) {
                int temperature = buffer[29 + i];
                leafTempValid[i] = temperature != INVALID_EXTRA_TEMPERATURE;
                leafTemp[i] = UnitConverter.toCelcius(temperature - TEMPERATURE_OFFSET);
            }

            outsideHum = buffer[33];

            for (int i = 0; i < NUM_EXTRA_HUMIDITIES; i++) {
                humidityExtra[i] = buffer[34 + i];
            }

            rainRate = UnitConverter.toMillimeter((float)BitConverter.ToInt16(buffer, 41) / 100.0F);

            uvIndex = buffer[43];
            solarRadiation = BitConverter.ToInt16(buffer, 44);

            stormRain = UnitConverter.toMillimeter((float)BitConverter.ToInt16(buffer, 46) / 100.0F);

            short time = BitConverter.ToInt16(buffer, 48);

            if (time != -1)
                stormStart = extractDate(time);

            dayRain = UnitConverter.toMillimeter((float)BitConverter.ToInt16(buffer, 50) * rainInterval);
            monthRain = UnitConverter.toMillimeter((float)BitConverter.ToInt16(buffer, 52) * rainInterval);
            yearRain = UnitConverter.toMillimeter((float)BitConverter.ToInt16(buffer, 54) * rainInterval);

            int idayET = BitConverter.ToInt16(buffer, 56);
            int imonthET = BitConverter.ToInt16(buffer, 58);
            int iyearET = BitConverter.ToInt16(buffer, 60);
            Debug.Print("ET: Day={0}, Month={1}, Year={2}", idayET, imonthET, iyearET);
            dayET = UnitConverter.toMillimeter((float)BitConverter.ToInt16(buffer, 56) / 1000F);
            monthET = UnitConverter.toMillimeter((float)BitConverter.ToInt16(buffer, 58) / 100F);
            yearET = UnitConverter.toMillimeter((float)BitConverter.ToInt16(buffer, 60) / 100F);

            for (int i = 0; i < NUM_SOIL_MOISTURES; i++)
                soilMoist[i] = buffer[62 + i];

            for (int i = 0; i < NUM_LEAF_WETNESSES; i++)
                leafWetness[i] = buffer[66 + i];

            transmitterBatteryStatus = (int)buffer[86];
            Debug.Print("Transmitter Battery Status: {0}", transmitterBatteryStatus);
            consoleBatteryVoltage = (((int)BitConverter.ToInt16(buffer, 87) * 300) / 512) / 100.0F;
            forecastIcon = (Forecast)buffer[89];
            forecastRule = buffer[90];

            DateTime today = DateTime.Today;
            int sunrise = BitConverter.ToInt16(buffer, 91);
            int sunset = BitConverter.ToInt16(buffer, 93);

            sunRiseTime = new DateTime(today.Year, today.Month, today.Day, sunrise / 100, sunrise % 100, 0);
            sunSetTime = new DateTime(today.Year, today.Month, today.Day, sunset / 100, sunset % 100, 0);
        
            if ((char)buffer[95] != CommandStrings.LINE_FEED || (char)buffer[96] != CommandStrings.CARRIAGE_RETURN) {
                Console.WriteLine("<LF><CR> not found");
                return false;
            }

            //Console.WriteLine(this.ToString());

            return VantagePro2Station.checkCRC(buffer, 97);
        }

        public static DateTime extractDate(short time) {
            int year = (time & 0x3F) + 2000;
            int day = (time >> 7) & 0x1F;
            int month = (time >> 12) & 0xF;

            return new DateTime(year, month, day);
        }

        public override String ToString() {
            StringBuilder sb = new StringBuilder();
            sb.AppendFormat("Next Record: {0}\n", nextRecord);
            sb.AppendFormat("Barometer: {0}  Trend: {1}\n", barometer, baroTrend);
            sb.AppendFormat("Outdoor Temperature: {0}  Outdoor Humidity: {1}\n", outsideTemp, outsideHum);
            sb.AppendFormat("Indoor Temperature: {0}  Indoor Humidity: {1}\n", insideTemp, insideHum);
            sb.AppendFormat("Extra Temperatures:");
            for (int i = 0; i < NUM_EXTRA_TEMPERATURES; i++)
                sb.AppendFormat(" [{0}]: {1}", i, tempExtra[i]);
            sb.Append("\n");
            sb.AppendFormat("Extra Humidities:");
            for (int i = 0; i < NUM_EXTRA_HUMIDITIES; i++)
                sb.AppendFormat(" [{0}]: {1}", i, humidityExtra[i]);
            sb.Append("\n");
            sb.AppendFormat("Wind: {0} MPH from {1}  10 Minute Avg Speed: {2}\n", windSpeed, windDirection, avgWindSpeed10Min);
            sb.AppendFormat("Rain: Rate: {0}  Storm: {1}  Day: {2}  Month: {3}  Year: {4}\n", rainRate, stormRain, dayRain, monthRain, yearRain);
            sb.AppendFormat("Leaf Wetnesses:");
            for (int i = 0; i < NUM_LEAF_WETNESSES; i++)
                sb.AppendFormat(" [{0}]: {1}", i, leafWetness[i]);
            sb.Append("\n");
            sb.AppendFormat("Soil Moistures:");
            for (int i = 0; i < NUM_SOIL_MOISTURES; i++)
                sb.AppendFormat(" [{0}]: {1}", i, soilMoist[i]);
            sb.Append("\n");
            sb.AppendFormat("Soil Temperatures:");
            for (int i = 0; i < NUM_SOIL_TEMPERATURES; i++)
                sb.AppendFormat(" [{0}]: {1}", i, soilTemp[i]);
            sb.Append("\n");
            sb.AppendFormat("Leaf Temperatures:");
            for (int i = 0; i < NUM_LEAF_TEMPERATURES; i++)
                sb.AppendFormat("  [{0}]: {1}", i, leafTemp[i]);
            sb.Append("\n");
            sb.AppendFormat("Transmitter Battery Status: 0x{0:x}\n", transmitterBatteryStatus);
            sb.AppendFormat("Console Battery Voltage: {0}\n", consoleBatteryVoltage);
            sb.AppendFormat("Forecast: {0}\n", forecastIcon);
            sb.AppendFormat("Forecast Rule: {0}\n", forecastRule);
            sb.AppendFormat("Sunrise: {0}  Sunset: {1}", sunRiseTime, sunSetTime);
            sb.Append("=========================\n");

            return sb.ToString();
        }
    }
}
