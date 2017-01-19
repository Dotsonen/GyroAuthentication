package com.example.adrian.gyroauth;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    float fduration, trend, minmax;
    int duration;
    double ssex, ssey, ssez, loginssex, loginssey, loginssez, oob;
    Button loginBtn, graphBtn, xgraphBtn, ygraphBtn, zgraphBtn;
    TextView loginText, test1, test2, test3;
    private SensorManager sensorManager;
    private Sensor sensor;
    ArrayList<ArrayList<float[]>> calculatedData;
    ArrayList<float[]> loginData;

    private boolean minMaxStdTest, seriesTrendTest, sumSquareErrorTest;
    float[] loginX, loginY, loginZ, minX, minY, minZ, avgX, avgY, avgZ, maxX, maxY, maxZ, stdX, stdY, stdZ;

    float MINMAXSTD_FAULTS_ALLOWED, MINMAXSTD_OOB, TREND_FAULTS_ALLOWED,
            TREND_SENSITIVITY, SSE_BASE, SSE_STD_SCALING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        graphBtn = (Button) findViewById(R.id.goToGraph);
        xgraphBtn = (Button) findViewById(R.id.x_graph_button);
        ygraphBtn = (Button) findViewById(R.id.y_graph_button);
        zgraphBtn = (Button) findViewById(R.id.z_graph_button);
        loginText = (TextView) findViewById(R.id.loginText);
        test1 = (TextView) findViewById(R.id.test1);
        test2 = (TextView) findViewById(R.id.test2);
        test3 = (TextView) findViewById(R.id.test3);

        calculatedData = (ArrayList<ArrayList<float[]>>) getIntent().getSerializableExtra("CALCULATED_DATA");
        duration = getIntent().getExtras().getInt("DURATION");
        fduration = (float) duration;


                    MINMAXSTD_FAULTS_ALLOWED    =   fduration/2;        // fdur/3
                    MINMAXSTD_OOB               =   fduration/15;       // fdur/25
                    TREND_FAULTS_ALLOWED        =   fduration;          // fdur
                    TREND_SENSITIVITY           =   50f;                // 50f
                    SSE_BASE                    =   0.01f;              // 0.02f
                    SSE_STD_SCALING             =   0.45f;              // 0.15f
        

        putDataInArray();

        loginX = new float[duration];
        loginY = new float[duration];
        loginZ = new float[duration];

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent sIntent = new Intent(LoginActivity.this, SensorSeries.class);
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
                Intent i = new Intent(LoginActivity.this, Graphs.class);
                i.putExtra("CALCULATED_DATA", calculatedData);
                i.putExtra("DURATION", duration);
                loginData = new ArrayList<>();
                loginData.add(loginX);
                loginData.add(loginY);
                loginData.add(loginZ);
                i.putExtra("LOGIN_DATA", loginData);
                startActivity(i);
            }
        });

        xgraphBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, XGraph.class);
                i.putExtra("CALCULATED_DATA", calculatedData);
                i.putExtra("DURATION", duration);
                loginData = new ArrayList<>();
                loginData.add(loginX);
                i.putExtra("LOGIN_DATA", loginData);
                startActivity(i);
            }
        });

        ygraphBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, YGraph.class);
                i.putExtra("CALCULATED_DATA", calculatedData);
                i.putExtra("DURATION", duration);
                loginData = new ArrayList<>();
                loginData.add(loginY);
                i.putExtra("LOGIN_DATA", loginData);
                startActivity(i);
            }
        });

        zgraphBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, ZGraph.class);
                i.putExtra("CALCULATED_DATA", calculatedData);
                i.putExtra("DURATION", duration);
                loginData = new ArrayList<>();
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

                minMaxStdDevTest();
                seriesTrendTest();
                sumSquareErrorTest();

                if (minMaxStdTest) {
                    test1.setText("MinMaxStdDevTest PASSED!; " + "Faults: " + minmax + ", OOB: " + oob);
                } else {
                    test1.setText("MinMaxStdDevTest FAILED!; " + "Faults: " + minmax + ", OOB: " + oob);
                }

                if (seriesTrendTest) {
                    test2.setText("SeriesTrendTest PASSED!; " + "Faults: " + trend);
                } else {
                    test2.setText("SeriesTrendTest FAILED!; " + "Faults: " + trend);
                }

                if (sumSquareErrorTest) {
                    test3.setText("sumSquareErrorTest PASSED!; " +
                            "SSEs: \n" + ssex + " \n" + ssey + " \n" + ssez +
                            " \n" + "LoginSSE: " + " \n" + loginssex + " \n" + loginssey + " \n" + loginssez );
                } else {
                    test3.setText("sumSquareErrorTest FAILED!; " +
                            "SSEs: \n" + ssex + " \n" + ssey + " \n" + ssez +
                            " \n" + "LoginSSE: " + " \n" + loginssex + " \n" + loginssey + " \n" + loginssez );
                }

                if (minMaxStdTest && seriesTrendTest && sumSquareErrorTest){
                    loginText.setBackgroundColor(Color.GREEN);
                } else {
                    loginText.setBackgroundColor(Color.RED);
                }
            }
        }
    }

    public void putDataInArray(){
        minX = calculatedData.get(0).get(0);
        maxX = calculatedData.get(0).get(1);
        avgX = calculatedData.get(0).get(2);
        stdX = calculatedData.get(0).get(3);

        minY = calculatedData.get(1).get(0);
        maxY = calculatedData.get(1).get(1);
        avgY = calculatedData.get(1).get(2);
        stdY = calculatedData.get(1).get(3);

        minZ = calculatedData.get(2).get(0);
        maxZ = calculatedData.get(2).get(1);
        avgZ = calculatedData.get(2).get(2);
        stdZ = calculatedData.get(2).get(3);
    }

    public void minMaxStdDevTest(){
        float outOfBoundsValue = 0;
        float checkCountM = 0;
        float top, bot;
        for(int i = 0; i < duration; i++){
            if( (minX[i] > loginX[i] || maxX[i] < loginX[i]) &&
                    ((avgX[i] + stdX[i]*-2) > loginX[i] || (avgX[i] + stdX[i]*2) < loginX[i]) ){

                checkCountM += 1;
                top = Math.max(maxX[i], avgX[i] + stdX[i]*2);
                bot = Math.min(minX[i], avgX[i] + stdX[i]*-2);

                if (loginX[i] > top) {
                    outOfBoundsValue += Math.abs(top - loginX[i]);
                } else {
                    outOfBoundsValue += Math.abs(bot - loginX[i]);
                }
            }

            if( (minY[i] > loginY[i] || maxY[i] < loginY[i]) &&
                    ((avgY[i] + stdY[i]*-2) > loginY[i] || (avgY[i] + stdY[i]*2) < loginY[i]) ){

                checkCountM += 1;
                top = Math.max(maxY[i], avgY[i] + stdY[i]*2);
                bot = Math.min(minY[i], avgY[i] + stdY[i]*-2);

                if (loginY[i] > top) {
                    outOfBoundsValue += Math.abs(top - loginY[i]);
                } else {
                    outOfBoundsValue += Math.abs(bot - loginY[i]);
                }
            }

            if( (minZ[i] > loginZ[i] || maxZ[i] < loginZ[i]) &&
                    ((avgZ[i] + stdZ[i]*-2) > loginZ[i] || (avgZ[i] + stdZ[i]*2) < loginZ[i]) ){

                checkCountM += 1;
                top = Math.max(maxZ[i], avgZ[i] + stdZ[i]*2);
                bot = Math.min(minZ[i], avgZ[i] + stdZ[i]*-2);

                if (loginZ[i] > top) {
                    outOfBoundsValue += Math.abs(top - loginZ[i]);
                } else {
                    outOfBoundsValue += Math.abs(bot - loginZ[i]);
                }
            }
        }

        oob = outOfBoundsValue;
        minmax = checkCountM;

        if( (checkCountM <= MINMAXSTD_FAULTS_ALLOWED) && (outOfBoundsValue <= MINMAXSTD_OOB) ){
            minMaxStdTest = true;
        } else {
            minMaxStdTest = false;
        }
    }

    public void seriesTrendTest() {
        float checkCountS = 0;
        for (int i = 0; i < duration; i++) {
            float lastX = 0, lastY = 0, lastZ = 0,
                    lastXL = 0, lastYL = 0, lastZL = 0;

            if (i != 0) {
                lastX   = avgX[i - 1];
                lastY   = avgY[i - 1];
                lastZ   = avgZ[i - 1];
                lastXL  = loginX[i - 1];
                lastYL  = loginY[i - 1];
                lastZL  = loginZ[i - 1];
            }

            float diffX, diffY, diffZ, diffXL, diffYL, diffZL;
            diffX   = (float) Math.pow( (avgX[i] - lastX), 2);
            diffY   = (float) Math.pow( (avgY[i] - lastY), 2);
            diffZ   = (float) Math.pow( (avgZ[i] - lastZ), 2);
            diffXL  = (float) Math.pow( (loginX[i] - lastXL), 2);
            diffYL  = (float) Math.pow( (loginY[i] - lastYL), 2);
            diffZL  = (float) Math.pow( (loginZ[i] - lastZL), 2);

            if (diffXL > diffX*TREND_SENSITIVITY || diffXL < diffX/TREND_SENSITIVITY) {
                checkCountS += 1;
            }
            if (diffYL > diffY*TREND_SENSITIVITY || diffYL < diffY/TREND_SENSITIVITY) {
                checkCountS += 1;
            }
            if (diffZL > diffZ*TREND_SENSITIVITY || diffZL < diffZ/TREND_SENSITIVITY) {
                checkCountS += 1;
            }
        }

        trend = checkCountS;

        if(checkCountS <= TREND_FAULTS_ALLOWED) {
            seriesTrendTest = true;
        } else {
            seriesTrendTest = false;
        }
    }

    public void sumSquareErrorTest() {
        double sumX = 0, resultX, sumStandardX = 0,
                sumY = 0, resultY, sumStandardY = 0,
                sumZ = 0, resultZ, sumStandardZ = 0,
                SSE_XLIMIT, SSE_YLIMIT, SSE_ZLIMIT;

        for(int i = 0; i < duration; i++){
            sumX += Math.pow(avgX[i] - loginX[i], 2);
            sumY += Math.pow(avgY[i] - loginY[i], 2);
            sumZ += Math.pow(avgZ[i] - loginZ[i], 2);

            sumStandardX += stdX[i];
            sumStandardY += stdY[i];
            sumStandardZ += stdZ[i];
        }

        resultX = sumX / duration;
        resultY = sumY / duration;
        resultZ = sumZ / duration;

        SSE_XLIMIT = SSE_BASE + (sumStandardX / duration) * SSE_STD_SCALING;
        SSE_YLIMIT = SSE_BASE + (sumStandardY / duration) * SSE_STD_SCALING;
        SSE_ZLIMIT = SSE_BASE + (sumStandardZ / duration) * SSE_STD_SCALING;

        ssex = SSE_XLIMIT;
        ssey = SSE_YLIMIT;
        ssez = SSE_ZLIMIT;

        loginssex = resultX;
        loginssey = resultY;
        loginssez = resultZ;

        if(resultX > SSE_XLIMIT || resultY > SSE_YLIMIT || resultZ > SSE_ZLIMIT){
            sumSquareErrorTest = false;
        } else {
            sumSquareErrorTest = true;
        }

        //loginText.setText(resultX + " X " + resultY + " Y " + resultZ + " Z " +
          //              " Standard X "  + SSE_XLIMIT + " Y " + SSE_YLIMIT + " Z " + SSE_ZLIMIT
        //);
    }
}


