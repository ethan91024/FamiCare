package com.ethan.FamiCare.Group;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ethan.FamiCare.Firebasecords.FriendModel;
import com.ethan.FamiCare.MainActivity;
import com.ethan.FamiCare.R;
import com.ethan.FamiCare.databinding.ActivityAddNewGroupBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddNewGroup extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    Button add, cancel;
    TextView edittext;
    CircleImageView editprofileimage;
    ActivityAddNewGroupBinding binding;
    String uid;//使用者的uid
    String photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_group);
        binding = ActivityAddNewGroupBinding.inflate(getLayoutInflater());

        add = findViewById(R.id.add);
        cancel = findViewById(R.id.cancel);
        edittext = findViewById(R.id.groupname);
        editprofileimage = findViewById(R.id.profile_image);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Grouplist");
        storageReference = FirebaseStorage.getInstance().getReference("Grouplist");
        uid = auth.getCurrentUser().getUid();

        editprofileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAlertDialog();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edittext.getText().toString().isEmpty()) {
                    Toast.makeText(AddNewGroup.this, "請輸入群組名稱 ", Toast.LENGTH_SHORT).show();
                } else {
                    String groupname = edittext.getText().toString();
                    String type="group";
                    FriendModel friend = new FriendModel(groupname,photo,type);
                    database.getReference().child("Grouplist").child(uid).push().setValue(friend)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(AddNewGroup.this, "群組創建成功", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AddNewGroup.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewGroup.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // 照片相關
    Bitmap bitmap;
    private static final int TAKE_PHOTO_REQUEST = 0;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    // 上傳照片到群組頭像或離開
    private void callAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("設定群組頭像")
                .setMessage("拍張照或選擇照片?")
                // 拍照
                .setPositiveButton("現在拍！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, TAKE_PHOTO_REQUEST);
                    }
                })
                // 选择照片
                .setNegativeButton("選擇照片", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                    }
                })
                // 直接跳回DiaryFragment
                .setNeutralButton("不用", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                    }
                })
                .setCancelable(false)
                .show();
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
                         photo = uri.toString();
//                        database.getReference().child("Grouplist").child(uid).setValue(GroupPhoto);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddNewGroup.this, "群組頭像上傳失敗!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}