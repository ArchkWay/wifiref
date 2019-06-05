package com.example.refactoringwnamqos.measurments;


public interface IWifiInterface {

    void enableWifi();
    void disableWifi();
    void disconnectFrom(String ssid);
    WifiInfoObject getWifiInfo();
}
