package com.ethan.FamiCare.Post;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ethan.FamiCare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DiaryCommentActivity extends AppCompatActivity {

    //Layout
    private EditText input_comment;
    private ImageView add_comment;
    private RecyclerView commentRecyclerView;

    //Recycler
    private List<Comment> commentList;
    private CommentAdapter commentAdapter;

    //FireBase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String uid = auth.getCurrentUser().getUid();
    private String uname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_comment);

        input_comment = findViewById(R.id.Input_comment);
        add_comment = findViewById(R.id.Add_comment);
        commentRecyclerView = findViewById(R.id.comments_recycler_view);

        //拿到username，設置為留言者的username
        database.getReference().child("Users").child(uid).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    uname = snapshot.getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //從Intent中獲取指定貼文的評論Id
        String commentId = getIntent().getStringExtra("commentId");

        //評論資料庫，接著放到CommentAdapter
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList, this);
        commentRecyclerView.setAdapter(commentAdapter);

        //設置留言的 RecyclerView
        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("Comments");
        Query query = commentRef.orderByChild("id").equalTo(commentId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Comment comment = ds.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("DiaryCommentsActivity", "Failed to get comments", error.toException());
            }
        });

        // 添加評論
        add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input_comment.getText() != null) {
                    String commentText = input_comment.getText().toString();

                    //建立 Comment 對象，id是該post的id(日期) + title，userName是使用者的userName
                    Comment comment = new Comment(commentId, uname, commentText);

                    //將 Comment 放到 firebase
                    String commentkey = commentRef.push().getKey();
                    commentRef.child(commentkey).setValue(comment);

                    //更新評論列表
                    Query query = commentRef.orderByChild("id").equalTo(commentId);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            commentList.clear();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Comment comment = ds.getValue(Comment.class);
                                commentList.add(comment);
                            }
                            commentAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
//                            Log.e("DiaryCommentsActivity", "Failed to get comments", error.toException());
                        }
                    });

                    //清空輸入框
                    input_comment.getText().clear();
                }

            }
        });
    }
}