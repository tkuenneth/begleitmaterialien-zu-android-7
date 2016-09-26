package com.thomaskuenneth.audioeffektedemo;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.BassBoost;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Virtualizer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;

public class AudioEffekteDemo extends Activity {

    private static final String TAG =
            AudioEffekteDemo.class.getSimpleName();

    private MediaPlayer mediaPlayer;

    // Effekte
    private BassBoost bassBoost = null;
    private Virtualizer virtualizer = null;
    private PresetReverb reverb = null;

    private Button button;
    private boolean playing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // MediaPlayer instantiieren
        mediaPlayer = MediaPlayer.create(this, R.raw.guten_tag);
        mediaPlayer.setOnCompletionListener((mp) -> {
            playing = false;
            updateButtonText();
        });
        int sessionId = mediaPlayer.getAudioSessionId();
        // BassBoost instantiieren und an Audio Session binden
        bassBoost = new BassBoost(0, sessionId);
        Log.d(TAG, "getRoundedStrength(): "
                + bassBoost.getRoundedStrength());
        if (bassBoost.getStrengthSupported()) {
            bassBoost.setStrength((short) 1000);
        }
        // Checkbox schaltet BassBoost aus und ein
        final CheckBox cbBassBoost =
                (CheckBox) findViewById(R.id.cbBassBoost);
        cbBassBoost.setOnCheckedChangeListener((buttonView,
                                                isChecked) -> {
            int result = bassBoost.setEnabled(isChecked);
            if (result != AudioEffect.SUCCESS) {
                Log.e(TAG, "Bass Boost: setEnabled("
                        + isChecked + ") = "
                        + result);
            }
        });
        cbBassBoost.setChecked(false);
        // Virtualizer instantiieren und an Audio Session binden
        virtualizer = new Virtualizer(0, sessionId);
        virtualizer.setStrength((short) 1000);
        // Checkbox schaltet Virtualizer aus und ein
        final CheckBox cbVirtualizer =
                (CheckBox) findViewById(R.id.cbVirtualizer);
        cbVirtualizer.setOnCheckedChangeListener((buttonView,
                                                  isChecked) -> {
            int result = virtualizer.setEnabled(isChecked);
            if (result != AudioEffect.SUCCESS) {
                Log.e(TAG, "Virtualizer: setEnabled("
                        + isChecked + ") = "
                        + result);
            }
        });
        cbVirtualizer.setChecked(false);
        // Hall
        reverb = new PresetReverb(0, 0);
        int effectId = reverb.getId();
        reverb.setPreset(PresetReverb.PRESET_PLATE);
        mediaPlayer.attachAuxEffect(effectId);
        mediaPlayer.setAuxEffectSendLevel(1f);
        // Checkbox schaltet Hall aus und ein
        final CheckBox cbReverb =
                (CheckBox) findViewById(R.id.cbReverb);
        cbReverb.setOnCheckedChangeListener((buttonView,
                                             isChecked) -> {
            int result = reverb.setEnabled(isChecked);
            if (result != AudioEffect.SUCCESS) {
                Log.e(TAG, "PresetReverb: setEnabled("
                        + isChecked + ") = "
                        + result);
            }
        });
        cbReverb.setChecked(false);
        // Schaltfläche
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener((view) -> {
            if (playing) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
            playing = !playing;
            updateButtonText();
        });
        playing = false;
        updateButtonText();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        if (bassBoost != null) {
            bassBoost.release();
        }
        if (virtualizer != null) {
            virtualizer.release();
        }
        if (reverb != null) {
            reverb.release();
        }
        mediaPlayer.release();
    }

    private void updateButtonText() {
        button.setText(getString(playing
                ? R.string.stop : R.string.start));
    }
}