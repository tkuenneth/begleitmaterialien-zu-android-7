package com.thomaskuenneth.rr;

import android.util.Log;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

class RRFile extends File {

    private static final String TAG = RRFile.class.getSimpleName();

    static final String EXT_3GP = ".3gp";

    RRFile(File path, String name) {
        super(path, name);
    }

    @Override
    public String toString() {
        String result = getName().toLowerCase();
        result = result.substring(0, result.indexOf(EXT_3GP));
        try {
            Date d = new Date(Long.parseLong(result));
            result = DateFormat.getInstance().format(d);
        } catch (Throwable tr) {
            Log.e(TAG, "Fehler beim Umwandeln oder Formatieren", tr);
        }
        return result;
    }
}
