package com.example.refactoringwnamqos.enteties;

import com.google.gson.annotations.SerializedName;

import java.net.HttpCookie;
import java.util.List;

import retrofit2.http.POST;

public class StepTwoResponse {


    String dst;
    String username;
    String password;
    String mac;
    String ip;
    String endPoint;
    @SerializedName("server-name")
    String serverName;

    @SerializedName("server-address")
    String serverAddress;

    @SerializedName("client-id")
    String clientId;

    @SerializedName("site-id")
    int siteId;

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }




    public String getDst() {
        return dst;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }
    List <HttpCookie> cookies;

    public List <HttpCookie> getCookies() {
        return cookies;
    }

    public void setCookies(List <HttpCookie> cookies) {
        this.cookies = cookies;
    }
}
