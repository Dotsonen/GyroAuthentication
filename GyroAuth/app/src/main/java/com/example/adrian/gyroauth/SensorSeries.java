package com.example.adrian.gyroauth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class SensorSeries extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private android.hardware.Sensor mSensor;

    int duration, time, durationLeft, from, fromTag;
    boolean go = false;
    float[] x, y ,z;

    TextView tv;
    Button bt;
    ProgressBar pb;
    MediaPlayer mp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

         mp = MediaPlayer.create(this, R.raw.beep_pcm);

        fromTag = getIntent().getExtras().getInt("ACTIVITY_TAG");

        if(fromTag == 1) {
            from = 1;
        } else {
            from = 2;
        }

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);

        duration = getIntent().getExtras().getInt("DURATION");
        durationLeft = duration;

        x = new float[duration];
        y = new float[duration];
        z = new float[duration];

        pb = (ProgressBar) findViewById(R.id.pb);
        pb.setMax(duration);

        bt = (Button) findViewById(R.id.bt);
        bt.setText("Start measuring");


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt.setVisibility(View.INVISIBLE);
                CountDownTimer countDownTimer = new CountDownTimer(3000, 100) {
                    boolean startOnce = false;


                    @Override
                    public void onTick(long millisUntilFinished) {
                        if ((millisUntilFinished > 2900 && millisUntilFinished < 3010) ||
                                (millisUntilFinished > 1900 && millisUntilFinished < 2100) ||
                                (millisUntilFinished > 900 && millisUntilFinished < 1100)) {

                            if (!mp.isPlaying()) mp.start();
                            if (!startOnce && (millisUntilFinished > 900 && millisUntilFinished < 1100)) {
                                mSensorManager.registerListener(SensorSeries.this,
                                        mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR),
                                        SensorManager.SENSOR_DELAY_GAME);
                                startOnce = true;
                            }

                        }

                        if ((2400 < millisUntilFinished && millisUntilFinished < 2600) ||
                                (1400 < millisUntilFinished && millisUntilFinished < 1600) ||
                                (400 < millisUntilFinished && millisUntilFinished < 600)){

                            if(mp.isPlaying()){

                                mp.stop();
                                try {
                                    mp.prepare();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                    @Override
                    public void onFinish() {
                        mp.start();
                        go = true;
                    }
                }.start();
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR &&
                go == true && durationLeft > 0) {

            time = duration - durationLeft;

            x[time] = event.values[0];
            y[time] = event.values[1];
            z[time] = event.values[2];

            durationLeft--;
            time = duration - durationLeft;

            pb.setProgress(time);

            if (durationLeft == 0) {
                mp.stop();
                go = false;
                bt.setVisibility(View.VISIBLE);
                sendBack();
            }
        }
    }

    void sendBack() {
        Intent toMeasurements = new Intent(SensorSeries.this, Measurements.class);
        Intent toLogin = new Intent(SensorSeries.this, LoginActivity.class);

        ArrayList<float[]> aL = new ArrayList<float[]>();
        aL.add(x);
        aL.add(y);
        aL.add(z);
        ArrayList<float[]> aLF = fixListRollOver(aL);

        if (from == 1){
            toMeasurements.putExtra("XYZ_DATA", aL);
            setResult(Activity.RESULT_OK, toMeasurements);
        } else {
            toLogin.putExtra("XYZ_DATA", aL);
            setResult(Activity.RESULT_OK, toLogin);
        }
        finish();
    }

    public ArrayList<float[]> fixListRollOver(ArrayList<float[]> list) {
        float last;
        float current;
        float addValue = 0;
        for(int i = 0; i < list.size(); i++){
            for(int j = 0; j < list.get(i).length; j++){
                if (j != 0) {
                    last    = list.get(i)[j-1];
                    current = list.get(i)[j];

                    if ((current > 0.5 && current <= 1) && (last < -0.5)){
                        addValue = (current - 1) - (last + 1);
                        list.get(i)[j] = last + addValue;
                    }
                    if ((current < -0.5 && current >= -1) && (last > 0.5)){
                        addValue = (1 + current) + (1 - last);
                        list.get(i)[j] = last + addValue;
                    }
                    if ((current > 0 && current <= 0.5) && (last < -1)){
                        addValue = (current - 0.5f) - (last + 1.5f);
                        list.get(i)[j] = last + addValue;
                    }
                    if ((current < 0 && current >= -0.5) && (last > 1)){
                        addValue = (0.5f + current) + (1.5f - last);
                        list.get(i)[j] = last + addValue;
                    }
                    if ((current > -0.5 && current <= 0) && (last < -1.5)){
                        addValue = (current) - (last + 2);
                        list.get(i)[j] = last + addValue;
                    }
                    if ((current < 0.5 && current >= 0) && (last > 1.5)){
                        addValue = (current) + (2 - last);
                        list.get(i)[j] = last + addValue;
                    }

                    addValue = 0;
                }
            }
        }
        return list;
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
        /*if (sensor == mSensor) {
            tv.setText("Accuracy: " + accuracy);
        }*/
    }
}
