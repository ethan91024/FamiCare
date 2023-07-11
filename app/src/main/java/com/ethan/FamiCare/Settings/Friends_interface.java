package com.ethan.FamiCare.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ethan.FamiCare.Firebasecords.FriendModel;
import com.ethan.FamiCare.Group.GroupChatroom;
import com.ethan.FamiCare.R;
import com.ethan.FamiCare.databinding.ActivityFriendsInterfaceBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Friends_interface extends AppCompatActivity {

    ActivityFriendsInterfaceBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendsInterfaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        uid=auth.getCurrentUser().getUid();
        String friendId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");
        binding.username.setText(userName);
        binding.userid.setText("#"+friendId);
        Picasso.get().load(profilePic).placeholder(R.drawable.avatar_b).into(binding.profileImage);
        binding.chatimage.findViewById(R.id.chatimage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String fuid = userSnapshot.getKey();

                            // 在這裡處理找到的使用者 UID
                            String id = userSnapshot.child("id").getValue(String.class);
                            String username = userSnapshot.child("username").getValue(String.class);
                            String profileImage = userSnapshot.child("profilepic").getValue(String.class);
                            String token = userSnapshot.child("token").getValue(String.class);
                            String type="friend";
                            FriendModel friend = new FriendModel(profileImage, username, id, token,type);
                database.getReference().child("Grouplist").child(uid).child(fuid).setValue(friend)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // 節點寫入成功
                                Toast.makeText(Friends_interface.this, "已將好友加到聊天列表", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // 節點寫入失敗

                            }
                        });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }
}