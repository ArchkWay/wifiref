package com.example.refactoringwnamqos.intefaces;

import java.net.HttpCookie;
import java.util.List;

public interface IWebCallBack1 {
    void callResponseFromServer(String data, List <HttpCookie> cookies);
}
