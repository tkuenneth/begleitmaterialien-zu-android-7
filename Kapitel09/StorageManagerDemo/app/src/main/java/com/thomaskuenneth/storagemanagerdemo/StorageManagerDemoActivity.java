package com.thomaskuenneth.storagemanagerdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.support.v4.provider.DocumentFile;
import android.widget.TextView;

import java.util.List;

public class StorageManagerDemoActivity extends Activity {

    private static final int REQUEST_CODE = 123;

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv = (TextView) findViewById(R.id.tv);
        tv.setText("");
        StorageManager m = getSystemService(StorageManager.class);
        List<StorageVolume> volumes = m.getStorageVolumes();
        for (StorageVolume volume : volumes) {
            appendLine(String.format("%s",
                    volume.getDescription(this)));
            appendLine(String.format("   getState(): %s",
                    volume.getState()));
            appendLine(String.format("   isPrimary(): %s",
                    volume.isPrimary()));
            appendLine(String.format("   isRemovable(): %s",
                    volume.isRemovable()));
            appendLine(String.format("   isEmulated(): %s",
                    volume.isEmulated()));
            if (volume.isPrimary()) {
                Intent intent = volume.createAccessIntent(
                        Environment.DIRECTORY_DOWNLOADS);
                startActivityForResult(intent, REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE) &&
                (resultCode == RESULT_OK) &&
                (data != null)) {
            DocumentFile dir = DocumentFile.fromTreeUri(this,
                    data.getData());
            appendLine("\n" + dir.getUri().toString() + "\n");
            for (DocumentFile file : dir.listFiles()) {
                appendLine(file.getName());
            }
        }
    }

    private void appendLine(String t) {
        tv.append(t);
        tv.append("\n");
    }
}
