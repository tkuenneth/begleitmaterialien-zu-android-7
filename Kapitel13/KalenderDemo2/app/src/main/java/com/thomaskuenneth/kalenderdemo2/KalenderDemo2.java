package com.thomaskuenneth.kalenderdemo2;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CalendarContract.Events;
import android.util.Log;

public class KalenderDemo2 extends Activity {

    private static final String TAG =
            KalenderDemo2.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_READ_CALENDAR
            = 123;

    @Override
    protected void onStart() {
        super.onStart();
        if (checkSelfPermission(Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]
                            {Manifest.permission.READ_CALENDAR},
                    PERMISSIONS_REQUEST_READ_CALENDAR);
        } else {
            doIt();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        if ((requestCode == PERMISSIONS_REQUEST_READ_CALENDAR) &&
                (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED)) {
            doIt();
        } else {
            finish();
        }
    }

    private void doIt() throws SecurityException {
        Cursor c = getContentResolver().
                query(Events.CONTENT_URI,
                        null, null, null, null);
        if (c != null) {
            int indexId =
                    c.getColumnIndex(Events._ID);
            int indexTitle =
                    c.getColumnIndex(Events.TITLE);
            while (c.moveToNext()) {
                Log.d(TAG, "_ID: "
                        + c.getString(indexId));
                Log.d(TAG, "TITLE: "
                        + c.getString(indexTitle));
            }
            c.close();
        }
    }
}