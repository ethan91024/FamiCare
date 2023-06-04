package com.ethan.FamiCare.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ethan.FamiCare.Firebasecords.FriendAdapter;
import com.ethan.FamiCare.Firebasecords.FriendModel;
import com.ethan.FamiCare.MainActivity;
import com.ethan.FamiCare.databinding.ActivityFriendsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {

ActivityFriendsBinding binding;
    ArrayList<FriendModel> list = new ArrayList<>();
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth auth=FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        binding=ActivityFriendsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        binding.backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FriendsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        binding.addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FriendsActivity.this, Addfriend.class);
                startActivity(intent);
            }
        });
        FriendAdapter adapter=new FriendAdapter(list,this.getApplicationContext());

        binding.recyclerview.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getApplicationContext());
        binding.recyclerview.setLayoutManager(layoutManager);

        database.getReference().child("Friend").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FriendModel users = dataSnapshot.getValue(FriendModel.class);
                    users.setUserId(dataSnapshot.getKey());
                    if(!users.getUserId().equals(FirebaseAuth.getInstance().getUid())){
                        list.add(users);
                        Log.d("TAG", "Message: " + users);
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }
}