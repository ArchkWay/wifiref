package com.example.refactoringwnamqos.phone;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.InfoAboutMe;
import com.example.refactoringwnamqos.enteties.LogItem;


public class PhoneState {

    private Context context;

    public PhoneState(Context context) {
        this.context = context;
    }

    public void init() {
        if (hasPermissions()) {
            saveUUID();
        } else {
            Intent intent = new Intent(context, RPS_PermissionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public void saveUUID() {
        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            return;
        InfoAboutMe.UUID = String.valueOf(tManager.getDeviceId());
        AllInterface.iLog.addToLog(new LogItem("Мой UUID",InfoAboutMe.UUID,null));
        System.out.println(InfoAboutMe.UUID);
    }

    //------------------------------------------------------------------------------------
    private boolean hasPermissions() {
        int res=0;
        String[] permissions = new String[] {Manifest.permission.READ_PHONE_STATE};

        for(String perm : permissions){
            res = context.checkCallingOrSelfPermission(perm);
            if(!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

}
