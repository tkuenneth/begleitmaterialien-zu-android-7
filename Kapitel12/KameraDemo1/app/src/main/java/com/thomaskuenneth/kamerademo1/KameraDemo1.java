package com.thomaskuenneth.kamerademo1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class KameraDemo1 extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Benutzeroberfläche anzeigen
        setContentView(R.layout.main);
        Button foto = (Button) findViewById(R.id.foto);
        foto.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Intent instantiieren
                Intent intent = new Intent(
                        MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
                // Activity starten
                startActivity(intent);
            }
        });
        Button video = (Button) findViewById(R.id.video);
        video.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(
                        MediaStore.INTENT_ACTION_VIDEO_CAMERA);
                startActivity(intent);
            }
        });
    }
}