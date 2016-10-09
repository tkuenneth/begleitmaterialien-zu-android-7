package com.thomaskuenneth.kontaktedemo1;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.TextView;

public class KontakteDemo1 extends Activity {

    private static final int
            PERMISSIONS_REQUEST_READ_CONTACTS = 123;

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv = (TextView) findViewById(R.id.tv);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]
                            {Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            doIt();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        if ((requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) &&
                (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED)) {
            doIt();
        }
    }

    private void doIt() {
        ContentResolver contentResolver = getContentResolver();
        // IDs und Namen aller sichtbaren Kontakte ermitteln
        String[] mainQueryProjection = {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME};
        String mainQuerySelection =
                ContactsContract.Contacts.IN_VISIBLE_GROUP
                        + " = ?";
        String[] mainQuerySelectionArgs = new String[]{"1"};
        Cursor mainQueryCursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                mainQueryProjection,
                mainQuerySelection,
                mainQuerySelectionArgs, null);
        // Trefferliste abarbeiten...
        if (mainQueryCursor != null) {
            while (mainQueryCursor.moveToNext()) {
                String contactId = mainQueryCursor.getString(0);
                String displayName = mainQueryCursor.getString(1);
                tv.append("===> " + displayName
                        + " (" + contactId + ")\n");
            }
            mainQueryCursor.close();
        }
    }
}