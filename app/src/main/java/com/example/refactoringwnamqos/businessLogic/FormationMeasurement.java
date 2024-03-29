package com.example.refactoringwnamqos.businessLogic;

import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.logs.WorkWithLog;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.enteties.jGetAll.FGetAllData;
import com.example.refactoringwnamqos.enteties.MeanObject;
import com.example.refactoringwnamqos.enteties.modelJson.jMeasurement.jSendMeasurement.TCOMMAN_X_ID;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class FormationMeasurement {

    /**
     * Create list of measurements from schedules (@link job.)and tasks
     */
    public void create(JobToMerge job) {

        List <MeanObject> meanObjectList;

        if (job == null) {
            Date date = new Date();
            AllInterface.iLog.addToLog(new LogItem("Критическая ошибка", "PlanWork->createMeasurement->job == null", String.valueOf(date)));
            return;
        }
        if (job.getListScheduls().size() != job.getListTasks().size()) {
            Date date = new Date();
            AllInterface.iLog.addToLog(new LogItem("Критическая ошибка", "PlanWork->createMeasurement->" + "job.getListScheduls().size() != job.getListTasks().size()", String.valueOf(date)));
            return;
        }

        for (int t = 0; t < job.getListTasks().size(); t++) {

            int pozTask = findTask(job.getListScheduls(), job.getListScheduls().get(t).getTask());

            if (pozTask == -1) {
                Date date = new Date();
                AllInterface.iLog.addToLog(new LogItem("Критическая ошибка", "PlanWork->createMeasurement->" + "Не найден Такс из скедулера", String.valueOf(date)));
                return;
            }

            job.getListTasks().get(t).setScheduleId(job.getListScheduls().get(pozTask).getId());
            job.getListTasks().get(t).setmInterval(Integer.valueOf(job.getListScheduls().get(pozTask).getSchedule()));

        }

        meanObjectList = new ArrayList <>();

        for (int w = 0; w < job.getListTasks().size(); w++) {
            MeanObject meanObject = new MeanObject();
            meanObject.setId(WorkWithLog.getTimeNow());
            meanObject.setTaskId(job.getListTasks().get(w).getId());
            meanObject.setScheduleId(job.getListTasks().get(w).getScheduleId());

            List <TCOMMAN_X_ID> list = new ArrayList <>();

            for (int e = 0; e < job.getListTasks().get(w).getCommands().size(); e++) {
                TCOMMAN_X_ID tcomman_x_id = new TCOMMAN_X_ID();
                tcomman_x_id.setCommandId(job.getListTasks().get(w).getCommands().get(e).getId());
                list.add(tcomman_x_id);
            }

            meanObject.setResults(list);
            meanObjectList.add(meanObject);
        }
        job.setMeanObjectList(meanObjectList);
    }

    private int findTask(List <FGetAllData> list, String task) {

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTask().equals(task)) return i;
        }
        return -1;
    }
}
