package com.thomaskuenneth.kontaktedemo2;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.TextView;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KontakteDemo2 extends Activity {

    private static final String TAG =
            KontakteDemo2.class.getSimpleName();

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
                infosAuslesen(contentResolver, contactId);
            }
            mainQueryCursor.close();
        }
    }
    private void infosAuslesen(ContentResolver contentResolver,
                               String contactId) {
        String[] dataQueryProjection = new String[]{
                ContactsContract.CommonDataKinds.Event.TYPE,
                ContactsContract.CommonDataKinds.Event.START_DATE,
                ContactsContract.CommonDataKinds.Event.LABEL};
        String dataQuerySelection = ContactsContract.Data.CONTACT_ID
                + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] dataQuerySelectionArgs = new String[]{contactId,
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE};
        Cursor dataQueryCursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI, dataQueryProjection,
                dataQuerySelection, dataQuerySelectionArgs, null);
        if (dataQueryCursor != null) {
            while (dataQueryCursor.moveToNext()) {
                int type = dataQueryCursor.getInt(0);
                String label = dataQueryCursor.getString(2);
                if (ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY
                        == type) {
                    String stringBirthday = dataQueryCursor.getString(1);
                    tv.append("     birthday: " + stringBirthday + "\n");
                } else {
                    String stringAnniversary = dataQueryCursor.getString(1);
                    tv.append("     event: " + stringAnniversary + " (type="
                            + type + ", label=" + label + ")\n");
                    if (ContactsContract.CommonDataKinds
                            .Event.TYPE_ANNIVERSARY == type) {
                        tv.append("     TYPE_ANNIVERSARY\n");
                    } else if (ContactsContract.CommonDataKinds
                            .Event.TYPE_CUSTOM == type) {
                        tv.append("     TYPE_CUSTOM\n");
                    } else {
                        tv.append("     TYPE_OTHER\n");
                    }
                }
            }
            dataQueryCursor.close();
        }
    }

    /**
     * Datum im Format jjjjmmtt, also 19700829
     */
    private static final SimpleDateFormat FORMAT_YYYYMMDD
            = new SimpleDateFormat("yyyyMMdd");

    public static Date getDateFromString1(String string) {
        Date result = null;
        if (string != null) {
            Pattern p = Pattern.compile(
                    "(\\d\\d\\d\\d).*(\\d\\d).*(\\d\\d)",
                    Pattern.DOTALL);
            Matcher m = p.matcher(string.subSequence(0,
                    string.length()));
            if (m.matches()) {
                String date = m.group(1) + m.group(2) + m.group(3);
                try {
                    result = FORMAT_YYYYMMDD.parse(date);
                } catch (Throwable tr) {
                    Log.e(TAG, "getDateFromString1()", tr);
                }
            }
        }
        return result;
    }
}