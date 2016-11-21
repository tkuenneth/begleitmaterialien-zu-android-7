package com.thomaskuenneth.appshortcutdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.widget.Toast;

import java.util.Arrays;

public class AppShortcutActivity extends Activity {

    private static final String ACTION =
            "com.thomaskuenneth.appshortcutdemo.AppShortcut";

    @Override
    protected void onStart() {
        super.onStart();
        Intent i = getIntent();
        if ((i != null) &&
                (ACTION.equals(i.getAction()))) {
            Uri uri = i.getData();
            String s = (uri == null) ? "<leer>" : uri.toString();
            Toast.makeText(this, s,
                    Toast.LENGTH_LONG).show();
        }
        // dynamisches Shortcut
        Intent intent = new Intent(this, AppShortcutActivity.class);
        intent.setAction(ACTION);
        intent.setData(Uri.parse("https://www.rheinwerk-verlag.de/"));
        ShortcutManager shortcutManager =
                getSystemService(ShortcutManager.class);
        ShortcutInfo shortcut = new ShortcutInfo.Builder(this,
                "dynamic1")
                .setShortLabel(getString(R.string.dynamic_shortcut))
                .setIcon(Icon.createWithResource(this,
                        R.drawable.ic_cloud))
                .setIntent(intent)
                .build();
        shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut));
        finish();
    }
}
