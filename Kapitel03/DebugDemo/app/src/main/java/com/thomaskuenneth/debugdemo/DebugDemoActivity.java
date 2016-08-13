package com.thomaskuenneth.debugdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class DebugDemoActivity extends Activity {

    private static final String TAG = DebugDemoActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        int fakultaet = 1;
        System.out.println("0! = " + fakultaet);
        for (int i = 1; i <= 5; i++) {
            fakultaet = i * fakultaet;
            System.out.println(i + "! = " + fakultaet);
        }

        Log.v(TAG, "ausführliche Protokollierung, nicht in Produktion verwenden");
        Log.d(TAG, "Debug-Ausgaben");
        Log.i(TAG, "Informationen");
        Log.w(TAG, "Warnungen");
        Log.e(TAG, "Fehler");

        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "noch eine Debug-Ausgabe");
        }

        String s = null;
        try {
            Log.i(TAG, "s ist " + s.length() + " Zeichen lang");
        } catch (Throwable tr) {
            Log.e(TAG, "Es ist ein Fehler aufgetreten.", tr);
        } finally {
            Log.i(TAG, "s ist " + s);
        }

        finish();
    }

}