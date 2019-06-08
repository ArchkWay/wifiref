package com.example.refactoringwnamqos.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.logs.WorkWithLog;


public class StartBroadReceiv extends BroadcastReceiver {

    final String LOG_TAG = "MyBroadReceiv";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "onReceive " + intent.getAction());
        //InfoAboutMe.context = context;
        WorkWithLog workWithLog = WorkWithLog.getInstance(context);
        WorkWithLog.startTime = System.currentTimeMillis();
        workWithLog.addToLog(new LogItem("Получен бродкаст",
                "BC или QP", String.valueOf(WorkWithLog.startTime)));
        if(!WorkService.isSeviceStart)
            context.startService(new Intent(context, WorkService.class));
    }
}
