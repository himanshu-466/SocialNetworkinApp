package com.mohit.socialnetworkinapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.mohit.socialnetworkinapp.Adapters.postadapter;
import com.mohit.socialnetworkinapp.Models.postmodel;
import com.mohit.socialnetworkinapp.R;
import com.mohit.socialnetworkinapp.Models.postmodel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class  Mypost extends AppCompatActivity {
    ArrayList<postmodel> postmodel;
    postadapter adapter;
    RecyclerView recyclerView;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    String uid;
    TextView nopost;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypost);
        getSupportActionBar().setTitle("Posts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        postmodel = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerview);
        nopost = findViewById(R.id.nopost);
        adapter = new postadapter(postmodel,this);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();


        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        if(getIntent().getStringExtra("uid")!=null)
        {
            uid = getIntent().getStringExtra("uid");
            database.getReference().child("Posts").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    postmodel.clear();
                    if(snapshot.exists())
                    {
                        for (DataSnapshot snapshot1 :snapshot.getChildren())
                        {
                            postmodel model = snapshot.getValue(postmodel.class);
                            model.setUsername(snapshot1.child("username").getValue(String.class));
                            model.setUserprofile(snapshot1.child("userprofile").getValue(String.class));
                            model.setPostimage(snapshot1.child("postimage").getValue(String.class));
                            model.setDate(snapshot1.child("date").getValue(String.class));
                            model.setTime(snapshot1.child("time").getValue(String.class));
                            model.setCaption(snapshot1.child("caption").getValue(String.class));
                            model.setPostid(snapshot1.child("postid").getValue(String.class));
                            model.setUid(snapshot1.child("uid").getValue(String.class));

                            postmodel.add(model);

                        }
                        adapter.notifyDataSetChanged();
                    }
                    else
                    {
                        nopost.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }

        else
        {
            database.getReference().child("Posts").child(auth.getUid()).orderByChild("countpost").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    postmodel.clear();
                    if(snapshot.exists())
                    {
                        for (DataSnapshot snapshot1 :snapshot.getChildren())
                        {
                            postmodel model = snapshot.getValue(postmodel.class);
                            model.setUsername(snapshot1.child("username").getValue(String.class));
                            model.setUserprofile(snapshot1.child("userprofile").getValue(String.class));
                            model.setPostimage(snapshot1.child("postimage").getValue(String.class));
                            model.setDate(snapshot1.child("date").getValue(String.class));
                            model.setTime(snapshot1.child("time").getValue(String.class));
                            model.setCaption(snapshot1.child("caption").getValue(String.class));
                            model.setPostid(snapshot1.child("postid").getValue(String.class));
                            model.setUid(snapshot1.child("uid").getValue(String.class));

                            postmodel.add(model);

                        }
                        adapter.notifyDataSetChanged();
                    }
                    else
                    {
                        nopost.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }




    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}