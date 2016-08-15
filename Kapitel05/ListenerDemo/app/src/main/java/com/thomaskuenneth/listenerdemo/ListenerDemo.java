package com.thomaskuenneth.listenerdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class ListenerDemo extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Oberfläche anzeigen
        setContentView(R.layout.main);

        // Referenzen ermitteln
        final TextView textview = (TextView) findViewById(R.id.textview);
        final CheckBox checkbox = (CheckBox) findViewById(R.id.checkbox);

        // OnClickListener für CheckBox registrieren
//        checkbox.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                textview.setText(Boolean.toString(checkbox.isChecked()));
//            }
//        });

        final Button status = (Button) findViewById(R.id.status);
        // OnClickListener für Button registrieren
        status.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                checkbox.setChecked(!checkbox.isChecked());
            }
        });

        checkbox.setOnCheckedChangeListener(new CompoundButton
                .OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                textview.setText(
                        Boolean.toString(checkbox.isChecked()));
            }
        });

    }
}
