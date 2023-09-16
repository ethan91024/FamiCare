package com.ethan.FamiCare.Firebasecords;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

public class ChatAdapter extends RecyclerView.Adapter{
    ArrayList<MessageModel> messageModels;
    Context context;
    String recId;
    FirebaseDatabase database;

    int Sender_View_Type=1;
    int Receiver_View_Type=2;

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context, String recId) {
        this.messageModels = messageModels;
        this.context = context;
        this.recId = recId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==Sender_View_Type)
        {
            View view=LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
            return new SenderViewHolder(view);
        }else
        {
            View view=LayoutInflater.from(context).inflate(R.layout.sample_reciever,parent,false);
            return new RecieverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messageModels.get(position).getUserId().equals(FirebaseAuth.getInstance().getUid())){
            return Sender_View_Type;
        }else {
            return Receiver_View_Type;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel=messageModels.get(position);
        database= FirebaseDatabase.getInstance();
        if(holder.getClass()== ChatAdapter.SenderViewHolder.class){
            String uid=messageModel.getUserId();
            database.getReference().child("Users")
                    .child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ((ChatAdapter.SenderViewHolder)holder).sendername.setText(snapshot.child("username").getValue(String.class));
                            if(messageModel.getMessage().contains("firebasestorage")){
                                Picasso.get().load(messageModel.getMessage())
                                        .into(((ChatAdapter.SenderViewHolder) holder).photo);
                                ((ChatAdapter.SenderViewHolder) holder).senderMsg.setText(" ");
                                ((ChatAdapter.SenderViewHolder)holder).senderTime.setText(null);
                            }else {
                                Picasso.get().load((Uri) null)
                                    .into(((ChatAdapter.SenderViewHolder) holder).photo);
                                ((ChatAdapter.SenderViewHolder) holder).senderMsg.setText(messageModel.getMessage());
                                ((ChatAdapter.SenderViewHolder)holder).senderTime.setText(getFormattedTime(messageModel.getDatetime()));
                            }

                            String profilePicUrl = snapshot.child("profilepic").getValue(String.class);
                            Picasso.get()
                                    .load(profilePicUrl).placeholder(R.drawable.avatar_b)
                                    .into(((ChatAdapter.SenderViewHolder) holder).imageView);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }else{
            String uid=messageModel.getUserId();
            database.getReference().child("Users")
                    .child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ((ChatAdapter.RecieverViewHolder)holder).username.setText(snapshot
                                    .child("username")
                                    .getValue(String.class));
                            if(messageModel.getMessage().contains("firebasestorage")){
                                Picasso.get().load(messageModel.getMessage())
                                        .into(((ChatAdapter.RecieverViewHolder) holder).photo);
                                ((RecieverViewHolder) holder).receiverMsg.setText(" ");
                                ((RecieverViewHolder)holder).receiverTime.setText(null);
                            }else {
                                Picasso.get().load((Uri) null)
                                        .into(((RecieverViewHolder) holder).photo);
                                ((RecieverViewHolder) holder).receiverMsg.setText(messageModel.getMessage());
                                ((RecieverViewHolder)holder).receiverTime.setText(getFormattedTime(messageModel.getDatetime()));
                            }
                            String profilePicUrl = snapshot.child("profilepic").getValue(String.class);
                            Picasso.get()
                                    .load(profilePicUrl)
                                    .placeholder(R.drawable.avatar_b)
                                    .into(((ChatAdapter.RecieverViewHolder) holder).imageView);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public class RecieverViewHolder extends RecyclerView.ViewHolder{
        TextView username,receiverMsg,receiverTime;

        ImageView imageView,photo;
        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.receicernames);
            receiverMsg=itemView.findViewById(R.id.receicertext);
            receiverTime=itemView.findViewById(R.id.receicertime);
            imageView=itemView.findViewById(R.id.receiverAvatar);
            photo=itemView.findViewById(R.id.recieverimageview);
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
            photo=itemView.findViewById(R.id.senderimageview);
        }
    }
    private String getFormattedTime(long timestamp) {
        // 使用SimpleDateFormat或其他日期时间格式化工具将时间戳转换为格式化的时间字符串
        // 这里只是一个示例，你可以根据需要进行调整
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

}