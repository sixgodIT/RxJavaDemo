package com.example.xzz.rxjava;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String tag = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);
        findViewById(R.id.button7).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button1:
                startActivity(new Intent(this, BasicActivity.class));
                break;
            case R.id.button2:
                startActivity(new Intent(this, MapActivity.class));
                break;
            case R.id.button3:
                startActivity(new Intent(this, FilterActivity.class));
                break;
            case R.id.button4:
                startActivity(new Intent(this, ZipActivity.class));
                break;
            case R.id.button5:
                startActivity(new Intent(this, SchedulerActivity.class));
                break;
            case R.id.button6:
                startActivity(new Intent(this, OtherActivity.class));
                break;
            case R.id.button7:
                startActivity(new Intent(this, ErrorActivity.class));
                break;
        }
    }
}