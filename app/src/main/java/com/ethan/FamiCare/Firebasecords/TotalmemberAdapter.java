package com.ethan.FamiCare.Firebasecords;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ethan.FamiCare.Group.TotalMemberActivity;
import com.ethan.FamiCare.R;
import com.ethan.FamiCare.Settings.Friends_interface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
public class TotalmemberAdapter extends RecyclerView.Adapter<TotalmemberAdapter.ViewHolder>{
    ArrayList<FriendModel> list;
    Context context;
    FirebaseDatabase database;
    FirebaseAuth auth;
    String uid;

    public TotalmemberAdapter(ArrayList<FriendModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.setting_friend_showcircleimage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendModel friendModel = list.get(position);
        database=FirebaseDatabase.getInstance();
        uid=auth.getCurrentUser().getUid();
        Toast.makeText(context, friendModel.getUserId(), Toast.LENGTH_SHORT).show();
        database.getReference().child("Group").child(uid).child(friendModel.getUserId())
                .child("userId")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Picasso.get().load(snapshot.child("profilepic").getValue(String.class)).placeholder(R.drawable.avatar_b).into(holder.image);
                holder.username.setText(snapshot.child("username").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

    ImageView image;
    TextView username;
    ArrayList<FriendModel> list;
    Context context;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        image=itemView.findViewById(R.id.profile_image);
        username = itemView.findViewById(R.id.usernamelist);
    }
}
}