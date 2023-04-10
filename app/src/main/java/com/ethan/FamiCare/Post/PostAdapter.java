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

    public PostAdapter(Context context, ArrayList<Posts> posts) {
        this.context = context;
        this.posts = posts;
//        notifyDataSetChanged();//not sure
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
//        try {
//            Diary diary = posts.get(position);
//            holder.username.setText(diary.getTitle());//之後要改成使用者的名稱
//            holder.usercontent.setText(diary.getContent());
//
//            //圖片
//            if (diary.getPhotoPath() != null) {
//                File imageFile2 = new File(diary.getPhotoPath());
//                Bitmap bitmap = BitmapFactory.decodeFile(imageFile2.getAbsolutePath());
//                holder.userphoto.setImageBitmap(bitmap);
//            } else {
//                holder.userphoto.setImageDrawable(null);
//            }
//        } catch (Exception e) {
//        }

        Posts post = posts.get(position);
        System.out.println(post.getTitle() + "-------------------------------------------------");
        holder.usertitle.setText(post.getTitle());
        holder.usercontent.setText(post.getContent());
//        holder.userphoto.setBackgroundColor(Color.BLACK);
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
