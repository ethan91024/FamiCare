package com.ethan.FamiCare.Post;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

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

public class DiaryCommentsFragment extends Fragment {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diary_comments, container, false);
        input_comment = view.findViewById(R.id.Input_comment);
        add_comment = view.findViewById(R.id.Add_comment);
        commentRecyclerView = view.findViewById(R.id.comments_recycler_view);


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

        //從PostAdapter拿到指定貼文的評論Id
        Bundle arguments = getArguments();
        if (arguments != null) {
            String commentId = arguments.getString("commentId");

            //評論資料庫，接著放到CommentAdapter
            commentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            commentList = new ArrayList<>();
            commentAdapter = new CommentAdapter(commentList, getContext());
            commentRecyclerView.setAdapter(commentAdapter);

            // 設置留言的 RecyclerView
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
                    Log.e("PostAdapter", "Failed to get comments", error.toException());
                }
            });


            // 添加評論
            add_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (input_comment.getText() != null) {
                        String commentText = input_comment.getText().toString();

                        // 建立 Comment 對象，id是該post的id(日期) + title，userName是使用者的userName
                        Comment comment = new Comment(commentId, uname, commentText);

                        // 將 Comment 放到 firebase
                        String commentkey = commentRef.push().getKey();
                        commentRef.child(commentkey).setValue(comment);

                        // 更新評論列表
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
                                Log.e("PostAdapter", "Failed to get comments", error.toException());
                            }
                        });

                        // 清空輸入框
                        input_comment.getText().clear();
                    }

                }
            });
        }


        return view;
    }
}