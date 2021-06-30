package com.example.floro;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IdentificationActivity extends AppCompatActivity {

    String imagePath;
    Bitmap bitmap;
    ImageView imageView;
    TextView plantNameText;
    TextView probabilityTextView;
    TextView rarityTextView;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;

    private ImageButton backButton;
    private TextView wikiTextView;

    private ChallengesList challengesListInstance = ChallengesList.getInstance();

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification);

        imageView = findViewById(R.id.imageView);
        plantNameText = findViewById(R.id.plantname_title);
        probabilityTextView = findViewById(R.id.probabilityTextView);
        wikiTextView = findViewById(R.id.wikiTextView);
        rarityTextView = findViewById(R.id.rarityTextView);

        pref = getApplicationContext().getSharedPreferences("completedChallenges", 0); // 0 - for private mode

        String imagePath = (String) getIntent().getExtras().getString("imagePath");
        Bitmap imageBitmap = imagePathToBitmap(imagePath);
        imageView.setImageBitmap(imageBitmap);

        String plantTitle = (String) getIntent().getExtras().get("title");
        String wikiDescriptionText = (String) getIntent().getExtras().get("wiki");

        try {
            evalCompletion(plantTitle);
        } catch(Exception e) {
            Log.e("evalCompletion", e.getMessage(), e);
        }


        double probability = Double.parseDouble( (String) getIntent().getExtras().get("probability") );
        int percentageProbability = (int) (probability * 100);

        plantNameText.setText(plantTitle);
        probabilityTextView.setText(String.valueOf(percentageProbability) + "% zekerheid");
        if (wikiDescriptionText != null) {
            wikiTextView.setText(wikiDescriptionText);
        }

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    } // on create

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

    public void evalCompletion(String name) {
        String challengePlantName = "";
        ArrayList<Object> completedChallenges = new ArrayList<Object>();
        ArrayList<String> completedChallengeNames = new ArrayList<String>();

        SharedPreferences.Editor editor = pref.edit();

        alertDialogBuilder = new AlertDialog.Builder(this);
        final View popup = getLayoutInflater().inflate(R.layout.popup_challenge_complete, null);

        Button okeButton = popup.findViewById(R.id.okeButton);
        TextView prijs1text = popup.findViewById(R.id.prijs1TextPopup);
        TextView prijs2text = popup.findViewById(R.id.prijs2TextPopup);
        TextView prijs3text = popup.findViewById(R.id.prijs3TextPopup);

        LinearLayout container = popup.findViewById(R.id.completedChallengesContainer);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.bottomMargin = 20;

        alertDialogBuilder.setView(popup);
        alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        okeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.hide();
            }
        });

        for (int i = 0; i < challengesListInstance.challengesList.size(); i++) {
            if (ChallengeWithPicture.class.isInstance(challengesListInstance.challengesList.get(i))) {

                ChallengeWithPicture currentChallenge = (ChallengeWithPicture) challengesListInstance.challengesList.get(i);
                challengePlantName = currentChallenge.getPlantName();

                if (name.toLowerCase().contains(challengePlantName.toLowerCase()) && !challengePlantName.isEmpty()) {
                    TextView textView = new TextView(this);
                    textView.setText(currentChallenge.getChallengeTitle());

                    textView.setLayoutParams(params);
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textView.setTextColor(ContextCompat.getColor(this, R.color.floro_bright_green));
                    textView.setTextSize(18);
                    textView.setTypeface(null, Typeface.BOLD);
                    container.addView(textView);

                    prijs1text.setText("+" + currentChallenge.getPrijs1());
                    prijs2text.setText("+" + currentChallenge.getPrijs2());
                    prijs3text.setText(currentChallenge.getPrijs3() + "xp");

                    alertDialog.show();

                    SeedsList.getInstance().seedsList.add(currentChallenge.getSeed());
                    SeedsNotPlanted.getInstance().seedsNotPlantedList.add(currentChallenge.getSeed());
                    completedChallenges.add(currentChallenge);
                    completedChallengeNames.add(currentChallenge.getPlantName());

                    editor.putBoolean(currentChallenge.getPlantName(), true);
                    editor.commit();
                    Log.d("challengetest", "evalCompletion: completed and removed challenge : " + currentChallenge.getChallengeTitle());
                }
            } else {

                Challenge currentChallenge = (Challenge) challengesListInstance.challengesList.get(i);
                challengePlantName = currentChallenge.getPlantName();

                if (!name.toLowerCase().equals("probeer opnieuw") && challengePlantName.equals("any")) { // if title does not equal probeer opnieuw
                    TextView textView = new TextView(this);
                    textView.setText(currentChallenge.getChallengeTitle());

                    textView.setLayoutParams(params);
                    textView.setTextColor(ContextCompat.getColor(this, R.color.floro_bright_green));
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textView.setTextSize(18);
                    textView.setTypeface(null, Typeface.BOLD);
                    container.addView(textView);

                    prijs1text.setText("+" + currentChallenge.getPrijs1());
                    prijs2text.setText("+" + currentChallenge.getPrijs2());
                    prijs3text.setText(currentChallenge.getPrijs3() + "xp");

                    alertDialog.show();

                    SeedsList.getInstance().seedsList.add(currentChallenge.getSeed());
                    SeedsNotPlanted.getInstance().seedsNotPlantedList.add(currentChallenge.getSeed());
                    Log.d("challengetest", "evalCompletion: " + challengePlantName);
                    completedChallenges.add(currentChallenge);
                    completedChallengeNames.add(currentChallenge.getPlantName());

                    editor.putBoolean(currentChallenge.getPlantName(), true);
                    editor.commit();
                    Log.d("challengetest", "evalCompletion: completed and removed challenge : " + currentChallenge.getChallengeTitle());
                }
            }
        }

        // remove all completed challenges
        ChallengesList.getInstance().challengesList.removeAll(completedChallenges);


    }



}