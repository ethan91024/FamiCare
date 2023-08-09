package com.ethan.FamiCare.Firebasecords;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ethan.FamiCare.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class GroupChatAdapter extends RecyclerView.Adapter{
    ArrayList<MessageModelGroup> messageModelGroups;
    Context context;
    String recId;

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
            return new SenderViewHolder(view);
        }else
        {
            View view=LayoutInflater.from(context).inflate(R.layout.group_receiver,parent,false);
            return new RecieverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messageModelGroups.get(position).getUserId().equals(FirebaseAuth.getInstance().getUid())){
            return Sender_View_Type;
        }else {
            return Receiver_View_Type;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModelGroup messageModelGroup=messageModelGroups.get(position);
        if(holder.getClass()==SenderViewHolder.class){
            ((SenderViewHolder)holder).sendername.setText(messageModelGroup.getUsername());
            ((SenderViewHolder)holder).senderMsg.setText(messageModelGroup.getMessage());
        }else{
            ((RecieverViewHolder)holder).username.setText(messageModelGroup.getUsername());
            ((RecieverViewHolder)holder).receiverMsg.setText(messageModelGroup.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageModelGroups.size();
    }

    public class RecieverViewHolder extends RecyclerView.ViewHolder{
        TextView username,receiverMsg,receiverTime;

        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.receicername);
            receiverMsg=itemView.findViewById(R.id.receicertext);
            receiverTime=itemView.findViewById(R.id.receicertime);
        }
    }
    public class SenderViewHolder extends RecieverViewHolder{
        TextView sendername,senderMsg,senderTime;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            sendername=itemView.findViewById(R.id.sendername);
            senderMsg=itemView.findViewById(R.id.sendertext);
            senderTime=itemView.findViewById(R.id.sendertime);
        }
    }
}