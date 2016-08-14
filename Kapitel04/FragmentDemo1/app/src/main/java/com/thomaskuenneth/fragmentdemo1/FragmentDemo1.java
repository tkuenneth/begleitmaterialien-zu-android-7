package com.thomaskuenneth.fragmentdemo1;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class FragmentDemo1 extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView tv = (TextView) findViewById(R.id.textview);
        tv.setText(getString(R.string.text2));
    }
}