package com.ethan.FamiCare.Post;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ethan.FamiCare.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private Context context;
    private List<Comment> commentList;


    public CommentAdapter(List<Comment> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_diary_comment_recycler, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.commentUser.setText(comment.getUserName());
        holder.commentContent.setText(comment.getContent());

    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (commentList != null) {
            size = commentList.size();
        }
        return size;
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
