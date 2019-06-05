package com.example.refactoringwnamqos.enteties.jGetAll;

public class FGetAllData {

    private String id;
    private String name;
    private String task;
    private String taskName;
    private boolean enabled;
    private String schedule;
    private String onfailure;

    public FGetAllData(String id, String name, String task, String taskName, boolean enabled, String schedule, String onfailure) {
        this.id = id;
        this.name = name;
        this.task = task;
        this.taskName = taskName;
        this.enabled = enabled;
        this.schedule = schedule;
        this.onfailure = onfailure;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getOnfailure() {
        return onfailure;
    }

    public void setOnfailure(String onfailure) {
        this.onfailure = onfailure;
    }
}
