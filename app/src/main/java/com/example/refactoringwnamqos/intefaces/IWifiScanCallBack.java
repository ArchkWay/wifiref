package com.example.refactoringwnamqos.intefaces;

import com.example.refactoringwnamqos.wifi.WifiItem;

import java.util.List;


public interface IWifiScanCallBack {
    void wifiScanCallBack(List <WifiItem> wifiItems, int code);
}
