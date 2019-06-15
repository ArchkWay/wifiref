package com.example.refactoringwnamqos.websocket.model.mMeasurement;

import android.util.Log;

import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.InfoAboutMe;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.websocket.base.WSClient;
import com.example.refactoringwnamqos.enteties.modelJson.jMeasurement.jDeleteAllMeasurements.FDeleteAllMeasurements;
import com.example.refactoringwnamqos.enteties.modelJson.jMeasurement.jDeleteAllMeasurements.TDeleteAllMeasurements;
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

public class DeleteAllMeasurements {
    public static final String TAG = "WSClient";
    private WSClient wsClient;
    private Gson mGson = new GsonBuilder().create();

    public DeleteAllMeasurements(WSClient wsClient){
        this.wsClient = wsClient;
    }

    public void subscribe(){
        // Receive greetings
        Date date = new Date();
        Disposable dispTopic = wsClient.mStompClient.topic("/user/queue/measurement/deleteall")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Received " + topicMessage.getPayload());

                    AllInterface.iLog.addToLog(new LogItem("Подписка удалить все измерения","Принято "+topicMessage.getPayload(), String.valueOf(date)));
                    FDeleteAllMeasurements fDeleteAllMeasurements = mGson.fromJson(topicMessage.getPayload(), FDeleteAllMeasurements.class);

                }, throwable -> {
                    AllInterface.iLog.addToLog(new LogItem("Подписка удалить все измерения","Ошибка подписки", String.valueOf(date)));
                    Log.e(TAG, "Error on subscribe topic", throwable);

                });
        wsClient.compositeDisposable.add(dispTopic);
    }

    public void send() {
        Date date = new Date();

        long uptime = (System.currentTimeMillis() - InfoAboutMe.startTime)/1000;

        TDeleteAllMeasurements tDeleteAllMeasurements = new TDeleteAllMeasurements(0);
        String gsonStr = mGson.toJson(tDeleteAllMeasurements);
        Log.d(TAG, "gson measurement/deleteall "+gsonStr);

        List<StompHeader> headers = new ArrayList<>();
        String sid = String.valueOf(System.currentTimeMillis());
        headers.add(new StompHeader("sid", sid));
        headers.add(new StompHeader(StompHeader.DESTINATION, "/app/measurement/deleteall"));

        StompMessage stompMessage = new StompMessage(StompCommand.SEND, headers, gsonStr);

        wsClient.compositeDisposable.add(wsClient.mStompClient.send(stompMessage)
                .compose(wsClient.applySchedulers())
                .subscribe(() -> {
                    Log.d(TAG, "STOMP echo send successfully");
                    AllInterface.iLog.addToLog(new LogItem("Отправка удалить все измерения","Данные отпрвлены удачно", String.valueOf(date)));
                }, throwable -> {
                    Log.d(TAG, "Error send STOMP echo", throwable);
                    AllInterface.iLog.addToLog(new LogItem("Отправка удалить все измерения","Ошибка отправки", String.valueOf(date)));
                    //toast(throwable.getMessage());
                }));
    }
}
