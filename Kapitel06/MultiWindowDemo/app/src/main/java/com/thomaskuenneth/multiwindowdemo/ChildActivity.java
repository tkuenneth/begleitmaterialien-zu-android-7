package com.thomaskuenneth.multiwindowdemo;

import android.app.Activity;
import android.os.Bundle;

public class ChildActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child);
    }
}
