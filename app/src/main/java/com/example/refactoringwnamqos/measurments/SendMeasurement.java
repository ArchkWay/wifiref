package com.example.refactoringwnamqos.measurments;

import android.util.Log;

import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.businessLogic.RegOnServise;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.intefaces.ISendMeasurement;
import com.example.refactoringwnamqos.enteties.modelJson.jMeasurement.jSendMeasurement.FSendMeasurement;
import com.example.refactoringwnamqos.websocket.base.WSClient;
import com.example.refactoringwnamqos.intefaces.ILoadMeasurCallback;
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

public class SendMeasurement implements ISendMeasurement {
    public static final String TAG = "WSClient";
    private WSClient wsClient;
    private Gson mGson = new GsonBuilder().create();

    public SendMeasurement(WSClient wsClient){
        this.wsClient = wsClient;
        AllInterface.iSendMeasurement = this;

    }

    public void subscribe(ILoadMeasurCallback iLoadMeasurCallback){
        // Receive greetings
        Disposable dispTopic = wsClient.mStompClient.topic("/user/queue/measurement/send")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Received " + topicMessage.getPayload());
                    Date date = new Date();
                    AllInterface.iLog.addToLog(new LogItem("Удачно","Ответ от сервера при отправке сообщения",String.valueOf(date)));
                    FSendMeasurement fSendMeasurement = mGson.fromJson(topicMessage.getPayload(), FSendMeasurement.class);

                    //iLoadMeasurCallback.loadMeasurCallBack(fSendMeasurement);
                }, throwable -> {
                    Date date = new Date();
                    AllInterface.iLog.addToLog(new LogItem("Ошибка","Отправка измерения",String.valueOf(date)));
                    Log.e(TAG, "Error on subscribe topic", throwable);

                });
        wsClient.compositeDisposable.add(dispTopic);
    }

    @Override
    public void sendMeanObject(MeanObject meanObject) {

        String gsonStr = mGson.toJson(meanObject);
        Log.d(TAG, "gson measurement/send "+gsonStr);

        List<StompHeader> headers = new ArrayList<>();
        String sid = String.valueOf(System.currentTimeMillis());
        headers.add(new StompHeader("sid", sid));
        headers.add(new StompHeader(StompHeader.DESTINATION, "/app/measurement/send"));

        StompMessage stompMessage = new StompMessage(StompCommand.SEND, headers, gsonStr);

        wsClient.compositeDisposable.add(wsClient.mStompClient.send(stompMessage)
                .compose(wsClient.applySchedulers())
                .subscribe(() -> {
                    Log.d(TAG, "STOMP echo send successfully");
                    Date date = new Date();
                    AllInterface.iLog.addToLog(new LogItem("Удачно","Измерение отправлено удачно "+gsonStr,String.valueOf(date)));
                    RegOnServise.isConnectinAfterMeasumerent=false;
                }, throwable -> {
                    Log.d(TAG, "Error send STOMP echo", throwable);
                    Date date = new Date();
                    AllInterface.iLog.addToLog(new LogItem("Ошибка","Измерение отправлено неудачно",String.valueOf(date)));
                    //toast(throwable.getMessage());
                }));
    }
}
