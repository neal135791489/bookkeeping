package com.example.bookkeeping;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DecimalFormat;

public class pieChart extends AppCompatActivity {
    static final String DB_NAME = "bookkeeping";
    static final String TB_NAME = "expenseTB";
    TextView tvFood, tvClothing, tvHousing, tvTransportation, tvEducation, tvEntertainment, tvDescription, tvSpandingMonth;
    PieChart pieChart;
    SQLiteDatabase db;
    Cursor cur;
    String dateStr, yAndM;
    String[] arrType;
    float[] arrNumType;
    float sumAllMonth;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);
        arrType = getResources().getStringArray(R.array.typeArr);
        arrNumType = new float[6];
        tvDescription = findViewById(R.id.tvDescription);
        tvFood = findViewById(R.id.tvFood);
        tvClothing = findViewById(R.id.tvClothing);
        tvHousing = findViewById(R.id.tvHousing);
        tvTransportation = findViewById(R.id.tvTransportation);
        tvEducation = findViewById(R.id.tvEducation);
        tvEntertainment = findViewById(R.id.tvEntertainment);
        tvSpandingMonth = findViewById(R.id.tvSpendingMonth);
        pieChart = findViewById(R.id.piechart);

        Intent getData = getIntent();
        dateStr = getData.getStringExtra("date");
        yAndM = dateStr.substring(0, 7);
        db = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        sumAllMonth = 0;
        cur = db.rawQuery(
                "SELECT SUM(price) FROM "+TB_NAME+
                        " WHERE date LIKE '"+yAndM+"%'", null);
        if(cur.moveToNext() && cur.getFloat(0) != 0){
            sumAllMonth = cur.getFloat(0);
        }
        else {
            sumAllMonth = 0;
        }
        for(int i = 0; i < arrType.length; i++) {
            cur = db.rawQuery(
                    "SELECT SUM(price) FROM "+TB_NAME+
                            " WHERE date LIKE '"+yAndM+"%' AND type = '"+ arrType[i] +"'",
                    null);
            if(cur.moveToNext()){
                arrNumType[i] = cur.getFloat(0);
            }
            else {
                arrNumType[i] = 0;
            }
        }
        setData();
    }
    private void setData() {
        pieChart.addPieSlice(
                new PieModel(
                        getResources().getString(R.string.food),
                        arrNumType[0],
                        Color.parseColor("#E91E63")));
        pieChart.addPieSlice(
                new PieModel(
                        getResources().getString(R.string.clothing),
                        arrNumType[1],
                        Color.parseColor("#FF5722")));
        pieChart.addPieSlice(
                new PieModel(
                        getResources().getString(R.string.housing),
                        arrNumType[2],
                        Color.parseColor("#FFC107")));
        pieChart.addPieSlice(
                new PieModel(
                        getResources().getString(R.string.transportation),
                        arrNumType[3],
                        Color.parseColor("#4CAF50")));
        pieChart.addPieSlice(
                new PieModel(
                        getResources().getString(R.string.education),
                        arrNumType[4],
                        Color.parseColor("#29B6F6")));
        pieChart.addPieSlice(
                new PieModel(
                        getResources().getString(R.string.entertainment),
                        arrNumType[5],
                        Color.parseColor("#673AB7")));
        DecimalFormat dformat = new DecimalFormat("########0.00");
        tvDescription.setText(yAndM + getResources().getString(R.string.pieDescription));
        tvSpandingMonth.setText(getResources().getString(R.string.expenseAllMonth)+ ":" + dformat.format(sumAllMonth));
        if(sumAllMonth != 0)
            for(int i = 0; i < 6; i++)
                arrNumType[i] = arrNumType[i] / sumAllMonth * 100;
        tvFood.setText(dformat.format(arrNumType[0]));
        tvClothing.setText(dformat.format(arrNumType[1]));
        tvHousing.setText(dformat.format(arrNumType[2]));
        tvTransportation.setText(dformat.format(arrNumType[3]));
        tvEducation.setText(dformat.format(arrNumType[4]));
        tvEntertainment.setText(dformat.format(arrNumType[5]));
        pieChart.startAnimation();
    }
}