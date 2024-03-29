package com.ethan.FamiCare.Diary;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ethan.FamiCare.Group.GroupCalendar;
import com.ethan.FamiCare.Post.DiaryPostActivity;
import com.ethan.FamiCare.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;


public class DiaryFragment extends Fragment {

    private int selected_date;
    //Layout 元素
    private CalendarView calender;
    private TextView date;
    private TextView cal_fold;
    private DiaryAdapter diaryAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton floating;
    private Button cal;
    private Button look;

    //資料庫
    private DiaryDoa diaryDoa;
    private List<Diary> diaries;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment你好
        View view = inflater.inflate(R.layout.fragment_diary, container, false);
        //更新標題
        getActivity().setTitle("生活點滴");

        diaryDoa = DiaryDB.getInstance(this.getContext()).diaryDoa();

        calender = view.findViewById(R.id.calender);
        date = view.findViewById(R.id.date);
        floating = view.findViewById(R.id.floating);
        cal = view.findViewById(R.id.cal);
        cal_fold = view.findViewById(R.id.cal_fold);
        look = view.findViewById(R.id.look);

        //一跳轉頁面就可以顯示是否輸入過資料
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        selected_date = getSelected_date(year, month, day);
        date.setText((month + 1) + "/" + day);

        // Initialize the RecyclerView and Adapter
        recyclerView = view.findViewById(R.id.rv_diary);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        if (diaryDoa.getDiariesById(selected_date) != null) {
            diaries = diaryDoa.getDiariesById(selected_date);
        }
        diaryAdapter = new DiaryAdapter(getContext(), diaries, recyclerView);
        recyclerView.setAdapter(diaryAdapter);


        //點擊recycler的其中一個日記，會根據日記的日期和標題跳轉到相對應的編輯內容

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (rv.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                    View childView = rv.findChildViewUnder(e.getX(), e.getY());
                    if (childView != null && e.getAction() == MotionEvent.ACTION_UP) {
                        int position = rv.getChildAdapterPosition(childView);
                        TextView textView = childView.findViewById(R.id.diary_title);
                        String t = textView.getText().toString();

                        Intent intent = new Intent(getActivity(), DiaryContentActivity.class);
                        intent.putExtra("edited", true);
                        intent.putExtra("id", selected_date);
                        intent.putExtra("title", t);
                        startActivity(intent);

                        return true;
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

//

        //監聽選擇到的日期，改變date，通知diaryAdapter日期改變，並改變recycler的內容
        calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                //變更日期
                date.setText((month + 1) + "/" + dayOfMonth);
                selected_date = getSelected_date(year, month, dayOfMonth);

                //Recycler 的內容會跟著日期改變
                if (diaryDoa.getDiariesById(selected_date) != null) {
                    diaries.clear();
                    diaries = diaryDoa.getDiariesById(selected_date);
                }
                diaryAdapter = new DiaryAdapter(getContext(), diaries, recyclerView);
                recyclerView.setAdapter(diaryAdapter);

            }
        });


        //點擊日記標題，紀錄選擇的日期(20230101)，跳轉到DiaryContent，一定是創建新的日記

        floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!date.getText().equals("日期")) {
                    Intent intent = new Intent(getActivity(), DiaryContentActivity.class);
                    intent.putExtra("edited", false);
                    intent.putExtra("id", selected_date);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "請選擇日期", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //跳轉到DiaryPostsFragment

        look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DiaryPostActivity.class);
                startActivity(intent);
            }
        });


        //摺疊行事曆
        cal_fold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (calender.getVisibility() == View.VISIBLE) {
                    calender.setVisibility(View.GONE);
                } else {
                    calender.setVisibility(View.VISIBLE);
                }
            }
        });


        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), GroupCalendar.class);
                startActivity(intent);
            }
        });

        //增加選擇日期底圖裝飾
        Drawable drawableR = getResources().getDrawable(R.drawable.schedulepoint_img);
        drawableR.setBounds(90, 8, 190, 150);
        Drawable drawableL = getResources().getDrawable(R.drawable.schedulepoint_img);
        drawableL.setBounds(-90, 8, 10, 150);
        cal_fold.setCompoundDrawables(drawableR, null, drawableL, null);

        Drawable drawable = getResources().getDrawable(R.drawable.schedule_img);
        drawable.setBounds(-40, 0, 80, 100);
        cal.setCompoundDrawables(null, null, drawable, null);

        Drawable drawable2 = getResources().getDrawable(R.drawable.seeother);
        drawable2.setBounds(-30, 0, 80, 100);
        look.setCompoundDrawables(null, null, drawable2, null);

        return view;
    }

    public int getSelected_date(int year, int month, int dayOfMonth) {
        String s = String.format("%4d%02d%02d", year, month + 1, dayOfMonth);
        return Integer.parseInt(s);
    }

}