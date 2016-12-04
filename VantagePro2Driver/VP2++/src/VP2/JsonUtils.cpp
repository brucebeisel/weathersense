/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   JsonUtils.cpp
 * Author: bruce
 * 
 * Created on December 3, 2016, 7:57 PM
 */

#include <ostream>
#include "JsonUtils.h"

using namespace std;

namespace vp2 {

ostream &
JsonUtils::formatSensorMeasurement(ostream & os, bool addComma, int key, const string & sensorType, double measurement) {
    if (addComma)
        os << ",";

    os << "\"" << key << "\":{\"sensorId\":" << key << "\"sensorType\":\"" << sensorType << "\","
               << "\"measurement\":" << measurement << "}";
    return os;
}
}
