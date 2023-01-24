package com.ethan.FamiCare;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

public class DiaryFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public DiaryFragment() {
        // Required empty public constructor
    }

    public static DiaryFragment newInstance(String param1, String param2) {
        DiaryFragment fragment = new DiaryFragment();
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

    private CalendarView calender;
    private TextView date;
    private Button title;

    //連接資料庫用
    private final static String DATE = "date";
    private final static String TITLE = "title";
    private final static String CONTENT = "content";
    private DiaryDB diaryDB;
    private Cursor cursor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diary, container, false);

        calender = view.findViewById(R.id.calender);
        date = view.findViewById(R.id.date);
        title = view.findViewById(R.id.title);

//        diaryDB = new DiaryDB(this.getContext());
//        diaryDB.open();//開啟資料庫

        //監聽選擇到的日期，改變date，抓取資料庫對應日期的資料，顯示資料庫標題到title
        calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                //變更日期
                date.setText((month + 1) + "/" + dayOfMonth);
            }
        });

        //點擊日記標題，跳轉到DiaryContent
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.Diary_layout, new DiaryContentFragment()).commit();
            }
        });

        return view;
    }
}