package com.example.refactoringwnamqos.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.refactoringwnamqos.InfoAboutMe;
import com.example.refactoringwnamqos.businessLogic.LoadListSettings;
import com.example.refactoringwnamqos.businessLogic.RegOnService;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.enteties.WebAuthorObj;
import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.intefaces.ISendMeasureCallBack;
import com.example.refactoringwnamqos.intefaces.IWebAuthorCallBack;
import com.example.refactoringwnamqos.logs.WorkWithLog;
import com.example.refactoringwnamqos.measurments.ConnectivityHelper;
import com.example.refactoringwnamqos.measurments.ISendMeasurment;
import com.example.refactoringwnamqos.measurments.webauthorizition.WebAuthor;

import java.util.Date;
import java.util.concurrent.TimeUnit;


public class WorkService extends Service implements IWebAuthorCallBack {

    public static boolean isSeviceStart = false;
    boolean endless = true;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
                starter();
        return START_REDELIVER_INTENT;
    }

    private void starter() {
        isSeviceStart = true;
        Date date = new Date();
        InfoAboutMe.startTime = System.currentTimeMillis();

        AllInterface.iLog.addToLog(new LogItem("WorkService", "onStartCommand -> Запуск сервиса", String.valueOf(date)));
        InfoAboutMe.context = getApplicationContext();
        RegOnService.isConnectAfterMeasumerent = false;
        if(!ConnectivityHelper.isConnectedToNetwork(getApplicationContext())) testWebAuth();

        LoadListSettings loadListSettings = new LoadListSettings();
        loadListSettings.start(1);
    }

    private void testWebAuth() {
        WebAuthorObj webAuthorObj = new WebAuthorObj();
        try {
        webAuthorObj.setTel(InfoAboutMe.phone);
        webAuthorObj.setUrl_1("http://www.ru");
        webAuthorObj.setUrl_2("http://wnam-srv1.alel.net/cp/mikrotik");
        webAuthorObj.setUrl_3("http://wnam-srv1.alel.net/cp/");
        WebAuthor webAuthor = new WebAuthor(webAuthorObj, this, 1);
        WifiManager wifiManager = (WifiManager) InfoAboutMe.context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        webAuthor.step1();
        }
        catch (Exception e){}

//        wifiManager.setWifiEnabled(true);
//        wifiManager.enableNetwork(0, true);

    }
    @Override
    public void onDestroy() {
        if (AllInterface.iswClientConnect != null) AllInterface.iswClientConnect.shutdown();
        if (AllInterface.iScheduleMeasurement != null)
            AllInterface.iScheduleMeasurement.stopSchedule();
        Date date = new Date();
        AllInterface.iLog.addToLog(new LogItem("WorkService", "onDestroy() Завершение работы сервиса", String.valueOf(date)));
        isSeviceStart = false;
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void webAuthorCallback(int state) {
        Log.d("Вебавторизация фаза: ", String.valueOf(state));
    }
}
