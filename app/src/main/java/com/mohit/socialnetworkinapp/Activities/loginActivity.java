package com.mohit.socialnetworkinapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.mohit.socialnetworkinapp.R;
import com.mohit.socialnetworkinapp.databinding.ActivityLoginBinding;
import com.mohit.socialnetworkinapp.databinding.ActivityRegisterBinding;

import java.util.HashMap;

public class loginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    Toolbar toolbar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();
//        checkuserexistence();
//
//        SharedPreferences sp1 = getSharedPreferences("profileinfo",MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp1.edit();
//        editor.putString("userid",auth.getCurrentUser().getUid());
//        editor.apply();


        binding.loginSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(loginActivity.this,register.class));
            }
        });
        binding.loginSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.loginEmail.getText().toString().isEmpty())
                {
                    binding.loginEmail.setError("Please Provide your Email");
                    
                    binding.loginEmail.requestFocus();
                    return;
                }
                if(binding.loginPassword.getText().toString().isEmpty())
                {
                    binding.loginPassword.setError("Please Provide your Password");
                    binding.loginPassword.requestFocus();
                    return;
                }
                if(!binding.loginCheckbox.isChecked())
                {
                    Toast.makeText(loginActivity.this, "Please Accept Terms and Condition ", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    binding.loginSignin.setVisibility(View.INVISIBLE);
                    binding.loginprogressbar.setVisibility(View.VISIBLE);
                    String login_email = binding.loginEmail.getText().toString();
                    String login_password = binding.loginPassword.getText().toString();

                    auth.signInWithEmailAndPassword(login_email,login_password)
                            .addOnCompleteListener(loginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                            @Override
                                            public void onComplete(@NonNull Task<String> task) {
                                                if(task.isSuccessful())
                                                {
                                                    HashMap <String,Object> map = new HashMap<>();
                                                    map.put("token",task.getResult());
                                                    FirebaseDatabase.getInstance().getReference().child("userprofile").child(auth.getUid()).updateChildren(map);
                                                }

                                            }
                                        });

                                        binding.loginSignin.setVisibility(View.VISIBLE);
                                        binding.loginprogressbar.setVisibility(View.INVISIBLE);
                                        binding.loginEmail.setText("");
                                        binding.loginPassword.setText("");
                                        Intent intent = new Intent(loginActivity.this,MainActivity.class);
                                        startActivity(intent) ;
                                        finish();

                                    } else {

                                        binding.loginSignin.setVisibility(View.VISIBLE);
                                        binding.loginprogressbar.setVisibility(View.INVISIBLE);
                                        binding.loginEmail.setText("");
                                        binding.loginPassword.setText("");
                                        Toast.makeText(loginActivity.this, task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }
        });

    }
//    private void checkuserexistence() {
//        SharedPreferences sp = getSharedPreferences("profileinfo",MODE_PRIVATE);
//        if(sp.equals(auth.getCurrentUser().getUid()))
//        {
//            Intent intent = new Intent(loginActivity.this,MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finish();
//        }
//
//
//    }
}