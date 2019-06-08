package com.example.refactoringwnamqos.enteties.modelJson.jMeasurement.jSendMeasurement;

import java.util.List;

public class TSendMeasurement {

    private String id;
    private String task;
    private String schedule;
    private String begin;
    private String end;
    private boolean status;
    private List<TCOMMAN_X_ID> results;
    private int mIndexTask;
    private int mInterval;
    private boolean mIsRepeat;

    public TSendMeasurement(String id, String task, String schedule, String begin, String end, boolean status, List<TCOMMAN_X_ID> results) {
        this.id = id;
        this.task = task;
        this.schedule = schedule;
        this.begin = begin;
        this.end = end;
        this.status = status;
        this.results = results;
    }

    public TSendMeasurement(){};

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<TCOMMAN_X_ID> getResults() {
        return results;
    }

    public void setResults(List<TCOMMAN_X_ID> results) {
        this.results = results;
    }

    public int getmIndexTask() {
        return mIndexTask;
    }

    public void setmIndexTask(int mIndexTask) {
        this.mIndexTask = mIndexTask;
    }

    public int getmInterval() {
        return mInterval;
    }

    public void setmInterval(int mInterval) {
        this.mInterval = mInterval;
    }

    public boolean ismIsRepeat() {
        return mIsRepeat;
    }

    public void setmIsRepeat(boolean mIsRepeat) {
        this.mIsRepeat = mIsRepeat;
    }
}
