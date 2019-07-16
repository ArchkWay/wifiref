package com.example.refactoringwnamqos.measurments;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.example.refactoringwnamqos.enteties.modelJson.jMeasurement.jSendMeasurement.TCOMMAN_X_ID;
import com.example.refactoringwnamqos.intefaces.AllInterface;
import com.example.refactoringwnamqos.InfoAboutMe;
import com.example.refactoringwnamqos.enteties.LogItem;
import com.example.refactoringwnamqos.intefaces.IDownloader;
import com.example.refactoringwnamqos.wifi.WF_permissions;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class Downloader {


    private IDownloader iDownloader;
    private int timeout;

    private Timer timerWDT;
    private TimerTask timerTaskWDT;
    TCOMMAN_X_ID tcomman_x_id;
    String url;
    long fileSize;
    public Downloader(IDownloader iDownloader, int timeout, TCOMMAN_X_ID tcomman_x_id) {
        this.iDownloader = iDownloader;
        this.timeout = timeout;
        this.tcomman_x_id = tcomman_x_id;
    }

    private boolean hasPermissions() {
        int res;
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        for (String perm : permissions) {
            res = InfoAboutMe.context.checkCallingOrSelfPermission(perm);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    public void start(String url) {
        this.url = url;
            DownloaderBackground downloaderBackground = new DownloaderBackground(iDownloader, tcomman_x_id, timerWDT, timerTaskWDT, timeout);
            downloaderBackground.doInBackground();
    }


    private class DownloaderBackground extends AsyncTask <Void, Void, Void> {
        private long downloadId;
        private IDownloader iDownloader;
        private int timeout;

        private Timer timerWDT;
        private TimerTask timerTaskWDT;
        TCOMMAN_X_ID tcomman_x_id;

        private DownloaderBackground(IDownloader iDownloader, TCOMMAN_X_ID tcomman_x_id, Timer timerWDT, TimerTask timerTaskWDT, int timeout) {
            this.iDownloader = iDownloader;
            this.tcomman_x_id = tcomman_x_id;
            this.timerWDT = timerWDT;
            this.timerTaskWDT = timerTaskWDT;
            this.timeout = timeout;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (timerWDT != null) {
                timerWDT.cancel();
                timerWDT = null;
            }

            timerWDT = new Timer();
            timerTaskWDT = new WDTimer();
            timerWDT.schedule(timerTaskWDT, timeout * 1_000);

            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            InfoAboutMe.context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    long broadcastedDownloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                    if (broadcastedDownloadID == downloadId) {
                        if (getDownLoadStatus() == DownloadManager.STATUS_SUCCESSFUL) {
                            if (timerWDT != null) timerWDT.cancel();
                            iDownloader.downloadEvent(tcomman_x_id, 0);
                            Date date = new Date();
                            AllInterface.iLog.addToLog(new LogItem("Downloader", "Удачно", String.valueOf(date)));
                            Toast.makeText(InfoAboutMe.context, "Download complete", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(InfoAboutMe.context, "Download not complete", Toast.LENGTH_SHORT).show();
                            Date date = new Date();
                            AllInterface.iLog.addToLog(new LogItem("Downloader", "Не получилось", String.valueOf(date)));
                            if (timerWDT != null) timerWDT.cancel();
                            iDownloader.downloadEvent(tcomman_x_id, 1);
                        }
                    }
                }
            }, filter);


            Uri uri = Uri.parse(url);
            hasPermissions();
            if (hasPermissions()) {
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setDestinationInExternalPublicDir("/menesured", "sss.jpg");
                DownloadManager downloadManager = (DownloadManager) InfoAboutMe.context.getSystemService(Context.DOWNLOAD_SERVICE);
                downloadId = downloadManager.enqueue(request);
            } else {
                Intent intent = new Intent(InfoAboutMe.context, WF_permissions.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                InfoAboutMe.context.startActivity(intent);
                Date date = new Date();
                AllInterface.iLog.addToLog(new LogItem("Downloader", "Нет пермишина для работы с памятью", String.valueOf(date)));
            }
            File file = new File(Environment.getExternalStorageDirectory() + "/menesured", "sss.jpg");
            fileSize = file.length();
            tcomman_x_id.setOutput(fileSize);
            deleteFile();
            return null;
        }

        private int getDownLoadStatus() {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);

            DownloadManager downloadManager = (DownloadManager) InfoAboutMe.context.getSystemService(Context.DOWNLOAD_SERVICE);
            Cursor cursor = downloadManager.query(query);

            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int status = cursor.getInt(columnIndex);

                return status;
            }
            cursor.close();

            return DownloadManager.ERROR_UNKNOWN;

        }


        public boolean deleteFile() {
            File file = new File(Environment.getExternalStorageDirectory() + "/menesured", "sss.jpg");

            if (file.exists()) {
                file.delete();
                //Toast.makeText(InfoAboutMe.context, "File find and delete", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                //Toast.makeText(InfoAboutMe.context, "File not delete", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

    }

    class WDTimer extends TimerTask {

        @Override
        public void run() {
            cancelDownload();
//            iDownloader.downloadEvent(tcomman_x_id,2);
            Date date = new Date();
            AllInterface.iLog.addToLog(new LogItem("Downloader файла->WDTimer","run()",  String.valueOf(date)));
        }

        public void cancelDownload() {
            DownloadManager downloadManager = (DownloadManager) InfoAboutMe.context.getSystemService(Context.DOWNLOAD_SERVICE);
        }
    }

}
