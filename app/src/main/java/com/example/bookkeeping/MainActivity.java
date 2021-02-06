package com.example.bookkeeping;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, DialogInterface.OnClickListener,
        DatePickerDialog.OnDateSetListener {
    static final String DB_NAME = "bookkeeping";
    static final String TB_NAME = "expenseTB";
    static final String[] FROM = new String[] {"type", "content", "price"};
    static final String[] COL = new String[] {"_id", "date", "type", "content", "price", "remark"};
    static final String[] typeColor = new String[] {"#E91E63", "#FF5722", "#FFC107", "#4CAF50", "#29B6F6", "#673AB7"};
    static final String dateFormat = "yyyy/MM/dd";

    SQLiteDatabase db;
    Cursor cur;
    SimpleCursorAdapter adapter;
    ImageButton btnAdding;
    ListView lv;
    TextView dateView, sumView;
    Calendar calendar;
    Date date;

    SimpleDateFormat df;
    String dateStr;
    String[] arrType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdding = (ImageButton) findViewById(R.id.btnAdd);
        calendar = Calendar.getInstance();
        dateView = (TextView) findViewById(R.id.dateView);
        sumView = (TextView) findViewById(R.id.sumView);
        date = new Date();
        df = new SimpleDateFormat(dateFormat);
        dateStr = df.format(date);
        dateView.setText(dateStr);
        arrType = getResources().getStringArray(R.array.typeArr);
        db = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        final String DROP_TABLE = "DROP TABLE IF EXISTS " + TB_NAME;
        db.execSQL(DROP_TABLE);
        String createTable = "CREATE TABLE IF NOT EXISTS " + TB_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date VARCHAR(20), " +
                "type VARCHAR(12), " +
                "content VARCHAR(32), " +
                "price INT, " +
                "remark VARCHAR(128))";
        db.execSQL(createTable);
        cur=db.rawQuery("SELECT * FROM "+TB_NAME, null);
        /*if(cur.getCount() == 0) {
            addData("食", "午餐", "100", "嗨");
            addData("衣", "衣服", "500", "嗨");
            addData("住", "房租", "1000", "嗨");
            addData("行", "公車", "15", "嗨");
            addData("育", "筆", "30", "嗨");
            addData("樂", "唱歌", "500", "嗨");
        }*/
        adapter = new SimpleCursorAdapter(this, R.layout.item, cur, FROM, new int[] {R.id.itemType, R.id.itemContent, R.id.itemPrice});
        lv = (ListView)findViewById(R.id.lv);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);
        lv.deferNotifyDataSetChanged();
        requery();
        new AlertDialog.Builder(this)
                .setTitle(R.string.instruction)
                .setMessage(R.string.selectDescription)
                .setNeutralButton(R.string.confirm, this)
                .show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        cur.moveToPosition(position);
        String remark = cur.getString(5);
        new AlertDialog.Builder(this)
                .setTitle(R.string.remark)
                .setMessage(remark)
                .show();
    }
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        cur.moveToPosition(position);
        new AlertDialog.Builder(this)
                .setMessage(R.string.deleteList)
                .setNegativeButton(R.string.cancel, this)
                .setPositiveButton(R.string.confirm, this)
                .show();
        return true;
    }
    @Override
    public void onClick(DialogInterface dialog, int PorN) {
        if (PorN == DialogInterface.BUTTON_POSITIVE) {
            db.delete(TB_NAME, "_id=" + cur.getInt(0), null);
            requery();
        }
    }
    public void onDateSelect(View v) {
        new DatePickerDialog(this, this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
                ).show();
    }
    public void goLastDay(View v) {
        calendar.add(Calendar.DATE,-1);//把日期往前減少一天，若想把日期向後推一天則將負數改為正數
        date=calendar.getTime();
        dateStr = df.format(date);
        dateView.setText(dateStr);
        requery();
    }
    public void goNextDay(View v) {
        calendar.add(Calendar.DATE,+1);//把日期往前減少一天，若想把日期向後推一天則將負數改為正數
        date=calendar.getTime();
        dateStr = df.format(date);
        dateView.setText(dateStr);
        requery();
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
    private void requery() {
        Cursor sumCur = db.rawQuery("SELECT CAST(SUM(price) AS VARCHAR(16)) FROM "+TB_NAME+" WHERE date = '"+dateStr+"'", null);
        sumCur.moveToNext();
        String sumAllDay = sumCur.getString(0);
        if(sumAllDay == null) {
            sumView.setText("0");
        }
        else {
            sumView.setText(sumAllDay);
        }

        cur = db.rawQuery("SELECT * FROM "+TB_NAME+" WHERE date = '"+dateStr+"'", null);
        adapter.changeCursor(cur);

    }

    public void onAdding(View v) {
        Intent goAdding = new Intent(this, AddingData.class);
        goAdding.putExtra("date", dateStr);
        startActivityForResult(goAdding, 100);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent it) {
        super.onActivityResult(requestCode, resultCode, it);
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, R.string.addingSuccess, Toast.LENGTH_SHORT).show();
            requery();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        date=calendar.getTime();
        dateStr = df.format(date);
        dateView.setText(dateStr);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent goPieChart = new Intent(this, pieChart.class);
        goPieChart.putExtra("date", dateStr);
        startActivity(goPieChart);
        return super.onOptionsItemSelected(item);
    }
}