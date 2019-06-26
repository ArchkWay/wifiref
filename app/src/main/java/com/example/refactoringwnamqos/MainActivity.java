package com.example.refactoringwnamqos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.enteties.jGetTask.FGetTaskCommands;
import com.example.refactoringwnamqos.enteties.modelJson.jMeasurement.jSendMeasurement.FSendMeasurement;
import com.example.refactoringwnamqos.enteties.modelJson.jMeasurement.jSendMeasurement.TCOMMAN_X_ID;
import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.intefaces.IDownloader;
import com.example.refactoringwnamqos.intefaces.IMainActivity;
import com.example.refactoringwnamqos.intefaces.ISendMeasureCallBack;
import com.example.refactoringwnamqos.intefaces.IWebAuthorCallBack;
import com.example.refactoringwnamqos.logs.LogAdapter;
import com.example.refactoringwnamqos.logs.WorkWithLog;
import com.example.refactoringwnamqos.measurments.webauthorizition.WebAuthor;
import com.example.refactoringwnamqos.measurments.webauthorizition.WebAuthorObj;
import com.example.refactoringwnamqos.phone.RPS_PermissionActivity;
import com.example.refactoringwnamqos.services.WorkService;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static com.example.refactoringwnamqos.measurments.Measurement.mCurrentConutCommands;

public class MainActivity extends AppCompatActivity implements IMainActivity, IWebAuthorCallBack, RPS_PermissionActivity.RequestPermissionAction {

    private RecyclerView recyclerView;
    private LogAdapter adapter;
    private List <LogItem> recLogItems = new ArrayList <>();
    WorkWithLog workWithLog;
    private static final String TAG = "MainActivity";
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InfoAboutMe.context = getApplicationContext();
        button = findViewById(R.id.btnDoWork);
        writingLogs();
        recyclerView = findViewById(R.id.recycler_view);
        initRecyclerView();
        workWithLog = WorkWithLog.getInstance(getApplicationContext());
        recLogItems = AllInterface.iLog.getLogList();
        adapter = new LogAdapter(recLogItems);
        recyclerView.setAdapter(adapter);

//        RPS_PermissionActivity rps_permissionActivity = new RPS_PermissionActivity();
//        rps_permissionActivity.getReadSMSPermission(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        button = findViewById(R.id.btnDoWork);
        isStoragePermissionGranted();

        button.setOnClickListener(v -> startService());
        startService();
    }


    public void startService() {
        if (WorkService.isSeviceStart) {
            setFinish();
        } else {
            setStart();
        }

    }



    private void setStart() {
        button.setText("Завершить работу");
        WorkService.isSeviceStart = true;
        Intent serviceIntent = new Intent(this, WorkService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private void setFinish() {
        button.setText("Запустить работу");
        WorkService.isSeviceStart = false;
        getApplication().stopService(new Intent(getApplicationContext(), WorkService.class));
        if (AllInterface.iScheduleMeasurement != null) {
            AllInterface.iScheduleMeasurement.stopSchedule();
        }
    }

    private void initRecyclerView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onPause() {
        AllInterface.iMainActivity = null;
        super.onPause();
    }

    @Override
    protected void onResume() {
        AllInterface.iMainActivity = this;
        updateRecycler();
        int count = adapter.getItemCount();
        recyclerView.smoothScrollToPosition(count);
        super.onResume();
    }


    @Override
    public void updateRecycler() {
        runOnUiThread(() -> adapter.notifyDataSetChanged());
        int count = adapter.getItemCount();
        recyclerView.smoothScrollToPosition(count);
    }

    public void showToast(String str) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show());
    }

    public void writingLogs() {
        String line;
        try {
            Process p = Runtime.getRuntime().exec("sh");
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            DataOutputStream out = new DataOutputStream(p.getOutputStream());
            out.writeBytes("cat /sys/class/net/wlan0/address\n");
            out.writeBytes("exit\n");
            out.flush();
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    private void testWebAuth() {
        WebAuthorObj webAuthorObj = new WebAuthorObj();
        webAuthorObj.setTel("+79094303146");
        webAuthorObj.setUrl_1("https://ya.ru");
        webAuthorObj.setUrl_2("https://ya.ru");
        webAuthorObj.setUrl_3("https://ya.ru");

        WebAuthor webAuthor = new WebAuthor(webAuthorObj, this, 1);
        WifiManager wifiManager = (WifiManager) InfoAboutMe.context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        wifiManager.enableNetwork(0, true);
        webAuthor.step1();
    }


    @Override
    public void webAuthorCallback(int state) {
        Log.d("sd", String.valueOf(state));
    }

    @Override
    public void permissionDenied() {
        RPS_PermissionActivity rps_permissionActivity = new RPS_PermissionActivity();
        rps_permissionActivity.checkReadSMSPermission();
    }

    @Override
    public void permissionGranted() {
        Log.d("sd", "sd");
    }


    public static class SmsReceiver extends AsyncTask <Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
                String sms;
                do {
                    sms = InfoAboutMe.SMS;
                    try {
                        Toast.makeText(InfoAboutMe.context, InfoAboutMe.SMS.substring(4, 8), Toast.LENGTH_LONG).show();
                        Date date = new Date();
                        AllInterface.iLog.addToLog(new LogItem("Измерения ", "Получена смс", String.valueOf(date)));
                        mCurrentConutCommands++;
                    }
                    catch (Exception e){}
                } while (sms == null);


            return null;
        }
    }
}
