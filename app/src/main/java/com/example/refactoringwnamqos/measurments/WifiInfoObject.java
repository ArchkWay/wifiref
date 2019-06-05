package com.example.refactoringwnamqos.measurments;

public class WifiInfoObject {

    private String ipaddr;
    private String gateway;
    private String netmask;
    private String dns1;
    private String dns2;
    private String lease;
    private String DHCPserver;

    public WifiInfoObject(String ipaddr, String gateway, String netmask, String dns1, String dns2, String lease, String DHCPserver) {
        this.ipaddr = ipaddr;
        this.gateway = gateway;
        this.netmask = netmask;
        this.dns1 = dns1;
        this.dns2 = dns2;
        this.lease = lease;
        this.DHCPserver = DHCPserver;
    }

    public WifiInfoObject(){}

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getDns1() {
        return dns1;
    }

    public void setDns1(String dns1) {
        this.dns1 = dns1;
    }

    public String getDns2() {
        return dns2;
    }

    public void setDns2(String dns2) {
        this.dns2 = dns2;
    }

    public String getLease() {
        return lease;
    }

    public void setLease(String lease) {
        this.lease = lease;
    }

    public String getDHCPserver() {
        return DHCPserver;
    }

    public void setDHCPserver(String DHCPserver) {
        this.DHCPserver = DHCPserver;
    }
}
