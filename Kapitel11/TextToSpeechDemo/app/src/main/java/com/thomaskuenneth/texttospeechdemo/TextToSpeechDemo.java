package com.thomaskuenneth.texttospeechdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.File;
import java.util.Hashtable;
import java.util.Locale;

public class TextToSpeechDemo extends Activity
        implements OnInitListener {

    private static final int RQ_CHECK_TTS_DATA = 1;
    private static final String TAG =
            TextToSpeechDemo.class.getSimpleName();

    private final Hashtable<String, Locale> supportedLanguages =
            new Hashtable<>();

    private TextToSpeech tts;
    private String last_utterance_id;

    private EditText input;
    private Spinner spinner;
    private Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // die Sprachsynthesekomponente wurde
        // noch nicht initialisiert
        tts = null;
        // prüfen, ob Sprachpakete vorhanden sind
        Intent intent = new Intent();
        intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, RQ_CHECK_TTS_DATA);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ggf. Ressourcen freigeben
        if (tts != null) {
            tts.shutdown();
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Sind Sprachpakete vorhanden?
        if (requestCode == RQ_CHECK_TTS_DATA) {
            if (resultCode ==
                    TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // Initialisierung der Sprachkomponente starten
                tts = new TextToSpeech(this, this);
            } else {
                // Installation der Sprachpakete vorbereiten
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
                // Activity beenden
                finish();
            }
        }
    }

    @Override
    public void onInit(int status) {
        if (status != TextToSpeech.SUCCESS) {
            // die Initialisierung war nicht erfolgreich
            finish();
        }
        // Activity initialisieren
        setContentView(R.layout.main);
        input = (EditText) findViewById(R.id.input);
        spinner = (Spinner) findViewById(R.id.locale);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener((v) -> {
            String text = input.getText().toString();
            String key = (String) spinner.getSelectedItem();
            Locale loc = supportedLanguages.get(key);
            if (loc != null) {
                button.setEnabled(false);
                tts.setLanguage(loc);
                last_utterance_id = Long.toString(System
                        .currentTimeMillis());
                tts.speak(text, TextToSpeech.QUEUE_FLUSH,
                        null, last_utterance_id);
                // in Datei schreiben
                File file = new File(getExternalFilesDir(
                        Environment.DIRECTORY_PODCASTS),
                        last_utterance_id
                                + ".wav");
                tts.synthesizeToFile(text, null, file,
                        last_utterance_id);
            }
        });
        tts.setOnUtteranceProgressListener(
                new UtteranceProgressListener() {

                    @Override
                    public void onStart(String utteranceId) {
                        Log.d(TAG, "onStart(): " + utteranceId);
                    }

                    @Override
                    public void onDone(final String utteranceId) {
                        final Handler h =
                                new Handler(Looper.getMainLooper());
                        h.post(() -> {
                            if (utteranceId.equals(last_utterance_id)) {
                                button.setEnabled(true);
                            }
                        });
                    }

                    @Override
                    public void onError(String utteranceId) {
                        Log.d(TAG, "onError(): " + utteranceId);
                    }

                });
        // Liste der Sprachen ermitteln
        String[] languages = Locale.getISOLanguages();
        for (String lang : languages) {
            Locale loc = new Locale(lang);
            switch (tts.isLanguageAvailable(loc)) {
                case TextToSpeech.LANG_MISSING_DATA:
                case TextToSpeech.LANG_NOT_SUPPORTED:
                    break;
                default:
                    String key = loc.getDisplayLanguage();
                    if (!supportedLanguages.containsKey(key)) {
                        supportedLanguages.put(key, loc);
                    }
                    break;
            }
        }
        ArrayAdapter<Object> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, supportedLanguages
                .keySet().toArray());
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
