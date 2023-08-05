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
import androidx.recyclerview.widget.RecyclerView;

import com.ethan.FamiCare.R;
import com.ethan.FamiCare.Settings.Friends_interface;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.viewHolder> {

    ArrayList<FriendModel> list;
    Context context;

    Boolean friendpermission = false;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth=FirebaseAuth.getInstance();
    String friendidnow =auth.getCurrentUser().getUid();
    String myname="";


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
                String fuid=users.getFuid();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                database.getReference().child("Friend").child(fuid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds:snapshot.getChildren()){
                            FriendModel friendModel=ds.getValue(FriendModel.class);
                           if(myname.equals(friendModel.getUsername())){
                                if (friendModel.getPermission()==true){
                                    Intent intent = new Intent(context, Friends_interface.class);
                                    intent.putExtra("userId", users.getId());
                                    intent.putExtra("profilePic", users.getProfilepic());
                                    intent.putExtra("userName", users.getUsername());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }else{
                                    Toast.makeText(context,"好友資訊未公開",Toast.LENGTH_LONG).show();
                                }
                           }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("data","data not found");
                    }
                });

            }
        });

        holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    friendpermission =true;
                }else{
                    friendpermission=false;
                }
                String friendid= users.getId();
                String friendname=users.getUsername();
                String profileimage=users.getProfilepic();
                String token=users.getToken();
                String type=users.getType();
                String fuid=users.getFuid();
                Boolean permissionchange=friendpermission;
                updatepermission(profileimage,friendname,friendid,token,type,fuid,permissionchange);
            }
        });
            holder.aSwitch.setChecked(users.getPermission());

    }




    @Override
    public int getItemCount() {
        return list.size();
    }
    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView username;
        Switch aSwitch;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.usernamelist);
            aSwitch=itemView.findViewById(R.id.switch9);
            findmyname(auth.getCurrentUser().getUid(), new UserNameCallback() {
                @Override
                public void onUserNameFounf(String name) {
                    if(!name.isEmpty()){
                        myname=name;
                        System.out.println(name);
                    }else {
                        Log.d("UserName", "Username not found");
                    }
                }
            });
        }
        }


    private void updatepermission(String profileImage, String friendname,String friendid,String token,String type,String fuid, Boolean permissionchange) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FriendModel friend = new FriendModel( profileImage,friendname, friendid,token,type,fuid,permissionchange);
        database.getReference().child("Friend").child(friendidnow).child(friendname).setValue(friend).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                System.out.println("成功更新");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("更新失敗");
            }
        });
    }

    private void findmyname(String uid,UserNameCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = "";
                if(snapshot.exists()){
                    Users user=snapshot.getValue(Users.class);
                    if(user!=null){
                        name=user.getUsername();
                    }
                }
                callback.onUserNameFounf(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}