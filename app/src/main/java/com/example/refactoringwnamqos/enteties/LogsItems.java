package com.example.refactoringwnamqos.enteties;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LogsItems {

    @SerializedName("logs")
    List<LogItem> logsItems = new ArrayList<>();

    public void setLogsItems(List<LogItem> logsItems) {
        this.logsItems = logsItems;
    }

    public LogsItems(List<LogItem> logsItems) {
        this.logsItems = logsItems;
    }

    public LogsItems() {
    }

    public void add(LogItem logItem){
        logsItems.add(logItem);
    }
}
