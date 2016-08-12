package com.thomaskuenneth.halloandroid;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView nachricht;
    private Button weiterFertig;
    private EditText eingabe;
    private boolean ersterKlick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nachricht = (TextView) findViewById(R.id.nachricht);
        weiterFertig = (Button) findViewById(R.id.weiter_fertig);
        eingabe = (EditText) findViewById(R.id.eingabe);
        eingabe.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (weiterFertig.isEnabled()) {
                    weiterFertig.performClick();
                }
                return true;
            }
        });

        ersterKlick = true;
        nachricht.setText(R.string.willkommen);
        weiterFertig.setText(R.string.weiter);

        weiterFertig.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ersterKlick) {
                    nachricht.setText(getString(R.string.hallo,
                            eingabe.getText()));
                    eingabe.setVisibility(View.INVISIBLE);
                    weiterFertig.setText(R.string.fertig);
                    ersterKlick = false;
                } else {
                    finish();
                }
            }
        });
        eingabe.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                weiterFertig.setEnabled(s.length() > 0);
            }
        });
        weiterFertig.setEnabled(false);
    }
}
