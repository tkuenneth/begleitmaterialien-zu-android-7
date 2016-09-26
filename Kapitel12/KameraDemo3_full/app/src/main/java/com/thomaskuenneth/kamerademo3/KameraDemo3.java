package com.thomaskuenneth.kamerademo3;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class KameraDemo3 extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = KameraDemo3.class.getSimpleName();

    /**
     * wird bei der Speicherung eines JPEG-Bildes verwendet
     */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private CameraManager manager;
    private SurfaceHolder holder;
    private String cameraId;
    private CameraDevice camera;
    private CameraCaptureSession activeSession;
    private ImageReader imageReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Benutzeroberfläche anzeigen
        setContentView(R.layout.main);
        SurfaceView view = (SurfaceView) findViewById(R.id.view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCaptureSession();
            }
        });
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
            // ImageReader erzeugen
            imageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 2);
            imageReader.setOnImageAvailableListener(
                    new ImageReader.OnImageAvailableListener() {
                        @Override
                        public void onImageAvailable(ImageReader reader) {
                            Log.d(TAG, "setOnImageAvailableListener()");
                            Image image = imageReader.acquireLatestImage();
                            final Image.Plane[] planes = image.getPlanes();
                            ByteBuffer buffer = planes[0].getBuffer();
                            saveJPG(buffer);
                            image.close();
                        }
                    }, null);
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

    private void createCaptureSession() {
        List<Surface> outputs = new ArrayList<>();
        Surface surface = imageReader.getSurface();
        outputs.add(surface);
        try {
            final CaptureRequest.Builder builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            builder.addTarget(surface);
            builder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            builder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            builder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            camera.createCaptureSession(outputs, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(builder.build(), new CameraCaptureSession.CaptureCallback() {

                            @Override
                            public void onCaptureCompleted(CameraCaptureSession session,
                                                           CaptureRequest request,
                                                           TotalCaptureResult result) {
                                createPreviewCaptureSession();
                            }

                            @Override
                            public void onCaptureFailed(CameraCaptureSession session,
                                                        CaptureRequest request,
                                                        CaptureFailure failure) {
                                createPreviewCaptureSession();
                            }
                        }, null);
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

    private void saveJPG(ByteBuffer data) {
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                TAG + "_" + Long.toString(System.currentTimeMillis()) + ".jpg");
        Log.d(TAG, "Dateiname: " + f.getAbsolutePath());
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            fos = new FileOutputStream(f);
            bos = new BufferedOutputStream(fos);
            while (data.hasRemaining()) {
                bos.write(data.get());
            }
        } catch (IOException e) {
            Log.e(TAG, "saveJPG()", e);
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    Log.e(TAG, "saveJPG()", e);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e(TAG, "saveJPG()", e);
                }
            }
            // Bild in Mediendatenbank übernehmen
            MediaScannerConnection.scanFile(this, new String[]{f.getAbsolutePath()}, null, null);
        }
    }
}