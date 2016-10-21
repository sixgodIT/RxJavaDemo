package com.example.xzz.rxjava;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class BaseActivity extends AppCompatActivity {

    public static String tag = BaseActivity.class.getName();
    String logText = "";
    public TextView logTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();
        logTv = (TextView) findViewById(R.id.log_text);
        logTv.setMovementMethod(new ScrollingMovementMethod());
    }

    public boolean isCurrentlyOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public void log(String log) {
        if (isCurrentlyOnMainThread()) {
            logText += ("\n" + log + " (main thread) " + Thread.currentThread().getName() + "\n");
            logTv.setText(logText);
        } else {
            logText += ("\n" + log + " (NOT main thread) " + Thread.currentThread().getName() + "\n");
            // You can only do below stuff on main thread.
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    logTv.setText(logText);
                }
            });
        }

    }
}
