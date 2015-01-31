using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace VantagePro2_C {
    class HighLowPacket {
        private float barometerDayHigh;
        private float barometerDayLow;
        private float barometerMonthHigh;
        private float barometerMonthLow;
        private float barometerYearHigh;
        private float barometerYearLow;
        private DateTime barometerDayHighTime;
        private DateTime barometerDayLowTime;

        private float windSpeedDayHigh;
        private DateTime windSpeedDayHighTime;
        private float windSpeedMonthHigh;
        private float windSpeedYearHigh;

        private float insideTemperatureDayHigh;
        private float insideTemperatureDayLow;
        private DateTime insideTemperatureDayHighTime;
        private DateTime insideTemperatureDayLowTime;
        private float insideTemperatureMonthHigh;
        private float insideTemperatureMonthLow;
        private float insideTemperatureYearHigh;
        private float insideTemperatureYearLow;

        private float insideHumidityDayHigh;
        private float insideHumidityDayLow;
        private DateTime insideHumidityDayHighTime;
        private DateTime insideHumidityDayLowTime;
        private float insideHumidityMonthHigh;
        private float insideHumidityMonthLow;
        private float insideHumidityYearHigh;
        private float insideHumidityYearLow;

        private float outsideTemperatureDayHigh;
        private float outsideTemperatureDayLow;
        private DateTime outsideTemperatureDayHighTime;
        private DateTime outsideTemperatureDayLowTime;
        private float outsideTemperatureMonthHigh;
        private float outsideTemperatureMonthLow;
        private float outsideTemperatureYearHigh;
        private float outsideTemperatureYearLow;


	    private float outsideHum;
	    private float outsideHumDayHigh;
	    private float outsideHumDayLow;
	    private DateTime  outHumHighTime;
        private DateTime  outHumLowTime;


	    private float outsideDew;
	    private float outsideDewDayHigh;
	    private float outsideDewDayLow;
	    private DateTime  outDewHighTime;
	    private DateTime  outDewLowTime;

	    private float windChill;
	    private float windChillDayLow;
	    private DateTime  outChillLowTime;

	    private float outsideHeat;
	    private float outsideHeatDayHigh;
	    private DateTime  outHeatHighTime;
     


        private float rainRate;
        private float stormRain;
        private DateTime stormStart;
        private float dayRain;
        private float monthRain;
        private float yearRain;

        private int uvIndex;
        private int solarRadiation;

        private float dayET;
        private float monthET;
        private float yearET;

	    private float[] soilMoist = new float[4];
        private int[] leafWetness = new int[4];
	    private float[] leafTemp= new float[4];
        private float[] soilTemp = new float[4];
       
	    private float[] tempExtra = new float[8];
        private float[] humidityExtra = new float[8];

	    private DateTime stationTime;
	    private DateTime sunRiseTime;
	    private DateTime sunSetTime;

        private float wind2MinuteAvg;
        private float windGust10Min;
        private float lastHourRain;
        private float last24HourRain;
        private float barAltimeter;

        public HighLowPacket() {
        }
    }
}
