package com.ethan.FamiCare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {
    private List<Diary> diaries;

    public DiaryAdapter(List<Diary> diaries) {
        this.diaries = diaries;
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
        Diary diary = diaries.get(position);
        holder.username.setText(diary.getTitle());//之後要改成使用者的名稱
        holder.usercontent.setText(diary.getContent());

        //圖片
        if (diary.getPhotoPath() != null) {
            File imageFile2 = new File(diary.getPhotoPath());
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile2.getAbsolutePath());
            holder.userphoto.setImageBitmap(bitmap);
        } else {
            holder.userphoto.setImageDrawable(null);
        }

    }

    @Override
    public int getItemCount() {
        return diaries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public ImageView userphoto;
        public TextView usercontent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.UserName);
            userphoto = itemView.findViewById(R.id.UserPhoto);
            usercontent = itemView.findViewById(R.id.UserContent);
        }
    }
}
