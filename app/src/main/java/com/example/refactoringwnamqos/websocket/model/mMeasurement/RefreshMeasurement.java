package com.example.refactoringwnamqos.websocket.model.mMeasurement;

import android.util.Log;

import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.InfoAboutMe;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.websocket.base.WSClient;
import com.example.refactoringwnamqos.enteties.modelJson.jMeasurement.jRefreshMeasurement.FRefreshMeasurement;
import com.example.refactoringwnamqos.enteties.modelJson.jMeasurement.jRefreshMeasurement.TRefreshMeasurement;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.dto.StompCommand;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;

public class RefreshMeasurement {

    public static final String TAG = "WSClient";
    private WSClient wsClient;
    private Gson mGson = new GsonBuilder().create();

    public RefreshMeasurement(WSClient wsClient){
        this.wsClient = wsClient;
    }

    public void subscribe(){
        // Receive greetings
        Disposable dispTopic = wsClient.mStompClient.topic("/user/queue/measurement/refresh")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Received " + topicMessage.getPayload());
                    Date date = new Date();
                    AllInterface.iLog.addToLog(new LogItem("Подписка обновить измерение","Принято "+topicMessage.getPayload(), String.valueOf(date)));
                    FRefreshMeasurement fRefreshMeasurement = mGson.fromJson(topicMessage.getPayload(), FRefreshMeasurement.class);

                }, throwable -> {
                    Date date = new Date();
                    AllInterface.iLog.addToLog(new LogItem("Подписка обновить измерение","Ошибка подписки", String.valueOf(date)));
                    Log.e(TAG, "Error on subscribe topic", throwable);

                });
        wsClient.compositeDisposable.add(dispTopic);
    }

    public void send() {

        long uptime = (System.currentTimeMillis() - InfoAboutMe.startTime)/1000;

        TRefreshMeasurement tRefreshMeasurement = new TRefreshMeasurement(0);
        String gsonStr = mGson.toJson(tRefreshMeasurement);
        Log.d(TAG, "gson measurement/refresh "+gsonStr);

        List<StompHeader> headers = new ArrayList<>();
        String sid = String.valueOf(System.currentTimeMillis());
        headers.add(new StompHeader("sid", sid));
        headers.add(new StompHeader(StompHeader.DESTINATION, "/app/measurement/refresh"));

        StompMessage stompMessage = new StompMessage(StompCommand.SEND, headers, gsonStr);

        wsClient.compositeDisposable.add(wsClient.mStompClient.send(stompMessage)
                .compose(wsClient.applySchedulers())
                .subscribe(() -> {
                    Log.d(TAG, "STOMP echo send successfully");
                    Date date = new Date();
                    AllInterface.iLog.addToLog(new LogItem("Отправка обновить измерение","Данные отпрвлены удачно", String.valueOf(date)));
                }, throwable -> {
                    Log.d(TAG, "Error send STOMP echo", throwable);
                    Date date = new Date();
                    AllInterface.iLog.addToLog(new LogItem("Отправка обновить измерение","Ошибка отправки", String.valueOf(date)));
                    //toast(throwable.getMessage());
                }));
    }

}
