package com.thomaskuenneth.filedemo1;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileDemo1Activity extends Activity {

    private static final String TAG =
            FileDemo1Activity.class.getSimpleName();
    private static final String FILENAME = TAG + ".txt";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // das Eingabefeld
        final EditText edit = (EditText) findViewById(R.id.edit);
        // Leeren
        final Button bClear = (Button) findViewById(R.id.clear);
        bClear.setOnClickListener((e) -> edit.setText(""));
        // Laden
        final Button bLoad = (Button) findViewById(R.id.load);
        bLoad.setOnClickListener((e) -> edit.setText(load()));
        // Speichern
        final Button bSave = (Button) findViewById(R.id.save);
        bSave.setOnClickListener((e) -> save(edit.getText().toString()));
        // Ablageort der Dateien ermitteln und ausgeben
        File f = getFilesDir();
        Log.d(TAG, "getFilesDir(): " + f.getAbsolutePath());
    }

    private String load() {
        StringBuilder sb = new StringBuilder();
        try (FileInputStream fis = openFileInput(FILENAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {
            String s;
            // Datei zeilenweise lesen
            while ((s = br.readLine()) != null) {
                // ggf. Zeilenumbruch hinzufügen
                if (sb.length() > 0) {
                    sb.append('\n');
                }
                sb.append(s);
            }
        } catch (IOException t) {
            Log.e(TAG, "load()", t);
        }
        return sb.toString();
    }

    private void save(String s) {
        try (FileOutputStream fos = openFileOutput(FILENAME,
                MODE_PRIVATE);
             OutputStreamWriter osw = new OutputStreamWriter(fos)) {
            osw.write(s);
        } catch (IOException t) {
            Log.e(TAG, "save()", t);
        }
    }
}