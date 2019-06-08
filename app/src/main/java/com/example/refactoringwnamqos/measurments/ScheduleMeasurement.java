package com.example.refactoringwnamqos.measurments;

import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.businessLogic.JobToMerge;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.intefaces.IScheduleMeasumerent;
import com.example.refactoringwnamqos.measurments.MeanObject;
import com.example.refactoringwnamqos.measurments.Measurement;

import java.util.Timer;
import java.util.TimerTask;


public class ScheduleMeasurement implements IScheduleMeasumerent {

    Timer timerSchedul;
    TimerTask timerTaskSchedule;
    JobToMerge job;
    Measurement measurement;

    public ScheduleMeasurement(){
        AllInterface.iScheduleMeasumerent = this;
    }

    public void start(JobToMerge job){
        this.job = job;
        measurement = new Measurement(job);

        int time = job.getListTasks().get(0).getmInterval()*60*1_000;

        if(timerSchedul != null){
            timerSchedul.cancel();
            timerSchedul = null;
        }

        timerSchedul = new Timer();
        timerTaskSchedule = new MyTimerTask();
        timerSchedul.schedule(timerTaskSchedule, time, time);

        AllInterface.iLog.addToLog(new LogItem("ScheduleMeasurement","start()", null));
    }

    @Override
    public void stopSchedule() {
        if(timerSchedul != null){
            timerSchedul.cancel();
            timerSchedul = null;
            timerTaskSchedule=null;
        }
        AllInterface.iLog.addToLog(new LogItem("ScheduleMeasurement","stopSchedule()", null));
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            clearMeasurementResuts();
            measurement = new Measurement(job);
            AllInterface.iLog.addToLog(new LogItem("ScheduleMeasurement->MyTimerTask","run()", null));
        }
    }

    private void clearMeasurementResuts(){
        MeanObject meanObject = job.getMeanObjectList().get(0);
        int count = meanObject.getResults().size();
        for(int q=0; q<count; q++){
            meanObject.getResults().get(q).setEnd(null);
            meanObject.getResults().get(q).setBegin(null);
            meanObject.getResults().get(q).setOutput(null);
        }
        AllInterface.iLog.addToLog(new LogItem("ScheduleMeasurement","clearMeasurementResuts()", null));
    }



}
