package com.example.refactoringwnamqos.intefaces;

import ua.naiksoftware.stomp.StompClient;

public interface IWSClient {
    void iwsClientCallBack(StompClient mStompClient, int code);
}
