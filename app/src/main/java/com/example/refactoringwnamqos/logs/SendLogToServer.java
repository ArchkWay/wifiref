package com.example.refactoringwnamqos.logs;

import com.example.refactoringwnamqos.InfoAboutMe;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class SendLogToServer implements Runnable{

    private static final String FILE = "log.txt";
    private File file;

    public void upload(String url, File file) throws IOException {
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("text/plain"), file))
                .addFormDataPart("other_field", "other_field_value")
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();
        //Response response = this.client.newCall(request).execute();
    }

    @Override
    public void run() {
        file = new File(InfoAboutMe.context.getFilesDir(), FILE);
        if(file.exists()){
            //return true;
        }else{
            //return false;
        }
    }
}
