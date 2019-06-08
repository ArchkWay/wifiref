package com.example.refactoringwnamqos.wifi;

public class WifiItem {
    private String SSID;
    private String BSSID;
    private int RSSI;
    private int frenquecy;
    private int centerFreq0;
    private int centerFreq1;
    private int channelBandwidth;
    private String capabilities;

    public WifiItem(String SSID, String BSSID, int RSSI, int frenquecy, int centerFreq0, int centerFreq1, int channelBandwidth, String capabilities) {
        this.SSID = SSID;
        this.BSSID = BSSID;
        this.RSSI = RSSI;
        this.frenquecy = frenquecy;
        this.centerFreq0 = centerFreq0;
        this.centerFreq1 = centerFreq1;
        this.channelBandwidth = channelBandwidth;
        this.capabilities = capabilities;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public int getRSSI() {
        return RSSI;
    }

    public void setRSSI(int RSSI) {
        this.RSSI = RSSI;
    }

    public int getFrenquecy() {
        return frenquecy;
    }

    public void setFrenquecy(int frenquecy) {
        this.frenquecy = frenquecy;
    }

    public int getCenterFreq0() {
        return centerFreq0;
    }

    public void setCenterFreq0(int centerFreq0) {
        this.centerFreq0 = centerFreq0;
    }

    public int getCenterFreq1() {
        return centerFreq1;
    }

    public void setCenterFreq1(int centerFreq1) {
        this.centerFreq1 = centerFreq1;
    }

    public int getChannelBandwidth() {
        return channelBandwidth;
    }

    public void setChannelBandwidth(int channelBandwidth) {
        channelBandwidth = channelBandwidth;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public String getString(){
        String str = "SSID:"+SSID+"\nBSSID:"+BSSID+"\nRSSI:"+String.valueOf(RSSI)+"\nfrenquecy:"+
            String.valueOf(frenquecy)+"\ncenterFreq0:"+String.valueOf(centerFreq0)+"\ncenterFreq1:"+
            String.valueOf(centerFreq1)+"\nchannelBandwidth:"+String.valueOf(channelBandwidth)+
            "\ncapabilities:"+capabilities+"\n";

        return str;
    }
}
