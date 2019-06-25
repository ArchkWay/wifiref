package com.example.refactoringwnamqos.measurments.webauthorizition;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.InfoAboutMe;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.intefaces.IWebAuthorCallBack;
import com.example.refactoringwnamqos.intefaces.IWebCallBack1;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class WebAuthor implements IWebCallBack1 {

    WebAuthorObj webAuthorObj;
    IWebAuthorCallBack iWebAuthorCallBack;
    private int timeout;
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
    public void callBack(String data) {
        switch (webAuthorObj.getStep()) {
            case 1:
                Date date = new Date();
                AllInterface.iLog.addToLog(new LogItem("WebAuthor", "callBack() step=" + webAuthorObj.getStep() + " data=" + data, String.valueOf(date)));
                //Log.d(tag, "WebAuthor->callBack step="+webAuthorObj.getStep()+" data="+data);
                if (data.equals("OkRequest -> getRequest -> onResponse = Error")) {
                    timerWDT.cancel();
                    iWebAuthorCallBack.webAuthorCallback(2);
                    return;
                }
                step2();
                break;
            case 2:
                date = new Date();
                AllInterface.iLog.addToLog(new LogItem("WebAuthor", "callBack() step=" + webAuthorObj.getStep() + " data=" + data, String.valueOf(date)));
                break;
            case 3:
                date = new Date();
                AllInterface.iLog.addToLog(new LogItem("WebAuthor", "callBack() step=" + webAuthorObj.getStep() + " data=" + data, String.valueOf(date)));
                timerWDT.cancel();
                if (data.indexOf("\"code\":0") > -1) iWebAuthorCallBack.webAuthorCallback(0);
                else iWebAuthorCallBack.webAuthorCallback(2);
                break;
        }
    }

    //----------------------------------------------------------------------------------------

    public void step2() {
        OkRequest okRequest = new OkRequest(this);
        webAuthorObj.setStep(2);
        InfoAboutMe.context.registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        okRequest.postReqest(webAuthorObj);
        Date date = new Date();
        AllInterface.iLog.addToLog(new LogItem("WebAuthor", "step2()", String.valueOf(date)));
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
