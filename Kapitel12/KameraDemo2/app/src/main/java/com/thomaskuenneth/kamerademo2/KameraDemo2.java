package com.thomaskuenneth.kamerademo2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class KameraDemo2 extends Activity {

    private static final String TITLE = "KameraDemo2";
    private static final String DESCRIPTION = "Ein mit der App KameraDemo2 aufgenommenes Foto";
    private static final String TAG = KameraDemo2.class.getSimpleName();

    private static final int IMAGE_CAPTURE = 1;

    // Views
    private ImageView imageView;

    // über diese Uri ist die Aufnahme erreichbar
    private Uri imageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Quermodus fest einstellen
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Benutzeroberfläche anzeigen
        setContentView(R.layout.main);
        imageView = (ImageView) findViewById(R.id.view);
        Button button = (Button) findViewById(R.id.shoot);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startCamera();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                try {
                    Bitmap b1 = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), imageUri);
                    // Größe des aufgenommenen Bildes
                    float w1 = b1.getWidth();
                    float h1 = b1.getHeight();
                    // auf eine Höhe von 300 Pixel skalieren
                    int h2 = 300;
                    int w2 = (int) (w1 / h1 * (float) h2);
                    Bitmap b2 = Bitmap.createScaledBitmap(b1, w2, h2, false);
                    imageView.setImageBitmap(b2);
                } catch (IOException e) { // und FileNotFoundException
                    Log.e(TAG, "setBitmap()", e);
                }
            } else {
                int rowsDeleted = getContentResolver().delete(imageUri, null,
                        null);
                Log.d(TAG, rowsDeleted + " rows deleted");
            }
        }
    }

    private void startCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, TITLE);
        values.put(MediaStore.Images.Media.DESCRIPTION, DESCRIPTION);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, IMAGE_CAPTURE);
    }
}