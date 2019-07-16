package com.example.refactoringwnamqos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.intefaces.IMainActivity;
import com.example.refactoringwnamqos.intefaces.IWebAuthorCallBack;
import com.example.refactoringwnamqos.logs.LogAdapter;
import com.example.refactoringwnamqos.logs.WorkWithLog;
import com.example.refactoringwnamqos.measurments.webauthorizition.WebAuthor;
import com.example.refactoringwnamqos.enteties.WebAuthorObj;
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
    EditText etPhone;
    TextView tvPhone;
    WorkWithLog workWithLog;
    private static final String TAG = "MainActivity";
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.btnDoWork);
        etPhone = findViewById(R.id.etPhone);
        tvPhone = findViewById(R.id.tvPhone);
        InfoAboutMe.context = getApplicationContext();

        writingLogs();
        workWithLog = WorkWithLog.getInstance(getApplicationContext());
        recLogItems = AllInterface.iLog.getLogList();

        initRecyclerView();
        phoneListener();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        isStoragePermissionGranted();
        if (InfoAboutMe.phone != null) {
            if (checkReadSMSPermission()) testWebAuth();
            else getReadSMSPermission(onPermissionCallBack);
        }

        button.setOnClickListener(v -> startService());

    }

    private void phoneListener() {
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Toast.makeText(MainActivity.this, "after changing - wait 10 seconds, ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterTextChanged(Editable s) {
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    InfoAboutMe.phone = s.toString();
                    if (InfoAboutMe.phone != null) {
                        if (InfoAboutMe.phone.length() >= 10) {
                            if (checkReadSMSPermission()) {
                                testWebAuth();
                                hideKeyboard();
                            }
                            else getReadSMSPermission(onPermissionCallBack);
                        }
                    }
                    etPhone.setVisibility(View.INVISIBLE);
                    tvPhone.setText(s.toString());
                    tvPhone.setVisibility(View.VISIBLE);
                    startService();
                }, 10000);

            }
        });
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
        recyclerView = findViewById(R.id.recycler_view);
        adapter = new LogAdapter(recLogItems);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

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
        webAuthorObj.setTel("79094303146");
//        webAuthorObj.setTel(InfoAboutMe.phone);
        webAuthorObj.setUrl_1("http://www.ru");
        webAuthorObj.setUrl_2("http://wnam-srv1.alel.net/cp/mikrotik");
        webAuthorObj.setUrl_3("http://wnam-srv1.alel.net/cp/sms");
        WebAuthor webAuthor = new WebAuthor(webAuthorObj, this, 1);
        WifiManager wifiManager = (WifiManager) InfoAboutMe.context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        wifiManager.setWifiEnabled(true);
//        wifiManager.enableNetwork(0, true);
        webAuthor.step1();
    }


    @Override
    public void webAuthorCallback(int state) {
        Log.d("Вебавторизация фаза: ", String.valueOf(state));
    }

    @Override
    public void permissionDenied() {
        RPS_PermissionActivity rps_permissionActivity = new RPS_PermissionActivity();
        rps_permissionActivity.checkReadSMSPermission();
    }

    @Override
    public void permissionGranted() {

        Log.d("__sd", "sd");
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
                } catch (Exception e) {
                }
            } while (sms == null);
            InfoAboutMe.gotSms = true;
            return null;
        }
    }

    private boolean checkReadSMSPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private final static String APP_NAME = "APP_NAME";
    private final static int REQUEST_READ_SMS_PERMISSION = 3004;
    public final static String READ_SMS_PERMISSION_NOT_GRANTED = "Please allow " + APP_NAME + " to access your SMS from setting";
    RequestPermissionAction onPermissionCallBack;

    public void getReadSMSPermission(RequestPermissionAction onPermissionCallBack) {
        this.onPermissionCallBack = onPermissionCallBack;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkReadSMSPermission()) {
                requestPermissions(new String[]{Manifest.permission.READ_SMS}, REQUEST_READ_SMS_PERMISSION);
                return;
            }
        }
        if (onPermissionCallBack != null) onPermissionCallBack.permissionGranted();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (REQUEST_READ_SMS_PERMISSION == requestCode) {
                // TODO Request Granted for READ_SMS.
                System.out.println("REQUEST_READ_SMS_PERMISSION Permission Granted");
            }
            if (onPermissionCallBack != null) onPermissionCallBack.permissionGranted();

        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            if (REQUEST_READ_SMS_PERMISSION == requestCode) {
            }
            if (onPermissionCallBack != null) onPermissionCallBack.permissionDenied();
        }
    }

    public interface RequestPermissionAction {
        void permissionDenied();

        void permissionGranted();
    }
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etPhone.getWindowToken(), 0);
    }

}
