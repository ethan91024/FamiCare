package com.ethan.FamiCare.Firebasecords;

import android.content.Context;
import android.net.Uri;
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
        if(viewType==Sender_View_Type)
        {
            View view=LayoutInflater.from(context).inflate(R.layout.group_sender,parent,false);
            return new GroupChatAdapter.SenderViewHolder(view);
        }else
        {
            View view=LayoutInflater.from(context).inflate(R.layout.group_receiver,parent,false);
            return new GroupChatAdapter.RecieverViewHolder(view);
        }
    }
    public int getItemViewType(int position) {
        if(messageModelGroups.get(position).getUserId().equals(FirebaseAuth.getInstance().getUid())){
            return Sender_View_Type;
        }else {
            return Receiver_View_Type;
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        database=FirebaseDatabase.getInstance();
        MessageModelGroup messageModelGroup=messageModelGroups.get(position);
        if(holder.getClass()==GroupChatAdapter.SenderViewHolder.class){
            String uid=messageModelGroup.getUserId();
            database.getReference().child("Users")
                    .child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ((SenderViewHolder)holder).sendername.setText(snapshot.child("username").getValue(String.class));
                            if(messageModelGroup.getMessage().contains("firebasestorage")){
                                Picasso.get().load(messageModelGroup.getMessage())
                                        .into(((GroupChatAdapter.SenderViewHolder) holder).photo);
                                ((SenderViewHolder) holder).senderMsg.setText(" ");
                                ((SenderViewHolder)holder).senderTime.setText(null);
                            }else {
                                Picasso.get().load((Uri) null)
                                        .into(((SenderViewHolder) holder).photo);
                                ((SenderViewHolder) holder).senderMsg.setText(messageModelGroup.getMessage());
                                ((SenderViewHolder)holder).senderTime.setText(getFormattedTime(messageModelGroup.getDatetime()));
                            }
                            String profilePicUrl = snapshot.child("profilepic").getValue(String.class);
                            Picasso.get()
                                    .load(profilePicUrl).placeholder(R.drawable.avatar_b)
                                    .into(((GroupChatAdapter.SenderViewHolder) holder).imageView);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }else{
            String uid=messageModelGroup.getUserId();
            database.getReference().child("Users")
                    .child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ((RecieverViewHolder)holder).username.setText(snapshot.child("username").getValue(String.class));
                    if(messageModelGroup.getMessage().contains("firebasestorage")){
                        Picasso.get().load(messageModelGroup.getMessage())
                                .into(((GroupChatAdapter.RecieverViewHolder) holder).photo);
                        ((GroupChatAdapter.RecieverViewHolder) holder).receiverMsg.setText(" ");
                        ((RecieverViewHolder)holder).receiverTime.setText(null);
                    }else {
                        Picasso.get().load((Uri) null)
                                .into(((GroupChatAdapter.RecieverViewHolder) holder).photo);
                        ((GroupChatAdapter.RecieverViewHolder) holder).receiverMsg.setText(messageModelGroup.getMessage());
                        ((GroupChatAdapter.RecieverViewHolder)holder).receiverTime.setText(getFormattedTime(messageModelGroup.getDatetime()));
                    }
                    String profilePicUrl = snapshot.child("profilepic").getValue(String.class);
                    Picasso.get()
                            .load(profilePicUrl).placeholder(R.drawable.avatar_b)
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

        ImageView imageView,photo;
        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.receicername);
            receiverMsg=itemView.findViewById(R.id.receicertext);
            receiverTime=itemView.findViewById(R.id.receicertime);
            imageView=itemView.findViewById(R.id.receiverAvatar);
            photo=itemView.findViewById(R.id.recieverimageviewg);
        }
    }
    public class SenderViewHolder extends RecieverViewHolder{
        TextView sendername,senderMsg,senderTime;
        ImageView imageView,photo;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            sendername=itemView.findViewById(R.id.sendername);
            senderMsg=itemView.findViewById(R.id.sendertext);
            senderTime=itemView.findViewById(R.id.sendertime);
            imageView=itemView.findViewById(R.id.receiverAvatar);
            photo=itemView.findViewById(R.id.senderimageviewg);
        }
    }
    private String getFormattedTime(long timestamp) {
        // 使用SimpleDateFormat或其他日期时间格式化工具将时间戳转换为格式化的时间字符串
        // 这里只是一个示例，你可以根据需要进行调整
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}