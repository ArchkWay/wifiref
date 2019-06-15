package com.example.refactoringwnamqos.enteties;

import com.google.gson.annotations.SerializedName;

public class LogItem {

    @SerializedName("date")
    private String mDate;
    private String finalDate;
    @SerializedName("caption")
    private String mCaption;
    @SerializedName("info")
    private String mInfo;

    public String getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(String finalDate) {
        this.finalDate = finalDate;
    }

    public LogItem(String caption, String info, String date) {
        mCaption = caption;
        mInfo = info;
        mDate = date;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getmCaption() {
        return mCaption;
    }

    public void setmCaption(String mCaption) {
        this.mCaption = mCaption;
    }

    public String getmInfo() {
        return mInfo;
    }

    public void setmInfo(String mInfo) {
        this.mInfo = mInfo;
    }
}
