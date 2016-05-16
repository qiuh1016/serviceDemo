package com.example.qiuhong.servicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class MsgService extends Service {

    public static final int MAX_PROGRESS = 100;
    public Boolean work = true;
    private int progress = 0;
    private OnProgressListener onProgressListener;

    public int getProgress() {
        return progress;
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public void startDownload() {
        Log.i("Main", "startDownload");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progress <= MAX_PROGRESS && work) {
                    Log.i("Main", progress + "");
                    progress += 5;

                    if (onProgressListener != null) {
                        onProgressListener.onProgress(progress);
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void stopDownload() {
        work = false;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MsgBinder();
    }

    public class MsgBinder extends Binder {
        public MsgService getService() {
            return MsgService.this;
        }
    }
}
