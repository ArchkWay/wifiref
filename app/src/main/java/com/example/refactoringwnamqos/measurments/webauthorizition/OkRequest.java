package com.example.refactoringwnamqos.measurments.webauthorizition;

import android.net.Uri;
import android.webkit.CookieSyncManager;

import com.example.refactoringwnamqos.InfoAboutMe;
import com.example.refactoringwnamqos.enteties.StepTwoResponse;
import com.example.refactoringwnamqos.enteties.WebAuthorObj;
import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.intefaces.IWebCallBack1;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkRequest {

    IWebCallBack1 iWebCallBack1;
    OkHttpClient okHttpClient;
    CookieManager cookieManager;

    public OkRequest(IWebCallBack1 iWebCallBack1) {
        this.iWebCallBack1 = iWebCallBack1;
    }

    public void getRequest(String url) {
        cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().cookieJar(new JavaNetCookieJar(cookieManager)).build();

        Date date = new Date();
        AllInterface.iLog.addToLog(new LogItem("OkRequest", "getRequest() url - " + url, String.valueOf(date)));
        Request request = new Request.Builder().url(url).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                iWebCallBack1.callResponseFromServer("OkRequest -> getRequest -> onResponse = Error", null);
                Date date = new Date();
                AllInterface.iLog.addToLog(new LogItem("OkRequest", "onFailure()", String.valueOf(date)));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String okResponse = response.body().string();
                    Date date = new Date();
                    AllInterface.iLog.addToLog(new LogItem("OkRequest", "onResponse = " + okResponse, String.valueOf(date)));
                    iWebCallBack1.callResponseFromServer(okResponse, null);

//                    googleapis
                }
            }
        });
    }

    public void postRequest(WebAuthorObj webAuthorObj) {
        if (cookieManager == null) cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        if (okHttpClient == null)
            okHttpClient = new OkHttpClient.Builder().cookieJar(new JavaNetCookieJar(cookieManager)).build();
        StepTwoResponse stepTwoResponse = webAuthorObj.getStepTwoResponse();
        Request request = null;
        String requestBody;
        MediaType mediaType;
        String url;
        String cookie;
        String smscode;
        switch (webAuthorObj.getStep()) {
            case 1:
                requestBody = "";
                url = "";
                Date date = new Date();
                AllInterface.iLog.addToLog(new LogItem("OkRequest", "postRequest = " + url + " body " + requestBody, String.valueOf(date)));
                mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
                RequestBody body = RequestBody.create(mediaType, requestBody);
                request = new Request.Builder().url(url).post(body).build();
                break;
            case 2:
                url = webAuthorObj.getStepTwoResponse().getEndPoint();
                RequestBody formBody = new FormBody.Builder()
                        .add("dst", stepTwoResponse.getDst())
                        .add("username", stepTwoResponse.getUsername())
                        .add("password", stepTwoResponse.getPassword())
                        .add("mac", stepTwoResponse.getMac())
                        .add("ip", stepTwoResponse.getIp())
                        .add("server-name", stepTwoResponse.getServerName())
                        .add("server-address", stepTwoResponse.getServerAddress()).build();
                request = new Request.Builder().url(url).post(formBody).build();
//                CookieSyncManager.createInstance(InfoAboutMe.context);
//                CookieSyncManager.getInstance().startSync();

                break;
            case 3:
                formBody = new FormBody.Builder().add("phone", webAuthorObj.getTel()).build();
                url = webAuthorObj.getUrl_3() + "sms";
                cookie = "wnam=" + webAuthorObj.getStepThreeResponse().getCookies().get(0).getValue();
                request = new Request.Builder().url(url).post(formBody).header("Cookie", cookie).build();
                break;
            case 4:
                url = webAuthorObj.getUrl_3() + "sms";
                cookie = "wnam=" + webAuthorObj.getStepThreeResponse().getCookies().get(0).getValue();
//                smscode = webAuthorObj.getStepFourResponse().getSmscode().substring(4, 8);
                smscode = InfoAboutMe.SMS.substring(4, 8);//cookie 1d8dc507-1a35-4155-9ebd-8fe4ce29c2d3
                formBody = new FormBody.Builder().add("smscode", smscode).build();
                request = new Request.Builder().url(url).post(formBody).header("Cookie", cookie).build();
                break;
            case 5:
                url = webAuthorObj.getUrl_3() + webAuthorObj.getStepPostFinalResponse().getEndPoint();
                cookie = "wnam=" + webAuthorObj.getStepThreeResponse().getCookies().get(0).getValue();
                requestBody = "";
                mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
                formBody = RequestBody.create(mediaType, requestBody);
//                request = new Request.Builder().url(url).post(body).build();
                request = new Request.Builder().url(url).post(formBody).header("Cookie", cookie).build();
            break;
            case 6:
                url = webAuthorObj.getStepFinalResponse().getUrl();
                formBody = new FormBody.Builder()
                        .add("username", stepTwoResponse.getUsername())
                        .add("password", stepTwoResponse.getPassword())
                        .add("dst","http://bash.im")
                        .build();

                cookie = "wnam=" + webAuthorObj.getStepThreeResponse().getCookies().get(0).getValue();
                request = new Request.Builder().url(url).post(formBody).header("Cookie", cookie).build();

        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Date date = new Date();
                AllInterface.iLog.addToLog(new LogItem("OkRequest", "postRequest -> okHttpClient.newCall -> onFailure()", String.valueOf(date)));
                iWebCallBack1.callResponseFromServer("Error", null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                List <HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
                String okResponse = response.body().string();
                Date date = new Date();
                AllInterface.iLog.addToLog(new LogItem("OkRequest", "postRequest -> okHttpClient.newCall -> onResponse() = " + okResponse, String.valueOf(date)));
                iWebCallBack1.callResponseFromServer(okResponse, cookies);
            }
        });
    }

}