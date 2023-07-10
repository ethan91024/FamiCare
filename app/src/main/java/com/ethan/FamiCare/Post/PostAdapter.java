package com.ethan.FamiCare.Post;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ethan.FamiCare.Firebasecords.Users;
import com.ethan.FamiCare.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Posts> posts;

    //FireBase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private StorageReference storageReference;


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
        //發文者的頭貼
        databaseReference = database.getReference().child("Users");
        String desiredUsername = post.getUserName();
        Query query = databaseReference.orderByChild("username").equalTo(desiredUsername);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Users user = userSnapshot.getValue(Users.class);
                    if (user != null) {
                        String profilepicUrl = (String) userSnapshot.child("profilepic").getValue();
                        // 在這裡將取得的頭像設置給 CircleImageView
                        Glide.with(context).load(profilepicUrl).into(holder.userpic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


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
        public CircleImageView userpic;//頭像

        public TextView username;
        public TextView usertitle;
        public ImageView userphoto;//下方貼文照片
        public TextView usercontent;
        public Button seecomments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userpic = itemView.findViewById(R.id.UserPic);
            username = itemView.findViewById(R.id.UserName);
            usertitle = itemView.findViewById(R.id.UserTitle);
            userphoto = itemView.findViewById(R.id.UserPhoto);
            usercontent = itemView.findViewById(R.id.UserContent);
            seecomments = itemView.findViewById(R.id.See_comments);

        }
    }
}