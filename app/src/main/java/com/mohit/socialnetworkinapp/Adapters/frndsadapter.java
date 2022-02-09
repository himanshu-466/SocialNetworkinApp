package com.mohit.socialnetworkinapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.mohit.socialnetworkinapp.Activities.OtherUserProfile;
import com.mohit.socialnetworkinapp.Activities.commentActivity;
import com.mohit.socialnetworkinapp.Models.profileinfo;
import com.mohit.socialnetworkinapp.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class frndsadapter extends RecyclerView.Adapter<frndsadapter.findfrndsviewholder>{
ArrayList<profileinfo> info;
Context context;
FirebaseAuth auth;

    public frndsadapter(ArrayList<profileinfo> info, Context context) {
        this.info = info;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public findfrndsviewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view1 = LayoutInflater.from(context).inflate(R.layout.finddriendslayout, parent, false);
        return new findfrndsviewholder(view1);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull frndsadapter.findfrndsviewholder holder, int position) {
        profileinfo profile = info.get(position);
        auth = FirebaseAuth.getInstance();
        if (!auth.getCurrentUser().getUid().equals(profile.getUid())) {
            Glide.with(context).load(profile.getImageurl()).placeholder(R.drawable.placeholder).into(holder.image);
            holder.Country.setText(profile.getCountry());
            holder.username.setText(profile.getFullname());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OtherUserProfile.class);
                    intent.putExtra("friendssearch",profile.getUid());
                    context.startActivity(intent);
                }
            });
        }
        else
        {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
        }




    }
    public void filterlist1(ArrayList<profileinfo> filterlist)
    {
        info=filterlist;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return info.size();
    }

    public class findfrndsviewholder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView username, Country;

        public findfrndsviewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.findfrndsuserimage);
            Country = itemView.findViewById(R.id.findfrndsstatus);
            username = itemView.findViewById(R.id.findfrndsusername);
        }
    }
}
