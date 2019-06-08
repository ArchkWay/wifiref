package com.example.refactoringwnamqos.measurments;

import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.enteties.LogItem;

import java.io.IOException;


public class TestInternet {

    public boolean executeCommand(){
        AllInterface.iLog.addToLog(new LogItem("TestInternet","executeCommand()", null));
        System.out.println("executeCommand");
        Runtime runtime = Runtime.getRuntime();
        try {
            Process mIpAddrProcess = runtime.exec("/system/bin/ping -c 8.8.8.8");
            int mExitValue = mIpAddrProcess.waitFor();
            System.out.println(" mExitValue "+mExitValue);

            if(mExitValue == 0){
                return true;
            } else{
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

}
