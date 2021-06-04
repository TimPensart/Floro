package com.example.floro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;


public class MainActivity extends AppCompatActivity {

    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    ImageCapture imageCapture;
    Preview preview;
    Camera camera;
    File pictureFile;
    String picturePath;

    ImageButton pictureButton;
    ImageButton menuButton;

    ProcessCameraProvider cameraProvider;
    private ExecutorService cameraExecutor = Executors.newSingleThreadExecutor();
    private CameraSelector cameraSelector;
    private CameraControl cameraControl;
    private CameraInfo cameraInfo;

    ScaleGestureDetector objScaleGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int statusBarColor = android.graphics.Color.parseColor("#50000000");
        int navBarColor = android.graphics.Color.parseColor("#50000000");
        setWindowStatusNav(getWindow(),statusBarColor,navBarColor);
        setContentView(R.layout.activity_main);


        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS);
        }

        pictureButton = findViewById(R.id.picture_button);
        menuButton = findViewById(R.id.menu_button);

        previewView = findViewById(R.id.previewView);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));

        objScaleGestureDetector = new ScaleGestureDetector(this, new PinchZoomListener());

    }// oncreate


    public boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                this.finish();
            }
        }
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        preview = new Preview.Builder()
                .build();

        ImageCapture.Builder builder = new ImageCapture.Builder();
        imageCapture =
                builder
                        .setTargetRotation(previewView.getDisplay().getRotation())
                        .build();

        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        //Vendor-Extensions (The CameraX extensions dependency in build.gradle)
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);
        // if has hdr (optional).
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable hdr.
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageCapture, preview);

        cameraControl = camera.getCameraControl();
        cameraInfo = camera.getCameraInfo();

        // Listen to tap events on the viewfinder and set them as focus regions
        previewView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                objScaleGestureDetector.onTouchEvent(motionEvent);
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // Get the MeteringPointFactory from PreviewView
                    MeteringPointFactory factory = previewView.getMeteringPointFactory();

                    // Create a MeteringPoint from the tap coordinates
                    MeteringPoint point = factory.createPoint(motionEvent.getX(), motionEvent.getY());

                    // Create a MeteringAction from the MeteringPoint, you can configure it to specify the metering mode
                    FocusMeteringAction action = new FocusMeteringAction.Builder(point).build();

                    // Trigger the focus and metering. The method returns a ListenableFuture since the operation
                    // is asynchronous. You can use it get notified when the focus is successful or if it fails.
                    cameraControl.startFocusAndMetering(action);

                    return true;
                } else {
                    return false;
                }

            }
        });

    } // Bindpreview

    public class PinchZoomListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // Get the camera's current zoom ratio
            float currentZoomRatio = cameraInfo.getZoomState().getValue().getZoomRatio();

            // Get the pinch gesture's scaling factor
            float delta = detector.getScaleFactor();

            // Update the camera's zoom ratio. This is an asynchronous operation that returns
            // a ListenableFuture, allowing you to listen to when the operation completes.
            cameraControl.setZoomRatio(currentZoomRatio * delta);


            return true;
        }
    }

    public void onClickTakePicture(View view) {
        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            pictureFile = new File(getBatchDirectoryName(), "FloroWaarneming " + mDateFormat.format(new Date()) + ".jpg");
            picturePath = pictureFile.getPath();
        } catch (Exception e) {
            Log.e("picturetest", "onClickTakePicture: ", e);
        }

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(pictureFile).build();
        Log.d("picturetest", picturePath );
        imageCapture.takePicture(outputFileOptions, cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                        // insert your code here.
                        Log.d("onImageSaved", "onImageSaved: " + outputFileResults);

                        runOnUiThread(new Runnable() {
                            public void run() {
                                final Toast toast = Toast.makeText(MainActivity.this, "Image saved to: " + pictureFile, Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });

                        Intent intentIdentification = new Intent(MainActivity.this, IdentificationActivity.class);
                        intentIdentification.putExtra("path", picturePath);
                        startActivity(intentIdentification);
                    }

                    @Override
                    public void onError(ImageCaptureException error) {
                        // insert your code here.
                        Log.d("onImageSaved", "Error:" + error);
                    }
                }
        );
    } // on click take picture


    public String getBatchDirectoryName() {
        String app_folder_path;
        if (android.os.Build.VERSION.SDK_INT >= 29) {//if Android 10,save to this private dir first
            app_folder_path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        } else { //if less than Android 10,still use this deprecated dir
            app_folder_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera";
        }

        File dir = new File(app_folder_path);

        Log.d("filepath", app_folder_path);
        return app_folder_path;
    }

    public static void setWindowStatusNav(android.view.Window window, int statusbarColor, int navbarColor) {

        int flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT || Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT_WATCH) {
            window.getAttributes().flags |= flags;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            window.getDecorView().setSystemUiVisibility(uiVisibility);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getAttributes().flags &= ~flags;

            window.setStatusBarColor(statusbarColor);
            window.setNavigationBarColor(navbarColor);
        }
    }


    public void OpenNavigationFragment(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_main,new NavigationOverlayFragment());
        fragmentTransaction.addToBackStack("fragNav");
        fragmentTransaction.commit();
    }


}