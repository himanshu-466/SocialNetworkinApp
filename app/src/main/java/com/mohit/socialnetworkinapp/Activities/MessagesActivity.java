package com.mohit.socialnetworkinapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohit.socialnetworkinapp.Adapters.messageactivityAdapter;
import com.mohit.socialnetworkinapp.Models.messageactivitymodel;
import com.mohit.socialnetworkinapp.Models.profileinfo;
import com.mohit.socialnetworkinapp.R;
import com.mohit.socialnetworkinapp.databinding.ActivityMessagesBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MessagesActivity extends AppCompatActivity {
    ActivityMessagesBinding binding;
    ArrayList<profileinfo>list;
    messageactivityAdapter adapter;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMessagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("Contacts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        String uid=auth.getCurrentUser().getUid();

        list = new ArrayList<>();
        adapter= new messageactivityAdapter(list,this);
        binding.messagerecyclerview.setAdapter(adapter);
//        Toast.makeText(MessagesActivity.this, uid, Toast.LENGTH_SHORT).show();


        database.getReference().child("FriendAccepted").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        final String friendsuid = snapshot1.getKey();

                        database.getReference().child("userprofile").child(friendsuid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                                if (snapshot.exists()) {
                                    profileinfo info = new profileinfo();
                                    info.setFullname(snapshot.child("fullname").getValue(String.class));
                                    info.setImageurl(snapshot.child("imageurl").getValue(String.class));
                                    info.setUid(snapshot.child("uid").getValue(String.class));
                                    info.setToken(snapshot.child("token").getValue(String.class));
                                    list.add(info);

                                    adapter.notifyDataSetChanged();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



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