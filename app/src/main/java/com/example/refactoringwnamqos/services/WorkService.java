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
//        new Thread(() -> {
//            while (endless) {
                starter();
//                try {
//                    TimeUnit.SECONDS.sleep(120);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
        return START_REDELIVER_INTENT;
    }

    private void starter() {
        isSeviceStart = true;
        Date date = new Date();
        InfoAboutMe.startTime = System.currentTimeMillis();

//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                0, notificationIntent, 0);
//
//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("Exapmple Service")
//                .setSmallIcon(R.drawable.ic_android)
//                .setContentIntent(pendingIntent)
//                .build();
//
//        startForeground(1, notification);

        AllInterface.iLog.addToLog(new LogItem("WorkService", "onStartCommand -> Запуск сервиса", String.valueOf(date)));
        WorkWithLog workWithLog = WorkWithLog.getInstance(getApplicationContext());
        InfoAboutMe.context = getApplicationContext();
//
//        Intent i= new Intent(this, MainActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        this.startActivity(i);

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
