package com.example.refactoringwnamqos.websocket.model.mMeasurement;

import android.util.Log;

import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.InfoAboutMe;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.intefaces.ILoadMeasurCallback;
import com.example.refactoringwnamqos.websocket.base.WSClient;
import com.example.refactoringwnamqos.enteties.modelJson.jMeasurement.jLoadAllMeasurement.FLoadAllMeasurement;
import com.example.refactoringwnamqos.enteties.modelJson.jMeasurement.jLoadAllMeasurement.TLoadAllMeasurements;
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

public class LoadAllMeasurements {

    public static final String TAG = "WSClient";
    private WSClient wsClient;
    private Gson mGson = new GsonBuilder().create();

    public LoadAllMeasurements(WSClient wsClient){
        this.wsClient = wsClient;
    }

    public void subscribe(ILoadMeasurCallback iLoadMeasurCallback){
        // Receive greetings
        Disposable dispTopic = wsClient.mStompClient.topic("/user/queue/measurement/getlist")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Received " + topicMessage.getPayload());
                    AllInterface.iLog.addToLog(new LogItem("Подписка загрузить все измерения","Принято "+topicMessage.getPayload(),null));
                    FLoadAllMeasurement fLoadAllMeasurement = mGson.fromJson(topicMessage.getPayload(), FLoadAllMeasurement.class);
                    iLoadMeasurCallback.loadMeasurCallBack(fLoadAllMeasurement);
                }, throwable -> {
                    AllInterface.iLog.addToLog(new LogItem("Подписка загрузить все измерения","Ошибка подписки",null));
                    Log.e(TAG, "Error on subscribe topic", throwable);

                });
        wsClient.compositeDisposable.add(dispTopic);
    }

    public void send() {

        long uptime = (System.currentTimeMillis() - InfoAboutMe.startTime)/1000;

        TLoadAllMeasurements tLoadAllMeasurements = new TLoadAllMeasurements(0);
        String gsonStr = mGson.toJson(tLoadAllMeasurements);
        Log.d(TAG, "gson measurement/getlist "+gsonStr);

        List<StompHeader> headers = new ArrayList<>();
        String sid = String.valueOf(System.currentTimeMillis());
        headers.add(new StompHeader("sid", sid));
        headers.add(new StompHeader(StompHeader.DESTINATION, "/app/measurement/getlist"));

        StompMessage stompMessage = new StompMessage(StompCommand.SEND, headers, gsonStr);

        wsClient.compositeDisposable.add(wsClient.mStompClient.send(stompMessage)
                .compose(wsClient.applySchedulers())
                .subscribe(() -> {
                    Log.d(TAG, "STOMP echo send successfully");
                    AllInterface.iLog.addToLog(new LogItem("Отправка загрузить все измерения","Данные отпрвлены удачно",null));
                }, throwable -> {
                    Log.d(TAG, "Error send STOMP echo", throwable);
                    AllInterface.iLog.addToLog(new LogItem("Отправка загрузить все измерения","Ошибка отправки",null));
                    //toast(throwable.getMessage());
                }));
    }

}
