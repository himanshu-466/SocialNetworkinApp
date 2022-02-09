package com.mohit.socialnetworkinapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohit.socialnetworkinapp.Adapters.othersfriendsadapter;
import com.mohit.socialnetworkinapp.Models.others_friends_model;
import com.mohit.socialnetworkinapp.Models.postmodel;
import com.mohit.socialnetworkinapp.Models.profileinfo;
import com.mohit.socialnetworkinapp.R;
import com.mohit.socialnetworkinapp.databinding.ActivityOtherUserProfileBinding;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class OtherUserProfile extends AppCompatActivity {
   ActivityOtherUserProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseUser user;
    String uid;
    String username,profileimageurl;
    DatabaseReference friendrequest, FriendAccept;
    String CurrenState="not_friends";
    String semderuid,recieveruid,token;
    long countpost,countfriends;
    ArrayList<others_friends_model> model;
    othersfriendsadapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityOtherUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user =FirebaseAuth.getInstance().getCurrentUser();
        friendrequest= FirebaseDatabase.getInstance().getReference().child("FriendRequest");
        FriendAccept = FirebaseDatabase.getInstance().getReference().child("FriendAccepted");
        model = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.othersfrndsrecylcerview.setLayoutManager(linearLayoutManager);
        binding.othersfrndsrecylcerview.setNestedScrollingEnabled(false);
        adapter = new othersfriendsadapter(model,this);
        binding.othersfrndsrecylcerview.setAdapter(adapter);

        if(getIntent().getStringExtra("allpostuid")!=null)
        {
            uid= getIntent().getStringExtra("allpostuid");
        }
        else if(getIntent().getStringExtra("friendssearch")!=null)
        {
            uid=getIntent().getStringExtra("friendssearch");
        }
        else
        {
            uid=getIntent().getStringExtra("myfriendsearch");
        }
        binding.othersnoofpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OtherUserProfile.this,Mypost.class);
                intent.putExtra("uid",uid);
                startActivity(intent);
            }
        });
        binding.otherspost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OtherUserProfile.this,Mypost.class);
                intent.putExtra("uid",uid);
                startActivity(intent);
            }
        });

        database.getReference().child("Posts").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    countpost=snapshot.getChildrenCount();
                    binding.othersnoofpost.setText(String.valueOf(countpost));

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        database.getReference().child("FriendAccepted").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        final String friendsuid = snapshot1.getKey();
                        database.getReference().child("userprofile").child(friendsuid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    others_friends_model info = snapshot.getValue(others_friends_model.class);
                                    info.setProfile(snapshot.child("imageurl").getValue(String.class));
                                    model.add(info);

                                    adapter.notifyDataSetChanged();
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
                    }
                }
                else
                {
                        binding.nofriends.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        database.getReference().child("FriendAccepted").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    countfriends=snapshot.getChildrenCount();
                     binding.othersnooffriends.setText(String.valueOf(countfriends));

                    }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        database.getReference().child("userprofile").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    profileinfo info = snapshot.getValue(profileinfo.class);

                    String image = info.getImageurl();
                    String cover = info.getCover();
                    profileimageurl= info.getImageurl();
                    username = info.getFullname();
                    binding.otherusername.setText(username);
                    token=info.getToken();

                    Glide.with(OtherUserProfile.this).load(image).placeholder(R.drawable.placeholder).into(binding.otherprofileImage);
                    Glide.with(OtherUserProfile.this).load(cover).placeholder(R.drawable.select_image).into(binding.othercoverimage);
                    MaintainanceofButtons();

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        semderuid = auth.getCurrentUser().getUid();
        recieveruid = uid;
        binding.declinerequest.setVisibility(View.INVISIBLE);

        MaintainanceofButtons();
        binding.sentfriendrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              binding.sentfriendrequest.setEnabled(false);

                if(CurrenState.equals("not_friends"))
                {
                    sendfriendrequest();
                }
                if(CurrenState.equals("request_sent"))
                {
                  canclerequrest();
                }
                if(CurrenState.equals("request_received"))
                {

                    AcceptFriendRequest();
                }
                if(CurrenState.equals("friends"))
                {

                    Unfriendexistingfriend();
                }


            }
        });








    }

    private void Unfriendexistingfriend() {
        FriendAccept.child(semderuid).child(recieveruid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    FriendAccept.child(recieveruid).child(semderuid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                binding.sentfriendrequest.setEnabled(true);
                                CurrenState ="not_friends";
                                binding.sentfriendrequest.setText("Friend Request");
                                binding.declinerequest.setVisibility(View.INVISIBLE);
                                binding.declinerequest.setEnabled(false);
                            }

                        }
                    });
                }
            }
        });
    }

    private void AcceptFriendRequest() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
        String savecurrentdate = currentdate.format(c.getTime());


        FriendAccept.child(semderuid).child(recieveruid).child("date").setValue(savecurrentdate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    FriendAccept.child(recieveruid).child(semderuid).child("date").setValue(savecurrentdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                friendrequest.child(semderuid).child(recieveruid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            friendrequest.child(recieveruid).child(semderuid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        binding.sentfriendrequest.setEnabled(true);
                                                        CurrenState ="friends";
                                                        binding.sentfriendrequest.setText("UnFriend");
                                                        binding.declinerequest.setVisibility(View.VISIBLE);
                                                        binding.declinerequest.setEnabled(true);
                                                        binding.declinerequest.setText("Send Message");


                                                    }

                                                }
                                            });
                                        }

                                    }
                                });
                            }

                        }
                    });
                }

            }
        });

    }

    private void canclerequrest() {
        friendrequest.child(semderuid).child(recieveruid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    friendrequest.child(recieveruid).child(semderuid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                binding.sentfriendrequest.setEnabled(true);
                                CurrenState ="not_friends";
                                binding.sentfriendrequest.setText("Friend Request");
                                binding.declinerequest.setVisibility(View.INVISIBLE);
                                binding.declinerequest.setEnabled(false);

                            }

                        }
                    });
                }

            }
        });
    }

    private  void  MaintainanceofButtons() {

        friendrequest.child(semderuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(recieveruid))
                {
                    String request_type = snapshot.child(recieveruid).child("request_type").getValue().toString();
                    if(request_type.equals("sent"))
                    {
                        CurrenState ="request_sent";
                        binding.sentfriendrequest.setText("Cancel Request");
                        binding.declinerequest.setVisibility(View.INVISIBLE);
                        binding.declinerequest.setEnabled(false);

                    }
                   else if(request_type.equals("received"))
                    {
                        CurrenState = "request_received";
                        binding.sentfriendrequest.setText("Accept Request");
                        binding.declinerequest.setVisibility(View.VISIBLE);
                        binding.declinerequest.setEnabled(true);
                        binding.declinerequest.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                canclerequrest();
                            }
                        });
                    }




                }
                else
                {
                    FriendAccept.child(semderuid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(recieveruid))
                            {
                                CurrenState="friends";
                                binding.sentfriendrequest.setText("Unfriend");
                                binding.declinerequest.setEnabled(true);
                                binding.declinerequest.setVisibility(View.VISIBLE);
                                binding.declinerequest.setText("Send Message");
                                binding.declinerequest.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                       Intent intent = new Intent(OtherUserProfile.this,chatdetailedActivity.class);
                                       intent.putExtra("username",username);
                                       intent.putExtra("profileimage",profileimageurl);
                                       intent.putExtra("muid",recieveruid);
                                       intent.putExtra("tokenid2",token);
                                       startActivity(intent);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void sendfriendrequest() {

    friendrequest.child(semderuid).child(recieveruid).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull @NotNull Task<Void> task) {
            if(task.isSuccessful())
            {
                friendrequest.child(recieveruid).child(semderuid).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            binding.sentfriendrequest.setEnabled(true);
                            CurrenState ="request_sent";
                            binding.sentfriendrequest.setText("Cancel Request");
                            binding.declinerequest.setVisibility(View.INVISIBLE);
                            binding.declinerequest.setEnabled(false);

                        }

                    }
                });
            }

        }
    });

    }

    }
