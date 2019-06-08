package com.example.refactoringwnamqos.enteties.modelJson.jRegister;

public class TRegister {

    private String uuid;
    private String mac1;
    private String mac2;
    private String phone;
    private String version;
    long uptime;

    public TRegister(String uuid, String mac1, String mac2, String phone, String version, long uptime) {
        this.uuid = uuid;
        this.mac1 = mac1;
        this.mac2 = mac2;
        this.phone = phone;
        this.version = version;
        this.uptime = uptime;
    }

    public TRegister(){}

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMac1() {
        return mac1;
    }

    public void setMac1(String mac1) {
        this.mac1 = mac1;
    }

    public String getMac2() {
        return mac2;
    }

    public void setMac2(String mac2) {
        this.mac2 = mac2;
    }

    public String getЗhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getUptime() {
        return uptime;
    }

    public void setUptime(long uptime) {
        this.uptime = uptime;
    }
}
