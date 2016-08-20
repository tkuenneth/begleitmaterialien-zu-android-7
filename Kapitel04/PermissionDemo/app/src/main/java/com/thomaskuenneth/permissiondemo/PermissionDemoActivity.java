package com.thomaskuenneth.permissiondemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PermissionDemoActivity extends Activity {

    private static final String TAG =
            PermissionDemoActivity.class.getSimpleName();

    private static final int RQ_CALL_LOG = 123;

    private TextView tv;
    private Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_demo);
        tv = (TextView) findViewById(R.id.tv);
        bt = (Button) findViewById(R.id.bt);
        bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                requestPermission();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        bt.setVisibility(View.GONE);
        if (checkSelfPermission(Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_CALL_LOG)) {
                tv.setText(R.string.explain1);
                bt.setVisibility(View.VISIBLE);
            } else {
                requestPermission();
            }
        } else {
            outputMissedCalls();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        if (requestCode == RQ_CALL_LOG) {
            bt.setVisibility(View.GONE);
            if ((grantResults.length > 0)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                outputMissedCalls();
            } else {
                tv.setText(R.string.explain2);
            }
        }
    }

    private void requestPermission() {
        requestPermissions(new String[]
                {Manifest.permission.READ_CALL_LOG},
                RQ_CALL_LOG);
    }

    private void outputMissedCalls() {
        tv.setText(getString(R.string.template,
                getMissedCalls()));
    }

    private int getMissedCalls() {
        int missedCalls = 0;
        String[] projection = {CallLog.Calls._ID};
        String selection = CallLog.Calls.TYPE + " = ?";
        String[] selectionArgs = {Integer.toString(CallLog.Calls.MISSED_TYPE)};
        try {
            Cursor c = getContentResolver().query(CallLog.Calls.CONTENT_URI,
                    projection, selection, selectionArgs, null);
            if (c != null) {
                missedCalls = c.getCount();
                c.close();
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getMissedCalls()", e);
        }
        return missedCalls;
    }
}
