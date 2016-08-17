package com.thomaskuenneth.servicedemo2;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.thomaskuenneth.servicedemo2.LocalService.LocalBinder;

public class ServiceDemo2Activity extends Activity {

    private LocalService mService = null;

    // wird in onStart() und onStop() verwendet
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final TextView textview = (TextView) findViewById(R.id.textview);
        final EditText edittext = (EditText) findViewById(R.id.edittext);
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService != null) {
                    // Service aufrufen und Fakultät berechnen lassen
                    int n = Integer.parseInt(edittext.getText().toString());
                    int fak = mService.fakultaet(n);
                    textview.setText(getString(R.string.template, n, fak));
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, LocalService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mService != null) {
            unbindService(mConnection);
            mService = null;
        }
    }
}
