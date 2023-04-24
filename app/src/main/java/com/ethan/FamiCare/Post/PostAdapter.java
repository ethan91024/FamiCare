package com.ethan.FamiCare.Post;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ethan.FamiCare.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Posts> posts;


    public PostAdapter(Context context, ArrayList<Posts> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_diary_post_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Posts post = posts.get(position);
        holder.usertitle.setText(post.getTitle());
        holder.usercontent.setText(post.getContent());
        Glide.with(context)
                .load(post.getphotoUrl())
                .into(holder.userphoto);


        //評論資料庫，接著放到CommentAdapter
        List<Comment> commentList = new ArrayList<>();
        String commentId = (post.getId() + "") + (post.getTitle().replace("/", "_"));
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PostAdapter", "Failed to get comments", error.toException());
            }
        });


        // 設置留言的 RecyclerView
        holder.commentRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        CommentAdapter commentAdapter = new CommentAdapter(commentList, context);
        holder.commentRecyclerView.setAdapter(commentAdapter);


        // 添加評論
        holder.addcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentText = holder.inputcomment.getText().toString();
                // 建立 Comment 对象，id是該post的id(日期) + title，userId之後會是使用者的id
                Comment comment = new Comment(commentId, "Unknown", commentText);
                // 将 Comment 放到 firebase
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("PostAdapter", "Failed to get comments", error.toException());
                    }
                });
                holder.commentRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                CommentAdapter commentAdapter = new CommentAdapter(commentList, context);
                holder.commentRecyclerView.setAdapter(commentAdapter);


                // 清空輸入框
                holder.inputcomment.getText().clear();
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView usertitle;
        public ImageView userphoto;
        public TextView usercontent;
        public EditText inputcomment;
        public ImageView addcomment;
        public RecyclerView commentRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usertitle = itemView.findViewById(R.id.UserTitle);
            userphoto = itemView.findViewById(R.id.UserPhoto);
            usercontent = itemView.findViewById(R.id.UserContent);
            inputcomment = itemView.findViewById(R.id.Input_comment);
            addcomment = itemView.findViewById(R.id.Add_comment);
            commentRecyclerView = itemView.findViewById(R.id.comments_recycler_view);
        }
    }
}