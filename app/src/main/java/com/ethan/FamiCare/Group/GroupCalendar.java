package com.ethan.FamiCare.Group;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.applandeo.materialcalendarview.CalendarView;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.applandeo.materialcalendarview.utils.SelectedDay;
import com.ethan.FamiCare.Calendar.CalendarItem;
import com.ethan.FamiCare.Calendar.calendarAdapter;
import com.ethan.FamiCare.CalendarDB;
import com.ethan.FamiCare.Firebasecords.Users;
import com.ethan.FamiCare.R;
import com.ethan.FamiCare.SharedPreUtils;
import com.ethan.FamiCare.alarmReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class GroupCalendar extends AppCompatActivity {

    private int selected_date;


    private CalendarView calendar;
    private TextView caldate;
    private Button addtime;
    private EditText addevent;
    Button ok;
    Button cancel;
    private Button savecal;
    private String time_text;
    private Button noti;
    private TextView cal_fold;

    //Listview呈現提醒事項
    private ListView listView;
    private RecyclerView recyclerView;
    private calendarAdapter rvAdapter;
    private ArrayList<CalendarItem> arrayList2;
    private ArrayList<HashMap<String, String>> arrayList;
    private SimpleAdapter adapter;
    private String[] from = {"date", "event", "time", "email","Image"};//Image暫放
    private int[] to = {R.id.item_id, R.id.item_event, R.id.item_time, R.id.item_email,R.id.all_image};

    //連firebase資料庫
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://famicare-375914-default-rtdb.firebaseio.com/");
    //顯示現在使用者
    FirebaseUser user;
    String token;


    //用來做time
    TimePickerDialog.OnTimeSetListener timeDialog;
    Calendar calendar1 = Calendar.getInstance();

    //notification
    private static final String channelId = "channeId";
    private static final String channelName = "channelName";

    //接收點擊後的通知內容
    String titlevalue;
    String bodyvalue;

    ArrayList<String> getalluser;
    ArrayList<String> getalltoken;

    //提醒有沒有選all
    Boolean ischooseall=false;


    List<EventDay> events = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_calendar);

        calendar = findViewById(R.id.cal1);//月曆
        caldate=findViewById(R.id.cal);//顯示日期
        savecal=findViewById(R.id.savecal);
        recyclerView=findViewById(R.id.recycler_view);
        cal_fold=findViewById(R.id.cal_fold);


        //顯示現在使用者
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
            finish();
        }


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        token = task.getResult();
                        System.out.println("Token=" + token);
                       /* FCMaddgroup.addgroup(
                                GroupCalendar.this,
                                "add",
                                token
                        );


                        */
                    }
                });

        //增加選擇日期底圖裝飾
        Drawable drawableR = getResources().getDrawable(R.drawable.schedulepoint_img);
        drawableR.setBounds(90, 8, 190, 150);
        Drawable drawableL = getResources().getDrawable(R.drawable.schedulepoint_img);
        drawableL.setBounds(-90, 8, 10, 150);
        cal_fold.setCompoundDrawables(drawableR, null, drawableL, null);

        //isEventDayaddIcon();
        //calendar.setEvents(events);

        //得到所有通知對象
        getAlluser();

        //RecyclerView
        arrayList2=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(GroupCalendar.this));
        rvAdapter=new calendarAdapter(arrayList2,GroupCalendar.this);
        recyclerView.setAdapter(rvAdapter);

        //監聽日期改變
        calendar.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(@NonNull EventDay eventDay) {
                Calendar selectDate=eventDay.getCalendar();
                //格式化所選日期
                String fomatteddate=getSelected_date(selectDate);
                caldate.setText(fomatteddate);
                selected_date=changeDateToNum(selectDate);//換成數字

                arrayList2.clear();
                myRef.child("Calendar").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean isDataExists = false;
                        arrayList2.clear();
                        for (DataSnapshot ds:snapshot.getChildren()){
                            CalendarDB calendarDB=ds.getValue(CalendarDB.class);
                            CalendarItem calendarItem;
                            if(calendarDB.getId().equals(String.valueOf(selected_date))){
                                if(calendarDB.getNotiischoose()==false ){
                                    calendarItem=new CalendarItem(calendarDB.getUser(),calendarDB.getEvent(),calendarDB.getTime());
                                }else {
                                    calendarItem = new CalendarItem(calendarDB.getUser(), calendarDB.getEvent(), calendarDB.getTime(), R.drawable.baseline_circle_24);
                                }
                                arrayList2.add(calendarItem);
                                rvAdapter.notifyDataSetChanged();
                                isDataExists = true;
                            }
                        }
                        if (!isDataExists) {
                            // arrayList2 = new ArrayList<>();
                            rvAdapter=new calendarAdapter(arrayList2,GroupCalendar.this);
                            recyclerView.setAdapter(rvAdapter);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "Failed to read value.", error.toException());

                    }
                });
            }
        });

        savecal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected_date==0){
                    Toast.makeText(GroupCalendar.this,"請選擇日期",Toast.LENGTH_SHORT).show();
                }else {
                    database();
                }
            }
        });

        //recyclerView左滑刪除
        ItemTouchHelper.SimpleCallback simpleCallback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
              // 在这里执行左滑删除的操作
                int position = viewHolder.getAdapterPosition();
                CalendarItem item=arrayList2.get(position);
                String event=(String)item.getEvent();
                String date=(String) item.getDate_id();
                arrayList2.remove(position);
                rvAdapter.notifyDataSetChanged();
                myRef.child("Calendar").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            CalendarDB calendarDB = ds.getValue(CalendarDB.class);
                            if (calendarDB.getEvent().equals(event) && calendarDB.getId().equals(date)) {
                                String path = ds.getKey();//第一層
                                Toast.makeText(GroupCalendar.this, path, Toast.LENGTH_SHORT).show();
                                myRef.child("Calendar").child(path).removeValue();

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });


            }

            // onChildDraw() 方法用于绘制滑动效果
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // 使用 RecyclerViewSwipeDecorator 定制左滑的背景颜色、图标和标签
                new  RecyclerViewSwipeDecorator.Builder(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(GroupCalendar.this,R.color.lightred))
                        .addActionIcon(R.drawable.baseline_delete_24)
                        .addSwipeLeftLabel("刪除")
                        .setSwipeLeftLabelColor(ContextCompat.getColor(GroupCalendar.this, R.color.white))
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


    }

    private void getAlluser() {
        getalluser=new ArrayList<>();
        getalltoken=new ArrayList<>();
        getalluser.add(0,"All");
        getalltoken.add(0,"APA91bEg-xO9Rlyb72AGxpt3wNoyKAYsA-9-fdbWKSNxyaG8qxz2syGfiwWVXoHLwZ2EIygaygZXGF19Ge1lL9h40NDhimvwoYJXJc37P2X3gWZDn7O0cA4");

        myRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Users user1 = snapshot1.getValue(Users.class);
                    getalluser.add(user1.getUsername());
                    getalltoken.add(user1.getToken());
                    // System.out.println("Added user: " + user1.getUsername());
                }
                System.out.println("getalluser->" + getalluser.toString());
                System.out.println("getalluser->" + getalltoken.toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


    private void getDialog(String addevent_text,String time4,int month,String[] date1) {
        String[] users = getalluser.toArray(new String[getalluser.size()]);
        String[] tokens = getalltoken.toArray(new String[getalltoken.size()]);
        boolean[] checkitems=new boolean[getalluser.size()];
        for (int i=0;i<checkitems.length;i++){
            checkitems[i]=false;
        }

        AlertDialog.Builder alertDialog=new AlertDialog.Builder(GroupCalendar.this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("選 擇 通 知 對 象");
        alertDialog.setMultiChoiceItems(users, checkitems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkitems[which]=isChecked;
            }
        });
        alertDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //StringBuilder sb = new StringBuilder();
                //String delimiter = ",";
                ArrayList<String> object=new ArrayList<>();
                if(checkitems[0]){
                    object.add(tokens[0]);
                    //   addListViewImage(selected_date,addevent_text,time4);
                    ischooseall=true;
                }else {
                    object.add(token);
                    for (int j = 1; j < checkitems.length; j++) {
                        if (checkitems[j]) {
                            object.add(tokens[j]);
                        }
                    }
                }
                System.out.println(object.toString());
                setAlarm(addevent_text,time4,month,date1,object);
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.create().show();

    }

    private void setAlarm(String addevent_text,String time4,int month,String[] date1,ArrayList<String> object){
        if (!(addevent_text.isEmpty() && time4.isEmpty())) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            //给每个闹钟设置不同ID防止覆盖
            int alarmId = SharedPreUtils.getInt(GroupCalendar.this, "alarm_id", 0);
            SharedPreUtils.setInt(GroupCalendar.this, "alarm_id", ++alarmId);


            //notificationId & message
            Intent intent = new Intent(GroupCalendar.this, alarmReceiver.class);
            intent.putExtra("event", addevent_text);
            intent.putExtra("time", time4);
            intent.putExtra("object", object);


            //PendingIntent pendingIntent = PendingIntent.getBroadcast(GroupCalendar.this, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(GroupCalendar.this, alarmId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            String[] time5 = time4.split(":");//ex:14:28
            int hour = Integer.parseInt(time5[0]);
            int minute = Integer.parseInt(time5[1]);

            //create time
            Calendar starttime = Calendar.getInstance();
            starttime.set(Calendar.MONTH, month);
            starttime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date1[2]));
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
    }


    //如果選all，listview會出現圓圈符號
    private void addListViewImage(int selected_date,String addevent_text,String time4) {
        for (int i=0;i<adapter.getCount();i++){
            HashMap item= (HashMap<String, String>) arrayList.get(i);
            if(item.get(from[0]).equals(String.valueOf(selected_date)) && item.get(from[1]).equals(addevent_text) && item.get(from[2]).equals(time4)){
                item.put(from[4], R.drawable.baseline_co2_24);//圖片暫放
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }



    public String getSelected_date(Calendar selectDate) {
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fomatteddate= dateFormat.format(selectDate.getTime());
        return fomatteddate;
    }

    public int changeDateToNum(Calendar selectDate){
        int year=selectDate.get(Calendar.YEAR);
        int month=selectDate.get(Calendar.MONTH)+1;
        int day=selectDate.get(Calendar.DAY_OF_MONTH);
        int dateNum=year*10000+month*100+day;
        return dateNum;
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
        // final AlertDialog.Builder builder=new AlertDialog.Builder(GroupCalendar.this);
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(GroupCalendar.this);
        //選用特定的layout
        View view=getLayoutInflater().inflate(R.layout.cal_botton_sheetdialog,null);
        //取得控制元件
        cancel=view.findViewById(R.id.cancel);
        ok=view.findViewById(R.id.ok);
        addevent=view.findViewById(R.id.addevent);
        addtime=view.findViewById(R.id.addtime);
        TextView title=view.findViewById(R.id.title);
        noti=view.findViewById(R.id.noti);

        bottomSheetDialog.setContentView(view);//將介面載入至BottomSheet內
        ViewGroup parent = (ViewGroup) view.getParent();//取得BottomSheet介面設定
        parent.setBackgroundResource(android.R.color.transparent);//將背景設為透明，否則預設白底

        title.setText(String.valueOf(selected_date));//標題放選到的日期

        bottomSheetDialog.show();//顯示BottomSheet


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

        //設置按鈕監聽事件
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id_date = String.valueOf(selected_date);//日期
                String event = addevent.getText().toString();//事件

                String time1 = time_text;
                if (event.isEmpty() || time1.isEmpty() || id_date.isEmpty()) {
                    Toast.makeText(GroupCalendar.this, "請填寫事件和時間", Toast.LENGTH_SHORT).show();
                    //finish();
                } else {
                    String[] time2 = time1.split("\t");
                    String time3 = time2[2];

                    String email = user.getEmail();

                    //後面改
                    CalendarDB calevent = new CalendarDB(id_date, event, time3, email, token,ischooseall);

                    ischooseall=false;
                    myRef.child("Calendar").push().setValue(calevent);
                    Toast.makeText(GroupCalendar.this, "儲存成功", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                }

            }
        });

        noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addevent_text = addevent.getText().toString();
                String time4 = addtime.getText().toString();
                String date = caldate.getText().toString();//顯示日期 ex:2023-10-31
                String[] date1 = date.split("-");
                int month = Integer.parseInt(date1[1]) - 1;

                getDialog(addevent_text, time4, month, date1);
            }
        });

    }

    public void isEventDayaddIcon(){
        Calendar eventDate=Calendar.getInstance();
        myRef.child("Calendar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    CalendarDB calendarDB = dataSnapshot.getValue(CalendarDB.class);
                    int year = Integer.parseInt(calendarDB.getId().toString().substring(0, 4));//ex:2023
                    int month = Integer.parseInt(calendarDB.getId().toString().substring(4, 6)) - 1;//ex:10
                    int day = Integer.parseInt(calendarDB.getId().toString().substring(6, 8));//ex:31
                    eventDate.set(year, month, day);
                    events.add(new EventDay(eventDate, R.drawable.baseline_circle_24));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

/*
    private void setAdapter() {
        adapter = new SimpleAdapter(this, arrayList, R.layout.event_item, from, to);
        listView.setAdapter(adapter);
    }
/*
    private boolean isTokenEmpty(String token) {
        myRef.child("Calendar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    CalendarDB calendarDB = ds.getValue(CalendarDB.class);
                    if (calendarDB.getToken() == token) {
                        return ;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return false;
    }

 */

}