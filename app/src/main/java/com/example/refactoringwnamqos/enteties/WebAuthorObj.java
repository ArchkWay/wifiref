package com.example.refactoringwnamqos.enteties;

public class WebAuthorObj {
    private String tel;
    private String url_1;
    private String url_2;
    private String url_3;
    private String code;
    private int timeOut;
    private int step;
    StepTwoResponse stepTwoResponse;
    StepThreeResponse stepThreeResponse;
    StepFourResponse stepFourResponse;
    StepFinalResponse stepFinalResponse;
    StepPostFinalResponse stepPostFinalResponse;

    public StepPostFinalResponse getStepPostFinalResponse() {
        return stepPostFinalResponse;
    }

    public void setStepPostFinalResponse(StepPostFinalResponse stepPostFinalResponse) {
        this.stepPostFinalResponse = stepPostFinalResponse;
    }

    public StepFinalResponse getStepFinalResponse() {
        return stepFinalResponse;
    }

    public void setStepFinalResponse(StepFinalResponse stepFinalResponse) {
        this.stepFinalResponse = stepFinalResponse;
    }

    public StepFourResponse getStepFourResponse() {
        return stepFourResponse;
    }

    public void setStepFourResponse(StepFourResponse stepFourResponse) {
        this.stepFourResponse = stepFourResponse;
    }

    public StepThreeResponse getStepThreeResponse() {
        return stepThreeResponse;
    }

    public void setStepThreeResponse(StepThreeResponse stepThreeResponse) {
        this.stepThreeResponse = stepThreeResponse;
    }

    public StepTwoResponse getStepTwoResponse() {
        return stepTwoResponse;
    }

    public void setStepTwoResponse(StepTwoResponse stepTwoResponse) {
        this.stepTwoResponse = stepTwoResponse;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getUrl_1() {
        return url_1;
    }

    public void setUrl_1(String url_1) {
        this.url_1 = url_1;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public String getUrl_2() {
        return url_2;
    }

    public void setUrl_2(String url_2) {
        this.url_2 = url_2;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUrl_3() {
        return url_3;
    }

    public void setUrl_3(String url_3) {
        this.url_3 = url_3;
    }
}
