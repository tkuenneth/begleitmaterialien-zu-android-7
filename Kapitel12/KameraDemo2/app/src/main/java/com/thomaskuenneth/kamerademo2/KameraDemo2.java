package com.thomaskuenneth.kamerademo2;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class KameraDemo2 extends Activity {

    private static final String TAG =
            KameraDemo2.class.getSimpleName();

    private static final int
            PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123;

    private static final int IMAGE_CAPTURE = 1;

    // Views
    private ImageView imageView;
    private Button button;

    // über diese Uri ist die Aufnahme erreichbar
    private Uri imageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Quermodus fest einstellen
        setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Benutzeroberfläche anzeigen
        setContentView(R.layout.main);
        imageView = (ImageView) findViewById(R.id.view);
        button = (Button) findViewById(R.id.shoot);
        button.setOnClickListener((v) ->
                startCamera()
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            button.setEnabled(false);
        } else {
            button.setEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        if ((requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) &&
                (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED)) {
            button.setEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                try {
                    Bitmap b1 = MediaStore.Images.Media
                            .getBitmap(
                                    getContentResolver(), imageUri);
                    // Größe des aufgenommenen Bildes
                    float w1 = b1.getWidth();
                    float h1 = b1.getHeight();
                    // auf eine Höhe von 300 Pixel skalieren
                    int h2 = 300;
                    int w2 = (int) (w1 / h1 * (float) h2);
                    Bitmap b2 = Bitmap.createScaledBitmap(b1,
                            w2, h2, false);
                    imageView.setImageBitmap(b2);
                } catch (IOException e) {
                    Log.e(TAG, "setBitmap()", e);
                }
            } else {
                int rowsDeleted =
                        getContentResolver().delete(imageUri,
                                null, null);
                Log.d(TAG, rowsDeleted + " rows deleted");
            }
        }
    }

    private void startCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,
                getString(R.string.app_name));
        values.put(MediaStore.Images.Media.DESCRIPTION,
                getString(R.string.descr));
        values.put(MediaStore.Images.Media.MIME_TYPE,
                "image/jpeg");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, IMAGE_CAPTURE);
    }
}