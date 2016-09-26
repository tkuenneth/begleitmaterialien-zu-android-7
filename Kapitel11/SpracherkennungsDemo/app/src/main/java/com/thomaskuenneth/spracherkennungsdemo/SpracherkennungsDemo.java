package com.thomaskuenneth.spracherkennungsdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SpracherkennungsDemo extends Activity {

    private static final int RQ_VOICE_RECOGNITION = 1;

    private TextView textview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Benutzeroberfläche anzeigen
        setContentView(R.layout.main);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener((v) ->
                startVoiceRecognitionActivity());
        textview = (TextView) findViewById(R.id.textview);
        // Verfügbarkeit der Spracherkennung prüfen
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities =
                pm.queryIntentActivities(new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            button.setEnabled(false);
            button.setText(getString(R.string.not_present));
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        if (requestCode == RQ_VOICE_RECOGNITION
                && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
            if (matches.size() > 0) {
                textview.setText(matches.get(0));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.prompt));
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "de-DE");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        startActivityForResult(intent, RQ_VOICE_RECOGNITION);
    }
}