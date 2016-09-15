package com.thomaskuenneth.schrittzaehler;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Locale;

public class SchrittzaehlerActivity extends Activity
        implements SensorEventListener {

    private static final String PREFS =
            SchrittzaehlerActivity.class.getName();

    private static final String PREFS_KEY = "last";

    private ProgressBar pb;
    private TextView steps;
    private Button reset;
    private Switch onOff;

    private SensorManager m;
    private Sensor s;

    private int last;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        pb = (ProgressBar) findViewById(R.id.pb);
        steps = (TextView) findViewById(R.id.steps);
        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener((event) -> {
            updateSharedPrefs(this, last);
            updateUI();
        });
        m = getSystemService(SensorManager.class);
        s = m.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        onOff = (Switch) findViewById(R.id.on_off);
        onOff.setOnCheckedChangeListener((buttonView, isChecked)
                -> updateUI());
        onOff.setChecked(s != null);
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;
        int _steps = (int) values[0];
        last = _steps;
        SharedPreferences prefs = getSharedPreferences(
                SchrittzaehlerActivity.PREFS,
                Context.MODE_PRIVATE);
        _steps -= prefs.getInt(PREFS_KEY, 0);
        this.steps.setText(String.format(Locale.US,
                "%d", _steps));
        if (pb.getVisibility() == View.VISIBLE) {
            pb.setVisibility(View.GONE);
            this.steps.setVisibility(View.VISIBLE);
            reset.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public static void updateSharedPrefs(Context context,
                                         int last) {
        SharedPreferences prefs =
                context.getSharedPreferences(
                        SchrittzaehlerActivity.PREFS,
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt(SchrittzaehlerActivity.PREFS_KEY, last);
        edit.apply();
    }

    private void updateUI() {
        reset.setVisibility(View.GONE);
        onOff.setEnabled(s != null);
        if (s != null) {
            steps.setVisibility(View.GONE);
            if (onOff.isChecked()) {
                m.registerListener(this, s,
                        SensorManager.SENSOR_DELAY_UI);
                pb.setVisibility(View.VISIBLE);
            } else {
                m.unregisterListener(this);
                pb.setVisibility(View.GONE);
            }
        } else {
            steps.setVisibility(View.VISIBLE);
            steps.setText(R.string.no_sensor);
            pb.setVisibility(View.GONE);
        }
    }
}
