package com.example.refactoringwnamqos.measurments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.refactoringwnamqos.InfoAboutMe;
import com.example.refactoringwnamqos.MainActivity;
import com.example.refactoringwnamqos.enteties.MeanObject;
import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.intefaces.IReconnectStomp;
import com.example.refactoringwnamqos.businessLogic.JobToMerge;
import com.example.refactoringwnamqos.businessLogic.RegOnService;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.enteties.jGetTask.FGetTaskCommands;
import com.example.refactoringwnamqos.enteties.jGetTask.FGetTaskData;
import com.example.refactoringwnamqos.intefaces.IWifiConnectCallBack;
import com.example.refactoringwnamqos.intefaces.IDownloader;
import com.example.refactoringwnamqos.enteties.modelJson.jMeasurement.jSendMeasurement.TCOMMAN_X_ID;
import com.example.refactoringwnamqos.intefaces.IWaitTime;
import com.example.refactoringwnamqos.intefaces.IWebAuthorCallBack;
import com.example.refactoringwnamqos.measurments.webauthorizition.WebAuthor;
import com.example.refactoringwnamqos.enteties.WebAuthorObj;
import com.example.refactoringwnamqos.intefaces.IWifiScanCallBack;
import com.example.refactoringwnamqos.wifi.Wifi;
import com.example.refactoringwnamqos.wifi.WifiItem;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Measurement implements IWifiScanCallBack, IWifiConnectCallBack, IWaitTime, IWebAuthorCallBack, IReconnectStomp, IDownloader {

    private JobToMerge job;
    private FGetTaskData mTask;
    private MeanObject mMeanObject;
    private List <TCOMMAN_X_ID> tcomman_x_ids;
    private int mCountCommands;
    private List <FGetTaskCommands> mCommands;
    public static int mCurrentConutCommands = 0;
    private Timer timer;
    private TimerTask timerTask;
    private boolean connectWifi;

    private static final String TAG = "Measurement";

    public Measurement(JobToMerge job) {
        this.job = job;
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

    public void preparation() {
        mCurrentConutCommands = 0;
        RegOnService.isConnectAfterMeasumerent = true;
        for (int q = 0; q < job.getListTasks().size(); q++) {
            mTask = job.getListTasks().get(q);
            mMeanObject = job.getMeanObjectList().get(q);
        }
        tcomman_x_ids = mMeanObject.getResults();
        mCountCommands = countCommand(mTask);
        mCommands = getCommands(mTask);

        mMeanObject.setBegin(getTime());
        mMeanObject.setId(getTime());

        start();
    }

    private int countCommand(FGetTaskData task) {
//        task = null;
        return task.getCommands().size();
    }

    public List <FGetTaskCommands> getCommands(FGetTaskData task) {
        return task.getCommands();
    }

    private void start() {
        Date date;
        if (mCurrentConutCommands < mCountCommands) {
            String type = findMethodMeasurement(mCommands.get(mCurrentConutCommands));
            switch (type) {
                case "SCAN_WIFI":
                    date = new Date();
                    AllInterface.iLog.addToLog(new LogItem("Измерения " + mCurrentConutCommands, "Сканирование сетей", String.valueOf(date)));
                    scanWifi2();
                    break;
                case "ASSOC_SSID":
                    date = new Date();
                    AllInterface.iLog.addToLog(new LogItem("Измерения " + mCurrentConutCommands, "Подлючение к сети", String.valueOf(date)));
                    assoc_ssid();
                    break;
                case "GET_IPPARAMS":
                    date = new Date();
                    AllInterface.iLog.addToLog(new LogItem("Измерения " + mCurrentConutCommands, "Зарос сетевых параметров", String.valueOf(date)));
                    get_ipparams();
                    break;
                case "WAIT":
                    date = new Date();
                    AllInterface.iLog.addToLog(new LogItem("Измерения " + mCurrentConutCommands, "Ожидание", String.valueOf(date)));
                    waitTime();
                    break;
                case "WEBAUTH":
                    date = new Date();
                    AllInterface.iLog.addToLog(new LogItem("Измерения " + mCurrentConutCommands, "Вебавторизвция", String.valueOf(date)));
                    webAuth();
                    break;
                case "TEST_INTERNET":

                    date = new Date();
                    AllInterface.iLog.addToLog(new LogItem("Измерения " + mCurrentConutCommands, "Тестирование интеренета", String.valueOf(date)));
                    testInternet();
//                    deleteSMS(InfoAboutMe.context);
                    break;
                case "GET_FILE":
                    date = new Date();
                    AllInterface.iLog.addToLog(new LogItem("Измерения " + mCurrentConutCommands, "Скачивание файла", String.valueOf(date)));
                    downloadFile();
                    break;
                case "GET_SMS":
                    getSms();
                    date = new Date();
                    AllInterface.iLog.addToLog(new LogItem("Измерения " + mCurrentConutCommands, "Получена смс", String.valueOf(date)));
                    break;
                case "CLEAR_SMS":
                    deleteSMS(InfoAboutMe.context);
                    date = new Date();
                    AllInterface.iLog.addToLog(new LogItem("Измерения " + mCurrentConutCommands, "Все смс удалены", String.valueOf(date)));
                    break;
                case "RESET_WEBAUTH":
                    date = new Date();
                    AllInterface.iLog.addToLog(new LogItem("Измерения " + mCurrentConutCommands, "Все смс удалены", String.valueOf(date)));

                    break;
                default:
                    date = new Date();
                    AllInterface.iLog.addToLog(new LogItem("Измерения " + mCurrentConutCommands, "Команда не поддерживается", String.valueOf(date)));
                    mCurrentConutCommands++;
                    start();
            }
        } else {
            mMeanObject.setEnd(getTime());
            date = new Date();
            AllInterface.iLog.addToLog(new LogItem("Измерения " + mCurrentConutCommands, "Измерение завершено", String.valueOf(date)));
//            AllInterface.iWifi.disableWifi();
            if (timer != null) {
                timer.cancel();
                timer = null;
            }

            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    Date date = new Date();
                    AllInterface.iLog.addToLog(new LogItem("Measurement -> TimerTask", "run()", String.valueOf(date)));
                    AllInterface.iswClientConnect.connectStomp();
                    timer.cancel();
                }
            };
            timer.schedule(timerTask, 5_000);
            date = new Date();
            AllInterface.iLog.addToLog(new LogItem("Measurement -> Timer", "Старт таймера", String.valueOf(date)));
//            AllInterface.iReconnectStomp.success();

        }
    }

    //------------------------------------------------------------------------------------
    private void testInternet() {
        TCOMMAN_X_ID tcommanXId = mMeanObject.getResults().get(mCurrentConutCommands);
        tcommanXId.setBegin(getTime());
        int timeout = mCommands.get(mCurrentConutCommands).getTimeout();
        if (ConnectivityHelper.isConnectedToNetwork(InfoAboutMe.context)) {
            TestInternet testInternet = new TestInternet();
            boolean test = testInternet.executeCommand();
            if (test) {
                tcommanXId.setOutput("OK");
                tcommanXId.setStatus(true);
            } else {
                tcommanXId.setOutput("connected, but ERROR");
                tcommanXId.setStatus(true);
            }

        } else {
            tcommanXId.setOutput("ERROR");
            tcommanXId.setStatus(false);
        }
        tcommanXId.setEnd(getTime());

        mMeanObject.getResults().set(mCurrentConutCommands, tcommanXId);
        mCurrentConutCommands++;
        start();
    }

    //------------------------------------------------------------------------------------
    private void getSms() {
        TCOMMAN_X_ID tcommanXId = mMeanObject.getResults().get(mCurrentConutCommands);
        tcommanXId.setBegin(getTime());
        MainActivity.SmsReceiver smsReceiver = new MainActivity.SmsReceiver();

        smsReceiver.execute();
        tcommanXId.setOutput("OK");
        tcommanXId.setEnd(getTime());
        tcommanXId.setStatus(true);
        mMeanObject.getResults().set(mCurrentConutCommands, tcommanXId);
    }

    //------------------------------------------------------------------------------------
    private void webAuth() {
        TCOMMAN_X_ID tcommanXId = mMeanObject.getResults().get(mCurrentConutCommands);
        tcommanXId.setBegin(getTime());
        InfoAboutMe.Measurment = true;
            WebAuthorObj webAuthorObj = new WebAuthorObj();
            try {
                webAuthorObj.setTel(InfoAboutMe.phone);
                webAuthorObj.setUrl_1("http://www.ru");
                webAuthorObj.setUrl_2("http://wnam-srv1.alel.net/cp/mikrotik");
                webAuthorObj.setUrl_3("http://wnam-srv1.alel.net/cp/");
                WebAuthor webAuthor = new WebAuthor(webAuthorObj, this, 1);
                WifiManager wifiManager = (WifiManager) InfoAboutMe.context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                webAuthor.step1();
            }
            catch (Exception e){}
        tcommanXId.setEnd(getTime());
        InfoAboutMe.Measurment = false;
        tcommanXId.setStatus(true);
        mMeanObject.getResults().set(mCurrentConutCommands, tcommanXId);
        mCurrentConutCommands++;
    }


    @Override
    public void webAuthorCallback(int state) {
        Log.d(TAG, "WEBAUTH - получен коллбек " + state);
        TCOMMAN_X_ID tcommanXId = mMeanObject.getResults().get(mCurrentConutCommands);
        if (state == 0) tcommanXId.setOutput("OK");
        if (state == 1) tcommanXId.setOutput("TIMEOUT");
        if (state == 2) tcommanXId.setOutput("ERROR");
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
        tcommanXId.setStatus(true);
        mMeanObject.getResults().set(mCurrentConutCommands, tcommanXId);
        Log.d(TAG, "WAIT - получен коллбек");
    }

    @Override
    public void waitCallBack() {
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
        Wifi wiFi = new Wifi(InfoAboutMe.context);
        WifiInfoObject wifiInfoObject;
        try {
            if (AllInterface.iWifi.getWifiInfo().getLease().isEmpty()) {
                wifiInfoObject = AllInterface.iWifi.getWifiInfo();
            } else wifiInfoObject = wiFi.getWifiInfo();

        } catch (Exception e) {
            try {
                wifiInfoObject = wiFi.getWifiInfo();
            } catch (Exception noInf) {
                wifiInfoObject = AllInterface.iWifi.getWifiInfo();
            }
        }
        tcommanXId.setOutput(wifiInfoObject);
        tcommanXId.setEnd(getTime());

        String str = wifiInfoObject.getIpaddr();

        if (str.equals("NaN")) {
            connectWifi = false;
            tcommanXId.setStatus(false);
        } else {
            connectWifi = true;
            tcommanXId.setStatus(true);
        }
        mMeanObject.getResults().set(mCurrentConutCommands, tcommanXId);

        mCurrentConutCommands++;
        start();
    }

    private String findMethodMeasurement(FGetTaskCommands command) {
        return command.getType();
    }

    //---------------------------------------------------------------------------
    private void scanWifi2() {
        FGetTaskCommands command = mCommands.get(mCurrentConutCommands);
        TCOMMAN_X_ID tcommanXId = mMeanObject.getResults().get(mCurrentConutCommands);
        tcommanXId.setBegin(getTime());
        int timeout = mCommands.get(mCurrentConutCommands).getTimeout();
        new WifiScan().scanWifi(this, timeout);
        tcommanXId.setStatus(true);
        tcomman_x_ids.set(mCurrentConutCommands, tcommanXId);
        Log.d(TAG, "SCAN_WIFI - получен коллбек, код - " + mCurrentConutCommands);

    }

    @Override
    public void wifiScanCallBack(List <WifiItem> wifiItems, int code) {
        Log.d(TAG, "SCAN_WIFI - получен коллбек, код - " + code);
        try {
            if (mMeanObject.getResults().size() >= mCurrentConutCommands) {
                TCOMMAN_X_ID tcommanXId = mMeanObject.getResults().get(mCurrentConutCommands);
                tcommanXId.setEnd(getTime());
                if (code == 1) {
                    tcommanXId.setOutput("TIMEOUT");
                } else {
                    tcommanXId.setOutput(wifiItems);
                    tcommanXId.setStatus(true);
                }
                tcomman_x_ids.set(mCurrentConutCommands, tcommanXId);

            }
        } catch (Exception e) {
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
        tcommanXId.setEnd(getTime());
        tcommanXId.setStatus(true);
        mMeanObject.getResults().set(mCurrentConutCommands, tcommanXId);
        mCurrentConutCommands++;
        start();
    }

    @Override
    public void wifiConnectCallBack(boolean state) {
        Log.d(TAG, "ASSOC_SSID - получен коллбек");
        TCOMMAN_X_ID tcommanXId = mMeanObject.getResults().get(mCurrentConutCommands);
        if (state) {
            tcommanXId.setOutput("OK");
            setConnectWifi(true);
            Date date = new Date();
            AllInterface.iLog.addToLog(new LogItem("Измерения " + mCurrentConutCommands, "Скачивание файла", String.valueOf(date)));
        } else tcommanXId.setOutput("TIMEOUT");
        tcommanXId.setEnd(getTime());
        mCurrentConutCommands++;
        start();
    }

    //-------------------------------------------------------------------------------------

    private String getTime() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    @Override
    public void success() {
        AllInterface.iSendMeasurement.sendMeanObject(mMeanObject);
        RegOnService.isConnectAfterMeasumerent = false;
    }

    //----------------------------------------------------
    private void downloadFile() {
        FGetTaskCommands command = mCommands.get(mCurrentConutCommands);
        TCOMMAN_X_ID tcommanXId = mMeanObject.getResults().get(mCurrentConutCommands);
        tcommanXId.setBegin(getTime());
        int timeout = mCommands.get(mCurrentConutCommands).getTimeout();
        String url = command.getParameters().getUrl_1();
        if (ConnectivityHelper.isConnectedToNetwork(InfoAboutMe.context)) {
            Downloader downloader = new Downloader(this, timeout, tcommanXId);

            downloader.start(url);

        } else {
            tcommanXId.setOutput("ERROR");
            tcommanXId.setEnd(getTime());
            tcommanXId.setStatus(false);
            mMeanObject.getResults().set(mCurrentConutCommands, tcommanXId);
            mCurrentConutCommands++;
            start();
        }
    }

    @Override
    public void downloadEvent(TCOMMAN_X_ID tcomman_x_id, int state) {
        Log.d(TAG, "GET_FILE - получен коллбек. state = " + state);
        if (state == 0) {
//            tcomman_x_id.setOutput("OK");
            tcomman_x_id.setStatus(true);
            try {
                mMeanObject.getResults().set(mCurrentConutCommands, tcomman_x_id);
            } catch (Exception e) {
                preparation();
            }
        }

        if (state == 1) {
            tcomman_x_id.setOutput("ERROR");
            tcomman_x_id.setStatus(false);
            mMeanObject.getResults().set(mCurrentConutCommands, tcomman_x_id);
        }
        if (state == 2) {
            tcomman_x_id.setOutput("TIMEOUT");
            tcomman_x_id.setStatus(true);
            mMeanObject.getResults().set(mCurrentConutCommands, tcomman_x_id);

        }
        tcomman_x_id.setEnd(getTime());
        mCurrentConutCommands++;
        start();
    }

    public void deleteSMS(Context context) {
        Uri inboxUri = Uri.parse("content://sms/inbox");
        int count = 0;
        Cursor c = context.getContentResolver().query(inboxUri, null, null, null, null);
        while (c.moveToNext()) {
            try {
                // Delete the SMS
                String pid = c.getString(0); // Get id;
                String uri = "content://sms/" + pid;
                count = context.getContentResolver().delete(Uri.parse(uri), null, null);
            } catch (Exception e) {
            }
        }
    }


}
