package com.ethan.FamiCare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class GroupCalendar extends AppCompatActivity {

    private int selected_date;


    private CalendarView calendar;
    private TextView caldate;
    private Button addtime;
    private TextView finalevent;
    private EditText addevent;
    private Button savecal;
    private String time_text;
    DatabaseReference myRef= FirebaseDatabase.getInstance().getReferenceFromUrl("https://famicare-375914-default-rtdb.firebaseio.com/");

    private CalendarDB calendarDB;

    TimePickerDialog.OnTimeSetListener timeDialog;
    Calendar calendar1 = Calendar.getInstance();//用來做time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_calendar);

        calendar=findViewById(R.id.cal1);
        caldate=findViewById(R.id.caldate);
        addevent=findViewById(R.id.addevent);
        addtime=findViewById(R.id.addtime);
        finalevent = (TextView) findViewById(R.id.time);
        savecal=findViewById(R.id.savecal);


        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                caldate.setText((month + 1) + "/" + dayOfMonth);
                selected_date = getSelected_date(year, month, dayOfMonth);

                String sd=String.valueOf(selected_date);
                myRef.child("Calendar").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            CalendarDB calendarDB=ds.getValue(CalendarDB.class);
                        if(!(calendarDB.getId().equals(sd))){
                            finalevent.setText("沒有提醒事項");
                        }else{
                                finalevent.setText(calendarDB.getId()+"\t"+calendarDB.getEvent()+"\t"+calendarDB.getTime());
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

        timeDialog=new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar1.set(Calendar.HOUR,hourOfDay);//小時
                calendar1.set(Calendar.MINUTE,minute);//分鐘

               String addevent_text=addevent.getText().toString();
                   time_text=addevent_text + "\t" + "時間：\t" + hourOfDay + ":" + minute;
                   addtime.setText(hourOfDay+":"+minute);
            }
        };

          database();

    }


    public int getSelected_date(int year, int month, int dayOfMonth) {
        String s = String.format("%4d%02d%02d", year, month + 1, dayOfMonth);
        return Integer.parseInt(s);
    }

    public void timePicker(View v){
        //建立time的dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(),
                timeDialog,
                calendar1.get(Calendar.HOUR),
                calendar1.get(Calendar.MINUTE),
                false);
        timePickerDialog.show();
    }

    private void database() {
            savecal.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              String id_date=String.valueOf(selected_date);
              String event=addevent.getText().toString();

              String time1=time_text;
              String[] time2=time1.split("\t");
              String time3=time2[2];

              CalendarDB calevent=new CalendarDB(id_date,event,time3);

              myRef.child("Calendar").child(id_date).setValue(calevent);
          }
      });

    }

}