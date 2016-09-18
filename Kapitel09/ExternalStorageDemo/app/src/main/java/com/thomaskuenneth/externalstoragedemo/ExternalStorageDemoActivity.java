package com.thomaskuenneth.externalstoragedemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ExternalStorageDemoActivity extends Activity {

    private static final String TAG =
            ExternalStorageDemoActivity.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE =
            123;

    private TextView tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv = (TextView) findViewById(R.id.tv);
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            doIt();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        if ((requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) &&
                (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED)) {
            doIt();
        }
    }

    private void doIt() {
        tv.setText(String.format("Medium kann%s entfernt werden\n\n",
                Environment.isExternalStorageRemovable()
                        ? "" : " nicht"));
        // Status abfragen
        final String state = Environment.getExternalStorageState();
        final boolean canRead;
        final boolean canWrite;
        switch (state) {
            case Environment.MEDIA_MOUNTED:
                canRead = true;
                canWrite = true;
                break;
            case Environment.MEDIA_MOUNTED_READ_ONLY:
                canRead = true;
                canWrite = false;
                break;
            default:
                canRead = false;
                canWrite = false;
        }
        tv.append(String.format("Lesen ist%s möglich\n\n",
                canRead ? "" : " nicht"));
        tv.append(String.format("Schreiben ist%s möglich\n\n",
                canWrite ? "" : " nicht"));
        // Wurzelverzeichnis des externen Mediums
        File dirBase = Environment.getExternalStorageDirectory();
        tv.append(String.format("getExternalStorageDirectory(): %s\n\n",
                dirBase.getAbsolutePath()));
        // App-spezifischen Pfad hinzufügen
        File dirAppBase = new File(dirBase.getAbsolutePath()
                + File.separator
                + "Android" + File.separator + "data" + File.separator
                + getClass().getPackage().getName() + File.separator
                + "files");
        // ggf. Verzeichnisse anlegen
        if (!dirAppBase.mkdirs()) {
            tv.append(String.format("alle Unterverzeichnisse " +
                            "von %s schon vorhanden\n\n",
                    dirAppBase.getAbsolutePath()));
        }
        // App-spezifisches Basisverzeichnis erfragen
        File f1 = getExternalFilesDir(null);
        if (f1 != null) {
            tv.append(String.format("getExternalFilesDir(null): %s\n\n",
                    f1.getAbsolutePath()));
        }
        // App-spezifisches Verzeichnis für Bilder erfragen
        File f2 = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (f2 != null) {
            tv.append(String.format("getExternalFilesDir(Environment" +
                            ".DIRECTORY_PICTURES): %s\n\n",
                    f2.getAbsolutePath()));
        }
        // Pfad auf öffentliches Verzeichnis für Bilder
        File dirPublicPictures = Environment
                .getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
        // ggf. Verzeichnisse anlegen
        if (!dirPublicPictures.mkdirs()) {
            tv.append(String.format(
                    "alle Unterverzeichnisse von %s schon vorhanden\n\n",
                    dirPublicPictures.getAbsolutePath()));
        }
        // Grafik erzeugen und speichern
        File file = new File(dirPublicPictures, "grafik.png");
        try (FileOutputStream fos =
                     new FileOutputStream(file)) {
            saveBitmap(fos);
        } catch (IOException e) {
            Log.e(TAG, "new FileOutputStream()", e);
        }
    }

    private void saveBitmap(OutputStream out) {
        // Grafik erzeugen
        int w = 100;
        int h = 100;
        Bitmap bm = Bitmap.createBitmap(w, h, Config.RGB_565);
        Canvas c = new Canvas(bm);
        Paint paint = new Paint();
        paint.setTextAlign(Align.CENTER);
        paint.setColor(Color.WHITE);
        c.drawRect(0, 0, w - 1, h - 1, paint);
        paint.setColor(Color.BLUE);
        c.drawLine(0, 0, w - 1, h - 1, paint);
        c.drawLine(0, h - 1, w - 1, 0, paint);
        paint.setColor(Color.BLACK);
        c.drawText("Hallo Android!", w / 2, h / 2, paint);
        // und speichern
        bm.compress(CompressFormat.PNG, 100, out);
    }
}