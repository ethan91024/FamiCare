package com.ethan.FamiCare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<GroupMessage> list;

    public RecyclerViewAdapter(Context context, ArrayList<GroupMessage> list) {
        this.context = context;
        this.list = list;
    }
    //1
    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_group_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        GroupMessage groupMessage=list.get(position);
        holder.username.setText(list.get(position).getUserEmail());
        holder.message.setText(list.get(position).getMessage());
        holder.datetime.setText(list.get(position).getDatetime());
        if(groupMessage.getUserId().equals(FirebaseAuth.getInstance().getUid())){

        }else {
            holder.main.setBackgroundColor(context.getResources().getColor(R.color.green));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username, message, datetime;
        private LinearLayout main;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById((R.id.user_email));
            message = itemView.findViewById((R.id.user_message));
            datetime = itemView.findViewById((R.id.user_message_date_time));
            main=itemView.findViewById(R.id.messaagelayout);
        }
    }
}