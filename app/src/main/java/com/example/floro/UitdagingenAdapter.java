package com.example.floro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UitdagingenAdapter extends RecyclerView.Adapter<UitdagingenAdapter.MyViewHolder> {

    private List<Object> list;
    private Context context;

    public UitdagingenAdapter(Context ct, List l) {
        this.context = ct;
        this.list = l;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.uitdagingen_row_image, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ChallengeWithPicture challengeItemData = (ChallengeWithPicture) list.get(position);

        holder.uitdagingTitle.setText(challengeItemData.getChallengeTitle());

        if (challengeItemData.getImageURL() != null) {
            Glide
                    .with(holder.uitdagingImageView.getContext())
                    .load(challengeItemData.getImageURL())
                    .into(holder.uitdagingImageView);
        } else {
            Glide
                    .with(holder.uitdagingImageView.getContext())
                    .load(challengeItemData.getImageResource())
                    .into(holder.uitdagingImageView);
        }

        holder.prijs1Text.setText(challengeItemData.getPrijs1());
        holder.prijs2Text.setText(challengeItemData.getPrijs2());
        holder.prijs3Text.setText(challengeItemData.getPrijs3());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView uitdagingTitle;
        private CircleImageView uitdagingImageView;
        private TextView prijs1Text;
        private TextView prijs2Text;
        private TextView prijs3Text;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            uitdagingTitle = itemView.findViewById(R.id.uitdagingTitle);

            uitdagingImageView = itemView.findViewById(R.id.uitdagingImageView);

            prijs1Text = itemView.findViewById(R.id.prijs1Text);
            prijs2Text = itemView.findViewById(R.id.prijs2Text);
            prijs3Text = itemView.findViewById(R.id.prijs3Text);
        }
    }
}
