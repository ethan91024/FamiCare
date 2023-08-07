package com.ethan.FamiCare.Settings;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ethan.FamiCare.Firebasecords.FriendModel;
import com.ethan.FamiCare.Firebasecords.PermissionModel;
import com.ethan.FamiCare.R;
import com.ethan.FamiCare.databinding.ActivityFriendsInterfaceBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Friends_interface extends AppCompatActivity {

    ActivityFriendsInterfaceBinding binding;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String uid, fuid;
    String userName;
    int perStep;
    int perHeartRate;
    int perSpeed;
    int perCalories;
    int perRespiratory;
    int perBloodOxygen;
    int perSleep;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendsInterfaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        uid = auth.getCurrentUser().getUid();
        String friendId = getIntent().getStringExtra("userId");
        userName = getIntent().getStringExtra("userName");
        Log.d("Intent", "userName: " + userName);
        String profilePic = getIntent().getStringExtra("profilePic");
        binding.username.setText(userName);
        binding.userid.setText("#" + friendId);
        Picasso.get().load(profilePic).placeholder(R.drawable.avatar_b).into(binding.profileImage);
        String fuid=getIntent().getStringExtra("fuid");

        // 获取 Firebase 实例
        DatabaseReference usersRef = database.getReference("Users");

        // 使用 orderByChild() 方法查询指定 id 的用户
        usersRef.orderByChild("id").equalTo(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 找到符合條件的使用者節點
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // 獲取對應的 uid
                        String uid = userSnapshot.getKey();

                        // 獲取 Status 資料表中的數據
                        DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference("Status").child(uid);
                        statusRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    // 如果該 UserId 在 Status 資料表中存在
                                    int statusStep = snapshot.child("status_step").getValue(Integer.class);
                                    int statusHeartRate = snapshot.child("status_heartRate").getValue(Integer.class);
                                    int statusSpeed = snapshot.child("status_speed").getValue(Integer.class);
                                    int statusCalories = snapshot.child("status_calories").getValue(Integer.class);
                                    int statusRespiratory = snapshot.child("status_respiratory").getValue(Integer.class);
                                    int statusBloodOxygen = snapshot.child("status_bloodOxygen").getValue(Integer.class);
                                    int statusSleep = snapshot.child("status_sleep").getValue(Integer.class);
                                    perStep=statusStep;
                                    perHeartRate=statusHeartRate;
                                    perSpeed=statusSpeed;
                                    perCalories=statusCalories;
                                    perRespiratory=statusRespiratory;
                                    perBloodOxygen=statusBloodOxygen;
                                    perSleep=statusSleep;
                                    /*
                                    // 更新對應的 TextView
                                    binding.statusStep.setText(getStatusText(statusStep));
                                    binding.statusHeartRate.setText(getStatusText(statusHeartRate));
                                    binding.statusSpeed.setText(getStatusText(statusSpeed));
                                    binding.statusCalories.setText(getStatusText(statusCalories));
                                    binding.statusRespiratory.setText(getStatusText(statusRespiratory));
                                    binding.statusBloodOxygen.setText(getStatusText(statusBloodOxygen));
                                    binding.statusSleep.setText(getStatusText(statusSleep));

                                     */

                                } else {
                                    // 如果該 UserId 在 Status 資料表中不存在，可以設置默認值或顯示 "無資料"
                                    binding.statusStep.setText("無資料");
                                    binding.statusHeartRate.setText("無資料");
                                    binding.statusSpeed.setText("無資料");
                                    binding.statusCalories.setText("無資料");
                                    binding.statusRespiratory.setText("無資料");
                                    binding.statusBloodOxygen.setText("無資料");
                                    binding.statusSleep.setText("無資料");

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // 處理錯誤情況
                            }
                        });
                    }
                    //去permission找權限有沒有開放，沒有就顯示未公開
                    DatabaseReference databaseReference=database.getReference().child("Permission");
                    databaseReference.child(fuid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            PermissionModel permissionModel=snapshot.getValue(PermissionModel.class);
                            if(permissionModel!=null){
                                Boolean step=permissionModel.getStep();
                                Boolean heartrate=permissionModel.getHeartrate();
                                Boolean speed=permissionModel.getSpeed();
                                Boolean calories=permissionModel.getCalories();
                                Boolean breathe=permissionModel.getBreathe();
                                Boolean oxy=permissionModel.getOxygen();
                                Boolean sleep=permissionModel.getSleep();
                                if(step==false){
                                    binding.statusStep.setText("未公開");
                                }else {
                                    // 更新對應的 TextView
                                    binding.statusStep.setText(getStatusText(perStep));
                                }
                                if(heartrate==false){
                                    binding.statusHeartRate.setText("未公開");
                                }else{
                                    binding.statusHeartRate.setText(getStatusText(perHeartRate));
                                }
                                if(speed==false){
                                    binding.statusSpeed.setText("未公開");
                                }else{
                                    binding.statusSpeed.setText(getStatusText(perSpeed));
                                }
                                if(calories==false){
                                    binding.statusCalories.setText("未公開");
                                }else{
                                    binding.statusCalories.setText(getStatusText(perCalories));
                                }
                                if(breathe==false){
                                    binding.statusRespiratory.setText("未公開");
                                }else{
                                    binding.statusRespiratory.setText(getStatusText(perRespiratory));
                                }
                                if(oxy==false){
                                    binding.statusBloodOxygen.setText("未公開");
                                }else{
                                    binding.statusBloodOxygen.setText(getStatusText(perBloodOxygen));
                                }
                                if(sleep==false){
                                    binding.statusSleep.setText("未公開");
                                }else{
                                    binding.statusSleep.setText(getStatusText(perSleep));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    // 没有找到匹配的用户
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 处理数据库查询错误情况
            }
        });

        binding.chatimage.findViewById(R.id.chatimage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.getReference().child("Friend").child(uid).child(userName)
                        //.orderByChild("id").equalTo(friendId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    String fuid = userSnapshot.child("fuid").getValue(String.class);

                                    // 在這裡處理找到的使用者 UID
                                    String token = userSnapshot.child("token").getValue(String.class);
                                    String type = "friend";
                                    Boolean permission=false;
                                    FriendModel friend = new FriendModel(profilePic, userName, friendId, token, type, fuid,permission);
                                    database.getReference().child("Grouplist").child(uid).child(userName).setValue(friend).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                                    Toast.makeText(Friends_interface.this, "失敗", Toast.LENGTH_SHORT).show();
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

        //返回按鈕
        binding.turnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }


    // Helper 方法來根據數值返回對應的狀態文字
    private String getStatusText(int status) {
        switch (status) {
            case 0:
                return "無資料";
            case 1:
                return "待加油";
            case 2:
                return "及格";
            case 3:
                return "滿分";
            default:
                return "未知狀態";
        }
    }
}