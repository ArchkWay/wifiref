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
        AllInterface.iLog.addToLog(new LogItem("Подключение к сети","Сеть - "+ssid, null));
        this.iWifiConnectCallBack = iWifiConnectCallBack;
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + ssid + "\"";
        toSsid = ssid;

        timerWDT = new Timer();
        timerTaskWDT = new ConnectToSSID.MyTimerTaskWDT();
        timerWDT.schedule(timerTaskWDT, 1000*timeout);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
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

    BroadcastReceiver wifiReceiverCon = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            AllInterface.iLog.addToLog(new LogItem("ConnectToSSID->wifiReceiverCon.",
                    "RX  SUPPLICANT_STATE_CHANGED_ACTION. WifiReceiverCon " + wifiInfo.toString() + " "
                            + "WifiReceiverCon " + toSsid + " " + wifiInfo.getSSID(), null));

            if (!wifiInfo.getSupplicantState().toString().equals("COMPLETED")){
                if(wifiInfo.getSSID().equals("\""+toSsid+"\"")) {
                    try {
                        context.unregisterReceiver(this);
                        AllInterface.iLog.addToLog(new LogItem("ConnectToSSID->wifiReceiverCon.","RX SUPPLICANT_STATE_CHANGED_ACTION -> COMPLETED", null));
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
        AllInterface.iLog.addToLog(new LogItem("ConnectToSSID->addNetwork"," "+ssid, null));
    }

    class MyTimerTaskWDT extends TimerTask {
        @Override
        public void run() {
            InfoAboutMe.context.unregisterReceiver(wifiReceiverCon);
            iWifiConnectCallBack.wifiConnectCallBack(false);
            AllInterface.iLog.addToLog(new LogItem("ConnectToSSID->MyTimerTaskWDT","run()", null));
        }
    }

}
