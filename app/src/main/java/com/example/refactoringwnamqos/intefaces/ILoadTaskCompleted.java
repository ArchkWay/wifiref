package com.example.refactoringwnamqos.intefaces;

import com.example.refactoringwnamqos.businessLogic.JobToMerge;

public interface ILoadTaskCompleted {
    void jobCallback(JobToMerge job);
}
