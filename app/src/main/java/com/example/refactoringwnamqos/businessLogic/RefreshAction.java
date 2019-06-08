package com.example.refactoringwnamqos.businessLogic;

import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.intefaces.IRefreshCallBack;

import java.util.Timer;
import java.util.TimerTask;


public class RefreshAction implements IRefreshCallBack {
    @Override
    public void refCallBack(int state) {
        if(AllInterface.iScheduleMeasumerent != null)
            AllInterface.iScheduleMeasumerent.stopSchedule();

        if(AllInterface.iswClientConnect != null)
            AllInterface.iswClientConnect.shutdown();

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                AllInterface.iswClientConnect.connectStomp();
            }
        };
        timer.schedule(timerTask, 3_000);
    }
}