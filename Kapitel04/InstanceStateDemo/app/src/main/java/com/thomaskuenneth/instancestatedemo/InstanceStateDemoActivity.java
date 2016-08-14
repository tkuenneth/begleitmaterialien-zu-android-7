package com.thomaskuenneth.instancestatedemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


public class InstanceStateDemoActivity extends Activity {

    private static final String TAG = InstanceStateDemoActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Log.d(TAG, "savedInstanceState war null");
        } else {
            Log.d(TAG,
                    "wurde vor "
                            + (System.currentTimeMillis() - savedInstanceState
                            .getLong(TAG)) + " Millisekunden beendet");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(TAG, System.currentTimeMillis());
    }
}
