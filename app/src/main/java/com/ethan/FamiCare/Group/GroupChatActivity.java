
package com.ethan.FamiCare.Group;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ethan.FamiCare.Firebasecords.ChatAdapter;
import com.ethan.FamiCare.Firebasecords.FriendModel;
import com.ethan.FamiCare.Firebasecords.GroupChatAdapter;
import com.ethan.FamiCare.Firebasecords.MessageModel;
import com.ethan.FamiCare.Firebasecords.MessageModelGroup;
import com.ethan.FamiCare.Firebasecords.Users;
import com.ethan.FamiCare.MainActivity;
import com.ethan.FamiCare.R;
import com.ethan.FamiCare.databinding.ActivityGroupChatBinding;
import com.ethan.FamiCare.databinding.FragmentGroupChatroomBinding;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {
    GroupChatAdapter adapter;
    RecyclerView recyclerView;//1
    final ArrayList<MessageModelGroup> list = new ArrayList<>();
    ActivityGroupChatBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    ImageView photo, camera, addmember,member;
    private static final int TAKE_PHOTO_REQUEST = 0;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    RelativeLayout containerLayout;
    String uid, fuidtotal = "";
    ArrayList<String> getalluser;
    String[] fuidlist;
    long cnt=0;

    public GroupChatActivity() {
        // Required empty public constructor
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        database = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Grouplist");
        storageReference = FirebaseStorage.getInstance().getReference("Grouplist");
        auth = FirebaseAuth.getInstance();


        setContentView(binding.getRoot());

        final String senderId = auth.getUid();
        String recieveId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");
        final String groupuid = getIntent().getStringExtra("groupuid");
        uid = auth.getCurrentUser().getUid();

        containerLayout = findViewById(R.id.groupchatroom);
        camera = findViewById(R.id.camera);
        photo = findViewById(R.id.photo);
        addmember = findViewById(R.id.addmember);
        member=findViewById(R.id.member);
        fuidtotal = uid;


        binding.username.setText(userName);

        getAlluser();

        Picasso.get().load(profilePic).placeholder(R.drawable.avatar_b).into(binding.profileImage);
        binding.backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final ArrayList<MessageModelGroup> messageModels = new ArrayList<>();
        final GroupChatAdapter chatAdapter = new GroupChatAdapter(messageModels, this, recieveId);
        binding.recyclerview.setAdapter(chatAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.recyclerview.setLayoutManager(layoutManager);

        final String chatroom = groupuid;

        database.getReference().child("Group chats").child(chatroom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageModels.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    MessageModelGroup model = snapshot1.getValue(MessageModelGroup.class);
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
                String message = binding.message.getText().toString();
                final MessageModelGroup model = new MessageModelGroup(senderId, message);
                model.setDatetime(new Date().getTime());
                binding.message.setText("");

                database.getReference().child("Group chats").child(chatroom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, TAKE_PHOTO_REQUEST);
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.getReference().child("Grouplist").child(uid).child(userName)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                       Intent intent ;
                                       intent = new Intent(GroupChatActivity.this, TotalMemberActivity.class);
                                       intent.putExtra("groupuid",snapshot.child("groupuid").getValue(String.class));
                                       startActivity(intent);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

            }
        });
        addmember.setOnClickListener(new View.OnClickListener() {//加好友到群組
            @Override
            public void onClick(View v) {
                String[] users = getalluser.toArray(new String[getalluser.size()]);
                ArrayList<String> getallfuid = new ArrayList<>();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(GroupChatActivity.this);
                alertDialog.setTitle("選擇要加進群組的好友");
                boolean[] checkedItems = new boolean[users.length];
                alertDialog.setMultiChoiceItems(users, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // 在点击项目时更新用户的选择
                        checkedItems[which] = isChecked;
                    }
                });

                // 添加确定按钮
                alertDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 在用户点击确定按钮后，处理用户的选择
                        // 遍历 checkedItems 数组，查找选中的用户，并执行相应的操作
                        for (int l = 0; l < users.length; l++) {
                            if (checkedItems[l]) {
                                database.getReference().child("Friend").child(uid).child(users[l])
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                String fuid = snapshot.child("fuid").getValue(String.class);
                                                String name = snapshot.child("username").getValue(String.class);
                                                getallfuid.add(fuid);
                                                String type = "group";
                                                fuidtotal += "," + fuid;
                                                FriendModel friendModel = new FriendModel(userName, profilePic, type, groupuid);
                                                database.getReference().child("Grouplist").child(fuid).child(userName).setValue(friendModel)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                                // 節點寫入成功
                                                                Toast.makeText(GroupChatActivity.this, "已將" + name + "加入群組", Toast.LENGTH_SHORT).show();

                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                // 節點寫入失敗
                                                                Toast.makeText(GroupChatActivity.this, "加入失敗" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        }
                            database.getReference().child("Group").child(uid).child(groupuid).child("Alluseruid")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                           cnt=snapshot.getChildrenCount();
                                            for (int i = 0; i < users.length; i++) {
                                                if (checkedItems[i]) {
                                                    // 在这里执行添加成员到聊天组的操作
                                                    database.getReference().child("Friend").child(uid).child(users[i])
                                                            .addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    String fuid = snapshot.child("fuid").getValue(String.class);
                                                                    FriendModel friendModel3 = new FriendModel(fuid);
                                                                    database.getReference().child("Group").child(uid).child(groupuid)
                                                                            .child("Alluseruid").child("useruid" + (++cnt)).setValue(friendModel3)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {

                                                                                }
                                                                            });
                                                                }
                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                    Toast.makeText(GroupChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });

                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(GroupChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }

                });
                // 添加取消按钮
                alertDialog.setNegativeButton("取消", null);

                alertDialog.show();
            }
        });
    }


    // 跳转到相机或是相册
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            savePhoto(selectedImageUri);

        } else if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photoBitmap = (Bitmap) extras.get("data");
                selectedImageUri = savePhotoToFile(photoBitmap);
                savePhoto(selectedImageUri);
            }
        }
    }

    //把照片暫存在本地端，為了轉成Uri
    private Uri savePhotoToFile(Bitmap bitmap) {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = null;
        try {
            photoFile = File.createTempFile(
                    "temp_photo",
                    ".jpg",
                    storageDir
            );
            FileOutputStream outputStream = new FileOutputStream(photoFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.fromFile(photoFile);
    }

    // 照片存到資料庫
    public void savePhoto(Uri uri) {
        StorageReference storageRef = storageReference.child(System.currentTimeMillis() + ".jpg");

        UploadTask uploadTask = storageRef.putFile(uri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // 照片上传成功
                // 获取照片的下载URL
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String GroupPhoto = uri.toString();
//                        database.getReference().child("Grouplist").child(uid).setValue(GroupPhoto);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupChatActivity.this, "群組頭像上傳失敗!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAlluser() {
        getalluser = new ArrayList<>();

        database.getReference().child("Friend").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Users user1 = snapshot1.getValue(Users.class);
                    getalluser.add(user1.getUsername());
                }
                System.out.println("getalluser->" + getalluser.toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

