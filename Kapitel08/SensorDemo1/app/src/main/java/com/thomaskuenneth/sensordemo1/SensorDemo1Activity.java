package com.thomaskuenneth.sensordemo1;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;

public class SensorDemo1Activity extends Activity {

    private static final String TAG =
            SensorDemo1Activity.class.getSimpleName();

    private TextView textview;
    private SensorManager manager;
    private Sensor sensor;
    private SensorEventListener listener;
    private SensorManager.DynamicSensorCallback callback;

    private HashMap<String, Boolean> hm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hm = new HashMap<>();
        setContentView(R.layout.main);
        textview = (TextView) findViewById(R.id.textview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        textview.setText("");
        // Liste der vorhandenen Sensoren ausgeben
        manager = getSystemService(SensorManager.class);
        List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor s : sensors) {
            textview.append(getString(R.string.template,
                    s.getName(),
                    s.getVendor(),
                    s.getVersion(),
                    Boolean.toString(s.isDynamicSensor())));
        }
        // Helligkeitssensor ermitteln
        sensor = manager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (sensor != null) {
            listener = new SensorEventListener() {

                @Override
                public void onAccuracyChanged(Sensor sensor,
                                              int accuracy) {
                    Log.d(TAG, "onAccuracyChanged(): " + accuracy);
                }

                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.values.length > 0) {
                        float light = event.values[0];
                        String text = Float.toString(light);
                        if ((SensorManager.LIGHT_SUNLIGHT <= light)
                                && (light <=
                                SensorManager.LIGHT_SUNLIGHT_MAX)) {
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

        // Callback für dynamische Sensoren
        callback = null;
        if (manager.isDynamicSensorDiscoverySupported()) {
            callback = new SensorManager.DynamicSensorCallback() {

                @Override
                public void onDynamicSensorConnected(Sensor sensor) {
                    textview.append(getString(R.string.connected,
                            sensor.getName()));
                }

                @Override
                public void onDynamicSensorDisconnected(Sensor sensor) {
                    textview.append(getString(R.string.disconnected,
                            sensor.getName()));
                }
            };
            manager.registerDynamicSensorCallback(callback,
                    new Handler());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensor != null) {
            manager.unregisterListener(listener);
        }
        if (callback != null) {
            manager.unregisterDynamicSensorCallback(callback);
        }
    }
}