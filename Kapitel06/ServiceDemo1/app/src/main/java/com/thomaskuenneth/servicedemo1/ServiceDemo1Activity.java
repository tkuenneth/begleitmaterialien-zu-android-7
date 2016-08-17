package com.thomaskuenneth.servicedemo1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ServiceDemo1Activity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, DemoService.class);
        startService(intent);
        finish();
    }
}