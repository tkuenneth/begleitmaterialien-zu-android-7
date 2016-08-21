package com.thomaskuenneth.servicedemo3_service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class RemoteService extends Service {

    private static final String TAG = RemoteService.class.getSimpleName();

    public static final int MSG_FAKULTAET_IN = 1;
    public static final int MSG_FAKULTAET_OUT = 2;

    private final Messenger mMessenger =
            new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    private static class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FAKULTAET_IN:
                    Integer n = msg.arg1;
                    Log.d(TAG, "Eingabe: " + n);
                    int fak = fakultaet(n);
                    Messenger m = msg.replyTo;
                    Message msg2 = Message.obtain(null,
                            MSG_FAKULTAET_OUT, n, fak);
                    try {
                        m.send(msg2);
                    } catch (RemoteException e) {
                        Log.e(TAG, "send()", e);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

        private int fakultaet(int n) {
            if (n <= 0) {
                return 1;
            }
            return n * fakultaet(n - 1);
        }
    }
}
