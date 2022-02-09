package com.mohit.socialnetworkinapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohit.socialnetworkinapp.Adapters.allpostAdapter;
import com.mohit.socialnetworkinapp.Adapters.frndsadapter;
import com.mohit.socialnetworkinapp.Adapters.myfriendsAdapter;
import com.mohit.socialnetworkinapp.Models.postmodel;
import com.mohit.socialnetworkinapp.Models.profileinfo;
import com.mohit.socialnetworkinapp.R;
import com.mohit.socialnetworkinapp.databinding.ActivityMainBinding;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ArrayList<profileinfo> list, list1;
    ActionBarDrawerToggle toggle;
    Toolbar appbarlayout;
    frndsadapter adapter1;
    ArrayList<postmodel> postmodel;
    allpostAdapter adapter;
    myfriendsAdapter myfriendsAdapter1;
    RecyclerView recyclerView;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ImageView headerimage, addpostbutton;
    TextView headerusername;
    NavigationView navview;
    FloatingActionButton floatingbutton;
    Uri selecteImage;
    Dialog dialog;
    EditText dialogcaption, dialogtag;
    AppCompatButton dialogsubmit;
    String savecurrentdate, savecurrrentTime;
    ProgressDialog progressDialog;
    ProgressBar dialogprogressbar;
    String fullname, userprofile;
    BottomSheetDialog bottomSheetDialog;
    Long countpost;

    @Override
    protected void onStart() {
        super.onStart();
        database.getReference("userprofile").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                {
                    startActivity(new Intent(MainActivity.this,profileActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        navview = findViewById(R.id.navigation_view);
        addpostbutton = findViewById(R.id.addNewPostButton);
        progressDialog = new ProgressDialog(this);
        dialogprogressbar = new ProgressBar(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading ....");
        postmodel = new ArrayList<>();
        recyclerView = findViewById(R.id.main_recycleview);
        adapter = new allpostAdapter(postmodel, this);
        String postid = database.getReference().child("Posts").child(auth.getUid()).push().getKey();

        View headerview = navview.getHeaderView(0);
        headerimage = headerview.findViewById(R.id.header_image);
        headerusername = headerview.findViewById(R.id.header_username);
        floatingbutton = headerview.findViewById(R.id.floatingActionButton2);
        floatingbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Myprofile.class));

            }
        });

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog);
        dialog.setCancelable(false);// yeh line dialog box ko bahar tab karne ke baad bhi dismiss nahi hoga
        dialogcaption = dialog.findViewById(R.id.dialog_caption);
        dialogtag = dialog.findViewById(R.id.dialog_tag);
        dialogsubmit = dialog.findViewById(R.id.dialog_submit);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        appbarlayout = findViewById(R.id.appbarlayout);
        appbarlayout.setTitle("Main Activity");

        setSupportActionBar(appbarlayout);
        toggle = new ActionBarDrawerToggle(this, binding.drawer, appbarlayout, R.string.open, R.string.close);

        binding.drawer.addDrawerListener(toggle);
        toggle.syncState();


        addpostbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addpostwork();
            }
        });
        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_my_post:
                        startActivity(new Intent(MainActivity.this, Mypost.class));
                        break;
                    case R.id.nav_about:
                        startActivity(new Intent(MainActivity.this, About.class));
                        break;
                    case R.id.nav_find_friends:
                        bottomsheetwork();
                        break;
                    case R.id.nav_home:
                        Toast.makeText(MainActivity.this, "home is clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_logout:
                        signOut();
                        break;
                    case R.id.nav_settings:
                        Toast.makeText(MainActivity.this, "setting is clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_messages:
                       startActivity(new Intent(MainActivity.this,MessagesActivity.class));
                        break;
                    case R.id.nav_my_friends:
                        bottomsheet2work();
                        break;
                }

                binding.drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });

        database.getReference().child("userprofile").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    profileinfo profile = snapshot.getValue(profileinfo.class);

                    fullname = profile.getFullname();
                    userprofile = profile.getImageurl();
                    headerusername.setText(fullname);
                    Glide.with(MainActivity.this).load(userprofile).placeholder(R.drawable.placeholder).into(headerimage);

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        // displaying all post into recyccler view
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        // for decendingly traverse the firebase database;
        database.getReference().child("Allposts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    countpost=snapshot.getChildrenCount();
                }
                else
                {
                    countpost= Long.valueOf(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference().child("Allposts").orderByChild("countpost").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                postmodel.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        postmodel model = new postmodel();
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

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void bottomsheet2work() {
        RecyclerView rec;
        BottomSheetDialog bottomSheetDialog1 = new BottomSheetDialog(MainActivity.this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.bottomsheetmyfriends,findViewById(R.id.sheet1));
        list1 = new ArrayList<>();
        myfriendsAdapter1 = new myfriendsAdapter(list1, MainActivity.this);
        rec = view.findViewById(R.id.myfriendsRecyclerview);
        database.getReference().child("FriendAccepted").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        final String friendsuid = snapshot1.getKey();
                        database.getReference().child("userprofile").child(friendsuid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    profileinfo info = snapshot.getValue(profileinfo.class);
                                    info.setCountry(snapshot.child("country").getValue(String.class));
                                    info.setFullname(snapshot.child("fullname").getValue(String.class));
                                    info.setImageurl(snapshot.child("imageurl").getValue(String.class));
                                    info.setUid(snapshot.child("uid").getValue(String.class));
                                    list1.add(info);

                                    myfriendsAdapter1.notifyDataSetChanged();
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

        rec.setAdapter(myfriendsAdapter1);
        bottomSheetDialog1.setContentView(view);
        bottomSheetDialog1.show();

    }

    private void bottomsheetwork() {
        ImageView searchbuton;
        EditText searchbox;
        RecyclerView recyclerView;


        bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.bottomsheetfindsfriends, findViewById(R.id.sheet));
        list = new ArrayList<>();
        adapter1 = new frndsadapter(list, MainActivity.this);

        searchbuton = view.findViewById(R.id.searchbutton);
        searchbox = view.findViewById(R.id.searchbox);
        recyclerView = view.findViewById(R.id.findfrndsRecycler);
        database.getReference().child("userprofile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        profileinfo info = new profileinfo();
                        info.setCountry(snapshot1.child("country").getValue(String.class));
                        info.setFullname(snapshot1.child("fullname").getValue(String.class));
                        info.setImageurl(snapshot1.child("imageurl").getValue(String.class));
                        info.setUid(snapshot1.child("uid").getValue(String.class));
                        list.add(info);


                    }
                    adapter1.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        recyclerView.setAdapter(adapter1);
        searchbuton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchbox.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        filter(s.toString());

                    }
                });

            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void filter(String text) {
        ArrayList<profileinfo> filterlist = new ArrayList<>();
        for (profileinfo items : list) {
            if (items.getFullname().toLowerCase().contains(text.toLowerCase())) {
                filterlist.add(items);
            }
        }
        adapter1.filterlist1(filterlist);

    }

    private void addpostwork() {
        ImagePicker.with(MainActivity.this)
                .crop()
                //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start(20);

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (data.getData() != null) {
            if (requestCode == 20) {
                progressDialog.show();
                selecteImage = data.getData();
                Calendar c = Calendar.getInstance();
                SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
                savecurrentdate = currentdate.format(c.getTime());

                Calendar ca = Calendar.getInstance();
                SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
                savecurrrentTime = currenttime.format(ca.getTime());
                String uniquenode = savecurrentdate + savecurrrentTime;

                StorageReference reference = storage.getReference().child("Post").child((uniquenode));
                reference.putFile(selecteImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String postimage = uri.toString();
                                    progressDialog.dismiss();
                                    dialog.show();
                                    dialogsubmit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogprogressbar.setVisibility(View.VISIBLE);
                                            dialogsubmit.setVisibility(View.INVISIBLE);
                                            Calendar c = Calendar.getInstance();
                                            SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
                                            String date = currentdate.format(c.getTime());

                                            Calendar ca = Calendar.getInstance();
                                            SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
                                            String times = currenttime.format(ca.getTime());

                                            String caption = dialogcaption.getText().toString();
                                            String tag = dialogtag.getText().toString();


                                            String postid = database.getReference().child("Posts").child(auth.getUid()).push().getKey();
                                            postmodel model = new postmodel(auth.getUid(), fullname, userprofile, caption, tag, postimage, date, times, postid,countpost);

                                            database.getReference().child("Posts").child(auth.getUid()).child(postid).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    database.getReference().child("Allposts").child(postid).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            dialogprogressbar.setVisibility(View.INVISIBLE);
                                                            dialogsubmit.setVisibility(View.VISIBLE);
                                                            dialog.dismiss();
                                                        }
                                                    });

                                                }
                                            });


                                        }
                                    });


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this, task.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

        }

    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, register.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        String currentId = FirebaseAuth.getInstance().getUid();
//        database.getReference().child("presence").child(currentId).setValue("online");
//
//    }
//    @Override
//    protected void onPause() {
//        super.onPause();
//        String currentId = FirebaseAuth.getInstance().getUid();
//        database.getReference().child("presence").child(currentId).setValue("offline");
//    }

}
