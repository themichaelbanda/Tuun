package com.penguinsonabeach.tuun.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.penguinsonabeach.tuun.Object.User;
import com.penguinsonabeach.tuun.R;

import java.util.ArrayList;

public class LeaderRecycleViewAdapter extends RecyclerView.Adapter<LeaderRecycleViewAdapter.LeaderViewHolder> {

        private final ArrayList<User> users;
        private final Context mContext;


        public LeaderRecycleViewAdapter(ArrayList<User> users, Context mContext) {
            this.users = users;
            this.mContext = mContext;
        }

        @Override
        public LeaderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_card, parent, false);
            return new LeaderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(LeaderViewHolder holder, int position) {
            String rank = (String.valueOf(position + 1))+": ";
            User currentUser = users.get(holder.getAdapterPosition());
            holder.speedTextView.setText(currentUser.getTopSpeed()+" MPH");
            holder.rankingTextView.setText(rank);

            if(currentUser.getUserName()== null){
                holder.nameTextView.setText(currentUser.getName());
            }else{
                holder.nameTextView.setText(currentUser.getUserName());
            }
            if(currentUser.getPhotoUrl() != null){
                Glide.with(mContext)
                        .load(currentUser.getPhotoUrl())
                        .apply(RequestOptions
                                .diskCacheStrategyOf(DiskCacheStrategy.ALL)
                                .apply(RequestOptions.circleCropTransform())
                                .override(250, 250))
                        .into(holder.leaderboardUserThumbnail);}

        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        public static class LeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, speedTextView, rankingTextView;
        public ImageView leaderboardUserThumbnail;

        public LeaderViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.leaderboardUserNameTV);
            speedTextView = itemView.findViewById(R.id.leaderboardSpeedTV);
            rankingTextView = itemView.findViewById(R.id.leaderboardNumberTV);
            leaderboardUserThumbnail = itemView.findViewById(R.id.leaderboard_user_thumbnail);
        }
    }
    }

