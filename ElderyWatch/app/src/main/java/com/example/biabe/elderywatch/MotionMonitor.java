package com.example.biabe.elderywatch;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.text.format.DateUtils;
import android.util.Log;

import static android.content.Context.SENSOR_SERVICE;

class MotionSender implements Runnable
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

public class MotionMonitor implements SensorEventListener {

    private SensorManager mSensorManager;
    private static final String TAG = "SensorService";
    private Sensor AccelerometerSensor;
    private Sensor mHeartrateSensor;

    static int BUFF_SIZE=50;
    static public double[] window = new double[BUFF_SIZE];
    double sigma=0.5,th=10,th1=5,th2=2;
    public static String curr_state,prev_state;

    private int steps = 127;
    private long lastUpdate;

    private double pulse=60;


    public MotionMonitor(Context context)
    {
        mSensorManager = ((SensorManager) context.getSystemService(SENSOR_SERVICE));
        AccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //updateDisplayFromServer("0","0","0");
        if (AccelerometerSensor != null) {
            final int measurementDuration = 30;   // Seconds
            final int measurementBreak = 15;    // Seconds
            //Toast.makeText(this, "sensor not null",Toast.LENGTH_SHORT).show();
            //SensorEventListener listener = new TestSensorEventListener();
            System.out.println("register pulse sensor");
            mSensorManager.registerListener(this, AccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

        } else {
            Log.d(TAG, "No accelre Sensor found");
        }

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


        for(int i=0;i<BUFF_SIZE;i++){
            window[i]=0;
        }
        prev_state="none";
        curr_state="none";
        lastUpdate = System.currentTimeMillis();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(Sensor.TYPE_HEART_RATE == event.sensor.getType()) {
            pulse = event.values[0];
        }
        if(Sensor.TYPE_ACCELEROMETER == event.sensor.getType()) {
            //System.out.println("Accelerometer data obtained" + event.values[0] + " "+ event.values[1] + " "+ event.values[2] + " ");

            double ax, ay, az;

            ax = event.values[0];
            ay = event.values[1];
            az = event.values[2];
            AddData(ax, ay, az);
            posture_recognition(window, ay);

            if (curr_state.equals("fall")) {
                System.out.println(curr_state + " " + prev_state);
                HeartBeatSender sender = new HeartBeatSender();
                sender.setUrl("http://codeforgood.avramiancuturda.ro/addUser.php?Name=Ion&Contact=012&Problem=FALL&Priority=9&XLoc=0&YLoc=0");

                Thread thread = new Thread(sender);
                thread.start();
            }


            if (curr_state.equals("walking") && ((System.currentTimeMillis() - lastUpdate) / DateUtils.SECOND_IN_MILLIS) > 3) {
                steps++;
                HeartBeatSender sender = new HeartBeatSender();
                sender.setUrl("http://codeforgood.avramiancuturda.ro/addBoardEntry.php?Name=Ion&Contact=123&Steps=" + steps + "&Temperature="+ pulse);

                Thread thread = new Thread(sender);
                thread.start();

                lastUpdate = System.currentTimeMillis();
            }


            if (!prev_state.equalsIgnoreCase(curr_state)) {
                prev_state = curr_state;


            }
        }





        int a = 5;
        a++;
    }

    private void posture_recognition(double[] window2,double ay2) {
        // TODO Auto-generated method stub
        int zrc=compute_zrc(window2);
        if(zrc==0){

            if(Math.abs(ay2)<th1){
                curr_state="sitting";
            }else{
                curr_state="standing";
            }

        }else{

            if(zrc>th2){
                curr_state="walking";
            }else{
                curr_state="none";
            }

        }

        if(window2[window2.length - 1] - window2[window2.length - 2]> 5)
            System.out.println(window2[window2.length - 1] - window2[window2.length - 2]);

        if(window2[window2.length - 1] - window2[window2.length - 2]> 15)
        {
            curr_state = "fall";

        }

    }

    private int compute_zrc(double[] window2) {
        // TODO Auto-generated method stub
        int count=0;
        for(int i=1;i<=BUFF_SIZE-1;i++){

            if((window2[i]-th)<sigma && (window2[i-1]-th)>sigma){
                count=count+1;
            }

        }
        return count;
    }

    private void AddData(double ax2, double ay2, double az2) {
        double a_norm;

        a_norm = Math.sqrt(ax2 * ax2 + ay2 * ay2 + az2 * az2);
        for (int i = 0; i <= BUFF_SIZE - 2; i++) {
            window[i] = window[i + 1];
        }
        window[BUFF_SIZE - 1] = a_norm;
    }


        @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
