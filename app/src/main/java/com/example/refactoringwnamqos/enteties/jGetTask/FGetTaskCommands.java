package com.example.refactoringwnamqos.enteties.jGetTask;

public class FGetTaskCommands {
    private String id;
    private String type;
    private String name;
    private String caption;
    private int timeout;
    private FGetTaskParameters parameters;
    private String begin;
    private String eng;
    private String out;

    public FGetTaskCommands(String id, String type, String name, String caption, int timeout, FGetTaskParameters parameters, String begin, String eng, String out) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.caption = caption;
        this.timeout = timeout;
        this.parameters = parameters;
        this.begin = begin;
        this.eng = eng;
        this.out = out;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public FGetTaskParameters getParameters() {
        return parameters;
    }

    public void setParameters(FGetTaskParameters parameters) {
        this.parameters = parameters;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEng() {
        return eng;
    }

    public void setEng(String eng) {
        this.eng = eng;
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
    }
}
