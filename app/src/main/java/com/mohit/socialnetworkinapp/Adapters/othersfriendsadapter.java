package com.mohit.socialnetworkinapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mohit.socialnetworkinapp.Models.others_friends_model;
import com.mohit.socialnetworkinapp.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class othersfriendsadapter extends RecyclerView.Adapter<othersfriendsadapter.viewholder>{
   ArrayList<others_friends_model> friendsmodels;
   Context context;

    public othersfriendsadapter(ArrayList<others_friends_model> friendsmodels, Context context) {
        this.friendsmodels = friendsmodels;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public viewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.other_user_friends_sample,parent,false);

        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull othersfriendsadapter.viewholder holder, int position) {
        others_friends_model friendsmodel =friendsmodels.get(position);
        Glide.with(context).load(friendsmodel.getProfile()).placeholder(R.drawable.placeholder).into(holder.friends_profile);

    }

    @Override
    public int getItemCount() {
        return friendsmodels.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        ImageView friends_profile;
        public viewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            friends_profile =itemView.findViewById(R.id.Others_friends_profile_image);
        }
    }
}
