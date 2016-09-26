package com.thomaskuenneth.rr;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;

public class RR extends Activity {

    private static final String TAG =
            RR.class.getSimpleName();

    private static final int PERMISSIONS_RECORD_AUDIO =
            123;

    // Zustand der App
    private enum MODE {
        WAITING, RECORDING, PLAYING
    }

    private MODE mode;

    // Bedienelemente der App
    private RRListAdapter listAdapter;
    private Button b;

    // Datei mit der aktuellen Aufnahme
    private File currentFile;

    private MediaPlayer player;
    private MediaRecorder recorder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // ListView initialisieren
        final ListView lv = (ListView) findViewById(R.id.listview);
        listAdapter = new RRListAdapter(this);
        lv.setAdapter(listAdapter);
        lv.setOnItemClickListener((parent, view,
                                   position, id) -> {
            // Datei wiedergeben
            File f = listAdapter.getItem(position);
            if (f != null) {
                playAudioFile(f.getAbsolutePath());
            }
        });
        lv.setOnItemLongClickListener((parent, view,
                                       position, id) -> {
            File f = listAdapter.getItem(position);
            if (f != null) {
                if (f.delete()) {
                    listAdapter.remove(f);
                }
            }
            return true;
        });
        // Schaltfläche Aufnehmen/Beenden initialisieren
        b = (Button) findViewById(R.id.button);
        b.setOnClickListener((v) -> {
            if (mode == MODE.WAITING) {
                currentFile = recordToFile();
            } else if (mode == MODE.RECORDING) {
                // die Aufnahme stoppen
                recorder.stop();
                releaseRecorder();
                listAdapter.add(currentFile);
                currentFile = null;
                mode = MODE.WAITING;
                updateButtonText();
            } else if (mode == MODE.PLAYING) {
                player.stop();
                releasePlayer();
                mode = MODE.WAITING;
                updateButtonText();
            }
        });
        currentFile = null;
        mode = MODE.WAITING;
        player = null;
        recorder = null;
        updateButtonText();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            b.setEnabled(false);
            requestPermissions(new String[]
                    {Manifest.permission.RECORD_AUDIO},
                    PERMISSIONS_RECORD_AUDIO);
        } else {
            b.setEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        if ((requestCode == PERMISSIONS_RECORD_AUDIO) &&
                (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED)) {
            b.setEnabled(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
        releaseRecorder();
    }

    private void updateButtonText() {
        b.setText(getString((mode != MODE.WAITING) ? R.string.finish
                : R.string.record));
    }

    private File recordToFile() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        File f = new RRFile(getBaseDir(this), Long.toString(System
                .currentTimeMillis()) + RRFile.EXT_3GP);
        try {
            if (!f.createNewFile()) {
                Log.d(TAG, "Datei schon vorhanden");
            }
            recorder.setOutputFile(f.getAbsolutePath());
            recorder.prepare();
            recorder.start();
            mode = MODE.RECORDING;
            updateButtonText();
            return f;
        } catch (IOException e) {
            Log.e(TAG, "Konnte Aufnahme nicht starten", e);
        }
        return null;
    }

    private void releaseRecorder() {
        if (recorder != null) {
            // Recorder-Ressourcen freigeben
            recorder.release();
            recorder = null;
        }
    }

    private void playAudioFile(String filename) {
        player = new MediaPlayer();
        player.setOnCompletionListener((player) -> {
            releasePlayer();
            mode = MODE.WAITING;
            updateButtonText();
        });
        try {
            player.setDataSource(filename);
            player.prepare();
            player.start();
            mode = MODE.PLAYING;
            updateButtonText();
        } catch (Exception thr) {
            Log.e(TAG, "konnte Audio nicht wiedergeben", thr);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            // Player-Ressourcen freigeben
            player.release();
            player = null;
        }
    }

    public static File getBaseDir(Context ctx) {
        // für Zugriff auf dieses Verzeichnis sind
        // ab KitKat keine Berechtigungen nötig
        File dir = new File(ctx.getExternalFilesDir(null),
                ".RR");
        if (!dir.mkdirs()) {
            Log.d(TAG, "Verzeichnisse schon vorhanden");
        }
        return dir;
    }
}