package com.example.refactoringwnamqos.measurments;

import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.enteties.LogItem;

import java.io.IOException;
import java.util.Date;


public class TestInternet {

    public boolean executeCommand(){
        Date date = new Date();
        AllInterface.iLog.addToLog(new LogItem("TestInternet","executeCommand()", String.valueOf(date)));
        System.out.println("executeCommand");
        Runtime runtime = Runtime.getRuntime();
        try {
            Process mIpAddrProcess = runtime.exec("/system/bin/ping -c 8.8.8.8");
            int mExitValue = mIpAddrProcess.waitFor();
            System.out.println(" mExitValue " + mExitValue);

            if(mExitValue == 0){
                return false;
            } else{
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

}
