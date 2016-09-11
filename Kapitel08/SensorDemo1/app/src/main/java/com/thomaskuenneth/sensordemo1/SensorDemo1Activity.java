package com.thomaskuenneth.sensordemo1;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class SensorDemo1Activity extends Activity {

    private static final String TAG = SensorDemo1Activity.class.getSimpleName();

    private TextView textview;

    private SensorManager manager;
    private Sensor sensor;
    private SensorEventListener listener;

    private HashMap<String, Boolean> hm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hm = new HashMap<>();
        setContentView(R.layout.main);
        textview = (TextView) findViewById(R.id.textview);
        // Liste der vorhandenen Sensoren ausgeben
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor s : sensors) {
            textview.append(getString(R.string.template, s.getName(), s.getVendor(), s.getVersion()));
        }
        // Helligkeitssensor ermitteln
        sensor = manager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (sensor != null) {
            listener = new SensorEventListener() {

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    Log.d(TAG, "onAccuracyChanged(): " + accuracy);
                }

                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.values.length > 0) {
                        float light = event.values[0];
                        String text = Float.toString(light);
                        if ((SensorManager.LIGHT_SUNLIGHT <= light)
                                && (light <= SensorManager.LIGHT_SUNLIGHT_MAX)) {
                            text = getString(R.string.sunny);
                        }
                        // jeden Wert nur einmal ausgeben
                        if (!hm.containsKey(text)) {
                            hm.put(text, Boolean.TRUE);
                            text += "\n";
                            textview.append(text);
                        }
                    }
                }
            };
            // Listener registrieren
            manager.registerListener(listener, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            textview.append(getString(R.string.no_seonsor));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Listener entfernen
        if (sensor != null) {
            manager.unregisterListener(listener);
        }
    }
}