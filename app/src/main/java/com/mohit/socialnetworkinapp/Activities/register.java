package com.mohit.socialnetworkinapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.mohit.socialnetworkinapp.Models.registerinformatiom;
import com.mohit.socialnetworkinapp.R;
import com.mohit.socialnetworkinapp.databinding.ActivityRegisterBinding;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class register extends AppCompatActivity {
    ActivityRegisterBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    GoogleSignInClient mGoogleSignInClient;
    ProgressDialog dialog;
    FirebaseStorage storage;
    @Override
    public void onStart() {
        super.onStart();
        if (auth.getCurrentUser()!=null)
        {
            startActivity(new Intent(register.this,MainActivity.class));
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        auth = FirebaseAuth.getInstance();
        database =FirebaseDatabase.getInstance();
        storage= FirebaseStorage.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("User is getting Register");
        dialog.setCancelable(false);
        processrequest();
        binding.registersignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(register.this,loginActivity.class));
            }
        });
        binding.registersignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.registername.getText().toString().isEmpty())
                {
                    binding.registername.setError("Please Provide your Name");
                    binding.registername.requestFocus();
                    return;
                }
                if(binding.registeremail.getText().toString().isEmpty())
                {
                    binding.registeremail.setError("Please Provide your Email");
                    binding.registeremail.requestFocus();
                    return;
                }
                if(binding.registerpassword.getText().toString().isEmpty())
                {
                    binding.registerpassword.setError("Please Provide your Password");
                    binding.registerpassword.requestFocus();
                    return;
                }
                if(!binding.checkbox.isChecked())
                {
                    Toast.makeText(register.this, "Please Accept Terms and Condition ", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {

                    binding.registersignup.setVisibility(View.INVISIBLE);
                    binding.registerprogressbar.setVisibility(View.VISIBLE);
                    String email = binding.registeremail.getText().toString();
                    String name = binding.registername.getText().toString();
                    String password = binding.registerpassword.getText().toString();



                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        registerinformatiom user = new registerinformatiom(binding.registername.getText().toString(),auth.getUid(),binding.registeremail.getText().toString(),binding.registerpassword.getText().toString(),"Not Available");
                                        database.getReference().child("registerinfo").child(auth.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                binding.registersignup.setVisibility(View.VISIBLE);
                                                binding.registerprogressbar.setVisibility(View.INVISIBLE);

                                                binding.registersignup.setVisibility(View.VISIBLE);
                                                binding.registeremail.setText("");
                                                binding.registerpassword.setText("");
                                                binding.registername.setText("");


                                                Toast.makeText(register.this, "User Register Successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(register.this,loginActivity.class);
                                                startActivity(intent);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull @NotNull Exception e) {

                                                binding.registersignup.setVisibility(View.VISIBLE);
                                                binding.registerprogressbar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(register.this, e.toString(), Toast.LENGTH_LONG).show();

                                            }
                                        });

                                    } else {
                                        binding.registersignup.setVisibility(View.VISIBLE);
                                        binding.registerprogressbar.setVisibility(View.INVISIBLE);
                                        binding.registersignup.setVisibility(View.VISIBLE);
                                        binding.registeremail.setText("");
                                        binding.registerpassword.setText("");
                                        binding.registername.setText("");
                                        Toast.makeText(register.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        binding.registerGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processlogin();
            }
        });


    }

    // google signin process begin
    private void processrequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void processlogin() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 103);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 103) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                dialog.show();
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if(task.isSuccessful())
                                    {
                                        HashMap<String,Object> map = new HashMap<>();
                                        map.put("token",task.getResult());
                                        FirebaseDatabase.getInstance().getReference().child("userprofile").child(auth.getUid()).updateChildren(map);
                                    }

                                }
                            });


                            FirebaseUser user = auth.getCurrentUser();
                            String Email = user.getEmail();
                            String Name = user.getDisplayName();
                            String imageurl = user.getPhotoUrl().toString();
                            String uid = user.getUid().toString();
                            String phone = user.getPhoneNumber();

                            registerinformatiom dum = new registerinformatiom(Name,uid,Email,"Not Available",imageurl);
                            database.getReference().child("registerinfo").child(uid).setValue(dum).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dialog.dismiss();
                                    startActivity(new Intent(register.this,profileActivity.class));
                                    Toast.makeText(register.this, "User Register Successfully", Toast.LENGTH_SHORT).show();
                                    finish();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    dialog.dismiss();
                                    Toast.makeText(register.this, e.toString(), Toast.LENGTH_LONG).show();

                                }
                            });


                        } else {
                            dialog.dismiss();
                            Toast.makeText(register.this, task.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    // Google signing prcoess end

}