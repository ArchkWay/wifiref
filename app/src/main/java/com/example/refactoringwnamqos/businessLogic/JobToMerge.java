package com.example.refactoringwnamqos.businessLogic;

import com.example.refactoringwnamqos.enteties.jGetAll.FGetAllData;
import com.example.refactoringwnamqos.enteties.jGetTask.FGetTaskData;
import com.example.refactoringwnamqos.measurments.MeanObject;

import java.util.List;


public class JobToMerge {

    private List<FGetAllData> listScheduls;
    private List<FGetTaskData> listTasks;
    private List<MeanObject> meanObjectList;

    public JobToMerge(List<FGetAllData> listScheduls, List<FGetTaskData> listTasks, List<MeanObject> meanObjectList) {
        this.listScheduls = listScheduls;
        this.listTasks = listTasks;
        this.meanObjectList = meanObjectList;
    }

    public List<FGetAllData> getListScheduls() {
        return listScheduls;
    }

    public void setListScheduls(List<FGetAllData> listScheduls) {
        this.listScheduls = listScheduls;
    }

    public List<FGetTaskData> getListTasks() {
        return listTasks;
    }

    public void setListTasks(List<FGetTaskData> listTasks) {
        this.listTasks = listTasks;
    }

    public List<MeanObject> getMeanObjectList() {
        return meanObjectList;
    }

    public void setMeanObjectList(List<MeanObject> meanObjectList) {
        this.meanObjectList = meanObjectList;
    }
}
