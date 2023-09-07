package com.ethan.FamiCare.Settings;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ethan.FamiCare.Firebasecords.FriendAdapter;
import com.ethan.FamiCare.Firebasecords.FriendModel;
import com.ethan.FamiCare.Firebasecords.PermissionModel;
import com.ethan.FamiCare.MainActivity;
import com.ethan.FamiCare.R;
import com.ethan.FamiCare.databinding.ActivityFriendsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    Boolean step = false;
    Boolean heartrate = false;
    Boolean speed = false;
    Boolean calories = false;
    Boolean breathe = false;
    Boolean oxygen = false;
    Boolean sleep = false;

    String currentuser;
    RelativeLayout friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        String uid = auth.getCurrentUser().getUid();
        currentuser = uid;
        binding = ActivityFriendsBinding.inflate(getLayoutInflater());
        friend=findViewById(R.id.friend);
        setContentView(binding.getRoot());

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "請先登入", Toast.LENGTH_SHORT).show();
        } else {
            database.getReference().child("Permission").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        System.out.println("存在");
                        PermissionModel permissionModel = snapshot.getValue(PermissionModel.class);
                        if (permissionModel != null) {
                            step = permissionModel.getStep();
                            heartrate = permissionModel.getHeartrate();
                            speed = permissionModel.getSpeed();
                            calories = permissionModel.getCalories();
                            breathe = permissionModel.getBreathe();
                            oxygen = permissionModel.getOxygen();
                            sleep = permissionModel.getSleep();
                        }
                        binding.switch2.setChecked(step);
                        binding.switch3.setChecked(heartrate);
                        binding.switch4.setChecked(speed);
                        binding.switch5.setChecked(calories);
                        binding.switch6.setChecked(breathe);
                        binding.switch7.setChecked(oxygen);
                        binding.switch8.setChecked(sleep);
                    } else {
                        System.out.println("沒有帳號");
                        PermissionModel permissionModel = new PermissionModel(false, false, false, false, false, false, false);
                        database.getReference().child("Permission").child(uid).setValue(permissionModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(FriendsActivity.this, "權限創建", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }
        binding.switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//步數
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 当开关打开时执行的代码
                    step = isChecked;
                } else {
                    // 当开关关闭时执行的代码
                    step = false;
                }
                updatePermissionData();
            }
        });
        binding.switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//步數
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 当开关打开时执行的代码
                    heartrate = isChecked;
                } else {
                    // 当开关关闭时执行的代码
                    heartrate = false;
                }
                updatePermissionData();
            }
        });
        binding.switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//步數
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 当开关打开时执行的代码
                    speed = isChecked;
                } else {
                    // 当开关关闭时执行的代码
                    speed = false;
                }
                updatePermissionData();
            }
        });
        binding.switch5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//步數
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 当开关打开时执行的代码
                    calories = isChecked;
                } else {
                    // 当开关关闭时执行的代码
                    calories = false;
                }
                updatePermissionData();
            }
        });
        binding.switch6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//步數
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 当开关打开时执行的代码
                    breathe = isChecked;
                } else {
                    // 当开关关闭时执行的代码
                    breathe = false;
                }
                updatePermissionData();
            }
        });
        binding.switch7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//步數
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 当开关打开时执行的代码
                    oxygen = isChecked;
                } else {
                    // 当开关关闭时执行的代码
                    oxygen = false;
                }
                updatePermissionData();
            }
        });
        binding.switch8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//步數
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 当开关打开时执行的代码
                    sleep = isChecked;
                } else {
                    // 当开关关闭时执行的代码
                    sleep = false;
                }
                updatePermissionData();
            }
        });


        binding.backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FriendsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        binding.addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsActivity.this, Addfriend.class);
                startActivity(intent);
            }
        });


        FriendAdapter adapter = new FriendAdapter(list, this.getApplicationContext());
        binding.recyclerview.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getApplicationContext());
        binding.recyclerview.setLayoutManager(layoutManager);

        database.getReference().child("Friend").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FriendModel users = dataSnapshot.getValue(FriendModel.class);
                    users.setUserId(dataSnapshot.getKey());
                    if (!users.getUserId().equals(FirebaseAuth.getInstance().getUid())) {
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

    private void updatePermissionData() {
        //權限有更改，更新到資料庫
        PermissionModel permissionModel2 = new PermissionModel(step, heartrate, speed, calories, breathe, oxygen, sleep);
        database.getReference().child("Permission").child(currentuser).setValue(permissionModel2).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(FriendsActivity.this, "權限更新", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FriendsActivity.this, "權限更新失敗", Toast.LENGTH_SHORT).show();
            }
        });
    }
}