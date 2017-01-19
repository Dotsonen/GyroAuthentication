package com.example.adrian.gyroauth;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class YGraph extends AppCompatActivity {


    GraphView gvy;
    LineGraphSeries<DataPoint>  ySeries1, ySeries2,
            ySeries3, ySeries4, ySeries5;
    int duration;
    ArrayList<ArrayList<float[]>> mainList;
    ArrayList<float[]> yList;
    ArrayList<float[]> loginData;


    Graphs graphs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xgraphs);

        gvy = (GraphView) findViewById(R.id.gvx);

        mainList = (ArrayList<ArrayList<float[]>>) getIntent().getSerializableExtra("CALCULATED_DATA");
        duration = getIntent().getExtras().getInt("DURATION");
        loginData = (ArrayList<float[]>) getIntent().getSerializableExtra("LOGIN_DATA");

        graphs = new Graphs();

        setGraphData();

        if(loginData != null){
            setLoginIfExists();
        }
    }

    public void setGraphData() {

        gvy.getViewport().setXAxisBoundsManual(true);
        gvy.getViewport().setYAxisBoundsManual(true);
        gvy.getViewport().setMaxY(2);
        gvy.getViewport().setMinY(-2);
        gvy.getViewport().setMaxX(duration);
        gvy.getViewport().setMinX(1);
        gvy.setTitle("Roll");
        gvy.getGridLabelRenderer().setNumHorizontalLabels(2);

        yList = mainList.get(1);

        ySeries1 = graphs.createFullSeries(yList).get(0);
        ySeries2 = graphs.createFullSeries(yList).get(1);
        ySeries3 = graphs.createFullSeries(yList).get(2);
        ySeries4 = graphs.createFullSeries(yList).get(3);

        ySeries1.setColor(Color.BLUE);
        ySeries2.setColor(Color.BLUE);
        ySeries3.setColor(Color.RED);
        ySeries4.setColor(Color.YELLOW);

        gvy.addSeries(ySeries1);
        gvy.addSeries(ySeries2);
        gvy.addSeries(ySeries3);
        gvy.addSeries(ySeries4);
    }

    public void setLoginIfExists(){

        ySeries5 = graphs.createFullSeries(loginData).get(0);

        ySeries5.setColor(Color.GREEN);

        gvy.addSeries(ySeries5);
    }
}
