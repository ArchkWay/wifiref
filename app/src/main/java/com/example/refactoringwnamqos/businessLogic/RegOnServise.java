package com.example.refactoringwnamqos.businessLogic;

import com.example.refactoringwnamqos.intefaces.IGetTaskCallBack;
import com.example.refactoringwnamqos.intefaces.ILoadTaskCompleted;
import com.example.refactoringwnamqos.services.WorkService;
import com.example.refactoringwnamqos.enteties.jGetAll.FGetAll;
import com.example.refactoringwnamqos.enteties.jGetAll.FGetAllData;
import com.example.refactoringwnamqos.enteties.jGetTask.FGetTask;
import com.example.refactoringwnamqos.enteties.jGetTask.FGetTaskData;
import com.example.refactoringwnamqos.measurments.SendMeasurement;
import com.example.refactoringwnamqos.websocket.base.WSClient;
import com.example.refactoringwnamqos.websocket.model.mGetAll.GetAll;
import com.example.refactoringwnamqos.websocket.model.mGetAll.IAllCallback;
import com.example.refactoringwnamqos.intefaces.IRegCallBack;
import com.example.refactoringwnamqos.websocket.model.Registration;
import com.example.refactoringwnamqos.websocket.model.Refresh;
import com.example.refactoringwnamqos.websocket.model.mTask.GetTask;

import java.util.ArrayList;
import java.util.List;


public class RegOnServise implements IRegCallBack, IAllCallback, IGetTaskCallBack {
    private WSClient wsClient;
    private GetAll getAll;
    private GetTask getTask;
    private Refresh refresh;
    private RefreshAction refreshAction;
    private SendMeasurement sendMeasurement;
    ILoadTaskCompleted iLoadTaskCompleted;

    private int countId = 0;
    private int currId = 0;

    private List <FGetAllData> listSchedules;
    private List <FGetTaskData> listTasks;

    public static boolean isConnectinAfterMeasumerent = false;

    public RegOnServise(WSClient wsClient, ILoadTaskCompleted iLoadTaskCompleted) {
        this.wsClient = wsClient;
        this.iLoadTaskCompleted = iLoadTaskCompleted;
    }

    //--------------------------------------------------------------------------------------------
    public void start() {
        Registration registration = new Registration(wsClient, this);
        registration.subscribe();
        registration.send();
    }

    @Override
    public void regCallBack(int state) {
        if (!WorkService.isSeviceStart) {
            return;
        }
        if (state != 0) {
            return;
        }
        getAll = new GetAll(wsClient, this);
        getAll.subscribe();
        getAll.send();
    }

    //--------------------------------------------------------------------------------------------
    @Override
    public void allCallBack(int state, FGetAll fGetAll) {
        if (state != 0) {
            return;
        }
        if (!WorkService.isSeviceStart) {
            return;
        }
        countId = fGetAll.getData().size();
        if (countId == 0) return;
        listSchedules = fGetAll.getData();
        loadTask(listSchedules);
    }

    private void loadTask(List <FGetAllData> listTasks) {
        if (!WorkService.isSeviceStart) {
            return;
        }
        getTask = new GetTask(wsClient, this);
        getTask.subscribe();
        currId = 0;
        this.listTasks = new ArrayList <>();
        getTask.send(listTasks.get(currId).getTask());
    }

    //----------------
    @Override
    public void taskCallBack(int state, FGetTask fGetTask) {
        if (!WorkService.isSeviceStart) {
            return;
        }
        currId++;
        listTasks.add(fGetTask.getData());
        if (currId < countId) {//if not a task - returning for it
            getTask.send(listSchedules.get(currId).getTask());
        } else {
            JobToMerge job = new JobToMerge(listSchedules, listTasks, null);
            tranportMethods(wsClient);
            iLoadTaskCompleted.jobCallback(job);
        }
    }

    private void tranportMethods(WSClient wsClient) {
        if (!WorkService.isSeviceStart) {
            return;
        }
        refresh = new Refresh(wsClient);
        refreshAction = new RefreshAction();
        refresh.subscribe(refreshAction);
        sendMeasurement = new SendMeasurement(wsClient);
        sendMeasurement.subscribe(null);
    }
    //---------------------------
}
