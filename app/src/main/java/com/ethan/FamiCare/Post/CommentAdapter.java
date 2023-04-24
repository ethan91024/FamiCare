package com.ethan.FamiCare.Post;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ethan.FamiCare.R;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    private Context context;

    public CommentAdapter(List<Comment> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_diary_comment_recycler, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        //以後要連接登入的使用者
        holder.commentUser.setText("Unknown_User");
        holder.commentContent.setText(comment.getContent());
    }

    @Override
    public int getItemCount() {
        if (commentList == null) {
            return 0;
        }
        return commentList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView commentUser;
        public TextView commentContent;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentUser = itemView.findViewById(R.id.commentUser);
            commentContent = itemView.findViewById(R.id.commentContent);
        }
    }
}
