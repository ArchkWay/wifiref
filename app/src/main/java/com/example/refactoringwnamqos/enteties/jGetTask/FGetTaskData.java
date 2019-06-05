package com.example.refactoringwnamqos.enteties.jGetTask;

import java.util.List;

public class FGetTaskData {
    private String id;
    private String name;
    private boolean critical;
    private List<FGetTaskCommands> commands;
    private String scheduleId;
    private int mIndexTask;
    private int mInterval;
    private boolean mIsRepeat;
    private String mStart;
    private String mEnd;

    public FGetTaskData(String id, String name, boolean critical, List<FGetTaskCommands> commands, String scheduleId, int mIndexTask, int mInterval, boolean mIsRepeat) {
        this.id = id;
        this.name = name;
        this.critical = critical;
        this.commands = commands;
        this.scheduleId = scheduleId;
        this.mIndexTask = mIndexTask;
        this.mInterval = mInterval;
        this.mIsRepeat = mIsRepeat;
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

    public boolean isCritical() {
        return critical;
    }

    public void setCritical(boolean critical) {
        this.critical = critical;
    }

    public List<FGetTaskCommands> getCommands() {
        return commands;
    }

    public void setCommands(List<FGetTaskCommands> commands) {
        this.commands = commands;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
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

    public String getmStart() {
        return mStart;
    }

    public void setmStart(String mStart) {
        this.mStart = mStart;
    }

    public String getmEnd() {
        return mEnd;
    }

    public void setmEnd(String mEnd) {
        this.mEnd = mEnd;
    }
}
