package com.example.biabe.elderywatch;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.text.style.TtsSpan;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends WearableActivity {

    private TextView mTextView;
    private HeartBeatMonitor _heartBeat;
    private MotionMonitor _motion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);
        //_heartBeat = new HeartBeatMonitor(this);
        _motion = new MotionMonitor(this);

        // Enables Always-on
        setAmbientEnabled();
    }

    public void ButtonClicked(View w)
    {
        int a = 5;
        a++;

        Thread thread = new Thread() {
            @Override
            public void run() {
                HttpClient client = new HttpClient("");
                String test = client.doInBackground("");
            }
        };

        thread.start();


    }
}
