package com.example.floro;

import android.content.Context;
import android.util.Log;
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

public class UitdagingenAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> list;
    private Context context;
    private ChallengesList challengesList = new ChallengesList();

    public UitdagingenAdapter(Context ct) {
        this.context = ct;
        this.list = challengesList.getChallengesList();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("viewType", String.valueOf(viewType));

        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.uitdagingen_row_image, parent, false);
            ChallengeWithPictureViewHolder holder = new ChallengeWithPictureViewHolder(view);
            return holder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.uitdaging_row, parent, false);
            ChallengeViewHolder holder = new ChallengeViewHolder(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == 0) {
            ChallengeWithPictureViewHolder challengeWithPictureViewHolder = (ChallengeWithPictureViewHolder) holder;
            ChallengeWithPicture challengeWithPictureObject = (ChallengeWithPicture) list.get(position);

            challengeWithPictureViewHolder.uitdagingTitle.setText(challengeWithPictureObject.getChallengeTitle());
            if (challengeWithPictureObject.getImageURL() != null) {
                Glide
                        .with(challengeWithPictureViewHolder.uitdagingImageView.getContext())
                        .load(challengeWithPictureObject.getImageURL())
                        .into(challengeWithPictureViewHolder.uitdagingImageView);
            } else {
                Glide
                        .with(challengeWithPictureViewHolder.uitdagingImageView.getContext())
                        .load(challengeWithPictureObject.getImageResource())
                        .into(challengeWithPictureViewHolder.uitdagingImageView);
            }

            challengeWithPictureViewHolder.prijs1Text.setText("+" + challengeWithPictureObject.getPrijs1());
            challengeWithPictureViewHolder.prijs2Text.setText("+" + challengeWithPictureObject.getPrijs2());
            challengeWithPictureViewHolder.prijs3Text.setText("+" + challengeWithPictureObject.getPrijs3() + "XP");

        } else { // normal viewholder no picture
            ChallengeViewHolder challengeViewHolder = (ChallengeViewHolder) holder;
            Challenge challengeObject = (Challenge) list.get(position);

            challengeViewHolder.uitdagingTitle.setText(challengeObject.getChallengeTitle());

            challengeViewHolder.prijs1Text.setText("+" + challengeObject.getPrijs1());
            challengeViewHolder.prijs2Text.setText("+" + challengeObject.getPrijs2());
            challengeViewHolder.prijs3Text.setText("+" + challengeObject.getPrijs3() + "XP");
        }
    } // onBindViewHolder

    @Override
    public int getItemViewType(int position) {
        if (ChallengeWithPicture.class.isInstance(list.get(position))) {
            return 0;
        } else {
            return 1;
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChallengeWithPictureViewHolder extends RecyclerView.ViewHolder {

        private TextView uitdagingTitle;
        private CircleImageView uitdagingImageView;
        private TextView prijs1Text;
        private TextView prijs2Text;
        private TextView prijs3Text;


        public ChallengeWithPictureViewHolder(@NonNull View itemView) {
            super(itemView);
            uitdagingTitle = itemView.findViewById(R.id.uitdagingTitle);

            uitdagingImageView = itemView.findViewById(R.id.uitdagingImageView);

            prijs1Text = itemView.findViewById(R.id.prijs1Text);
            prijs2Text = itemView.findViewById(R.id.prijs2Text);
            prijs3Text = itemView.findViewById(R.id.prijs3Text);
        }
    }

    public class ChallengeViewHolder extends RecyclerView.ViewHolder {

        private TextView uitdagingTitle;
        private TextView prijs1Text;
        private TextView prijs2Text;
        private TextView prijs3Text;


        public ChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            uitdagingTitle = itemView.findViewById(R.id.uitdagingTitle);

            prijs1Text = itemView.findViewById(R.id.prijs1Text);
            prijs2Text = itemView.findViewById(R.id.prijs2Text);
            prijs3Text = itemView.findViewById(R.id.prijs3Text);
        }
    }
}
