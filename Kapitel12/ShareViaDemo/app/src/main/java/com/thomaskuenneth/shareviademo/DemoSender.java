package com.thomaskuenneth.shareviademo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class DemoSender extends Activity {

    private static final String TAG =
            DemoSender.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE =
            123;

    private Bitmap greyscaleBitmap;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        Intent intent = getIntent();
        if ((intent != null) &&
                (Intent.ACTION_SEND.equals(intent.getAction()))) {
            setContentView(R.layout.demosender);
            ImageView imageView =
                    (ImageView) findViewById(R.id.image);
            // Uri des erhaltenen Bildes
            Uri imageUri = (Uri) intent.getExtras().get(
                    Intent.EXTRA_STREAM);
            try {
                // Bitmap erzeugen
                Bitmap bm1 = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);
                // in Graustufen umwandeln und anzeigen
                greyscaleBitmap = toGrayscale(bm1);
                bm1.recycle();
                imageView.setImageBitmap(greyscaleBitmap);
            } catch (IOException e) {
                Log.e(TAG, e.getClass().getSimpleName(), e);
            }
            // Button
            final Button button = (Button) findViewById(R.id.button);
            button.setOnClickListener((v) -> {
                if (checkSelfPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]
                                    {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                } else {
                    share();
                }
            });
        } else {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        if ((requestCode ==
                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) &&
                (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED)) {
            share();
        } else {
            finish();
        }
    }

    private Bitmap toGrayscale(Bitmap src) {
        // Breite und Höhe
        int width = src.getWidth();
        int height = src.getHeight();
        // neue Bitmap erzeugen
        Bitmap desti = Bitmap
                .createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(desti);
        Paint paint = new Paint();
        // Umwandlung in Graustufen
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        // mit Filter kopieren
        c.drawBitmap(src, 0, 0, paint);
        return desti;
    }

    private void share() {
        String uri = MediaStore.Images.Media.insertImage(
                getContentResolver(),
                greyscaleBitmap, "Titel", "Beschreibung");
        Uri _uri = Uri.parse(uri);
        Intent intent = new Intent(Intent.ACTION_VIEW, _uri);
        intent.setType("image/?");
        startActivity(intent);
    }
}
