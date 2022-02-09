package com.mohit.socialnetworkinapp.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohit.socialnetworkinapp.Activities.ImageActivity;
import com.mohit.socialnetworkinapp.Models.messgaeModel;
import com.mohit.socialnetworkinapp.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class messageAdapter extends RecyclerView.Adapter{
    ArrayList<messgaeModel> msgmodel;
    Context context;
    String sender_Room;
    String receiver_Room;
    final int Item_sent =1;
    final int Item_receive =2;
    String senderuid;
    String receiveruid;

    public messageAdapter(ArrayList<messgaeModel> msgmodel, Context context, String sender_Room, String receiver_Room) {
        this.msgmodel = msgmodel;
        this.context = context;
        this.sender_Room = sender_Room;
        this.receiver_Room = receiver_Room;
    }


    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if(viewType==Item_sent)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout,parent,false);
            return  new sendervieholder(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout,parent,false);
            return  new recievervieholder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        messgaeModel model = msgmodel.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(model.getSenderid()))
        {
            return Item_sent;
        }
        else {
            return Item_receive;
        }

    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {


        messgaeModel message= msgmodel.get(position);
       senderuid = FirebaseAuth.getInstance().getUid();
       FirebaseDatabase.getInstance().getReference().child("Chats").child(senderuid).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists())
               {
                    for (DataSnapshot snapshot1:snapshot.getChildren())
                    {
                        receiveruid= snapshot1.getKey();
                    }

               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });


        if(holder.getClass()==sendervieholder.class)
        {
            sendervieholder viewholder = (sendervieholder)holder;
            viewholder.sendermsg.setText(message.getMessage());
            if(message.getMessage().equals("Photo"))
            {
                viewholder.senderimage.setVisibility(View.VISIBLE);
                viewholder.sendermsg.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageurl()).placeholder(R.drawable.placeholder).into(viewholder.senderimage);
                viewholder.senderimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ImageActivity.class);
                        intent.putExtra("imageurl",message.getImageurl());
                        context.startActivity(intent);
                    }
                });
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm ");
            viewholder.sendermsgtime.setText(dateFormat.format(new Date(message.getTimestamp())));
            viewholder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage("Delete This Message");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "DELETE FOR ME",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    FirebaseDatabase.getInstance().getReference().child("Chats").child(senderuid).child(receiveruid).child("Message").child(message.getMessageid()).removeValue();
                                }
                            });

                    builder1.setNeutralButton(
                            "DELETE FOR EVERYONE",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    FirebaseDatabase.getInstance().getReference().child("Chats").child(senderuid).child(receiveruid).child("Message").child(message.getMessageid()).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("Chats").child(receiveruid).child(senderuid).child("Message").child(message.getMessageid()).removeValue();

                                }
                            });
                    builder1.setNegativeButton(
                            "CANCEL",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                    return true;
                }
            });

        }
        else
        {
                recievervieholder viewholder = (recievervieholder)holder;
                viewholder.recievermsg.setText(message.getMessage());
                if(message.getMessage().equals("Photo"))
            {
                viewholder.receiverimage.setVisibility(View.VISIBLE);
                viewholder.recievermsg.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageurl()).placeholder(R.drawable.placeholder).into(viewholder.receiverimage);
                viewholder.receiverimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ImageActivity.class);
                        intent.putExtra("imageurl",message.getImageurl());
                        context.startActivity(intent);
                    }
                });
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm ");
            viewholder.receivermsgtime.setText(dateFormat.format(new Date(message.getTimestamp())));
            viewholder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage("Delete This Message");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "DELETE FOR ME",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    FirebaseDatabase.getInstance().getReference().child("Chats").child(senderuid).child(receiveruid).child("Message").child(message.getMessageid()).removeValue();

                                }
                            });


                    builder1.setNegativeButton(
                            "CANCEL",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                    return true;
                }
            });


        }

    }

    @Override
    public int getItemCount() {
        return msgmodel.size();
    }

    public class sendervieholder extends RecyclerView.ViewHolder {
        TextView sendermsg,sendermsgtime;
        ImageView senderfeeling,senderimage;
        public sendervieholder(@NonNull @NotNull View itemView) {
            super(itemView);
            senderfeeling = itemView.findViewById(R.id.senderfeeling);
            senderimage = itemView.findViewById(R.id.senderimage);
            sendermsg = itemView.findViewById(R.id.sendermsg);
            sendermsgtime = itemView.findViewById(R.id.sendertime);
        }
    }

    public class recievervieholder extends RecyclerView.ViewHolder {
        TextView recievermsg,receivermsgtime;
        ImageView receiverfeeling,receiverimage;
        public recievervieholder(@NonNull @NotNull View itemView) {
            super(itemView);
            receiverfeeling = itemView.findViewById(R.id.receiversfeeling);
            recievermsg = itemView.findViewById(R.id.receivermsg);
            receiverimage = itemView.findViewById(R.id.receiverimage);
            receivermsgtime = itemView.findViewById(R.id.receivertime);
        }
    }
}
