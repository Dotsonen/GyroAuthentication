package com.example.adrian.gyroauth;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CalculatorActivity extends AppCompatActivity {

    private ArrayList<float[]> mainList;

    ArrayList<float[]> xList = new ArrayList<float[]>();
    ArrayList<float[]> yList = new ArrayList<float[]>();
    ArrayList<float[]> zList = new ArrayList<float[]>();

    ArrayList<ArrayList<float[]>> calculatedData = new ArrayList<ArrayList<float[]>>();

    ArrayList<float[]> calculatedX = new ArrayList<>();
    ArrayList<float[]> calculatedY = new ArrayList<>();
    ArrayList<float[]> calculatedZ = new ArrayList<>();

    float[] minX, avgX, maxX, stdX,
            minY, avgY, maxY, stdY,
            minZ, avgZ, maxZ, stdZ;
    int duration;
    TextView tv;
    Button button, loginBtn, saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        tv = (TextView) findViewById(R.id.dataText);
        button = (Button) findViewById(R.id.button);
        loginBtn = (Button) findViewById(R.id.goToLoginBtn);
        saveBtn = (Button) findViewById(R.id.saveButton);

        mainList = (ArrayList<float[]>) getIntent().getSerializableExtra("PACKAGE");
        duration = getIntent().getExtras().getInt("DURATION");

        splitMainlist();
        addCalculatedData();

        tv.setText("Calculations completed!");


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    String mega = Arrays.toString(minX);
               /*     mega += Arrays.toString(minX);
                    mega += Arrays.toString(avgX);
                    mega += Arrays.toString(stdX);
                    mega += Arrays.toString(minY);
                    mega += Arrays.toString(avgY);
                    mega += Arrays.toString(stdY);
                    mega += Arrays.toString(minZ);
                    mega += Arrays.toString(maxZ);
                    mega += Arrays.toString(avgZ);
                    mega += Arrays.toString(stdZ);

*/
                    FileOutputStream fos = openFileOutput("Profile.txt", MODE_PRIVATE);
                    DataOutputStream dos = new DataOutputStream(fos);
 /*                   os.write(Arrays.toString(minX));
                    os.write(Arrays.toString(maxX));
                    os.write(Arrays.toString(avgX));
                    os.write(Arrays.toString(stdX));
                    os.write(Arrays.toString(minY));
                    os.write(Arrays.toString(avgY));
                    os.write(Arrays.toString(stdY));
                    os.write(Arrays.toString(minZ));
                    os.write(Arrays.toString(maxZ));
                    os.write(Arrays.toString(avgZ));
                    os.write(Arrays.toString(stdZ));
*/

                //    os.write(mega);
                //    os.flush();
                    saveData(dos);
                    dos.flush();
                    dos.close();

                    saveBtn.setText(Arrays.toString(minX));

                } catch(Exception ex) {
                    ex.printStackTrace();
                    saveBtn.setText(ex.getMessage());
                }

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CalculatorActivity.this, Graphs.class);
                i.putExtra("CALCULATED_DATA", calculatedData);
                i.putExtra("DURATION", duration);
                startActivity(i);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CalculatorActivity.this, LoginActivity.class);
                i.putExtra("CALCULATED_DATA", calculatedData);
                i.putExtra("DURATION", duration);
                startActivity(i);
            }
        });
    }

    /**
     * Split the saved data into separated list for each axis
     */
    public void splitMainlist(){
        for(int i = 0; i < mainList.size(); i+=3) {
            xList.add(mainList.get(i));
            yList.add(mainList.get(i + 1));
            zList.add(mainList.get(i + 2));
        }
    }

    /**
     *
     * @param list
     * @return
     */
    public float[] getMinArray(ArrayList<float[]> list){
        float[] min = new float[list.get(0).length];

        for(int i = 0; i < list.size(); i++){
            for(int j = 0; j < list.get(i).length; j++){

                if(i == 0) {
                    min[j] = list.get(i)[j];
                }
                else if(min[j] > list.get(i)[j]) {
                    min[j] = list.get(i)[j];
                }
            }
        }
        return min;
    }

    public float[] getMaxArray(ArrayList<float[]> list){
        float[] max = new float[list.get(0).length];

        for(int i = 0; i < list.size(); i++){
            for(int j = 0; j < list.get(i).length; j++){

                if(i == 0) {
                    max[j] = list.get(i)[j];
                }
                else if(max[j] < list.get(i)[j]) {
                    max[j] = list.get(i)[j];
                }
            }
        }
        return max;
    }

    public float[] getAverageArray(ArrayList<float[]> list){
        float[] avg = new float[list.get(0).length];
        float temp = 0;

        for(int i = 0; i < list.get(0).length; i++){
            for(int j = 0; j < list.size(); j++){
                temp += list.get(j)[i];
            }

            avg[i] = temp/list.size();
            temp = 0;

        }

        return avg;
    }

    public float[] getStdArray(ArrayList<float[]> list){
        float[] std = new float[list.get(0).length];

        for(int i = 0; i < list.get(0).length; i++){
            float[] temp = new float[list.size()];

            for(int j = 0; j < list.size(); j++){
                temp[j] = list.get(j)[i];
            }

            Statistics staTEMP = new Statistics(temp);
            float staSTD = staTEMP.getStdDev();
            std[i] = staSTD;
        }
        return std;
    }

    public void addCalculatedData(){

        minX = getMinArray(xList);
        maxX = getMaxArray(xList);
        avgX = getAverageArray(xList);
        stdX = getStdArray(xList);

        minY = getMinArray(yList);
        maxY = getMaxArray(yList);
        avgY = getAverageArray(yList);
        stdY = getStdArray(yList);

        minZ = getMinArray(zList);
        maxZ = getMaxArray(zList);
        avgZ = getAverageArray(zList);
        stdZ = getStdArray(zList);

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

    }

    public void saveData (DataOutputStream dos){
        try {
            for (int i = 0; i < duration; i++) {
                dos.writeFloat(minX[i]);
                dos.flush();
            }
            for (int i = 0; i < duration; i++) {
                dos.writeFloat(maxX[i]);
                dos.flush();
            }
            for (int i = 0; i < duration; i++) {
                dos.writeFloat(avgX[i]);
            }
            for (int i = 0; i < duration; i++) {
                dos.writeFloat(stdX[i]);
            }
            for (int i = 0; i < duration; i++) {
                dos.writeFloat(minY[i]);
            }
            for (int i = 0; i < duration; i++) {
                dos.writeFloat(maxY[i]);
            }
            for (int i = 0; i < duration; i++) {
                dos.writeFloat(avgY[i]);
            }
            for (int i = 0; i < duration; i++) {
                dos.writeFloat(stdY[i]);
            }
            for (int i = 0; i < duration; i++) {
                dos.writeFloat(maxZ[i]);
            }
            for (int i = 0; i < duration; i++) {
                dos.writeFloat(minZ[i]);
            }
            for (int i = 0; i < duration; i++) {
                dos.writeFloat(avgZ[i]);
            }
            for (int i = 0; i < duration; i++) {
                dos.writeFloat(stdZ[i]);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
