package com.example.floro;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;


public class NavigationOverlayFragment extends Fragment {

    // navigation overlay views
    ConstraintLayout navigationBackground;
    ImageView imageViewSteel;
    ImageButton imageButtonTuin;
    ImageButton imageButtonQuiz;
    ImageButton imageButtonUitdagingen;
    ImageButton imageButtonProfiel;


    View view;

    Animation slideUpIn;
    Animation slideDownOut;
    Animation fadeIn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_navigation_overlay, container, false);

        navigationBackground = view.findViewById(R.id.navigationBackground);
        imageViewSteel = view.findViewById(R.id.steelImage);
        imageButtonTuin = view.findViewById(R.id.tuinButton);
        imageButtonQuiz = view.findViewById(R.id.quizButton);
        imageButtonUitdagingen = view.findViewById(R.id.uitdagingenButton);
        imageButtonProfiel = view.findViewById(R.id.profielButton);

        imageButtonUitdagingen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UitdagingenActivity.class);
                startActivity(intent);
            }
        });


        slideUpIn = AnimationUtils.loadAnimation(getContext(),
                R.anim.slide_up_in);
        slideDownOut = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down_out);
        fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AnimateIn();
    }

    public void AnimateIn() {
        navigationBackground.startAnimation(fadeIn);
        imageViewSteel.startAnimation(slideUpIn);
        imageButtonTuin.startAnimation(slideUpIn);
        imageButtonQuiz.startAnimation(slideUpIn);
        imageButtonUitdagingen.startAnimation(slideUpIn);
        imageButtonProfiel.startAnimation(slideUpIn);

    }

    public void AnimateOut() {

    }



}