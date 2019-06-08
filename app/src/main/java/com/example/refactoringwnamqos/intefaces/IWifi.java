package com.example.refactoringwnamqos.intefaces;


import com.example.refactoringwnamqos.measurments.WifiInfoObject;

public interface IWifi {

    void enableWifi();
    void disableWifi();
    void disconnectFrom(String ssid);
    WifiInfoObject getWifiInfo();
}
