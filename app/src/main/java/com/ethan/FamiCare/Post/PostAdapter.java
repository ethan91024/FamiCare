package com.ethan.FamiCare.Post;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.ethan.FamiCare.R;

import java.util.ArrayList;


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
        holder.username.setText(post.getUserName());
        holder.usertitle.setText(post.getTitle());
        holder.usercontent.setText(post.getContent());
        Glide.with(context)
                .load(post.getphotoUrl())
                .into(holder.userphoto);


        //跳轉到子view對應的留言列表
        String commentId = (post.getId() + "") + (post.getTitle().replace("/", "_"));
        holder.seecomments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 将需要在 Firebase 中搜索的 ID 字符串传递给 DiaryCommentsActivity
                Intent intent = new Intent(context, DiaryCommentActivity.class);
                intent.putExtra("commentId", commentId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public TextView usertitle;
        public ImageView userphoto;
        public TextView usercontent;
        public Button seecomments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.UserName);
            usertitle = itemView.findViewById(R.id.UserTitle);
            userphoto = itemView.findViewById(R.id.UserPhoto);
            usercontent = itemView.findViewById(R.id.UserContent);
            seecomments = itemView.findViewById(R.id.See_comments);

        }
    }
}