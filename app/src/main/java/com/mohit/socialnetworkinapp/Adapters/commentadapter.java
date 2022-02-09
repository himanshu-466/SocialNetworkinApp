package com.mohit.socialnetworkinapp.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.IntentCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohit.socialnetworkinapp.Activities.MainActivity;
import com.mohit.socialnetworkinapp.Activities.commentActivity;
import com.mohit.socialnetworkinapp.Models.commentmodel;
import com.mohit.socialnetworkinapp.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class commentadapter extends RecyclerView.Adapter<commentadapter.viewholder> {
    ArrayList<commentmodel> model;
    Context context;
    FirebaseAuth auth;
    FirebaseDatabase database;
    Dialog dialog;
    EditText  editcomment;
    Button cancel,update;

    public commentadapter(ArrayList<commentmodel> model, Context context) {
        this.model = model;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public viewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.commentlayout,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull commentadapter.viewholder holder, int position) {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        commentmodel detail = model.get(position);
        holder.username.setText(detail.getUsername());
        Glide.with(context).load(detail.getImage()).placeholder(R.drawable.placeholder).into(holder.image);
        holder.date.setText(detail.getDate());
        holder.time.setText(detail.getTime());
        holder.discription.setText(detail.getDiscription());


        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialogforedit);
        dialog.setCancelable(false);// yeh line dialog box ko bahar tab karne ke baad bhi dismiss nahi hoga
        editcomment=dialog.findViewById(R.id.editcomment);
        cancel=dialog.findViewById(R.id.editcommnetcancel);
        update=dialog.findViewById(R.id.editcommentupdate);

        database.getReference().child("Comments").child(detail.getPostid()).child(detail.getUniquekey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    commentmodel cmt = snapshot.getValue(commentmodel.class);
                     editcomment.setText(cmt.getDiscription());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        holder.option.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(auth.getCurrentUser().getUid().equals(detail.getUid()))
                {
                    PopupMenu popupMenu = new PopupMenu(v.getContext(),v );
                    popupMenu.setGravity(Gravity.END);
                    popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            dialog.show();
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    Window window = dialog.getWindow();
                                    window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                }
                            });
                            update.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String text = editcomment.getText().toString();
                                    String uniquekey=database.getReference().push().getKey();
                                    Calendar c = Calendar.getInstance();
                                    SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
                                    String savecurrentdate = currentdate.format(c.getTime());

                                    Calendar ca = Calendar.getInstance();
                                    SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm");
                                    String savecurrrentTime = currenttime.format(ca.getTime());
                                    HashMap<String,Object>map = new HashMap<>();
                                    map.put("discription",text);
                                    map.put("time",savecurrrentTime);
                                    map.put("date",savecurrentdate);
                                    database.getReference().child("Comments").child(detail.getPostid()).child(detail.getUniquekey()).updateChildren(map);
                                    dialog.dismiss();
                                }
                            });

                            return false;

                        }
                    });
                    popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                            builder1.setMessage("Do You Want to delete this Comment");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "Delete",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            database.getReference().child("Comments").child(detail.getPostid()).child(detail.getUniquekey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    notifyDataSetChanged();
                                                }
                                            });

                                            dialog.cancel();
                                        }
                                    });

                            builder1.setNegativeButton(
                                    "No",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();

                            return false;
                        }
                    });
                    popupMenu.show();

                }
                else
                {
                    PopupMenu popupMenu = new PopupMenu(v.getContext(),v );
                    popupMenu.setGravity(Gravity.END);
                    popupMenu.getMenu().add("Report").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();

                            return false;

                        }
                    });
                    popupMenu.show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        ImageView image,option;
        TextView username,date,time,discription;

        public viewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.commentuserimage);
            option = itemView.findViewById(R.id.commentmoreoption);
            username = itemView.findViewById(R.id.commentusername);
            date = itemView.findViewById(R.id.commentuserdate);
            time = itemView.findViewById(R.id.commentusertime);
            discription = itemView.findViewById(R.id.commentdiscription);
        }
    }
}
