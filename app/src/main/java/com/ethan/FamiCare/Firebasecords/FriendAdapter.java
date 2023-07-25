package com.ethan.FamiCare.Firebasecords;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ethan.FamiCare.R;
import com.ethan.FamiCare.Settings.Friends_interface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.viewHolder> {

    ArrayList<FriendModel> list;
    Context context;

    public FriendAdapter(ArrayList<FriendModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.setting_friend_showcircleimage, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {


        FriendModel users = list.get(position);

        Picasso.get().load(users.getProfilepic()).placeholder(R.drawable.avatar_b).into(holder.image);
        holder.username.setText(users.getUsername());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Friends_interface.class);
                intent.putExtra("userId", users.getId());
                intent.putExtra("profilePic", users.getProfilepic());
                intent.putExtra("userName", users.getUsername());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView username;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.usernamelist);
        }
    }
}
