package com.example.refactoringwnamqos.websocket.model;

import android.util.Log;

import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.InfoAboutMe;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.intefaces.IRefreshCallBack;
import com.example.refactoringwnamqos.websocket.base.WSClient;
import com.example.refactoringwnamqos.enteties.modelJson.jRegister.TRegister;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.dto.StompCommand;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;

public class Refresh {
    public static final String TAG = "WSClient";
    private WSClient wsClient;
    private Gson mGson = new GsonBuilder().create();

    public Refresh(WSClient wsClient){
        this.wsClient = wsClient;
    }

    public void subscribe(IRefreshCallBack iRefreshCallBack){


        // Receive greetings
        Disposable dispTopic = wsClient.mStompClient.topic("/user/queue/refresh")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    String receive = topicMessage.getPayload();
                    AllInterface.iLog.addToLog(new LogItem("Refresh -> subscribe","Принято "+receive,null));
                    iRefreshCallBack.refCallBack(0);
                }, throwable -> {
                    AllInterface.iLog.addToLog(new LogItem("Refresh -> subscribe","Ошибка подписки",null));
                    Log.e(TAG, "Error on subscribe topic", throwable);
                    iRefreshCallBack.refCallBack(1);
                });
        wsClient.compositeDisposable.add(dispTopic);
    }

    public void send() {

        long uptime = (System.currentTimeMillis() - InfoAboutMe.startTime)/1000;

        TRegister tRegister = new TRegister(InfoAboutMe.UUID, InfoAboutMe.WifiMac1, InfoAboutMe.WifiMac2, InfoAboutMe.phone, InfoAboutMe.version, uptime);
        String gsonStr = mGson.toJson(tRegister);
        Log.d(TAG, "gson register "+gsonStr);

        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("sid", InfoAboutMe.UUID));
        headers.add(new StompHeader(StompHeader.DESTINATION, "/app/refresh"));

        StompMessage stompMessage = new StompMessage(StompCommand.SEND, headers, gsonStr);

        wsClient.compositeDisposable.add(wsClient.mStompClient.send(stompMessage)
                .compose(wsClient.applySchedulers())
                .subscribe(() -> {
                    Log.d(TAG, "STOMP echo send successfully");
                    AllInterface.iLog.addToLog(new LogItem("Отправка на обновление","Данные отпрвлены удачно",null));
                }, throwable -> {
                    Log.d(TAG, "Error send STOMP echo", throwable);
                    AllInterface.iLog.addToLog(new LogItem("Отправка на обновление","Ошибка отправки",null));
                    //toast(throwable.getMessage());
                }));
    }
}
