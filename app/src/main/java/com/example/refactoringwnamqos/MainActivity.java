package com.example.refactoringwnamqos;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.refactoringwnamqos.enteties.LogItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LogAdapter recyclerViewLog;
    private List <LogItem> recLogItems = new ArrayList <>();
    WorkWithLog workWithLog;
    private static final String TAG = "MainActivity";
    Button button;
    boolean test = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startService(View view) {
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
}
