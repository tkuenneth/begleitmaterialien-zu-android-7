package com.thomaskuenneth.kamerademo3;

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class KameraDemo3 extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = KameraDemo3.class.getSimpleName();

    private CameraManager manager;
    private SurfaceHolder holder;
    private String cameraId;
    private CameraDevice camera;
    private CameraCaptureSession activeSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Benutzeroberfläche anzeigen
        setContentView(R.layout.main);
        SurfaceView view = (SurfaceView) findViewById(R.id.view);
        holder = view.getHolder();
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
                    sizes = configs.getOutputSizes(SurfaceHolder.class);
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
            holder.setFixedSize(width, height);
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
        }
        holder.removeCallback(this);
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        holder.addCallback(this);
        Log.d(TAG, "onResume()");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed()");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated()");
        openCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.d(TAG, "surfaceChanged()");
    }

    private void openCamera() {
        try {
            manager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    Log.d(TAG, "onOpened()");
                    KameraDemo3.this.camera = camera;
                    createPreviewCaptureSession();
                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                    Log.d(TAG, "onDisconnected()");
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    Log.d(TAG, "onError()");
                }
            }, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "openCamera()", e);
        }
    }

    private void createPreviewCaptureSession() {
        List<Surface> outputs = new ArrayList<>();
        outputs.add(holder.getSurface());
        try {
            final CaptureRequest.Builder builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(holder.getSurface());
            camera.createCaptureSession(outputs, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.setRepeatingRequest(builder.build(), null, null);
                        KameraDemo3.this.activeSession = session;
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
            Log.e(TAG, "createPreviewCaptureSession()", e);
        }
    }
}