package com.example.refactoringwnamqos.websocket.base;

import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.InfoAboutMe;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.intefaces.ISWClientConnect;
import com.example.refactoringwnamqos.intefaces.IWSClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class WSClient implements ISWClientConnect {

    private static final int SCHEDULER_INTERVAL = 1;
    Date date;
    //private static final String TAG = "WSClient";

    public StompClient mStompClient;
    private Disposable mRestPingDisposable;
    public CompositeDisposable compositeDisposable;
    private IWSClient iwsClient;

    public void init() {
        AllInterface.iswClientConnect = this;
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://qos.wnam.ru/api/v1");
        resetSubscriptions();
    }

    public IWSClient getIwsClient() {
        return iwsClient;
    }

    @Override
    public void setIwsClient(IWSClient iwsClient) {
        this.iwsClient = iwsClient;
    }

    private void resetSubscriptions() {
        Date date = new Date();
        AllInterface.iLog.addToLog(new LogItem("WSClient", "Сброс подписок", String.valueOf(date)));
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void connectStomp() {
        date = new Date();
        AllInterface.iLog.addToLog(new LogItem("WSClient", "connectStomp() Попытка подключения к серверу", String.valueOf(date)));
        List <StompHeader> headers = new ArrayList <>();
        headers.add(new StompHeader("login", InfoAboutMe.UUID));
        headers.add(new StompHeader("sid", InfoAboutMe.UUID));
        mStompClient.withClientHeartbeat(5000).withServerHeartbeat(5000);
        resetSubscriptions();
        mStompClient.connect(headers);
        Disposable dispLifecycle = mStompClient.lifecycle().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    AllInterface.iLog.addToLog(new LogItem("WSClient", "connectStomp() Подключение удалось", String.valueOf(date)));
                    if (iwsClient != null) {
                        iwsClient.iwsClientCallBack(mStompClient, 0);
                    }
                    break;
                case ERROR://InfoAboutMe.stompClient = null;
                    AllInterface.iLog.addToLog(new LogItem("WSClient", "connectStomp() Ошибка подключения", String.valueOf(date)));
//                            if(iwsClient != null)//                                iwsClient.iwsClientCallBack(null, 1);
                    break;
                case CLOSED:
                    InfoAboutMe.stompClient = null;
                    AllInterface.iLog.addToLog(new LogItem("WSClient", "connectStomp() Подключение закрыто", String.valueOf(date)));
                    resetSubscriptions();
                    if (iwsClient != null) iwsClient.iwsClientCallBack(null, 2);
                    break;
                case FAILED_SERVER_HEARTBEAT:
                    //InfoAboutMe.stompClient = null;
                    AllInterface.iLog.addToLog(new LogItem("WSClient", "connectStomp() Ошибка пинга", String.valueOf(date)));
//                            if(iwsClient != null)//                                iwsClient.iwsClientCallBack(null, 3);
                    break;
            }
        });
        compositeDisposable.add(dispLifecycle);
    }

    //---------------------------------------------------------------------------------
    public CompletableTransformer applySchedulers() {
        return upstream -> upstream.unsubscribeOn(Schedulers.newThread()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void shutdown() {
        Date date = new Date();
        AllInterface.iLog.addToLog(new LogItem("WSClient", "shutdown()", String.valueOf(date)));
        mStompClient.disconnect();
        if (mRestPingDisposable != null) mRestPingDisposable.dispose();
        if (compositeDisposable != null) compositeDisposable.dispose();
    }

}
