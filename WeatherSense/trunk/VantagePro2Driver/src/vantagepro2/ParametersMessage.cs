using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Globalization;

namespace VantagePro2 {
    /// <summary>
    /// Message to send weather station parameters to the collector
    /// </summary>
    public class ParametersMessage {
        private readonly static String MANUFACTURER = "Davis Instruments";
        private readonly static String MODEL = "Vantage Pro 2";
        private readonly static String KEY_VALUE = "<entry><key>{0}</key><value>{1}</value></entry>";

        private String firmwareDate;
        public String FirmwareDate {
            get { return firmwareDate; }
            set { firmwareDate = value; }
        }

        private String firmwareVersion;
        public String FirmwareVersion {
            get { return firmwareVersion; }
            set { firmwareVersion = value; }
        }

        private double latitude;
        public double Latitude {
            get { return latitude; }
            set { latitude = value; }
        }

        private double longitude;
        public double Longitude {
            get { return longitude; }
            set { longitude = value; }
        }

        private int elevation;
        public int Elevation {
            get { return elevation; }
            set { elevation = value; }
        }

        private int rainSeasonStart;
        public int RainSeasonStart {
            get { return rainSeasonStart; }
            set { rainSeasonStart = value; }
        }

        private int archivePeriod;
        public int ArchivePeriod {
            get { return archivePeriod; }
            set { archivePeriod = value; }
        }

        private int windCupSize;
        public int WindCupSize {
            get { return windCupSize; }
            set { windCupSize = value; }
        }

        private float rainCollectorSize;
        public float RainCollectorSize {
            get { return rainCollectorSize; }
            set { rainCollectorSize = value; }
        }

 
        /// <summary>
        /// Format the parameters message
        /// </summary>
        /// <returns>The message as a string</returns>
        public String formatMessage() {
            StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            sb.Append("<wsParametersMessage>");
            sb.AppendFormat("<manufacturer>{0}</manufacturer>", MANUFACTURER);
            sb.AppendFormat("<model>{0}</model>", MODEL);
            sb.AppendFormat("<firmwareDate>{0}</firmwareDate>", firmwareDate);
            sb.AppendFormat("<firmwareVersion>{0}</firmwareVersion>", firmwareVersion);
            sb.AppendFormat("<location><latitude><value>{0}</value></latitude>", latitude);
            sb.AppendFormat("<longitude><value>{0}</value></longitude>", longitude);
            sb.AppendFormat("<altitude><value>{0}</value></altitude></location>", UnitConverter.feetToMillimeters(elevation));
            sb.AppendFormat("<weatherYearStartMonth>{0}</weatherYearStartMonth>", RainSeasonStart);
            sb.Append("<parameters>");
            sb.AppendFormat(KEY_VALUE, "Archive Period", archivePeriod);
            sb.AppendFormat(KEY_VALUE, "Wind Cup Size", windCupSize == 0 ? "Small" : "Large");
            sb.AppendFormat(KEY_VALUE, "Rain Collector Size", rainCollectorSize + " inches");
            sb.Append("</parameters>");
            sb.Append("</wsParametersMessage>");

            return sb.ToString();
        }
    }
}
