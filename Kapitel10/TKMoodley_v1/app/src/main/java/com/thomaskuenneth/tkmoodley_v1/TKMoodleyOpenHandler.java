package com.thomaskuenneth.tkmoodley_v1;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class TKMoodleyOpenHandler extends SQLiteOpenHelper {
    private static final String TAG = TKMoodleyOpenHandler.class
            .getSimpleName();

    // Name und Version der Datenbank
    private static final String DATABASE_NAME = "tkmoodley.db";
    private static final int DATABASE_VERSION = 1;

    // Name und Attribute der abelle "mood"
    private static final String _ID = "_id";
    private static final String TABLE_NAME_MOOD = "mood";
    private static final String MOOD_TIME = "timeMillis";
    private static final String MOOD_MOOD = "mood";

    // Konstanten für die Stimmungen
    static final int MOOD_FINE = 1;
    static final int MOOD_OK = 2;
    static final int MOOD_BAD = 3;

    // abelle mood anlegen
    private static final String TABLE_MOOD_CREATE = "CREATE TABLE "
            + TABLE_NAME_MOOD + " (" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + MOOD_TIME
            + " INTEGER, " + MOOD_MOOD + " INTEGER);";
    // abelle mood löschen
    private static final String TABLE_MOOD_DROP =
            "DROP TABLE IF EXISTS " + TABLE_NAME_MOOD;

    TKMoodleyOpenHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_MOOD_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        Log.w(TAG, "Upgrade der Datenbank von Version "
                + oldVersion + " zu "
                + newVersion + "; alle Daten werden gelöscht");
        db.execSQL(TABLE_MOOD_DROP);
        onCreate(db);
    }

    void insert(int mood, long timeMillis) {
        long rowId = -1;
        try {
            // Datenbank öffnen
            SQLiteDatabase db = getWritableDatabase();
            Log.d(TAG, "Pfad: " + db.getPath());
            // die zu speichernden Werte
            ContentValues values = new ContentValues();
            values.put(MOOD_MOOD, mood);
            values.put(MOOD_TIME, timeMillis);
            // in die abelle mood einfügen
            rowId = db.insert(TABLE_NAME_MOOD, null, values);
        } catch (SQLiteException e) {
            Log.e(TAG, "insert()", e);
        } finally {
            Log.d(TAG, "insert(): rowId=" + rowId);
        }
    }
}
