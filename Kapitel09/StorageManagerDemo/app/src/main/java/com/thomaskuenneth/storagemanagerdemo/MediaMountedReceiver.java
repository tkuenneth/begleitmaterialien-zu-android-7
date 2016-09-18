package com.thomaskuenneth.storagemanagerdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.storage.StorageVolume;
import android.widget.Toast;

public class MediaMountedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            StorageVolume volume = intent.getParcelableExtra(
                    StorageVolume.EXTRA_STORAGE_VOLUME);
            if (volume != null) {
                Toast.makeText(context, volume.getDescription(context),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
