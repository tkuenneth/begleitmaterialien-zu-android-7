package com.thomaskuenneth.shareviademo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DemoSender extends Activity {

    private static final String TAG = DemoSender.class.getSimpleName();

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        // Wurde ein Intent übermittelt?
        Intent intent = getIntent();
        if (intent != null) {
            if (Intent.ACTION_SEND.equals(intent.getAction())) {
                // ja, dann Benutzeroberfläche anzeigen
                setContentView(R.layout.demosender);
                ImageView imageView =
                        (ImageView) findViewById(R.id.image);
                // Uri des Bildes
                final Uri imageUri = (Uri) intent.getExtras().get(
                        Intent.EXTRA_STREAM);
                // Button
                final Button button = (Button) findViewById(R.id.button);
                button.setOnClickListener((v) ->
                        share(imageUri));
                try {
                    // Bitmap erzeugen
                    Bitmap bm1 = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), imageUri);
                    // in Graustufen umwandeln
                    Bitmap bm2 = toGrayscale(bm1);
                    // und anzeigen
                    imageView.setImageBitmap(bm2);
                } catch (IOException e) { // FileNotFoundException
                    Log.e(TAG, e.getClass().getSimpleName(), e);
                }
            }
        } else {
            // nein, dann beenden
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

    private void share(Uri uri) {
        List<Intent> intentList = new ArrayList<>();
        // Activities suchen, die auf ACTION_SEND reagieren
        PackageManager pm = getPackageManager();
        Intent targetIntent = new Intent(Intent.ACTION_SEND);
        targetIntent.setType("image/?");
        List<ResolveInfo> activities =
                pm.queryIntentActivities(targetIntent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        // Intents der Liste hinzufügen
        for (ResolveInfo info : activities) {
            String packageName = info.activityInfo.packageName;
            if (getPackageName().equals(packageName)) {
                // die eigene App ausblenden
                continue;
            }
            // das zu feuernde Intent bauen und hinzufügen
            Intent intent = new Intent(Intent.ACTION_SEND, uri);
            intent.setType("image/?");
            intent.setPackage(packageName);
            intentList.add(intent);
        }
        // Chooser aufrufen
        int size = intentList.size();
        if (size > 0) {
            Intent chooserIntent =
                    Intent.createChooser(intentList.remove(
                            size - 1), getString(R.string.share_via));
            Parcelable[] p = new Parcelable[size - 1];
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                    intentList.toArray(p));
            startActivity(chooserIntent);
        }
        finish();
    }
}
