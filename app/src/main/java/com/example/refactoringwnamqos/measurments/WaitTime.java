package com.example.refactoringwnamqos.measurments;

import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.intefaces.IWaitTime;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class WaitTime {

    private IWaitTime iWaitTime;
    private Timer timer;
    private TimerTask timerTask;

    public WaitTime(IWaitTime iWaitTime) {
        this.iWaitTime = iWaitTime;
    }

    public void start(int seconds){
        timer = new Timer();
        timerTask = new MyTimerTask();
        timer.schedule(timerTask, 1000*seconds);
        Date date = new Date();
        AllInterface.iLog.addToLog(new LogItem("WaitTime","start()", String.valueOf(date)));
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            Date date = new Date();
            AllInterface.iLog.addToLog(new LogItem("WaitTime->MyTimerTask","run()", String.valueOf(date)));
            iWaitTime.waitCallBack();
        }
    }

}
