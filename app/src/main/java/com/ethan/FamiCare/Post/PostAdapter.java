package com.ethan.FamiCare.Post;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ethan.FamiCare.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    Context context;
    ArrayList<Posts> posts;
    RecyclerView recyclerView;

    public PostAdapter(Context context, ArrayList<Posts> posts, RecyclerView recyclerView) {
        this.context = context;
        this.posts = posts;
        this.recyclerView = recyclerView;
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
        System.out.println(post.getTitle() + "-------------------------------------------------");
        holder.usertitle.setText(post.getTitle());
        holder.usercontent.setText(post.getContent());
        Glide.with(context)
                .load(post.getphotoUrl())
                .into(holder.userphoto);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView usertitle;
        public ImageView userphoto;
        public TextView usercontent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usertitle = itemView.findViewById(R.id.UserTitle);
            userphoto = itemView.findViewById(R.id.UserPhoto);
            usercontent = itemView.findViewById(R.id.UserContent);
        }
    }
}