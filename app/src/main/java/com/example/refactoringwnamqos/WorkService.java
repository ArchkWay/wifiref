package com.example.refactoringwnamqos;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.example.refactoringwnamqos.enteties.LogItem;

import static com.example.refactoringwnamqos.App.CHANNEL_ID;


public class WorkService extends Service {

    public static boolean isSeviceStart = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        isSeviceStart = true;

        InfoAboutMe.startTime = System.currentTimeMillis();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Exapmple Service")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        AllInterface.iLog.addToLog(new LogItem("WorkService", "onStartCommand -> Запуск сервиса", null));
        WorkWithLog workWithLog = WorkWithLog.getInstance(getApplicationContext());
        InfoAboutMe.context = getApplicationContext();

        Intent i= new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(i);

//        LoadListSettings loadListSettings = new LoadListSettings();
//        loadListSettings.start(10);
//        RegOnServise.isConnectinAfterMeasumerent = false;
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
//        if(AllInterface.iswClientConnect != null)
//            AllInterface.iswClientConnect.shutdown();
        if(AllInterface.iScheduleMeasumerent !=null)
            AllInterface.iScheduleMeasumerent.stopSchedule();
        AllInterface.iLog.addToLog(new LogItem("WorkService", "onDestroy() Завершение работы сервиса",null));
        isSeviceStart = false;
        super.onDestroy();
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
