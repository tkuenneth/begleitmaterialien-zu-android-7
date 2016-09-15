package com.thomaskuenneth.bewegungsmelder;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

public class BewegungsmelderActivity extends Activity {

    private static final String KEY1 = "shouldCallWaitForTriggerInOnResume";
    private static final String KEY2 = "tv";

    private TextView tv;
    private Button bt;

    private SensorManager m;
    private TriggerEventListener l;
    private Sensor s;

    private boolean shouldCallWaitForTriggerInOnResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv = (TextView) findViewById(R.id.tv);
        bt = (Button) findViewById(R.id.bt);
        bt.setOnClickListener((e) -> {
            shouldCallWaitForTriggerInOnResume = true;
            waitForTrigger();
        });

        m = getSystemService(SensorManager.class);
        s = m.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
        l = new TriggerEventListener() {
            @Override
            public void onTrigger(TriggerEvent event) {
                shouldCallWaitForTriggerInOnResume = false;
                bt.setVisibility(View.VISIBLE);
                tv.setText(
                        DateFormat.getTimeFormat(
                                BewegungsmelderActivity.this).
                                format(new Date()));
            }
        };
        if (s == null) {
            shouldCallWaitForTriggerInOnResume = false;
            bt.setVisibility(View.GONE);
            tv.setText(R.string.no_sensors);
        } else {
            shouldCallWaitForTriggerInOnResume = true;
            if (savedInstanceState != null) {
                shouldCallWaitForTriggerInOnResume =
                        savedInstanceState.getBoolean(KEY1);
                tv.setText(savedInstanceState.getString(KEY2));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY1, shouldCallWaitForTriggerInOnResume);
        outState.putString(KEY2, tv.getText().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (s != null) {
            if (shouldCallWaitForTriggerInOnResume) {
                waitForTrigger();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (s != null) {
            m.cancelTriggerSensor(l, s);
        }
    }

    private void waitForTrigger() {
        bt.setVisibility(View.GONE);
        tv.setText(R.string.wait);
        m.requestTriggerSensor(l, s);
    }
}
