package com.example.refactoringwnamqos.enteties.modelJson.waring;

import java.util.List;

public class TCommand {

    private String id;
    private String type;
    private List<Param> parameters;
    private int timeout;
    private boolean critical;

    public TCommand(String id, String type, List<Param> parameters, int timeout, boolean critical) {
        this.id = id;
        this.type = type;
        this.parameters = parameters;
        this.timeout = timeout;
        this.critical = critical;
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

    public List<Param> getParameters() {
        return parameters;
    }

    public void setParameters(List<Param> parameters) {
        this.parameters = parameters;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isCritical() {
        return critical;
    }

    public void setCritical(boolean critical) {
        this.critical = critical;
    }
}

