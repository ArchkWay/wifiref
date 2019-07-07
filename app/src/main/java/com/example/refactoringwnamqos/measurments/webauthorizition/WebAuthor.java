package com.example.refactoringwnamqos.measurments.webauthorizition;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.refactoringwnamqos.enteties.StepTwoResponse;
import com.example.refactoringwnamqos.enteties.WebAuthorObj;
import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.InfoAboutMe;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.intefaces.IWebAuthorCallBack;
import com.example.refactoringwnamqos.intefaces.IWebCallBack1;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class WebAuthor implements IWebCallBack1 {

    WebAuthorObj webAuthorObj;
    IWebAuthorCallBack iWebAuthorCallBack;
    private int timeout;
    public static String serverName;
    //private String tag="WebAuthor";

    Timer timerWDT;
    TimerTask timerTaskWDT;

    public WebAuthor(WebAuthorObj webAuthorObj, IWebAuthorCallBack iWebAuthorCallBack, int timeout) {
        this.webAuthorObj = webAuthorObj;
        this.iWebAuthorCallBack = iWebAuthorCallBack;
        this.timeout = timeout;
    }

    public WebAuthor(WebAuthorObj webAuthorObj, int timeout) {
        this.webAuthorObj = webAuthorObj;
        this.timeout = timeout;
    }

    public void step1() {

        timerWDT = new Timer();
        timerTaskWDT = new WebAuthor.MyTimerTaskWDT();
        timerWDT.schedule(timerTaskWDT, 1000 * timeout);
        Date date = new Date();
        AllInterface.iLog.addToLog(new LogItem("WebAuthor", "step1()", String.valueOf(date)));

        OkRequest okRequest = new OkRequest(this);
        webAuthorObj.setStep(1);
        okRequest.getRequest(webAuthorObj.getUrl_1());
    }

    @Override
    public void callResponseFromServer(String data) {
        switch (webAuthorObj.getStep()) {
            case 1:
                Date date = new Date();
                StepTwoResponse stepTwoResponse = new StepTwoResponse();
                stepTwoResponse = parseResponseFirst(data);
                AllInterface.iLog.addToLog(new LogItem("WebAuthor", "callResponseFromServer() step=" + webAuthorObj.getStep() + " data=" + data, String.valueOf(date)));
                //Log.d(tag, "WebAuthor->callResponseFromServer step="+webAuthorObj.getStep()+" data="+data);
                if (data.equals("OkRequest -> getRequest -> onResponse = Error")) {
                    timerWDT.cancel();
                    iWebAuthorCallBack.webAuthorCallback(2);
                    return;
                }
                step2(stepTwoResponse);
                break;
            case 2:
                date = new Date();
                AllInterface.iLog.addToLog(new LogItem("WebAuthor", "callResponseFromServer() step=" + webAuthorObj.getStep() + " data=" + data, String.valueOf(date)));
                break;
            case 3:
                date = new Date();
                AllInterface.iLog.addToLog(new LogItem("WebAuthor", "callResponseFromServer() step=" + webAuthorObj.getStep() + " data=" + data, String.valueOf(date)));
                timerWDT.cancel();
                if (data.indexOf("\"code\":0") > -1) iWebAuthorCallBack.webAuthorCallback(0);
                else iWebAuthorCallBack.webAuthorCallback(2);
                break;
        }
    }

    //----------------------------------------------------------------------------------------

    public void step2(StepTwoResponse stepTwoResponse) {
        OkRequest okRequest = new OkRequest(this);
        webAuthorObj.setStep(2);
        webAuthorObj.setStepTwoResponse(stepTwoResponse);
        InfoAboutMe.context.registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        okRequest.postReqest(webAuthorObj);
        Date date = new Date();
        AllInterface.iLog.addToLog(new LogItem("WebAuthor", "step2()", String.valueOf(date)));
    }

    private StepTwoResponse parseResponseFirst(String input) {
        StepTwoResponse stepTwoResponse = new StepTwoResponse();
        boolean wordStart = false;
       boolean wnamlogin = false;
       boolean dst = false;
       boolean username = false;
       boolean password = false;
       boolean mac = false;
       boolean ip = false;
       boolean server = false;
       boolean serverAddres = false;
       boolean client = false;
       boolean siteId = false;
        List <String> letters = new ArrayList <>();
        List <String> words = new ArrayList <>();
        for (int i = 0; i < input.length() - 1; i++) {
            letters.add(input.substring(i, i + 1));
        }
        letters.add(input.substring(input.length() - 1));
        String word ="";
        for(int i = 0; i < letters.size(); i++){
            if(letters.get(i).equals("\"")){
                i=i+1;
                if(!wordStart) {
                    wordStart = true;
                }
                else {
                    wordStart = false;
                    words.add(word);
                    word = "";
                }
            }
            if(wordStart){
                word += (letters.get(i));
            }
        }
        for(int i = 1; i < words.size(); i++){
            switch (words.get(i - 1)){
                case "wnamlogin":
                    if(!wnamlogin) {
                        serverName = words.get(i);
                    wnamlogin = true;
                    }
                    break;
                case "dst":
                    if(!dst) {
                        stepTwoResponse.setDst(words.get(i));
                    dst = true;
                    }
                    break;
                case "username":
                    if(!username) {
                        stepTwoResponse.setUsername(words.get(i));
                    username = true;
                    }
                    break;
                case "password":
                    if(!password) {
                        stepTwoResponse.setPassword(words.get(i));
                    password = true;
                    }
                    break;
                case "mac":
                    if(!mac) {
                        stepTwoResponse.setMac(words.get(i));
                    mac = true;
                    }
                    break;
                case "ip":
                    if(!ip) {
                        stepTwoResponse.setIp(words.get(i));
                    ip = true;
                    }
                    break;
                case "server-name":
                    if(!server) {
                        stepTwoResponse.setServerName(words.get(i));
                    server = true;
                    }
                    break;
                case "server-address":
                    if(!serverAddres) {
                        stepTwoResponse.setServerAddress(words.get(i));
                    serverAddres = true;
                    }
                    break;
                case "client-id":
                    if(!client) {
                        stepTwoResponse.setClientId(words.get(i));
                    client = true;
                    }
                    break;
                case "site-id":
                    if(!siteId) {
                        stepTwoResponse.setSiteId(Integer.parseInt(words.get(i)));
                    siteId = true;
                    }
                    break;
            }
        }
        return stepTwoResponse;
    }

    BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle pudsBundle = intent.getExtras();
            Object[] pdus = (Object[]) pudsBundle.get("pdus");
            SmsMessage messages = SmsMessage.createFromPdu((byte[]) pdus[0]);
            Date date = new Date();
            AllInterface.iLog.addToLog(new LogItem("WebAuthor", "smsReceiver " + messages.getMessageBody(), String.valueOf(date)));
            //Toast.makeText(context, "SMS prinyato ot :"+messages.getOriginatingAddress()+"\n"+ messages.getMessageBody(), Toast.LENGTH_LONG).show();
            String[] smsItem = messages.getMessageBody().split(" ");
            context.unregisterReceiver(this);
            if (smsItem.length == 3) step3(smsItem[2]);
        }
    };

    //-------------------------------------------------------------------------------------

    private void step3(String s) {
        OkRequest okRequest = new OkRequest(this);
        webAuthorObj.setStep(3);
        webAuthorObj.setCode(s);
        okRequest.postReqest(webAuthorObj);
        Date date = new Date();
        AllInterface.iLog.addToLog(new LogItem("WebAuthor", "step3()", String.valueOf(date)));
    }

    class MyTimerTaskWDT extends TimerTask {
        @Override
        public void run() {
            Date date = new Date();
            AllInterface.iLog.addToLog(new LogItem("WebAuthor->MyTimerTaskWDT", "run()", String.valueOf(date)));
//            InfoAboutMe.context.unregisterReceiver(smsReceiver);
            iWebAuthorCallBack.webAuthorCallback(1);
        }
    }

}
