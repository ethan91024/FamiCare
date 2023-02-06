package com.ethan.FamiCare;

import static android.content.Context.*;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddGroupScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddGroupScheduleFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddGroupScheduleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddGroupScheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddGroupScheduleFragment newInstance(String param1, String param2) {
        AddGroupScheduleFragment fragment = new AddGroupScheduleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    Button save;
    EditText event;
    EditText hour;
    EditText minute;

    private int date1;
    private GroupCal temp;
    public static final String NOTE_EXTRA_key = "note_id";
    private boolean status;

    private GroupCalDoa groupCalDoa;
    private GroupCal groupCal;


    private long currentsystemtime;//存取目前的時間
    private long settime;//存取設定的時間時間
    private Calendar calendar;

    //取得時、分三種時間輸入
    String event_text;
    String hour_text;
    String minute_text;

    Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_add_group_schedule, container, false);

        Bundle arguments = getArguments();
        date1= arguments.getInt("id");
        event=view.findViewById(R.id.event);
        hour=view.findViewById(R.id.hour);
        minute=view.findViewById(R.id.minute);

        save=view.findViewById(R.id.save2);
        groupCalDoa=GroupCalDB.getInstance(this.getContext()).groupCalDoa();

        if(arguments!=null){
            status=arguments.getBoolean("edited",false);
        }
        if(status){
            temp=groupCalDoa.getGroupCalById(date1);
            event.setText(temp.getEvent());
            hour.setText(temp.getHour());
            minute.setText(temp.getMinute());
        }else{
            temp=new GroupCal();
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onSaveNote()) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.beginTransaction().addToBackStack(null).replace(R.id.addSchedule, new GroupCalendarFragment()).commit();

                }
            }
        });
        return view;
    }

    private boolean onSaveNote(){
         event_text=event.getText().toString();
         hour_text=hour.getText().toString();
         minute_text=minute.getText().toString();

        if (!event_text.isEmpty() && !hour_text.isEmpty()&& !minute_text.isEmpty()) {
            temp.setId(date1);
            temp.setEvent(event_text);
            temp.setHour(hour_text);
            temp.setMinute(minute_text);

            if (status) {//更新或創建
                groupCalDoa.deleteGroupCal(temp);
                groupCalDoa.updateGroupCal(temp);
            } else {
                GroupCalDB.getInstance(getContext()).groupCalDoa().insertGroupCal(temp);
            }
        } else {
            Toast.makeText(getContext(), "沒新增事件", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }


}

