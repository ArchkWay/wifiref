package com.example.refactoringwnamqos.measurments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.InfoAboutMe;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.intefaces.IWifiScanCallBack;
import com.example.refactoringwnamqos.wifi.WifiItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class WifiScan{

    private ArrayList<String> arrayList = new ArrayList<>();
    IWifiScanCallBack iWifiScanCallBack;
    WifiManager wifiManager;
    private List<ScanResult> results;
    private String tag = "Wifi";

    Timer timerWDT;
    TimerTask timerTaskWDT;

    public void scanWifi(IWifiScanCallBack iWifiScanCallBack, int timeout) {
        this.iWifiScanCallBack = iWifiScanCallBack;
        wifiManager = (WifiManager) InfoAboutMe.context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        arrayList.clear();
        InfoAboutMe.context.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Date date = new Date();
        AllInterface.iLog.addToLog(new LogItem("WifiScan","scanWifi()", String.valueOf(date)));

        timerWDT = new Timer();
        timerTaskWDT = new WifiScan.MyTimerTaskWDT();
        timerWDT.schedule(timerTaskWDT, 1000 * timeout);
    }

    //-------------------------------------------------------------------------------------
    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            StringBuilder wifiNetwork = new StringBuilder();
            List<WifiItem> wifiItems = new ArrayList<>();

            context.unregisterReceiver(this);
            results = wifiManager.getScanResults();

            for (int t = 0; t < results.size(); t++) {

                wifiItems.add(new WifiItem(results.get(t).SSID, results.get(t).BSSID,
                        results.get(t).level, results.get(t).frequency, results.get(t).centerFreq0,
                        results.get(t).centerFreq1, results.get(t).channelWidth,
                        results.get(t).capabilities));

                if(results.get(t).centerFreq0 == 0) wifiItems.get(t).setFrenquecy(
                        results.get(t).frequency);
                String data = wifiItems.get(t).getString();
                wifiNetwork.append(data);
                //String data = results.get(t).toString();
                Date date = new Date();
                AllInterface.iLog.addToLog(new LogItem("WifiScan " + String.valueOf(t), data, String.valueOf(date)));
            }


            Toast.makeText(context, "Найдено - " + results.size() + " сетей", Toast.LENGTH_SHORT).show();
            Log.d(tag, "Найдено - " + results.size() + " сетей");

            timerWDT.cancel();
            timerWDT = null;
            timerTaskWDT = null;
            // Тут можно отправлять по интревейсу данные
            iWifiScanCallBack.wifiScanCallBack(wifiItems,0);
        }
    };

    class MyTimerTaskWDT extends TimerTask {
        @Override
        public void run() {
            Date date = new Date();
//            AllInterface.iLog.addToLog(new LogItem("WifiScan -> MyTimerTaskWDT", "run()", String.valueOf(date)));
//            InfoAboutMe.context.unregisterReceiver(wifiReceiver);
//            iWifiScanCallBack.wifiScanCallBack(null, 1);
        }
    }


}
