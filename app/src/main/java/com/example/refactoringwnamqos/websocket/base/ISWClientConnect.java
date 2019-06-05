package com.example.refactoringwnamqos.websocket.base;

public interface ISWClientConnect {
    void shutdown();
    void connectStomp();
    void setIwsClient(IWSClient iwsClient);
}
