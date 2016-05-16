package com.example.qiuhong.servicedemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ServiceConfigurationError;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MsgService msgService = new MsgService();
    private int progress = 0;
    private ProgressBar progressBar;
    private Button button;
    private TextView textView;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            msgService = ((MsgService.MsgBinder)service).getService();
            msgService.setOnProgressListener(new OnProgressListener() {
                @Override
                public void onProgress(int progress) {
                    progressBar.setProgress(progress);

                    if (progress == 20) {
                        msgService.stopDownload();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText("20");

                            }
                        });

                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Service Demo");
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
        textView = (TextView) findViewById(R.id.textView);


        Intent intent = new Intent();
        intent.setAction("com.example.qiuhong.MSG_ACTION");
        intent.setPackage(getPackageName());
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                msgService.startDownload();
//                listenProgress();
                break;
        }
    }

    public void listenProgress() {

        progress = msgService.getProgress();
        progressBar.setProgress(progress);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progress < msgService.MAX_PROGRESS) {
                    Log.i("Main", "get progress" + progress);
                    progress = msgService.getProgress();
                    progressBar.setProgress(progress);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }


}
