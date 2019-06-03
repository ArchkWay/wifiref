package com.example.refactoringwnamqos.intefaces;


import com.example.refactoringwnamqos.enteties.LogItem;

import java.util.List;

public interface ILog {

    void addToLog(LogItem logItem);
    List<LogItem> getLogList();

}
