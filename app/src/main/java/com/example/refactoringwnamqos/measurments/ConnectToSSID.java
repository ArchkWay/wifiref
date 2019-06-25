package com.example.refactoringwnamqos.measurments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.InfoAboutMe;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.intefaces.IWifiConnectCallBack;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class ConnectToSSID {

    IWifiConnectCallBack iWifiConnectCallBack;
    WifiManager wifiManager;
    private String toSsid;

    Timer timerWDT;
    TimerTask timerTaskWDT;

    public ConnectToSSID(){
        wifiManager = (WifiManager) InfoAboutMe.context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public void connect(String ssid, IWifiConnectCallBack iWifiConnectCallBack, int timeout) {
        Date date = new Date();
        AllInterface.iLog.addToLog(new LogItem("Подключение к сети","Сеть - "+ssid,  String.valueOf(date)));
        this.iWifiConnectCallBack = iWifiConnectCallBack;
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + ssid + "\"";
        toSsid = ssid;

//        timerWDT = new Timer();
//        timerTaskWDT = new ConnectToSSID.MyTimerTaskWDT();
//        timerWDT.schedule(timerTaskWDT, 1000*timeout);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        try {
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                    InfoAboutMe.context.registerReceiver(wifiReceiverCon, new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();
                    break;
                }
            }
        }
        catch (Exception e){}

    }

    BroadcastReceiver wifiReceiverCon = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            Date date = new Date();
            AllInterface.iLog.addToLog(new LogItem("ConnectToSSID->wifiReceiverCon.",
                    "RX  SUPPLICANT_STATE_CHANGED_ACTION. WifiReceiverCon " + wifiInfo.toString() + " "
                            + "WifiReceiverCon " + toSsid + " " + wifiInfo.getSSID(),  String.valueOf(date)));

            if (!wifiInfo.getSupplicantState().toString().equals("COMPLETED")){
                if(wifiInfo.getSSID().equals("\""+toSsid+"\"")) {
                    try {
                        context.unregisterReceiver(this);
                        date = new Date();
                        AllInterface.iLog.addToLog(new LogItem("ConnectToSSID->wifiReceiverCon.","RX SUPPLICANT_STATE_CHANGED_ACTION -> COMPLETED",  String.valueOf(date)));
                        timerWDT.cancel();
                        timerWDT = null;
                        timerTaskWDT = null;
                        iWifiConnectCallBack.wifiConnectCallBack(true);
                    }
                    catch (Exception e){}


                }
            }
        }
    };

    public void addNetwork(String ssid){
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + ssid + "\"";
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wifiManager.addNetwork(conf);
        Date date = new Date();
        AllInterface.iLog.addToLog(new LogItem("ConnectToSSID->addNetwork"," "+ssid,  String.valueOf(date)));
    }

    class MyTimerTaskWDT extends TimerTask {
        @Override
        public void run() {
            InfoAboutMe.context.unregisterReceiver(wifiReceiverCon);
            iWifiConnectCallBack.wifiConnectCallBack(false);
            Date date = new Date();
            AllInterface.iLog.addToLog(new LogItem("ConnectToSSID->MyTimerTaskWDT","run()",  String.valueOf(date)));
        }
    }

}
