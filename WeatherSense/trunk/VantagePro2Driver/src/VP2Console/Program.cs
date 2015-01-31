using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;

using VantagePro2;

namespace VP2Console {
    class Program {
        static void Main(string[] args) {
            Debug.Listeners.Add(new TextWriterTraceListener(System.Console.Out));
            VantagePro2.VantagePro2Driver driver = new VantagePro2.VantagePro2Driver("COM3", 19200);
            driver.start();
        }
    }
}
