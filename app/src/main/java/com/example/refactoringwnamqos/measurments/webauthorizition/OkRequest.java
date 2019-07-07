package com.example.refactoringwnamqos.measurments.webauthorizition;

import com.example.refactoringwnamqos.enteties.StepTwoResponse;
import com.example.refactoringwnamqos.enteties.WebAuthorObj;
import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.intefaces.IWebCallBack1;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkRequest {

    IWebCallBack1 iWebCallBack1;
    OkHttpClient okHttpClient;
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
                iWebCallBack1.callResponseFromServer("OkRequest -> getRequest -> onResponse = Error");
                Date date = new Date();
                AllInterface.iLog.addToLog(new LogItem("OkRequest", "onFailure()", String.valueOf(date)));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String okResponse = response.body().string();
                    Date date = new Date();
                    AllInterface.iLog.addToLog(new LogItem("OkRequest", "onResponse = " + okResponse, String.valueOf(date)));
                    iWebCallBack1.callResponseFromServer(okResponse);
                }
            }
        });
    }

    public void postReqest(WebAuthorObj webAuthorObj) {
        if(okHttpClient == null) okHttpClient= new OkHttpClient();
        StepTwoResponse stepTwoResponse = webAuthorObj.getStepTwoResponse();
        Request request = null;
        String requestBody = null;
        MediaType mediaType;
        String url = "";
        switch (webAuthorObj.getStep()){
            case 1:
                requestBody = "";
                url = "";
                Date date = new Date();
                AllInterface.iLog.addToLog(new LogItem("OkRequest", "postReqest = " + url + " body " + requestBody, String.valueOf(date)));
                mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
                RequestBody body = RequestBody.create(mediaType, requestBody);
                request = new Request.Builder().url(url).post(body).build();
            break;
            case 2:
                requestBody = "phone=" + webAuthorObj.getTel() + "&vualya_agree=-1";
                url = webAuthorObj.getStepTwoResponse().getServerAddress();
                HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder();
                        httpBuider.addQueryParameter("dst",stepTwoResponse.getDst());
                        httpBuider.addQueryParameter("username",stepTwoResponse.getUsername());
                        httpBuider.addQueryParameter("password",stepTwoResponse.getPassword());
                        httpBuider.addQueryParameter("mac",stepTwoResponse.getMac());
                        httpBuider.addQueryParameter("ip",stepTwoResponse.getIp());
                        httpBuider.addQueryParameter("server-name",stepTwoResponse.getServerName());
                        httpBuider.addQueryParameter("server-address",stepTwoResponse.getServerAddress());
                mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
                body = RequestBody.create(mediaType, requestBody);
                request = new Request.Builder().url(httpBuider.build()).build();
                break;
            case 3:
                requestBody = "code=" + webAuthorObj.getCode() + "&phone=" + webAuthorObj.getTel() + "&vualya_agree=-1";
                url = webAuthorObj.getUrl_3();
                break;
        }
        
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Date date = new Date();
                AllInterface.iLog.addToLog(new LogItem("OkRequest", "postReqest -> okHttpClient.newCall -> onFailure()", String.valueOf(date)));
                iWebCallBack1.callResponseFromServer("Error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String okResponse = response.body().string();
                Date date = new Date();
                AllInterface.iLog.addToLog(new LogItem("OkRequest", "postReqest -> okHttpClient.newCall -> onResponse() = " + okResponse, String.valueOf(date)));
                iWebCallBack1.callResponseFromServer(okResponse);
            }
        });
    }
}
