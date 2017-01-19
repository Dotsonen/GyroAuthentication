package com.example.adrian.gyroauth;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MergeClass extends AppCompatActivity {

    int duration, durationLeft, time;
    Button loginBtn, graphBtn;
    TextView loginText;
    private SensorManager sensorManager;
    private Sensor sensor;
    ArrayList<ArrayList<float[]>> calculatedData = new ArrayList<ArrayList<float[]>>();
    boolean print;

    final double SSE_LIMIT = 0.03;

    float[] loginX, loginY, loginZ, minX, minY, minZ, avgX, avgY, avgZ, maxX, maxY, maxZ, stdX, stdY, stdZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        graphBtn = (Button) findViewById(R.id.goToGraph);
        loginText = (TextView) findViewById(R.id.loginText);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        calculatedData = (ArrayList<ArrayList<float[]>>) getIntent().getSerializableExtra("CALCULATED_DATA");
        duration = getIntent().getExtras().getInt("DURATION");

        putDataInArray();

        loginX = new float[duration];
        loginY = new float[duration];
        loginZ = new float[duration];

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent sIntent = new Intent(MergeClass.this, SensorSeries.class);
                    sIntent.putExtra("ACTIVITY_TAG", 2);
                    sIntent.putExtra("DURATION", duration);
                    startActivityForResult(sIntent, 2);
                } catch ( ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        graphBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MergeClass.this, Graphs.class);
                i.putExtra("CALCULATED_DATA", calculatedData);
                i.putExtra("DURATION", duration);
                ArrayList<float[]> loginData = new ArrayList<>();
                loginData.add(loginX);
                loginData.add(loginY);
                loginData.add(loginZ);
                i.putExtra("LOGIN_DATA", loginData);
                startActivity(i);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2){
            if(resultCode == RESULT_OK){

                ArrayList<float[]> fromSensor = new ArrayList<float[]>();
                fromSensor = (ArrayList<float[]>) data.getSerializableExtra("XYZ_DATA");

                loginX = fromSensor.get(0);
                loginY = fromSensor.get(1);
                loginZ = fromSensor.get(2);

                isDataCorrect();
            }
        }

    }



    public void putDataInArray(){

        minX = calculatedData.get(0).get(0);
        maxX = calculatedData.get(0).get(1);
        avgX = calculatedData.get(0).get(2);

        minY = calculatedData.get(1).get(0);
        maxY = calculatedData.get(1).get(1);
        avgY = calculatedData.get(1).get(2);

        minZ = calculatedData.get(2).get(0);
        maxZ = calculatedData.get(2).get(1);
        avgZ = calculatedData.get(2).get(2);
    }


    public void isDataCorrect(){

        int checkCount = 0;

        boolean check = true;
        for(int i = 0; i < duration; i++){

            if(minX[i] > loginX[i] || maxX[i] < loginX[i]){
                check = false;
                checkCount ++;
            }

            if(minY[i] > loginY[i] || maxY[i] < loginY[i]){
                check = false;
                checkCount ++;
            }

            if(minZ[i] > loginZ[i] || maxZ[i] < loginZ[i]){
                check = false;
                checkCount ++;
            }
        }
        if(check){
            loginText.setBackgroundColor(Color.GREEN);
        }
        else{
            loginText.setBackgroundColor(Color.RED);
        }


    }

    public void sumTest(){

        double sumX = 0, resultX,
                sumY = 0, resultY,
                sumZ = 0, resultZ;

        for(int i = 0; i < duration; i++){

            sumX += Math.pow(avgX[i] - loginX[i], 2);
            sumY += Math.pow(avgY[i] - loginY[i], 2);
            sumZ += Math.pow(avgZ[i] - loginZ[i], 2);
        }

        resultX = sumX / duration;
        resultY = sumY / duration;
        resultZ = sumZ / duration;

        if(resultX > SSE_LIMIT || resultY > SSE_LIMIT || resultZ > SSE_LIMIT){
            loginText.setBackgroundColor(Color.RED);
        }else{
            loginText.setBackgroundColor(Color.GREEN);
        }

        loginText.setText(resultX + " X " + resultY + " Y " + resultZ + " Z ");
    }
}
