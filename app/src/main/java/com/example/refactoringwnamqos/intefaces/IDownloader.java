package com.example.refactoringwnamqos.intefaces;

import com.example.refactoringwnamqos.enteties.modelJson.jMeasurement.jSendMeasurement.TCOMMAN_X_ID;

public interface IDownloader {
    void downloadEvent(TCOMMAN_X_ID tcomman_x_id, int state);
}
