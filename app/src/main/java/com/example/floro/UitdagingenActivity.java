package com.example.floro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class UitdagingenActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageButton closeButton;
    private UitdagingenAdapter adapter;
    private List<Object> uitdagingenList = new ArrayList<Object>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uitdagingen);

        uitdagingenList.add(new ChallengeWithPicture(
                "vind je eerste zonnebloem",
                "https://images.pexels.com/photos/6429840/pexels-photo-6429840.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260",
                "100",
                "100",
                "100"
                ));

        uitdagingenList.add(new ChallengeWithPicture(
                "vind je eerste paardenbloem",
                "https://www.hunebednieuwscafe.nl/wp-content/uploads/2017/02/paardebloem.jpg",
                "300",
                "200",
                "10"
        ));

        uitdagingenList.add(new ChallengeWithPicture(
                "vind je eerste papaver",
                "https://images.pexels.com/photos/1130430/pexels-photo-1130430.jpeg?auto=compress&cs=tinysrgb&dpr=3&h=750&w=1260",
                "10",
                "10",
                "1"
        ));


        recyclerView = findViewById(R.id.uitdagingenRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UitdagingenAdapter(this, uitdagingenList);
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