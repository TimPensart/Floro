package com.example.floro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class SeedsActivity extends AppCompatActivity implements SeedsAdapter.ItemClickListener {

    private RecyclerView seedsRecyclerView;
    private SeedsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeds);

        seedsRecyclerView = findViewById(R.id.seedsRecyclerView);
        seedsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SeedsAdapter(this);
        adapter.setClickListener(this);
        seedsRecyclerView.setAdapter(adapter);

        for (Seed seed : SeedsList.getInstance().seedsList) {
            Log.d("seedtest", "seeds collected: " + seed.getPlantName());
        }

    }

    @Override
    public void onItemClick(View view, int position) {
        Intent returnIntent = getIntent();
        returnIntent.putExtra("result",position);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent returnIntent = getIntent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}