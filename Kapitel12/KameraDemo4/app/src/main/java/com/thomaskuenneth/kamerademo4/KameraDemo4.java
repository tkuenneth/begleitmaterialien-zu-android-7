package com.thomaskuenneth.kamerademo4;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.widget.Button;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.widget.Toast;

public class KameraDemo4 extends Activity {

    private static final String TAG =
            KameraDemo4.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_RECORD
            = 123;

    private CameraDevice camera;
    private CameraCaptureSession activeSession;
    private MediaRecorder recorder;
    private boolean recording;
    private Button startStop;
    private CaptureRequest.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        startStop = (Button) findViewById(R.id.button);
        startStop.setOnClickListener((v) -> {
                    if (!recording) {
                        try {
                            recorder.start();
                            activeSession.setRepeatingRequest(
                                    builder.build(),
                                    null,
                                    new Handler());
                            recording = true;
                        } catch (CameraAccessException e) {
                            Log.e(TAG, "setRepeatingRequest()", e);
                        }
                    } else {
                        stop();
                        showMovieAndFinish();
                    }
                    updateStartStop();
                }
        );
        startStop.setEnabled(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if ((checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) ||
                (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED)) {
            updateStartStop();
            requestPermissions(new String[]
                            {Manifest.permission.CAMERA,
                                    Manifest.permission.RECORD_AUDIO},
                    PERMISSIONS_REQUEST_RECORD);
        } else {
            doIt();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        if ((requestCode == PERMISSIONS_REQUEST_RECORD) &&
                (grantResults.length == 2
                        && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED
                        && grantResults[1] ==
                        PackageManager.PERMISSION_GRANTED)) {
            doIt();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        stop();
    }

    private void doIt() throws SecurityException {
        CameraManager manager = getSystemService(CameraManager.class);
        String cameraId = null;
        Size[] sizes = null;
        try {
            String[] ids = manager.getCameraIdList();
            for (String id : ids) {
                CameraCharacteristics cc =
                        manager.getCameraCharacteristics(id);
                Integer lensFacing =
                        cc.get(CameraCharacteristics.LENS_FACING);
                if ((lensFacing != null) && (lensFacing
                        == CameraCharacteristics.LENS_FACING_BACK)) {
                    cameraId = id;
                    StreamConfigurationMap configs = cc.get(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    if (configs != null) {
                        sizes =
                                configs.getOutputSizes(MediaRecorder.class);
                    }
                    break;
                }
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "doIt()", e);
        }
        if ((cameraId == null) || (sizes == null)) {
            Log.d(TAG, "keine passende Kamera gefunden");
            finish();
        } else {
            final int width = sizes[sizes.length - 1].getWidth();
            final int height = sizes[sizes.length - 1].getHeight();
            try {
                // Recorder vorbereiten
                recorder = new MediaRecorder();
                recorder.setVideoSource(
                        MediaRecorder.VideoSource.SURFACE);
                recorder.setAudioSource(
                        MediaRecorder.AudioSource.CAMCORDER);
                recorder.setOutputFormat(
                        MediaRecorder.OutputFormat.MPEG_4);
                recorder.setVideoEncoder(
                        MediaRecorder.VideoEncoder.H264);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                recorder.setVideoSize(width, height);
                recorder.setOutputFile(getFilename());
                recorder.prepare();
                recording = false;
                // Kamera öffnen
                manager.openCamera(cameraId,
                        new CameraDevice.StateCallback() {

                            @Override
                            public void onOpened(CameraDevice camera) {
                                Log.d(TAG, "onOpened()");
                                KameraDemo4.this.camera = camera;
                                createCaptureSession();
                            }

                            @Override
                            public void onDisconnected(
                                    CameraDevice camera) {
                                Log.d(TAG, "onDisconnected()");
                            }

                            @Override
                            public void onError(CameraDevice camera,
                                                int error) {
                                Log.d(TAG, "onError(): " + error);
                            }
                        }, null);
            } catch (CameraAccessException | IOException e) {
                Log.e(TAG, "doIt()", e);
            }
            updateStartStop();
        }
    }

    private void createCaptureSession() {
        List<Surface> outputs = new ArrayList<>();
        final Surface surface = recorder.getSurface();
        outputs.add(surface);
        try {
            builder =
                    camera.createCaptureRequest(
                            CameraDevice.TEMPLATE_RECORD);
            builder.addTarget(surface);
            camera.createCaptureSession(outputs,
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            KameraDemo4.this.activeSession = session;
                            startStop.setEnabled(true);
                        }

                        @Override
                        public void onConfigureFailed(
                                CameraCaptureSession session) {
                            Log.e(TAG, "onConfigureFailed()");
                        }
                    }, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "createCaptureSession()", e);
        }
    }

    private String getFilename() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        if (dir != null) {
            if (dir.mkdirs()) {
                Log.d(TAG, "Verzeichnisse wurden angelegt");
            }
            File f = new File(dir, "KameraDemo4.mpg");
            return f.getAbsolutePath();
        }
        return null;
    }

    private void updateStartStop() {
        startStop.setText(getString(recording
                ? R.string.end
                : R.string.start
        ));
    }

    private void stop() {
        if (camera != null) {
            if (activeSession != null) {
                activeSession.close();
                activeSession = null;
            }
            camera.close();
            camera = null;
        }
        if (recording) {
            recorder.stop();
            recording = false;
        }
        recorder.release();
    }

    private void showMovieAndFinish() {
        MediaScannerConnection.scanFile(this,
                new String[]{getFilename()},
                new String[]{"video/mpeg"},
                (path, uri) -> {
                    runOnUiThread(() -> {
                        Intent i = new Intent(Intent.ACTION_VIEW,
                                uri);
                        try {
                            startActivity(i);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(this,
                                    R.string.no_app,
                                    Toast.LENGTH_LONG).show();
                        } finally {
                            Log.d(TAG, path);
                            finish();
                        }
                    });
                });
    }
}
