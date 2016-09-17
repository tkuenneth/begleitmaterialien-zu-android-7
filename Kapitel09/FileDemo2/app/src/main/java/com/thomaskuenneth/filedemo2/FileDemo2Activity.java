package com.thomaskuenneth.filedemo2;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileDemo2Activity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView tv = (TextView) findViewById(R.id.tv);
        tv.setText("");
        // 10 Dateien mit unterschiedlicher Länge anlegen
        for (int i = 1; i <= 10; i++) {
            String name = "Datei_" + Integer.toString(i);
            try (FileOutputStream fos =
                         openFileOutput(name, MODE_PRIVATE)) {
                // ergibt Datei_1, Datei_2, ...
                // ein Feld der Länge i mit dem Wert i füllen
                byte[] bytes = new byte[i];
                for (int j = 0; j < bytes.length; j++) {
                    bytes[j] = (byte) i;
                }
                fos.write(bytes);
            } catch (IOException t) {
                tv.append(name + ":\n" +
                        t.toString() + "\n");
            }
        }
        // Dateien ermitteln
        String[] files = fileList();
        // Verzeichnis ermitteln
        File dir = getFilesDir();
        for (String name : files) {
            File f = new File(dir, name);
            // Länge in Bytes ermitteln
            tv.append("Länge von " + name + " in Byte: "
                    + f.length() + "\n");
            // Datei löschen
            tv.append("Löschen " + (!f.delete() ? "nicht " : "")
                    + "erfolgreich\n");
        }
    }
}