package com.example.refactoringwnamqos.measurments.webauthorizition;

import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.intefaces.IWebCallBack1;

import java.io.IOException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkRequest {

    IWebCallBack1 iWebCallBack1;

    public OkRequest(IWebCallBack1 iWebCallBack1) {
        this.iWebCallBack1 = iWebCallBack1;
    }

    public void getRequest(String url) {
        OkHttpClient okHttpClient = new OkHttpClient();

        Date date = new Date();
        AllInterface.iLog.addToLog(new LogItem("OkRequest", "getRequest() url - " + url, String.valueOf(date)));
        Request request = new Request.Builder().url(url).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                iWebCallBack1.callBack("OkRequest -> getRequest -> onResponse = Error");
                Date date = new Date();
                AllInterface.iLog.addToLog(new LogItem("OkRequest", "onFailure()", String.valueOf(date)));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String okResponse = response.body().string();
                    Date date = new Date();
                    AllInterface.iLog.addToLog(new LogItem("OkRequest", "onResponse = " + okResponse, String.valueOf(date)));
                    iWebCallBack1.callBack(okResponse);
                }
            }
        });
    }

    public void postReqest(WebAuthorObj obj) {
        OkHttpClient okHttpClient = new OkHttpClient();
        String requestBody = "";
        String url = "";
        if (obj.getStep() == 2) {
            requestBody = "phone=" + obj.getTel() + "&vualya_agree=-1";
            url = obj.getUrl_2();
        }
        if (obj.getStep() == 3) {
            requestBody = "code=" + obj.getCode() + "&phone=" + obj.getTel() + "&vualya_agree=-1";
            url = obj.getUrl_3();
        }
        Date date = new Date();
        AllInterface.iLog.addToLog(new LogItem("OkRequest", "postReqest = " + url + " body " + requestBody, String.valueOf(date)));

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, requestBody);

        Request request = new Request.Builder().url(url).post(body).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Date date = new Date();
                AllInterface.iLog.addToLog(new LogItem("OkRequest", "postReqest -> okHttpClient.newCall -> onFailure()", String.valueOf(date)));
                iWebCallBack1.callBack("Error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String okResponse = response.body().string();
                Date date = new Date();
                AllInterface.iLog.addToLog(new LogItem("OkRequest", "postReqest -> okHttpClient.newCall -> onResponse() = " + okResponse, String.valueOf(date)));
                iWebCallBack1.callBack(okResponse);
            }
        });
    }
}
