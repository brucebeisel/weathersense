using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Linq;
using System.ServiceProcess;
using System.Text;
using VantagePro2;

namespace VP2Service {
    public partial class VP2Service : ServiceBase {
        private VantagePro2.VantagePro2Driver driver;
        public VP2Service() {
            InitializeComponent();
            driver = new VantagePro2Driver("COM3", 19200);

            if (!System.Diagnostics.EventLog.SourceExists("VP2Source")) {
                System.Diagnostics.EventLog.CreateEventSource(
                    "VP2Source", "VP2Log");
            }
            eventLog1.Source = "VP2Source";
            eventLog1.Log = "VP2Log";
        }

        protected override void OnStart(string[] args) {
            eventLog1.WriteEntry("Vantage Pro2 Service Starting");
            driver.start();
        }

        protected override void OnStop() {
            eventLog1.WriteEntry("Vantage Pro2 Service Stopping");
            driver.stop();
        }
    }
}
