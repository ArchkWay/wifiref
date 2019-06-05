package com.example.refactoringwnamqos;


import com.example.refactoringwnamqos.businessLogic.IReconnectStomp;
import com.example.refactoringwnamqos.intefaces.ILog;
import com.example.refactoringwnamqos.intefaces.IMainActivity;
import com.example.refactoringwnamqos.intefaces.IScheduleMeasumerent;
import com.example.refactoringwnamqos.measurments.ISendMeasurement;
import com.example.refactoringwnamqos.measurments.IWifiInterface;
import com.example.refactoringwnamqos.websocket.base.ISWClientConnect;

public class AllInterface {

    public static ILog iLog;
    public static IMainActivity iMainActivity;
    public static IWifiInterface iWifi;
    public static ISendMeasurement iSendMeasurement;
    public static ISWClientConnect iswClientConnect;
    public static IReconnectStomp iReconnectStomp;
    public static IScheduleMeasumerent iScheduleMeasumerent;
}
