package com.example.refactoringwnamqos.measurments;

import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.businessLogic.JobToMerge;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.intefaces.IScheduleMeasurement;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class ScheduleMeasurement implements IScheduleMeasurement {

    Timer timerSchedul;
    TimerTask timerTaskSchedule;
    JobToMerge job;
    Measurement measurement;

    public ScheduleMeasurement(){
        AllInterface.iScheduleMeasurement = this;
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
        Date date = new Date();
        AllInterface.iLog.addToLog(new LogItem("ScheduleMeasurement","start()", String.valueOf(date)));
    }

    @Override
    public void stopSchedule() {
        if(timerSchedul != null){
            timerSchedul.cancel();
            timerSchedul = null;
            timerTaskSchedule=null;
        }
        Date date = new Date();
        AllInterface.iLog.addToLog(new LogItem("ScheduleMeasurement","stopSchedule()", String.valueOf(date)));
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            clearMeasurementResuts();
            measurement = new Measurement(job);
//            measurement.preparation();
            Date date = new Date();
            AllInterface.iLog.addToLog(new LogItem("ScheduleMeasurement->MyTimerTask","run()", String.valueOf(date)));
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
        Date date = new Date();
        AllInterface.iLog.addToLog(new LogItem("ScheduleMeasurement","clearMeasurementResuts()", String.valueOf(date)));
    }



}
