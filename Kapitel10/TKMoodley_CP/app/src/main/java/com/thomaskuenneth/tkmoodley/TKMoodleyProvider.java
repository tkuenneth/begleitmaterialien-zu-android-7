package com.thomaskuenneth.tkmoodley;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class TKMoodleyProvider extends ContentProvider {

    public static final String AUTHORITY =
            TKMoodleyProvider.class.getName().toLowerCase();

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/"
            + TKMoodleyOpenHandler.TABLE_NAME_MOOD);

    private TKMoodleyOpenHandler dbHelper;

    private static final int MOOD = 1;
    private static final int MOOD_ID = 2;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY,
                TKMoodleyOpenHandler.TABLE_NAME_MOOD,
                MOOD);
        uriMatcher.addURI(AUTHORITY,
                TKMoodleyOpenHandler.TABLE_NAME_MOOD
                        + "/#", MOOD_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new TKMoodleyOpenHandler(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
        sqlBuilder.setTables(TKMoodleyOpenHandler.TABLE_NAME_MOOD);
        // Ein bestimmer Eintrag?
        if (uriMatcher.match(uri) == MOOD_ID) {
            sqlBuilder.appendWhere(TKMoodleyOpenHandler._ID + " = "
                    + uri.getPathSegments().get(1));
        }
        if (sortOrder == null || "".equals(sortOrder)) {
            sortOrder = TKMoodleyOpenHandler.MOOD_TIME;
        }
        Cursor c = sqlBuilder.query(db, projection,
                selection, selectionArgs,
                null, null, sortOrder);
        // bei Änderungen benachrichtigen
        Context ctx = getContext();
        if (ctx != null) {
            ContentResolver cr = ctx.getContentResolver();
            if (cr != null) {
                c.setNotificationUri(cr, uri);
            }
        }
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (uriMatcher.match(uri)) {
            case MOOD:
                count = db.update(TKMoodleyOpenHandler.TABLE_NAME_MOOD,
                        values, selection, selectionArgs);
                break;
            case MOOD_ID:
                count = db.update(
                        TKMoodleyOpenHandler.TABLE_NAME_MOOD,
                        values,
                        TKMoodleyOpenHandler._ID
                                + " = "
                                + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(selection)
                                ? " AND (" + selection + ')'
                                : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        notifyChange(uri);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (uriMatcher.match(uri)) {
            case MOOD:
                count = db.delete(TKMoodleyOpenHandler.TABLE_NAME_MOOD,
                        selection, selectionArgs);
                break;
            case MOOD_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(
                        TKMoodleyOpenHandler.TABLE_NAME_MOOD,
                        TKMoodleyOpenHandler._ID
                                + " = "
                                + id
                                + (!TextUtils.isEmpty(selection) ? " AND ("
                                + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        notifyChange(uri);
        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowID = db
                .insert(TKMoodleyOpenHandler.TABLE_NAME_MOOD,
                        "", values);
        if (rowID > 0) {
            Uri result = ContentUris.withAppendedId(CONTENT_URI,
                    rowID);
            notifyChange(result);
            return result;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case MOOD:
                // alle Einträge
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + "/"
                        + TKMoodleyOpenHandler.TABLE_NAME_MOOD;
            // einen bestimmten Eintrag
            case MOOD_ID:
                return "vnd.android.cursor.item/vnd." + AUTHORITY + "/"
                        + TKMoodleyOpenHandler.TABLE_NAME_MOOD;
            default:
                throw new IllegalArgumentException("Unsupported URI: "
                        + uri);
        }
    }

    private void notifyChange(Uri uri) {
        Context c = getContext();
        if (c != null) {
            ContentResolver cr = c.getContentResolver();
            if (cr != null) {
                cr.notifyChange(uri, null);
            }
        }
    }
}
