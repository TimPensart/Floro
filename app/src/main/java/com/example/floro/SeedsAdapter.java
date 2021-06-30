package com.example.floro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SeedsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ItemClickListener mClickListener;


    public SeedsAdapter(Context _ct) {
        this.context = _ct;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seeds_row, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        String currentSeedName = SeedsNotPlanted.getInstance().seedsNotPlantedList.get(position).getPlantName();
        viewHolder.seedTextView.setText(currentSeedName);
        viewHolder.seedImageView.setImageResource(SeedsNotPlanted.getInstance().seedsNotPlantedList.get(position).getPlantResourceImage());
    }

    @Override
    public int getItemCount() {
        return SeedsNotPlanted.getInstance().seedsNotPlantedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView seedImageView;
        private TextView seedTextView;
        private Button kweekButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            seedImageView = itemView.findViewById(R.id.seedRowImage);
            seedTextView = itemView.findViewById(R.id.seedRowTextView);
            kweekButton = itemView.findViewById(R.id.kweekButton);
            kweekButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

    }


    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
