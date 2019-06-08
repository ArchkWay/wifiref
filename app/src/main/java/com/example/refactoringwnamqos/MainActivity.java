package com.example.refactoringwnamqos;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.intefaces.IMainActivity;
import com.example.refactoringwnamqos.logs.LogAdapter;
import com.example.refactoringwnamqos.logs.WorkWithLog;
import com.example.refactoringwnamqos.services.WorkService;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements IMainActivity{

    private RecyclerView recyclerView;
    private LogAdapter recyclerViewLog;
    private List<LogItem> recLogItems = new ArrayList<>();

    WorkWithLog workWithLog;
    private static final String TAG = "MainActivity";
    Button button;
    boolean test = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InfoAboutMe.context = getApplicationContext();
        button = findViewById(R.id.btnDoWork);
        String line = null;

        try {
            Process p = Runtime.getRuntime().exec("sh");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));

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


        recyclerView = findViewById(R.id.recycler_view);
        initRecyclerView();
        workWithLog = WorkWithLog.getInstance(getApplicationContext());
        recLogItems = AllInterface.iLog.getLogList();
        recyclerViewLog = new LogAdapter(recLogItems);
        recyclerView.setAdapter(recyclerViewLog);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if(WorkService.isSeviceStart) {
            button.setText("Завершить работу");
        }else{
            button.setText("Запустить работу");
        }
        button = findViewById(R.id.btnDoWork);
        button.setOnClickListener(v -> startService());
//        final Handler handler = new Handler();
//        handler.postDelayed(() -> startService(), 30000);
//        startService();
    }

    public void startService() {
        if(WorkService.isSeviceStart) {
            button.setText("Завершить работу");
            WorkService.isSeviceStart = false;
            getApplication().stopService(new Intent(getApplicationContext(), WorkService.class));
            if(AllInterface.iScheduleMeasumerent != null) AllInterface.iScheduleMeasumerent.stopSchedule();

        }
        else {
            button.setText("Запустить работу");
            Intent serviceIntent = new Intent(this, WorkService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
        }

    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
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
        super.onResume();
    }


    @Override
    public void updateRecycler() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recyclerViewLog.notifyDataSetChanged();
            }
        });
    }

    public void showToast(String str){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
