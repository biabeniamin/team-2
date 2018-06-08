package com.example.biabe.elderywatch;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.SENSOR_SERVICE;

class HeartBeatSender implements Runnable
{
    private String url;

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void run() {
        HttpClient client = new HttpClient(url);
        System.out.println(client.doInBackground(""));
    }
}

public class HeartBeatMonitor implements SensorEventListener {

    private SensorManager mSensorManager;
    private static final String TAG = "SensorService";
    private Sensor mHeartrateSensor;


    public HeartBeatMonitor(Context context)
    {
        mSensorManager = ((SensorManager) context.getSystemService(SENSOR_SERVICE));
        mHeartrateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        //updateDisplayFromServer("0","0","0");
        if (mHeartrateSensor != null) {
            final int measurementDuration = 30;   // Seconds
            final int measurementBreak = 15;    // Seconds
            //Toast.makeText(this, "sensor not null",Toast.LENGTH_SHORT).show();
            //SensorEventListener listener = new TestSensorEventListener();
            System.out.println("register pulse sensor");
            mSensorManager.registerListener(this, mHeartrateSensor, SensorManager.SENSOR_DELAY_NORMAL);

        } else {
            Log.d(TAG, "No Heartrate Sensor found");
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        HeartBeatSender sender = new HeartBeatSender();
        sender.setUrl("http://192.168.0.108/index.php?pulse=" + event.values[0]);

        Thread thread = new Thread(sender);

        thread.start();


        int a = 5;
        a++;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
