package com.example.refactoringwnamqos.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.refactoringwnamqos.InfoAboutMe;
import com.example.refactoringwnamqos.businessLogic.LoadListSettings;
import com.example.refactoringwnamqos.businessLogic.RegOnService;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.intefaces.ISendMeasureCallBack;
import com.example.refactoringwnamqos.logs.WorkWithLog;
import com.example.refactoringwnamqos.measurments.ISendMeasurment;

import java.util.Date;
import java.util.concurrent.TimeUnit;


public class WorkService extends Service  {

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

        LoadListSettings loadListSettings = new LoadListSettings();
        loadListSettings.start(1);
        RegOnService.isConnectAfterMeasumerent = false;
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


}
