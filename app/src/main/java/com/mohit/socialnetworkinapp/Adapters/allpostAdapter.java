package com.mohit.socialnetworkinapp.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.mohit.socialnetworkinapp.Activities.MainActivity;
import com.mohit.socialnetworkinapp.Activities.Myprofile;
import com.mohit.socialnetworkinapp.Activities.OtherUserProfile;
import com.mohit.socialnetworkinapp.Activities.commentActivity;
import com.mohit.socialnetworkinapp.Models.commentmodel;
import com.mohit.socialnetworkinapp.Models.postmodel;
import com.mohit.socialnetworkinapp.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class allpostAdapter extends RecyclerView.Adapter<allpostAdapter.viewholder2> {
    ArrayList<postmodel> post ;
    Context context;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseUser user;
    boolean likechecker=false;
    Dialog dialog;
    EditText editcomment;
    Button cancel,update;


    public allpostAdapter(ArrayList<postmodel> post, Context context) {
        this.post = post;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public viewholder2 onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_2,parent,false);
        return new viewholder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull allpostAdapter.viewholder2 holder, int position) {
        postmodel model = post.get(position);
        auth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        Glide.with(context).load(model.getPostimage()).placeholder(R.drawable.placeholder).into(holder.poststory);
        Glide.with(context).load(model.getUserprofile()).placeholder(R.drawable.placeholder).into(holder.profile);
        holder.date.setText(model.getDate());
        holder.time.setText(model.getTime());
        holder.username.setText(model.getUsername());
            holder.setliketStatus(model.getPostid());
        holder.setcommentStatus(model.getPostid());

        holder.poststory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likechecker = true;
                database.getReference().child("Likes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if(likechecker==true)
                        {
                            if(snapshot.child(model.getPostid()).hasChild(auth.getCurrentUser().getUid()))
                            {
                                database.getReference().child("Likes").child(model.getPostid()).child(auth.getCurrentUser().getUid()).removeValue();
                                likechecker=false;
                            }
                            else
                            {
                                database.getReference().child("Likes").child(model.getPostid()).child(auth.getCurrentUser().getUid()).setValue(true);
                                likechecker=false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

            }
        });



        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialogforedit);
        dialog.setCancelable(false);// yeh line dialog box ko bahar tab karne ke baad bhi dismiss nahi hoga
        editcomment=dialog.findViewById(R.id.editcomment);
        cancel=dialog.findViewById(R.id.editcommnetcancel);
        update=dialog.findViewById(R.id.editcommentupdate);

        database.getReference().child("Allposts").child(model.getPostid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    postmodel pst = snapshot.getValue(postmodel.class);
                    editcomment.setText(pst.getCaption());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        holder.more.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(auth.getCurrentUser().getUid().equals(model.getUid()))
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
                                    Calendar c = Calendar.getInstance();
                                    SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
                                    String savecurrentdate = currentdate.format(c.getTime());

                                    Calendar ca = Calendar.getInstance();
                                    SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm");
                                    String savecurrrentTime = currenttime.format(ca.getTime());
                                    HashMap<String,Object>map = new HashMap<>();
                                    map.put("caption",text);
                                    map.put("time",savecurrrentTime);
                                    map.put("date",savecurrentdate);
                                    database.getReference().child("Allposts").child(model.getPostid()).updateChildren(map);
                                    database.getReference().child("Posts").child(model.getUid()).child(model.getPostid()).updateChildren(map);
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
                            builder1.setMessage("Do You Want to delete this Post");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "Delete",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            database.getReference().child("Allposts").child(model.getPostid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    database.getReference().child("Comments").child(model.getPostid()).removeValue();

                                                    database.getReference().child("Posts").child(model.getUid()).child(model.getPostid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            notifyDataSetChanged();
                                                        }
                                                    });
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
                    popupMenu.getMenu().add("Share").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            ArrayList<Uri> imageUris = new ArrayList<Uri>();
                            imageUris.add(Uri.parse(model.getPostimage())); // Add your image URIs here
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
                            shareIntent.setType("image/*");
                            context.startActivity(Intent.createChooser(shareIntent, "Share images to.."));
                            return false;

                        }
                    });
                    popupMenu.show();

                }
                else
                {
                    PopupMenu popupMenu = new PopupMenu(v.getContext(),v );
                    popupMenu.setGravity(Gravity.END);
                    popupMenu.getMenu().add("Share").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            ArrayList<Uri> imageUris = new ArrayList<Uri>();
                            imageUris.add(Uri.parse(model.getPostimage())); // Add your image URIs here
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
                            shareIntent.setType("image/*");
                            context.startActivity(Intent.createChooser(shareIntent, "Share images to.."));

                            return false;

                        }
                    });
                    popupMenu.show();
                }
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.getUid().equals(model.getUid()))
                {
                    Intent intent = new Intent(context,Myprofile.class);
                    context.startActivity(intent);
                }
                else
                {

                    Intent intent = new Intent(context,OtherUserProfile.class);
                    intent.putExtra("allpostuid",model.getUid());
                    context.startActivity(intent);
                }


            }
        });
        holder.caption.setText(model.getCaption());
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, commentActivity.class);
                intent.putExtra("postid",model.getPostid());
                context.startActivity(intent);
            }
        });

        holder.commentimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, commentActivity.class);
                intent.putExtra("postid",model.getPostid());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return post.size();
    }

    public  class viewholder2 extends RecyclerView.ViewHolder {
    ImageView poststory,profile,more,like,commentimage;
    TextView nooflike,username,date,caption,time,comment;
    int likescount,commentcounts;
    DatabaseReference likeref,commentref;




    public viewholder2(@NonNull @NotNull View itemView) {
        super(itemView);
        poststory = itemView.findViewById(R.id.poststory);
        profile = itemView.findViewById(R.id.profile_image);
        more = itemView.findViewById(R.id.more);
        like = itemView.findViewById(R.id.likeimage);
        commentimage = itemView.findViewById(R.id.commentimage);
        nooflike= itemView.findViewById(R.id.nooflikes);
        comment = itemView.findViewById(R.id.samplecomment);
        username = itemView.findViewById(R.id.Username);
        date = itemView.findViewById(R.id.postdate);
        time = itemView.findViewById(R.id.sampleposttime);
        caption = itemView.findViewById(R.id.caption);


    }


        public void setliketStatus(String postid) {
            likeref = database.getReference().child("Likes");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String userid = user.getUid();
            String likes = " Likes";
            likeref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if (snapshot.child(postid).hasChild(userid)) {
                        likescount = (int) snapshot.child(postid).getChildrenCount();
                        like.setImageResource(R.drawable.finalredheart);
                        nooflike.setText(likescount + likes);
                    } else {
                        likescount = (int) snapshot.child(postid).getChildrenCount();
                        like.setImageResource(R.drawable.heart);
                        nooflike.setText(likescount + likes);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
            public void setcommentStatus(String postid) {
                commentref =database.getReference().child("Comments");

                commentref.child(postid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            commentcounts = (int)snapshot.getChildrenCount();
                            comment.setText(commentcounts+" comments");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
            }

        }
    }

