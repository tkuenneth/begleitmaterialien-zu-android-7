package com.thomaskuenneth.android;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

public class TelephonyManagerDemoActivity extends Activity {

    private static final String TAG = TelephonyManagerDemoActivity.class.getSimpleName();

    private TextView textview;
    private TelephonyManager mgr;
    private PhoneStateListener psl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textview = (TextView) findViewById(R.id.textview);

        mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        psl = new PhoneStateListener() {

            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                textview.setText(("Status: " + state + "\n") + "Eingehende Rufnummer: " + incomingNumber + "\n");
            }

            @Override
            public void onMessageWaitingIndicatorChanged(boolean mwi) {
                textview.setText(Boolean.toString(mwi));
            }
        };
        mgr.listen(psl, PhoneStateListener.LISTEN_CALL_STATE);
        // mgr.listen(psl, PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR);

        Log.d(TAG, mgr.getDeviceId());
        Log.d(TAG, Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID));
        try {
            Log.d(TAG, "SKIP_FIRST_USE_HINTS: " + Settings.Secure.getInt(getContentResolver(),
                    Settings.Secure.SKIP_FIRST_USE_HINTS));
        } catch (Settings.SettingNotFoundException e) {
            // Ausnahme ignorieren
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mgr.listen(psl, PhoneStateListener.LISTEN_NONE);
    }
}