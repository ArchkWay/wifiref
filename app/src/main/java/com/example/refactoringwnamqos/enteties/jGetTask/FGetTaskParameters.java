package com.example.refactoringwnamqos.enteties.jGetTask;

public class FGetTaskParameters {
    private String time;
    private String band;
    private String url_2;
    private String exept;
    private String cna;
    private String tel;
    private String sha256sum;
    private String ssid;
    private String url_1;
    private String url_3;

    public FGetTaskParameters(String time, String band, String url_2, String exept, String cna, String sha256sum, String ssid) {
        this.time = time;
        this.band = band;
        this.url_2 = url_2;
        this.exept = exept;
        this.cna = cna;
        this.sha256sum = sha256sum;
        this.ssid = ssid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public String getUrl_2() {
        return url_2;
    }

    public void setUrl_2(String url_2) {
        this.url_2 = url_2;
    }

    public String getExept() {
        return exept;
    }

    public void setExept(String exept) {
        this.exept = exept;
    }

    public String getCna() {
        return cna;
    }

    public void setCna(String cna) {
        this.cna = cna;
    }

    public String getSha256sum() {
        return sha256sum;
    }

    public void setSha256sum(String sha256sum) {
        this.sha256sum = sha256sum;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getUrl_1() {
        return url_1;
    }

    public void setUrl_1(String url_1) {
        this.url_1 = url_1;
    }

    public String getUrl_3() {
        return url_3;
    }

    public void setUrl_3(String url_3) {
        this.url_3 = url_3;
    }
}
