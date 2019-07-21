package com.example.refactoringwnamqos.measurments.webauthorizition;

import com.example.refactoringwnamqos.MainActivity;
import com.example.refactoringwnamqos.enteties.StepFinalResponse;
import com.example.refactoringwnamqos.enteties.StepFourResponse;
import com.example.refactoringwnamqos.enteties.StepPostFinalResponse;
import com.example.refactoringwnamqos.enteties.StepThreeResponse;
import com.example.refactoringwnamqos.enteties.StepTwoResponse;
import com.example.refactoringwnamqos.enteties.WebAuthorObj;
import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.InfoAboutMe;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.intefaces.IWebAuthorCallBack;
import com.example.refactoringwnamqos.intefaces.IWebCallBack1;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class WebAuthor implements IWebCallBack1 {

    WebAuthorObj webAuthorObj;
    IWebAuthorCallBack iWebAuthorCallBack;
    private int timeout;
    public static String httpName;
    List <String> letters = new ArrayList <>();
    List <String> words = new ArrayList <>();
    Timer timerWDT;
    TimerTask timerTaskWDT;

    public WebAuthor(WebAuthorObj webAuthorObj, IWebAuthorCallBack iWebAuthorCallBack, int timeout) {
        this.webAuthorObj = webAuthorObj;
        this.iWebAuthorCallBack = iWebAuthorCallBack;
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
    public void callResponseFromServer(String data, List <HttpCookie> cookies) {
        switch (webAuthorObj.getStep()) {
            case 1:
                Date date = new Date();
                StepTwoResponse stepTwoResponse = parseResponseFirst(data);//parsing
                AllInterface.iLog.addToLog(new LogItem("WebAuthor", "callResponseFromServer() step=" + webAuthorObj.getStep() + " data=" + data, String.valueOf(date)));
                //Log.d(tag, "WebAuthor->callResponseFromServer step="+webAuthorObj.getStep()+" data="+data);
                if (data.equals("OkRequest -> getRequest -> onResponse = Error")) {
                    timerWDT.cancel();
                    iWebAuthorCallBack.webAuthorCallback(2);
                    return;
                }
                if(stepTwoResponse.getDst() != null) {
                    step2(stepTwoResponse);
                }
                else break;
                break;
            case 2:
                date = new Date();
                AllInterface.iLog.addToLog(new LogItem("WebAuthor", "callResponseFromServer() step=" + webAuthorObj.getStep() + " data=" + data, String.valueOf(date)));
                StepThreeResponse stepThreeResponse = parseTwoResponse(data, cookies);//parsing
                step3PostNumber(stepThreeResponse);
                break;
            case 3:
                date = new Date();
                AllInterface.iLog.addToLog(new LogItem("WebAuthor", "callResponseFromServer() step=" + webAuthorObj.getStep() + " data=" + data, String.valueOf(date)));
                timerWDT.cancel();
                if (data.indexOf("\"code\":0") > -1) iWebAuthorCallBack.webAuthorCallback(0);
                else iWebAuthorCallBack.webAuthorCallback(2);
                StepFourResponse stepFourResponse = new StepFourResponse();
                step4PostSMS(stepFourResponse);
                break;
            case 4:
                date = new Date();
                AllInterface.iLog.addToLog(new LogItem("WebAuthor", "callResponseFromServer() step=" + webAuthorObj.getStep() + " data=" + data, String.valueOf(date)));
                StepPostFinalResponse stepPostFinalResponse = new StepPostFinalResponse();
//                stepPostFinal(stepPostFinalResponse);
                OkRequest okRequest = new OkRequest(this);
                webAuthorObj.setStep(5);
                webAuthorObj.setStepPostFinalResponse(stepPostFinalResponse);
                okRequest.postRequest(webAuthorObj);
                break;
            case 5:
                date = new Date();
                AllInterface.iLog.addToLog(new LogItem("WebAuthor", "callResponseFromServer() step=" + webAuthorObj.getStep() + " data=" + data, String.valueOf(date)));
                StepFinalResponse stepFinalResponse;
                okRequest = new OkRequest(this);
                webAuthorObj.setStep(6);
                stepFinalResponse = parseFinal(data);
                webAuthorObj.setStepFinalResponse(stepFinalResponse);
                okRequest.postRequest(webAuthorObj);
                break;
            case 6:
                iWebAuthorCallBack.webAuthorCallback(6);
                break;
        }


    }

    //----------------------------------------------------------------------------------------

    public void step2(StepTwoResponse stepTwoResponse) {
        OkRequest okRequest = new OkRequest(this);
        webAuthorObj.setStep(2);
        webAuthorObj.setStepTwoResponse(stepTwoResponse);
        okRequest.postRequest(webAuthorObj);
        Date date = new Date();
        AllInterface.iLog.addToLog(new LogItem("WebAuthor", "step2()", String.valueOf(date)));
    }

    public void step3PostNumber(StepThreeResponse stepThreeResponse) {
        OkRequest okRequest = new OkRequest(this);
        webAuthorObj.setStep(3);
        webAuthorObj.setStepThreeResponse(stepThreeResponse);
        okRequest.postRequest(webAuthorObj);
        Date date = new Date();
        AllInterface.iLog.addToLog(new LogItem("WebAuthor", "step2()", String.valueOf(date)));
    }

    public void step4PostSMS(StepFourResponse stepFourResponse) {
        OkRequest okRequest = new OkRequest(this);
        webAuthorObj.setStep(4);
        webAuthorObj.setStepFourResponse(stepFourResponse);
        MainActivity.SmsReceiver smsGetter = new MainActivity.SmsReceiver();
        smsGetter.execute();
        while (!InfoAboutMe.gotSms) {
            webAuthorObj.getStepFourResponse().setSmscode(InfoAboutMe.SMS);
        }
        InfoAboutMe.gotSms = true;
//        InfoAboutMe.context.registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        okRequest.postRequest(webAuthorObj);
        Date date = new Date();
        AllInterface.iLog.addToLog(new LogItem("WebAuthor", "step2()", String.valueOf(date)));
    }


    private StepFinalResponse parseFinal(String input){
        StepFinalResponse stepFinalResponse = new StepFinalResponse();
        parseJSOnWords(input);
        int n = words.size();
        for(String string : words){
            if(string.contains("/login")){
                stepFinalResponse.setUrl(string);
            }
        }

        return stepFinalResponse;
    }

    private StepTwoResponse parseResponseFirst(String input) {
        StepTwoResponse stepTwoResponse = new StepTwoResponse();
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
        parseInputToWords(input);
        for (int i = 1; i < words.size(); i++) {
            switch (words.get(i - 1)) {
                case "wnamlogin":
                    if (!wnamlogin) {
                        httpName = words.get(i);
//                        webAuthorObj.setUrl_2(httpName);
                        stepTwoResponse.setEndPoint(words.get(i));
                        wnamlogin = true;
                    }
                    break;
                case "dst":
                    if (!dst) {
                        stepTwoResponse.setDst(words.get(i));
                        dst = true;
                    }
                    break;
                case "username":
                    if (!username) {
                        stepTwoResponse.setUsername(words.get(i));
                        username = true;
                    }
                    break;
                case "password":
                    if (!password) {
                        stepTwoResponse.setPassword(words.get(i));
                        password = true;
                    }
                    break;
                case "mac":
                    if (!mac) {
                        stepTwoResponse.setMac(words.get(i));
                        mac = true;
                    }
                    break;
                case "ip":
                    if (!ip) {
                        stepTwoResponse.setIp(words.get(i));
                        ip = true;
                    }
                    break;
                case "server-name":
                    if (!server) {
                        stepTwoResponse.setServerName(words.get(i));
                        server = true;
                    }
                    break;
                case "server-address":
                    if (!serverAddres) {
                        stepTwoResponse.setServerAddress(words.get(i));
                        serverAddres = true;
                    }
                    break;
                case "client-id":
                    if (!client) {
                        stepTwoResponse.setClientId(words.get(i));
                        client = true;
                    }
                    break;
                case "site-id":
                    if (!siteId) {
                        stepTwoResponse.setSiteId(Integer.parseInt(words.get(i)));
                        siteId = true;
                    }
                    break;
            }
        }
        return stepTwoResponse;
    }

    private void parseInputToWords(String input) {
        boolean wordStart = false;
        words.clear();
        letters.clear();
        for (int i = 0; i < input.length() - 1; i++) {
            letters.add(input.substring(i, i + 1));
        }
        letters.add(input.substring(input.length() - 1));
        String word = "";
        for (int i = 0; i < letters.size(); i++) {
            if (letters.get(i).equals("\"")) {
                i = i + 1;
                if (!wordStart) {
                    wordStart = true;
                } else {
                    wordStart = false;
                    words.add(word);
                    word = "";
                }
            }
            if (wordStart) {
                word += (letters.get(i));
            }
        }
    }
    private void parseJSOnWords(String input) {
        boolean wordStart = false;
        words.clear();
        letters.clear();
        for (int i = 0; i < input.length() - 1; i++) {
            letters.add(input.substring(i, i + 1));
        }
        letters.add(input.substring(input.length() - 1));
        String word = "";
        for (int i = 0; i < letters.size(); i++) {
            if (letters.get(i).equals("\'")) {
                i = i + 1;
                if (!wordStart) {
                    wordStart = true;
                } else {
                    wordStart = false;
                    words.add(word);
                    word = "";
                }
            }
            if (wordStart) {
                word += (letters.get(i));
            }
        }
    }

    private StepThreeResponse parseTwoResponse(String input, List <HttpCookie> cookies) {
        StepThreeResponse stepThreeResponse = new StepThreeResponse();
        boolean sms = false;
        parseInputToWords(input);
        for (int i = 1; i < words.size(); i++) {
            if (words.get(i - 1).equals("sms") && !sms) {
                stepThreeResponse.setEndPoint(words.get(i));
                sms = true;
            }
        }
        List <HttpCookie> cook = new ArrayList <>();
        for(HttpCookie httpCookie : cookies){
            if(httpCookie.getName().equals("wnam")){
                cook.add(httpCookie);
            }
        }
        stepThreeResponse.setPhone(webAuthorObj.getTel());
        stepThreeResponse.setCookies(cook);
        return stepThreeResponse;
    }

    //-------------------------------------------------------------------------------------


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
