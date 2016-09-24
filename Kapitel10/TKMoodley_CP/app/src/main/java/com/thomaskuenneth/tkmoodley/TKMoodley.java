package com.thomaskuenneth.tkmoodley;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class TKMoodley extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final ImageButton buttonGut =
                (ImageButton) findViewById(R.id.gut);
        buttonGut.setOnClickListener((e) ->
                imageButtonClicked(TKMoodleyOpenHandler.MOOD_FINE)
        );
        final ImageButton buttonOk =
                (ImageButton) findViewById(R.id.ok);
        buttonOk.setOnClickListener((e) ->
                imageButtonClicked(TKMoodleyOpenHandler.MOOD_OK)
        );
        final ImageButton buttonSchlecht =
                (ImageButton) findViewById(R.id.schlecht);
        buttonSchlecht.setOnClickListener((e) ->
                imageButtonClicked(TKMoodleyOpenHandler.MOOD_BAD)
        );
        final Button buttonAuswertung =
                (Button) findViewById(R.id.auswertung);
        buttonAuswertung.setOnClickListener((e) -> {
            Intent intent = new Intent(this, History.class);
            startActivity(intent);
        });
    }

    private void imageButtonClicked(int mood) {
        ContentValues values = new ContentValues();
        values.put(TKMoodleyOpenHandler.MOOD_MOOD, mood);
        values.put(TKMoodleyOpenHandler.MOOD_TIME,
                System.currentTimeMillis());
        getContentResolver()
                .insert(TKMoodleyProvider.CONTENT_URI, values);
        Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT)
                .show();
    }
}
