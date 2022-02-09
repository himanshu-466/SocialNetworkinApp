package com.mohit.socialnetworkinapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohit.socialnetworkinapp.Models.profileinfo;
import com.mohit.socialnetworkinapp.R;
import com.mohit.socialnetworkinapp.databinding.ActivityMyprofileBinding;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class  Myprofile extends AppCompatActivity {
    ActivityMyprofileBinding binding;
    Uri coverimage,profileimage;
    FirebaseStorage storage;
    FirebaseDatabase database;
    FirebaseAuth auth;
    ProgressDialog dialog;
    Long countpost,countfriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyprofileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        storage = FirebaseStorage.getInstance();
        database=FirebaseDatabase.getInstance();
        auth= FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading.....");
        dialog.setCancelable(false);

        binding.editcover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(Myprofile.this)
                        .crop()
                        //Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start(30);
            }
        });

        binding.editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(Myprofile.this)
                        .crop()
                        //Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start(40);
            }
        });
        database.getReference().child("userprofile").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    profileinfo info = snapshot.getValue(profileinfo.class);

                    String image = info.getImageurl();
                    String cover = info.getCover();

                    Glide.with(Myprofile.this).load(image).placeholder(R.drawable.placeholder).into(binding.myProfileImage);
                    Glide.with(Myprofile.this).load(cover).placeholder(R.drawable.select_image).into(binding.mycover);

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        binding.mynoofpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Myprofile.this,Mypost.class);

                startActivity(intent);
            }
        });
        binding.mypost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Myprofile.this,Mypost.class);
                startActivity(intent);
            }
        });

        database.getReference().child("Posts").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    countpost=snapshot.getChildrenCount();
                    binding.mynoofpost.setText(String.valueOf(countpost));

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        database.getReference().child("FriendAccepted").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    countfriends=snapshot.getChildrenCount();
                binding.mynooffriends.setText(String.valueOf(countfriends));

            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(data.getData()!=null) {
            if (requestCode == 40) {
//            get data uri ka ek method hai
                dialog.show();
                binding.myProfileImage.setImageURI(data.getData());
//                jo bhi image aai yeh use ek variable me store karwana hai
                profileimage = data.getData();
                StorageReference reference = storage.getReference().child("Profiles").child((auth.getUid()));
                reference.putFile(profileimage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String image = uri.toString();
                                    dialog.dismiss();
                                    HashMap<String,Object>map = new HashMap<>();
                                    map.put("imageurl",image);
                                    database.getReference().child("userprofile").child(auth.getUid()).updateChildren(map);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Toast.makeText(Myprofile.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });



            }
        }
        if(data.getData()!=null) {
            if (requestCode == 30) {
//            get data uri ka ek method hai
                dialog.show();
                binding.mycover.setImageURI(data.getData());
//                jo bhi image aai yeh use ek variable me store karwana hai
                coverimage = data.getData();
                StorageReference reference = storage.getReference().child("Profiles").child((auth.getUid())).child("Cover");
                reference.putFile(coverimage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String image = uri.toString();
                                    dialog.dismiss();
                                    HashMap<String,Object>map = new HashMap<>();
                                    map.put("Cover",image);
                                    database.getReference().child("userprofile").child(auth.getUid()).updateChildren(map);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Toast.makeText(Myprofile.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        }

    }
}