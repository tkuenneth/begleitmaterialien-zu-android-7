package com.thomaskuenneth.preferencesdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PreferencesDemoActivity extends Activity {

    private static final int RQ_SETTINGS = 1234;

    private TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreferencesDemoActivity.this, SettingsActivity.class);
                startActivityForResult(intent, RQ_SETTINGS);
            }
        });
        textview = (TextView) findViewById(R.id.textview);
        updateTextView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RQ_SETTINGS == requestCode) {
            updateTextView();
        }
    }

    private void updateTextView() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        boolean cb1 = prefs.getBoolean("checkbox_1", false);
        boolean cb2 = prefs.getBoolean("checkbox_2", false);
        String et1 = prefs.getString("edittext_1", "");
        textview.setText(getString(R.string.template,
                Boolean.toString(cb1), Boolean.toString(cb2), et1));
//        SharedPreferences.Editor e = prefs.edit();
//        e.putBoolean("checkbox_1", cb1);
//        e.putBoolean("checkbox_2", cb2);
//        e.commit();
    }
}