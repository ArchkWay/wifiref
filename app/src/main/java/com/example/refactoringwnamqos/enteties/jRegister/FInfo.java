package com.example.refactoringwnamqos.enteties.jRegister;

public class FInfo {

    private String id;
    private String uuid;
    private String phone;

    public FInfo(String id, String uuid, String phone) {
        this.id = id;
        this.uuid = uuid;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
