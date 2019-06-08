package com.example.refactoringwnamqos.intefaces;

public interface ISWClientConnect {
    void shutdown();
    void connectStomp();
    void setIwsClient(IWSClient iwsClient);
}
