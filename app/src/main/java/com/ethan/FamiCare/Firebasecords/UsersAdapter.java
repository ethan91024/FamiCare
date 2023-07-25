package com.ethan.FamiCare.Firebasecords;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ethan.FamiCare.Group.GroupChatActivity;
import com.ethan.FamiCare.Group.GroupChatroom;
import com.ethan.FamiCare.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.viewHolder> {

    ArrayList<Users> list;
    Context context;

    public UsersAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_group_showcircleimage, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {


        Users users = list.get(position);
        String groupname,uid;
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        FirebaseAuth auth=FirebaseAuth.getInstance();

        uid=auth.getCurrentUser().getUid();

        Picasso.get().load(users.getProfilepic()).placeholder(R.drawable.avatar_b).into(holder.image);
        holder.username.setText(users.getUsername());

        //最後一則訊息
        database.getReference().child("chats").child(FirebaseAuth.getInstance().getUid() + users.getUserId())
                .orderByChild("timestamp").limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                holder.lastmessage.setText(snapshot1.child("message").getValue().toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.getReference().child("Grouplist").child(uid).child(holder.username.getText().toString())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String type = snapshot.child("type").getValue(String.class);
                                    Intent intent;
                                    if (type == (null)) {
                                        intent = new Intent(context, GroupChatActivity.class);
                                        intent.putExtra("userId", users.getUserId());
                                        intent.putExtra("profilePic", users.getProfilepic());
                                        intent.putExtra("userName", users.getUsername());
                                        Toast.makeText(context, uid + holder.username.getText().toString() + type + "", Toast.LENGTH_SHORT).show();
                                        context.startActivity(intent);
                                    } else if (type.equals("friend")) {
                                        intent = new Intent(context, GroupChatroom.class);
                                        intent.putExtra("userId", users.getUserId());
                                        intent.putExtra("profilePic", users.getProfilepic());
                                        intent.putExtra("userName", users.getUsername());
                                        context.startActivity(intent);
                                    } else if (type.equals("group")) {
                                        intent = new Intent(context, GroupChatActivity.class);
                                        intent.putExtra("userId", users.getUserId());
                                        intent.putExtra("profilePic", users.getProfilepic());
                                        intent.putExtra("userName", users.getUsername());
                                        intent.putExtra("groupuid",users.getFuid());
                                        context.startActivity(intent);
                                    }
                                }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // 取消监听时的处理逻辑
                            }
                        });

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alertDialog =
                        new AlertDialog.Builder(context);
                alertDialog.setMessage("是否要刪除群組");
                alertDialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nodeName = holder.username.getText().toString();
                        database.getReference().child(uid).child(nodeName).removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        Toast.makeText(context, "資料已成功刪除", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });

                alertDialog.setNeutralButton("否",(dialog, which) -> {
                });
                alertDialog.setCancelable(false);
                alertDialog.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView username, lastmessage;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.usernamelist);
            lastmessage = itemView.findViewById(R.id.lastmessage);
        }
    }
}
