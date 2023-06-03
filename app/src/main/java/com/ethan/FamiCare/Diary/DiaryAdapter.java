package com.ethan.FamiCare.Diary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ethan.FamiCare.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {
    Context context;
    List<Diary> diaries;
    RecyclerView recyclerView;
    private ImageView diary_image;

    public DiaryAdapter(Context context, List<Diary> diaries, RecyclerView recyclerView) {
        this.context = context;
        this.diaries = diaries;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public DiaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_diary_diary_recycler, parent, false);
        return new DiaryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryAdapter.ViewHolder holder, int position) {
        Diary diary = diaries.get(position);
        holder.diary_title.setText(diary.getTitle());

        //設定日記照片
        if (diary.getPhotoPath() != null) {
            File imageFile = new File(diary.getPhotoPath());

            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                holder.diary_image.setImageBitmap(bitmap);
            }

        } else {
            holder.diary_image.setImageDrawable(null);
        }
    }

    @Override
    public int getItemCount() {
        return diaries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView diary_title;
        public ImageView diary_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            diary_title = itemView.findViewById(R.id.diary_title);
            diary_image = itemView.findViewById(R.id.diary_image);

        }
    }
}