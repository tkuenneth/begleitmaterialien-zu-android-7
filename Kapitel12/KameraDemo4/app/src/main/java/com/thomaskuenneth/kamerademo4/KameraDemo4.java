package com.thomaskuenneth.kamerademo4;

import android.app.Activity;
import android.content.Context;
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
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KameraDemo4 extends Activity {

    private static final String TAG = KameraDemo4.class.getSimpleName();

    private CameraManager manager;
    private String cameraId;
    private CameraDevice camera;
    private CameraCaptureSession activeSession;
    private MediaRecorder recorder;
    boolean recording;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Benutzeroberfläche anzeigen
        setContentView(R.layout.main);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recording) {
                    recorder.start();
                    recording = true;
                } else {
                    stop();
                    finish();
                }
                updateButton();
            }
        });
        // CameraManager-Instanz ermitteln
        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        // Kamera suchen und auswählen
        cameraId = null;
        Size[] sizes = null;
        try {
            // vorhandene Kameras ermitteln und auswählen
            String[] ids = manager.getCameraIdList();
            for (String id : ids) {
                CameraCharacteristics cc = manager.getCameraCharacteristics(id);
                Log.d(TAG, id + ": " + cc.toString());
                if (cc.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                    cameraId = id;
                    StreamConfigurationMap configs = cc.get(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    sizes = configs.getOutputSizes(MediaRecorder.class);
                    break;
                }
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "getCameraIdList() oder getCameraCharacteristics()", e);
        }
        if ((cameraId == null) || (sizes == null)) {
            Log.d(TAG, "keine passende Kamera gefunden");
            finish();
        } else {
            final int width = sizes[0].getWidth();
            final int height = sizes[0].getHeight();
            recorder = new MediaRecorder();
            recorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setVideoSize(width, height);
            recorder.setOutputFile(getFilename());
            recording = false;
            updateButton();
            openCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            if (activeSession != null) {
                activeSession.close();
                activeSession = null;
            }
            camera.close();
            camera = null;
            stop();
            recorder.release();
        }
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    private void openCamera() {
        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare()", e);
        }
        try {
            manager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    Log.d(TAG, "onOpened()");
                    KameraDemo4.this.camera = camera;
                    createCaptureSession();
                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                    Log.d(TAG, "onDisconnected()");
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    Log.d(TAG, "onError(): " + error);
                }
            }, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "openCamera()", e);
        }
    }

    private void createCaptureSession() {
        List<Surface> outputs = new ArrayList<>();
        final Surface surface = recorder.getSurface();
        outputs.add(surface);
        try {
            final CaptureRequest.Builder builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            builder.addTarget(surface);
            camera.createCaptureSession(outputs, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.setRepeatingRequest(builder.build(), null, null);
                        KameraDemo4.this.activeSession = session;
                    } catch (CameraAccessException e) {
                        Log.e(TAG, "capture()", e);
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    Log.e(TAG, "onConfigureFailed()");
                }
            }, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "createCaptureSession()", e);
        }
    }

    private String getFilename() {
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
                "KameraDemo4.mpg");
        return f.getAbsolutePath();
    }

    private void updateButton() {
        button.setText(getString(recording ? R.string.end : R.string.start));
    }

    private void stop() {
        if (recording) {
            recorder.stop();
            recording = false;
        }
        MediaScannerConnection.scanFile(getApplicationContext(),
                new String[]{getFilename()}, null, null);
    }
}