package com.example.bookkeeping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class AddingData extends AppCompatActivity {

    static final String DB_NAME = "bookkeeping";
    static final String TB_NAME = "expenseTB";
    static final String[] COL = new String[] {"_id", "date", "type", "content", "price", "remark"};

    SQLiteDatabase db;
    EditText etContent, etPrice, etRemark;
    ImageButton btnNextDay, btnLastDay, btnComfirm;
    TextView viewDate;
    Spinner spinType;
    Calendar calendar;
    Date date;

    String dateStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_data);

        etContent = (EditText) findViewById(R.id.etContent);
        etPrice = (EditText) findViewById(R.id.etPrice);
        etRemark = (EditText) findViewById(R.id.etRemark);
        btnComfirm = (ImageButton) findViewById(R.id.btnComfirm);
        spinType = (Spinner) findViewById(R.id.spinType);
        calendar = Calendar.getInstance();
        db = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        viewDate = (TextView) findViewById(R.id.viewDate);

        date = new Date();
        Intent getData = getIntent();
        dateStr = getData.getStringExtra("date");
        viewDate.setText(dateStr);
    }

    private void addData(String type, String content, String price, String remark) {
        ContentValues cv = new ContentValues(5);
        cv.put(COL[1], dateStr);
        cv.put(COL[2], type);
        cv.put(COL[3], content);
        cv.put(COL[4], price);
        cv.put(COL[5], remark);
        db.insert(TB_NAME, null, cv);
    }
    public void onAdd(View v) {
        String[] typeStr = getResources().getStringArray(R.array.typeArr);
        int index = spinType.getSelectedItemPosition();
        String contentStr = etContent.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String remarkStr = etRemark.getText().toString().trim();
        if(contentStr.length() == 0 || priceStr.length() == 0) return;
        addData(typeStr[index], contentStr, priceStr, remarkStr);
        db.close();

        Intent goBack = new Intent();
        goBack.putExtra("date", dateStr);
        setResult(RESULT_OK, goBack);
        finish();
    }
}