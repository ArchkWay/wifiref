package com.example.refactoringwnamqos.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.refactoringwnamqos.InfoAboutMe;

import java.util.List;


public class WifiConnectAp {

    private WifiManager wifiManager;
    private WifiConfiguration wifiConfiguration;
    private ConnectivityManager cm;
    private static final String Tag = "Wifi";

    final static String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    public WifiConnectAp(){
        cm = (ConnectivityManager) InfoAboutMe.context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(checkForInternet()){
            Log.d(Tag, "isConnected - true");
            //loadData();
        }else{
            Log.d(Tag, "isConnected - false");
            //updateUI();
        }
    }

    public List<WifiConfiguration> getConfigWifi() {
        wifiManager = (WifiManager) InfoAboutMe.context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        Log.d(Tag, "getConfigWifi - "+dhcpInfo.toString());
        return list;
    }

    public NetworkInfo getCurrentConnection(){
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        Log.d(Tag, "getCurrentConnection - "+activeNetwork.toString());
        return activeNetwork;
    }

    public void sss(){

        InfoAboutMe.context.registerReceiver(wifiReceiver1, new IntentFilter(CONNECTIVITY_ACTION));

    }

    // Self explanatory method
    public boolean checkForInternet() {

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    BroadcastReceiver wifiReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String actionOfIntent = intent.getAction();
            boolean isConnected = checkForInternet();
            if(actionOfIntent.equals(CONNECTIVITY_ACTION)){
                if(isConnected){
                    Log.d(Tag, "onReceive isConnected - true");
                    //loadData();
                }else{
                    Log.d(Tag, "onReceive isConnected - false");
                    //updateUI();
                }
            }
        }
    };

}
