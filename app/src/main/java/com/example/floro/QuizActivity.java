package com.example.floro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class QuizActivity extends AppCompatActivity {

    private Button rightButton;
    private Button button2;
    private Button button3;
    private Button button4;

    private ImageView quizResultImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        quizResultImage = findViewById(R.id.quizResultImageView);

        rightButton = findViewById(R.id.narcis);

        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button  = (Button) view;
                button.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                button.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.floro_bright_green));

                button.setVisibility(View.GONE);
                button2.setVisibility(View.GONE);
                button3.setVisibility(View.GONE);
                button4.setVisibility(View.GONE);

                quizResultImage.setVisibility(View.VISIBLE);

            }
        });
    }

    public void closeButtonOnClick(View v) {
        finish();
    }

    public void wrongAnswer(View v) {
        Button b = (Button) v;
        b.setTextColor(Color.parseColor("#b30000"));
    }
}