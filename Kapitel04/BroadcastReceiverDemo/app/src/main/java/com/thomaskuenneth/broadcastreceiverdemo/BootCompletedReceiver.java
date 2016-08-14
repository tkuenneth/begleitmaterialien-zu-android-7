package com.thomaskuenneth.broadcastreceiverdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.text.DateFormat;
import java.util.Date;

public class BootCompletedReceiver extends BroadcastReceiver {

    private static final int ID = 42;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Benachrichtigung zusammenbauen
            String msg =
                    DateFormat.getDateTimeInstance().format(new Date());
            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(R.drawable.ic_launcher).
                    setContentTitle(context.getString(R.string.app_name)).
                    setContentText(msg).
                    setWhen(System.currentTimeMillis());
            Notification n = builder.build();
            // anzeigen
            NotificationManager m = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            m.notify(ID, n);
        }
    }
}
