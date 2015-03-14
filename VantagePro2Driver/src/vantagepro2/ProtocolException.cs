using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace VantagePro2 {
    public class ProtocolException : Exception {
        public ProtocolException(String message) : base(message) {
        }   
    }
}
