package com.ethan.FamiCare.Firebasecords;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ethan.FamiCare.R;

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
        holder.username.setText(groupMessage.getUserEmail());
        holder.message.setText(groupMessage.getMessage());
        holder.datetime.setText(groupMessage.getDatetime());
        /*
        if(groupMessage.getUserId().equals(FirebaseAuth.getInstance().getUid())){
            holder.main.setBackgroundColor(context.getResources().getColor(R.color.dark));
        }else {
            holder.main.setBackgroundColor(context.getResources().getColor(R.color.green));
        }
        
         */
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username, message, datetime;
        private CardView main;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById((R.id.user_email));
            message = itemView.findViewById((R.id.user_message));
            datetime = itemView.findViewById((R.id.user_message_date_time));
            main=itemView.findViewById(R.id.messaagelayout);
        }
    }
}