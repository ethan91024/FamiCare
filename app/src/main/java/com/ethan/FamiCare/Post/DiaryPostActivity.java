package com.ethan.FamiCare.Post;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ethan.FamiCare.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Toast;

import com.ethan.FamiCare.Diary.Diary;
import com.ethan.FamiCare.Diary.DiaryDB;
import com.ethan.FamiCare.Diary.DiaryDoa;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DiaryPostActivity extends AppCompatActivity {

    //Layout
    ArrayList<Posts> posts;
    private PostAdapter postAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton add_post;

    //Local DataBase
    private List<Diary> diaries;
    private DiaryDoa diaryDoa;

    //FireBase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String uid = auth.getCurrentUser().getUid();
    private String uname;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_post);
        // Initialize database reference
        diaryDoa = DiaryDB.getInstance(this).diaryDoa();
        storageReference = FirebaseStorage.getInstance().getReference("Posts");
        databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        database.getReference().child("Users").child(uid).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //拿到username，給貼文的Username使用
                    uname = snapshot.getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Initialize the RecyclerView and Adapter
        recyclerView = findViewById(R.id.PostRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(this, posts);
        recyclerView.setAdapter(postAdapter);

        // Attach database listener
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Posts post = snapshot.getValue(Posts.class);
                    posts.add(post);
                }
                postAdapter.notifyDataSetChanged();

                // 如果有資料就滾動到最上方
                if (posts.size() > 0) {
                    int latestPosition = postAdapter.getItemCount() - 1;
                    recyclerView.scrollToPosition(latestPosition);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DiaryPostActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        add_post = findViewById(R.id.Add_post);
        //根據今天日期新增全部貼文，先拿日期，去本地資料庫找資料，照片上傳到storage，其它到realtime
        add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DiaryPostActivity.this);
                builder.setMessage("確定上傳嗎？")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //使用者按下確定，繼續執行點擊事件
                                Calendar calendar = Calendar.getInstance();
                                int year = calendar.get(Calendar.YEAR);
                                int month = calendar.get(Calendar.MONTH);
                                int day = calendar.get(Calendar.DAY_OF_MONTH);
                                int today = getSelected_date(year, month, day);//今天日期的Id

                                if (diaryDoa.getDiariesById(today) != null) {
                                    diaries = diaryDoa.getDiariesById(today);
                                    uploadPost(diaries);
                                } else {
                                    Toast.makeText(DiaryPostActivity.this, "no diary found", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //使用者按下取消，關閉視窗
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    //實現 uploadPhoto 方法，使用 Firebase Storage 將照片上傳到雲端並取得下載 URL，最後將整個Post傳到firebase
    private void uploadPost(List<Diary> diaries) {
        int cnt = 0;

        for (Diary diary : diaries) {
            if (diary.getIsSaved() != true) {

                String photoPath = diary.getPhotoPath();
                Uri uri = Uri.fromFile(new File(photoPath));

                //連接firebase storage
                StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");

                fileReference.putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Posts post = new Posts(diary.getId(), uname, diary.getTitle(), diary.getContent(), uri.toString());
                                        String postId = databaseReference.push().getKey();
                                        databaseReference.child(postId).setValue(post);
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DiaryPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                diary.setIsSaved(true);
                diaryDoa.updateDiary(diary);
            } else {
                cnt++;
            }
        }

        //如果沒有新的diary被上傳
        if (cnt == diaries.size()) {
            Toast.makeText(DiaryPostActivity.this, "all diaries had been uploaded", Toast.LENGTH_SHORT).show();
        }
    }

    public int getSelected_date(int year, int month, int dayOfMonth) {
        String s = String.format("%4d%02d%02d", year, month + 1, dayOfMonth);
        return Integer.parseInt(s);
    }
}