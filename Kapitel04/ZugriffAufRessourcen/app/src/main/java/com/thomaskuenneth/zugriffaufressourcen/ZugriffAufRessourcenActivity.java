package com.thomaskuenneth.zugriffaufressourcen;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import java.util.Calendar;

public class ZugriffAufRessourcenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zugriff_auf_ressourcen);

        final TextView textview = (TextView) findViewById(R.id.textview);
        textview.append("getString(R.string.app_name): "
                + getString(R.string.app_name));
        Calendar cal = Calendar.getInstance();
        textview.append("\n\n" + getString(R.string.datum,
                "Heute ist der",
                cal.get(Calendar.DAY_OF_YEAR),
                cal.get(Calendar.YEAR)));
        boolean b1 = getResources().getBoolean(R.bool.bool1);
        boolean b2 = getResources().getBoolean(R.bool.bool2);
        textview.append("\n\nb1=" + b1 + ", b2=" + b2);
        textview.append("\n\nadams=" + getResources().getInteger(R.integer.adams));
        textview.setTextColor(getResources().getColor(R.color.eine_farbe,
                getTheme()));
    }
}
