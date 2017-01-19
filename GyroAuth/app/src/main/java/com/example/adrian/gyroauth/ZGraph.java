package com.example.adrian.gyroauth;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class ZGraph extends AppCompatActivity {


    GraphView gvz;
    LineGraphSeries<DataPoint>  zSeries1, zSeries2, zSeries3, zSeries4, zSeries5;
    int duration;
    ArrayList<ArrayList<float[]>> mainList;
    ArrayList<float[]> zList;
    ArrayList<float[]> loginData;

    Graphs graphs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xgraphs);

        gvz = (GraphView) findViewById(R.id.gvx);

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

        gvz.getViewport().setXAxisBoundsManual(true);
        gvz.getViewport().setYAxisBoundsManual(true);
        gvz.getViewport().setMaxY(2);
        gvz.getViewport().setMinY(-2);
        gvz.getViewport().setMaxX(duration);
        gvz.getViewport().setMinX(1);
        gvz.setTitle("Yaw");
        gvz.getGridLabelRenderer().setNumHorizontalLabels(2);

        zList = mainList.get(2);

        zSeries1 = graphs.createFullSeries(zList).get(0);
        zSeries2 = graphs.createFullSeries(zList).get(1);
        zSeries3 = graphs.createFullSeries(zList).get(2);
        zSeries4 = graphs.createFullSeries(zList).get(3);

        zSeries1.setColor(Color.BLUE);
        zSeries2.setColor(Color.BLUE);
        zSeries3.setColor(Color.RED);
        zSeries4.setColor(Color.YELLOW);

        gvz.addSeries(zSeries1);
        gvz.addSeries(zSeries2);
        gvz.addSeries(zSeries3);
        gvz.addSeries(zSeries4);
    }

    public void setLoginIfExists(){

        zSeries5 = graphs.createFullSeries(loginData).get(0);

        zSeries5.setColor(Color.GREEN);

        gvz.addSeries(zSeries5);
    }
}
