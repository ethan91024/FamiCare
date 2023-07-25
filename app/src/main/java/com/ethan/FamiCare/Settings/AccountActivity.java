package com.ethan.FamiCare.Settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ethan.FamiCare.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {

    private TextView username, userid, email;
    private CircleImageView profile_image;
    private TextView status_step, status_heartRate, status_speed, status_calories, status_respiratory, status_bloodOxygen, status_sleep;
    private Button turnback;

    //Firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("Users");
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String uid = auth.getCurrentUser().getUid();

    //照片相關
    private static final int TAKE_PHOTO_REQUEST = 0;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        username = findViewById(R.id.username);
        userid = findViewById(R.id.userid);
        email = findViewById(R.id.emailTextView);
        profile_image = findViewById(R.id.profile_image);

        // 點群組裡或是好友的頭像可以傳到對應的人的個人檔案，再透過個人檔案的id抓取對應的健康資料，從資料庫拿到每個狀態的當日平均，然後評估 1:待加油|2:及格|3:滿分 ，放到對應textView
        status_step = findViewById(R.id.status_step);
        status_heartRate = findViewById(R.id.status_heartRate);
        status_speed = findViewById(R.id.status_speed);
        status_calories = findViewById(R.id.status_calories);
        status_respiratory = findViewById(R.id.status_respiratory);
        status_bloodOxygen = findViewById(R.id.status_bloodOxygen);
        status_sleep = findViewById(R.id.status_sleep);
        turnback = findViewById(R.id.turnback);



        if (auth.getCurrentUser() == null) {
            username.setText("username");
            userid.setText("#userid");

        } else {
            database.getReference().child("Users").child(uid).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String username1 = snapshot.getValue(String.class);
                        // 在這裡將取得的 username 設置給 TextView
                        username.setText(username1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            database.getReference().child("Users").child(uid).child("id").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String userid1 = snapshot.getValue(String.class);
                        // 在這裡將取得的 username 設置給 TextView
                        userid.setText("#" + userid1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            database.getReference().child("Users").child(uid).child("userEmail").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String useremail = snapshot.getValue(String.class);
                        // 在這裡將取得的 email 設置給 TextView
                        email.setText(useremail);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            database.getReference().child("Users").child(uid).child("profilepic").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String profilepicUrl = snapshot.getValue(String.class);
                        // 在這裡將取得的頭像設置給 CircleImageView
                        Glide.with(AccountActivity.this).load(profilepicUrl).into(profile_image);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        //設定頭像
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call_AlertDialog();
            }
        });

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

                    // 更新對應的 TextView
                    status_step.setText(getStatusText(statusStep));
                    status_heartRate.setText(getStatusText(statusHeartRate));
                    status_speed.setText(getStatusText(statusSpeed));
                    status_calories.setText(getStatusText(statusCalories));
                    status_respiratory.setText(getStatusText(statusRespiratory));
                    status_bloodOxygen.setText(getStatusText(statusBloodOxygen));
                    status_sleep.setText(getStatusText(statusSleep));
                } else {
                    // 如果該 UserId 在 Status 資料表中不存在，可以設置默認值或顯示 "無資料"
                    status_step.setText("無資料");
                    status_heartRate.setText("無資料");
                    status_speed.setText("無資料");
                    status_calories.setText("無資料");
                    status_respiratory.setText("無資料");
                    status_bloodOxygen.setText("無資料");
                    status_sleep.setText("無資料");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 處理錯誤情況
            }
        });

        //返回按鈕
        turnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }


    //上傳照片或相簿挑選或直接離開編輯
    private void Call_AlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("更換頭像")
                .setMessage("拍張照或選擇圖片?")

                //拍照
                .setPositiveButton("現在拍！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, TAKE_PHOTO_REQUEST);

                    }
                })

                //選擇照片
                .setNegativeButton("選擇照片", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

                    }
                })
                .setNeutralButton("不用", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setCancelable(false)
                .show();
    }


    //跳轉到相機或是相簿
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                // 上傳照片到 Firebase Storage 並取得下載 URL
                selectedImageUri = data.getData();
                UploadImage(selectedImageUri);

            } else if (requestCode == TAKE_PHOTO_REQUEST && data != null) {
                // 上傳照片到 Firebase Storage 並取得下載 URL
                selectedImageUri = data.getData();
                UploadImage(selectedImageUri);
            }
        }

    }

    private void UploadImage(Uri selectedImageUri) {
        //連接firebase storage
        StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");

        fileReference.putFile(selectedImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                databaseReference.child(uid).child("profilepic").setValue(uri.toString());

                            }
                        });
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