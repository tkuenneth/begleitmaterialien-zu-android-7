package com.thomaskuenneth.rr;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.io.File;

class RRListAdapter extends ArrayAdapter<File> {

    private final Context context;

    RRListAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        this.context = context;
        findAndAddFiles();
    }

    private void findAndAddFiles() {
        File d = RR.getBaseDir(context);
        File[] files = d.listFiles((dir, filename) -> {
            if (!filename.toLowerCase().endsWith(RRFile.EXT_3GP)) {
                return false;
            }
            File f = new File(dir, filename);
            return f.canRead() && !f.isDirectory();
        });
        if (files != null) {
            for (File f : files) {
                add(new RRFile(f.getParentFile(), f.getName()));
            }
        }
    }
}
