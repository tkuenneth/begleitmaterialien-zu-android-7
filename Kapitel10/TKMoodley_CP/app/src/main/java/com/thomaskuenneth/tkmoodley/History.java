package com.thomaskuenneth.tkmoodley;

import android.app.ListActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CursorAdapter;

public class History extends ListActivity {

    private CursorAdapter ca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerForContextMenu(getListView());
        ca = new TKMoodleyAdapter(this);
        setListAdapter(ca);
        updateList();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_good:
                update(info.id, TKMoodleyOpenHandler.MOOD_FINE);
                updateList();
                return true;
            case R.id.menu_ok:
                update(info.id, TKMoodleyOpenHandler.MOOD_OK);
                updateList();
                return true;
            case R.id.menu_bad:
                update(info.id, TKMoodleyOpenHandler.MOOD_BAD);
                updateList();
                return true;
            case R.id.menu_delete:
                delete(info.id);
                updateList();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void updateList() {
        Cursor cursor = getContentResolver()
                .query(TKMoodleyProvider.CONTENT_URI, null, null,
                        null, TKMoodleyOpenHandler.MOOD_TIME + " DESC");
        ca.changeCursor(cursor);
    }

    private void update(long id, int mood) {
        Uri uri = Uri.withAppendedPath(TKMoodleyProvider.CONTENT_URI,
                Long.toString(id));
        ContentValues values = new ContentValues();
        values.put(TKMoodleyOpenHandler.MOOD_MOOD, mood);
        getContentResolver().update(uri, values, null, null);
    }

    private void delete(long id) {
        Uri uri = Uri.withAppendedPath(TKMoodleyProvider.CONTENT_URI,
                Long.toString(id));
        getContentResolver().delete(uri, null, null);
    }
}
