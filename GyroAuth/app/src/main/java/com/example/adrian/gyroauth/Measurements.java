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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Measurements extends AppCompatActivity {

    TextView tv;
    Button btMeasure;
    Button btComplete, loadButton;

            // Measuring duration (number of sensor uses)
            final int MEASUREMENT_DURATION = 100;

            // Number of measurements
            final int NUMBER_OF_MEASUREMENTS = 20;

    int measurementsLeft = NUMBER_OF_MEASUREMENTS;

    ArrayList<float[]> measurementPackage = new ArrayList<float[]>();
    ArrayList<ArrayList<float[]>> calculatedData = new ArrayList<ArrayList<float[]>>();
    ArrayList<float[]> calculatedX = new ArrayList<>();
    ArrayList<float[]> calculatedY = new ArrayList<>();
    ArrayList<float[]> calculatedZ = new ArrayList<>();

    float[] minX = new float[MEASUREMENT_DURATION],
            maxX = new float[MEASUREMENT_DURATION],
            avgX = new float[MEASUREMENT_DURATION],
            stdX = new float[MEASUREMENT_DURATION],
            minY = new float[MEASUREMENT_DURATION],
            maxY = new float[MEASUREMENT_DURATION],
            avgY = new float[MEASUREMENT_DURATION],
            stdY = new float[MEASUREMENT_DURATION],
            maxZ = new float[MEASUREMENT_DURATION],
            minZ = new float[MEASUREMENT_DURATION],
            avgZ = new float[MEASUREMENT_DURATION],
            stdZ = new float[MEASUREMENT_DURATION];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotation_vector);

        tv = (TextView) findViewById(R.id.tv);
        tv.setText("Measurements: " + NUMBER_OF_MEASUREMENTS +
                "\nDuration: " + MEASUREMENT_DURATION / 50 + "s" +
                "\nMeasurements left: " + measurementsLeft);

        btMeasure = (Button) findViewById(R.id.bt);
        loadButton = (Button) findViewById(R.id.loadButton);

        btMeasure.setText("Go to measurement");

        btComplete = (Button) findViewById(R.id.btc);
        btComplete.setVisibility(View.INVISIBLE);
        btComplete.setText("Complete test");

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int test, counter = 0;
                char temp = 'a';
                float epic = 0;
                String result = "";
                try {
                    FileInputStream fs = openFileInput("Profile.txt");
                    DataInputStream dis = new DataInputStream(fs);
                    while (dis.available() > 0) {

                        epic = dis.readFloat();
                               if (counter < MEASUREMENT_DURATION) {
                                 minX[counter] = epic;
                        } else if (counter < MEASUREMENT_DURATION*2) {
                            maxX[counter - MEASUREMENT_DURATION] = epic;
                        } else if (counter < MEASUREMENT_DURATION*3) {
                            avgX[counter - MEASUREMENT_DURATION*2] = epic;
                        } else if(counter < MEASUREMENT_DURATION*4){
                            stdX[counter - MEASUREMENT_DURATION*3] = epic;
                        } else if(counter < MEASUREMENT_DURATION*5){
                            minY[counter - MEASUREMENT_DURATION*4] = epic;
                        } else if(counter < MEASUREMENT_DURATION*6){
                            maxY[counter - MEASUREMENT_DURATION*5] = epic;
                        } else if(counter < MEASUREMENT_DURATION*7){
                            avgY[counter - MEASUREMENT_DURATION*6] = epic;
                        } else if(counter < MEASUREMENT_DURATION*8){
                            stdY[counter - MEASUREMENT_DURATION*7] = epic;
                        } else if(counter < MEASUREMENT_DURATION*9){
                            minZ[counter - MEASUREMENT_DURATION*8] = epic;
                        } else if(counter < MEASUREMENT_DURATION*10){
                            maxZ[counter - MEASUREMENT_DURATION*9] = epic;
                        } else if(counter < MEASUREMENT_DURATION*11){
                            avgZ[counter - MEASUREMENT_DURATION*10] = epic;
                        } else if(counter < MEASUREMENT_DURATION*12){
                            stdZ[counter - MEASUREMENT_DURATION*11] = epic;

                    }


                    counter++;


                         }


             //       loadButton.setText(Float.toString(minX[2]));
                    calculatedX.add(minX);
                    calculatedX.add(maxX);
                    calculatedX.add(avgX);
                    calculatedX.add(stdX);

                    calculatedY.add(minY);
                    calculatedY.add(maxY);
                    calculatedY.add(avgY);
                    calculatedY.add(stdY);

                    calculatedZ.add(minZ);
                    calculatedZ.add(maxZ);
                    calculatedZ.add(avgZ);
                    calculatedZ.add(stdZ);

                    calculatedData.add(calculatedX);
                    calculatedData.add(calculatedY);
                    calculatedData.add(calculatedZ);

                    Intent i = new Intent(Measurements.this, LoginActivity.class);
                    i.putExtra("CALCULATED_DATA", calculatedData);
                    i.putExtra("DURATION", MEASUREMENT_DURATION);
                    startActivity(i);


                } catch (IOException e) {
                    loadButton.setBackgroundColor(Color.RED);
                    loadButton.setText(e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        btMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btMeasure.setVisibility(View.INVISIBLE);
                try {
                    Intent sIntent = new Intent(Measurements.this, SensorSeries.class);
                    sIntent.putExtra("ACTIVITY_TAG", 1);
                    sIntent.putExtra("DURATION", MEASUREMENT_DURATION);
                    startActivityForResult(sIntent, 1);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });

        btComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calcIntent = new Intent(Measurements.this, CalculatorActivity.class);
                calcIntent.putExtra("PACKAGE", measurementPackage);
                calcIntent.putExtra("DURATION", MEASUREMENT_DURATION);
                startActivity(calcIntent);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                measurementsLeft--;

                tv.setText("Measurements: " + NUMBER_OF_MEASUREMENTS +
                        "\nDuration: " + MEASUREMENT_DURATION +
                        "\nMeasurements left: " + measurementsLeft);

                ArrayList<float[]> fromSensor;
                fromSensor = (ArrayList<float[]>) data.getSerializableExtra("XYZ_DATA");
                measurementPackage.add(fromSensor.get(0));
                measurementPackage.add(fromSensor.get(1));
                measurementPackage.add(fromSensor.get(2));

                if(measurementsLeft == 0) {
                    btComplete.setVisibility(View.VISIBLE);
                } else {
                    Intent sIntent = new Intent(Measurements.this, SensorSeries.class);
                    sIntent.putExtra("ACTIVITY_TAG", 1);
                    sIntent.putExtra("DURATION", MEASUREMENT_DURATION);
                    startActivityForResult(sIntent, 1);
                }
            }
        }
    }
}
