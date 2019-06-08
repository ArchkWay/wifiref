package com.example.refactoringwnamqos.enteties.modelJson.waring;

public class Param {
    private String par1;
    private String par2;

    public Param(String par1, String par2) {
        this.par1 = par1;
        this.par2 = par2;
    }

    public String getPar1() {
        return par1;
    }

    public void setPar1(String par1) {
        this.par1 = par1;
    }

    public String getPar2() {
        return par2;
    }

    public void setPar2(String par2) {
        this.par2 = par2;
    }
}
