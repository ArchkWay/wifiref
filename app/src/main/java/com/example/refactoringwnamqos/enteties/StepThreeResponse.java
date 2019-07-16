package com.example.refactoringwnamqos.enteties;

import java.net.HttpCookie;
import java.util.List;

public class StepThreeResponse {
    String phone;
    String endPoint;

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    List <HttpCookie> cookies;

    public List <HttpCookie> getCookies() {
        return cookies;
    }

    public void setCookies(List <HttpCookie> cookies) {
        this.cookies = cookies;
    }
}
