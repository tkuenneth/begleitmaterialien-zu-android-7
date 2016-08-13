package com.thomaskuenneth.tierkreiszeichen;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Dies ist die Listenansicht der App. Sie zeigt alle Sternzeichen nach
 * Kalendermonaten sortiert an.
 *
 * @author Thomas Künneth
 */
public class TierkreiszeichenActivity extends ListActivity implements
        OnItemClickListener {

    private TierkreiszeichenAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // hier werden die Tierkreiszeichen gespeichert
        adapter = new TierkreiszeichenAdapter(this);
        setListAdapter(adapter);
        // auf das Antippen von Listenelementen
        // reagieren
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Tierkreiszeichen zeichen = (Tierkreiszeichen) adapter.getItem(position);
        String url = getString(R.string.wikipedia_url, zeichen.getName(this));
        // eine Webseite anzeigen
        Intent viewIntent = new Intent("android.intent.action.VIEW",
                Uri.parse(url));
        startActivity(viewIntent);
    }
}