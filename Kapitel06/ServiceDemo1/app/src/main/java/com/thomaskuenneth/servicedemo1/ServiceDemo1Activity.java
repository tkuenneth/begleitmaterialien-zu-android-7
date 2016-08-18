package com.thomaskuenneth.servicedemo1;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class ServiceDemo1Activity extends Activity {

    private static final int RQ_CALL_LOG = 123;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkSelfPermission(Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]
                            {Manifest.permission.READ_CALL_LOG},
                    RQ_CALL_LOG);
        } else {
            startService();
        }
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case RQ_CALL_LOG: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startService();
                }
                finish();
            }
        }
    }

    private void startService() {
        Intent intent = new Intent(this, DemoService.class);
        startService(intent);
    }
}