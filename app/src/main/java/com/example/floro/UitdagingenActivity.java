package com.example.floro;

import androidx.annotation.AnyRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class UitdagingenActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FrameLayout closeButton;
    private UitdagingenAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uitdagingen);

        recyclerView = findViewById(R.id.uitdagingenRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UitdagingenAdapter(this);
        recyclerView.setAdapter(adapter);

        closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}