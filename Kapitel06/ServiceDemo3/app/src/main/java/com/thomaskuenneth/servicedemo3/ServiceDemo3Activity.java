package com.thomaskuenneth.servicedemo3;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ServiceDemo3Activity extends Activity {

    public static final int MSG_FAKULTAET_IN = 1;
    public static final int MSG_FAKULTAET_OUT = 2;

    private static final String TAG = ServiceDemo3Activity.class.getSimpleName();

    private Messenger mService = null;
    private TextView textview;

    private final Messenger mMessenger = new Messenger(new IncomingHandler());

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FAKULTAET_OUT:
                    int n = msg.arg1;
                    int fakultaet = msg.arg2;
                    Log.d(TAG, "Fakultaet: " + fakultaet);
                    textview.setText(getString(R.string.template, n, fakultaet));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textview = (TextView) findViewById(R.id.textview);
        final EditText edittext = (EditText) findViewById(R.id.edittext);
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService != null) {
                    int n = Integer.parseInt(edittext.getText().toString());
                    Message msg = Message.obtain(null, MSG_FAKULTAET_IN, n, 0);
                    msg.replyTo = mMessenger;
                    try {
                        mService.send(msg);
                    } catch (RemoteException e) {
                        Log.d(TAG, "send()", e);
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ComponentName c = new ComponentName("com.thomaskuenneth.servicedemo3_service",
                "com.thomaskuenneth.servicedemo3_service.RemoteService");
        Intent i = new Intent();
        i.setComponent(c);
        if (!bindService(i, mConnection, Context.BIND_AUTO_CREATE)) {
            Log.d(TAG, "bindService() nicht erfolgreich");
            unbindService(mConnection);
            mService = null;
            finish();
        }
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