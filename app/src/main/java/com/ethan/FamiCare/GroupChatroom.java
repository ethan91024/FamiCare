
package com.ethan.FamiCare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ethan.FamiCare.Firebasecords.ChatAdapter;
import com.ethan.FamiCare.Firebasecords.MessageModel;
import com.ethan.FamiCare.databinding.FragmentGroupChatroomBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Date;

public class GroupChatroom extends AppCompatActivity {
    ChatAdapter adapter;
    RecyclerView recyclerView;//1
    final ArrayList<MessageModel> list =new ArrayList<>();
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

        binding = FragmentGroupChatroomBinding.inflate(getLayoutInflater());
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        final String senderId = auth.getUid();
        String recieveId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        binding.username.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.avatar_b).into(binding.profileImage);
        binding.backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupChatroom.this, GroupFragment.class);
                startActivity(intent);
            }
        });

        final ArrayList<MessageModel> messageModels = new ArrayList<>();
        final ChatAdapter chatAdapter = new ChatAdapter(messageModels, this, recieveId);
        binding.recyclerview.setAdapter(chatAdapter);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        binding.recyclerview.setLayoutManager(layoutManager);

        final String senderRoom=senderId+recieveId;
        final String receiverRoom=recieveId+senderId;

        database.getReference().child("chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageModels.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    MessageModel model=snapshot1.getValue(MessageModel.class);
                    model.setMessageId(snapshot1.getKey());
                    messageModels.add(model);
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=binding.message.getText().toString();
                final MessageModel model=new MessageModel(senderId,message);
                model.setDatetime(new Date().getTime());
                binding.message.setText("");

                database.getReference().child("chats").child(senderRoom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.getReference().child("chats").child(receiverRoom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });
                    }
                });
            }
        });
    }
}