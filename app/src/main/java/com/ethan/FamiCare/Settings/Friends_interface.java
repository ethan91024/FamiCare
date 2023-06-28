package com.ethan.FamiCare.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ethan.FamiCare.R;
import com.ethan.FamiCare.databinding.ActivityFriendsInterfaceBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Friends_interface extends AppCompatActivity {

    ActivityFriendsInterfaceBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendsInterfaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        String friendId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");
        binding.username.setText(userName);
        binding.userid.setText("#"+friendId);
        Picasso.get().load(profilePic).placeholder(R.drawable.avatar_b).into(binding.profileImage);
    }
}