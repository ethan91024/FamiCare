package com.ethan.FamiCare.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ethan.FamiCare.Firebasecords.FriendModel;
import com.ethan.FamiCare.MainActivity;
import com.ethan.FamiCare.R;
import com.ethan.FamiCare.databinding.ActivityAddfriendBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Addfriend extends AppCompatActivity {

    ActivityAddfriendBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    Button add,cancel;
    TextView editText,inputid;
    String username,userid;//在程式打的好友的名字與ID
    String uid;//使用者的uid
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend);
        binding= ActivityAddfriendBinding.inflate(getLayoutInflater());

        add=findViewById(R.id.add);
        cancel=findViewById(R.id.cancel);
        editText = findViewById(R.id.editText); // 初始化 editText
        inputid = findViewById(R.id.inputid); // 初始化 inputid

        auth=FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        uid=auth.getCurrentUser().getUid();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Addfriend.this, FriendsActivity.class);
                startActivity(intent);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username=editText.getText().toString();
                userid=inputid.getText().toString();
                if (username.isEmpty() || userid.isEmpty()) {
                    Toast.makeText(Addfriend.this, "請填入使用者名稱或ID", Toast.LENGTH_LONG).show();
                } else {
                    Query usernameQ = database.getReference().child("Users").orderByChild("username").equalTo(username);
                    usernameQ.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                String fuid = userSnapshot.getKey();

                                // 在這裡處理找到的使用者 UID
                                String id = userSnapshot.child("id").getValue(String.class);
                                String username = userSnapshot.child("username").getValue(String.class);
                                String profileImage = userSnapshot.child("profilepic").getValue(String.class);
                                String token=userSnapshot.child("token").getValue(String.class);
                                String type="friend";
                                if (id.equals(userid)) {

                                    // 在這裡將 uid、username、token 和 profileImage 加入到 "Friend" 資料庫中
                                    FriendModel friend = new FriendModel( profileImage,username, id,token,type,fuid);
                                     database.getReference().child("Friend").child(uid).child(username).setValue(friend)
                                             .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                 @Override
                                                 public void onSuccess(Void aVoid) {
                                                     // 節點寫入成功
                                                     Toast.makeText(Addfriend.this, "新增好友已成功", Toast.LENGTH_SHORT).show();
                                                 }
                                             })
                                             .addOnFailureListener(new OnFailureListener() {
                                                 @Override
                                                 public void onFailure(@NonNull Exception e) {
                                                     // 節點寫入失敗
                                                     Toast.makeText(Addfriend.this, "新增好友失敗: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                 }
                                             });


                                }
                                FriendModel friend = new FriendModel( profileImage,username, id,token,type,fuid);
                                database.getReference().child("Grouplist").child(uid).child(username).setValue(friend)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // 節點寫入成功
                                                Toast.makeText(Addfriend.this, "已將好友加到聊天列表", Toast.LENGTH_SHORT).show();
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
                            // 發生錯誤時的處理邏輯
                            Toast.makeText(Addfriend.this, "錯誤", Toast.LENGTH_LONG).show();
                        }
                    });

                    Intent intent = new Intent(Addfriend.this, FriendsActivity.class);
                    startActivity(intent);
                }
            }
        });



    }
}