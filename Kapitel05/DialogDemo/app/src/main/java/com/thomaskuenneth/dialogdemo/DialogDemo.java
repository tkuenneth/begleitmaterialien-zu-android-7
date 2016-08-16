package com.thomaskuenneth.dialogdemo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

public class DialogDemo extends Activity
        implements DatePickerDialog.OnDateSetListener,
        DialogInterface.OnClickListener{

    private TextView textview;
    private DatePickerFragment datePickerFragment;
    private AlertFragment alertFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textview = (TextView) findViewById(R.id.textview);
        // DatePicker
        datePickerFragment = new DatePickerFragment();
        final Button buttonDatePicker = (Button) findViewById(R.id.button_datepicker);
        buttonDatePicker.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                datePickerFragment.show(getFragmentManager(), DatePickerFragment.TAG);
            }
        });
        // Alert
        alertFragment = new AlertFragment();
        final Button buttonAlert = (Button) findViewById(R.id.button_alert);
        buttonAlert.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                alertFragment.show(getFragmentManager(), AlertFragment.TAG);
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        textview.setText(getString(R.string.button_datepicker));
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        textview.setText(getString(R.string.button_alert));
    }
}
