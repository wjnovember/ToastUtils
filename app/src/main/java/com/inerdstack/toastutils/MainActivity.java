package com.inerdstack.toastutils;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private int i = 0;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ToastUtils.makeText(MainActivity.this, "弹出窗口" + i, ToastUtils.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "弹出窗口" + i, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                i++;
                mHandler.sendEmptyMessage(0);
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, 0, 2000);
    }
}
