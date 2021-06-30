package com.example.floro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.internal.VisibilityAwareImageButton;

import java.util.ArrayList;

public class TuinActivity extends AppCompatActivity {
    private int mScrollDistance;

    private ArrayList<FrameLayout> gridList = new ArrayList<FrameLayout>();

    private ScrollView myScrollView;

    private ImageButton seedsButton;
    private TextView seedsNumber;

    private FrameLayout gardenFrameLayout;

    private ImageButton placeSeedButton;
    private ImageButton cancelPlaceButton;

    private ImageView currentPlantImage;
    private int currentSeedPosition;

    int x = 0;
    int y = 0;

    private int ivWidth = 400;
    private int ivHeight = 400;

    String leftMarginString = String.valueOf(0);
    String topMarginString = String.valueOf(0);

    private SharedPreferences pref;

    private int displayHeight;
    private int displayWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuin);

        pref = getApplicationContext().getSharedPreferences("completedChallenges", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        displayHeight = displayMetrics.heightPixels;
        displayWidth = displayMetrics.widthPixels;

        myScrollView = findViewById(R.id.scroll_view);
        gardenFrameLayout = findViewById(R.id.constraintLayoutGarden);

        addSeedImages();

        seedsNumber = findViewById(R.id.seedsNumber);
        seedsNumber.setText(String.valueOf(SeedsNotPlanted.getInstance().seedsNotPlantedList.size()));

        placeSeedButton = findViewById(R.id.placeSeedButton);
        cancelPlaceButton = findViewById(R.id.cancelPlaceButton);

        placeSeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPlantImage.clearColorFilter();
                currentPlantImage.setOnTouchListener(null);

                editor.putBoolean("seed" + SeedsNotPlanted.getInstance().seedsNotPlantedList.get(currentSeedPosition).getPlantName(), true);
                editor.commit();
                editor.putString("leftMargin" + SeedsNotPlanted.getInstance().seedsNotPlantedList.get(currentSeedPosition).getPlantName(), leftMarginString);
                editor.commit();
                editor.putString("topMargin" + SeedsNotPlanted.getInstance().seedsNotPlantedList.get(currentSeedPosition).getPlantName(), topMarginString);
                editor.commit();

                SeedsNotPlanted.getInstance().seedsNotPlantedList.remove(currentSeedPosition);

                resetViews();
                seedsNumber.setText(String.valueOf(SeedsNotPlanted.getInstance().seedsNotPlantedList.size()));

                currentPlantImage = null;
                currentSeedPosition = -1;
                leftMarginString = String.valueOf(0);
                topMarginString = String.valueOf(0);
            }
        });

        cancelPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gardenFrameLayout.removeView(currentPlantImage);
                currentPlantImage.setOnTouchListener(null);
                currentPlantImage = null;
                currentSeedPosition = -1;
                leftMarginString = String.valueOf(0);
                topMarginString = String.valueOf(0);
                resetViews();
            }
        });

        myScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                mScrollDistance = scrollY;
                Log.d("scrolltest"," scrollDistance: "+ mScrollDistance);
            }
        });

        gardenFrameLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {

                int action = dragEvent.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED: {
                        return true;
                    }
                    case DragEvent.ACTION_DRAG_ENTERED: {
                        view.invalidate();
                        return true;
                    }
                    case DragEvent.ACTION_DRAG_LOCATION: {
                        View v = (View) dragEvent.getLocalState();

                        y = (int) dragEvent.getY();
                        x = (int) dragEvent.getX();

                        int scrollViewHeight = myScrollView.getMeasuredHeight();

                        Log.d("scrolltest"," top: "+ y);

                        if (y > ((mScrollDistance + scrollViewHeight) - displayHeight/4)) {
                            myScrollView.smoothScrollBy(0, 30);
                        }

                        if (y < (mScrollDistance + displayHeight/4)) {
                            myScrollView.smoothScrollBy(0, -30);
                        }

                        return true;
                    }
                    case DragEvent.ACTION_DRAG_EXITED: {
                        view.invalidate();
                        return true;
                    }
                    case DragEvent.ACTION_DROP: {
                        view.invalidate();

                        View v = (View) dragEvent.getLocalState();
                        ViewGroup parent = (ViewGroup) v.getParent();
                        parent.removeView(v);
                        FrameLayout destination = (FrameLayout) view;

                        destination.addView(v);
                        v.setVisibility(View.VISIBLE);

                        int vHeight = v.getMeasuredHeight();
                        int vWidth = v.getMeasuredWidth();
                        FrameLayout.LayoutParams llp = (FrameLayout.LayoutParams) v.getLayoutParams();

                        int leftMargin = x - (vWidth/2);
                        int topMargin = y - (vHeight/2);

                        leftMarginString = String.valueOf(leftMargin);
                        topMarginString = String.valueOf(topMargin);

                        llp.leftMargin = leftMargin;
                        llp.topMargin = topMargin;
                        v.setLayoutParams(llp);
                        return true;
                    }
                    case DragEvent.ACTION_DRAG_ENDED: {
                        view.invalidate();
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }
        });

        seedsButton = findViewById(R.id.seedsButton);

        seedsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SeedsActivity.class);
                startActivityForResult(intent, 1);
            }
        });


    }// oncreate

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        int position = -1;

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                int result = data.getIntExtra("result", -1);
                if (result != -1) {
                    position = result;
                    currentSeedPosition = position;

                    // Initialize a new ImageView widget
                    ImageView iv = new ImageView(getApplicationContext());

                    if (position != -1) {
                        iv.setImageDrawable(getDrawable(SeedsNotPlanted.getInstance().seedsNotPlantedList.get(position).getPlantResourceImage()));
                    }
                    // Set an image for ImageView
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ivWidth, ivHeight);

                    lp.topMargin = (mScrollDistance + displayHeight/2) - (ivHeight/2);
                    lp.leftMargin = (displayWidth/2) - (ivWidth/2);

                    // Add layout parameters to ImageView
                    iv.setLayoutParams(lp);

                    currentPlantImage = iv;


                    currentPlantImage.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent motionEvent) {
                            int event = motionEvent.getAction();

                            if (event == MotionEvent.ACTION_DOWN) {
                                ClipData data = ClipData.newPlainText("", "");

                                View.DragShadowBuilder dragshadow = new View.DragShadowBuilder(v);

                                // Starts the drag
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    v.startDragAndDrop(data       // data to be dragged
                                            , dragshadow  // drag shadow
                                            , v            // local data about the drag and drop operation
                                            , 0          // flags set to 0 because not using currently
                                    );
                                }

                                v.setVisibility(View.INVISIBLE);

                                return true;
                            }
                            return false;
                        }
                    });


                    // Finally, add the ImageView to layout
                    gardenFrameLayout.addView(currentPlantImage);

                    seedsButton.setVisibility(View.GONE);
                    placeSeedButton.setVisibility(View.VISIBLE);
                    cancelPlaceButton.setVisibility(View.VISIBLE);
                    currentPlantImage.setColorFilter(Color.argb(200, 255, 255, 255), PorterDuff.Mode.SRC_ATOP);

                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }

    } //onActivityResult

    public void resetViews() {
        seedsButton.setVisibility(View.VISIBLE);
        placeSeedButton.setVisibility(View.GONE);
        cancelPlaceButton.setVisibility(View.GONE);
    }

    public void addSeedImages() {
        for (int i = 0; i < SeedsList.getInstance().seedsList.size(); i++) {

            if (pref.getBoolean("seed" + SeedsList.getInstance().seedsList.get(i).getPlantName(), false)) { // if not empty

                String lMargin = pref.getString("leftMargin" + SeedsList.getInstance().seedsList.get(i).getPlantName(), "");
                String tMargin = pref.getString("topMargin" + SeedsList.getInstance().seedsList.get(i).getPlantName(), "");

                Log.d("seed", "placed seeds : " + SeedsList.getInstance().seedsList.get(i).getPlantName());
                Log.d("seed", "placed seeds left margin : " + lMargin);
                Log.d("seed", "placed seeds left margin : " + tMargin);

                ImageView iv = new ImageView(this);

                iv.setImageDrawable(getDrawable(SeedsList.getInstance().seedsList.get(i).getPlantResourceImage()));

                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ivWidth, ivHeight);

                int storedLeftMargin = Integer.parseInt(lMargin);
                int storedTopMargin = Integer.parseInt(tMargin);

                lp.leftMargin = storedLeftMargin;
                lp.topMargin = storedTopMargin;

                // Add layout parameters to ImageView
                iv.setLayoutParams(lp);
                gardenFrameLayout.addView(iv);

            } else {
                Log.d("seed", "seed : " + SeedsList.getInstance().seedsList.get(i).getPlantName() + " not placed yet");
            }
        }
    } // add seed images

    public void closeButtonOnClick(View v) {
        finish();
    }



}