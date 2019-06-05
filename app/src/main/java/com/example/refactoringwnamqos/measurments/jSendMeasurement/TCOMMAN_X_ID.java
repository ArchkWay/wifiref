package com.example.refactoringwnamqos.measurments.jSendMeasurement;

public class TCOMMAN_X_ID {
    private String commandId;
    private String begin;
    private String end;
    private Object output;


    public TCOMMAN_X_ID(){}

    public TCOMMAN_X_ID(String commandId, String begin, String end, Object output) {
        this.commandId = commandId;
        this.begin = begin;
        this.end = end;
        this.output = output;
    }

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
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

    public Object getOutput() {
        return output;
    }

    public void setOutput(Object output) {
        this.output = output;
    }
}
