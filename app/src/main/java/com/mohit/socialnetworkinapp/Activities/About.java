package com.mohit.socialnetworkinapp.Activities;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.mohit.socialnetworkinapp.Models.profileinfo;
import com.mohit.socialnetworkinapp.R;
import com.mohit.socialnetworkinapp.databinding.ActivityAboutBinding;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;


public class About extends AppCompatActivity {
    ActivityAboutBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        binding.aboutbackpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        database.getReference().child("userprofile").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    profileinfo info = snapshot.getValue(profileinfo.class);

                    String fullname = info.getFullname();
                    String interest = info.getInterest();
                    String country = info.getCountry();
                    String bio = info.getBio();
                    String religion = info.getReligion();
                    String phoneno=info.getPhoneNo();
                    String gender=info.getGender();

                    binding.aboutReligion.setText(religion);
                    binding.aboutPhoneno.setText(phoneno);
                    binding.aboutInterest.setText(interest);
                    binding.aboutGender.setText(gender);
                    binding.aboutCountry.setText(country);
                    binding.aboutfullname.setText(fullname);
                    binding.aboutBio.setText(bio);

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        binding.aboutEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.aboutEdit.setEnabled(false);
                binding.aboutSave.setEnabled(true);
                binding.aboutfullname.setEnabled(true);
                binding.aboutBio.setEnabled(true);
                binding.aboutCountry.setEnabled(true);
                binding.aboutGender.setEnabled(true);
                binding.aboutInterest.setEnabled(true);
                binding.aboutPhoneno.setEnabled(true);
                binding.aboutReligion.setEnabled(true);
            }
        });
        binding.aboutSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.aboutEdit.setEnabled(true);
                binding.aboutSave.setEnabled(false);
                binding.aboutfullname.setEnabled(false);
                binding.aboutBio.setEnabled(false);
                binding.aboutCountry.setEnabled(false);
                binding.aboutGender.setEnabled(false);
                binding.aboutInterest.setEnabled(false);
                binding.aboutPhoneno.setEnabled(false);
                binding.aboutReligion.setEnabled(false);

                String religion=  binding.aboutReligion.getText().toString();
                String phono= binding.aboutPhoneno.getText().toString();
                String interest= binding.aboutInterest.getText().toString();
                String gender= binding.aboutGender.getText().toString();
                String country= binding.aboutCountry.getText().toString();
                String fullname= binding.aboutfullname.getText().toString();
                String bio= binding.aboutBio.getText().toString();

                HashMap<String,Object> map = new HashMap<>();
                map.put("fullname",fullname);
                map.put("gender",gender);
                map.put("religion",religion);
                map.put("interest",interest);
                map.put("bio",bio);
                map.put("country",country);
                map.put("phoneNo",phono);

                database.getReference().child("userprofile").child(auth.getUid()).updateChildren(map);




            }
        });







    }


}