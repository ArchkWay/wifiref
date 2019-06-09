package com.example.refactoringwnamqos.measurments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.intefaces.IReconnectStomp;
import com.example.refactoringwnamqos.businessLogic.JobToMerge;
import com.example.refactoringwnamqos.businessLogic.RegOnServise;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.enteties.jGetTask.FGetTaskCommands;
import com.example.refactoringwnamqos.enteties.jGetTask.FGetTaskData;
import com.example.refactoringwnamqos.intefaces.IWifiConnectCallBack;
import com.example.refactoringwnamqos.intefaces.IDownloader;
import com.example.refactoringwnamqos.enteties.modelJson.jMeasurement.jSendMeasurement.TCOMMAN_X_ID;
import com.example.refactoringwnamqos.intefaces.IWaitTime;
import com.example.refactoringwnamqos.intefaces.IWebAuthorCallBack;
import com.example.refactoringwnamqos.measurments.webauthorizition.WebAuthor;
import com.example.refactoringwnamqos.measurments.webauthorizition.WebAuthorObj;
import com.example.refactoringwnamqos.intefaces.IWifiScanCallBack;
import com.example.refactoringwnamqos.wifi.WifiItem;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class Measurement implements IWifiScanCallBack, IWifiConnectCallBack, IWaitTime, IWebAuthorCallBack, IReconnectStomp, IDownloader {

    private JobToMerge mJob;
    private FGetTaskData mTask;
    private MeanObject mMeanObject;
    private List<TCOMMAN_X_ID> tcomman_x_ids;
    private int mCountCommands;
    private List<FGetTaskCommands> mCommands;
    private int mCurrentConutCommands = 0;
    private Timer timer;
    private TimerTask timerTask;
    private boolean connectWifi;

    private static final String TAG = "Measurement";

    public Measurement(JobToMerge job) {
        mJob = job;
        AllInterface.iswClientConnect.shutdown();
        AllInterface.iReconnectStomp = this;
        preparation();
    }

    public boolean isConnectWifi() {
        return connectWifi;
    }

    public void setConnectWifi(boolean connectWifi) {
        this.connectWifi = connectWifi;
    }

    public void preparation(){

        RegOnServise.isConnectinAfterMeasumerent = true;

        for(int q=0; q<mJob.getListTasks().size(); q++){
           mTask = mJob.getListTasks().get(q);
           mMeanObject = mJob.getMeanObjectList().get(q);
        }

        tcomman_x_ids = mMeanObject.getResults();
        mCountCommands = countCommand(mTask);
        mCommands = getCommands(mTask);

        mMeanObject.setBegin(getTime());
        mMeanObject.setId(getTime());

        start();
    }

    private int countCommand(FGetTaskData task){
        return task.getCommands().size();
    }

    public List<FGetTaskCommands> getCommands(FGetTaskData task) {
        return task.getCommands();
    }

    private void start(){

        if(mCurrentConutCommands < mCountCommands){
            String type = findMetodMeasurement(mCommands.get(mCurrentConutCommands));
            switch (type){
                case "SCAN_WIFI":
                    AllInterface.iLog.addToLog(new LogItem("Измерения " + mCurrentConutCommands,"Сканирование сетей", null));
                    scanWifi2();
                    break;
                case "ASSOC_SSID":
                    AllInterface.iLog.addToLog(new LogItem("Измерения " + mCurrentConutCommands,"Подлючение к сети", null));
                    assoc_ssid();
                    break;
                case "GET_IPPARAMS":
                    AllInterface.iLog.addToLog(new LogItem("Измерения " + mCurrentConutCommands,"Зарос сетевых параметров", null));
                    get_ipparams();
                    break;
                case "WAIT":
                    AllInterface.iLog.addToLog(new LogItem("Измерения " + mCurrentConutCommands,"Ожидание", null));
                    waitTime();
                    break;
                case "WEBAUTH":
                    AllInterface.iLog.addToLog(new LogItem("Измерения " + mCurrentConutCommands,"Вебавторизвция", null));
                    webAuth();
                    break;
                case "TEST_INTERNET":
                    AllInterface.iLog.addToLog(new LogItem("Измерения " + mCurrentConutCommands,"Тестирование интеренета", null));
                    testInternet();
                    break;
                case "GET_FILE":
                    AllInterface.iLog.addToLog(new LogItem("Измерения "+mCurrentConutCommands,"Скачивание файла", null));
                    downloadFile();
                    break;
                default:
                    AllInterface.iLog.addToLog(new LogItem("Измерения " + mCurrentConutCommands,"Команда не поддерживается", null));
                    mCurrentConutCommands++;
                    start();
            }
        } else{
            mMeanObject.setEnd(getTime());
            AllInterface.iLog.addToLog(new LogItem("Измерения " + mCurrentConutCommands,"Измерение завершено", null));
            AllInterface.iWifi.disableWifi();

            if(timer != null){
                timer.cancel();
                timer = null;
            }

            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    AllInterface.iLog.addToLog(new LogItem("Measurement -> TimerTask","run()", null));
                    AllInterface.iswClientConnect.connectStomp();
                    timer.cancel();
                }
            };
            timer.schedule(timerTask, 5_000);
            AllInterface.iLog.addToLog(new LogItem("Measurement -> Timer","Старт таймера", null));

        }
    }

    //------------------------------------------------------------------------------------
    private void testInternet() {
        TCOMMAN_X_ID tcommanXId = mMeanObject.getResults().get(mCurrentConutCommands);
        tcommanXId.setBegin(getTime());
        int timeout = mCommands.get(mCurrentConutCommands).getTimeout();
//        setConnectWifi(true);//testing
        if(connectWifi){
            TestInternet testInternet = new TestInternet();
            boolean test = testInternet.executeCommand();
            if(test) {
                tcommanXId.setOutput("OK");
            }
            else tcommanXId.setOutput("ERROR");

        } else{
            tcommanXId.setOutput("ERROR");
        }
        tcommanXId.setEnd(getTime());
        mCurrentConutCommands++;
        start();
    }

    //------------------------------------------------------------------------------------
    private void webAuth() {

        FGetTaskCommands command = mCommands.get(mCurrentConutCommands);
        TCOMMAN_X_ID tcommanXId = mMeanObject.getResults().get(mCurrentConutCommands);
        tcommanXId.setBegin(getTime());
        String tel = command.getParameters().getTel();
        String url_1 = command.getParameters().getUrl_1();
        String url_2 = command.getParameters().getUrl_2();
        String url_3 = command.getParameters().getUrl_3();

        int timeout = mCommands.get(mCurrentConutCommands).getTimeout();

        WebAuthorObj webAuthorObj = new WebAuthorObj();
        webAuthorObj.setTel(tel);
        webAuthorObj.setUrl_1(url_1);
        webAuthorObj.setUrl_2(url_2);
        webAuthorObj.setUrl_3(url_3);

        WebAuthor webAuthor = new WebAuthor(webAuthorObj, this, timeout);
        webAuthor.step1();
    }


    @Override
    public void webAuthorCallback(int state){
        Log.d(TAG, "WEBAUTH - получен коллбек "+state);
        TCOMMAN_X_ID tcommanXId = mMeanObject.getResults().get(mCurrentConutCommands);
        if(state==0)tcommanXId.setOutput("OK");
        if(state==1)tcommanXId.setOutput("TIMEOUT");
        if(state==2)tcommanXId.setOutput("ERROR");
        tcommanXId.setEnd(getTime());
        mCurrentConutCommands++;
        start();
    }

    //------------------------------------------------------------------------------------
    private void waitTime() {

        WaitTime waitTime = new WaitTime(this);
        FGetTaskCommands command = mCommands.get(mCurrentConutCommands);
        TCOMMAN_X_ID tcommanXId = mMeanObject.getResults().get(mCurrentConutCommands);
        tcommanXId.setBegin(getTime());
        int seconds = Integer.valueOf(command.getParameters().getTime());
        waitTime.start(seconds);
    }

    @Override
    public void waitCallBack(){
        Log.d(TAG, "WAIT - получен коллбек");
        TCOMMAN_X_ID tcommanXId = mMeanObject.getResults().get(mCurrentConutCommands);
        tcommanXId.setOutput("OK");
        tcommanXId.setEnd(getTime());
        mCurrentConutCommands++;
        start();
    }

    //------------------------------------------------------------------------------------
    private void get_ipparams() {
        TCOMMAN_X_ID tcommanXId = mMeanObject.getResults().get(mCurrentConutCommands);
        tcommanXId.setBegin(getTime());
        int timeout = mCommands.get(mCurrentConutCommands).getTimeout();
        WifiInfoObject wifiInfoObject = AllInterface.iWifi.getWifiInfo();
        tcommanXId.setOutput(wifiInfoObject);
        tcommanXId.setEnd(getTime());
        String str = wifiInfoObject.getIpaddr();
        if (str.equals("NaN")){
            connectWifi = false;
        }
        else connectWifi = true;



        mCurrentConutCommands++;
        start();
    }

    private String findMetodMeasurement(FGetTaskCommands command) {
        return command.getType();
    }

    //---------------------------------------------------------------------------
    private void scanWifi2() {
        FGetTaskCommands command = mCommands.get(mCurrentConutCommands);
        TCOMMAN_X_ID tcommanXId = mMeanObject.getResults().get(mCurrentConutCommands);
        tcommanXId.setBegin(getTime());
        int timeout = mCommands.get(mCurrentConutCommands).getTimeout();
        new WifiScan().scanWifi(this, timeout);
    }

    @Override
    public void wifiScanCallBack(List<WifiItem> wifiItems, int code) {
        Log.d(TAG, "SCAN_WIFI - получен коллбек, код - "+code);
        TCOMMAN_X_ID tcommanXId = mMeanObject.getResults().get(mCurrentConutCommands);
        tcommanXId.setEnd(getTime());
        if(code == 1){
            tcommanXId.setOutput("TIMEOUT");
        }else {
            tcommanXId.setOutput(wifiItems);
        }
            mCurrentConutCommands++;
            start();
    }

    //-----------------------------------------------------------------------------------------
    private void assoc_ssid() {
        FGetTaskCommands command = mCommands.get(mCurrentConutCommands);
        TCOMMAN_X_ID tcommanXId = mMeanObject.getResults().get(mCurrentConutCommands);
        tcommanXId.setBegin(getTime());
        int timeout = mCommands.get(mCurrentConutCommands).getTimeout();
        String ssid = command.getParameters().getSsid();
        ConnectToSSID connectToSSID = new ConnectToSSID();
        connectToSSID.addNetwork(ssid);
        connectToSSID.connect(ssid, this, timeout);
    }

    @Override
    public void wifiConnectCallBack(boolean state) {
        Log.d(TAG, "ASSOC_SSID - получен коллбек");
        TCOMMAN_X_ID tcommanXId = mMeanObject.getResults().get(mCurrentConutCommands);
        connectWifi = false;
        if(state == true){
            tcommanXId.setOutput("OK");
            connectWifi = true;
        }
        else tcommanXId.setOutput("TIMEOUT");
        tcommanXId.setEnd(getTime());
        mCurrentConutCommands++;
        start();
    }

    //-------------------------------------------------------------------------------------

    private String getTime(){
        return
                String.valueOf(System.currentTimeMillis()/1000);
    }

    @Override
    public void success() {
        AllInterface.iSendMeasurement.sendMeanObject(mMeanObject);
        RegOnServise.isConnectinAfterMeasumerent=false;
    }

    //----------------------------------------------------
    private void downloadFile(){

        FGetTaskCommands command = mCommands.get(mCurrentConutCommands);
        TCOMMAN_X_ID tcommanXId = mMeanObject.getResults().get(mCurrentConutCommands);
        tcommanXId.setBegin(getTime());
        int timeout = mCommands.get(mCurrentConutCommands).getTimeout();
        String url = command.getParameters().getUrl_1();
        if(connectWifi){
            Downloader downloader = new Downloader(this, timeout);
            downloader.start(url);
        }
        else{
            tcommanXId.setOutput("ERROR");
            tcommanXId.setEnd(getTime());
            mCurrentConutCommands++;
            start();
        }

    }

    @Override
    public void downdoladEvent(int state) {
        Log.d(TAG, "GET_FILE - получен коллбек. state = "+state);
        TCOMMAN_X_ID tcommanXId = mMeanObject.getResults().get(mCurrentConutCommands);
        if(state == 0) tcommanXId.setOutput("OK");
        if(state == 1) tcommanXId.setOutput("ERROR");
        if(state == 2) tcommanXId.setOutput("TIMEOUT");
        tcommanXId.setEnd(getTime());
        mCurrentConutCommands++;
        start();
    }
}
