package com.thomaskuenneth.kamerademo3;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class KameraDemo3 extends Activity
        implements SurfaceHolder.Callback {

    private static final String TAG =
            KameraDemo3.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_CAMERA
            = 123;

    private CameraManager manager;
    private SurfaceHolder holder;
    private CameraDevice camera;
    private String cameraId;
    private CameraCaptureSession activeSession;

    private CaptureRequest.Builder builderPreview;
    private CaptureRequest.Builder builderPicture;
    private ImageReader imageReader;
    private CameraCaptureSession.CaptureCallback
            captureCallback = null;
    private CameraCaptureSession.StateCallback captureSessionCallback =
            new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(
                        CameraCaptureSession session) {
                    try {
                        session.setRepeatingRequest(
                                builderPreview.build(),
                                captureCallback, null);
                        KameraDemo3.this.activeSession = session;
                    } catch (CameraAccessException e) {
                        Log.e(TAG, "onConfigured()", e);
                    }
                }

                @Override
                public void onConfigureFailed(
                        CameraCaptureSession session) {
                    Log.e(TAG, "onConfigureFailed()");
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        holder = null;
        camera = null;
        cameraId = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]
                            {Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CAMERA);
        } else {
            doIt();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (holder != null) {
            holder.addCallback(this);
        }
        Log.d(TAG, "onResume()");
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
        if (holder != null) {
            holder.removeCallback(this);
        }
        Log.d(TAG, "onPause()");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        if ((requestCode == PERMISSIONS_REQUEST_CAMERA) &&
                (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED)) {
            doIt();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed()");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated()");
        try {
            openCamera();
        } catch (SecurityException |
                CameraAccessException e) {
            Log.e(TAG, "openCamera()", e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder,
                               int format, int width,
                               int height) {
        Log.d(TAG, "surfaceChanged()");
    }

    private void doIt() {
        SurfaceView view =
                (SurfaceView) findViewById(R.id.view);
        holder = view.getHolder();
        // CameraManager-Instanz ermitteln
        manager = getSystemService(CameraManager.class);
        Size[] sizes = findCameraFacingBack();
        if ((cameraId == null) || (sizes == null)) {
            Log.d(TAG, "keine passende Kamera gefunden");
            finish();
        } else {
            DisplayMetrics metrics =
                    getResources().getDisplayMetrics();
            int _w = metrics.widthPixels;
            int _h = metrics.heightPixels;
            boolean found = false;
            for (Size size : sizes) {
                int width = size.getWidth();
                int height = size.getHeight();
                if (width > _w || height > _h) {
                    continue;
                }
                holder.setFixedSize(width, height);
                found = true;
                imageReader = ImageReader.newInstance(width, height,
                        ImageFormat.JPEG, 2);
                imageReader.setOnImageAvailableListener(
                        reader -> {
                            Log.d(TAG, "setOnImageAvailableListener()");
                            Image image = imageReader.acquireLatestImage();
                            final Image.Plane[] planes = image.getPlanes();
                            ByteBuffer buffer = planes[0].getBuffer();
                            saveJPG(buffer);
                            image.close();
                        }, null);
                break;
            }
            if (!found) {
                Log.d(TAG, "Zu groß");
                finish();
            }
            view.setOnClickListener((v) -> takePicture());
        }
    }

    /**
     * Sucht Kamera auf Geräterückseite und liefert
     * Infos zur Auflösung. Setzt auch die Variable
     * <code>cameraId</code>
     *
     * @return Auflösung
     */
    private Size[] findCameraFacingBack() {
        Size[] sizes = null;
        try {
            boolean found = false;
            // vorhandene Kameras ermitteln und auswählen
            String[] ids = manager.getCameraIdList();
            for (String id : ids) {
                CameraCharacteristics cc =
                        manager.getCameraCharacteristics(id);
                Log.d(TAG, id + ": " + cc.toString());
                Integer lensFacing =
                        cc.get(CameraCharacteristics.LENS_FACING);
                if ((lensFacing != null) &&
                        (lensFacing ==
                                CameraCharacteristics.LENS_FACING_BACK)) {
                    if (found) {
                        continue;
                    }
                    found = true;
                    cameraId = id;
                    StreamConfigurationMap configs = cc.get(
                            CameraCharacteristics.
                                    SCALER_STREAM_CONFIGURATION_MAP);
                    if (configs != null) {
                        sizes = configs.getOutputSizes(SurfaceHolder.class);
                    }
                }
            }
        } catch (CameraAccessException |
                NullPointerException e) {
            Log.e(TAG, "findCameraFacingBack()", e);
        }
        return sizes;
    }

    private void openCamera() throws
            SecurityException,
            CameraAccessException {
        manager.openCamera(cameraId,
                new CameraDevice.StateCallback() {

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
                    public void onError(CameraDevice camera,
                                        int error) {
                        Log.d(TAG, "onError()");
                    }
                }, null);
    }

    private void createPreviewCaptureSession() {
        List<Surface> outputs = new ArrayList<>();
        outputs.add(holder.getSurface());
        try {
            // Builder für Vorschau
            builderPreview = camera.createCaptureRequest(
                    CameraDevice.TEMPLATE_PREVIEW);
            builderPreview.addTarget(holder.getSurface());
            // Builder für die Aufnahme
            Surface surface = imageReader.getSurface();
            outputs.add(surface);
            builderPicture =
                    camera.createCaptureRequest(
                            CameraDevice.TEMPLATE_STILL_CAPTURE);
            builderPicture.addTarget(surface);
            camera.createCaptureSession(outputs,
                    captureSessionCallback,
                    new Handler());
        } catch (CameraAccessException e) {
            Log.e(TAG, "createPreviewCaptureSession()", e);
        }
    }

    private void takePicture() {
        try {
            activeSession.capture(builderPicture.build(),
                    captureCallback,
                    new Handler());
        } catch (CameraAccessException e) {
            Log.e(TAG, "takePicture()", e);
        }
    }

    private void saveJPG(ByteBuffer data) {
        File dir = getExternalFilesDir(
                Environment.DIRECTORY_PICTURES);
        dir.mkdirs();
        File f = new File(dir, TAG + "_"
                + Long.toString(System.currentTimeMillis())
                + ".jpg");
        Log.d(TAG, "Dateiname: " + f.getAbsolutePath());
        try (
                FileOutputStream fos = new FileOutputStream(f);
                BufferedOutputStream bos = new BufferedOutputStream(fos)
        ) {
            while (data.hasRemaining()) {
                bos.write(data.get());
            }
            Toast.makeText(this, R.string.click,
                    Toast.LENGTH_SHORT).show();
            addToMediaProvider(f);
        } catch (IOException e) {
            Log.e(TAG, "saveJPG()", e);
        }
    }

    private void addToMediaProvider(File f) {
        MediaScannerConnection.scanFile(this,
                new String[]{f.toString()},
                new String[]{"image/jpeg"},
                (path, uri) -> {
                    Intent i = new Intent(
                            Intent.ACTION_VIEW,
                            uri);
                    startActivity(i);
                });
    }
}
