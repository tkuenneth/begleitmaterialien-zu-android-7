package com.thomaskuenneth.datepickerdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DatePickerDemoActivity extends Activity
        implements OnDateChangedListener {

    private static final String TAG =
            DatePickerDemoActivity.class.getSimpleName();

    Calendar cal;
    DateFormat df;
    DatePicker dp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, Calendar.AUGUST);
        cal.set(Calendar.DAY_OF_MONTH, 29);
        df = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
        dp = (DatePicker) findViewById(R.id.dp);
        dp.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH), this);
    }

    @Override
    public void onDateChanged(DatePicker view,
                              int year, int monthOfYear,
                              int dayOfMonth) {
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        JSONObject object = new JSONObject();
        try {
            object.put("datum", df.format(cal.getTime()));
        } catch (JSONException e) {
            Log.e(TAG, "onDateChanged()", e);
        }
        Log.i(TAG, "JSON: " + object);
    }
}
