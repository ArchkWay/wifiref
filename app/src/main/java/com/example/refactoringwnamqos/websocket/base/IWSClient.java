package com.example.refactoringwnamqos.websocket.base;

import ua.naiksoftware.stomp.StompClient;

public interface IWSClient {
    void iwsClientCallBack(StompClient mStompClient, int code);
}
