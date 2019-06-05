package com.example.refactoringwnamqos.enteties.jGetTask;

public class FGetTask {

     private String sid;
     private int code;
     private FGetTaskData data;

     public FGetTask(String sid, int code, FGetTaskData data) {
          this.sid = sid;
          this.code = code;
          this.data = data;
     }

     public FGetTask(){}

     public String getSid() {
          return sid;
     }

     public void setSid(String sid) {
          this.sid = sid;
     }

     public int getCode() {
          return code;
     }

     public void setCode(int code) {
          this.code = code;
     }

     public FGetTaskData getData() {
          return data;
     }

     public void setData(FGetTaskData data) {
          this.data = data;
     }
}
