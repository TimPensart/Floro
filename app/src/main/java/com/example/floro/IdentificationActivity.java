package com.example.floro;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IdentificationActivity extends AppCompatActivity {

    String imagePath;
    Bitmap bitmap;
    ImageView imageView;
    TextView plantNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification);

        imageView = findViewById(R.id.imageView);
        plantNameText = findViewById(R.id.plantname_title);


        imagePath = getIntent().getExtras().getString("path");
        File imgFile = new File(imagePath);

        if (imgFile.exists()) {
            bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }

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
        switch(orientation) {

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

        imageView.setImageBitmap(rotatedBitmap);

        //IdentifyPlant(rotatedBitmap);

    } // on create

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    } // rotate bitmap


    public void IdentifyPlant(Bitmap bitmap) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] imageBytes = byteArrayOutputStream.toByteArray();
                    String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                    String apiKey = "REDACTED_API_KEY";

                    JSONObject data = new JSONObject();
                    try {
                        data.put("api_key", apiKey);

                        // add images
                        JSONArray images = new JSONArray();

                        images.put(imageString);

                        data.put("images", images);


                        // add modifiers
                        JSONArray modifiers = new JSONArray()
                                .put("crops_medium")
                                .put("similar_images");
                        data.put("modifiers", modifiers);

                        // add language
                        data.put("plant_language", "nl");

                        // add details
                        JSONArray plantDetails = new JSONArray()
                                .put("common_names")
                                .put("url")
                                .put("name_authority")
                                .put("wiki_description")
                                .put("taxonomy")
                                .put("synonyms");
                        data.put("plant_details", plantDetails);
                    } catch (Exception e){
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

                        while(!nameFound && index <= suggestions.length()) {
                            suggestion = suggestions.getJSONObject(index);
                            plantObj = suggestion.getJSONObject("plant_details");
                            if (!plantObj.isNull("common_names")) {

                                JSONArray commonNames = plantObj.getJSONArray("common_names");

                                String firstCommonName = commonNames.getString(0);
                                Log.d("post", firstCommonName );

                                plantNameText.setText(firstCommonName);

                                nameFound = true;
                            } else {
                                Log.d("post", "null common names found at suggestion index " + index);
                                index++;
                            }
                        }

                        if (!nameFound) {
                            plantNameText.setText("Probeer opnieuw");
                        }

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                    }

                    con.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }



                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //UI Thread work here
                    }
                });
            }
        });
    }

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

}