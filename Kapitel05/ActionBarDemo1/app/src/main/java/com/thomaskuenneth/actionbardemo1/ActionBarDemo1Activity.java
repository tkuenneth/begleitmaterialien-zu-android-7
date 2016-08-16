package com.thomaskuenneth.actionbardemo1;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class ActionBarDemo1Activity extends Activity {

    private TextView textview;

    @Override
    protected void onStart() {
        super.onStart();
        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textview = (TextView) findViewById(R.id.textview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        textview.setText(item.getTitle());
        if (android.R.id.home == item.getItemId()) {
            Toast.makeText(this, R.string.app_name, Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}