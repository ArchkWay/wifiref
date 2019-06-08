package com.example.refactoringwnamqos.websocket.model.mTask;

import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.enteties.jGetTask.FGetTask;
import com.example.refactoringwnamqos.enteties.jGetTask.TGetTask;
import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.intefaces.IGetTaskCallBack;
import com.example.refactoringwnamqos.websocket.base.WSClient;
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

public class GetTask {

    //public static final String TAG = "WSClient";
    private WSClient wsClient;
    private Gson mGson = new GsonBuilder().create();
    private IGetTaskCallBack iGetTaskCallBack;

    public GetTask(WSClient wsClient, IGetTaskCallBack iGetTaskCallBack){
        this.wsClient = wsClient;
        this.iGetTaskCallBack = iGetTaskCallBack;
    }

    public void subscribe() {

        // Receive greetings
        Disposable dispTopic = wsClient.mStompClient.topic("/user/queue/task/get")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    FGetTask fGetTask = mGson.fromJson(topicMessage.getPayload(), FGetTask.class);
                    iGetTaskCallBack.taskCallBack(0,fGetTask);
                    AllInterface.iLog.addToLog(new LogItem("GetTask -> subscribe()", topicMessage.getPayload(), null));
                }, throwable -> {
                    AllInterface.iLog.addToLog(new LogItem("GetTask -> subscribe()", "Error on subscribe topic", null));
                    iGetTaskCallBack.taskCallBack(1, null);
                });

        wsClient.compositeDisposable.add(dispTopic);
    }

    public void send(String taskId) {
        TGetTask tGetTask = new TGetTask(taskId);
        String gsonStr = mGson.toJson(tGetTask);

        List<StompHeader> headers = new ArrayList<>();
        String sid = String.valueOf(System.currentTimeMillis());
        headers.add(new StompHeader("sid", sid));
        headers.add(new StompHeader(StompHeader.DESTINATION, "/app/task/get"));

        StompMessage stompMessage = new StompMessage(StompCommand.SEND, headers, gsonStr);

        wsClient.compositeDisposable.add(wsClient.mStompClient.send(stompMessage)
                .compose(wsClient.applySchedulers())
                .subscribe(() -> {
                    AllInterface.iLog.addToLog(new LogItem("GetTask -> send()", gsonStr, null));
                }, throwable -> {
                    AllInterface.iLog.addToLog(new LogItem("GetTask -> send()", "Error send STOMP echo", null));
                    //toast(throwable.getMessage());
                }));
    }

}
