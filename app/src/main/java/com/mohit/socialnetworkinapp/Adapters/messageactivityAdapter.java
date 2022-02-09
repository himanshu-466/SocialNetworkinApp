package com.mohit.socialnetworkinapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohit.socialnetworkinapp.Activities.chatdetailedActivity;
import com.mohit.socialnetworkinapp.Models.messageactivitymodel;
import com.mohit.socialnetworkinapp.Models.profileinfo;
import com.mohit.socialnetworkinapp.R;
import com.mohit.socialnetworkinapp.databinding.ChatsampleBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class messageactivityAdapter extends RecyclerView.Adapter<messageactivityAdapter.viewholder> {
    ArrayList<profileinfo> list;
    Context context;
    String receiveruid, senderuid;


    public messageactivityAdapter(ArrayList<profileinfo> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chatsample, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        profileinfo model = list.get(position);
        senderuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        receiveruid = model.getUid();

                        FirebaseDatabase.getInstance().getReference().child("Chats").child(senderuid).child(receiveruid).child("lastmsg").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String lastmsg = snapshot.child("lastmsg").getValue(String.class);
                            long time = snapshot.child("lastmsgtime").getValue(Long.class);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm ");
                            holder.lastmsgtime.setText(dateFormat.format(new Date(time)));
                            holder.lastmsgtime.setVisibility(View.VISIBLE);
                                    holder.lastmsg.setText(lastmsg);


                                }

                                else {
                                    holder.lastmsg.setText("Tap to chat");
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });







        holder.name.setText(model.getFullname());
        Glide.with(context).load(model.getImageurl()).placeholder(R.drawable.placeholder).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, chatdetailedActivity.class);
                intent.putExtra("image", model.getImageurl());
                intent.putExtra("naming", model.getFullname());
                intent.putExtra("uid", model.getUid());
                intent.putExtra("tokenid",model.getToken());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, lastmsg, lastmsgtime;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.ChatuserName);
            image = itemView.findViewById(R.id.chatuserimage);
            lastmsg = itemView.findViewById(R.id.Chatuserlastmessage);
            lastmsgtime = itemView.findViewById(R.id.chatlastmsgtime);
        }
    }

}
