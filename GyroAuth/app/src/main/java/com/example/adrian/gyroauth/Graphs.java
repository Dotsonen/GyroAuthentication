package com.example.adrian.gyroauth;

import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Graphs extends AppCompatActivity {

    GraphView gv1, gv2, gv3;

    LineGraphSeries<DataPoint> xSeries1, xSeries2, xSeries3, xSeries4, xSeries5, ySeries1, ySeries2,
            ySeries3, ySeries4, ySeries5, zSeries1, zSeries2, zSeries3, zSeries4, zSeries5;

    int duration;

    ArrayList<float[]> loginData = new ArrayList<>();
    float[] loginX, loginY, loginZ;

    ArrayList<ArrayList<float[]>> mainList = new ArrayList<ArrayList<float[]>>();
    ArrayList<float[]> xList = new ArrayList<float[]>();
    ArrayList<float[]> yList = new ArrayList<float[]>();
    ArrayList<float[]> zList = new ArrayList<float[]>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);

        gv1 = (GraphView) findViewById(R.id.gv1);
        gv2 = (GraphView) findViewById(R.id.gv2);
        gv3 = (GraphView) findViewById(R.id.gv3);

        mainList = (ArrayList<ArrayList<float[]>>) getIntent().getSerializableExtra("CALCULATED_DATA");
        duration = getIntent().getExtras().getInt("DURATION");
        loginData = (ArrayList<float[]>) getIntent().getSerializableExtra("LOGIN_DATA");

        setGraphData();

        if(loginData != null){
            setLoginIfExists();
        }

    }

    public ArrayList<LineGraphSeries<DataPoint>> createFullSeries(ArrayList<float[]> aL){

        ArrayList<LineGraphSeries<DataPoint>> seriesList =
                new ArrayList<LineGraphSeries<DataPoint>>();

        for(int i = 0; i < aL.size(); i++){
            DataPoint[] dTemp = new DataPoint[aL.get(i).length];

            for(int j = 1; j < aL.get(i).length+1; j++){
                DataPoint tempDP = new DataPoint(j, aL.get(i)[j-1]);
                dTemp[j-1] = tempDP;
            }

            LineGraphSeries<DataPoint> tempLGS = new LineGraphSeries<DataPoint>(dTemp);
            seriesList.add(tempLGS);
        }
        return seriesList;
    }

    public void setGraphData(){

        gv1.getViewport().setXAxisBoundsManual(true);
        gv1.getViewport().setYAxisBoundsManual(true);
        gv1.getViewport().setMaxY(2.5);
        gv1.getViewport().setMinY(-2.5);
        gv1.getViewport().setMaxX(duration);
        gv1.getViewport().setMinX(1);
        gv1.setTitle("Pitch");
        gv1.getGridLabelRenderer().setNumHorizontalLabels(10);

        gv2.getViewport().setXAxisBoundsManual(true);
        gv2.getViewport().setYAxisBoundsManual(true);
        gv2.getViewport().setMaxY(2.5);
        gv2.getViewport().setMinY(-2.5);
        gv2.getViewport().setMaxX(duration);
        gv2.getViewport().setMinX(1);
        gv2.setTitle("Roll");
        gv2.getGridLabelRenderer().setNumHorizontalLabels(10);

        gv3.getViewport().setXAxisBoundsManual(true);
        gv3.getViewport().setYAxisBoundsManual(true);
        gv3.getViewport().setMaxY(2.5);
        gv3.getViewport().setMinY(-2.5);
        gv3.getViewport().setMaxX(duration);
        gv3.getViewport().setMinX(1);
        gv3.setTitle("Yaw");
        gv3.getGridLabelRenderer().setNumHorizontalLabels(10);

        xList = mainList.get(0);
        yList = mainList.get(1);
        zList = mainList.get(2);

        xSeries1 = createFullSeries(xList).get(0);
        xSeries2 = createFullSeries(xList).get(1);
        xSeries3 = createFullSeries(xList).get(2);
        xSeries4 = createFullSeries(xList).get(3);
        ySeries1 = createFullSeries(yList).get(0);
        ySeries2 = createFullSeries(yList).get(1);
        ySeries3 = createFullSeries(yList).get(2);
        ySeries4 = createFullSeries(yList).get(3);
        zSeries1 = createFullSeries(zList).get(0);
        zSeries2 = createFullSeries(zList).get(1);
        zSeries3 = createFullSeries(zList).get(2);
        zSeries4 = createFullSeries(yList).get(3);

        xSeries1.setColor(Color.RED);
        xSeries2.setColor(Color.RED);
        xSeries3.setColor(Color.MAGENTA);
        xSeries4.setColor(Color.YELLOW);
        ySeries1.setColor(Color.GREEN);
        ySeries2.setColor(Color.GREEN);
        ySeries3.setColor(Color.MAGENTA);
        ySeries4.setColor(Color.YELLOW);
        zSeries1.setColor(Color.BLUE);
        zSeries2.setColor(Color.BLUE);
        zSeries3.setColor(Color.MAGENTA);
        zSeries4.setColor(Color.YELLOW);

        gv1.addSeries(xSeries1);
        gv1.addSeries(xSeries2);
        gv1.addSeries(xSeries3);
        gv1.addSeries(xSeries4);

        gv2.addSeries(ySeries1);
        gv2.addSeries(ySeries2);
        gv2.addSeries(ySeries3);
        gv2.addSeries(ySeries4);

        gv3.addSeries(zSeries1);
        gv3.addSeries(zSeries2);
        gv3.addSeries(zSeries3);
        gv3.addSeries(zSeries4);
    }

    public void setLoginIfExists(){

        xSeries5 = createFullSeries(loginData).get(0);
        ySeries5 = createFullSeries(loginData).get(1);
        zSeries5 = createFullSeries(loginData).get(2);

        xSeries5.setColor(Color.CYAN);
        ySeries5.setColor(Color.CYAN);
        zSeries5.setColor(Color.CYAN);

        gv1.addSeries(xSeries5);
        gv2.addSeries(ySeries5);
        gv3.addSeries(zSeries5);

    }
}
















