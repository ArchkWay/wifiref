package com.example.refactoringwnamqos.websocket.model.mGetAll;

import android.util.Log;

import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.enteties.jGetAll.FGetAll;
import com.example.refactoringwnamqos.websocket.base.WSClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.dto.StompCommand;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;

public class GetAll {
    public static final String TAG = "WSClient";
    private WSClient wsClient;
    private Gson mGson = new GsonBuilder().create();
    IAllCallback iAllCallback;

    private Timer timerWDT;
    private TimerTask timerTaskWDT;
    private int countWDT=0;

    public GetAll(WSClient wsClient, IAllCallback iAllCallback)
    {
        this.wsClient = wsClient;
        this.iAllCallback = iAllCallback;
    }

    public void subscribe() {

        // Receive greetings
        Disposable dispTopic = wsClient.mStompClient.topic("/user/queue/scheduler/getall")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    timerWDT.cancel();
                    Log.d(TAG, "Received getall" + topicMessage.getPayload());
                    AllInterface.iLog.addToLog(new LogItem("Подписка GetAll","Принято "+topicMessage.getPayload(),null));
                    FGetAll fGetAll = mGson.fromJson(topicMessage.getPayload(), FGetAll.class);
                    iAllCallback.allCallBack(0, fGetAll);
                    System.out.println(topicMessage.getPayload());
                }, throwable -> {
                    AllInterface.iLog.addToLog(new LogItem("Подписка GetAll","Ошибка подписки",null));
                    iAllCallback.allCallBack(1, null);
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });

        wsClient.compositeDisposable.add(dispTopic);
    }

    public void send() {

        String gsonStr = "{\"code\":0}";
        Log.d(TAG, "gson getAll "+gsonStr);

        timerWDT = new Timer();
        timerTaskWDT = new MyTimerTask();
        timerWDT.schedule(timerTaskWDT, 7_000, 7_000);

        List<StompHeader> headers = new ArrayList<>();
        String sid = String.valueOf(System.currentTimeMillis());
        headers.add(new StompHeader("sid", sid));
        headers.add(new StompHeader(StompHeader.DESTINATION, "/app/scheduler/getall"));

        StompMessage stompMessage = new StompMessage(StompCommand.SEND, headers, gsonStr);

        wsClient.compositeDisposable.add(wsClient.mStompClient.send(stompMessage)
                .compose(wsClient.applySchedulers())
                .subscribe(() -> {
                    Log.d(TAG, "STOMP echo send successfully");
                    AllInterface.iLog.addToLog(new LogItem("Отправка GetAll","Данные отпрвлены удачно ",null));
                }, throwable -> {
                    Log.d(TAG, "Error send STOMP echo", throwable);
                    AllInterface.iLog.addToLog(new LogItem("Отправка GetAll","Ошибка отправки",null));
                    //toast(throwable.getMessage());
                }));
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            AllInterface.iLog.addToLog(new LogItem("GetAll -> MyTimerTask","run()",null));
            if(countWDT==3){
                timerWDT.cancel();
            }else{
                countWDT++;
                send();
            }
        }
    }
}
