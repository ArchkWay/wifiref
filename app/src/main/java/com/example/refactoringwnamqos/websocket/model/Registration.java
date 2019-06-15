package com.example.refactoringwnamqos.websocket.model;

import android.util.Log;

import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.InfoAboutMe;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.enteties.jRegister.FRegister;
import com.example.refactoringwnamqos.intefaces.IRegCallBack;
import com.example.refactoringwnamqos.websocket.base.WSClient;
import com.example.refactoringwnamqos.enteties.modelJson.jRegister.TRegister;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.dto.StompCommand;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;

public class Registration {

    public static final String TAG = "WSClient";
    private WSClient wsClient;
    private Gson mGson = new GsonBuilder().create();
    private IRegCallBack iRegCallBack;

    private Timer timerWDT;
    private TimerTask timerTaskWDT;
    private int countWDT = 0;

    public Registration(WSClient wsClient, IRegCallBack iRegCallBack){
        this.wsClient = wsClient;
        this.iRegCallBack = iRegCallBack;
    }

    public void subscribe(){
        // Receive greetings
        Date date = new Date();
        Disposable dispTopic = wsClient.mStompClient.topic("/user/queue/register")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    timerWDT.cancel();

                    Log.d(TAG, "Received register " + topicMessage.getPayload());
                    AllInterface.iLog.addToLog(new LogItem("Подписка регистрации","Принято "+topicMessage.getPayload(),String.valueOf(date)));
                    FRegister fRegister = mGson.fromJson(topicMessage.getPayload(), FRegister.class);
                    InfoAboutMe.phone = fRegister.getData().getPhone();
                    iRegCallBack.regCallBack(0);
                }, throwable -> {
                    AllInterface.iLog.addToLog(new LogItem("Подписка регистрации","Ошибка подписки",String.valueOf(date)));
                    Log.e(TAG, "Error on subscribe topic", throwable);
                    iRegCallBack.regCallBack(1);
                });
        wsClient.compositeDisposable.add(dispTopic);
    }

    public void send() {
        Date date = new Date();
        long uptime = (System.currentTimeMillis() - InfoAboutMe.startTime)/1000;

        timerWDT = new Timer();
        timerTaskWDT = new MyTimerTask();
        timerWDT.schedule(timerTaskWDT, 7000, 7000);

        TRegister tRegister = new TRegister(InfoAboutMe.UUID, InfoAboutMe.WifiMac1, InfoAboutMe.WifiMac2, InfoAboutMe.phone, InfoAboutMe.version, uptime);
        String gsonStr = mGson.toJson(tRegister);
        Log.d(TAG, "gson register "+gsonStr);

        List<StompHeader> headers = new ArrayList<>();
        String sid = String.valueOf(System.currentTimeMillis());
        headers.add(new StompHeader("sid", sid));
        headers.add(new StompHeader(StompHeader.DESTINATION, "/app/register"));

        StompMessage stompMessage = new StompMessage(StompCommand.SEND, headers, gsonStr);

        wsClient.compositeDisposable.add(wsClient.mStompClient.send(stompMessage)
                .compose(wsClient.applySchedulers())
                .subscribe(() -> {
                    Log.d(TAG, "STOMP echo send successfully");

                    AllInterface.iLog.addToLog(new LogItem("Отправка регистрации", gsonStr, String.valueOf(date)));
                }, throwable -> {
                    Log.d(TAG, "Error send STOMP echo", throwable);
                    AllInterface.iLog.addToLog(new LogItem("Отправка регистрации","Ошибка отправки",String.valueOf(date)));
                    //toast(throwable.getMessage());
                }));
    }

    class MyTimerTask extends TimerTask{

        @Override
        public void run() {
            Date date = new Date();
            AllInterface.iLog.addToLog(new LogItem("Registration -> MyTimerTask", "run()", String.valueOf(date)));
            if(countWDT==3){
                timerWDT.cancel();
            }else{
                countWDT++;
                send();
            }
        }
    }

}
