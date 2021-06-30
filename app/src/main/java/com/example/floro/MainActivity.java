package com.example.floro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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
    String wikiDescriptionText;
    String plantTitle;
    String probability;

    ProgressBar progressCircle;
    ConstraintLayout progressOverlay;

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
        setWindowStatusNav(getWindow(), statusBarColor, navBarColor);
        setContentView(R.layout.activity_main);


        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS);
        }

        wikiDescriptionText = "";
        plantTitle = "";

        pictureButton = findViewById(R.id.picture_button);
        menuButton = findViewById(R.id.menu_button);

        progressOverlay = findViewById(R.id.progress_overlay);
        progressOverlay.setVisibility(View.GONE);

        progressCircle = findViewById(R.id.progressBar);


        previewView = findViewById(R.id.previewViewMain);

        previewView.post(new Runnable() {
                             @Override
                             public void run() {
                                 cameraProviderFuture = ProcessCameraProvider.getInstance(getApplicationContext());
                                 cameraProviderFuture.addListener(() -> {
                                     try {
                                         cameraProvider = cameraProviderFuture.get();
                                         bindPreview(cameraProvider, previewView);
                                     } catch (ExecutionException | InterruptedException e) {
                                         // No errors need to be handled for this Future.
                                         // This should never be reached.
                                     }
                                 }, ContextCompat.getMainExecutor(getApplicationContext()));
                             }
            }
        );



        SharedPreferences pref = getApplicationContext().getSharedPreferences("completedChallenges", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        Boolean firstUse = true;

        try {
            firstUse = pref.getBoolean("firstUse", true);
            Log.d("firstUse", "onCreate: " + firstUse);
        } catch (Exception e) {
            Log.e("Some Tag", "mainActivity getBoolean first use" + e.getMessage(), e);
        }

        if (firstUse) { // if app first use
            Log.d("challengetest", "onCreate: first use of app");
            editor.putBoolean("firstUse", false);
            editor.commit();
        } else {
            String plantName = "";

            ArrayList<Object> completedChallenges = new ArrayList<Object>();
            for (int i = 0; i < ChallengesList.getInstance().challengesList.size(); i++) {
                Object object = ChallengesList.getInstance().challengesList.get(i);

                if (ChallengeWithPicture.class.isInstance(object)) {
                    ChallengeWithPicture challengeWithPicture = (ChallengeWithPicture) object;
                    plantName = challengeWithPicture.getPlantName();

                    if (pref.getBoolean(plantName, false)) {
                        completedChallenges.add(challengeWithPicture);
                        SeedsList.getInstance().seedsList.add(challengeWithPicture.getSeed());
                    }
                } else {
                    Challenge challenge = (Challenge) object;
                    plantName = challenge.getPlantName();

                    if (pref.getBoolean(plantName, false)) {
                        completedChallenges.add(challenge);
                        SeedsList.getInstance().seedsList.add(challenge.getSeed());
                    }
                }
            } // for loop
            ChallengesList.getInstance().challengesList.removeAll(completedChallenges);
        }

        for (int i = 0; i < SeedsList.getInstance().seedsList.size(); i++) {
            if (pref.getBoolean("seed" + SeedsList.getInstance().seedsList.get(i).getPlantName(), false)) { // if not empty

            } else {
                SeedsNotPlanted.getInstance().seedsNotPlantedList.add(SeedsList.getInstance().seedsList.get(i));
                Log.d("seed", "seed : " + SeedsList.getInstance().seedsList.get(i).getPlantName() + " not placed yet");
            }
        }


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

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider, PreviewView pv) {

        preview = new Preview.Builder()
                // Set initial target rotation
                .setTargetRotation(pv.getDisplay().getRotation())
                .build();

        ImageCapture.Builder builder = new ImageCapture.Builder();

        Log.d("previewView", "bindPreview: " + pv);

        try {
            if (pv != null) {
                imageCapture =
                        builder
                                .setTargetRotation(pv.getDisplay().getRotation())
                                .build();
            } else {
                imageCapture =
                        builder
                                .setTargetRotation(pv.getDisplay().getRotation())
                                .build();
            }
        } catch (Exception e) {
            Log.e("imageCaptureBuilder", "bindPreview: ", e);
        }


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
        Log.d("picturetest", picturePath);
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
                                progressCircle.getProgress();
                                progressOverlay.setVisibility(View.VISIBLE);
                            }
                        });

                        identifyPlant(imagePathToBitmap(picturePath));

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
        fragmentTransaction.replace(R.id.fragment_container_main, new NavigationOverlayFragment());
        fragmentTransaction.addToBackStack("fragNav");
        fragmentTransaction.commit();
    }

    public void identifyPlant(Bitmap bitmap) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] imageBytes = byteArrayOutputStream.toByteArray();
                    String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                    String apiKey = getResources().getString(R.string.api_key);

                    JSONObject data = new JSONObject();
                    try {
                        data.put("api_key", apiKey);

                        // add images
                        JSONArray images = new JSONArray();

                        images.put(imageString);

                        data.put("images", images);


                        // add modifiers
                        JSONArray modifiers = new JSONArray()
                                .put("crops_fast");
                        data.put("modifiers", modifiers);

                        // add language
                        data.put("plant_language", "nl");

                        // add details
                        JSONArray plantDetails = new JSONArray()
                                .put("common_names")
                                .put("name_authority")
                                .put("wiki_description");
                        data.put("plant_details", plantDetails);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.d("post", "called");
                    URL url = new URL("https://api.plant.id/v2/identify");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");

                    OutputStream os = con.getOutputStream();
                    os.write(data.toString().getBytes());
                    os.close();

                    InputStream is = con.getInputStream();
                    Log.d("post", is.toString());
                    String response = convertInputStreamToString(is);

                    Log.d("post", response);
                    try {

                        JSONObject obj = new JSONObject(response);
                        JSONArray suggestions = obj.getJSONArray("suggestions");


                        int index = 0;
                        boolean nameFound = false;
                        JSONObject suggestion;
                        JSONObject plantObj;


                        while (!nameFound && index < suggestions.length()) {
                            suggestion = suggestions.getJSONObject(index);
                            probability = suggestion.getString("probability");
                            Log.d("post", probability);
                            if (Double.parseDouble(probability) < 0.1) {
                                break;
                            }
                            plantObj = suggestion.getJSONObject("plant_details");
                            if (!plantObj.isNull("common_names")) {

                                JSONArray commonNames = plantObj.getJSONArray("common_names");
                                plantTitle = commonNames.getString(0);
                                Log.d("post", plantTitle);

                                JSONObject wikiObject = plantObj.getJSONObject("wiki_description");
                                wikiDescriptionText = wikiObject.getString("value");
                                Log.d("post", wikiDescriptionText);


                                nameFound = true;
                            } else {
                                Log.d("post", "geen soort gevonden" + index);
                                index++;
                            }
                        }
                        Log.d("post", "while done");

                        if (!nameFound) {
                            plantTitle = "Probeer opnieuw";
                        }

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                    }

                    con.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    public void run() {

                    }
                });

            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent intentIdentification = new Intent(MainActivity.this, IdentificationActivity.class);
        intentIdentification.putExtra("title", plantTitle);
        intentIdentification.putExtra("probability", probability);
        intentIdentification.putExtra("wiki", wikiDescriptionText);
        intentIdentification.putExtra("imagePath", picturePath);
        startActivity(intentIdentification);

    } // identify plant

    public Bitmap imagePathToBitmap(String imagePath) {
        Bitmap bitmap;

        File imgFile = new File(imagePath);

        bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());


        // getting EXIF metadata of image to get image rotation and rotating bitmap accordingly
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap = null;
        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }

        return rotatedBitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    } // rotate bitmap

    private String convertInputStreamToString(InputStream inputStream)
            throws IOException {

        final char[] buffer = new char[8192];
        final StringBuilder result = new StringBuilder();

        // InputStream -> Reader
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            int charsRead;
            while ((charsRead = reader.read(buffer, 0, buffer.length)) > 0) {
                result.append(buffer, 0, charsRead);
            }
        }

        return result.toString();

    }

    @Override
    protected void onStart() {
        super.onStart();

        progressOverlay.setVisibility(View.GONE);
    }

    public void closeOnClick(View v) {
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        fragmentManager.popBackStack("fragNav", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}