package com.thomaskuenneth.audiomanagerdemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

public class AudioManagerDemoActivity extends Activity {

    private TextView tv;
    private BroadcastReceiver mReceiver;
    private MediaSession session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv = (TextView) findViewById(R.id.tv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Intent Filter konfigurieren
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AudioManager.ACTION_HEADSET_PLUG);
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        // Broadcast Receiver erzeugen
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context,
                                  Intent intent) {
                handleIntent(intent);
            }
        };
        // Broadcast Receiver registrieren
        registerReceiver(mReceiver, intentFilter);
        // MediaSession konfigurieren
        session = new MediaSession(this,
                getClass().getSimpleName());
        session.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS);
        session.setCallback(new MediaSession.Callback() {

            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
                handleIntent(mediaButtonIntent);
                return super.onMediaButtonEvent(mediaButtonIntent);
            }
        });
        session.setActive(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        session.release();
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (Intent.ACTION_MEDIA_BUTTON.equals(action)) {
                KeyEvent keyEvent = intent
                        .getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (KeyEvent.ACTION_UP == keyEvent.getAction()) {
                    tv.append("getKeyCode(): "
                            + keyEvent.getKeyCode() + "\n");
                }
                if (keyEvent.isLongPress()) {
                    tv.append("laaange gedrückt\n");
                }
            } else if (AudioManager.ACTION_AUDIO_BECOMING_NOISY
                    .equals(action)) {
                tv.append("ACTION_AUDIO_BECOMING_NOISY\n");
            } else if (AudioManager.ACTION_HEADSET_PLUG.equals(action)) {
                tv.append("ACTION_HEADSET_PLUG\n");
            }
        }
    }
}