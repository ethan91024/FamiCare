
package com.ethan.FamiCare;

import android.app.AppComponentFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ethan.FamiCare.Firebasecords.ChatAdapter;
import com.ethan.FamiCare.Firebasecords.GroupMessage;
import com.ethan.FamiCare.databinding.FragmentGroupChatroomBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class GroupChatroom extends AppCompatActivity {
    ChatAdapter adapter;
    RecyclerView recyclerView;//1
    final ArrayList<GroupMessage> list =new ArrayList<>();
    FragmentGroupChatroomBinding binding;
    DatabaseReference db;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;


    public GroupChatroom() {
        // Required empty public constructor
    }




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=FragmentGroupChatroomBinding.inflate(getLayoutInflater());
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        final String senderId=auth.getUid();
        String recieverId=getIntent().getStringExtra("userId");
        String userName=getIntent().getStringExtra("userName");
        String profilePic=getIntent().getStringExtra("profilePic");

        binding.username.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.avatar_b).into(binding.profileImage);

    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_chatroom, container, false);


        return view;
    }


}