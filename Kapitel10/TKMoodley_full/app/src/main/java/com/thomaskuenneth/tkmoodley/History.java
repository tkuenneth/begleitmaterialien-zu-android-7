package com.thomaskuenneth.tkmoodley;

import android.app.ListActivity;
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
    private TKMoodleyOpenHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerForContextMenu(getListView());
        ca = new TKMoodleyAdapter(this);
        setListAdapter(ca);
        dbHandler = new TKMoodleyOpenHandler(this);
        updateList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHandler.close();
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
                dbHandler.update(info.id,
                        TKMoodleyOpenHandler.MOOD_FINE);
                updateList();
                return true;
            case R.id.menu_ok:
                dbHandler.update(info.id,
                        TKMoodleyOpenHandler.MOOD_OK);
                updateList();
                return true;
            case R.id.menu_bad:
                dbHandler.update(info.id,
                        TKMoodleyOpenHandler.MOOD_BAD);
                updateList();
                return true;
            case R.id.menu_delete:
                dbHandler.delete(info.id);
                updateList();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void updateList() {
        // Cursor tauschen - der alte wird geschlossen
        ca.changeCursor(dbHandler.query());
    }
}
