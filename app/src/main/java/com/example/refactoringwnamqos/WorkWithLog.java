package com.example.refactoringwnamqos;

import android.content.Context;
import android.util.Log;

import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.enteties.LogsItems;
import com.example.refactoringwnamqos.intefaces.ILog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class WorkWithLog implements ILog {

    private static final String FILE = "log.txt";
    private Gson mGson = new GsonBuilder().create();
    private static WorkWithLog instance;

    //-----------------------------------------------------------------
    File file;
    Context context;
    List<LogItem> logI = new ArrayList<>();

    public static long startTime=0;
    //-----------------------------------------------------------------
    private WorkWithLog(Context context){
        AllInterface.iLog = this;
        this.context = context;
        file = new File(context.getFilesDir(), FILE);
        start();
    }

    //----------------------------------------------------------------
    public static WorkWithLog getInstance(Context mycontext){ // #3
        if(instance == null){		//если объект еще не создан
            instance = new WorkWithLog(mycontext);	//создать новый объект
        }
        return instance;		// вернуть ранее созданный объект
    }

    //-----------------------------------------------------------------
    @Override
    public void addToLog(LogItem logItem) {
        LogsItems exLogItems = getLogItemsFormLog();
        logItem.setmDate(getTimeNow());
        exLogItems.add(logItem);

        Log.d("ADD TO LOG", logItem.getmCaption() + " "+logItem.getmInfo()+" "+logItem.getmDate());

        try {
            FileWriter fw = new FileWriter(file, false); //the true will append the new data
            String str = mGson.toJson(exLogItems);
            fw.write(str);
            fw.flush();
            fw.close();
        }
        catch(IOException ioe)
            {
                System.err.println("IOException: " + ioe.getMessage());
            }

        logI.add(logItem);
        try {
            if(AllInterface.iMainActivity == null) return;
            AllInterface.iMainActivity.updateRecycler();
        } catch (Exception e) {

        };
    }

    public static String getTimeNow(){
        String time = String.valueOf(System.currentTimeMillis());
        return time.substring(0,10);
    }

    private void start() {
        startTime = System.currentTimeMillis()/1000;
        addToLog(new LogItem("Запуск логгирования","", String.valueOf(startTime)));
    }

    @Override
    public List<LogItem> getLogList() {
        return logI;
    }

    //--------------------------------------------------------------------
    private String readAllLog() {
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    context.openFileInput(FILE)));
            String str = "";
            StringBuffer sb = new StringBuffer();
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                sb.append(str);
                //Log.d(LOG_TAG, str);
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //------------------------------------------------------------------------------
    public void deleteLog(){
        File myFile = new File(String.valueOf(file));
        if(myFile.exists())
            myFile.delete();
    }

    //------------------------------------------------------------------------------
    public boolean check(){
        if(file.exists()){
            return true;
        }else{
            return false;
        }
    }

    //------------------------------------------------------------------------------
    private LogsItems getLogItemsFormLog(){
        LogsItems exLogItems;
        if(check()){
            String str = readAllLog();
            try {
                exLogItems = mGson.fromJson(str, LogsItems.class);
                if(exLogItems == null ) exLogItems = new LogsItems();
                return exLogItems;
            }catch (Exception e){
                return new LogsItems();
            }
        }else
            return
                    new LogsItems();
    }


}
