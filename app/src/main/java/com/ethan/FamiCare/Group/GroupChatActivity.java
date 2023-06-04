package com.ethan.FamiCare.Group;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ethan.FamiCare.Firebasecords.GroupChatAdapter;
import com.ethan.FamiCare.Firebasecords.MessageModelGroup;
import com.ethan.FamiCare.MainActivity;
import com.ethan.FamiCare.databinding.ActivityChatroomBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {

    ActivityChatroomBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth=FirebaseAuth.getInstance();

        binding=ActivityChatroomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        binding.backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GroupChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final ArrayList<MessageModelGroup> messageModelGroups=new ArrayList<>();

        final String senderId= FirebaseAuth.getInstance().getUid();
        binding.username.setText("Group Chat");
        final String receiveId= FirebaseAuth.getInstance().getUid();

        final GroupChatAdapter adapter=new GroupChatAdapter(messageModelGroups,this);
        binding.recyclerview.setAdapter(adapter);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        binding.recyclerview.setLayoutManager(layoutManager);

        database.getReference().child("Group Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageModelGroups.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    MessageModelGroup model=dataSnapshot.getValue(MessageModelGroup.class);
                    messageModelGroups.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid=auth.getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference().
                        child("Users").child(uid).child("username")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String username = snapshot.getValue(String.class);
                        String message=binding.message.getText().toString();
                        MessageModelGroup model=new MessageModelGroup(username,senderId,message);
                        model.setDatetime(new Date().getTime());

                        binding.message.setText("");
                        database.getReference().child("Group Chat").push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(GroupChatActivity.this, "Message Send.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        // 在這裡使用名字
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // 當資料取得失敗時要執行的程式碼
                    }
                });
            }
        });

    }
}