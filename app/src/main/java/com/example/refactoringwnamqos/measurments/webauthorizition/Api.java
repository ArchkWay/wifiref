package com.example.refactoringwnamqos.measurments.webauthorizition;


import com.example.refactoringwnamqos.enteties.StepTwoResponse;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    @POST("cp/mikrotik")
    Call <Object> postStepTwo(@Body StepTwoResponse stepTwoResponse);
}

