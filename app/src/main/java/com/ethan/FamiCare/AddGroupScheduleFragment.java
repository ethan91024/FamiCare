package com.ethan.FamiCare;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Calendar;
import java.util.TimeZone;

public class AddGroupScheduleFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public AddGroupScheduleFragment() {
        // Required empty public constructor
    }

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
//1
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

    //取得時、分時間輸入
    String event_text;
    String hour_text;
    String minute_text;

    Context context;
    private int notificationId = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_group_schedule, container, false);

        Bundle arguments = getArguments();
        date1 = arguments.getInt("id");
        event = view.findViewById(R.id.event);
        hour = view.findViewById(R.id.hour);
        minute = view.findViewById(R.id.minute);

        save = view.findViewById(R.id.save2);
        groupCalDoa = GroupCalDB.getInstance(this.getContext()).groupCalDoa();


        if (arguments != null) {
            status = arguments.getBoolean("edited", false);
        }
        if (status) {
            temp = groupCalDoa.getGroupCalById(date1);
            event.setText(temp.getEvent());
            hour.setText(temp.getHour());
            minute.setText(temp.getMinute());
        } else {
            temp = new GroupCal();
        }


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSaveNote()) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.beginTransaction().addToBackStack(null).replace(R.id.addSchedule, new GroupCalendarFragment()).commit();
                }

            }
        });


        return view;
    }

    private boolean onSaveNote() {
        event_text = event.getText().toString();
        hour_text = hour.getText().toString();
        minute_text = minute.getText().toString();

        if (!event_text.isEmpty() && !hour_text.isEmpty() && !minute_text.isEmpty()) {
            temp.setId(date1);
            temp.setEvent(event_text);
            temp.setHour(hour_text);
            temp.setMinute(minute_text);

            groupCalDoa.deleteGroupCalById(date1);
            if (status) {//更新或創建

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

    //取得目前時間
    private void currentTime(){
        calendar=Calendar.getInstance();//calendar實例化，取得預設時間、預設時區
        calendar.setTimeInMillis(System.currentTimeMillis());//設定時間
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));//設定時區時區
        currentsystemtime=System.currentTimeMillis();//獲得系統目前的時間
    }
    //設定定時
    private void setTime(Calendar calendar){
        calendar.set(Calendar.DATE,date1);
        calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(hour_text));
        calendar.set(Calendar.MINUTE,Integer.parseInt(minute_text));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        //獲得定時時間
        settime=calendar.getTimeInMillis();
        // 若定時時間(日、時、分)比目前小自動設定為下個月的時間(日、時、分)
        if(currentsystemtime>settime){
            calendar.add(Calendar.MONTH,1);
            //  重新獲得定時時間
            settime = calendar.getTimeInMillis();
        }
    }

    private void setAlarm() {
        Intent intent = new Intent(this.getContext(), alarmReceiver.class);
        //        PendingIntent.getBroadcast調用廣播
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getContext(), 0, intent, 0);
        //        獲得AlarmManager物件
        AlarmManager alarmManager = (AlarmManager) this.getContext().getSystemService(ALARM_SERVICE);
        //        設定單次提醒
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void showtime(){
        String text=(calendar.get(Calendar.MONTH)+1)+"月"+calendar.get(Calendar.DAY_OF_MONTH)+"日\n" +calendar.get(Calendar.HOUR_OF_DAY)+":" + calendar.get(Calendar.MINUTE);
        Toast.makeText(this.getContext(),text,Toast.LENGTH_LONG).show();

    }

    }



