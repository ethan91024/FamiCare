package com.ethan.FamiCare.Firebasecords;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ethan.FamiCare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GroupChatAdapter extends RecyclerView.Adapter{
    ArrayList<MessageModelGroup> messageModelGroups;
    Context context;
    String recId;
    FirebaseAuth auth;
    FirebaseDatabase database;

    int Sender_View_Type=1;
    int Receiver_View_Type=2;


    public GroupChatAdapter(ArrayList<MessageModelGroup> messageModelGroups, Context context) {
        this.messageModelGroups = messageModelGroups;
        this.context = context;
    }

    public GroupChatAdapter(ArrayList<MessageModelGroup> messageModelGroups, Context context, String recId) {
        this.messageModelGroups = messageModelGroups;
        this.context = context;
        this.recId = recId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //監測發送者訊息顏色， 若為發送者訊息框為綠色，否則灰色

        View view = LayoutInflater.from(context).inflate(R.layout.group_receiver, parent, false);
        return new RecieverViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        database=FirebaseDatabase.getInstance();
        MessageModelGroup messageModelGroup=messageModelGroups.get(position);
        if(holder.getClass()==SenderViewHolder.class){
            ((SenderViewHolder)holder).sendername.setText(database.getReference().child("Users").child(messageModelGroup.getUserId()).child("username").getKey());
            ((SenderViewHolder)holder).senderMsg.setText(messageModelGroup.getMessage());
            ((SenderViewHolder)holder).senderTime.setText(getFormattedTime(messageModelGroup.getDatetime()));
        }else{
            String uid=messageModelGroup.getUserId();
            database.getReference().child("Users")
                    .child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ((RecieverViewHolder)holder).username.setText(snapshot.child("username").getValue(String.class));
                    ((RecieverViewHolder)holder).receiverMsg.setText(messageModelGroup.getMessage());
                    ((RecieverViewHolder)holder).receiverTime.setText(getFormattedTime(messageModelGroup.getDatetime()));
                    String profilePicUrl = snapshot.child("profilepic").getValue(String.class);
                    Picasso.get()
                            .load(profilePicUrl)
                            .into(((RecieverViewHolder) holder).imageView);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return messageModelGroups.size();
    }

    public class RecieverViewHolder extends RecyclerView.ViewHolder{
        TextView username,receiverMsg,receiverTime;

        ImageView imageView;
        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.receicername);
            receiverMsg=itemView.findViewById(R.id.receicertext);
            receiverTime=itemView.findViewById(R.id.receicertime);
            imageView=itemView.findViewById(R.id.receiverAvatar);
        }
    }
    public class SenderViewHolder extends RecieverViewHolder{
        TextView sendername,senderMsg,senderTime;
        ImageView imageView;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            sendername=itemView.findViewById(R.id.sendername);
            senderMsg=itemView.findViewById(R.id.sendertext);
            senderTime=itemView.findViewById(R.id.sendertime);
            imageView=itemView.findViewById(R.id.profile_image);
        }
    }
    private String getFormattedTime(long timestamp) {
        // 使用SimpleDateFormat或其他日期时间格式化工具将时间戳转换为格式化的时间字符串
        // 这里只是一个示例，你可以根据需要进行调整
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}