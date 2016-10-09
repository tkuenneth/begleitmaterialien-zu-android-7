package com.thomaskuenneth.kontaktedemo3;

import android.content.pm.PackageManager;
import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class KontakteDemo3 extends Activity {

    private static final String TAG =
            KontakteDemo3.class.getSimpleName();
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final int
            PERMISSIONS_REQUEST_READ_CONTACTS = 123;

    @Override
    protected void onStart() {
        super.onStart();
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]
                            {Manifest.permission.READ_CONTACTS,
                                    Manifest.permission.WRITE_CONTACTS},
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
                (grantResults.length == 2
                        && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED
                        && grantResults[1] ==
                        PackageManager.PERMISSION_GRANTED
                )) {
            doIt();
        }
    }

    private void doIt() {
        // Content Resolver
        ContentResolver contentResolver = getContentResolver();
        // nach "Testperson" suchen
        String[] mainQueryProjection = {ContactsContract.Contacts._ID};
        String mainQuerySelection =
                ContactsContract.Contacts.IN_VISIBLE_GROUP
                        + " = ?" + " AND " + ContactsContract.Contacts.DISPLAY_NAME
                        + " is ?";
        String[] mainQuerySelectionArgs = new String[]{"1", "Testperson"};
        Cursor mainQueryCursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI, mainQueryProjection,
                mainQuerySelection, mainQuerySelectionArgs, null);
        if (mainQueryCursor != null) {
            if (mainQueryCursor.moveToNext()) {
                String contactId = mainQueryCursor.getString(0);
                Log.d(TAG, "===> Testperson gefunden"
                        + " (" + contactId + ")");
                updateOrInsertBirthday(contentResolver, contactId);
            } else {
                Log.d(TAG, "Testperson nicht gefunden");
            }
            mainQueryCursor.close();
        }
        // Activity beenden
        finish();
    }

    private void updateOrInsertBirthday(ContentResolver contentResolver,
                                        String contactId) {
        String[] dataQueryProjection = new String[]{
                ContactsContract.CommonDataKinds.Event._ID,
                ContactsContract.CommonDataKinds.Event.START_DATE};
        String dataQuerySelection = ContactsContract.Data.CONTACT_ID
                + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"
                + " AND " + ContactsContract.CommonDataKinds.Event.TYPE
                + " = ?";
        String[] dataQuerySelectionArgs = new String[]{
                contactId,
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
                Integer.toString(ContactsContract.CommonDataKinds
                        .Event.TYPE_BIRTHDAY)};
        // Gibt es einen Geburtstag zu Kontakt #contactId?
        Cursor dataQueryCursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI, dataQueryProjection,
                dataQuerySelection, dataQuerySelectionArgs, null);
        if (dataQueryCursor != null) {
            if (dataQueryCursor.moveToNext()) {
                // ja, Eintrag gefunden
                String dataId = dataQueryCursor.getString(0);
                String date = dataQueryCursor.getString(1);
                Log.d(TAG, "Geburtstag (_id=" + dataId + "): " + date);
                // Jahr um 1 verringern
                try {
                    Date d = DATE_FORMAT.parse(date);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(d);
                    cal.add(Calendar.YEAR, -1);
                    d = cal.getTime();
                    date = DATE_FORMAT.format(d);
                    Log.d(TAG, "neues Geburtsdatum: " + date);
                    // abelle aktualisieren
                    String updateWhere =
                            ContactsContract.CommonDataKinds.Event._ID
                                    + " = ?"
                                    + " AND "
                                    + ContactsContract.Data.MIMETYPE
                                    + " = ?"
                                    + " AND "
                                    + ContactsContract.CommonDataKinds.Event.TYPE
                                    + " = ?";
                    String[] updateSelectionArgs = new String[]{
                            dataId,
                            ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
                            Integer.toString(
                                    ContactsContract.CommonDataKinds
                                            .Event.TYPE_BIRTHDAY)};
                    ContentValues values = new ContentValues();
                    values.put(
                            ContactsContract.CommonDataKinds.Event.START_DATE,
                            date);
                    int numRows = contentResolver.update(
                            ContactsContract.Data.CONTENT_URI, values,
                            updateWhere, updateSelectionArgs);
                    Log.d(TAG, "update() war "
                            + ((numRows == 0) ? "nicht " : "")
                            + "erfolgreich");
                } catch (ParseException e) {
                    Log.e(TAG, date, e);
                }
            } else {
                Log.d(TAG, "keinen Geburtstag gefunden");
                // Strings für die Suche nach RawContacts
                String[] rawProjection = new String[]{RawContacts._ID};
                String rawSelection = RawContacts.CONTACT_ID + " = ?";
                String[] rawSelectionArgs = new String[]{contactId};
                // Werte für Tabellenzeile vorbereiten
                ContentValues values = new ContentValues();
                values.put(ContactsContract.CommonDataKinds.Event.START_DATE,
                        DATE_FORMAT.format(new Date()));
                values.put(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE);
                values.put(ContactsContract.CommonDataKinds.Event.TYPE,
                        ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY);
                // alle RawContacts befüllen
                Cursor c = contentResolver.query(RawContacts.CONTENT_URI,
                        rawProjection, rawSelection, rawSelectionArgs, null);
                if (c != null) {
                    while (c.moveToNext()) {
                        String rawContactId = c.getString(0);
                        values.put(
                                ContactsContract.CommonDataKinds.Event.RAW_CONTACT_ID,
                                rawContactId);
                        Uri uri = contentResolver.insert(
                                ContactsContract.Data.CONTENT_URI, values);
                        Log.d(TAG,
                                "   ---> Hinzufügen des Geburtstags "
                                        + "für RawContacts-Id "
                                        + rawContactId
                                        + " war"
                                        + ((uri == null)
                                        ? " nicht erfolgreich"
                                        : " erfolgreich"));
                    }
                    c.close();
                }
            }
            dataQueryCursor.close();
        }
    }
}
