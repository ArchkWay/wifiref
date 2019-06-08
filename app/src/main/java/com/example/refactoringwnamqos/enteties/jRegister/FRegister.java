package com.example.refactoringwnamqos.enteties.jRegister;

public class FRegister {
    private int code;
    private FInfo data;
    private String sid;

    public FRegister(int code, FInfo data, String sid) {
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

    public FInfo getData() {
        return data;
    }

    public void setData(FInfo data) {
        this.data = data;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
