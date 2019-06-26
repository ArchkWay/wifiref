package com.example.refactoringwnamqos.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.example.refactoringwnamqos.InfoAboutMe;
import com.example.refactoringwnamqos.MainActivity;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.logs.WorkWithLog;
import com.example.refactoringwnamqos.wifi.WF_permissions;


public class StartBroadReceiv extends BroadcastReceiver {

    final String LOG_TAG = "MyBroadReceiv";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "onReceive " + intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        }
        WorkWithLog workWithLog = WorkWithLog.getInstance(context);
        WorkWithLog.startTime = System.currentTimeMillis();
        workWithLog.addToLog(new LogItem("Получен бродкаст", "BC или QP", String.valueOf(WorkWithLog.startTime)));
        if (!WorkService.isSeviceStart)
            context.startService(new Intent(context, WorkService.class));

        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
//                        if(msg_from.equals("WNAM")){
                            InfoAboutMe.SMS = msgBody;
//                        }
                        Toast.makeText(InfoAboutMe.context, msg_from, Toast.LENGTH_LONG).show();
                        Toast.makeText(InfoAboutMe.context, InfoAboutMe.SMS.substring(4,8), Toast.LENGTH_LONG).show();
                    }
                }catch(Exception e){
                            Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }
}
