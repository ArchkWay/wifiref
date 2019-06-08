package com.example.refactoringwnamqos.websocket.model.mTask;

import com.example.refactoringwnamqos.websocket.base.WSClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class Sendlist {

    public static final String TAG = "WSClient";
    private WSClient wsClient;
    private Gson mGson = new GsonBuilder().create();

    public Sendlist(WSClient wsClient){
        this.wsClient = wsClient;
    }

    public void subscribe() {

        // Receive greetings
//        Disposable dispTopic = wsClient.mStompClient.topic("/user/queue/task/sendlist")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(topicMessage -> {
//                    Log.d(TAG, "Received " + topicMessage.getPayload());
//                    FSendlist fSendlist = mGson.fromJson(topicMessage.getPayload(), FSendlist.class);
//                    System.out.println(topicMessage.getPayload());
//                }, throwable -> {
//                    Log.e(TAG, "Error on subscribe topic", throwable);
//                });
//
//        wsClient.compositeDisposable.add(dispTopic);
    }

    public void send() {

        //  ************************
//        ArrayList<TaskData> taskData = new ArrayList<TaskData>();
//        TaskData tData = new TaskData("12346", "bla-bla", null, false);
//        taskData.add(tData);
//        //  ************************
//
//        TSendlist tSendlist = new TSendlist(taskData);
//        String gsonStr = mGson.toJson(tSendlist);
//        Log.d(TAG, "gson "+gsonStr);
//        wsClient.compositeDisposable.add(wsClient.mStompClient.send("/app/task/sendlist", gsonStr)
//                .compose(wsClient.applySchedulers())
//                .subscribe(() -> {
//                    Log.d(TAG, "STOMP echo send successfully");
//                }, throwable -> {
//                    Log.d(TAG, "Error send STOMP echo", throwable);
//                    //toast(throwable.getMessage());
//                }));
    }

}
