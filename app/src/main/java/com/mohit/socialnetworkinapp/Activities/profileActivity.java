package com.mohit.socialnetworkinapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohit.socialnetworkinapp.Models.profileinfo;
import com.mohit.socialnetworkinapp.Models.registerinformatiom;
import com.mohit.socialnetworkinapp.R;
import com.mohit.socialnetworkinapp.databinding.ActivityProfileBinding;
import com.mohit.socialnetworkinapp.databinding.ActivityRegisterBinding;

import org.jetbrains.annotations.NotNull;

public class profileActivity extends AppCompatActivity {
    ActivityProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    GoogleSignInClient mGoogleSignInClient;
    ProgressDialog dialog;
    FirebaseStorage storage;
    Uri selectedimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        auth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        binding.profilename.requestFocus();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null)
        {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            Uri personPhoto = acct.getPhotoUrl();


//            Glide.with(this).load(String.valueOf(personPhoto)).into(binding.userimage);
//            selectedImage = personPhoto;


        }



        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(profileActivity.this)
                        .crop()
                        //Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start(10);

//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                startActivityForResult(intent,45);
            }
        });

        binding.profiledone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.profilename.getText().toString().isEmpty()) {
                    binding.profilename.setError("Please Enter the field");
                    binding.profilename.requestFocus();
                    return;
                }
                if (binding.profilefullname.getText().toString().isEmpty()) {
                    binding.profilefullname.setError("Please Enter the field");
                    binding.profilefullname.requestFocus();
                    return;
                }
                if (binding.profilecountry.getText().toString().isEmpty()) {
                    binding.profilecountry.setError("Please Enter the field");
                    binding.profilecountry.requestFocus();
                    return;
                }
                if (!binding.profilecheckbox.isChecked()) {
                    Toast.makeText(profileActivity.this, "Please Accept Terms and Condition ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedimage != null) {
                    binding.profileprogressbar.setVisibility(View.VISIBLE);
                    binding.profiledone.setVisibility(View.INVISIBLE);


                    StorageReference reference = storage.getReference().child("Profiles").child((auth.getUid()));
                    reference.putFile(selectedimage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // above uri me ek image ka path aa raha hai
                                        String imageUrl = uri.toString();
                                        String uid = auth.getCurrentUser().getUid();
                                        String name = binding.profilename.getText().toString();
                                        String fullname = binding.profilefullname.getText().toString();
                                        String country = binding.profilecountry.getText().toString();
                                        String phone = auth.getCurrentUser().getPhoneNumber();

//                                                  Users name ka ek model hai jiske contrutor ko yeh 4 value chahiy
                                        profileinfo user = new profileinfo(uid, name, fullname, country, imageUrl, phone,"","","","");
                                        database.getReference().child("userprofile").child(uid).setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        binding.profiledone.setVisibility(View.VISIBLE);
                                                        binding.profileprogressbar.setVisibility(View.GONE);
                                                        startActivity(new Intent(profileActivity.this, MainActivity.class));
                                                        finish();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull @NotNull Exception e) {
                                                binding.profiledone.setVisibility(View.VISIBLE);
                                                binding.profileprogressbar.setVisibility(View.GONE);
                                                Toast.makeText(profileActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                            }
                                        });


                                    }
                                });
                            }
                            else
                            {
                                binding.profiledone.setVisibility(View.VISIBLE);
                                binding.profileprogressbar.setVisibility(View.GONE);
                                Toast.makeText(profileActivity.this, task.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
                else {

                    binding.profiledone.setVisibility(View.GONE);
                    binding.profileprogressbar.setVisibility(View.VISIBLE);
                    String name = binding.profilename.getText().toString();
                    String fullname = binding.profilefullname.getText().toString();
                    String country = binding.profilecountry.getText().toString();
                    String uid = auth.getUid();



//                        String name = binding.namebox.getText().toString();


                    profileinfo user = new profileinfo(uid,name,fullname,country,"No Image","No Mobile Number Avaialbe","","","","");
                    database.getReference().child("userprofile").child(uid).setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    binding.profiledone.setVisibility(View.VISIBLE);
                                    binding.profileprogressbar.setVisibility(View.GONE);
                                    startActivity(new Intent(profileActivity.this,MainActivity.class));
                                    finish();
                                }
                            });


                }
            }
            });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(data.getData()!=null) {
            if (requestCode == 10) {
//            get data uri ka ek method hai
                binding.userimage.setImageURI(data.getData());
//                jo bhi image aai yeh use ek variable me store karwana hai
                selectedimage = data.getData();
            }
        }

    }
}