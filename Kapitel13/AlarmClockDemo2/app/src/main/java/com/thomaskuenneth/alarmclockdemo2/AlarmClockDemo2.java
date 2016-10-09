package com.thomaskuenneth.alarmclockdemo2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class AlarmClockDemo2 extends Activity {

    private TextView tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv = (TextView) findViewById(R.id.tv);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent i = getIntent();
        if (i == null) {
            tv.setText("Es wurde kein Intent übergeben");
        } else {
            tv.setText(i.getAction() + "\n");
            Bundle b = i.getExtras();
            if (b != null) {
                for (String s : b.keySet()) {
                    tv.append(s + "\n");
                    Object o = b.get(s);
                    if (o != null) {
                        tv.append(o.toString()
                                + "\n\n");
                    }
                }
            }
        }
    }
}