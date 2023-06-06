package com.ethan.FamiCare.Calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ethan.FamiCare.R;

import java.util.ArrayList;
import java.util.HashMap;

public class calendarAdapter extends RecyclerView.Adapter<calendarAdapter.ViewHolder> {

    ArrayList<CalendarItem> data;
    Context context;

    public calendarAdapter(ArrayList<CalendarItem> data, android.content.Context context){
        this.data=data;
        this.context=context;

    }


    @NonNull
    @Override
    public calendarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cal_recyclerview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
    CalendarItem calendarItem=data.get(position);
    viewHolder.date.setText(calendarItem.getDate_id());
    viewHolder.email.setText(calendarItem.getEmail());
    viewHolder.event.setText(calendarItem.getEvent());
    viewHolder.time.setText(calendarItem.getTime());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView date,email,event,time;
        //public ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date=itemView.findViewById(R.id.item_id);
            email=itemView.findViewById(R.id.item_email);
            event=itemView.findViewById(R.id.item_event);
            time=itemView.findViewById(R.id.item_time);
            //image=itemView.findViewById(R.id.all_image);


        }
    }
}
