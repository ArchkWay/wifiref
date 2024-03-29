package com.example.refactoringwnamqos.measurments;

import com.example.refactoringwnamqos.enteties.modelJson.jMeasurement.jSendMeasurement.TCOMMAN_X_ID;

import java.util.List;


public class ISendMeasurment {

    private String id;
    private String taskId;
    private String scheduleId;
    private String begin;
    private String end;
    private boolean status;
    private List<TCOMMAN_X_ID> results;

    public ISendMeasurment(){}

    public ISendMeasurment(String id, String taskId, String scheduleId, String begin, String end, boolean status, List<TCOMMAN_X_ID> results) {
        this.id = id;
        this.taskId = taskId;
        this.scheduleId = scheduleId;
        this.begin = begin;
        this.end = end;
        this.status = status;
        this.results = results;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
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
}
