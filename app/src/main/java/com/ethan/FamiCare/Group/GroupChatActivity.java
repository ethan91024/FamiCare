package com.ethan.FamiCare.Group;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ethan.FamiCare.Firebasecords.GroupChatAdapter;
import com.ethan.FamiCare.Firebasecords.MessageModelGroup;
import com.ethan.FamiCare.MainActivity;
import com.ethan.FamiCare.R;
import com.ethan.FamiCare.databinding.ActivityChatroomBinding;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {

    ActivityChatroomBinding binding;
    ImageView photo, camera;
    FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final int TAKE_PHOTO_REQUEST = 0;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        camera = findViewById(R.id.camera);
        photo = findViewById(R.id.photo);

        database = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Grouplist");
        storageReference = FirebaseStorage.getInstance().getReference("Grouplist");
        FirebaseAuth auth = FirebaseAuth.getInstance();

        binding = ActivityChatroomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final ArrayList<MessageModelGroup> messageModelGroups = new ArrayList<>();

        final String senderId = FirebaseAuth.getInstance().getUid();
        binding.username.setText("Group Chat");
        final String receiveId = FirebaseAuth.getInstance().getUid();

        final GroupChatAdapter adapter = new GroupChatAdapter(messageModelGroups, this);
        binding.recyclerview.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.recyclerview.setLayoutManager(layoutManager);

        database.getReference().child("Group Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageModelGroups.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageModelGroup model = dataSnapshot.getValue(MessageModelGroup.class);
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
                String uid = auth.getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference().
                        child("Users").child(uid).child("username")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String username = snapshot.getValue(String.class);
                                String message = binding.message.getText().toString();
                                MessageModelGroup model = new MessageModelGroup(username, senderId, message);
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

}
