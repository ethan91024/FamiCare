package com.ethan.FamiCare;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class GroupCalendar extends AppCompatActivity {

    private int selected_date;


    private CalendarView calendar;
    private TextView caldate;
    private Button addtime;
    private EditText addevent;
    private Button savecal;
    private String time_text;
    private Button noti;

    //Listview呈現提醒事項
    private ListView listView;
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    private SimpleAdapter adapter;
    private String[] from = {"date", "event", "time", "email"};
    private int[] to = {R.id.item_id, R.id.item_event, R.id.item_time, R.id.item_email};

    //連firebase資料庫
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://famicare-375914-default-rtdb.firebaseio.com/");
    //顯示現在使用者
    FirebaseUser user;

    private CalendarDB calendarDB;

    //用來做time
    TimePickerDialog.OnTimeSetListener timeDialog;
    Calendar calendar1 = Calendar.getInstance();

    //private  int notificationId=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_calendar);

        calendar = findViewById(R.id.cal1);
        caldate = findViewById(R.id.caldate);
        addevent = findViewById(R.id.addevent);
        addtime = findViewById(R.id.addtime);
        listView = findViewById(R.id.listview);
        savecal = findViewById(R.id.savecal);
        noti = findViewById(R.id.noti);

        //顯示現在使用者
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
            finish();
        }
        creatNotificationChannel();


        //監聽日期改變
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                caldate.setText((month + 1) + "/" + dayOfMonth);
                selected_date = getSelected_date(year, month, dayOfMonth);
                String sd = String.valueOf(selected_date);

                myRef.child("Calendar").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            CalendarDB calendarDB = ds.getValue(CalendarDB.class);
                            if (calendarDB.getId().equals(sd)) {
                                setAdapter();
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put(from[0], calendarDB.getId());
                                hashMap.put(from[1], calendarDB.getEvent());
                                hashMap.put(from[2], calendarDB.getTime());
                                hashMap.put(from[3], calendarDB.getUser());
                                arrayList.add(hashMap);
                                adapter.notifyDataSetChanged();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        //time裡面dialog時間的選擇給Calendar.xxx及時間的顯示
        timeDialog = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar1.set(Calendar.HOUR, hourOfDay);//小時
                calendar1.set(Calendar.MINUTE, minute);//分鐘

                String addevent_text = addevent.getText().toString();
                time_text = addevent_text + "\t" + "時間：\t" + hourOfDay + ":" + minute;
                addtime.setText(hourOfDay + ":" + minute);
            }
        };

        database();

        //notification
        noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addevent_text = addevent.getText().toString();
                String time4 = addtime.getText().toString();
                String date = caldate.getText().toString();//ex:2/6
                String[] date1 = date.split("/");
                int month = Integer.parseInt(date1[0]) - 1;


                //notificationId & message
                Intent intent = new Intent(GroupCalendar.this, alarmReceiver.class);

                intent.putExtra("event", addevent_text);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(GroupCalendar.this, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                //AlarmManager
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                String[] time5 = time4.split(":");//ex:14:28
                int hour = Integer.parseInt(time5[0]);
                int minute = Integer.parseInt(time5[1]);

                //create time
                Calendar starttime = Calendar.getInstance();
                starttime.set(Calendar.MONTH, month);
                starttime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date1[1]));
                starttime.set(Calendar.HOUR_OF_DAY, hour);
                starttime.set(Calendar.MINUTE, minute);
                starttime.set(Calendar.SECOND, 0);
                starttime.set(Calendar.MILLISECOND, 0);
                long alarmStartTime = starttime.getTimeInMillis();


                //Set Alarm
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmStartTime, pendingIntent);

                String text = (starttime.get(Calendar.MONTH) + 1) + "月"
                        + starttime.get(Calendar.DAY_OF_MONTH) + "日\n"
                        + starttime.get(Calendar.HOUR_OF_DAY) + ":"
                        + starttime.get(Calendar.MINUTE);
                Toast.makeText(GroupCalendar.this, text, Toast.LENGTH_SHORT).show();

            }
        });


    }


    public int getSelected_date(int year, int month, int dayOfMonth) {
        String s = String.format("%4d%02d%02d", year, month + 1, dayOfMonth);
        return Integer.parseInt(s);
    }

    public void timePicker(View v) {
        //建立time的dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(),
                timeDialog,
                calendar1.get(Calendar.HOUR),
                calendar1.get(Calendar.MINUTE),
                false);
        timePickerDialog.show();
    }

    //存進資料庫
    private void database() {
        savecal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id_date = String.valueOf(selected_date);
                String event = addevent.getText().toString();

                String time1 = time_text;
                if (event.isEmpty() || time1.isEmpty()) {
                    Toast.makeText(GroupCalendar.this, "請填寫事件和時間", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String[] time2 = time1.split("\t");
                    String time3 = time2[2];

                    String email = user.getEmail();


                    CalendarDB calevent = new CalendarDB(id_date, event, time3, email);

                    myRef.child("Calendar").push().setValue(calevent);
                    Toast.makeText(GroupCalendar.this, "儲存成功", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setAdapter() {
        adapter = new SimpleAdapter(this, arrayList, R.layout.event_item, from, to);
        listView.setAdapter(adapter);
    }

    private void creatNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For API 26 and above
            CharSequence channelName = "My Notification";
            String des = "Channel for alarm";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel("calandroid", channelName, importance);
            channel.setDescription(des);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

}