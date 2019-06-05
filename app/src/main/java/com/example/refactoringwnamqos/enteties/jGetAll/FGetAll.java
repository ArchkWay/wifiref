package com.example.refactoringwnamqos.enteties.jGetAll;

import java.util.List;

public class FGetAll {
    private int code;
    private List<FGetAllData> data;
    private String sid;

    public FGetAll(int code, List<FGetAllData> data, String sid) {
        this.code = code;
        this.data = data;
        this.sid = sid;
    }



    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<FGetAllData> getData() {
        return data;
    }

    public void setData(List<FGetAllData> data) {
        this.data = data;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

}
