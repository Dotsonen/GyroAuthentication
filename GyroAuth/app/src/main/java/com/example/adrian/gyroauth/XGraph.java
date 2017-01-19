package com.example.adrian.gyroauth;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class XGraph extends AppCompatActivity {


    GraphView gvx;
    LineGraphSeries<DataPoint> xSeries1, xSeries2, xSeries3, xSeries4, xSeries5;
    int duration;
    ArrayList<ArrayList<float[]>> mainList;
    ArrayList<float[]> xList;
    ArrayList<float[]> loginData;

    Graphs graphs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xgraphs);

        gvx = (GraphView) findViewById(R.id.gvx);

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

        gvx.getViewport().setXAxisBoundsManual(true);
        gvx.getViewport().setYAxisBoundsManual(true);
        gvx.getViewport().setMaxY(2);
        gvx.getViewport().setMinY(-2);
        gvx.getViewport().setMaxX(duration);
        gvx.getViewport().setMinX(1);
        gvx.setTitle("Pitch");

        gvx.getGridLabelRenderer().setNumHorizontalLabels(2);


        xList = mainList.get(0);

        xSeries1 = graphs.createFullSeries(xList).get(0);
        xSeries2 = graphs.createFullSeries(xList).get(1);
        xSeries3 = graphs.createFullSeries(xList).get(2);
        xSeries4 = graphs.createFullSeries(xList).get(3);

        xSeries1.setColor(Color.BLUE);
        xSeries2.setColor(Color.BLUE);
        xSeries3.setColor(Color.RED);
        xSeries4.setColor(Color.YELLOW);

        gvx.addSeries(xSeries1);
        gvx.addSeries(xSeries2);
        gvx.addSeries(xSeries3);
        gvx.addSeries(xSeries4);
    }

    public void setLoginIfExists(){

        xSeries5 = graphs.createFullSeries(loginData).get(0);

        xSeries5.setColor(Color.GREEN);

        gvx.addSeries(xSeries5);
    }
}
