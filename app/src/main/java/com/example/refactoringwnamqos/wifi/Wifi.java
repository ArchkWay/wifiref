package com.example.refactoringwnamqos.wifi;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.Network;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.InfoAboutMe;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.intefaces.IWifi;
import com.example.refactoringwnamqos.measurments.WifiInfoObject;
import com.example.refactoringwnamqos.intefaces.IWifiConnectCallBack;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Wifi implements IWifi {
    private WifiManager wifiManager;
    Context context;
    private String toSsid;

    public Wifi(Context context) {
        this.context = context;
    }

    IWifiConnectCallBack iWifiConnectCallBack;
    private static final String TAG = "Wifi";

    //------------------------------------------------------------------------------------
    public void init() {
        AllInterface.iWifi = this;
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (hasPermissions()) {
            saveMAC();
        } else {
            sentIntentToStart();
        }
    }

    //------------------------------------------------------------------------------------
    public void saveMAC() {
        String mac;
        enableWifi();
        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mac = getMacAddr();
//        disableWifi();
        InfoAboutMe.WifiMac1 = mac;
    }

    private void sentIntentToStart() {
        Intent intent = new Intent(context, WF_permissions.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    //------------------------------------------------------------------------------------
    public static String getMacAddr() {
        try {
            List <NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                Date date = new Date();
                AllInterface.iLog.addToLog(new LogItem("Определён мой MAC", res1.toString(), String.valueOf(date)));
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        Date date = new Date();
        AllInterface.iLog.addToLog(new LogItem("Ошибка определения MAC адреса", "", String.valueOf(date)));
        return "02:00:00:00:00:00";
    }

    //------------------------------------------------------------------------------------
    private boolean hasPermissions() {
        int res = 0;
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};

        for (String perm : permissions) {
            res = context.checkCallingOrSelfPermission(perm);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    //------------------------------------------------------------------------------------
    @Override
    public void enableWifi() {
        wifiManager.setWifiEnabled(true);
    }

    //------------------------------------------------------------------------------------
    @Override
    public void disableWifi() {
        wifiManager.setWifiEnabled(false);
    }

    //------------------------------------------------------------------------------------

    public void disconnect() {
        wifiManager.disconnect();
    }

    /**
     * ***************************************************************
     */
    public void removeAllNetwork() {
        List <WifiConfiguration> list = wifiManager.getConfiguredNetworks();

        if (list != null) {
            if (list.size() == 0) {
                wifiManager.disconnect();
                return;
            }
        } else return;

        for (int t = 0; t < list.size(); t++) {
            Log.d(TAG, "Remove network " + list.get(t).networkId);
            wifiManager.removeNetwork(list.get(t).networkId);
            wifiManager.saveConfiguration();
        }
//        wifiManager.disconnect();
    }

    //---------------------------------------------------------------------------------
    @Override
    public void disconnectFrom(String ssid) {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + ssid + "\"";

        List <WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                wifiManager.disableNetwork(i.networkId);
                wifiManager.reconnect();
                break;
            }
        }
    }

    private String getAdress(int adr) {
        byte[] bytes = BigInteger.valueOf(adr).toByteArray();
        InetAddress address = null;
        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(bytes).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e("WIFI_IP", "Unable to get host address.");
            ipAddressString = "NaN";
        }
        if (ipAddressString.length() > 10) {
            ipAddressString = reverseIpAdres(ipAddressString);
        }
        return ipAddressString;
    }

    @Override
    public WifiInfoObject getWifiInfo() {
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Network network = conManager.getActiveNetwork();
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        WifiInfoObject wifiInfoObject = new WifiInfoObject();
        wifiInfoObject.setIpaddr(getAdress(dhcpInfo.ipAddress));
        wifiInfoObject.setGateway(getAdress(dhcpInfo.gateway));
        wifiInfoObject.setDHCPserver(getAdress(dhcpInfo.serverAddress));
        wifiInfoObject.setDns1(getAdress(dhcpInfo.dns1));
        wifiInfoObject.setDns2(getAdress(dhcpInfo.dns2));
        wifiInfoObject.setLease(String.valueOf(dhcpInfo.leaseDuration));
        wifiInfoObject.setNetmask(getAdress(dhcpInfo.netmask));
        return wifiInfoObject;
    }

    private String reverseIpAdres(String addr) {
        String address = "";
        String chunkNum = "";
        int firstAmount = 0;
        boolean one = false;
        boolean two = false;
        boolean three = false;
        List <String> chunks = new ArrayList <>();
        List <String> letters = new ArrayList <>();
        for (int i = 0; i < addr.length() - 1; i++) {
            letters.add(addr.substring(i, i + 1));
        }
        letters.add(addr.substring(addr.length() - 1));
        for (int i = 0; i < letters.size(); i++) {
            if (letters.get(i).equals(".") && !one && !two && !three) {
                one = true;
                firstAmount = i;
                for (int j = 0; j < firstAmount; j++) {
                    chunkNum += letters.get(j);
                }
                chunks.add(chunkNum);
                continue;
            }
            if (letters.get(i).equals(".") && one && !two && !three) {
                two = true;
                chunkNum = "";
                for (int j = firstAmount+1; j < i; j++) {
                    chunkNum += letters.get(j);
                }
                firstAmount = i;
                chunks.add(chunkNum);
                continue;
            }
            if (letters.get(i).equals(".") && one && two && !three) {
                three = true;
                chunkNum = "";
                for (int j = firstAmount+1; j < i; j++) {
                    chunkNum += letters.get(j);
                }
                chunks.add(chunkNum);
                chunks.add(addr.substring(i+1));
                for(int w = chunks.size(); w> 0; w--) {
                    address += chunks.get(w-1)+".";
                }
                address = address.substring(0, address.length()-1);
            }
        }
        return address;
    }
}
