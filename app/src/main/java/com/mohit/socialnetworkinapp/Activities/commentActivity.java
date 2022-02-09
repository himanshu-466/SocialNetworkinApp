package com.mohit.socialnetworkinapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohit.socialnetworkinapp.Adapters.commentadapter;
import com.mohit.socialnetworkinapp.Models.commentmodel;
import com.mohit.socialnetworkinapp.Models.postmodel;
import com.mohit.socialnetworkinapp.Models.profileinfo;
import com.mohit.socialnetworkinapp.R;
import com.mohit.socialnetworkinapp.databinding.ActivityCommentBinding;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class commentActivity extends AppCompatActivity {
    ActivityCommentBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ArrayList<commentmodel> commentmodels;
    commentadapter adapter;
    ProgressDialog dialog;
    String imageurl;
    String fullname;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        String postid = getIntent().getStringExtra("postid");
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading");
        commentmodels = new ArrayList<>();
        adapter = new commentadapter(commentmodels,this);


        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        binding.commnetrecycler.setLayoutManager(manager);
        binding.commnetrecycler.setAdapter(adapter);
        database.getReference().child("Comments").child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    commentmodels.clear();
                {
                    for (DataSnapshot snapshot1 :snapshot.getChildren()) {
                        commentmodel model = snapshot1.getValue(commentmodel.class);
                        model.setImage(snapshot1.child("image").getValue(String.class));
                        model.setDate(snapshot1.child("date").getValue(String.class));
                        model.setDiscription(snapshot1.child("discription").getValue(String.class));
                        model.setTime(snapshot1.child("time").getValue(String.class));
                        model.setUsername(snapshot1.child("username").getValue(String.class));
                        model.setUid(snapshot1.child("uid").getValue(String.class));
                        model.setPostid(snapshot1.child("postid").getValue(String.class));
                        model.setUniquekey(snapshot1.child("uniquekey").getValue(String.class));

                        commentmodels.add(model);

                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        database.getReference().child("userprofile").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    profileinfo info = snapshot.getValue(profileinfo.class);
                    imageurl = info.getImageurl();
                    fullname= info.getFullname();

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        binding.commentback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.commentsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!binding.commentedittext.getText().toString().isEmpty()) {
                    dialog.show();
                    String text = binding.commentedittext.getText().toString().trim();

                    String uniquekey=database.getReference().push().getKey();
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
                    String savecurrentdate = currentdate.format(c.getTime());

                    Calendar ca = Calendar.getInstance();
                    SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm");
                    String savecurrrentTime = currenttime.format(ca.getTime());



                    commentmodel commentmodel = new commentmodel(imageurl,fullname,savecurrentdate,savecurrrentTime,text,auth.getCurrentUser().getUid(),postid,uniquekey);

                    database.getReference().child("Comments").child(postid).child(uniquekey).setValue(commentmodel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            binding.commentedittext.setText("");
                            dialog.dismiss();


                        }
                    });

                }
                else
                {
                    Toast.makeText(commentActivity.this, "Please Write Something", Toast.LENGTH_SHORT).show();
                    binding.commentedittext.requestFocus();
                }
            }
        });
    }
}