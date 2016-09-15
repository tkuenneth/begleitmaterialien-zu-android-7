package com.thomaskuenneth.schrittzaehler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SchrittzaehlerActivity.updateSharedPrefs(context, 0);
    }
}
