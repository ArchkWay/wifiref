package com.example.refactoringwnamqos.businessLogic;

import java.util.Timer;
import java.util.TimerTask;

import ew8s.com.wnamqos.AllInterface;
import ew8s.com.wnamqos.InfoAboutMe;
import ew8s.com.wnamqos.log.LogItem;
import ew8s.com.wnamqos.measurement.schedule.ScheduleMeasurement;
import ew8s.com.wnamqos.phone.PhoneState;
import ew8s.com.wnamqos.service.WorkService;
import ew8s.com.wnamqos.websocket.base.IWSClient;
import ew8s.com.wnamqos.websocket.base.WSClient;
import ew8s.com.wnamqos.wifi.Wifi;
import ua.naiksoftware.stomp.StompClient;

public class LoadListSettings implements IWSClient, ILoadTaskCompleted {

    private WSClient wsClient;
    private Wifi wifi;
    PhoneState mMyInfo;
    FormationMeasurement  formationMeasurement;
    Timer timer;
    TimerTask timerTask;
    Timer conTimer;
    TimerTask conTimerTask;
    RegOnServise regOnServise;
    ScheduleMeasurement scheduleMeasurement;

    public static boolean isLoad = false;

    //-------------------------------------------------------
    public void start(int secondsWait){
        startWifi();
        timer = new Timer();
        timerTask = new LoadListSettings.MyTimerTask();
        timer.schedule(timerTask, 1000*secondsWait);

        AllInterface.iLog.addToLog(new LogItem("Запуск системы", "LoadListSettings->start="+secondsWait, null));
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            if(AllInterface.iswClientConnect != null) AllInterface.iswClientConnect.shutdown();
            if(!WorkService.isSeviceStart) return;
            AllInterface.iLog.addToLog(new LogItem("Запуск системы", "LoadListSettings->MyTimerTask", null));
            startWork();
        }
    }
    //------------------------------------------------------
    /**
     * This callback works if loaded all schedules and tasks.
     * This final method in class
     * @param job
     */
    @Override
    public void jobCallback(JobToMerge job) {

        if(RegOnServise.isConnectinAfterMeasumerent == false) {
            formationMeasurement.create(job);
            scheduleMeasurement = new ScheduleMeasurement();
            scheduleMeasurement.start(job);
            RegOnServise.isConnectinAfterMeasumerent = true;
        } else{
           AllInterface.iReconnectStomp.success();
        }
    }

    //---------------------------------------------------------
    @Override
    public void iwsClientCallBack(StompClient mStompClient, int code) {
        if(mStompClient != null){
            AllInterface.iLog.addToLog(new LogItem("Запуск системы", "LoadListSettings->iwsClientCallBack. Connection was succeed", null));
            //connection was succeed

            if(conTimer != null){
                conTimer.cancel();
                conTimer=null;
                conTimerTask = null;
            }

            formationMeasurement = new FormationMeasurement();
            regOnServise = new RegOnServise(wsClient, this);
            regOnServise.start();
        }else{
            //connection was failed
            AllInterface.iLog.addToLog(new LogItem("Запуск системы", "LoadListSettings->iwsClientCallBack. Connection was failed", null));
            if(conTimer == null) {
                wsClient.shutdown();
                if(timer == null) {
                    conTimer = new Timer();
                    conTimerTask = new LoadListSettings.ConTimerTask();
                    conTimer.schedule(timerTask, 600_000);  //every 10 minutes
                }
                AllInterface.iLog.addToLog(new LogItem("Запуск системы", "LoadListSettings->iwsClientCallBack. Try after 10 minutes", null));
            }
        }
    }

    class ConTimerTask extends TimerTask {
        @Override
        public void run() {
            if(WorkService.isSeviceStart == false) return;
            AllInterface.iLog.addToLog(new LogItem("Запуск системы", "LoadListSettings->ConTimerTask. Try connection", null));
            conTimer.cancel();
            conTimer = null;
            startWSClient();
        }
    }

    //--------------------------------------------------------

    private void startWork(){
        AllInterface.iLog.addToLog(new LogItem("Запуск системы", "LoadListSettings -> startWork", null));
        startPhone();
        startWSClient();
    }

    private void startPhone() {
        mMyInfo = new PhoneState(InfoAboutMe.context);
        mMyInfo.init();
    }

    private void startWifi() {
        wifi = new Wifi(InfoAboutMe.context);
        wifi.init();
        wifi.enableWifi();
        wifi.removeAllNetwork();
        wifi.disableWifi();
    }

    private void startWSClient() {
        wsClient = new WSClient();
        wsClient.init();
        wsClient.setIwsClient(this);
        wsClient.connectStomp();
    }

}
