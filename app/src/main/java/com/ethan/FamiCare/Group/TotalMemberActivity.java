package com.ethan.FamiCare.Group;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ethan.FamiCare.Firebasecords.FriendAdapter;
import com.ethan.FamiCare.Firebasecords.FriendModel;
import com.ethan.FamiCare.Firebasecords.TotalmemberAdapter;
import com.ethan.FamiCare.MainActivity;
import com.ethan.FamiCare.R;
import com.ethan.FamiCare.databinding.ActivityTotalMemberBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TotalMemberActivity extends AppCompatActivity {
    ActivityTotalMemberBinding binding;
    FirebaseAuth auth;
    ArrayList<FriendModel> list = new ArrayList<>();
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        String uid = auth.getCurrentUser().getUid();
        String groupuid=getIntent().getStringExtra("groupuid");
        binding = ActivityTotalMemberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        TotalmemberAdapter adapter=new TotalmemberAdapter(list,this.getApplicationContext());
        binding.recyclerview.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getApplicationContext());
        binding.recyclerview.setLayoutManager(layoutManager);
        database.getReference().child("Group")
                .child(uid)
                .child(groupuid)
                .child("Alluseruid")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FriendModel users = dataSnapshot.getValue(FriendModel.class);
                    users.setUserId(dataSnapshot.getKey());
                    list.add(users);
                    Log.d("TAG", "Message: " + users);
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
